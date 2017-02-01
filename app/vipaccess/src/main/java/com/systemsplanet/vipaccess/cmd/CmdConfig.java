package com.systemsplanet.vipaccess.cmd;

import java.util.Arrays;
import java.util.Iterator;
import com.systemsplanet.vipaccess.VipConst;
import com.systemsplanet.vipaccess.log.Log;

public class CmdConfig {
    private static final Log    LOG         = new Log();

    private static final String NAME        = CmdConfig.class.getSimpleName();

    private static final String helpMessage = "-debug <true|false>  -silent <true|false>  -mode <desktop|mobile>   -proxyHost <hostname>  -proxyPort <port #>";

    private static final String helpExample = "-debug true    -silent false    -mode desktop   -proxyHost localhost   -proxyPort 8888  ";

    public String               mode        = VipConst.MODEL_MOBILE;

    public String               proxyHost   = null;

    public String               proxyPort   = null;

    public String               arguments   = "";

    public CmdConfig() {
    }

    public CmdConfig init(String[] args) throws Exception {
        arguments = String.join(" ", args);
        int min = args == null ? 0 : args.length;
        if (min > 0) {
            if (args == null || args.length < min) throw new Exception("Expecting at least [" + min + "] command arguments. Found [" + args.length + "] arguments");
        }
        Iterator<String> argIterator = Arrays.asList(args).iterator();
        while (argIterator.hasNext()) {
            String arg = argIterator.next();
            handleArg(arg, argIterator);
        }
        return this;
    }

    private void handleArg(String arg, Iterator<String> i) {
        String[] match = null;
        CmdLineArgs cla = new CmdLineArgs();
        try {
            match = cla.parse(arg, i, "proxyHost", true);
            if (match[0] != null) {
                proxyHost = match[1];
                proxyHost = proxyHost != null && proxyHost.trim().length() > 2 ? proxyHost : "localhost";
                System.setProperty("https.proxyHost", proxyHost);
                LOG.info("proxyHost:[" + proxyHost + "]");
            }
            match = cla.parse(arg, i, "proxyPort", true);
            if (match[0] != null) {
                proxyPort = match[1];
                proxyPort = proxyPort != null && proxyPort.trim().length() > 2 ? proxyPort : "8888";
                System.setProperty("https.proxyPort", proxyPort);
                LOG.info("proxyPort:[" + proxyPort + "]");
            }

            match = cla.parse(arg, i, "mode", true);
            if (match[0] != null) {
                mode = "destop".equalsIgnoreCase(match[1]) ? VipConst.MODEL_DESKTOP : VipConst.MODEL_MOBILE;
                LOG.info("mode:[" + mode + "]");
            }

            match = cla.parse(arg, i, "debug", true);
            if (match[0] != null) {
                Log.DEBUG = Boolean.parseBoolean(match[1]);
                LOG.info("debug enabled from commandline");
            }

            match = cla.parse(arg, i, "silent", true);
            if (match[0] != null) Log.SILENT = Boolean.parseBoolean(match[1]);

            cla.tryHelp(arg, i, NAME, helpMessage, helpExample);
        }
        catch (Exception e) {
            String msg = NAME + " Error arg:[" + Arrays.toString(match) + "]\n" + helpMessage + helpExample;
            LOG.warn(msg);
        }
    }

    @Override
    public String toString() {
        return "arguments:[" + arguments + "] isSilent:" + Log.SILENT + " isDebug:" + Log.DEBUG + " mode:" + mode;
    }
}
