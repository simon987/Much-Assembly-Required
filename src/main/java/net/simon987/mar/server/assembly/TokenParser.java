package net.simon987.mar.server.assembly;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.simon987.mar.server.assembly.exception.AssemblyException;
import org.apache.velocity.runtime.directive.Parse;

public class TokenParser {

    public enum TokenType {
        Constant, BinaryOperator, UnaryOperator, GroupOperator, Space, EOF
    }

    public enum BinaryOperatorType {
        Add("+", 40) {
            @Override
            public int apply(int a, int b) {
                return (a + b) & 0xFFFF;
            }
        },Sub("-", 40) {
            @Override
            public int apply(int a, int b) {
                return (a - b) & 0xFFFF;
            }
        }, Mul("*", 30) {
            @Override
            public int apply(int a, int b) {
                return (a * b) & 0xFFFF;
            }
        }, Div("/", 30) {
            @Override
            public int apply(int a, int b) {
                return (a / b) & 0xFFFF;
            }
        }, Rem("%", 30) {
            @Override
            public int apply(int a, int b) {
                return (a / b) & 0xFFFF;
            }
        }, Shl("<<", 50) {
            @Override
            public int apply(int a, int b) {
                if (b >= 16) return 0;
                return (a << b) & 0xFFFF;
            }
        }, Shr(">>", 50) {
            @Override
            public int apply(int a, int b) {
                if (b >= 16) return 0;
                return (a >>> b) & 0xFFFF;
            }
        }, Rol("<", 50) {
            @Override
            public int apply(int a, int b) {
               b &= 0x11;
               return ((a >>> (16-b)) | (a << b)) & 0xFFFF;
            }
        }, Ror(">", 50) {
            @Override
            public int apply(int a, int b) {
                b &= 0x11;
                return ((a >>> b) | (a << (16-b))) & 0xFFFF;
            }
        }, Or("|", 80) {
            @Override
            public int apply(int a, int b) {
                return (a | b) & 0xFFFF;
            }
        }, And("&", 60) {
            @Override
            public int apply(int a, int b) {
                return (a & b) & 0xFFFF;
            }
        }, Xor("^", 70) {
            @Override
            public int apply(int a, int b) {
                return (a ^ b) & 0xFFFF;
            }
        };

        final public static Map<String, BinaryOperatorType> stringMap = new HashMap<>();

        static {
            for (BinaryOperatorType op : values()) stringMap.put(op.symbol, op);
        }

        public final String symbol;
        public final int precedence;

        BinaryOperatorType(final String symbol, final int precedence) {
            this.symbol = symbol;
            this.precedence = precedence;
        }

        public abstract int apply(int a, int b);

    }

    public enum UnaryOperatorType {
        Neg("-") {
            @Override
            public int apply(int a) {
                return -a;
            }
        }, Not("~") {
            @Override
            public int apply(int a) {
                return ~a;
            }
        };

        public static final Map<String, UnaryOperatorType> stringMap = new HashMap<>();

        static {
            for (UnaryOperatorType op : values()) stringMap.put(op.symbol, op);
        }

        public final String symbol;

        UnaryOperatorType(final String symbol) {
            this.symbol = symbol;
        }
        public abstract int apply(int a);
    }

    public enum GroupOperatorType {
        GroupStart("(", false, 0),
        GroupEnd(")", true, 0);

        public static final Map<String, GroupOperatorType> stringMap = new HashMap<>();

        static {
            for (GroupOperatorType op : values()) stringMap.put(op.symbol, op);
        }

        public final String symbol;
        public final boolean end;
        public final int groupType;

        GroupOperatorType(final String symbol, boolean end, int groupType) {
            this.symbol = symbol;
            this.end = end;
            this.groupType = groupType;
        }
    }

    /**
     * Interface allowing parse states to be manipulated, evaluated, and stacked.
     */
    private static class ParseOperator {
        public int getPrecedence() {
            return 0;
        }
        public int apply(int other) {
            return other;
        }

        public final int closeExpect;

        public ParseOperator(int closeExpect) {
            this.closeExpect = closeExpect;
        }
    }

    private static class ParseOperatorUnary extends ParseOperator {
        TokenParser.UnaryOperatorType op;
        @Override
        public int getPrecedence() {
            return 0;
        }

        @Override
        public int apply(int other) {
            return op.apply(other);
        }

        public ParseOperatorUnary(int closeExpect, TokenParser.UnaryOperatorType op) {
            super(closeExpect);
            this.op = op;
        }
    }

    private static class ParseOperatorBinary extends ParseOperator {
        private final TokenParser.BinaryOperatorType op;
        private final int value;
        @Override
        public int getPrecedence() {
            return op.precedence;
        }

        @Override
        public int apply(int other) {
            return op.apply(value, other);
        }

