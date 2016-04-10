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
        SimpleDateFormat logTime = new SimpleDateFormat("HH:mm:ss");
        Calendar cal = new GregorianCalendar();
        cal.setTimeInMillis(record.getMillis());
        
        if (infoDensity == 0)
            return record.getMessage() + "\n";
        
        return logTime.format(cal.getTime())
                /*+ " || "
                            + record.getSourceClassName().substring(
                                    record.getSourceClassName().lastIndexOf(".")+1,
                                    record.getSourceClassName().length())
                            + "."
                            + record.getSourceMethodName()
                            + "() : "*/
                + " [" + record.getLevel() + "] "
                + record.getMessage() + "\n";
    }

}
