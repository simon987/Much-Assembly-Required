package net.simon987.server.logging;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Generic formatter for the game logging
 */
public class GenericFormatter extends Formatter {

    @Override
    public String format(LogRecord record) {

        StringBuilder sb = new StringBuilder();

        //Regular record
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm:ss:SSS"); //ex. 11/25 22:03:59:010

        sb.append(String.format("[%s] [%s] %s", sdf.format(date), record.getLevel(), record.getMessage()));
        sb.append('\n');


        return sb.toString();
    }
}
