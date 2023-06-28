package samurayrus.vk_widget_servers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import samurayrus.vk_widget_servers.log.LoggerFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class JsonParser {

    /**
     * Маппит входящий String со SkyMpIp в список объектов серверов {@link ServerObj}.
     */
    public static ArrayList<ServerObj> getServerData(String jsonResponse) throws IOException {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            if (jsonResponse == null) {
                jsonResponse = "[]";
            }
            LoggerFile.writeLog("ObjectMapper begin");

            // InputStream inputStream = Resources.getResource("JsonExample.json").openStream();
            // Берет строки из json файла и пихает их в objectMapper, который на их основе генерит objects (ServerObj).

            ArrayList<ServerObj> people = objectMapper.readValue(jsonResponse, new TypeReference<List<ServerObj>>() {
            });

            LoggerFile.writeLog("ObjectMapper end");
            return people;
        } catch (java.lang.IllegalArgumentException ex) {
            LoggerFile.writeLog("File Not Found \n For test");
            return null;
        }

    }
}
