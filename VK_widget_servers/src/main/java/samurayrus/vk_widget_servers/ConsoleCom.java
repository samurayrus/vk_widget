/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package samurayrus.vk_widget_servers;

//import com.sun.xml.internal.stream.Entity;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author maxim
 */
public class ConsoleCom extends Thread{
    private Scanner sc;
    private Boolean bb = true;
    private TimerTask timerTask;
    private Timer timer;
    
    public ConsoleCom() {
        sc = new Scanner(System.in);
    }
    
    
        @Override
    public void start()
    {
        System.out.println("Console_start");
    }
    
    @Override
    public void run()
    {   
        while(bb)
        {
        System.out.print("Input: ");
        switch(sc.nextLine())
        {
            case "pause":pause_widget(); break;
            case "restart":restart_widget(); break;
            case "e": start_widget(); break;
            case "q":exit_widget(); break;
        }
        }
    }
    
    private void pause_widget()
    {
        timer.cancel();
      System.out.println("samurayrus.vk_widget_servers.ConsoleCom.pause_widget()"+"ok");
    }
    
    private void restart_widget()
    {
    System.out.println("samurayrus.vk_widget_servers.ConsoleCom.pause_widget()"+"no");
    }
    
    private void start_widget()
    {
          timerTask = new TimerTt();
         timer = new Timer(true);
        timer.scheduleAtFixedRate(timerTask, 0, 50*1000); // (50 * 1000 миллисекунд)

    System.out.println("samurayrus.vk_widget_servers.ConsoleCom.pause_widget()"+"yes");
    }
    
    private void exit_widget()
    {
        timer.cancel();
        setBb(false);
    System.out.println("samurayrus.vk_widget_servers.ConsoleCom.pause_widget()"+"Exit");
    }

    public Boolean getBb() {
        return bb;
    }

    public void setBb(Boolean bb) {
        this.bb = bb;
    }
    
}
