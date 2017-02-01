package com.systemsplanet.vipaccess.cmd;

import java.util.Arrays;
import java.util.Iterator;
import com.systemsplanet.vipaccess.log.Log;

public class CmdLineArgs {
    Log log = new Log();

    public String[] parse(String arg, Iterator<String> argIterator, String argSwitch, boolean hasParam) throws Exception {
        if (arg == null) throw new IllegalArgumentException("parse arg null");
        if (argIterator == null) throw new IllegalArgumentException("parse argIterator null");
        if (argSwitch == null) throw new IllegalArgumentException("parse tryArg null");
        String found = ("-" + argSwitch).equalsIgnoreCase(arg) ? "Y" : null;
        String val = "";
        if ("Y".equalsIgnoreCase(found)) {
            String msg = "           [" + arg + "]";
            if (hasParam) {
                val = argIterator.next();
                msg += " [" + val + "]";
            }
            if ("help".equalsIgnoreCase(argSwitch) == false) log.debug(msg);
        }
        return new String[] { found, val };
    }

    public String dump(String[] args) {
        return Arrays.toString(args);
    }

    public void tryHelp(String arg, Iterator<String> argIterator, String className, String helpMessage, String helpExample) throws Exception {
        Log log = new Log();
        CmdLineArgs cla = new CmdLineArgs();
        String[] match = cla.parse(arg, argIterator, "help", false);
        if (match[0] != null) {
            log.info(String.format("      %-35s%s", className, ""));
            log.info(String.format("      %-35s%s", "   Options:", helpMessage));
            log.info(String.format("      %-35s%s", "   Example:", helpExample));
        }
    }

}
