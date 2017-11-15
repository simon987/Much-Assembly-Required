package net.simon987.server.assembly;

import net.simon987.server.ServerConfiguration;
import net.simon987.server.assembly.exception.AssemblyException;
import net.simon987.server.assembly.exception.DuplicateSegmentException;
import net.simon987.server.logging.LogManager;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Result of an assembly attempt
 */
public class AssemblyResult {


    /**
     * The origin of the program, default is 0x400
     */
    public int origin;
    /**
     * A list of labels
     */
    HashMap<String, Character> labels = new HashMap<>(20);
    /**
     * List of exceptions encountered during the assembly attempt,
     * they will be displayed in the editor
     */
    ArrayList<AssemblyException> exceptions = new ArrayList<>(50);
    /**
     * Offset of the code segment
     */
    public int codeSegmentOffset;
    /**
     * Line of the code segment definition (for editor icons)
     */
    private int codeSegmentLine;

    /**
     * The encoded user code (will be incomplete or invalid if the
     * assembler encountered an error during assembly)
     */
    public byte[] bytes;
    /**
     * Offset of the data segment, default is 0x4000
     */
    private int dataSegmentOffset;
    /**
     * Line of the data segment definition (for editor icons)
     */
    private int dataSegmentLine;
    /**
     * Whether or not the code segment is set
     */
    private boolean codeSegmentSet = false;
    /**
     * Whether or not the data segment is set
     */
    private boolean dataSegmentSet = false;

    AssemblyResult(ServerConfiguration config) {
        origin = config.getInt("org_offset");
    }

    /**
     * Define a segment.
     *
     * @param segment       Segment to define
     * @param currentOffset Current offset, in bytes of the segment
     * @param currentLine   Line number of the segment declaration
     * @throws DuplicateSegmentException when a segment is defined twice
     */
    void defineSegment(Segment segment, int currentLine, int currentOffset) throws DuplicateSegmentException {

        if (segment == Segment.TEXT) {
            //Code segment

            if (!codeSegmentSet) {
                codeSegmentOffset = origin + currentOffset;
                codeSegmentLine = currentLine;

                LogManager.LOGGER.fine("DEBUG: .text offset @" + codeSegmentOffset);


                codeSegmentSet = true;
            } else {
                throw new DuplicateSegmentException(currentLine);
            }

        } else {
            //Data segment
            if (!dataSegmentSet) {
                dataSegmentOffset = origin + currentOffset;
                dataSegmentLine = currentLine;

                LogManager.LOGGER.fine("DEBUG: .data offset @" + dataSegmentOffset);

                dataSegmentSet = true;
            } else {
                throw new DuplicateSegmentException(currentLine);
            }

        }

    }

}
