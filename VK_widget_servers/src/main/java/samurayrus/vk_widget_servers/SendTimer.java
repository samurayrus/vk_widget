package samurayrus.vk_widget_servers;

import samurayrus.vk_widget_servers.log.LoggerFile;

import java.io.IOException;
import java.util.Date;
import java.util.TimerTask;

/**
 * Класс Таймер. Каждые n секунд вызывает {@link ServerManager#newConnectAndPushInfoInVkApi}
 */
public class SendTimer extends TimerTask {
    @Override
    public void run() {
        try {
            LoggerFile.writeLog("\n TimerTask begin in:" + new Date());
            String answer = completeTask();
            LoggerFile.writeLog("\n TimerTask end and return: " + answer + "\n");
        } catch (IOException ex) {
            LoggerFile.writeLog(SendTimer.class.getName() + " IOException " + ex.getMessage());
        }
    }

    private String completeTask() throws IOException {
        return ServerManager.newConnectAndPushInfoInVkApi();
    }
}
