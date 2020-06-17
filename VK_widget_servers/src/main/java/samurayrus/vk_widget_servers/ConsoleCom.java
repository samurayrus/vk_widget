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
    public void run()
    {   
        System.out.println("Console_start");
        while(bb)
        {
            A:
         {
        System.out.print("Input: ");
        String []com= sc.nextLine().split(" ");
        
        switch(com[0])
        {
            case "pause":pause_widget(); break; //Сброс. Возврат к консоли
            case "restart":restart_widget(); break;
            case "e": start_widget(); break;  //запуск по дефолту 
            case "q":exit_widget(); break;    //завершение программы
            case "timer":   //Присвоение значение таймеру
                try{
                    
                int t= Integer.valueOf(com[1]);
                if(t>30)
                timer_set(t);
                else
                System.out.println("Error -> \n timer X / where x MIN = 30 (sec)");
                
                break;
                }catch(NumberFormatException ex) {System.out.println("NumberFormatException\n");}
                 catch(java.lang.ArrayIndexOutOfBoundsException exx){System.out.println("ArrayIndexOutOfBoundsException\n");}
                finally{System.out.println("Try again \n {timer X} / where X - Integer, not null, MIN 30 (sec)"); break A;}
                 
        }
        }
        }
    }
    
    private void pause_widget()
    {
        if(timer!=null)
        timer.cancel();
      System.out.println("pause_widget");
    }
    
    private void timer_set(int t)
    {
        pause_widget();
        
        timerTask = new TimerTt();
        timer = new Timer(true);
        timer.scheduleAtFixedRate(timerTask, 0, t*1000); // (50 * 1000 миллисекунд)
        
        
      System.out.println("samurayrus.vk_widget_servers.ConsoleCom.pause_widget()"+"ok");
    }
    
    private void restart_widget()
    {
    System.out.println("samurayrus.vk_widget_servers.ConsoleCom.pause_widget()"+"no");
    }
    
    private void start_widget()
    {
        pause_widget();
        
        timerTask = new TimerTt();
        timer = new Timer(true);
        timer.scheduleAtFixedRate(timerTask, 0, 50*1000); // (50 * 1000 миллисекунд)

    System.out.println("samurayrus.vk_widget_servers.ConsoleCom.pause_widget()"+"yes");
    }
    
    private void exit_widget()
    {
        pause_widget();
        
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
