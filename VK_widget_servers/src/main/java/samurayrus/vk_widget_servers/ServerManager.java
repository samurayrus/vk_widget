package samurayrus.vk_widget_servers;

import com.vk.api.sdk.client.ClientResponse;
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.appwidgets.UpdateType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import samurayrus.vk_widget_servers.log.Logger;
import samurayrus.vk_widget_servers.vk.VkMessage;
import samurayrus.vk_widget_servers.vk.VkMessageBody;
import samurayrus.vk_widget_servers.vk.VkMessageHead;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ServerManager проводит полную работу с соединениями и формированием ответов
 *
 * @author SamurayRus
 */

public class ServerManager {
    private static final Map<String, String> blackList = new HashMap<>();
    private static final JsonMapper jsonMapper = new JsonMapper();
    private static int groupId; //id группы
    @Getter(AccessLevel.NONE)
    private static String groupToken; //Токен безопасности для доступа к группе. Получается в настройках группы - api и через свое приложение с запросом bridge или,
    //вроде, как можно установить дефолтное приложение вк по виджетам и взять его токен.
    private static String URL_address;
    @Getter
    private static String officialServerIp;
    @Setter
    @Getter
    private static boolean safeMode = false;
    @Setter
    @Getter
    private static boolean hardSafeMode = false;
    @Setter
    @Getter
    private static boolean showLocal = false;

    static {
        setPropertyForWidget();
        blackList.put("25.25.25.25", "test (:");
    }

    public static String addBlackListValue(final String ip, final String text) {
        Logger.logInfo("addBlackListValue:" + ip + " " + text);
        blackList.put(ip, text);
        return "Ok! BlackList Size now: " + blackList.size();
    }

    public static String deleteBlackListValue(final String ip) {
        if (blackList.containsKey(ip)) {
            blackList.remove(ip);
            Logger.logInfo("deleteBlackListValue:" + ip);
            return "OK! BlackList Size now: " + blackList.size();
        } else {
            return "Not Found this ip. BlackList Size now: " + blackList.size();
        }
    }

    public static String showBlackList() {
        return blackList.toString();
    }

    /**
     * Пропинговка серверов, чтобы не выводть локальные. Эту проверку можно отключить (showLocal = true)
     */
    public static boolean pingThisIp(final String ip) {

        Logger.logInfo("Ping to: " + ip);
        //Проверка на локальный адрес
        if (!showLocal) {
            String[] ipArray = ip.split("\\.");
            if (ipArray[0].equals("127")) {
                Logger.logInfo("Local Ip: dont Ping this!");
                return false;
            }
            //Пропинговка
            try {
                InetAddress inetAddress = InetAddress.getByName(ip);
                return inetAddress.isReachable(500);
            } catch (UnknownHostException ex) {
                Logger.logInfo("UnknownHostException " + ex.getMessage());
                return false;
            } catch (IOException ex) {
                Logger.logInfo("IOException " + ex.getMessage());
                return false;
            }
        }
        return true;
    }

