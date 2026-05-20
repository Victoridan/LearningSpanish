package model;

public interface CardStateListener {
    void onStateChanged(Card card, State newState);
}