package samurayrus.vk_widget_servers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import samurayrus.vk_widget_servers.log.LoggerFile;
import samurayrus.vk_widget_servers.vk.VkMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class JsonMapper {
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Маппит входящий String со SkyMpIp в список объектов серверов {@link ServerObj}.
     */
    public ArrayList<ServerObj> mapJsonToServerObjects(String jsonResponse) throws IOException {
        LoggerFile.writeLog("ObjectMapper begin");
        if (jsonResponse == null) {
            jsonResponse = "[]";
        }

        // InputStream inputStream = Resources.getResource("JsonExampleNew.json").openStream();
        // Берет строки из json файла и пихает их в objectMapper, который на их основе генерит objects (ServerObj).

        ArrayList<ServerObj> people = objectMapper.readValue(jsonResponse, new TypeReference<List<ServerObj>>() {
        });

        LoggerFile.writeLog("ObjectMapper end");
        return people;
    }

    public String mapVkMessageToJson(final VkMessage vkMessage) throws IOException {
        String jsonRequestForVkApiWithServers = objectMapper.writeValueAsString(vkMessage);
        LoggerFile.writeLog("ObjectMapper-write result: \n" + jsonRequestForVkApiWithServers);
        return jsonRequestForVkApiWithServers;
    }
}
