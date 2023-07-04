package samurayrus.vk_widget_servers;

import samurayrus.vk_widget_servers.log.Logger;

import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class ConsoleCommandLine extends Thread {

    private final Scanner scanner;
    private boolean work = true;
    private TimerTask timerTask;
    private Timer timer;
    private int timerValueInSeconds = 50;

    public ConsoleCommandLine() {
        scanner = new Scanner(System.in);
    }

    @Override
    public void run() {
        start_widget();
        Logger.logInfo("Console Start");
        while (work) {
            A:
            try {
                {
                    //for nohup
                    if (!scanner.hasNextLine()) continue;
                    String[] com = scanner.nextLine().split(" ");

                    switch (com[0]) {
                        case "help":
                            System.out.println(
                                    "Commands: \n"
                                            + " [p] - pause widget. \n"
                                            + " [e] - start \n"
                                            + " [r] - restart \n"
                                            + " [local TRUE/FALSE] - show or not local servers (127.x.x.x). default is false \n"
                                            + " [s TRUE/FALSE] - Easy safe mod. Doesn'newTimerValueInSeconds show empty(0-1) servers! \n"
                                            + " [Hs TRUE/FALSE] - Doesn'newTimerValueInSeconds show other servers! Only official! \n"
                                            + " [blA IP TEXT] - add IP/TEXT into blackList \n"
                                            + " [blR IP] - remove ip from blackList \n"
                                            + " [blS] - show blackList \n"
                                            + " [q] - exit widget \n"
                                            + " [t X] -  the refresh timer where X min = 30 (sec)\n"
                                            + " [scurprop] - show current properties for ServerManager\n"
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
                            ServerManager.setSafeMode(Boolean.parseBoolean(com[1]));
                            Logger.logUser("SafeMode set " + Boolean.parseBoolean(com[1]));
                            break;

                        case "local":
                            ServerManager.setShowLocal(Boolean.parseBoolean(com[1]));
                            Logger.logUser("showLocal servers set " + Boolean.parseBoolean(com[1]));
                            break;

                        case "Hs":
                            ServerManager.setHardSafeMode(Boolean.parseBoolean(com[1]));
                            Logger.logUser("HardSafeMode set " + Boolean.parseBoolean(com[1]));
                            break;

                        case "blA":
                            StringBuilder text = new StringBuilder("");
                            for (int i = 2; i < com.length; i++) {
                                text.append(com[i]);
                            }
                            Logger.logUser(ServerManager.addBlackListValue(com[1], text.toString()));
                            break;

                        case "blR":
                            Logger.logUser(ServerManager.deleteBlackListValue(com[1]));
                            break;

                        case "blS":
                            Logger.logUser(ServerManager.showBlackList());
                            break;

                        case "q":
                            exit_widget();
                            break;

                        case "t":   //Присвоение значение таймеру
                            int newTimerValueInSeconds = Integer.parseInt(com[1]);
                            if (newTimerValueInSeconds > 30) {
                                timer_set(newTimerValueInSeconds);
                                Logger.logUser("Timer set " + newTimerValueInSeconds);
                            } else {
                                Logger.logUserError("Error -> \n Try again \n {newTimerValueInSeconds X} / where X - Integer, not null, MIN 30 (sec)");
                            }
                            break;

                        case "scurprop":
                            System.out.println(
                                    "ServerManager current params: "
                                            + "\n show local servers - " + ServerManager.isShowLocal()
                                            + "\n safe mode enabled - " + ServerManager.isSafeMode()
                                            + "\n hard safe mode enabled - " + ServerManager.isHardSafeMode()
                                            + "\n current timer value in seconds - " + timerValueInSeconds
                                            + "\n ┌( ಠ_ಠ)┘[Good luck from SamurayRus!] ┌( ಠ_ಠ)┘ \n"
                            );
                            break;
                    }
                }
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException ex) {
                Logger.logUserError("error when using the console " + ex.getMessage());
            } finally {
                //break A;
            }
        }
    }

    private void pause_widget() {
        if (timer != null) {
            timer.cancel();
        }
        Logger.logUser("Widget on pause");
    }

    private void timer_set(int newTimerValueInSeconds) {
        pause_widget();

        timerTask = new ServerManagerTimer();
        timer = new java.util.Timer(true);
        timer.scheduleAtFixedRate(timerTask, 0, newTimerValueInSeconds * 1000); // (50 * 1000 миллисекунд)
        timerValueInSeconds = newTimerValueInSeconds;
        Logger.logUser("Timer new value set " + newTimerValueInSeconds);
    }

    private void restart_widget() {
        pause_widget();
        ServerManager.setPropertyForWidget();
        start_widget();
        Logger.logUser("Restart Widget");
    }

    private void start_widget() {
        pause_widget();
        timerTask = new ServerManagerTimer();
        timer = new Timer(true);
        timer.scheduleAtFixedRate(timerTask, 0, timerValueInSeconds * 1000); // (50 * 1000 миллисекунд)
        Logger.logUser("Start Widget");
    }

    private void exit_widget() {
        pause_widget();
        work = false;
        Logger.logUser("Exit");
    }
}
