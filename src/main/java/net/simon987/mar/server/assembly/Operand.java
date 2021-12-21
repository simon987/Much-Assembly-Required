package net.simon987.mar.server.assembly;

import net.simon987.mar.server.assembly.exception.AssemblyException;
import net.simon987.mar.server.assembly.exception.InvalidOperandException;

import java.util.HashMap;

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
     * Value of the operand, this is the part that will
     * be written into the instruction.
     */
    private int value = 0;

    /**
     * Data of the operand. This will be appended after the instruction.
     * For example, "[AX+3]" value={index of AX] + {number of registers}, Data=3
     */
    private char data = 0;

    public Operand(OperandType type, int value) {
        this.type = type;
        this.value = value;
    }

    public Operand(OperandType type, int value, char data) {
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

        if (parseReg(this.text, registerSet)) {
            return;
        }
        if (parseConstExpression(line, labels)) {
            type = OperandType.IMMEDIATE16;
            value = IMMEDIATE_VALUE;
            return;
        }
        if (this.text.startsWith("[") && this.text.endsWith("]")) {

            //Remove []s
            this.text = this.text.substring(1, this.text.length() - 1);
            if (parseConstExpression(line, labels)) {
                type = OperandType.MEMORY_IMM16;
                value = Operand.IMMEDIATE_VALUE_MEM;
                return;
            }
            if (!parseRegExpr(registerSet, labels, line)) {
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

    /**
     * Parses a constant expression made of operators, literals, and labels as a single immediate.
     * Sets data to this value.
     * @param line The current line of compilation
     * @param labels The labels known to the compiler
     * @return true on success, false otherwise.
     */
    private boolean parseConstExpression(int line, HashMap<String, Character> labels) {
        TokenParser parser = new TokenParser(text, line, labels);
        if (labels == null) return false;
        try {
            data = parser.parseConstExpression();
            return true;
        } catch (AssemblyException ex) {
            return false;
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
    private boolean parseRegExpr(RegisterSet registerSet, HashMap<String, Character> labels, int line) {

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

        //Remove white space
        expr = expr.replaceAll("\\s+", "");

        if (expr.isEmpty()) {
            //No data specified
            type = OperandType.MEMORY_REG16;
            value += registerSet.size(); //refers to memory.
            data = 0;
            return true;
        }

        if (!expr.startsWith("-") && !expr.startsWith("+")) {
            return false;
        }
        type = OperandType.MEMORY_REG_DISP16;
        value += registerSet.size() * 2;
        if (labels == null) {
            data = 0;
            return true;
        }

        expr = "0" + expr;
        TokenParser parser = new TokenParser(expr, line, labels);
        try {
            data = parser.parseConstExpression();
            return true;
        } catch (AssemblyException ex) {
            return false;
        }
    }

    public OperandType getType() {
        return type;
    }

    public int getValue() {
        return value;
    }

    public char getData() {
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
