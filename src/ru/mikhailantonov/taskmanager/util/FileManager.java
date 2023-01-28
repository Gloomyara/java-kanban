package ru.mikhailantonov.taskmanager.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * Utility class для работы с файлами и директориями
 */
public class FileManager {
    private static final String HOME = "resources";
    private static final String TEST_HOME = "TestResources";
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd.MM.yy");

    //создание файла
    public static Path createFile(String str) throws IOException {
        Path path = Paths.get(HOME, str);
        if (!Files.exists(path)) {
            Files.createFile(path);
            System.out.println("Файл успешно создан.");
        }
        return path;
    }

    public static Path createTestFile(String str) throws IOException {
        Path path = Paths.get(TEST_HOME, str);
        if (!Files.exists(path)) {
            Files.createFile(path);
            System.out.println("Файл для тестов успешно создан.");
        }
        return path;
    }

    // создание директории testDirectory
    public static Path createDir(String str) throws IOException {
        Path path = Paths.get(HOME, str);
        if (!Files.exists(path)) {
            Files.createDirectory(path);
            System.out.println("Директория успешно создана.");
        }
        return path;
    }

    public static Path createTestDir(String str) throws IOException {
        Path path = Paths.get(TEST_HOME, str);
        if (!Files.exists(path)) {
            Files.createDirectory(path);
            System.out.println("Директория для тестов успешно создана.");
        }
        return path;
    }

    public static Path moveFile(Path dir, Path init, String result) throws IOException {
        // перемещение файла testFile в директорию testDirectory
        Path path = Paths.get(dir.toString(), result);
        Files.move(init, path, REPLACE_EXISTING);

        if (Files.exists(path)) {
            System.out.println("Файл перемещён в testDirectory.");
        }
        return path;
    }

    // удаление файла
    public static void deleteFile(Path path) throws IOException {
        Files.delete(path);
        if (!Files.exists(path)) {
            System.out.println("Тестовый файл удалён.");
        }
    }

    // удаление пустой директории
    public static void deleteDir(Path path) throws IOException {
        Files.delete(path);
        if (!Files.exists(path)) {
            System.out.println("Директория удалена.");
        }
    }
}
