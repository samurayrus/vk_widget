package samurayrus.vk_widget_servers.log;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class LoggerWriter {
    private final String path;
    private FileWriter fileWriter;
    private BufferedWriter bufferedWriter;
    private final boolean adding;

    public LoggerWriter(final String path, final boolean adding) {
        this.path = path;
        this.adding = adding;
        fileCreator();
    }

    private void fileCreator() {
        try {
            File file;
            if (path == null || path.equals("-")) {
                file = new File("VkWidgetLog.log");
                if (file.exists() && file.isFile()) {
                    System.out.println("File for logs loaded and using default path " + file.getAbsolutePath());
                    //TODO: добавить архивирование старых файлов с логами
                    if(file.length()>500_000){ //~0.5 mb
                        System.out.println("File size more then ~0.5mb. Save old log file and create new");
                        Path source = Paths.get(file.getAbsolutePath());
                        Files.move(source, source.resolveSibling("VkWidgetLog-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss")) + ".log.old"));
                        file = new File("VkWidgetLog.log");
                    }
                } else {
                    System.out.println("File [VkWidgetLog.log] was created using default path " + file.getAbsolutePath());
                }
            } else {
                file = new File(path);
                System.out.println("File Path " + file.getAbsolutePath());
            }
            //true чтобы не перезаписывать файл, а добавлять в конец.
            fileWriter = new FileWriter(file, adding);
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(System.lineSeparator() + System.lineSeparator() + "___Start Log in" + new Date() + "____");
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            System.err.println("File for write not found");
        }
    }


    public void writeLog(final String log) {
        try {
            System.out.print(log);
            //Чтобы ответ был не в одну строку, а блоком. Так читабельней
            String[] answerMessageBlock = log.split(System.lineSeparator());

            for (int h = 1; h < answerMessageBlock.length; h++) {
                bufferedWriter.newLine();
                bufferedWriter.write(answerMessageBlock[h]);
                bufferedWriter.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("IOException Записи результата. " + e.getMessage());
        } catch (NullPointerException ex) {
            ex.printStackTrace();
            System.err.println("Запись невозможна, т.к не был инициализирован filecreator()");
        }
    }

    public void writeLogWithExceptionTrace(final String log, final Exception exception){
        writeLog(log + System.lineSeparator() + getStackTraceFromException(exception));
    }
    
    private String getStackTraceFromException(final Exception exception){
        StringWriter errors = new StringWriter();
        exception.printStackTrace(new PrintWriter(errors));
        return errors.toString();
    }

    public void closer() {
        try {
            bufferedWriter.close();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Ошибка закрытия FileWriter");
        } catch (NullPointerException ex) {
            ex.printStackTrace();
            System.err.println("Ошибка закрытия FileWriter = null?");
        }
    }
}
