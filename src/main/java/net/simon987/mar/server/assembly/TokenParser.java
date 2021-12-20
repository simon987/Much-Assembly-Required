package net.simon987.mar.server.assembly;

import java.util.HashMap;
import java.util.Map;
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

    public enum ParseContext {
        TackOn, Value
    }

    private static final Pattern BINARY_OPERATOR_PATTERN = Pattern.compile("(<<|>>|[\\-+*/<>&|^])");
    private static final Pattern UNARY_OPERATOR_PATTERN = Pattern.compile("([\\-~])");
    private static final Pattern GROUP_OPERATOR_PATTERN = Pattern.compile("([()])");
    private static final Pattern IDENTIFIER_PATTERN = Pattern.compile("([\\w]+)");
    private static final Pattern NUMBER_PATTERN_16 = Pattern.compile("0[xX]([\\da-fA-F]+)(?![\\w\\d])");
    private static final Pattern NUMBER_PATTERN_8 = Pattern.compile("0[oO]([0-7]+)(?![\\w\\d])");
    private static final Pattern NUMBER_PATTERN_2 = Pattern.compile("0[bB]([01]+)(?![\\w\\d])");
    private static final Pattern NUMBER_PATTERN_10 = Pattern.compile("(\\d+)(?![\\w\\d])");
    private static final Pattern NUMBER_PATTERN_START = Pattern.compile("(\\d)");
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

    private int line, start, end;

    private Map<String, Character> labels;

    private Matcher matcher;

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

                try {
                    if (matcher.usePattern(NUMBER_PATTERN_10).lookingAt())
                        lastInt = Integer.parseInt(matcher.group(1), 10);
                    else if (matcher.usePattern(NUMBER_PATTERN_16).lookingAt())
                        lastInt = Integer.parseInt(matcher.group(1), 16);
                    else if (matcher.usePattern(NUMBER_PATTERN_2).lookingAt())
                        lastInt = Integer.parseInt(matcher.group(1), 2);
                    else if (matcher.usePattern(NUMBER_PATTERN_8).lookingAt())
                        lastInt = Integer.parseInt(matcher.group(1), 8);
                    else {
                        if (matcher.usePattern(RESET_PATTERN).find()) start = matcher.start();
                        else start = end;
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

                if (val == null) throw new AssemblyException("Unknown label found", line);

                lastInt = val;
                return TokenType.Constant;
            }
            matcher.usePattern(UNARY_OPERATOR_PATTERN);
            if (matcher.lookingAt()) {
                start = matcher.end(1);
                String symbol = matcher.group(1);
                lastUnary = UnaryOperatorType.stringMap.get(symbol);

                // Should never happen unless the regex does not agree with UnaryOperatorType.
                if (lastUnary == null) throw new AssemblyException("Unary operator not supported", line);
                return TokenType.UnaryOperator;
            }
        }
        if (matcher.usePattern(RESET_PATTERN).find()) start = matcher.end();
        else start = end;
        throw new AssemblyException("Invalid token found", line);
    }


}
