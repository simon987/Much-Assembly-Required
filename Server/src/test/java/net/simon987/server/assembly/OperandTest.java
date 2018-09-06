package net.simon987.server.assembly;

import net.simon987.server.assembly.exception.InvalidOperandException;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;



public class OperandTest {


    @Test
    public void Operand() {

        RegisterSet registerSet = new DefaultRegisterSet();
        HashMap<String, Character> labels = new HashMap<>(10);
        labels.put("label1", (char) 10);
        labels.put("label2", (char) 20);
        labels.put("label3", (char) 30);
        labels.put("label4", (char) 40);


        //Valid operands
        try {
            //Register
            Operand reg1 = new Operand("a", labels, registerSet, 0);
            assertEquals(OperandType.REGISTER16, reg1.getType());
            assertEquals(1, reg1.getValue());
            assertEquals(0, reg1.getData());

            Operand reg2 = new Operand("a ", labels, registerSet, 0);
            assertEquals(OperandType.REGISTER16, reg2.getType());
            assertEquals(1, reg2.getValue());
            assertEquals(0, reg2.getData());

            Operand reg3 = new Operand("    A ", labels, registerSet, 0);
            assertEquals(OperandType.REGISTER16, reg3.getType());
            assertEquals(1, reg3.getValue());
            assertEquals(0, reg3.getData());

            Operand reg4 = new Operand("B", labels, registerSet, 0);
            assertEquals(OperandType.REGISTER16, reg4.getType());
            assertEquals(2, reg4.getValue());
            assertEquals(0, reg4.getData());

            //Immediate
            Operand imm1 = new Operand(" +12", labels, registerSet, 0);
            assertEquals(OperandType.IMMEDIATE16, imm1.getType());
            assertEquals(Operand.IMMEDIATE_VALUE, imm1.getValue());
            assertEquals(12, imm1.getData());

            Operand imm2 = new Operand(" -12", labels, registerSet, 0);
            assertEquals(OperandType.IMMEDIATE16, imm2.getType());
            assertEquals(Operand.IMMEDIATE_VALUE, imm2.getValue());
            assertEquals(-12, imm2.getData());

            Operand imm3 = new Operand(" 0xABCD", labels, registerSet, 0);
            assertEquals(OperandType.IMMEDIATE16, imm3.getType());
            assertEquals(Operand.IMMEDIATE_VALUE, imm3.getValue());
            assertEquals(0xABCD, imm3.getData());

            Operand imm4 = new Operand(" label1", labels, registerSet, 0);
            assertEquals(OperandType.IMMEDIATE16, imm4.getType());
            assertEquals(Operand.IMMEDIATE_VALUE, imm4.getValue());
            assertEquals(10, imm4.getData());

            //Memory Immediate
            Operand mem1 = new Operand("[+12]", labels, registerSet, 0);
            assertEquals(OperandType.MEMORY_IMM16, mem1.getType());
            assertEquals(Operand.IMMEDIATE_VALUE_MEM, mem1.getValue());
            assertEquals(12, mem1.getData());

            Operand mem2 = new Operand("[-12    ]", labels, registerSet, 0);
            assertEquals(OperandType.MEMORY_IMM16, mem2.getType());
            assertEquals(Operand.IMMEDIATE_VALUE_MEM, mem2.getValue());
            assertEquals(-12, mem2.getData());

            Operand mem3 = new Operand(" [ 0xABCD]", labels, registerSet, 0);
            assertEquals(OperandType.MEMORY_IMM16, mem3.getType());
            assertEquals(Operand.IMMEDIATE_VALUE_MEM, mem3.getValue());
            assertEquals(0xABCD, mem3.getData());

            Operand mem4 = new Operand("[ label1 ]", labels, registerSet, 0);
            assertEquals(OperandType.MEMORY_IMM16, mem4.getType());
            assertEquals(Operand.IMMEDIATE_VALUE_MEM, mem4.getValue());
            assertEquals(10, mem4.getData());

            //Memory Reg
            Operand mem5 = new Operand("[ A ]", labels, registerSet, 0);
            assertEquals(OperandType.MEMORY_REG16, mem5.getType());
            assertEquals(1 + registerSet.size(), mem5.getValue());
            assertEquals(0, mem5.getData());

            Operand mem6 = new Operand("[ B     ]", labels, registerSet, 0);
            assertEquals(OperandType.MEMORY_REG16, mem6.getType());
            assertEquals(2 + registerSet.size(), mem6.getValue());
            assertEquals(0, mem6.getData());

            //Memory Reg + displacement
            Operand mem7 = new Operand("[ A + 1 ]", labels, registerSet, 0);
            assertEquals(OperandType.MEMORY_REG_DISP16, mem7.getType());
            assertEquals(1 + 2 * registerSet.size(), mem7.getValue());
            assertEquals(1, mem7.getData());

            Operand mem8 = new Operand("[   B +    label1     ]", labels, registerSet, 0);
            assertEquals(OperandType.MEMORY_REG_DISP16, mem8.getType());
            assertEquals(2 + 2 * registerSet.size(), mem8.getValue());
            assertEquals(10, mem8.getData());

            Operand mem9 = new Operand("[ BP + 1 ]", labels, registerSet, 0);
            assertEquals(OperandType.MEMORY_REG_DISP16, mem9.getType());
            assertEquals(8 + 2 * registerSet.size(), mem9.getValue());
            assertEquals(1, mem9.getData());

            Operand mem10 = new Operand("[   B -    label1     ]", labels, registerSet, 0);
            assertEquals(OperandType.MEMORY_REG_DISP16, mem10.getType());
            assertEquals(2 + 2 * registerSet.size(), mem10.getValue());
            assertEquals(-10, mem10.getData());


        } catch (InvalidOperandException e) {
            fail("Failed trying to parse a valid operand");
        }

        //Invalid operands
        try{ new Operand("aa", labels, registerSet, 0); } catch (InvalidOperandException e){}
        try{   new Operand("a1", labels, registerSet, 0); } catch (InvalidOperandException e){}
        try{   new Operand("a_", labels, registerSet, 0); } catch (InvalidOperandException e){}
        try{   new Operand("_a", labels, registerSet, 0); } catch (InvalidOperandException e){}
        try{   new Operand("_1", labels, registerSet, 0); } catch (InvalidOperandException e){}
        try{    new Operand("S", labels, registerSet, 0); } catch (InvalidOperandException e){}
        try{   new Operand("label1_", labels, registerSet, 0); } catch (InvalidOperandException e){}
        try{   new Operand("+label1", labels, registerSet, 0); } catch (InvalidOperandException e){}
        try{    new Operand("[- 12]", labels, registerSet, 0); } catch (InvalidOperandException e){}
        try{    new Operand("[12+1]", labels, registerSet, 0); } catch (InvalidOperandException e){}
        try{    new Operand("[+label1", labels, registerSet, 0); } catch (InvalidOperandException e){}
        try{    new Operand("[*12]", labels, registerSet, 0); } catch (InvalidOperandException e){}
        try{     new Operand("[-A]", labels, registerSet, 0); } catch (InvalidOperandException e){}
        try{    new Operand("[A B]", labels, registerSet, 0); } catch (InvalidOperandException e){}
        try{     new Operand("[A + B]", labels, registerSet, 0); } catch (InvalidOperandException e){}
        try{    new Operand("[A + -1]", labels, registerSet, 0); } catch (InvalidOperandException e){}
        try{  new Operand("[A + ]", labels, registerSet, 0); } catch (InvalidOperandException e){}
        try{    new Operand("[]", labels, registerSet, 0); } catch (InvalidOperandException e){}
        try{    new Operand("[A+A+]", labels, registerSet, 0); } catch (InvalidOperandException e){}
        try{    new Operand("[A+[1]]", labels, registerSet, 0); } catch (InvalidOperandException e){}




    }

}
