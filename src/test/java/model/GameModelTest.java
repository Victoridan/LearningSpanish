package model;

import org.junit.jupiter.api.Test;

import repository.WordRepository;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import repository.Language;

public class GameModelTest {

    private static class FakeRepo implements WordRepository {
        private final List<WordPair> pairs;

        FakeRepo(List<WordPair> pairs) { this.pairs = pairs; }

        @Override
        public List<WordPair> getPairs() { return pairs; }
    }

    @Test
    void initializationCreatesCorrectNumberOfCards() {
        List<WordPair> pairs = List.of(
                new WordPair(1, "r1", "f1"),
                new WordPair(2, "r2", "f2"),
                new WordPair(3, "r3", "f3")
        );
        Language dummy = new Language("dummy", "Dummy");
        Map<Language, WordRepository> map = Map.of(dummy, new FakeRepo(pairs));

        GameModel model = new GameModel(map, dummy, 2);

        assertEquals(2, model.getRussianCards().size(), "должно быть 2 русских карточки");
        assertEquals(2, model.getForeignCards().size(), "должно быть 2 иностранных карточки");
    }

    @Test
    void checkUpdatesCorrectAndIncorrectCountsAndSessionResult() {
        List<WordPair> pairs = List.of(
                new WordPair(1, "r1", "f1"),
                new WordPair(2, "r2", "f2"),
                new WordPair(3, "r3", "f3")
        );
        Language dummy = new Language("dummy", "Dummy");
        Map<Language, WordRepository> map = Map.of(dummy, new FakeRepo(pairs));

        GameModel model = new GameModel(map, dummy, 3);

        // найдём русскую карточку и соответствующую ей иностранную
        Card rus = model.getRussianCards().getFirst();
        Card matchingForeign = model.getForeignCards().stream()
                .filter(c -> c.getId() == rus.getId())
                .findFirst()
                .orElseThrow();

        model.selectCard(rus);
        model.selectCard(matchingForeign);
        assertEquals(State.MATCHED, rus.getState(), "карточки должны совпасть");

        SessionResult res1 = model.getSessionResult();
        assertEquals(1, res1.correct());
        assertEquals(0, res1.incorrect());

        // теперь намеренно неверная пара
        Card rus2 = model.getRussianCards().stream().filter(c -> c.getId() != rus.getId()).findFirst().orElseThrow();
        Card foreignNotMatching = model.getForeignCards().stream()
                .filter(c -> c.getId() != rus.getId() && c.getId() != rus2.getId())
                .findFirst()
                .orElseThrow();

        model.selectCard(rus2);
        model.selectCard(foreignNotMatching);
        assertEquals(State.WRONG, rus2.getState(), "карточки должны быть неверной парой");

        SessionResult res2 = model.getSessionResult();
        assertEquals(1, res2.correct());
        assertEquals(1, res2.incorrect());
    }

    @Test
    void startSessionResetsCountersAndReloadsPairs() {
        List<WordPair> pairs1 = List.of(new WordPair(1, "r1", "f1"));
        List<WordPair> pairs2 = List.of(
                new WordPair(2, "r2", "f2"),
                new WordPair(3, "r3", "f3")
        );
        
        Language lang1 = new Language("l1", "L1");
        Language lang2 = new Language("l2", "L2");
        Map<Language, WordRepository> map = Map.of(lang1, new FakeRepo(pairs1), lang2, new FakeRepo(pairs2));

        GameModel model = new GameModel(map, lang1, 1);

        Card rus = model.getRussianCards().getFirst();
        Card foreign = model.getForeignCards().getFirst();
        model.selectCard(rus);
        model.selectCard(foreign);
        
        SessionResult before = model.getSessionResult();
        assertEquals(1, before.correct());

        // перезапускаем с новым языком
        model.startSession(lang2);

        SessionResult after = model.getSessionResult();
        assertEquals(0, after.correct(), "после startSession счётчики должны обнулиться");
        assertEquals(0, after.incorrect());
        assertEquals(1, model.getRussianCards().size(), "должна загрузиться 1 карточка из нового репозитория, т.к. pairCount был 1");
    }
}
