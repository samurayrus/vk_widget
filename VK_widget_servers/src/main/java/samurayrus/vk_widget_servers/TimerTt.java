/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package samurayrus.vk_widget_servers;

import java.io.IOException;
import java.util.Date;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author maxim
 */
public class TimerTt extends TimerTask{
        @Override
    public void run() {
        System.out.println("TimerTask начал свое выполнение в:" + new Date());
        
            try {
                System.out.println("TimerTask закончил и вернул:" + completeTask());
            } catch (IOException ex) {
                Logger.getLogger(TimerTt.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
 
    private String completeTask() throws IOException {
          widget_main.sc=7;
        return NewConVk.newCon();
    }
}
