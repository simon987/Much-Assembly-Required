package net.simon987.server.assembly;

import net.simon987.server.GameServer;
import net.simon987.server.ServerConfiguration;
import net.simon987.server.assembly.exception.*;
import net.simon987.server.logging.LogManager;
import org.apache.commons.text.StringEscapeUtils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Top-level class for assembly operations.
 */
public class Assembler {

    private ServerConfiguration config;

    private InstructionSet instructionSet;

    private RegisterSet registerSet;

    private static final int MEM_SIZE = GameServer.INSTANCE.getConfig().getInt("memory_size");

    public Assembler(InstructionSet instructionSet, RegisterSet registerSet, ServerConfiguration config) {
        this.instructionSet = instructionSet;
        this.registerSet = registerSet;
        this.config = config;
    }

    /**
     * Remove the comment part of a line
     *
     * @param line The line to trim
     * @return The line without its comment part
     */
    private static String removeComment(String line) {
        if (line.indexOf(';') != -1) {
            return line.substring(0, line.indexOf(';'));
        } else {
            return line;
        }
    }

    /**
     * Remove the label part of a line
     *
     * @param line The line to trim
     * @return The line without its label part
     */
    private static String removeLabel(String line) {

        return line.replaceAll("^\\s*\\b\\w*\\b:", "");

    }

    /**
     * Check for and save the origin
     *
     * @param line   Current line. Assuming that the comments and labels are removed
     * @param result Current line number
     */
    private static void checkForORGInstruction(String line, AssemblyResult result, int currentLine)
            throws AssemblyException {
        line = removeComment(line);
        line = removeLabel(line);

        //Split string
        String[] tokens = line.trim().split("\\s+");
        String mnemonic = tokens[0];

        if (mnemonic.toUpperCase().equals("ORG")) {
            if (tokens.length > 1) {
                try {
                    result.origin = (Integer.decode(tokens[1]));
                    throw new PseudoInstructionException(currentLine);
                } catch (NumberFormatException e) {
                    throw new InvalidOperandException("Invalid operand \"" + tokens[1] + '"', currentLine);
                }
            }
        }
    }

    /**
     * Check for labels in a line and save it
     *
     * @param line          Line to check
     * @param result        Current assembly result
     * @param currentOffset Current offset in bytes
     */
    private static void checkForLabel(String line, AssemblyResult result, char currentOffset) {

        line = removeComment(line);

        //Check for labels
        Pattern pattern = Pattern.compile("^\\s*\\b\\w*\\b:");
        Matcher matcher = pattern.matcher(line);

        if (matcher.find()) {
            String label = matcher.group(0).substring(0, matcher.group(0).length() - 1).trim();

            LogManager.LOGGER.fine("DEBUG: Label " + label + " @ " + (result.origin + currentOffset));
            result.labels.put(label, (char) (result.origin + currentOffset));
        }
    }

    /**
     * Check if a line is empty
     *
     * @param line Line to check
     * @return true if a line only contains white space
     */
    private static boolean isLineEmpty(String line) {
        return line.replaceAll("\\s+", "").isEmpty();
    }

