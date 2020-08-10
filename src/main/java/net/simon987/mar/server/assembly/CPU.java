package net.simon987.mar.server.assembly;

import net.simon987.mar.server.GameServer;
import net.simon987.mar.server.IServerConfiguration;
import net.simon987.mar.server.assembly.exception.CancelledException;
import net.simon987.mar.server.assembly.instruction.*;
import net.simon987.mar.server.event.CpuInitialisationEvent;
import net.simon987.mar.server.event.GameEvent;
import net.simon987.mar.server.game.GameUniverse;
import net.simon987.mar.server.game.objects.ControllableUnit;
import net.simon987.mar.server.game.objects.HardwareHost;
import net.simon987.mar.server.io.MongoSerializable;
import net.simon987.mar.server.logging.LogManager;
import org.bson.Document;

/**
 * CPU: Central Processing Unit. A CPU is capable of reading bytes from
 * a Memory object and execute them. A CPU object holds registers objects and
 * a Memory object.
 */
public class CPU implements MongoSerializable {

    private final Status status;

    private boolean trapFlag = false;
    private boolean executing = false;

    /**
     * Memory associated with the CPU, 64kb max
     */
    private Memory memory;

    /**
     * set of instructions of this CPU
     */
    private final InstructionSet instructionSet;

    /**
     * set of registers of this CPU
     */
    private RegisterSet registerSet;

    /**
     * Offset of the code section. The code starts to get
     * executed at this address each tick. Defaults to org_offset@config.properties
     */
    private int codeSectionOffset;

    private int interruptVectorTableOffset;
    private final int graceInstructionCount;
    private int graceInstructionsLeft;
    private boolean isGracePeriod;
    private int instructionAllocation;

    /**
     * Instruction pointer, always points to the next instruction
     */
    private int ip;

    public static final int INSTRUCTION_COST = 10000;

    /**
     * Hardware is connected to the hardwareHost
     */
    private HardwareHost hardwareHost;

    private int registerSetSize;

    private static final char EXECUTION_COST_ADDR = 0x0150;
    private static final char EXECUTED_INS_ADDR = 0x0151;

    private void addInstructions() {
        instructionSet.add(new JmpInstruction(this));
        instructionSet.add(new JnzInstruction(this));
        instructionSet.add(new JzInstruction(this));
        instructionSet.add(new JgInstruction(this));
        instructionSet.add(new JgeInstruction(this));
        instructionSet.add(new JleInstruction(this));
        instructionSet.add(new JlInstruction(this));
        instructionSet.add(new PushInstruction(this));
        instructionSet.add(new PopInstruction(this));
        instructionSet.add(new CallInstruction(this));
        instructionSet.add(new RetInstruction(this));
        instructionSet.add(new MulInstruction(this));
        instructionSet.add(new DivInstruction(this));
        instructionSet.add(new JnsInstruction(this));
        instructionSet.add(new JsInstruction(this));
        instructionSet.add(new HwiInstruction(this));
        instructionSet.add(new HwqInstruction(this));
        instructionSet.add(new XchgInstruction(this));
        instructionSet.add(new JcInstruction(this));
        instructionSet.add(new JncInstruction(this));
        instructionSet.add(new JnoInstruction(this));
        instructionSet.add(new JoInstruction(this));
        instructionSet.add(new PushfInstruction(this));
        instructionSet.add(new PopfInstruction(this));
        instructionSet.add(new JnaInstruction(this));
        instructionSet.add(new JaInstruction(this));
        instructionSet.add(new IntInstruction(this));
        instructionSet.add(new IntoInstruction(this));
        instructionSet.add(new IretInstruction(this));
    }

    public CPU() {
        instructionSet = new DefaultInstructionSet();
        registerSet = new DefaultRegisterSet();
        IServerConfiguration config = GameServer.INSTANCE.getConfig();
        codeSectionOffset = config.getInt("org_offset");
        graceInstructionCount = config.getInt("grace_instruction_count");

        interruptVectorTableOffset = config.getInt("ivt_offset");

        addInstructions();
        status = new Status();
    }

