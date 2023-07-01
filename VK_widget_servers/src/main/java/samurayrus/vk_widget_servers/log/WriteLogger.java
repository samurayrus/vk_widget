package samurayrus.vk_widget_servers.log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class WriteLogger {
    private final String path;
    private FileWriter fileWriter;
    private BufferedWriter bufferedWriter;
    private final boolean adding;

    public WriteLogger(final String path, final boolean adding) {
        this.path = path;
        this.adding = adding;
        fileCreator();
    }

    //Создание файла для записи результатов или же поиск его и добавление первой строки с датой
    private void fileCreator() {
        try {
            File file;
            //Создает новый файл, если не был указан в аргументах (пересоздает уже созданный там)
            if (path == null || path.equals("-")) {
                file = new File("LogFile.txt");
                System.out.println("File [LogFile.txt] was created using the default path " + file.getAbsolutePath());
            } else {
                file = new File(path);
                System.out.println("File Path " + file.getAbsolutePath());
            }
            //true чтобы не перезаписывать файл, а добавлять в конец.
            fileWriter = new FileWriter(file, adding);
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write("___Start Log in" + new Date().toString() + "____");
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            System.out.println("File for write not found");
        }
    }


    public void writeAnswer(final String answer) {
        try {
            //Чтобы ответ был не в одну строку, а блоком. Так читабельней
            String[] answerMessageBlock = answer.split("\n");

            for (int h = 1; h < answerMessageBlock.length; h++) {
                bufferedWriter.newLine();
                bufferedWriter.write(answerMessageBlock[h]);
                bufferedWriter.flush();
            }
        } catch (IOException e) {
            System.out.println("IOException Записи результата");
        } catch (NullPointerException ex) {
            ex.printStackTrace();
            System.out.println("Запись невозможна, т.к не был инициализирован filecreator()");
        }

    }

    public void closer() {
        try {
            bufferedWriter.close();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Ошибка закрытия FileWriter");
            //Не выбрасывается, но решил добавить. Мало ли в будущем этот метод будет вызываться в другом порядке, нежели в ConsoleMain
        } catch (NullPointerException ex) {
            ex.printStackTrace();
            System.out.println("Ошибка закрытия FileWriter = null?");
        }
    }
}
