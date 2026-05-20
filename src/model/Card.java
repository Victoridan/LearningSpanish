package model;
import java.util.ArrayList;
import java.util.List;


/**
 * Класс карточки.
 * id - индекс пары, по нему проверяем правильность ответа (у карточек, которые друг другу
 * соответствуют, он одинаковый).
 * text - само слово.
 * state - состояние карточки в процессе игры.
 * массив Listeners хранит всех подписчиков.
 */
public class Card {
    private final int id;
    private final String text;
    private State state = State.NORMAL;
    private final List<CardStateListener> listeners = new ArrayList<>();

    public Card(int id, String text) {
        this.id = id;
        this.text = text;
    }

    public int getId() { return id; }
    public String getText() { return text; }
    public State getState() { return state; }

    public void setState(State state) {
        if (this.state != state) {
            this.state = state;
            notifyListeners();
        }
    }

    public void addListener(CardStateListener listener) {
        listeners.add(listener);
    }

    private void notifyListeners() {
        for (CardStateListener listener : listeners) {
            listener.onStateChanged(this, state);
        }
    }
}