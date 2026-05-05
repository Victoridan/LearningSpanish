package controller;

import model.Cards;
import model.GameModel;
import view.CardPanel;
import view.MainFrame;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GameController {
    private final GameModel model; // модель игры
    private final MainFrame view; // главное окно
    private CardPanel selected; // первая выбранная

    /**
     * Конструктор, инициализирует поля, ставил флаг "в левую колонку"
     * */
    public GameController(GameModel model, MainFrame view) {
        this.model = model;
        this.view = view;
        for (Cards.Card c : model.getRussianCards()) addCard(c, true);
        for (Cards.Card c : model.getForeignCards()) addCard(c, false);
    }

    /**
     * Создает визуальную карточку, добавляет в панель в правую или левую колонку,
     * */
    private void addCard(Cards.Card card, boolean left) {
        CardPanel panel = new CardPanel(card);
        view.getBoardPanel().addCard(panel, left);
        panel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { onClick(panel); }
        });
    }

    /**
     * Обработка клика мышки. Получаем модель карточки и доступ к полям.
     * Если она уже угадана или пользователь в качестве пары к карточке выбрал ее саму, не реагируем.
     * Если выбрана первая и вторая, получаем данные, проверяем, правильно ли. Если правильно,
     * меняем состояние и перерисовываем. Если неправильно, то же самое и устанавливаем
     * таймер на секунду. Мы проверяем: если она не стала правильно выбранной, меняем состояние
     * на нормальное. То же самое делаем для второй карточки. Делаем сброс выбранной карточки.
     * Иначе, т.е. если это первый клик, мы запоминаем первую выбранную карточку, меняем ее состояние
     * на выбранную, перерисовываем.
     * */
    private void onClick(CardPanel clicked) {
        Cards.Card c2 = clicked.getCardModel();
        if (c2.getState() == Cards.Card.State.MATCHED || selected == clicked) return;

        if (selected != null) { // выбрана первая и вторая
            Cards.Card c1 = selected.getCardModel();
            CardPanel panel1 = selected;
            CardPanel panel2 = clicked;

            if (model.check(c1, c2)) {
                c1.setState(Cards.Card.State.MATCHED);
                c2.setState(Cards.Card.State.MATCHED);
                panel1.repaint();
                panel2.repaint();
                selected = null;
            } else {
                c1.setState(Cards.Card.State.WRONG);
                c2.setState(Cards.Card.State.WRONG);
                panel1.repaint();
                panel2.repaint();

                new Timer(1000, e -> {
                    if (c1.getState() != Cards.Card.State.MATCHED) {
                        c1.setState(Cards.Card.State.NORMAL);
                        panel1.repaint();
                    }
                    if (c2.getState() != Cards.Card.State.MATCHED) {
                        c2.setState(Cards.Card.State.NORMAL);
                        panel2.repaint();
                    }
                }).start();
                selected = null;
            }
        } else {
            selected = clicked;
            c2.setState(Cards.Card.State.SELECTED);
            clicked.repaint();
        }
    }
}