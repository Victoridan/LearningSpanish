package model;

import java.util.List;

public class GameModel {
    private final List<Cards.Card> cards;
    
    // конструктор инициализирует игровую сессию
    public GameModel(List<Cards.Card> cards) {
        this.cards = cards;
    }
    
    // возвращаем все русские слова (левая колонка)
    public List<Cards.Card> getRussianCards() {
        return cards.subList(0, cards.size() / 2);
    }
    
    // возвращаем все испанские слова (правая колонка)
    public List<Cards.Card> getForeignCards() {
        return cards.subList(cards.size() / 2, cards.size());
    }

    // проверяем правильный ответ
    public boolean check(Cards.Card c1, Cards.Card c2) {
        return c1.getId() == c2.getId();
    }
}