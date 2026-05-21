package model;

import repository.WordRepository;
import java.util.*;
import java.util.stream.Collectors;

public class GameModel {
    private WordRepository repository;
    private int pairCount;
    private List<Card> russianCards;
    private List<Card> foreignCards;
    private int correctCount = 0;
    private int incorrectCount = 0;

    public GameModel(WordRepository repository, int pairCount) {
        this.repository = repository;
        this.pairCount = pairCount;
        initializeCards(repository);
    }

    /**
     * Загружает пары слов из репозитория, перемешивает и отбирает нужное количество.
     * Создаёт карточки для левой (русской) и правой (иностранной) колонок.
     */
    private void initializeCards(WordRepository repo) {
        List<WordPair> pairs = repo.loadPairs();
        Collections.shuffle(pairs);
        int n = Math.min(pairCount, pairs.size());
        List<WordPair> selected = pairs.subList(0, n);
        this.russianCards = selected.stream()
                .map(p -> new Card(p.id(), p.russian()))
                .collect(Collectors.toCollection(ArrayList::new));
        this.foreignCards = selected.stream()
                .map(p -> new Card(p.id(), p.foreign()))
                .collect(Collectors.toCollection(ArrayList::new));
        Collections.shuffle(this.russianCards);
        Collections.shuffle(this.foreignCards);
    }

    /**
     * Сбрасывает счётчики и перезапускает игру с новым репозиторием (новым языком).
     */
    public void restartSession(WordRepository newRepository) {
        this.repository = newRepository;
        this.correctCount = 0;
        this.incorrectCount = 0;
        initializeCards(newRepository);
    }

    /**
     * Начать новый раунд с тем же репозиторием (языком).
     * Счётчики правильных/неправильных ответов при этом НЕ сбрасываются —
     * статистика накапливается по всем раундам в текущей сессии.
     */
    public void startNextRound() {
        if (this.repository != null) {
            initializeCards(this.repository);
        }
    }

    /**
     * Проверяет, завершён ли текущий раунд (все карточки сопоставлены).
     */
    public boolean isRoundComplete() {
        for (Card c : russianCards) {
            if (c.getState() != State.MATCHED) return false;
        }
        for (Card c : foreignCards) {
            if (c.getState() != State.MATCHED) return false;
        }
        return true;
    }

    public boolean check(Card c1, Card c2) {
        if (c1.getId() == c2.getId()) {
            correctCount++;
            return true;
        } else {
            incorrectCount++;
            return false;
        }
    }

    /**
     * Возвращает результаты текущей игровой сессии.
     */
    public SessionResult getSessionResult() {
        return new SessionResult(correctCount, incorrectCount);
    }

    public List<Card> getRussianCards() { return russianCards; }
    public List<Card> getForeignCards() { return foreignCards; }
}
