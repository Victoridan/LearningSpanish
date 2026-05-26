package model;

/**
 * Класс карточки.
 * id - индекс пары, по нему проверяем правильность ответа (у карточек, которые друг другу
 * соответствуют, он одинаковый).
 * text - само слово.
 * state - состояние карточки в процессе игры.
 */
public class Card {
    private final int id;
    private final String text;
    private State state = State.NORMAL;

    public Card(int id, String text) {
        this.id = id;
        this.text = text;
    }

    public int getId() { return id; }
    public String getText() { return text; }
    public State getState() { return state; }

    public void setState(State state) {
        this.state = state;
    }
}
