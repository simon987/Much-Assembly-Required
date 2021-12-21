package net.simon987.mar.server.assembly;

import net.simon987.mar.server.IServerConfiguration;
import net.simon987.mar.server.assembly.exception.*;
import net.simon987.mar.server.logging.LogManager;
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

    private static final String labelPattern = "^\\s*[a-zA-Z_]\\w*:";
    private static final Pattern commentPattern = Pattern.compile("\"[^\"]*\"|(;)");
    private final IServerConfiguration config;

    private static int MEM_SIZE;
    private final InstructionSet instructionSet;
    private final RegisterSet registerSet;

    public Assembler(InstructionSet instructionSet, RegisterSet registerSet, IServerConfiguration config) {
        this.instructionSet = instructionSet;
        this.registerSet = registerSet;
        this.config = config;

        Assembler.MEM_SIZE = config.getInt("memory_size");
    }

    /**
     * Remove the comment part of a line
     *
     * @param line The line to trim
     * @return The line without its comment part
     */
    private static String removeComment(String line) {

        Matcher m = commentPattern.matcher(line);

        while (m.find()) {
            try {
                return line.substring(0, m.start(1));
            } catch (IndexOutOfBoundsException ignored) {
            }
        }

        return line;
    }

    /**
     * Remove the label part of a line
     *
     * @param line The line to trim
     * @return The line without its label part
     */
    private static String removeLabel(String line) {
        return line.replaceAll(labelPattern, "");
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

        if (mnemonic.equalsIgnoreCase("ORG")) {
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
        Pattern pattern = Pattern.compile(labelPattern);
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


    private static final Pattern DUP_PATTERN = Pattern.compile("(.+)\\s+DUP(.+)");
    private static final Pattern STRING_PATTERN = Pattern.compile("\"(.*)\"$");
    /**
     * Parse the DW instruction (Define word). Handles DUP operator
     *
     * @param line        Current line. assuming that comments and labels are removed
     * @param currentLine Current line number
     * @param labels      Map of labels
     * @return Encoded instruction, null if the line is not a DW instruction
     */
    private byte[] parseDWInstruction(String line, HashMap<String, Character> labels, int currentLine)
            throws InvalidOperandException {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(bos);

        //System.out.println(line);

        if (line.length() >= 2 && line.substring(0, 2).equalsIgnoreCase("DW")) {

            try {

                //Special thanks to https://stackoverflow.com/questions/1757065/
                String[] values = line.substring(2).split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

                for (String value : values) {

                    value = value.trim();
                    Matcher m = STRING_PATTERN.matcher(value);
                    if (m.lookingAt()) {
                        // Parse string
                        String string = m.group(1);
                        try {
                            string = StringEscapeUtils.unescapeJava(string);
                        } catch (IllegalArgumentException e) {
                            throw new InvalidOperandException(
                                    "Invalid string operand \"" + string + "\": " + e.getMessage(),
                                    currentLine);
                        }
                        out.write(string.getBytes(StandardCharsets.UTF_16BE));
                        continue;
                    }
                    int factor;
                    if (m.usePattern(DUP_PATTERN).lookingAt()) {
                        // Get DUP factor
                        TokenParser parser = new TokenParser(m.group(1), currentLine, new HashMap<>());
                        try {
                            if (TokenParser.TokenType.Constant !=
                                    parser.getNextToken(true, TokenParser.ParseContext.Value)) {
                                throw new InvalidOperandException("Invalid DUP factor " + m.group(1), currentLine);
                            }
                        } catch (AssemblyException ae) {
                            throw new InvalidOperandException("Couldn't parse DUP factor " + m.group(1), currentLine);
                        }
                        factor = parser.lastInt;
                        if (factor > MEM_SIZE) {
                            throw new InvalidOperandException(
                                    "Factor '" + factor + "' exceeds total memory size", currentLine);
                        }

                        value = m.group(2);
                    }
                    else {
                        // Parse as single number
                        factor = 1;
                    }

                    Operand operand = new Operand(value, labels, registerSet, currentLine);
                    char s = (char)operand.getData();
                    for (int i = 0; i < factor; i++) {
                        out.write(Util.getHigherByte(s));
                        out.write(Util.getLowerByte(s));
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
     * Parse the DW instruction (Define word). Handles DUP operator
     *
     * @param line        Current line. assuming that comments and labels are removed
     * @param currentLine Current line number
     * @return Encoded instruction, null if the line is not a DW instruction
     */
    private byte[] parseDWInstruction(String line, int currentLine) throws AssemblyException {
        return parseDWInstruction(line, null, currentLine);
    }

    /**
     * Check for and handle section declarations (.text and .data)
     *
     * @param line Current line
     */
    private void checkForSectionDeclaration(String line, AssemblyResult result,
                                            int currentLine, int currentOffset) throws AssemblyException {

        String[] tokens = line.split("\\s+");

        if (tokens[0].equalsIgnoreCase(".TEXT")) {

            result.defineSection(Section.TEXT, currentLine, currentOffset);
            result.disassemblyLines.add(".text");
            throw new PseudoInstructionException(currentLine);

        } else if (tokens[0].equalsIgnoreCase(".DATA")) {

            LogManager.LOGGER.fine("DEBUG: .data @" + currentLine);

            result.defineSection(Section.DATA, currentLine, currentOffset);
            throw new PseudoInstructionException(currentLine);
        }
    }

    private static final Pattern EQU_PATTERN = Pattern.compile("([\\w]+)\\s+EQU\\s+(.+)",
                Pattern.CASE_INSENSITIVE);

    /**
     * Check for and handle the EQU instruction
     *
     * @param line        Current line. The method is assuming that comments and labels are removed
     * @param labels      Map of labels. Constants will be added as labels
     * @param currentLine Current line number
     */
    private static void checkForEQUInstruction(String line, HashMap<String, Character> labels,
                                               int currentLine)
            throws AssemblyException {
        /*  the EQU pseudo instruction is equivalent to the #define compiler directive in C/C++
         *  usage: constant_name EQU <immediate_value>
         *  A constant treated the same way as a label.
         */
        line = line.trim();

        Matcher matcher = EQU_PATTERN.matcher(line);

        if (matcher.lookingAt()) {
            //Save value as a label
            TokenParser parser = new TokenParser(matcher.group(2), currentLine, labels);
            char value = (char)parser.parseConstExpression();
            labels.put(matcher.group(1), value);
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
        //Split in lines
        AssemblyResult result = new AssemblyResult(config);
        String[] lines = text.split("\n");

        LogManager.LOGGER.info("Assembly job started: " + lines.length + " lines to parse.");

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        //Pass 1: Get code origin
        getCodeOrigin(lines, result);
        //Pass 2: Save label names and location
        saveLabelNamesAndLocation(lines, result);
        //Pass 3: encode instructions
        encodeInstructions(lines, result, out);


        //If the code contains OffsetOverFlowException(s), don't bother writing the assembled bytes to memory
        boolean writeToMemory = true;
        for (Exception e : result.exceptions) {
            if (e instanceof OffsetOverflowException) {
                writeToMemory = false;
                break;
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

    private void getCodeOrigin(String[] lines, AssemblyResult result) {
        for (int currentLine = 0; currentLine < lines.length; currentLine++) {
            try {
                checkForORGInstruction(lines[currentLine], result, currentLine);

            } catch (PseudoInstructionException e) {
                break; //Origin is set, skip checking the rest

            } catch (AssemblyException e) {
                //Ignore error
            }
        }
    }

    private void saveLabelNamesAndLocation(String[] lines, AssemblyResult result) {
        int currentOffset = 0;
        for (int currentLine = 0; currentLine < lines.length; currentLine++) {
            try {
                checkForLabel(lines[currentLine], result, (char) currentOffset);

                //Increment offset
                currentOffset += parseInstruction(result, lines[currentLine], currentLine, currentOffset, instructionSet).length / 2;

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
    }

    private void encodeInstructions(String[] lines, AssemblyResult result, ByteArrayOutputStream out) {
        int currentOffset = 0;
        for (int currentLine = 0; currentLine < lines.length; currentLine++) {

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

                for (String label : result.labels.keySet()) {
                    if (result.labels.get(label) == result.origin + currentOffset) {
                        result.disassemblyLines.add(String.format("                     %s:", label));
                    }
                }

                //Encode instruction
                byte[] bytes = parseInstruction(result, line, currentLine, result.origin + currentOffset, result.labels, instructionSet);
                result.codeLineMap.put(result.origin + currentOffset, result.disassemblyLines.size() - 1);
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
    }

    /**
     * Parse an instruction and encode it
     *
     * @param line        Line to parse
     * @param currentLine Current line
     * @return The encoded instruction
     */
    private byte[] parseInstruction(AssemblyResult result, String line, int currentLine, int offset, InstructionSet instructionSet) throws AssemblyException {
        return parseInstruction(result, line, currentLine, offset, null, instructionSet, true);
    }

    /**
     * Parse an instruction and encode it
     *
     * @param line        Line to parse
     * @param currentLine Current line
     * @param labels      List of labels
     * @return The encoded instruction
     */
    private byte[] parseInstruction(AssemblyResult result, String line, int currentLine, int offset,
                                    HashMap<String, Character> labels, InstructionSet instructionSet)
            throws AssemblyException {
        return parseInstruction(result, line, currentLine, offset, labels, instructionSet, false);
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
    private byte[] parseInstruction(AssemblyResult result, String line, int currentLine, int offset, HashMap<String, Character> labels,
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
            byte[] bytes;
            if (assumeLabels) bytes = parseDWInstruction(line, currentLine);
            else bytes = parseDWInstruction(line, labels, currentLine);

            if (bytes != null) {
                out.write(bytes);
                return out.toByteArray();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (instructionSet.get(mnemonic) == null) {
            throw new InvalidMnemonicException(mnemonic, currentLine);
        }

        StringBuilder disassembly = new StringBuilder();

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

            if (!assumeLabels) {
                byte[] bytes = out.toByteArray();
                for (int i = 0; i < bytes.length; i += 2) {
                    disassembly.append(String.format("%02X%02X ", bytes[i], bytes[i + 1]));
                }
                result.disassemblyLines.add(String.format(
                        "%04X  %-15s  %s %s, %s", offset, disassembly, mnemonic.toUpperCase(),
                        o1.toString(registerSet), o2.toString(registerSet)
                ));
            }

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

            if (!assumeLabels) {
                byte[] bytes = out.toByteArray();
                for (int i = 0; i < bytes.length; i += 2) {
                    disassembly.append(String.format("%02X%02X ", bytes[i], bytes[i + 1]));
                }
                result.disassemblyLines.add(String.format(
                        "%04X  %-15s  %s %s", offset, disassembly, mnemonic.toUpperCase(), o1.toString(registerSet)
                ));
            }
        } else {
            //No operand

            //Encode instruction
            //Get instruction by name
            instructionSet.get(mnemonic).encode(out, currentLine);

            if (!assumeLabels) {
                byte[] bytes = out.toByteArray();
                for (int i = 0; i < bytes.length; i += 2) {
                    disassembly.append(String.format("%02X%02X ", bytes[i], bytes[i + 1]));
                }
                result.disassemblyLines.add(String.format(
                        "%04X  %-15s  %s", offset, disassembly, mnemonic.toUpperCase()
                ));
            }
        }

        return out.toByteArray();
    }
}
