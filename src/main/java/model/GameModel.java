package model;

import repository.WordRepository;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class GameModel {
    private WordRepository repository;
    private int pairCount;
    private List<Card> russianCards;
    private List<Card> foreignCards;
    private int correctCount = 0;
    private int incorrectCount = 0;
    private final List<GameStateListener> listeners = new CopyOnWriteArrayList<>();

    public GameModel(WordRepository repository, int pairCount) {
        if (pairCount <= 0) {
            throw new IllegalArgumentException("Число пар должно быть больше 0");
        }
        int maxPairs = repository.getPairs().size();
        if (pairCount > maxPairs) {
            throw new IllegalArgumentException("Число пар (" + pairCount + ") превышает доступное количество (" + maxPairs + ")");
        }
        this.repository = repository;
        this.pairCount = pairCount;
        initializeCards(repository);
    }
    
    public void addListener(GameStateListener listener) {
        listeners.add(listener);
    }

    /**
     * Загружает пары слов из репозитория, перемешивает и отбирает нужное количество.
     * Создаёт карточки для левой (русской) и правой (иностранной) колонок.
     */
    private void initializeCards(WordRepository repo) {
        List<WordPair> pairs = new ArrayList<>(repo.getPairs());
        Collections.shuffle(pairs);
        List<WordPair> selected = pairs.subList(0, pairCount);
        this.russianCards = selected.stream()
                .map(p -> new Card(p.id(), p.russian()))
                .collect(Collectors.toCollection(ArrayList::new));
        this.foreignCards = selected.stream()
                .map(p -> new Card(p.id(), p.foreign()))
                .collect(Collectors.toCollection(ArrayList::new));
        Collections.shuffle(this.russianCards);
        Collections.shuffle(this.foreignCards);

        for (GameStateListener l : listeners) {
            l.onCardsDealt(this.russianCards, this.foreignCards);
        }
    }

    /**
     * Сбрасывает счётчики и перезапускает игру с новым репозиторием (новым языком).
     */
    public void startSession(WordRepository newRepository) {
        this.repository = newRepository;
        this.correctCount = 0;
        this.incorrectCount = 0;
        initializeCards(newRepository);
        for (GameStateListener l : listeners) l.onScoreUpdated(correctCount, incorrectCount);
    }
    
    public void endSession() {
        for (GameStateListener l : listeners) {
            l.onSessionCompleted(getSessionResult());
        }
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
            c1.setState(State.MATCHED);
            c2.setState(State.MATCHED);
            notifyStateChanged(c1);
            notifyStateChanged(c2);
            for (GameStateListener l : listeners) l.onScoreUpdated(correctCount, incorrectCount);
            if (isRoundComplete()) {
                for (GameStateListener l : listeners) l.onRoundCompleted();
            }
            return true;
        } else {
            incorrectCount++;
            c1.setState(State.WRONG);
            c2.setState(State.WRONG);
            notifyStateChanged(c1);
            notifyStateChanged(c2);
            for (GameStateListener l : listeners) l.onScoreUpdated(correctCount, incorrectCount);
            return false;
        }
    }
    
    public void resetWrongState(Card c1, Card c2) {
        if (c1.getState() == State.WRONG) {
            c1.setState(State.NORMAL);
            notifyStateChanged(c1);
        }
        if (c2.getState() == State.WRONG) {
            c2.setState(State.NORMAL);
            notifyStateChanged(c2);
        }
    }
    
    private void notifyStateChanged(Card c) {
        for (GameStateListener l : listeners) l.onCardStateChanged(c);
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
