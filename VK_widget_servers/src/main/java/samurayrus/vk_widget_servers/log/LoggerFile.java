package samurayrus.vk_widget_servers.log;


public class LoggerFile {

    private static final WriteLogger logger = new WriteLogger(null, false);

    public static void writeLog(final String log) {
        System.out.println(log);
        logger.writeAnswer("\n" + System.lineSeparator() + log);
    }
}