        public ParseOperatorBinary(int closeExpect, TokenParser.BinaryOperatorType op, int value) {
            super(closeExpect);
            this.op = op;
            this.value = value;
        }
    }



    public enum ParseContext {
        TackOn, Value
    }

    private static final Pattern BINARY_OPERATOR_PATTERN = Pattern.compile("(<<|>>|[\\-+*/<>&|^])");
    private static final Pattern UNARY_OPERATOR_PATTERN = Pattern.compile("([\\-~])");
    private static final Pattern GROUP_OPERATOR_PATTERN = Pattern.compile("([()])");
    private static final Pattern IDENTIFIER_PATTERN = Pattern.compile("([\\w]+)");
    private static final Pattern NUMBER_PATTERN_16 = Pattern.compile("(?:0[xX]|#)([\\da-fA-F]+)(?![\\w\\d])");
    private static final Pattern NUMBER_PATTERN_8 = Pattern.compile("0[oO]?([0-7]+)(?![\\w\\d])");
    private static final Pattern NUMBER_PATTERN_2 = Pattern.compile("0[bB]([01]+)(?![\\w\\d])");
    private static final Pattern NUMBER_PATTERN_10 = Pattern.compile("([1-9][\\d]*|0)(?![\\w\\d])");
    private static final Pattern NUMBER_PATTERN_START = Pattern.compile("([+]?)[\\d#]");
    private static final Pattern RESET_PATTERN = Pattern.compile("[^\\w\\d]");
    private static final Pattern SPACE_PATTERN = Pattern.compile("[^\\S\\n]+");

    /**
     * @param sequence The characters to parse
     * @param start The index of the first character to be parsed
     * @param end The index of the character after the last character to be parsed
     */

    public TokenParser(CharSequence sequence, int line, Map<String, Character> labels, int start, int end) {
        matcher = SPACE_PATTERN.matcher(sequence);
        this.line = line;
        this.start = start;
        this.end = end;
        this.labels = labels;
    }

    public TokenParser(CharSequence sequence, int line, Map<String, Character> labels) {
        this(sequence, line, labels,0, sequence.length());
    }

    public int line, start, end;

    private final Map<String, Character> labels;

    private final Matcher matcher;

    public int lastInt;

    public BinaryOperatorType lastBinary;

    public UnaryOperatorType lastUnary;

    public GroupOperatorType lastGroup;

    /**
     * Reads the next token.
     *
     * @param eatSpace whether the parser should ignore leading spaces
     * @param context the current class of tokens expected
     * @return The token that was found
     * @throws AssemblyException if an unrecognized token is found,
     * or if the found token is not supported in the current context.
     */
    public TokenType getNextToken(boolean eatSpace, ParseContext context) throws AssemblyException {
        if (start >= end) return TokenType.EOF;
        matcher.region(start, end);
        if (matcher.usePattern(SPACE_PATTERN).lookingAt()) {
            start = matcher.end();
            if (!eatSpace) return TokenType.Space;
            if (start >= end) return TokenType.EOF;
            matcher.region(start, end);
        }
        matcher.usePattern(GROUP_OPERATOR_PATTERN);
        if (matcher.lookingAt()) {
            start = matcher.end(1);
            String symbol = matcher.group(1);
            lastGroup = GroupOperatorType.stringMap.get(symbol);

            // Should never happen unless the regex does not agree with GroupOperatorType.
            if (lastGroup == null) throw new AssemblyException("Group operator not supported", line);

            return TokenType.GroupOperator;
        }
        if (context == ParseContext.TackOn) {
            if (matcher.usePattern(BINARY_OPERATOR_PATTERN).lookingAt()) {
                start = matcher.end();
                String symbol = matcher.group(1);
                lastBinary = BinaryOperatorType.stringMap.get(symbol);

                // Should never happen unless the regex does not agree with BinaryOperatorType.
                if (lastBinary == null) throw new AssemblyException("Binary operator not supported", line);

                return TokenType.BinaryOperator;
            }
        }
        else {
            if (matcher.usePattern(NUMBER_PATTERN_START).lookingAt()) {
                start = matcher.end(1);
                matcher.region(start, end);
                try {
                    if (matcher.usePattern(NUMBER_PATTERN_10).lookingAt()) {
                        lastInt = Integer.parseInt(matcher.group(1), 10);
                    } else if (matcher.usePattern(NUMBER_PATTERN_16).lookingAt()) {
                        lastInt = Integer.parseInt(matcher.group(1), 16);
                    } else if (matcher.usePattern(NUMBER_PATTERN_2).lookingAt()) {
                        lastInt = Integer.parseInt(matcher.group(1), 2);
                    } else if (matcher.usePattern(NUMBER_PATTERN_8).lookingAt()) {
                        lastInt = Integer.parseInt(matcher.group(1), 8);
                    } else {
                        if (matcher.usePattern(RESET_PATTERN).find()) {
                            start = matcher.start();
                        } else {
                            start = end;
                        }
                        throw new AssemblyException("Invalid number found.", line);
                    }
                } catch (NumberFormatException ex) {
                    start = matcher.end(1);
                    throw new AssemblyException("Number parsing failed", line);
                }
                start = matcher.end(1);
                lastInt &= 0xFFFF;
                return TokenType.Constant;
            }
            if (matcher.usePattern(IDENTIFIER_PATTERN).lookingAt()) {
                start = matcher.end(1);
                String identifier = matcher.group(1);
                Character val = labels.get(identifier);

                if (val == null) {
                    throw new AssemblyException("Unknown label found", line);
                }

                lastInt = val;
                return TokenType.Constant;
            }
            matcher.usePattern(UNARY_OPERATOR_PATTERN);
            if (matcher.lookingAt()) {
                start = matcher.end(1);
                String symbol = matcher.group(1);
                lastUnary = UnaryOperatorType.stringMap.get(symbol);

                // Should never happen unless the regex does not agree with UnaryOperatorType.
                if (lastUnary == null) {
                    throw new AssemblyException("Unary operator not supported", line);
                }
                return TokenType.UnaryOperator;
            }
        }
        matcher.usePattern(RESET_PATTERN);
        if (matcher.lookingAt()) {
            start = matcher.end();
        } else if (matcher.find()) {
            start = matcher.end();
        } else {
            start = end;
        }
        throw new AssemblyException("Invalid token found", line);
    }

