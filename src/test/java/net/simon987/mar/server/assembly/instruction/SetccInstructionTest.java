package net.simon987.mar.server.assembly.instruction;

import net.simon987.mar.server.assembly.Operand;
import net.simon987.mar.server.assembly.OperandType;
import net.simon987.mar.server.assembly.exception.AssemblyException;
import net.simon987.mar.server.assembly.exception.IllegalOperandException;
import net.simon987.mar.server.assembly.exception.InvalidMnemonicException;
import org.junit.Test;

import java.io.ByteArrayOutputStream;

import static org.junit.Assert.assertTrue;

public class SetccInstructionTest {
    /**
     * Since SETCC is not an actual valid mnemonic, encoding the SetccInstruction class should throw an exception
     */
    @Test
    public void throwsInvalidMnemonicException() {
        SetccInstruction instruction = new SetccInstruction();

        boolean hasThrown = false;
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Operand operand = new Operand(OperandType.MEMORY_REG16, 1);
            instruction.encode(stream, operand, 0);
        } catch (AssemblyException exception) {
            if (exception instanceof InvalidMnemonicException) {
                hasThrown = true;
            }
        }
        assertTrue(hasThrown);
    }

    @Test
    public void throwsIllegalOperandException() {
        SetccInstruction instruction = new SetccInstruction();

        boolean hasThrownForZeroOperands = false;
        boolean oneOperandImmediatIsInvalid = false;
        boolean hasThrownForTwoOperands = false;

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        try {
            instruction.encode(stream, 0);
        } catch (AssemblyException exception) {
            if (exception instanceof IllegalOperandException) {
                hasThrownForZeroOperands = true;
            }
        }

        try {
            Operand o1 = new Operand(OperandType.MEMORY_REG16, 1);
            Operand o2= new Operand(OperandType.MEMORY_REG16, 1);
            instruction.encode(stream, o1, o2, 0);
        } catch (AssemblyException exception) {
            if (exception instanceof IllegalOperandException) {
                hasThrownForTwoOperands = true;
            }
        }

        Operand invalidOperand = new Operand(OperandType.IMMEDIATE16, 0);
        oneOperandImmediatIsInvalid = !instruction.operandValid(invalidOperand);

        assertTrue(hasThrownForZeroOperands);
        assertTrue(hasThrownForTwoOperands);
        assertTrue(oneOperandImmediatIsInvalid);
    }
}
