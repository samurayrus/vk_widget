package samurayrus.vk_widget_servers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class DataForTest {

    public static ArrayList<ServerObj> getServerData(String jsn) throws IOException {

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            if (jsn == null) {
                jsn = "[]";
            }
            System.out.println("Начали объектить");

            //
            // InputStream inputStream = Resources.getResource("JsonExample.json").openStream();
            //Берет строки из json файла и пихает их в objectMapper, который на их основе генерит objects (ServerObj).
            //
            ArrayList<ServerObj> people = objectMapper.readValue(jsn, new TypeReference<List<ServerObj>>() {
            });
            System.out.println("Закончили");
            return people;
        } catch (java.lang.IllegalArgumentException ex) {
            System.out.println("File Not Found \n For test");
            return null;
        }

    }
}
