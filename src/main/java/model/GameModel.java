package model;

import repository.Language;
import repository.WordRepository;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class GameModel {
    private final Map<Language, WordRepository> availableLanguages;
    private Language currentLanguage;
    private final int pairCount;

    private List<Card> russianCards;
    private List<Card> foreignCards;
    private int correctCount = 0;
    private int incorrectCount = 0;

    private Card selectedCard = null;

    private final List<GameStateListener> listeners = new CopyOnWriteArrayList<>();

    public GameModel(Map<Language, WordRepository> availableLanguages, Language initialLanguage, int pairCount) {
        if (pairCount <= 0) throw new IllegalArgumentException("Число пар должно быть больше 0");
        this.availableLanguages = availableLanguages;
        this.currentLanguage = initialLanguage;
        this.pairCount = pairCount;

        WordRepository initialRepo = availableLanguages.get(initialLanguage);
        if (initialRepo == null) throw new IllegalArgumentException("Репозиторий для выбранного языка не найден");

        int maxPairs = initialRepo.getPairs().size();
        if (pairCount > maxPairs) throw new IllegalArgumentException("Число пар (" + pairCount + ") превышает доступное количество (" + maxPairs + ")");

        initializeCards(initialRepo);
    }

    public void addListener(GameStateListener listener) {
        listeners.add(listener);
    }

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

    public void startSession(Language language) {
        WordRepository newRepository = availableLanguages.get(language);
        if (newRepository == null) return;

        this.currentLanguage = language;
        this.correctCount = 0;
        this.incorrectCount = 0;
        this.selectedCard = null;
        initializeCards(newRepository);

        for (GameStateListener l : listeners) l.onScoreUpdated(correctCount, incorrectCount);
    }

    public void endSession() {
        for (GameStateListener l : listeners) {
            l.onSessionCompleted(getSessionResult());
        }
    }

    public void startNextRound() {
        WordRepository repo = availableLanguages.get(currentLanguage);
        if (repo != null) {
            this.selectedCard = null;
            initializeCards(repo);
        }
    }

    public boolean isRoundComplete() {
        for (Card c : russianCards) {
            if (c.getState() != State.MATCHED) return false;
        }
        for (Card c : foreignCards) {
            if (c.getState() != State.MATCHED) return false;
        }
        return true;
    }

    public void selectCard(Card clickedCard) {
        if (clickedCard.getState() == State.MATCHED || this.selectedCard == clickedCard) {
            return;
        }

        if (this.selectedCard == null) {
            this.selectedCard = clickedCard;
            for (GameStateListener l : listeners) l.onCardSelectionChanged(clickedCard, true);
        } else {
            Card c1 = this.selectedCard;
            Card c2 = clickedCard;
            this.selectedCard = null;

            for (GameStateListener l : listeners) l.onCardSelectionChanged(c1, false);

            match(c1, c2);
        }
    }

    private void match(Card c1, Card c2) {
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
        } else {
            incorrectCount++;
            for (GameStateListener l : listeners) l.onScoreUpdated(correctCount, incorrectCount);
            for (GameStateListener l : listeners) l.onCardsMismatch(c1, c2);
        }
    }

    private void notifyStateChanged(Card c) {
        for (GameStateListener l : listeners) l.onCardStateChanged(c);
    }

    public SessionResult getSessionResult() {
        return new SessionResult(correctCount, incorrectCount);
    }

    public Collection<Language> getAvailableLanguages() {
        return availableLanguages.keySet();
    }

    public Language getCurrentLanguage() {
        return currentLanguage;
    }

    public List<Card> getRussianCards() { return russianCards; }
    public List<Card> getForeignCards() { return foreignCards; }
}