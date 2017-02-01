package com.systemsplanet.vipaccess.log;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {
    public static boolean DEBUG  = false;

    public static boolean SILENT = false;

    public Log() {
    }


    public String now() {
        DateFormat fmt = new SimpleDateFormat("HH:mm:ss.SSS");
        return fmt.format(new Date().getTime());
    }

    public void info(Object... args) {
        if (SILENT) return;
        log("INFO", args);
    }

    public void debug(Object... args) {
        if (SILENT) return;
        if (DEBUG) {
            log("DEBUG", args);
        }
    }

    public void warn(Object... args) {
        log("WARN", args);
    }

    public void error(Object... args) {
        log("ERROR", args);
    }

    public void fatal(Object... args) {
        log("FATAL", args);
        System.out.flush();
        System.exit(1);
    }

    private void log(String level, Object... a) {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        StackTraceElement frame = stack[3];
        StringBuilder msg = new StringBuilder();
        left(msg, now(), 0);
        left(msg, level, 5);
        left(msg, frame.getClassName(), 50);
        left(msg, frame.getMethodName(), 15);
        if (a != null) {
            int iMax = a.length - 1;
            if (iMax != -1) for (int i = 0;; i++) {
                msg.append(String.valueOf(a[i]));
                if (i == iMax) break;
                msg.append(" ");
            }
        }
        System.out.println(msg.toString());
    }

    private void left(StringBuilder sb, String s, int len) {
        if (s==null) s="";
        sb.append(s);
        len = len - s.length();
        for (int i = 0; i < len; i++) {
            sb.append(" ");
        }
        sb.append(" ");
    }

    public static boolean isDEBUG() {
        return DEBUG;
    }

    public static void setDEBUG(boolean dEBUG) {
        DEBUG = dEBUG;
    }

    public static boolean isSILENT() {
        return SILENT;
    }

    public static void setSILENT(boolean sILENT) {
        SILENT = sILENT;
    }
}
