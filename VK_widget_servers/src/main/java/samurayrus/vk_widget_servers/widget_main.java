/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package samurayrus.vk_widget_servers;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import java.io.IOException;
/**
 * @author maxim
 */
//@Resources("WidgetJ")
public class widget_main {
    public static int sc=0, sch=5;  //sc - всего серверов / sch -на каком элементе разделение серверов
    public static void main(String[] args) throws ApiException, ClientException, IOException {
        
        //чо нада сделать:
        //Открыть канал. Реализация:
       // String wid = "return {\"title\": \"Цитasdasasdата\",\"text\": \"Текст цитаты\"};";
        ConsoleCom cO = new ConsoleCom();
        cO.start();
        cO.run();
    }

}
