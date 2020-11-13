package samurayrus.vk_widget_servers;

import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class ConsoleCom extends Thread {

    private Scanner sc;
    private Boolean bb = true;
    private TimerTask timerTask;
    private Timer timer;

    public ConsoleCom() {
        sc = new Scanner(System.in);
        start_widget();
    }

    @Override
    public void run() {
        System.out.println("Console_start");
        while (bb) {
            A:
            {
                System.out.print("Input: ");
                String[] com = sc.nextLine().split(" ");

                switch (com[0]) {
                    case "help":
                        System.out.println(
                                "Commands: \n"
                                + " [p] - pause widget. \n"
                                + " [e] - start \n"
                                + " [r] - restart \n"
                                + " [s TRUE/FALSE] - Easy safe mod. Doesn't show empty(0-1) servers! \n"
                                + " [Hs TRUE/FALSE] - Doesn't show other servers! Only official! \n"
                                + " [q] - exit widget \n"
                                + " [t X] -  the refresh timer where X min = 30 (sec)\n"
                                + " ┌( ಠ_ಠ)┘[Good luck from SamurayRus!] ┌( ಠ_ಠ)┘ \n"
                                + " "
                        );
                        break;
                    case "p": //Пауза
                        pause_widget();
                        break;
                    case "r": //Сброс. Возврат к консоли. Забиваются новые значения из Prop
                        restart_widget();
                        break;
                    case "e":
                        start_widget();
                        break;  //запуск по дефолту
                    case "s":
                        NewConVk.setSafeMode(Boolean.parseBoolean(com[1]));
                        System.out.println("SafeMode set " + Boolean.parseBoolean(com[1]));
                        break;
                    case "Hs":
                        NewConVk.setHardSafeMode(Boolean.parseBoolean(com[1]));
                        System.out.println("HardSafeMode set " + Boolean.parseBoolean(com[1]));
                        break;
                    case "q":
                        exit_widget(); //завершение программы
                        break;
                    case "t":   //Присвоение значение таймеру
                        try {
                            int t = Integer.valueOf(com[1]);
                            if (t > 30) {
                                timer_set(t);
                                System.out.println("Timer set " + t);
                            } else {
                                System.out.println("Error -> \n Try again \n {t X} / where X - Integer, not null, MIN 30 (sec)");
                            }
                            break;
                        } catch (NumberFormatException ex) {
                            System.out.println("NumberFormatException\n");
                        } catch (java.lang.ArrayIndexOutOfBoundsException exx) {
                            System.out.println("ArrayIndexOutOfBoundsException\n");
                        } finally {
                            //break A;
                        }
                }
            }
        }
    }

    private void pause_widget() {
        if (timer != null) {
            timer.cancel();
        }
        System.out.println("pause_widget");
    }

    private void timer_set(int t) {
        pause_widget();

        timerTask = new TimerTt();
        timer = new Timer(true);
        timer.scheduleAtFixedRate(timerTask, 0, t * 1000); // (50 * 1000 миллисекунд)
        System.out.println("samurayrus.vk_widget_servers.ConsoleCom.pause_widget()" + "ok");
    }

    private void restart_widget() {
        pause_widget();
        NewConVk.newProp();
        start_widget();
        System.out.println("samurayrus.vk_widget_servers.ConsoleCom.pause_widget()" + "restarted");
    }

    private void start_widget() {
        pause_widget();
        timerTask = new TimerTt();
        timer = new Timer(true);
        timer.scheduleAtFixedRate(timerTask, 0, 50 * 1000); // (50 * 1000 миллисекунд)

        System.out.println("samurayrus.vk_widget_servers.ConsoleCom.pause_widget()" + "yes");
    }

    private void exit_widget() {
        pause_widget();

        setBb(false);
        System.out.println("samurayrus.vk_widget_servers.ConsoleCom.pause_widget()" + "Exit");
    }

    public Boolean getBb() {
        return bb;
    }

    public void setBb(Boolean bb) {
        this.bb = bb;
    }

}
