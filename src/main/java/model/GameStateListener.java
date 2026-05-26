package model;

import java.util.List;

public interface GameStateListener {
    void onCardsDealt(List<Card> russianCards, List<Card> foreignCards);
    void onCardStateChanged(Card card);
    void onScoreUpdated(int correct, int incorrect);
    void onRoundCompleted();
    void onSessionCompleted(SessionResult result);
}

