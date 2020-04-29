/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package samurayrus.vk_widget_servers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author maxim
 */
public class DataForTest {
        public static ArrayList<ServerObj> getServerData() throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();

        InputStream inputStream = Resources.getResource("JsonExample.json").openStream();
        ArrayList<ServerObj> people = objectMapper.readValue(inputStream, new TypeReference<List<ServerObj>>() {
        });
        
//            ArrayList<ServerObj> people2 = objectMapper.readValue("[\n" +
//"    {\"ServerName\":\"SkyOn_1_OFF\",\"Linq\":\"https://vk.com/skymp\",\"Icon\":\"club194163484\",\"Players\":150,\"Slots\":170,\"Raiting\":9,\"Official\":1}," +
//"    {\"ServerName\":\"SkyOn_2_None\",\"Linq\":\"https://vk.com/skymp\",\"Icon\":\"club90019663\",\"Players\":66,\"Slots\":255,\"Raiting\":5,\"Official\":0}]", new TypeReference<List<ServerObj>>() {
//        });
//            people2.forEach(System.out::println);
     return people;
}
}
