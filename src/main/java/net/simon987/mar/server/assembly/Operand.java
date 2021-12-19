package net.simon987.mar.server.assembly;

import net.simon987.mar.server.assembly.exception.AssemblyException;
import net.simon987.mar.server.assembly.exception.InvalidOperandException;

import java.util.HashMap;
import java.util.Stack;

/**
 * Represents an operand of an instruction. An operand can refer to a
 * location in memory, a register or an immediate value
 */
public class Operand {

    public static final int IMMEDIATE_VALUE = 0b11111; //1 1111

    public static final int IMMEDIATE_VALUE_MEM = 0b11110; //1 1110

    /**
     * The actual text of the operand (e.g. "[AX]")
     */
    private String text;

    /**
     * Type of the operand
     */
    private OperandType type;

    /**
     * Value of the the operand, this is the part that will
     * written into the instruction.
     */
    private int value = 0;

    /**
     * Data of the operand. This will be appended after the instruction.
     * For example, "[AX+3]" value={index of AX] + {number of registers}, Data=3
     */
    private int data = 0;

    public Operand(OperandType type, int value) {
        this.type = type;
        this.value = value;
    }

    public Operand(OperandType type, int value, int data) {
        this(type, value);
        this.data = data;
    }

    /**
     * Create an Operand from text. It assumes that the numerical values that can't be
     * parsed are labels that are not defined yet.
     *
     * @param text Text of the operand
     * @param line Line of the instruction. Will be used to report exceptions
     */
    public Operand(String text, RegisterSet registerSet, int line) throws InvalidOperandException {
        this(text, null, registerSet, line);
    }

    /**
     * Creates an Operand from text. If labels is not null, it will be used to parse the
     * operand.
     *
     * @param text   Text of the operand
     * @param labels Map of labels
     * @param line   Line of the instruction. Will be used to report exceptions
     */
    public Operand(String text, HashMap<String, Character> labels, RegisterSet registerSet, int line)
            throws InvalidOperandException {

        this.text = text.replace(",", "");
        this.text = this.text.trim();


        if (!parseImmediate(this.text) && !parseReg(this.text, registerSet) && !parseLabel(this.text, labels)) {
            if (this.text.startsWith("[") && this.text.endsWith("]")) {

                //Remove []s
                this.text = this.text.substring(1, this.text.length() - 1);

                if (parseImmediate(this.text) || parseLabel(this.text, labels)) {
                    //Operand refers to memory
                    type = OperandType.MEMORY_IMM16;
                    value = Operand.IMMEDIATE_VALUE_MEM;

                } else if (!parseRegExpr(registerSet, labels)) {

                    if (labels == null) {
                        type = OperandType.MEMORY_IMM16;
                        data = 0;
                    } else {
                        throw new InvalidOperandException("Invalid operand " + this.text, line);
                    }

                }

            } else {
                if (labels == null) {
                    type = OperandType.IMMEDIATE16;
                    data = 0;
                } else {
                    throw new InvalidOperandException("Invalid operand " + this.text, line);
                }
            }
        }
    }


    /**
     * Attempt to parse an integer
     *
     * @param text Text to parse, can be a label or immediate value (hex or dec)
     * @return true if successful, false otherwise
     */
    private boolean parseImmediate(String text) {

        text = text.trim();

        try {
            //Try IMM
            type = OperandType.IMMEDIATE16;
            data = Integer.decode(text);
            value = IMMEDIATE_VALUE;
            return true;
        } catch (NumberFormatException e) {

            //Try Binary number (format 0bXXXX)
            if (text.startsWith("0b")) {
                try {
                    data = Integer.parseInt(text.substring(2), 2);
                    value = IMMEDIATE_VALUE;
                    return true;
                } catch (NumberFormatException e2) {
                    return false;
                }
            } else if (text.startsWith("0o")) {
                try {
                    data = Integer.parseInt(text.substring(2), 8);
                    value = IMMEDIATE_VALUE;
                    return true;
                } catch (NumberFormatException e2) {
                    return false;
                }
            }

            return false;
        }
    }

