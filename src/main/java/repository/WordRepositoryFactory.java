package repository;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.logging.Level;

public class WordRepositoryFactory {
    private static final String MANIFEST_PATH = "languages.properties";
    private static final Logger LOGGER = Logger.getLogger(WordRepositoryFactory.class.getName());

    private static Map<Language, WordRepository> cachedLanguages = null;

    public static Map<Language, WordRepository> getLanguages() {
        if (cachedLanguages != null) {
            return cachedLanguages;
        }

        Map<Language, WordRepository> map = new HashMap<>();
        InputStream in = WordRepositoryFactory.class.getClassLoader().getResourceAsStream(MANIFEST_PATH);

        if (in == null) {
            LOGGER.log(Level.SEVERE, "Файлы словарей не найдены в resources/");
            cachedLanguages = map;
            return map;
        }

        try {
            Properties props = new Properties();
            props.load(new InputStreamReader(in, StandardCharsets.UTF_8));

            for (String key : props.stringPropertyNames()) {
                Language lang = new Language(key, props.getProperty(key));
                map.put(lang, new ResourceWordRepository(key));
            }

            cachedLanguages = map;

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Ошибка манифеста", e);
            cachedLanguages = map;
        }

        return cachedLanguages;
    }
}