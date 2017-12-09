package net.simon987.server.io;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileUtils {

	private static final int BUFFER_SIZE = 1024;
	private static final String STR_ENCODING = "UTF-8";
	private static final String DATE_FORMAT = "yyyyMMddHHmmss";
	private static final String FILE_TYPE = ".zip";
	private static final Path ROOT_DIR;
	private static final String DIR_NAME = "history";
	public static final Path DIR_PATH;
	
	static {
		ROOT_DIR = Paths.get(".").normalize();
		DIR_PATH = ROOT_DIR.resolve(DIR_NAME);
	}
	
	//Private constructor
	private FileUtils() {
		
	}
	
	/**
	 * Creates a new stamp containing the current date and time
	 * 
	 * @return date and time stamp
	 */
	private static String getDateTimeStamp() {
		Date millisToDate = new Date(System.currentTimeMillis());
		SimpleDateFormat f = new SimpleDateFormat(DATE_FORMAT);
		return f.format(millisToDate);
	}

	/**
	 * Created a directory if none exists with the specified name
	 *
     * @param directory folder to create
     * @return true is the file exists or create operation is successful
	 */
	public static boolean prepDirectory(Path directory) {	
		File file = directory.toFile();
		
		//If the directory exists or the directory created successfully return true
		if(file.exists() || file.mkdir()) {	
			return true;
			
		} else {
		System.out.println("Error creating directory: " + file.toString());
		return false;
		}
	}
	
	/**
	 * Converts a file into an array of bytes
	 *
     * @param path the file to be converted into bytes
     * @return the byte array of the given file
	 */
	public static byte[] bytifyFile(Path path) {
		byte[] bytes = null;

		try {
			bytes = Files.readAllBytes(path);

		} catch (IOException e) {
			System.out.println("Failed to extract bytes from: " + path);
			e.printStackTrace();
		}

		return bytes;
	}

	/**
	 * Takes in a file that had been converted to a byte[] to be written to a new
	 * zip file
	 *
     * @param data
     *            contains data in byte array form to be written, typically a file
	 *            that has been converted with bytifyFile()
	 * @throws IOException
	 *             if an error occurs during the write process
	 */
	public static void writeSaveToZip(String name, byte[] data) throws IOException {

		String newFile = DIR_PATH.resolve(getDateTimeStamp() + FILE_TYPE).toString();
		FileOutputStream output = new FileOutputStream(newFile);
		ZipOutputStream stream = new ZipOutputStream(output);
		byte[] buffer = new byte[BUFFER_SIZE];
		ByteArrayInputStream bais = new ByteArrayInputStream(buffer);

		while ((bais.read(buffer)) > -1) {
			// File name
			ZipEntry entry = new ZipEntry(name);
			// Set to start of next entry in the stream.
			stream.putNextEntry(entry);
			// Data to write.
			stream.write(data);
			// Close the current entry.
			stream.closeEntry();
		}

		stream.close();
		output.close();
	}
	
	public static void cleanHistory(int size) {
		
		
		File[] files = new File(DIR_PATH.toString()).listFiles();
		File[] sorted = new File[size];
		
		File nextSortedFile = null;
		File currentFile = null;
		boolean changed = false;
		
		for(int i = 0; i < files.length / 2; i++) {
			currentFile = files[i];
		    files[i] = files[files.length - i - 1];
		    files[files.length - i - 1] = currentFile;
		}
		
		currentFile = null;
		
		for(int f = 0; f < files.length; f++) {
			changed = false;
			long dirFile = Long.parseLong(files[f].getName().substring(0, (files[f].getName().length() -4)));
			
			if(f < size && sorted[f] == null) {
				sorted[f] = files[f];
				
			} else {
				
				for(int s = 0; s < sorted.length; s++) {
					
					long sortedFile = Long.parseLong(sorted[s].getName().substring(0, (sorted[s].getName().length() -4)));
					
					if(dirFile > sortedFile) {
					
						if(s == sorted.length - 1) {
							sorted[s] = files[f];
						
						} else if(nextSortedFile == null) {
							nextSortedFile = sorted[s];
							sorted[s] = files[f];
						
						} else {
							currentFile = sorted[s];
							sorted[s] = nextSortedFile;
							nextSortedFile = currentFile;		
						}
						
						nextSortedFile = null;
						currentFile = null;
						changed = true;
					}
				}
				
				if(changed == false) {
					files[f].delete();
				}
				
			}
		}

	}
	
	/**
	 * Converts a byte array into human readable format using the provided encoding
	 * 
	 * @param bytes
	 *            data to be encoded to String
	 * @return a String containing the encoded bytes
	 */
	public static String byteArrAsString(byte[] bytes) throws UnsupportedEncodingException {
		return new String(bytes, STR_ENCODING);
	}
}
