package samurayrus.vk_widget_servers.log;


import java.time.LocalDateTime;

public class Logger {
    private static final LoggerWriter loggerWirter = new LoggerWriter(null, true);

    private static String getDefaultLogMessage() {
        return System.lineSeparator() + LocalDateTime.now() + " - [%s] - %s";
    }

    public static void logUser(final String log) {
        loggerWirter.writeLog(String.format(getDefaultLogMessage(), LogEnum.USER, log));
    }

    public static void logUserError(final String log) {
        loggerWirter.writeLog(String.format(getDefaultLogMessage(), LogEnum.USER_ERROR, log));
    }

    public static void logInfo(final String log) {
        loggerWirter.writeLog(String.format(getDefaultLogMessage(), LogEnum.INFO, log));
    }

    public static void logWarn(final String log) {
        loggerWirter.writeLog(String.format(getDefaultLogMessage(), LogEnum.WARN, log));
    }

    public static void logError(final String log, final Exception exception) {
        loggerWirter.writeLogWithExceptionTrace(
                String.format(getDefaultLogMessage(), LogEnum.ERROR, log),
                exception
        );
    }
}