    /**
     * Загрузка Properties для виджета со всеми данными.
     */
    public static void setPropertyForWidget() {
        try {
            Properties properties = new Properties();

            String pathToProperty = new File(WidgetApp.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent();
            String propertyName = "/GroupLogin.properties";
            File propertyFile = new File(pathToProperty + propertyName);
            Logger.logInfo("pathToProperty: " + pathToProperty + propertyName);

            try (FileInputStream fileInputStream = new FileInputStream(propertyFile)) {
                properties.load(fileInputStream);
                groupId = Integer.parseInt(properties.getProperty("GroupId"));
                groupToken = properties.getProperty("GroupToken");
                URL_address = properties.getProperty("URL");
                officialServerIp = properties.getProperty("OfficialServerIp");
            }
        } catch (IOException ioException) {
            Logger.logInfo("ioException" + ioException.getMessage() + System.lineSeparator());
        } catch (java.lang.IllegalArgumentException | NullPointerException ex) {
            Logger.logInfo(" Properties File Not Found " + System.lineSeparator());
        }
    }

    /**
     * Создание нового подключения к SkyMpIo и получение текущих серверов. Возвращает то, что вернет вызов {@link ServerManager#vkMessageCreatorWithFilters(List< ServerInfoDto )} ()}
     */
    public static VkMessage loadServersInfoFromSkympApi() throws IOException {
        try {
            HttpTransportClient httpTransportClient = new HttpTransportClient();
            ClientResponse clientResponse = httpTransportClient.get(URL_address);

            Logger.logInfo(" URL: " + URL_address + System.lineSeparator() + "New Content: " + clientResponse.getContent());

            List<ServerInfoDto> serverInfoDtos = jsonMapper.mapJsonToServerObjects(clientResponse.getContent());

            if (serverInfoDtos == null || serverInfoDtos.isEmpty()) {
                Logger.logWarn(System.lineSeparator() + " loadServersInfoFromSkympApi - no content");
                return null;
            }
            //Сортировка [игроки]/[офф-неофф]
            Collections.sort(serverInfoDtos, ServerInfoDto.COMPARE_BY_COUNT);
//            serverInfoDtos.forEach(System.out::println);
            return vkMessageCreatorWithFilters(serverInfoDtos);
        } catch (NullPointerException ex) {
            Logger.logError("loadServersInfoFromSkympApi() NullPointerException exception: " + ex);
            return null;
        }

    }

    /**
     * Создание нового подключения к VkApi, посылка того, что вернет вызов {@link ServerManager#loadServersInfoFromSkympApi}
     * Вызывается по таймеру в {@link ServerManagerTimer}
     */
    public static String newConnectAndPushInfoInVkApi() throws IOException {
        TransportClient transportClient = HttpTransportClient.getInstance();
        VkApiClient vkApiClient = new VkApiClient(transportClient);
        GroupActor groupActor = new GroupActor(groupId, groupToken);

        try {
            VkMessage vkMessage = loadServersInfoFromSkympApi();
            if (vkMessage == null) {
                throw new ClientException("VkMessage is null");
            }
            //Запрос вк с выводом ответа
            return vkApiClient.appWidgets().update(groupActor, "return " + jsonMapper.mapVkMessageToJson(vkMessage) + ";", UpdateType.TABLE).executeAsString();
        } catch (ClientException ex) {
            Logger.logError("ClientException - " + ex);
            return "ClientException: " + ex.getMessage();
        }
    }

    /**
     * Формирует сообщение для VkApi со списком серверов для вывода с учетом заданных условий (показ локальных серверов, hard safe mode, safe mode и тд)
     */
    private static VkMessage vkMessageCreatorWithFilters(List<ServerInfoDto> listServerInfoDto) {
        Logger.logInfo("Begin creating answer...");
        //Строка для отображения, если серверов не будет
        if (listServerInfoDto.size() == 0) {
            ServerInfoDto serverInfoDtoNullInfo = new ServerInfoDto();
            serverInfoDtoNullInfo.setIp("ServerList is Empty");
            serverInfoDtoNullInfo.setMaxPlayers(0);
            serverInfoDtoNullInfo.setName("None");
            serverInfoDtoNullInfo.setOnline(0);
            serverInfoDtoNullInfo.setPort(0);
            listServerInfoDto.add(serverInfoDtoNullInfo);
        }

        int online = 0;
        for (ServerInfoDto obj : listServerInfoDto) {
            if (obj.getOnline() > 0)
                online += obj.getOnline();
        }
        VkMessageHead[] vkMessageHeads = new VkMessageHead[4];
        vkMessageHeads[0] = VkMessageHead.builder().text("IP:PORT ").build();
        vkMessageHeads[1] = VkMessageHead.builder().text("Сервер").align("center").build();
        vkMessageHeads[2] = VkMessageHead.builder().text("Игроки/Слоты").align("center").build();
        vkMessageHeads[3] = VkMessageHead.builder().text("Official").align("center").build();

        Logger.logInfo("Servers value: " + listServerInfoDto.size());

        //TODO: добавить поддержку большого кол-во серверов. На пропинговку всех много времени уйдет,
        // т.ч нужно пинговать только первые, а если они локальные, то добавлять новые
        listServerInfoDto = listServerInfoDto.stream()
                //Сервера в черном списке
                .filter(x -> !blackList.containsKey(x.getIp()))
                //Отображение только оффициальных серверов
                .filter(x -> hardSafeMode ? x.getOfficial() == 1 : true)
                //Отображение мусорных серверов
                .filter(x -> safeMode ? x.getOnline() > 2 || x.getOfficial() == 1 : true)
                //Пропинговка серверов (исключение локальных)
                .filter(x -> showLocal ? true : x.getOfficial() == 1 || pingThisIp(x.getIp()))
                .collect(Collectors.toList());

        Logger.logInfo("Servers value after filter: " + listServerInfoDto.size());

        int length = listServerInfoDto.size();
        if (length > 10) {
            length = 10;
            Logger.logInfo("More than 10 servers. Its good!");
        }
        VkMessageBody[][] vkMessageBodies = new VkMessageBody[length][4];
        for (int i = 0; i < length; i++) {
            vkMessageBodies[i][0] = VkMessageBody.builder()
                    .text(listServerInfoDto.get(i).getIp() + ":" + listServerInfoDto.get(i).getPort())
                    .icon_id("club194163484") //заглушка
                    .url("https://vk.com/skymp") //заглушка
                    .build();
            vkMessageBodies[i][1] = VkMessageBody.builder()
                    .text(listServerInfoDto.get(i).getName())
                    .build();
            vkMessageBodies[i][2] = VkMessageBody.builder()
                    .text(listServerInfoDto.get(i).getOnline() + "/" + listServerInfoDto.get(i).getMaxPlayers())
                    .build();
            vkMessageBodies[i][3] = VkMessageBody.builder()
                    .text(listServerInfoDto.get(i).getOfficial() == 1 ? "✅" : " ")
                    .build();
        }

        Logger.logInfo("End creating answer...");
        return VkMessage.builder().title("Общий онлайн: ").title_counter(online).head(vkMessageHeads).body(vkMessageBodies).build();
    }
}
