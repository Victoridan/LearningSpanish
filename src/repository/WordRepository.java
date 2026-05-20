package repository;
import model.WordPair;
import java.util.List;

public interface WordRepository {
    List<WordPair> loadPairs();
}