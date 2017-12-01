package net.simon987.server;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import net.simon987.server.logging.LogManager;

public class ZipUtils {

	private static final int BUFFER_SIZE = 1024;

	public static byte[] bytifyFile(String fileName) {

		Path path = Paths.get(fileName);
		byte[] bytes = null;

		try {
			bytes = Files.readAllBytes(path);

		} catch (IOException e) {
			System.out.println("Failed to extract bytes from: " + fileName);
			e.printStackTrace();
		}

		return bytes;
	}
	
	public static String getByteArrAsString(byte[] bytes) throws UnsupportedEncodingException {
		return new String(bytes, "UTF-8");
	}

	public static void writeSavesToZip(ArrayList<byte[]> array) throws IOException {
		
		int writeCount = 0;
		FileOutputStream output = new FileOutputStream("archive_" + getDateTimeStamp() + ".zip");
		ZipOutputStream stream = new ZipOutputStream(output);
		byte[] buffer = new byte[BUFFER_SIZE];
		ByteArrayInputStream bais = new ByteArrayInputStream(buffer);

		while ((bais.read(buffer)) > -1) {
			for (int i = 0; i < array.size(); i++) {

				ZipEntry entry = new ZipEntry("save_" + getTickTime(array.get(i)) + ".json");
				stream.putNextEntry(entry);
				stream.write(array.get(i));
				stream.closeEntry();
				writeCount++;
			}
		}

		stream.close();
		output.close();
		
		LogManager.LOGGER.info(writeCount + " saves moved to zip file archive");
	}

	private static String getTickTime(byte[] bytes) throws UnsupportedEncodingException {

		Pattern pattern = Pattern.compile("\"time\"");
		String stringedBytes = getByteArrAsString(bytes);
		Matcher matcher = pattern.matcher(stringedBytes);
		int startIndex = 0;
		
		while (matcher.find()) {
			startIndex = matcher.end() + 1;
		}
		
		int endIndex = stringedBytes.indexOf(",", startIndex);
		
		return stringedBytes.substring(startIndex, endIndex);
	}
	
	private static String getDateTimeStamp() {
		Date millisToDate = new Date(System.currentTimeMillis());
		SimpleDateFormat f = new SimpleDateFormat("yyyyMMddHHmmss");    
		return f.format(millisToDate);
	}

}
