package model;

import org.junit.jupiter.api.Test;

import repository.WordRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GameModelTest {

    private static class FakeRepo implements WordRepository {
        private final List<WordPair> pairs;

        FakeRepo(List<WordPair> pairs) { this.pairs = pairs; }

        @Override
        public List<WordPair> loadPairs() { return new java.util.ArrayList<>(pairs); }
    }

    @Test
    void initializationCreatesCorrectNumberOfCards() {
        List<WordPair> pairs = List.of(
                new WordPair(1, "r1", "f1"),
                new WordPair(2, "r2", "f2"),
                new WordPair(3, "r3", "f3")
        );

        GameModel model = new GameModel(new FakeRepo(pairs), 2);

        assertEquals(2, model.getRussianCards().size(), "должно быть 2 русских карточки");
        assertEquals(2, model.getForeignCards().size(), "должно быть 2 иностранных карточки");
    }

    @Test
    void checkUpdatesCorrectAndIncorrectCountsAndSessionResult() {
        List<WordPair> pairs = List.of(
                new WordPair(1, "r1", "f1"),
                new WordPair(2, "r2", "f2")
        );

        GameModel model = new GameModel(new FakeRepo(pairs), 2);

        // найдём русскую карточку и соответствующую ей иностранную
        Card rus = model.getRussianCards().get(0);
        Card matchingForeign = model.getForeignCards().stream()
                .filter(c -> c.getId() == rus.getId())
                .findFirst()
                .orElseThrow();

        assertTrue(model.check(rus, matchingForeign), "пара должна быть корректной");

        SessionResult res1 = model.getSessionResult();
        assertEquals(1, res1.correct());
        assertEquals(0, res1.incorrect());

        // теперь намеренно неверная пара
        Card rus2 = model.getRussianCards().stream().filter(c -> c.getId() != rus.getId()).findFirst().orElseThrow();
        Card foreignNotMatching = model.getForeignCards().stream().filter(c -> c.getId() != rus2.getId()).findFirst().orElseThrow();

        assertFalse(model.check(rus2, foreignNotMatching), "пара должна быть неверной");

        SessionResult res2 = model.getSessionResult();
        assertEquals(1, res2.correct());
        assertEquals(1, res2.incorrect());
    }

    @Test
    void restartSessionResetsCountersAndReloadsPairs() {
        List<WordPair> pairs1 = List.of(new WordPair(1, "r1", "f1"));
        GameModel model = new GameModel(new FakeRepo(pairs1), 1);

        Card rus = model.getRussianCards().get(0);
        Card foreign = model.getForeignCards().get(0);
        assertTrue(model.check(rus, foreign));
        SessionResult before = model.getSessionResult();
        assertEquals(1, before.correct());

        // перезапускаем с новым репозиторием
        List<WordPair> pairs2 = List.of(
                new WordPair(2, "r2", "f2"),
                new WordPair(3, "r3", "f3")
        );
        model.restartSession(new FakeRepo(pairs2));

        SessionResult after = model.getSessionResult();
        assertEquals(0, after.correct(), "после restartSession счётчики должны обнулиться");
        assertEquals(0, after.incorrect());
        assertEquals(1, model.getRussianCards().size(), "должна загрузиться 1 карточка из нового репозитория, т.к. pairCount был 1");
    }
}
