package samurayrus.vk_widget_servers;

import samurayrus.vk_widget_servers.log.Logger;

import java.io.IOException;
import java.util.TimerTask;

/**
 * Класс Таймер. Каждые n секунд вызывает {@link ServerManager#newConnectAndPushInfoInVkApi}
 */
public class ServerManagerTimer extends TimerTask {
    @Override
    public void run() {
        try {
            Logger.logInfo("TimerTask begin");
            String answer = ServerManager.newConnectAndPushInfoInVkApi();
            if (answer.equals("{\"response\":1}"))
                Logger.logInfo("TimerTask end and return: " + answer);
            else
                Logger.logError("VkApi answer is not success! Returned answer: " + answer);
        } catch (IOException ex) {
            Logger.logError(ServerManagerTimer.class.getName() + " IOException \n" + ex);
        }
    }
}