    /**
     * Creates a new CPU
     */
    public CPU(IServerConfiguration config) throws CancelledException {
        instructionSet = new DefaultInstructionSet();
        registerSet = new DefaultRegisterSet();
        codeSectionOffset = config.getInt("org_offset");
        graceInstructionCount = config.getInt("grace_instruction_count");

        interruptVectorTableOffset = config.getInt("ivt_offset");

        addInstructions();

        status = new Status();
        memory = new Memory(config.getInt("memory_size"));
    }

    /**
     * Sets the IP to IVT + number and pushes flags then the old IP
     */
    public void interrupt(int number) {

        if (number == 3) {
            trapFlag = true;
            return;
        }

        Instruction push = instructionSet.get(PushInstruction.OPCODE);
        push.execute(status.toWord(), status);
        push.execute(ip, status);

        this.setIp((char) memory.get(interruptVectorTableOffset + number));
    }

    public void reset() {
        status.clear();
        ip = codeSectionOffset;
        graceInstructionsLeft = graceInstructionCount;
        isGracePeriod = false;
        trapFlag = false;
    }

    public void setInstructionAlloction(int instructionAllocation) {
        this.instructionAllocation = instructionAllocation;
    }

    public int execute() {

        executing = true;
        int counter = 0;
        status.clear();

        registerSetSize = registerSet.size();

        while (!status.isBreakFlag()) {
            counter++;

            if (isGracePeriod) {
                if (graceInstructionsLeft-- <= 0) {
                    writeExecutionStats(counter);
                    return counter / INSTRUCTION_COST;
                }
            } else if (instructionAllocation-- <= 0) {
                interrupt(IntInstruction.INT_EXEC_LIMIT_REACHED);
                isGracePeriod = true;
            }

            step();

            if (trapFlag) {
                break;
            }
        }

//        LogManager.LOGGER.fine(counter + " instruction in " + elapsed + "ms : " + (double) counter / (elapsed / 1000) / 1000000 + "MHz");

        writeExecutionStats(counter);

        executing = false;
        return counter / INSTRUCTION_COST;
    }

    public void step() {
        //fetch instruction
        int machineCode = memory.get(ip);

        /*
         * Contents of machineCode should look like this:
         * SSSS SDDD DDOO OOOO
         * Where S is source, D is destination and O is the opCode
         */
        Instruction instruction = instructionSet.get(machineCode & 0x03F); // 0000 0000 00XX XXXX

        int source = (machineCode >> 11) & 0x001F; // XXXX X000 0000 0000
        int destination = (machineCode >> 6) & 0x001F; // 0000 0XXX XX00 0000

        executeInstruction(instruction, source, destination);
//        LogManager.LOGGER.info(instruction.getMnemonic());

        if (status.isBreakFlag()) {
            trapFlag = false;
        }
    }

    private void writeExecutionStats(int counter) {
        memory.set(EXECUTED_INS_ADDR, Util.getHigherWord(counter));
        memory.set(EXECUTED_INS_ADDR + 1, Util.getLowerWord(counter));
    }