    /**
     * Parse the DW instruction (Define word). Handles DUP operator
     *
     * @param line        Current line. assuming that comments and labels are removed
     * @param currentLine Current line number
     * @param labels      Map of labels
     * @return Encoded instruction, null if the line is not a DW instruction
     */
    private static byte[] parseDWInstruction(String line, HashMap<String, Character> labels, int currentLine)
            throws InvalidOperandException {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(bos);

        //System.out.println(line);

        if (line.length() >= 2 && line.substring(0, 2).toUpperCase().equals("DW")) {

            try {

                //Special thanks to https://stackoverflow.com/questions/1757065/
                String[] values = line.substring(2).split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

                for (String value : values) {

                    value = value.trim();

                    String[] valueTokens = value.split("\\s+");

                    //Handle DUP operator
                    if (valueTokens.length == 2 && valueTokens[1].toUpperCase().contains("DUP(")) {
                        out.write(parseDUPOperator16(valueTokens, labels, currentLine));
                    } else if (value.startsWith("\"") && value.endsWith("\"")) {
                        //Handle string

                        //Unescape the string
                        String string = value.substring(1, value.length() - 1);

                        try {
                            string = StringEscapeUtils.unescapeJava(string);
                        } catch (IllegalArgumentException e) {
                            throw new InvalidOperandException(
                                "Invalid string operand \"" + string + "\": " + e.getMessage(), 
                                currentLine);
                        }

                        out.write(string.getBytes(StandardCharsets.UTF_16BE));
                    } else if (labels != null && labels.containsKey(value)) {
                        //Handle label
                        out.writeChar(labels.get(value));

                    } else {
                        //Handle integer value
                        try {
                            out.writeChar(Integer.decode(value));

                        } catch (NumberFormatException e) {
                            //Handle assumed label
                            if (labels == null) {

                                // Write placeholder word
                                out.writeChar(0);

                            } else {

                                //Integer.decode failed, try binary
                                if (value.startsWith("0b")) {
                                    try {
                                        out.writeChar(Integer.parseInt(value.substring(2), 2));
                                    } catch (NumberFormatException e2) {
                                        throw new InvalidOperandException("Invalid operand \"" + value + '"', currentLine);
                                    }
                                } else {
                                    throw new InvalidOperandException("Invalid operand \"" + value + '"', currentLine);

                                }
                            }
                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            return null;
        }

        return bos.toByteArray();

    }

    /**
     * Parse the dup operator
     *
     * @param valueTokens Value tokens e.g. {"8", "DUP(12)"}
     * @param labels      Map of labels
     * @return The encoded instruction
     */
    private static byte[] parseDUPOperator16(String[] valueTokens, HashMap<String, Character> labels, int currentLine)
            throws InvalidOperandException {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {

            int factor = Integer.decode(valueTokens[0]);

            if (factor > MEM_SIZE) {
                throw new InvalidOperandException("Factor '"+factor+"' exceeds total memory size", currentLine);
            }

            String value = valueTokens[1].substring(4, valueTokens[1].lastIndexOf(')'));

            //Handle label
            if (labels != null && labels.containsKey(value)) {
                //Label value is casted to byte
                for (int i = 0; i < factor; i++) {
                    char s = labels.get(value);

                    out.write(Util.getHigherByte(s));
                    out.write(Util.getLowerByte(s));
                }

            } else {
                //Handle integer value
                char s = (char) (int) Integer.decode(value);

                for (int i = 0; i < factor; i++) {
                    out.write(Util.getHigherByte(s));
                    out.write(Util.getLowerByte(s));
                }
            }


        } catch (NumberFormatException e) {
            throw new InvalidOperandException("Usage: <factor> DUP(<value>)", currentLine);
        }

        return out.toByteArray();

    }

    /**
     * Parse the DW instruction (Define word). Handles DUP operator
     *
     * @param line        Current line. assuming that comments and labels are removed
     * @param currentLine Current line number
     * @return Encoded instruction, null if the line is not a DW instruction
     */
    private static byte[] parseDWInstruction(String line, int currentLine) throws AssemblyException {
        return parseDWInstruction(line, null, currentLine);
    }

    /**
     * Check for and handle section declarations (.text and .data)
     *
     * @param line Current line
     */
    private static void checkForSectionDeclaration(String line, AssemblyResult result,
                                                   int currentLine, int currentOffset) throws AssemblyException {

        String[] tokens = line.split("\\s+");

        if (tokens[0].toUpperCase().equals(".TEXT")) {

            result.defineSecton(Section.TEXT, currentLine, currentOffset);
            throw new PseudoInstructionException(currentLine);

        } else if (tokens[0].toUpperCase().equals(".DATA")) {

            LogManager.LOGGER.fine("DEBUG: .data @" + currentLine);

            result.defineSecton(Section.DATA, currentLine, currentOffset);
            throw new PseudoInstructionException(currentLine);
        }
    }

    /**
     * Check for and handle the EQU instruction
     *
     * @param line        Current line. The method is assuming that comments and labels are removed
     * @param labels      Map of labels. Constants will be added as labels
     * @param currentLine Current line number
     */
    private static void checkForEQUInstruction(String line, HashMap<String, Character> labels, int currentLine)
            throws AssemblyException {
        /*  the EQU pseudo instruction is equivalent to the #define compiler directive in C/C++
         *  usage: constant_name EQU <immediate_value>
         *  A constant treated the same way as a label.
        */
        line = line.trim();
        String[] tokens = line.split("\\s+");


        if (line.toUpperCase().matches(".*\\bEQU\\b.*")) {
            if (tokens[1].toUpperCase().equals("EQU") && tokens.length == 3) {
                try {
                    //Save value as a label
                    labels.put(tokens[0], (char) (int) Integer.decode(tokens[2]));
                } catch (NumberFormatException e) {
                    throw new InvalidOperandException("Usage: constant_name EQU immediate_value", currentLine);
                }
            } else {
                throw new InvalidOperandException("Usage: constant_name EQU immediate_value", currentLine);
            }

            throw new PseudoInstructionException(currentLine);
        }
    }

    /**
     * Parses a text and assembles it. The assembler splits the text in
     * lines and parses them one by one. It does 3 passes, the first one
     * gets the origin of the code, the second one gets the label offsets
     * and the third pass encodes the instructions.
     *
     * @param text text to assemble
     * @return the result of the assembly. Includes the assembled code and
     * the errors, if any.
     */
    public AssemblyResult parse(String text) {

        int currentLine;

        //Split in lines
        AssemblyResult result = new AssemblyResult(config);
        String[] lines = text.split("\n");

        LogManager.LOGGER.info("Assembly job started: " + lines.length + " lines to parse.");

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        //Pass 1: Get code origin
        for (currentLine = 0; currentLine < lines.length; currentLine++) {
            try {
                checkForORGInstruction(lines[currentLine], result, currentLine);

            } catch (PseudoInstructionException e) {
                break; //Origin is set, skip checking the rest

            } catch (AssemblyException e) {
                //Ignore error
            }
        }

        //Pass 2: Save label names and location
        int currentOffset = 0;
        for (currentLine = 0; currentLine < lines.length; currentLine++) {
            try {
                checkForLabel(lines[currentLine], result, (char)currentOffset);

                //Increment offset
                currentOffset += parseInstruction(lines[currentLine], currentLine, instructionSet).length / 2;

                if (currentOffset >= MEM_SIZE) {
                    throw new OffsetOverflowException(currentOffset, MEM_SIZE, currentLine);
                }
            } catch (FatalAssemblyException e) {
                //Don't bother parsing the rest of the code, since it will not be assembled anyway
                break;
            } catch (AssemblyException e1) {
                //Ignore error on pass 2

            }
        }


        //Pass 3: encode instructions
        currentOffset = 0;
        for (currentLine = 0; currentLine < lines.length; currentLine++) {

            String line = lines[currentLine];

            try {

                line = removeComment(line);
                line = removeLabel(line);

                if (isLineEmpty(line)) {
                    throw new EmptyLineException(currentLine);
                }

                //Check for pseudo instructions
                checkForSectionDeclaration(line, result, currentLine, currentOffset);
                checkForEQUInstruction(line, result.labels, currentLine);
                checkForORGInstruction(line, result, currentLine);

                //Encode instruction
                byte[] bytes = parseInstruction(line, currentLine, result.labels, instructionSet);
                currentOffset += bytes.length / 2;

                if (currentOffset >= MEM_SIZE) {
                    throw new OffsetOverflowException(currentOffset, MEM_SIZE, currentLine);
                }

                out.write(bytes);

            } catch (EmptyLineException | PseudoInstructionException e) {
                //Ignore empty lines and pseudo-instructions
            } catch (FatalAssemblyException asmE) {
                // Save error, but abort assembly at this line
                result.exceptions.add(asmE);
                break;
            } catch (AssemblyException asmE) {
                //Save errors on pass3
                result.exceptions.add(asmE);
            } catch (IOException ioE) {
                ioE.printStackTrace();
            }
        }

        //If the code contains OffsetOverFlowException(s), don't bother writing the assembled bytes to memory
        boolean writeToMemory = true;
        for (Exception e : result.exceptions) {
            if (e instanceof OffsetOverflowException) {
                writeToMemory = false;
            }
        }

        if (writeToMemory) {
            result.bytes = out.toByteArray();
        } else {
            result.bytes = new byte[0];
            LogManager.LOGGER.fine("Skipping writing assembled bytes to memory. (OffsetOverflowException)");
        }

        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        LogManager.LOGGER.info("Assembled " + result.bytes.length + " bytes (" + result.exceptions.size() + " errors)");
        for (AssemblyException e : result.exceptions) {
            LogManager.LOGGER.severe(e.getMessage() + '@' + e.getLine());
        }
        LogManager.LOGGER.info('\n' + Util.toHex(result.bytes));

        return result;

    }

    /**
     * Parse an instruction and encode it
     *
     * @param line        Line to parse
     * @param currentLine Current line
     * @return The encoded instruction
     */
    private byte[] parseInstruction(String line, int currentLine, InstructionSet instructionSet) throws AssemblyException {
        return parseInstruction(line, currentLine, null, instructionSet, true);
    }

    /**
     * Parse an instruction and encode it
     *
     * @param line        Line to parse
     * @param currentLine Current line
     * @param labels      List of labels
     * @return The encoded instruction
     */
    private byte[] parseInstruction(String line, int currentLine, HashMap<String, Character> labels,
                                    InstructionSet instructionSet)
            throws AssemblyException {
        return parseInstruction(line, currentLine, labels, instructionSet, false);
    }

    /**
     * Parse an instruction and encode it
     *
     * @param line         Line to parse
     * @param currentLine  Current line
     * @param labels       List of labels
     * @param assumeLabels Assume that unknown operands are labels
     * @return The encoded instruction
     */
    private byte[] parseInstruction(String line, int currentLine, HashMap<String, Character> labels,
                                    InstructionSet instructionSet, boolean assumeLabels)
            throws AssemblyException {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        line = removeComment(line);
        line = removeLabel(line);
        line = line.trim();

        if (isLineEmpty(line)) {
            throw new EmptyLineException(currentLine);
        }

        //Split string
        String[] tokens = line.trim().split("\\s+");
        String mnemonic = tokens[0];

        //Check for DW instruction
        try {
            if (assumeLabels) {
                byte[] bytes = parseDWInstruction(line, currentLine);
                if (bytes != null) {
                    out.write(bytes);
                    return out.toByteArray();
                }
            } else {
                byte[] bytes = parseDWInstruction(line, labels, currentLine);
                if (bytes != null) {
                    out.write(bytes);
                    return out.toByteArray();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (instructionSet.get(mnemonic) == null) {
            throw new InvalidMnemonicException(mnemonic, currentLine);

        }

        //Check operands and encode instruction
        final int beginIndex = line.indexOf(mnemonic) + mnemonic.length();
        if (line.contains(",")) {
            //2 operands
            String strO1 = line.substring(beginIndex, line.indexOf(','));
            String strO2 = line.substring(line.indexOf(','));

            Operand o1, o2;

            if (assumeLabels) {
                o1 = new Operand(strO1, registerSet, currentLine);
                o2 = new Operand(strO2, registerSet, currentLine);
            } else {
                o1 = new Operand(strO1, labels, registerSet, currentLine);
                o2 = new Operand(strO2, labels, registerSet, currentLine);
            }

            //Encode instruction
            //Get instruction by name
            instructionSet.get(mnemonic).encode(out, o1, o2, currentLine);

        } else if (tokens.length > 1) {
            //1 operand

            String strO1 = line.substring(beginIndex);

            Operand o1;
            if (assumeLabels) {
                o1 = new Operand(strO1, registerSet, currentLine);
            } else {
                o1 = new Operand(strO1, labels, registerSet, currentLine);
            }

            //Encode instruction
            //Get instruction by name
            instructionSet.get(mnemonic).encode(out, o1, currentLine);
        } else {
            //No operand

            //Encode instruction
            //Get instruction by name
            instructionSet.get(mnemonic).encode(out, currentLine);
        }

        return out.toByteArray();
    }
}
