package repository;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class LanguageManifest {
    private static final String MANIFEST_PATH = "languages.manifest";

    /**
     * Возвращает отображение ключа в отображаемое название языка, прим. spanish - Испанский.
     * Загружает файл, если файла нет, возвращает пустую мапу.
     * Открывает BufferedReader и построчно читает файл. Пока не дошли до конца файла, читаем строку,
     * удаляем пробелы, опускает пустые строки и комментарии, разделяет по =, добавляет в мапу.
     */
    public static Map<String, String> getLanguages() {
        Map<String, String> languages = new LinkedHashMap<>();
        InputStream in = LanguageManifest.class.getClassLoader().getResourceAsStream(MANIFEST_PATH);
        if (in == null) {
            return languages;
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.strip();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] parts = line.split("=", 2);
                if (parts.length == 2) languages.put(parts[0].strip(), parts[1].strip());
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка манифеста", e);
        }
        return languages;
    }

    /*.
     * По ключу языка создаёт репозиторий, который загружает конкретный словарь.
     */
    public static WordRepository repositoryFor(String languageKey) {
        return new ClasspathWordRepository(languageKey);
    }
}

