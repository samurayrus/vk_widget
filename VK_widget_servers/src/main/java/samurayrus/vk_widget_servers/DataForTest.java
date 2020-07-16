/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package samurayrus.vk_widget_servers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author maxim
 */
public class DataForTest {
        public static ArrayList<ServerObj> getServerData(String jsn) throws IOException {

            try{
        ObjectMapper objectMapper = new ObjectMapper();

        System.out.println("Начали объектить");
       // InputStream inputStream = Resources.getResource("JsonExample.json").openStream();
        ArrayList<ServerObj> people = objectMapper.readValue(jsn, new TypeReference<List<ServerObj>>() {
        });   //Берет строки из json файла и пихает их в objectMapper, который на их основе генерит objects (ServerObj).
        System.out.println("Закончили");
            return people;
            } catch(java.lang.IllegalArgumentException ex) {
                System.out.println("File Not Found \n For test");
                return null;
            }
            
}
}
