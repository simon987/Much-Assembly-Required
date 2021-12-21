package net.simon987.mar.server.assembly;

import net.simon987.mar.server.IServerConfiguration;
import net.simon987.mar.server.assembly.exception.AssemblyException;
import net.simon987.mar.server.assembly.exception.DuplicateSectionException;
import net.simon987.mar.server.logging.LogManager;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Result of an assembly attempt
 */
public class AssemblyResult {

    /**
     * The origin of the program, default is 0x200
     */
    public int origin;
    /**
     * A list of labels
     */
    public HashMap<String, Character> labels = new HashMap<>(20);
    /**
     * List of exceptions encountered during the assembly attempt,
     * they will be displayed in the editor
     */
    public ArrayList<AssemblyException> exceptions = new ArrayList<>(50);
    /**
     * Offset of the code segment
     */
    private int codeSectionOffset;
    /**
     * Line of the code segment definition
     */
    private int codeSectionLine;

    /**
     * The encoded user code (will be incomplete or invalid if the
     * assembler encountered an error during assembly)
     */
    public byte[] bytes;
    /**
     * Offset of the data segment
     */
    private int dataSectionOffset;
    /**
     * Line of the data segment definition
     */
    private int dataSectionLine;
    /**
     * Whether or not the code segment is set
     */
    private boolean codeSectionSet = false;
    /**
     * Whether or not the data segment is set
     */
    private boolean dataSectionSet = false;

    public final Map<Integer, Integer> codeLineMap;

    public List<String> disassemblyLines;

    AssemblyResult(IServerConfiguration config) {
        origin = config.getInt("org_offset");
        codeLineMap = new HashMap<>();
        disassemblyLines = new ArrayList<>();
    }

    /**
     * Define a section.
     *
     * @param section       Section to define
     * @param currentOffset Current offset, in bytes of the section
     * @param currentLine   Line number of the section declaration
     * @throws DuplicateSectionException when a section is defined twice
     */
    void defineSection(Section section, int currentLine, int currentOffset) throws DuplicateSectionException {

        if (section == Section.TEXT) {
            //Code section

            if (!codeSectionSet) {
                codeSectionOffset = origin + currentOffset;
                codeSectionLine = currentLine;

                LogManager.LOGGER.fine("DEBUG: .text offset @" + codeSectionOffset);


                codeSectionSet = true;
            } else {
                throw new DuplicateSectionException(currentLine);
            }

        } else {
            //Data section
            if (!dataSectionSet) {
                dataSectionOffset = origin + currentOffset;
                dataSectionLine = currentLine;

                LogManager.LOGGER.fine("DEBUG: .data offset @" + dataSectionOffset);

                dataSectionSet = true;
            } else {
                throw new DuplicateSectionException(currentLine);
            }

        }

    }

    public char[] getWords() {

        char[] assembledCode = new char[bytes.length / 2];
        ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN).asCharBuffer().get(assembledCode);

        return assembledCode;
    }

    public int getCodeSectionOffset() {
        if (codeSectionSet) {
            return codeSectionOffset;
        } else {
            return origin;
        }
    }

}