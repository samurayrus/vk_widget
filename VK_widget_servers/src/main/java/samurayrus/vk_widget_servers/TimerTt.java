package samurayrus.vk_widget_servers;

import java.io.IOException;
import java.util.Date;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TimerTt extends TimerTask{
        @Override
    public void run() {
        System.out.println("TimerTask начал свое выполнение в:" + new Date());
            try {
                System.out.println("TimerTask закончил и вернул: " + completeTask());
            } catch (IOException ex) {
                Logger.getLogger(TimerTt.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
 
    private String completeTask() throws IOException {
        return NewConVk.newCon();
    }
}