    /**
     * Attempt to parse a user-defined label
     *
     * @param text   Text to parse
     * @param labels Map of labels
     * @return true if parsing is successful, false otherwise
     */
    private boolean parseLabel(String text, HashMap<String, Character> labels) {

        text = text.trim();

        if (labels == null) {
            return false;
        } else if (labels.containsKey(text)) {
            type = OperandType.IMMEDIATE16;
            data = labels.get(text);
            value = IMMEDIATE_VALUE;
            return true;
        } else {
            return false;
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

    private int parseConstExpression(String text, int line, HashMap<String, Character> labels)
            throws AssemblyException {
        TokenParser parser = new TokenParser(text, line, labels);
        Stack<ParseOperator> parseOps = new Stack<>();
        int closeExpect = -1; // No closing parenthesis expected
        TokenParser.ParseContext context = TokenParser.ParseContext.Value;
        int lastValue = 0;
        while (true) {
            TokenParser.TokenType ty = parser.GetNextToken(true, context);
            if (context == TokenParser.ParseContext.Value) {
                // Parse value
                if (ty == TokenParser.TokenType.UnaryOperator) {
                    parseOps.push(new ParseOperatorUnary(closeExpect, parser.lastUnary));
                    closeExpect = -1;
                }
                else if (ty == TokenParser.TokenType.GroupOperator) {
                    if (parser.lastGroup.end) throw new AssemblyException("Unexpected group close", line);
                    if (closeExpect != -1) parseOps.push(new ParseOperator(closeExpect));
                    closeExpect = parser.lastGroup.groupType;
                } else if (ty == TokenParser.TokenType.Constant) {
                    lastValue = parser.lastInt;
                    context = TokenParser.ParseContext.TackOn;
                } else throw new AssemblyException("Value not found", line);
            } else {
                // Parse modifier
                if (ty == TokenParser.TokenType.EOF || ty == TokenParser.TokenType.GroupOperator) {
                    if (ty == TokenParser.TokenType.GroupOperator && !parser.lastGroup.end)
                        throw new AssemblyException("Unexpected group open", line);
                    if (closeExpect != -1) throw new AssemblyException("Found empty group", line);

                    //Evaluation chain
                    while (!parseOps.isEmpty()) {
                        ParseOperator op = parseOps.peek();
                        if (op.closeExpect != -1) {
                            if (ty == TokenParser.TokenType.EOF) throw new AssemblyException("Unclosed group", line);
                            else if (op.closeExpect != parser.lastGroup.groupType)
                                throw new AssemblyException("Unmatched group ends", line);
                            lastValue = op.apply(lastValue);
                            parseOps.pop();
                            break;
                        }
                        lastValue = op.apply(lastValue);
                        parseOps.pop();
                    }
                    if (parseOps.isEmpty() && ty == TokenParser.TokenType.EOF) return lastValue;
                }
                else if (ty == TokenParser.TokenType.BinaryOperator) {
                    TokenParser.BinaryOperatorType bop = parser.lastBinary;
                    while (closeExpect == -1 && !parseOps.empty()) {
                        ParseOperator op = parseOps.peek();
                        if (bop.precedence <= op.getPrecedence()) break;
                        lastValue = op.apply(lastValue);
                        closeExpect = op.closeExpect;
                        parseOps.pop();
                    }
                    parseOps.push(new ParseOperatorBinary(closeExpect, bop, lastValue));
                    closeExpect = -1;
                }
                else throw new AssemblyException("Modifier or end not found", line);
            }
        }
    }

    /**
     * Attempt to parse a register
     *
     * @param text Text to parse
     * @return true if successful
     */
    private boolean parseReg(String text, RegisterSet registerSet) {

        int index = registerSet.getIndex(text.trim());

        if (index != -1) {
            value = index;
            type = OperandType.REGISTER16;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Attempt to parse a register followed by an expression.
     * The expression has to follow this format: REGISTER+X or REGISTER-X. Any amount of white space between
     * the terms will be ignored.
     *
     * @return true if successful
     */
    private boolean parseRegExpr(RegisterSet registerSet, HashMap<String, Character> labels) {

        String expr;

        text = text.replaceAll("\\s+", "");

        if (text.isEmpty()) {
            return false;
        }

        if (text.length() >= 2 && parseReg(text.substring(0, 2), registerSet)) {
            expr = text.substring(2);
        } else if (parseReg(text.substring(0, 1), registerSet)) {
            //Starts with 1-char register
            expr = text.substring(1);

        } else {
            return false;
        }

        if (expr.replaceAll("\\s+", "").isEmpty()) {
            //No data specified
            type = OperandType.MEMORY_REG16;
            value += registerSet.size(); //refers to memory.
            data = 0;
            return true;
        }

        //Remove white space
        expr = expr.replaceAll("\\s+", "");

        try {
            type = OperandType.MEMORY_REG_DISP16;

            if (labels != null) {

                Character address = labels.get(expr.replaceAll("[^A-Za-z0-9_]", ""));
                if (address != null) {
                    data = (expr.startsWith("-")) ? -address : address;
                    value += registerSet.size() * 2;//refers to memory with disp

                    return true;
                }
            }

            //label is invalid
            data = Integer.decode(expr);
            value += registerSet.size() * 2; //refers to memory with disp
            return true;
        } catch (NumberFormatException e) {

            //Integer.decode failed, try binary
            if (expr.startsWith("+0b")) {
                try {
                    data = Integer.parseInt(expr.substring(3), 2);
                    value += registerSet.size() * 2; //refers to memory with disp
                    return true;
                } catch (NumberFormatException e2) {
                    return false;
                }
            } else if (expr.startsWith("-0b")) {
                try {
                    data = -Integer.parseInt(expr.substring(3), 2);
                    value += registerSet.size() * 2; //refers to memory with disp
                    return true;
                } catch (NumberFormatException e2) {
                    return false;
                }
            } else if (expr.startsWith("+0o")) {
                try {
                    data = Integer.parseInt(expr.substring(3), 8);
                    value += registerSet.size() * 2; //refers to memory with disp
                    return true;
                } catch (NumberFormatException e2) {
                    return false;
                }
            } else if (expr.startsWith("-0o")) {
                try {
                    data = -Integer.parseInt(expr.substring(3), 8);
                    value += registerSet.size() * 2; //refers to memory with disp
                    return true;
                } catch (NumberFormatException e2) {
                    return false;
                }
            }

            return false;
        }
    }

    public OperandType getType() {
        return type;
    }

    public int getValue() {
        return value;
    }

    public int getData() {
        return data;
    }

    public String toString(RegisterSet registerSet) {
        switch (type) {
            case REGISTER16:
                return registerSet.getRegister(value).getName();
            case MEMORY_IMM16:
                return String.format("[%s]", Util.toHex16(data));
            case MEMORY_REG16:
                return String.format("[%s]", registerSet.getRegister(value - registerSet.size()).getName());
            case MEMORY_REG_DISP16:
                return String.format("[%s + %s]", registerSet.getRegister(value - registerSet.size() * 2).getName(), Util.toHex16(data));
            case IMMEDIATE16:
                return Util.toHex16(data);
        }
        return null;
    }
}
