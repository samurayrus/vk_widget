package samurayrus.vk_widget_servers;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import java.io.IOException;
/**
 * При использовании кода этого проекта, укажите того, кто вам сэкономил нехило так времени (меня)
 * @author SamurayRus
 */
// me
public class Widget_main {
    public static void main(String[] args) throws ApiException, ClientException, IOException {
        ConsoleCommandLine consoleCommandLine = new ConsoleCommandLine();
        consoleCommandLine.start();
    }
}
