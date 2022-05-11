package samurayrus.vk_widget_servers;

import com.vk.api.sdk.client.ClientResponse;
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Properties;

import com.vk.api.sdk.objects.appwidgets.UpdateType;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import samurayrus.vk_widget_servers.log.LoggerFile;

/**
 * ServerManager проводит полную работу с соединениями и формированием ответов
 * @author SamurayRus
 * */
public class ServerManager {

    private static Integer groupId; //id группы. Тут все просто
    private static String groupToken; //Токен безопасности для доступа к группе. Получается в настройках группы - api и через свое приложение с запросом bridge или,
    //вроде, как можно установить дефолтное приложение вк по виджетам и зять его токен.
    private static String URL_adress;
    private static String OfficialServerIp;
    private static Boolean SafeMode = false;
    private static Boolean HardSafeMode = false;
    private static String path = "/GroupLogin.properties";
    public static boolean showLocal = false;

    private static HashMap<String, String> blackList = new HashMap();

    public static String addBlackListValue(String ip, String text) {
        LoggerFile.writeLog("addBlackListValue:" + ip + " " + text);
        blackList.put(ip, text);
        return "Ok! BlackList Size now: " + blackList.size();
    }

    public static String deleteBlackListValue(String ip) {
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

    public static void setHardSafeMode(Boolean HardSafeMode) {
        ServerManager.HardSafeMode = HardSafeMode;
    }

    public static void setSafeMode(Boolean SafeMode) {
        ServerManager.SafeMode = SafeMode;
    }

    public static Boolean getSafeMode() {
        return SafeMode;
    }

    static {
        setPropertyForWidget();
        blackList.put("25.25.25.25", "test (:");
    }

    /**
     * Пропинговка серверов, чтобы не выводть локальные. Эту проверку можно отключить
     */
    public static boolean pingThisIp(String ip) {
        LoggerFile.writeLog("Ping to: " + ip);
        //Проверка на локальный адрес
        if (!showLocal) {
            String[] cas = ip.split("\\.");
            if (cas[0].equals("127")) {
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
    public static void setPropertyForWidget() { //Параметры входа из файла
        try {
            Properties properties = new Properties();

            String pathToProperty = new File(Widget_main.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent();
            File propertyFile = new File(pathToProperty + path);
            LoggerFile.writeLog("pathToProperty: " + pathToProperty + " / " + path);

            try (FileInputStream fileInputStream = new FileInputStream(propertyFile)) {
                properties.load(fileInputStream);
                groupId = Integer.valueOf(properties.getProperty("GroupId"));
                groupToken = properties.getProperty("GroupToken");
                URL_adress = properties.getProperty("URL");
                OfficialServerIp = properties.getProperty("OfficialServerIp");
            }
        } catch (IOException ioException) {
            LoggerFile.writeLog("ioException" + ioException.getMessage() + System.lineSeparator());
        } catch (java.lang.IllegalArgumentException | NullPointerException ex) {
            LoggerFile.writeLog(" Properties File Not Found " + System.lineSeparator());
        }
    }

    /**
     * Создание нового подключения к SkyMpIo и получение текущих серверов. Возвращает то, что вернет вызов {@link ServerManager#jsonMapperAnswer} ()}
     */
    public static JSONObject loadServersInfoFromSkympApi() throws IOException {
        try {
            HttpTransportClient httpTransportClient = new HttpTransportClient(); //Данные с сервера
            ClientResponse clientResponse = httpTransportClient.get(URL_adress);

            LoggerFile.writeLog(" URL: " + URL_adress + System.lineSeparator() + "New Content: " + clientResponse.getContent());

            ArrayList<ServerObj> serverObjs = JsonParser.getServerData(clientResponse.getContent());
            Collections.sort(serverObjs, ServerObj.COMPARE_BY_COUNT); //Сортировка [игроки]/[офф-неофф]

            serverObjs.forEach(System.out::println);
            return jsonMapperAnswer(serverObjs);
        } catch (NullPointerException ex) {
            LoggerFile.writeLog(System.lineSeparator() + " Properties File Not Found " + System.lineSeparator());
            return null;
        }

    }

    /**
     * Создание нового подключения к VkApi, посылка того, что вернет вызов {@link ServerManager#loadServersInfoFromSkympApi}
     * Вызывается по таймеру в {@link SendTimer}
     */
    public static String newConnectAndPushInfoInVkApi() throws IOException {
        TransportClient transportClient = HttpTransportClient.getInstance();  //Канал с vk
        VkApiClient vkApiClient = new VkApiClient(transportClient);
        GroupActor groupActor = new GroupActor(groupId, groupToken);

        try {
            JSONObject jsonServersInfoFromSkympApi = loadServersInfoFromSkympApi();
            if (jsonServersInfoFromSkympApi == null) {
                return "ClientException";
            }
            return vkApiClient.appWidgets().update(groupActor, "return " + jsonServersInfoFromSkympApi + ";", UpdateType.TABLE).executeAsString();  //Запрос вк с выводом ответа
        } catch (ClientException ex) {
            return "ClientException";
        }
    }

    //TODO: Переделать составление запроса
    /**
     * Формирует сообщение JSON для VkApi со списком серверов для вывода
     */
    private static JSONObject jsonMapperAnswer(ArrayList<ServerObj> listServerObj)
    {
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

        LoggerFile.writeLog(" Begin creating answer...");

        JSONArray jsonWidgetInfo = new JSONArray();
        JSONObject jsonReqestWidget = new JSONObject();

        JSONObject jsonFirstColumn = new JSONObject();
        JSONObject jsonSecondColumn = new JSONObject();
        JSONObject jsonThirdColumn = new JSONObject();
        JSONObject jsonFourthColumn = new JSONObject();

        int online = 0;
        for (ServerObj obj : listServerObj) {
            if (obj.getOnline() > 0)
                online += obj.getOnline();
        }
        jsonReqestWidget.put("title", "Общий Онлайн: ");
        jsonReqestWidget.put("title_counter", online);
        //jo2.put("title_url","https://vk.com/aveloli?z=photo-149959198_457274585%2Falbum-149959198_00%2Frev"); ANIME

        jsonFirstColumn.put("text", "IP:PORT ");
        // jj1.put("align", "left");
        jsonWidgetInfo.add(jsonFirstColumn);

        jsonSecondColumn.put("text", "Сервер");
        jsonSecondColumn.put("align", "center");
        jsonWidgetInfo.add(jsonSecondColumn);

        jsonThirdColumn.put("text", "Игроки/Слоты");
        jsonThirdColumn.put("align", "center");
        jsonWidgetInfo.add(jsonThirdColumn);

        jsonFourthColumn.put("text", "Official");
        jsonFourthColumn.put("align", "center");
        jsonWidgetInfo.add(jsonFourthColumn);

        JSONObject[] jsonServerBaseInfo = new JSONObject[listServerObj.size()];
        JSONObject[] jsonServerNameInfo = new JSONObject[listServerObj.size()];
        JSONObject[] jsonServerOnlineInfo = new JSONObject[listServerObj.size()];
        JSONObject[] jsonServerOfficialInfo = new JSONObject[listServerObj.size()];

        JSONArray[] jsonServerInfo = new JSONArray[listServerObj.size() + 1];
        JSONArray jsonServersInfo = new JSONArray();

        LoggerFile.writeLog("Servers value: " + listServerObj.size());

        int length = listServerObj.size();
        if (length > 10) {
            length = 10;
            System.out.println("More than 10 servers. Its good!");
        }

        //Проверки на включенные моды начало.
        for (int i = 0; i < length; i++) {

            if (SafeMode) { //Проверка на мусорные сервера
                if (listServerObj.get(i).getOnline() < 2 && listServerObj.get(i).getOfficial() == 0) {
                    if (length < listServerObj.size())
                        length++;
                    continue;
                }
            }
            if (HardSafeMode) { //Проверка на не офф сервера
                if (listServerObj.get(i).getOfficial() == 0) {
                    if (length < listServerObj.size())
                        length++;
                    continue;
                }
            }

            if (blackList.containsKey(listServerObj.get(i).getIp())) { //Проверка на сервера в черном списке
                if (listServerObj.get(i).getOfficial() == 0) {
                    if (length < listServerObj.size())
                        length++;
                    continue;
                }
            }

            if (listServerObj.get(i).getOfficial() == 0)   //Проверка на локальные сервера
                if (!pingThisIp(listServerObj.get(i).getIp())) {
                    LoggerFile.writeLog("Cant ping this" + listServerObj.get(i).toString() + " \n continue;");
                    if (length < listServerObj.size())
                        length++;
                    continue;
                }
            //Проверки на включенные моды конец.

            jsonServerBaseInfo[i] = new JSONObject();
            jsonServerNameInfo[i] = new JSONObject();
            jsonServerOnlineInfo[i] = new JSONObject();
            jsonServerOfficialInfo[i] = new JSONObject();

            jsonServerBaseInfo[i].put("text", listServerObj.get(i).getIp() + ":" + listServerObj.get(i).getPort()); //Имя сервера
            jsonServerBaseInfo[i].put("icon_id", "club194163484");  //заглушка
            jsonServerBaseInfo[i].put("url", "https://vk.com/skymp"); //заглушка
            jsonServerNameInfo[i].put("text", listServerObj.get(i).getName()); //players/slots

            jsonServerOnlineInfo[i].put("text", listServerObj.get(i).getOnline() + "/" + listServerObj.get(i).getMaxPlayers()); //ip/port. Временно?

            if (listServerObj.get(i).getOfficial() == 1) {
                jsonServerOfficialInfo[i].put("text", "✅"); //Галочка
            } else {
                jsonServerOfficialInfo[i].put("text", " "); //не галочка
            }

            jsonServerInfo[i] = new JSONArray();
            jsonServerInfo[i].add(jsonServerBaseInfo[i]);
            jsonServerInfo[i].add(jsonServerNameInfo[i]);
            jsonServerInfo[i].add(jsonServerOnlineInfo[i]);
            jsonServerInfo[i].add(jsonServerOfficialInfo[i]);
            LoggerFile.writeLog(jsonServerInfo[i].toJSONString());
            jsonServersInfo.add(jsonServerInfo[i]);
        }

        jsonReqestWidget.put("head", jsonWidgetInfo);
        jsonReqestWidget.put("body", jsonServersInfo);
        LoggerFile.writeLog(" End creatind answer...");
        return jsonReqestWidget;
    }

    public static String getOfficialServerIp() {
        return OfficialServerIp;
    }
}