    public void executeInstruction(Instruction instruction, int source, int destination) {

        //Execute the instruction
        if (source == 0) {
            //No operand (assuming that destination is also null)
            ip++;
            instruction.execute(status);
        } else if (source == Operand.IMMEDIATE_VALUE) {
            executeImmediateValue(instruction, source, destination);
        } else if (source == Operand.IMMEDIATE_VALUE_MEM) {
            executeImmediateValueMem(instruction, source, destination);
        } else if (source <= registerSetSize) {
            executeSourceIsRegister(instruction, source, destination);
        } else if (source <= registerSetSize * 2) {
            //Source is [reg]
            if (destination == 0) {
                //Single operand
                ip++;
                instruction.execute(memory, registerSet.get(source - registerSetSize), status);
            } else if (destination == Operand.IMMEDIATE_VALUE) {
                //Destination is an immediate value
                //this shouldn't happen
                LogManager.LOGGER.severe("Trying to execute an instruction with an" +
                        "immediate values as dst operand"); //todo remove debug info
            } else if (destination == Operand.IMMEDIATE_VALUE_MEM) {
                //Destination is an memory immediate
                ip++;
                instruction.execute(memory, memory.get(ip++), memory, registerSet.get(source - registerSetSize), status);
            } else if (destination <= registerSetSize) {
                //Destination is a register
                ip++;
                instruction.execute(registerSet, destination, memory, registerSet.get(source - registerSetSize), status);
            } else if (destination <= registerSetSize * 2) {
                //Destination is [reg]
                ip++;
                instruction.execute(memory, registerSet.get(destination - registerSetSize), memory, registerSet.get(source - registerSetSize), status);
            } else {
                //Assuming that destination is [reg + x]
                ip += 2;
                instruction.execute(memory, registerSet.get(destination - registerSetSize - registerSetSize) + memory.get(ip - 1),
                        memory, registerSet.get(source - registerSetSize), status);
            }
        } else {
            //Assuming that source is [reg + X]

            ip++;
            int sourceDisp = memory.get(ip);

            if (destination == 0) {
                //Single operand
                ip += 1;
                instruction.execute(memory, registerSet.get(source - registerSetSize - registerSetSize) + memory.get(ip - 1), status);

            } else if (destination == Operand.IMMEDIATE_VALUE) {
                //Destination is an immediate value
                //this shouldn't happen
                LogManager.LOGGER.severe("Trying to execute an instruction with an" +
                        "immediate values as dst operand"); //todo remove debug info
            } else if (destination == Operand.IMMEDIATE_VALUE_MEM) {
                //Destination is memory immediate
                ip += 2;
                instruction.execute(memory, memory.get(ip - 1), memory,
                        registerSet.get(source - registerSetSize - registerSetSize) + sourceDisp, status);
            } else if (destination <= registerSetSize) {
                //Destination is a register
                ip++;
                instruction.execute(registerSet, destination, memory,
                        registerSet.get(source - registerSetSize - registerSetSize) + sourceDisp, status);
            } else if (destination <= registerSetSize * 2) {
                //Destination is [reg]
                ip++;
                instruction.execute(memory, registerSet.get(destination - registerSetSize), memory,
                        registerSet.get(source - registerSetSize - registerSetSize) + sourceDisp, status);
            } else {
                //Assuming that destination is [reg + x]
                ip += 2;
                instruction.execute(memory, registerSet.get(destination - registerSetSize - registerSetSize) + memory.get(ip - 1),
                        memory, registerSet.get(source - registerSetSize - registerSetSize) + sourceDisp, status);
            }
        }
    }

    private void executeSourceIsRegister(Instruction instruction, int source, int destination) {
        //Source is a register
        if (destination == 0) {
            //Single operand
            ip++;
            instruction.execute(registerSet, source, status);

        } else if (destination == Operand.IMMEDIATE_VALUE) {
            //Destination is an immediate value
            //this shouldn't happen
            LogManager.LOGGER.severe("Trying to execute an instruction with an" +
                    "immediate values as dst operand"); //todo remove debug info
        } else if (destination == Operand.IMMEDIATE_VALUE_MEM) {
            //Destination is memory immediate
            ip += 2;
            instruction.execute(memory, memory.get(ip - 1), registerSet, source, status);
        } else if (destination <= registerSetSize) {
            //Destination is a register too
            ip++;
            instruction.execute(registerSet, destination, registerSet, source, status);
        } else if (destination <= registerSetSize * 2) {
            //Destination is [reg]
            ip++;
            instruction.execute(memory, registerSet.get(destination - registerSetSize), registerSet, source, status);
        } else {
            //Assuming that destination is [reg + x]
            ip += 2;
            instruction.execute(memory, registerSet.get(destination - registerSetSize - registerSetSize) + memory.get(ip - 1),
                    registerSet, source, status);
        }
    }

    private void executeImmediateValue(Instruction instruction, int source, int destination) {
        ip++;
        int sourceValue = memory.get(ip);

        if (destination == 0) {
            //Single operand
            ip++;
            instruction.execute(sourceValue, status);
        } else if (destination == Operand.IMMEDIATE_VALUE) {
            //Destination is an immediate value too
            //this shouldn't happen
            LogManager.LOGGER.severe("Trying to execute an instruction with 2" +
                    "immediate values as operands"); //todo remove debug info

        } else if (destination == Operand.IMMEDIATE_VALUE_MEM) {
            //Destination is memory immediate
            ip += 2;
            instruction.execute(memory, memory.get(ip - 1), sourceValue, status);
        } else if (destination <= registerSetSize) {
            //Destination is a register
            ip++;
            instruction.execute(registerSet, destination, sourceValue, status);

        } else if (destination <= registerSetSize * 2) {
            //Destination is [reg]
            ip++;
            instruction.execute(memory, registerSet.get(destination - registerSetSize), sourceValue, status);
        } else {
            //Assuming that destination is [reg + x]
            ip += 2;
            instruction.execute(memory, registerSet.get(destination - registerSetSize - registerSetSize) + memory.get(ip - 1),
                    sourceValue, status);
        }
    }

