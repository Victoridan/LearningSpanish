package repository;
import model.WordPair;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class ClasspathWordRepository implements WordRepository {
    private final String fileName;

    /**
     * Принимает ключ языка, по нему формирует название файла язык + .txt
     */
    public ClasspathWordRepository(String languageKey) {
        this.fileName = languageKey + ".txt";
    }

    /**
     * Ищет файл, если не находит, возвращает null и выбрасываем исключение.
     * Считываем все байты, превращаем в символы кодировки UTF-8.
     * Создаем пустой список для накопления пар слов, храним строку и id, id увеличиваем на 1
     * после создания каждой пары. Удаляет пробельные символы, пропускает пустые строки и комментарии с #,
     * разбивает строку по |, добавляет пару в список пар.
     */
    @Override
    public List<WordPair> loadPairs() {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (in == null) throw new IllegalStateException("Файл не найден в classpath: " + fileName);

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                List<WordPair> result = new ArrayList<>();
                String line;
                int id = 0;
                while ((line = reader.readLine()) != null) {
                    line = line.strip();
                    if (line.isEmpty() || line.startsWith("#")) continue;
                    String[] parts = line.split("\\|", 2);
                    if (parts.length == 2) {
                        result.add(new WordPair(id++, parts[0].strip(), parts[1].strip()));
                    }
                }
                return result;
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка чтения: " + fileName, e);
        }
    }
}