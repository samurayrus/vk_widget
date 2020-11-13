package samurayrus.vk_widget_servers;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import java.io.IOException;
/**
 * @author SamurayRus
 */ // me

//@Resources("WidgetJ")
public class widget_main {
    public static void main(String[] args) throws ApiException, ClientException, IOException {
        ConsoleCom cO = new ConsoleCom();
        cO.start();
    }
}
