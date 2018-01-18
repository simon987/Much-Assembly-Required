package net.simon987.server.io;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FileUtils {

    private static final String DATE_FORMAT = "yyyyMMddHHmmss";


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

}