    private void executeImmediateValueMem(Instruction instruction, int source, int destination) {
        //Source is [x]
        ip++;
        int sourceValue = memory.get(memory.get(ip));

        if (destination == 0) {
            //Single operand
            ip++;
            instruction.execute(memory, memory.get(ip - 1), status);
        } else if (destination == Operand.IMMEDIATE_VALUE) {
            //Destination is an immediate value

            //this shouldn't happen
            LogManager.LOGGER.severe("Trying to execute an instruction with an" +
                    "immediate values as dst operand"); //todo remove debug info
        } else if (destination == Operand.IMMEDIATE_VALUE_MEM) {
            //Destination is memory immediate too
            ip += 2;
            instruction.execute(memory, memory.get(ip - 1), sourceValue, status);
        } else if (destination <= registerSetSize) {
            //Destination is a register
            ip++;
            instruction.execute(registerSet, destination, sourceValue, status);
        } else if (destination <= registerSetSize * 2) {
            //Destination is [reg]
            ip++;
            instruction.execute(memory, registerSet.get(destination - registerSetSize), sourceValue, status);
        } else {
            //Assuming that destination is [reg + x]
            ip += 2;
            instruction.execute(memory, registerSet.get(destination - registerSetSize - registerSetSize) + memory.get(ip - 1), sourceValue, status);
        }
    }

    @Override
    public Document mongoSerialise() {
        Document dbObject = new Document();

        dbObject.put("memory", memory.mongoSerialise());

        dbObject.put("registerSet", registerSet.mongoSerialise());
        dbObject.put("codeSegmentOffset", codeSectionOffset);


        return dbObject;

    }

    public static CPU deserialize(Document obj, ControllableUnit unit) throws CancelledException {

        CPU cpu = new CPU(GameServer.INSTANCE.getConfig());

        cpu.codeSectionOffset = obj.getInteger("codeSegmentOffset");

        cpu.memory = new Memory((Document) obj.get("memory"));
        cpu.registerSet = RegisterSet.deserialize((Document) obj.get("registerSet"));

        GameEvent event = new CpuInitialisationEvent(cpu, unit);
        GameServer.INSTANCE.getEventDispatcher().dispatch(event);
        if (event.isCancelled()) {
            throw new CancelledException();
        }

        return cpu;
    }

    public InstructionSet getInstructionSet() {
        return instructionSet;
    }

    public RegisterSet getRegisterSet() {
        return registerSet;
    }

    public Memory getMemory() {
        return memory;
    }

    public void setMemory(Memory memory) {
        this.memory = memory;
    }

    public void setRegisterSet(RegisterSet registerSet) {
        this.registerSet = registerSet;
    }

    public Status getStatus() {
        return status;
    }

    public int getIp() {
        return ip;
    }

    public void setIp(char ip) {
        this.ip = ip;
    }

    public int getCodeSectionOffset() {
        return codeSectionOffset;
    }

    public void setCodeSectionOffset(int codeSectionOffset) {
        this.codeSectionOffset = codeSectionOffset;
    }


    @Override
    public String toString() {
        String str = registerSet.toString();
        str += status.toString();

        return str;
    }

    public HardwareHost getHardwareHost() {
        return hardwareHost;
    }

    public void setHardwareHost(HardwareHost hardwareHost) {
        this.hardwareHost = hardwareHost;
    }

    /**
     * For testing/debugging, this creates an copy (please be mindful of the memory usage)
     */
    public CpuState getState() {
        return new CpuState(
                registerSet.clone(),
                memory.clone(),
                status.clone()
        );
    }

    public boolean isPaused() {
        return trapFlag;
    }

    public boolean isExecuting() {
        return executing;
    }

    public void setTrapFlag(boolean trapFlag) {
        this.trapFlag = trapFlag;
    }
}
