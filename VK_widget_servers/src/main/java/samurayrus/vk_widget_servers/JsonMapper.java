package samurayrus.vk_widget_servers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import samurayrus.vk_widget_servers.log.Logger;
import samurayrus.vk_widget_servers.vk.VkMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class JsonMapper {
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Маппит входящий String со SkyMpIp в список объектов серверов {@link ServerInfoDto}.
     */
    public List<ServerInfoDto> mapJsonToServerObjects(final String jsonResponse) throws IOException {
        if (jsonResponse == null) {
            Logger.logInfo("ObjectMapper end with empty result");
            return new ArrayList<>();
        }

        // InputStream inputStream = Resources.getResource("JsonExampleNew.json").openStream();
        // Берет строки из json файла и пихает их в objectMapper, который на их основе генерит objects (ServerObj).

        List<ServerInfoDto> serverInfoDtos = objectMapper.readValue(jsonResponse, new TypeReference<List<ServerInfoDto>>() {
        });

        Logger.logInfo("ObjectMapper end and map List with size: " + serverInfoDtos.size());
        return serverInfoDtos;
    }

    public String mapVkMessageToJson(final VkMessage vkMessage) throws IOException {
        String jsonRequestForVkApiWithServers = objectMapper.writeValueAsString(vkMessage);
        Logger.logInfo("ObjectMapper-write result: " + System.lineSeparator() + jsonRequestForVkApiWithServers);
        return jsonRequestForVkApiWithServers;
    }
}
