package net.simon987.server.assembly;

/**
 * Types of an operand
 */
public enum OperandType {

    REGISTER16("16-bit Register"),
    MEMORY_IMM16("16-bit Memory referred by immediate"),
    MEMORY_REG16("16-bit Memory referred by register"),
    MEMORY_REG_DISP16("16-bit Memory referred by register with displacement"),
    IMMEDIATE16("16-bit Immediate");

    /**
     * Description of the Operand type
     */
    private String description;

    public String getDescription() {
        return description;
    }

    OperandType(String desc) {
        this.description = desc;
    }
}
