/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.io.OutputStream;
import java.util.logging.ConsoleHandler;

/**
 *
 * @author fabian
 * Found on http://stackoverflow.com/questions/194165/how-do-i-change-java-logging-console-output-from-std-err-to-std-out
 */
public class StdoutConsoleHandler extends ConsoleHandler {

    @Override
    protected void setOutputStream(OutputStream out) throws SecurityException {
        super.setOutputStream(System.out);
    }
}