    public int parseConstExpression()
            throws AssemblyException {
        Stack<ParseOperator> parseOps = new Stack<>();
        int closeExpect = -1; // No closing parenthesis expected
        TokenParser.ParseContext context = TokenParser.ParseContext.Value;
        int lastValue = 0;
        while (true) {
            TokenParser.TokenType ty = getNextToken(true, context);
            if (context == TokenParser.ParseContext.Value) {
                // Parse value
                if (ty == TokenParser.TokenType.UnaryOperator) {
                    parseOps.push(new ParseOperatorUnary(closeExpect, lastUnary));
                    closeExpect = -1;
                } else if (ty == TokenParser.TokenType.GroupOperator) {
                    if (lastGroup.end) {
                        throw new AssemblyException("Unexpected group close", line);
                    }
                    if (closeExpect != -1) {
                        parseOps.push(new ParseOperator(closeExpect));
                    }
                    closeExpect = lastGroup.groupType;
                } else if (ty == TokenParser.TokenType.Constant) {
                    lastValue = lastInt;
                    context = TokenParser.ParseContext.TackOn;
                } else {
                    throw new AssemblyException("Value not found", line);
                }
            } else {
                // Parse modifier
                if (ty == TokenParser.TokenType.EOF || ty == TokenParser.TokenType.GroupOperator) {
                    if (ty == TokenParser.TokenType.GroupOperator && !lastGroup.end) {
                        throw new AssemblyException("Unexpected group open", line);
                    }
                    if (closeExpect != -1) {
                        if (ty == TokenParser.TokenType.EOF) {
                            throw new AssemblyException("Unclosed group", line);
                        } else if (closeExpect != lastGroup.groupType) {
                            throw new AssemblyException("Unmatched group ends", line);
                        } else {
                            closeExpect = -1;
                            continue;
                        }
                    }

                    boolean completed = false;

                    //Evaluation chain
                    while (!parseOps.isEmpty()) {
                        ParseOperator op = parseOps.pop();
                        if (op.closeExpect != -1) {
                            if (ty == TokenParser.TokenType.EOF) {
                                throw new AssemblyException("Unclosed group", line);
                            } else if (op.closeExpect != lastGroup.groupType) {
                                throw new AssemblyException("Unmatched group ends", line);
                            }
                            lastValue = op.apply(lastValue);
                            completed = true;
                            break;
                        }
                        lastValue = op.apply(lastValue);
                    }
                    if (!completed) {
                        if (ty == TokenParser.TokenType.EOF) {
                            return lastValue;
                        } else if (lastGroup.groupType != -1) {
                            throw new AssemblyException("Unexpected group close", line);
                        }
                    }
                } else if (ty == TokenParser.TokenType.BinaryOperator) {
                    TokenParser.BinaryOperatorType bop = lastBinary;
                    while (closeExpect == -1 && !parseOps.empty()) {
                        ParseOperator op = parseOps.peek();
                        if (bop.precedence <= op.getPrecedence()) {
                            break;
                        }
                        lastValue = op.apply(lastValue);
                        closeExpect = op.closeExpect;
                        parseOps.pop();
                    }
                    parseOps.push(new ParseOperatorBinary(closeExpect, bop, lastValue));
                    closeExpect = -1;
                    context = TokenParser.ParseContext.Value;
                }
                else throw new AssemblyException("Modifier or end not found", line);
            }
        }
    }


}
