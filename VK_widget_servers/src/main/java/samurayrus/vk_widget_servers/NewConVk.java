package samurayrus.vk_widget_servers;

import com.google.common.io.Resources;
import com.vk.api.sdk.client.ClientResponse;
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class NewConVk {

    private static Integer groupId; //id группы. Тут все просто
    private static String groupToken; //Токен безопасности для доступа к группе. Получается в настройках группы - api и через свое приложение с запросом bridge или,
    //вроде, как пможно установить дефолтное приложение вк по виджетам и зять его токен.
    private static String URL_adress;
    private static String OfficialServerIp;
    private static Boolean SafeMode = false;
    private static Boolean HardSafeMode = false;
    private static String path = "/GroupLogin.properties";

    public static void setHardSafeMode(Boolean HardSafeMode) {
        NewConVk.HardSafeMode = HardSafeMode;
    }

    public static void setSafeMode(Boolean SafeMode) {
        NewConVk.SafeMode = SafeMode;
    }

    public static Boolean getSafeMode() {
        return SafeMode;
    }

    static {
        newProp();
    }

    public static void newProp() { //Параметры входа из файла
        InputStream inputStream;
        try {
            Properties prop = new Properties();

            String path2 = new File(widget_main.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent();
            File file;
            file = new File(path2 + path);
            System.out.println(path2 + path);

            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                prop.load(fileInputStream);
                groupId = Integer.valueOf(prop.getProperty("GroupId"));
                groupToken = prop.getProperty("GroupToken");
                URL_adress = prop.getProperty("URL");
                OfficialServerIp = prop.getProperty("OfficialServerIp");
            }
        } catch (IOException ex) {
            Logger.getLogger(NewConVk.class.getName()).log(Level.SEVERE, null, ex);
        }//испрвить заглушку
        catch (java.lang.IllegalArgumentException | NullPointerException ex) {
            System.out.println("Properties File Not Found \n");
        }
    }

    public static JSONObject getExample() throws IOException {
        try {
            HttpTransportClient ht = new HttpTransportClient(); //Данные с сервера
            System.out.println("URL: " + URL_adress);

            ClientResponse cr = ht.get(URL_adress);

            System.out.println("Пришел ответ: " + cr.getContent());
            ArrayList<ServerObj> people = DataForTest.getServerData(cr.getContent());
            Collections.sort(people, ServerObj.COMPARE_BY_COUNT); //Сортировка [игроки]/[офф-неофф]

            people.forEach(System.out::println);
            return code(people);
        } catch (NullPointerException ex) {
            System.out.println("Properties File Not Found \n");
            return null;
        }

    }

    public static String newCon() throws IOException {
        TransportClient tC = HttpTransportClient.getInstance();  //Канал с vk
        VkApiClient vkC = new VkApiClient(tC);
        GroupActor arc = new GroupActor(groupId, groupToken);

        System.out.println();
        try {
            JSONObject jo = getExample();
            if (jo == null) {
                return "ClientException";
            }
            return vkC.appWidgets().update(arc, "return " + jo + ";").type("table").executeAsString();  //Запрос вк с выводом ответа
        } catch (ClientException ex) {
            return "ClientException";
        }
    }

    private static JSONObject code(ArrayList<ServerObj> arayContext) //Реализация заполнения запроса для создания таблицы очень такое себе. Не хотел текстом делать, в 8 java неудобно.
    {
        if (arayContext.size() == 0) {  //Вывод, если серверов не будет
            ServerObj ojj = new ServerObj();
            ojj.setIp("ServerList is Empty");
            ojj.setMaxPlayers(0);
            ojj.setName("None");
            ojj.setOnline(0);
            ojj.setPort(0);
            arayContext.add(ojj);
        }

        System.out.println("Начал формировать ответ...");
        JSONObject jo = new JSONObject();
        JSONArray ja = new JSONArray();
        JSONObject jo2 = new JSONObject();
        JSONObject jo3 = new JSONObject();
        JSONObject jj1 = new JSONObject();
        JSONObject jj2 = new JSONObject();
        JSONObject jj3 = new JSONObject();
        int online = 0;
        for (ServerObj obj : arayContext) {
            online += obj.getOnline();
        }
        jo2.put("title", "Общий Онлайн: ");
        jo2.put("title_counter", online);
        //jo2.put("title_url","https://vk.com/aveloli?z=photo-149959198_457274585%2Falbum-149959198_00%2Frev"); ANIME

        jo3.put("text", "IP:PORT ");
        // jj1.put("align", "left");
        ja.add(jo3);

        jj1.put("text", "Сервер");
        jj1.put("align", "center");
        ja.add(jj1);

        jj2.put("text", "Игроки/Слоты");
        jj2.put("align", "center");
        ja.add(jj2);

        jj3.put("text", "Official");
        jj3.put("align", "center");
        ja.add(jj3);

        JSONObject[] jo4 = new JSONObject[arayContext.size()];
        JSONObject[] jo5 = new JSONObject[arayContext.size()];
        JSONObject[] jo55 = new JSONObject[arayContext.size()];
        JSONObject[] jo54 = new JSONObject[arayContext.size()];

        JSONArray[] ja2 = new JSONArray[arayContext.size() + 1];
        JSONArray ja6 = new JSONArray();
        JSONArray ja7 = new JSONArray();

        System.out.println(arayContext.size());

        int length = arayContext.size();
        if (length > 10) {
            length = 10;
            System.out.println("More than 10 servers. Its good!");
        }

        for (int i = 0; i < length; i++) {
            if (SafeMode) {
                if (arayContext.get(i).getOnline() < 2 && arayContext.get(i).getOfficial() == 0) { //Проверка на мусорные сервера
                    continue;
                }
            }
            if (HardSafeMode) {
                if (arayContext.get(i).getOfficial() == 0) { //Проверка на не офф сервера
                    continue;
                }
            }

            jo4[i] = new JSONObject();
            jo5[i] = new JSONObject();
            jo55[i] = new JSONObject();
            jo54[i] = new JSONObject();

            jo4[i].put("text", arayContext.get(i).getIp() + ":" + arayContext.get(i).getPort()); //Имя сервера
            jo4[i].put("icon_id", "club194163484");  //заглушка
            jo4[i].put("url", "https://vk.com/skymp"); //заглушка
            jo5[i].put("text", arayContext.get(i).getName()); //players/slots 

            jo55[i].put("text", arayContext.get(i).getOnline() + "/" + arayContext.get(i).getMaxPlayers()); //ip/port. Временно? 

            if (arayContext.get(i).getOfficial() == 1) {
                jo54[i].put("text", "✅"); //Галочка
            } else {
                jo54[i].put("text", " "); //не галочка 
            }

            ja2[i] = new JSONArray();
            ja2[i].add(jo4[i]);
            ja2[i].add(jo5[i]);
            ja2[i].add(jo55[i]);
            ja2[i].add(jo54[i]);
            System.out.println(ja2[i].toJSONString());
            ja7.add(ja2[i]);
        }

        jo2.put("head", ja);
        jo2.put("body", ja7);
        
        return jo2;
    }

    public static String getOfficialServerIp() {
        return OfficialServerIp;
    }
}
