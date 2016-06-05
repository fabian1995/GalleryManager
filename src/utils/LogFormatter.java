/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 *
 * @author fabian
 */
public class LogFormatter extends Formatter {
    
    public static final int MESSAGES_ONLY = 0;
    public static final int MESSAGES_AND_TIME = 1;

    private final int infoDensity;
    
    public LogFormatter (int infoDensity) {
        this.infoDensity = infoDensity;
    }
    
    @Override
    public String format(LogRecord record) {
        Object[] params = record.getParameters();
        String message = record.getMessage();
        
        for (int i = 0; params != null && i < params.length; i++) {
            Object o = params[i];
            message = message.replace("{" + i + "}", o.toString());
        }
        
        if (infoDensity == MESSAGES_ONLY)
            return message + "\n";
        
        SimpleDateFormat logTime = new SimpleDateFormat("HH:mm:ss");
        Calendar cal = new GregorianCalendar();
        cal.setTimeInMillis(record.getMillis());
        
        return logTime.format(cal.getTime())
                + " [" + record.getLevel() + "] "
                + message + "\n";
    }

}
