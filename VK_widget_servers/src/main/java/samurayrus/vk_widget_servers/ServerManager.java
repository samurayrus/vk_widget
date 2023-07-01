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
import samurayrus.vk_widget_servers.log.LoggerFile;
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
    private static final HashMap<String, String> blackList = new HashMap();

    static {
        setPropertyForWidget();
        blackList.put("25.25.25.25", "test (:");
    }

    public static String addBlackListValue(final String ip, final String text) {
        LoggerFile.writeLog("addBlackListValue:" + ip + " " + text);
        blackList.put(ip, text);
        return "Ok! BlackList Size now: " + blackList.size();
    }

    public static String deleteBlackListValue(final String ip) {
        if (blackList.containsKey(ip)) {
            blackList.remove(ip);
            LoggerFile.writeLog("deleteBlackListValue:" + ip);
            return "OK! BlackList Size now: " + blackList.size();
        } else {
            return "Not Found this ip. BlackList Size now: " + blackList.size();
        }
    }

    public static String showBlackList() {
        return blackList.toString();
    }

    /**
     * Пропинговка серверов, чтобы не выводть локальные. Эту проверку можно отключить
     */
    public static boolean pingThisIp(final String ip) {
        LoggerFile.writeLog("Ping to: " + ip);
        //Проверка на локальный адрес
        if (!showLocal) {
            String[] ipArray = ip.split("\\.");
            if (ipArray[0].equals("127")) {
                LoggerFile.writeLog("Local Ip: dont Ping this!");
                return false;
            }
            //Пропинговка
            try {
                InetAddress inetAddress = InetAddress.getByName(ip);
                return inetAddress.isReachable(500);
            } catch (UnknownHostException ex) {
                LoggerFile.writeLog("UnknownHostException " + ex.getMessage());
                return false;
            } catch (IOException ex) {
                LoggerFile.writeLog("IOException " + ex.getMessage());
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
            LoggerFile.writeLog("pathToProperty: " + pathToProperty + " / " + propertyName);

            try (FileInputStream fileInputStream = new FileInputStream(propertyFile)) {
                properties.load(fileInputStream);
                groupId = Integer.parseInt(properties.getProperty("GroupId"));
                groupToken = properties.getProperty("GroupToken");
                URL_address = properties.getProperty("URL");
                officialServerIp = properties.getProperty("OfficialServerIp");
            }
        } catch (IOException ioException) {
            LoggerFile.writeLog("ioException" + ioException.getMessage() + System.lineSeparator());
        } catch (java.lang.IllegalArgumentException | NullPointerException ex) {
            LoggerFile.writeLog(" Properties File Not Found " + System.lineSeparator());
        }
    }

    /**
     * Создание нового подключения к SkyMpIo и получение текущих серверов. Возвращает то, что вернет вызов {@link ServerManager#vkMessageCreatorWithFilters(List<ServerObj)} ()}
     */
    public static VkMessage loadServersInfoFromSkympApi() throws IOException {
        try {
            HttpTransportClient httpTransportClient = new HttpTransportClient();
            ClientResponse clientResponse = httpTransportClient.get(URL_address);

            LoggerFile.writeLog(" URL: " + URL_address + System.lineSeparator() + "New Content: " + clientResponse.getContent());

            ArrayList<ServerObj> serverObjs = JsonParser.getServerData(clientResponse.getContent());

            if (serverObjs == null || serverObjs.isEmpty()) {
                LoggerFile.writeLog(System.lineSeparator() + " loadServersInfoFromSkympApi - no content");
                return null;
            }
            //Сортировка [игроки]/[офф-неофф]
            Collections.sort(serverObjs, ServerObj.COMPARE_BY_COUNT);
            serverObjs.forEach(System.out::println);
            return vkMessageCreatorWithFilters(serverObjs);
        } catch (NullPointerException ex) {
            LoggerFile.writeLog(System.lineSeparator() + "loadServersInfoFromSkympApi() exception: " + ex.getMessage());
            return null;
        }

    }

    /**
     * Создание нового подключения к VkApi, посылка того, что вернет вызов {@link ServerManager#loadServersInfoFromSkympApi}
     * Вызывается по таймеру в {@link SendTimer}
     */
    public static String newConnectAndPushInfoInVkApi() throws IOException {
        TransportClient transportClient = HttpTransportClient.getInstance();
        VkApiClient vkApiClient = new VkApiClient(transportClient);
        GroupActor groupActor = new GroupActor(groupId, groupToken);

        try {
            VkMessage vkMessage = loadServersInfoFromSkympApi();
            if (vkMessage == null) {
                return "ClientException: vkMessage is null";
            }
            //Запрос вк с выводом ответа
            return vkApiClient.appWidgets().update(groupActor, "return " + JsonParser.mapVkMessageToJson(vkMessage) + ";", UpdateType.TABLE).executeAsString();
        } catch (ClientException ex) {
            return "ClientException: " + ex.getMessage();
        }
    }


    /**
     * Формирует сообщение для VkApi со списком серверов для вывода с учетом заданных условий (показ локальных серверов, hard safe mode, safe mode и тд)
     */
    private static VkMessage vkMessageCreatorWithFilters(List<ServerObj> listServerObj) {
        LoggerFile.writeLog(" Begin creating answer...");
        //Строка для отображения, если серверов не будет
        if (listServerObj.size() == 0) {
            ServerObj serverObjNullInfo = new ServerObj();
            serverObjNullInfo.setIp("ServerList is Empty");
            serverObjNullInfo.setMaxPlayers(0);
            serverObjNullInfo.setName("None");
            serverObjNullInfo.setOnline(0);
            serverObjNullInfo.setPort(0);
            listServerObj.add(serverObjNullInfo);
        }

        int online = 0;
        for (ServerObj obj : listServerObj) {
            if (obj.getOnline() > 0)
                online += obj.getOnline();
        }
        VkMessageHead[] vkMessageHeads = new VkMessageHead[4];
        vkMessageHeads[0] = VkMessageHead.builder().text("IP:PORT ").build();
        vkMessageHeads[1] = VkMessageHead.builder().text("Сервер").align("center").build();
        vkMessageHeads[2] = VkMessageHead.builder().text("Игроки/Слоты").align("center").build();
        vkMessageHeads[3] = VkMessageHead.builder().text("Official").align("center").build();

        LoggerFile.writeLog("Servers value: " + listServerObj.size());

        //TODO: добавить поддержку большого кол-во серверов. На пропинговку всех много времени уйдет,
        // т.ч нужно пинговать только первые, а если они локальные, то добавлять новые
        listServerObj = listServerObj.stream()
                //Сервера в черном списке
                .filter(x -> !blackList.containsKey(x.getIp()))
                //Отображение только оффициальных серверов
                .filter(x -> hardSafeMode ? x.getOfficial() == 1 : true)
                //Отображение мусорных серверов
                .filter(x -> safeMode ? x.getOnline() > 2 || x.getOfficial() == 1 : true)
                //Пропинговка серверов (исключение локальных)
                .filter(x -> showLocal ? true : x.getOfficial() == 1 || pingThisIp(x.getIp()))
                .collect(Collectors.toList());

        LoggerFile.writeLog("Servers value after filter: " + listServerObj.size());
        int length = listServerObj.size();
        if (length > 10) {
            length = 10;
            LoggerFile.writeLog("More than 10 servers. Its good!");
        }
        VkMessageBody[][] vkMessageBodies = new VkMessageBody[length][4];
        for (int i = 0; i < length; i++) {
            vkMessageBodies[i][0] = VkMessageBody.builder()
                    .text(listServerObj.get(i).getIp() + ":" + listServerObj.get(i).getPort())
                    .icon_id("club194163484") //заглушка
                    .url("https://vk.com/skymp") //заглушка
                    .build();
            vkMessageBodies[i][1] = VkMessageBody.builder()
                    .text(listServerObj.get(i).getName())
                    .build();
            vkMessageBodies[i][2] = VkMessageBody.builder()
                    .text(listServerObj.get(i).getOnline() + "/" + listServerObj.get(i).getMaxPlayers())
                    .build();
            vkMessageBodies[i][3] = VkMessageBody.builder()
                    .text(listServerObj.get(i).getOfficial() == 1 ? "✅" : " ")
                    .build();
        }

        LoggerFile.writeLog(" End creating answer...");
        return VkMessage.builder().title("Общий онлайн: ").title_counter(online).head(vkMessageHeads).body(vkMessageBodies).build();
    }
}
