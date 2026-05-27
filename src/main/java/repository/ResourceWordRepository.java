package repository;

import model.WordPair;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Map;

public class ResourceWordRepository implements WordRepository {
    private final String fileName;

    private volatile List<WordPair> cachedPairs;

    public ResourceWordRepository(String languageKey) {
        this.fileName = languageKey + ".properties";
    }

    @Override
    public List<WordPair> getPairs() {
        List<WordPair> result = cachedPairs;
        if (result != null) {
            return result;
        }

        synchronized (this) {
            result = cachedPairs;
            if (result == null) {
                try (InputStream in = getClass().getClassLoader().getResourceAsStream(fileName)) {
                    if (in == null) throw new IllegalStateException("Файл не найден в resources: " + fileName);

                    Properties props = new Properties();
                    props.load(new InputStreamReader(in, StandardCharsets.UTF_8));

                    List<WordPair> parsingResult = new ArrayList<>();
                    int id = 0;

                    for (Map.Entry<Object, Object> entry : props.entrySet()) {
                        String key = entry.getKey().toString().replace("\uFEFF", "").strip();
                        if (key.startsWith("#") || key.isEmpty()) {
                            continue;
                        }

                        String value = entry.getValue().toString().strip();
                        parsingResult.add(new WordPair(id++, key, value));
                    }

                    cachedPairs = parsingResult;
                    result = parsingResult;
                } catch (IOException e) {
                    throw new RuntimeException("Ошибка чтения: " + fileName, e);
                }
            }
            return result;
        }
    }
}