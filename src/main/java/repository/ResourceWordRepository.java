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
    private List<WordPair> cachedPairs;

    public ResourceWordRepository(String languageKey) {
        this.fileName = languageKey + ".properties";
    }

    @Override
    public List<WordPair> getPairs() {
        if (cachedPairs != null) {
            return cachedPairs;
        }

        try (InputStream in = getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (in == null) throw new IllegalStateException("Файл не найден в resources: " + fileName);

            Properties props = new Properties();
            props.load(new InputStreamReader(in, StandardCharsets.UTF_8));
            
            List<WordPair> result = new ArrayList<>();
            int id = 0;
            
            for (Map.Entry<Object, Object> entry : props.entrySet()) {
                result.add(new WordPair(id++, entry.getKey().toString().strip(), entry.getValue().toString().strip()));
            }
            
            cachedPairs = result;
            return result;
        } catch (IOException e) {
            throw new RuntimeException("Ошибка чтения: " + fileName, e);
        }
    }
}
