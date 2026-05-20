package controller;
import model.Card;
import model.State;
import model.GameModel;
import view.CardPanel;
import view.MainFrame;
import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GameController {
    private final GameModel model;
    private final MainFrame view;
    private CardPanel selectedPanel;

    public GameController(GameModel model, MainFrame view) {
        this.model = model;
        this.view = view;
        for (Card c : model.getRussianCards()) addCard(c, true);
        for (Card c : model.getForeignCards()) addCard(c, false);
    }

    private void addCard(Card card, boolean left) {
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
        Card c2 = clicked.getCardModel();
        if (c2.getState() == State.MATCHED || selectedPanel == clicked) return;

        if (selectedPanel != null) {
            Card c1 = selectedPanel.getCardModel();

            if (model.check(c1, c2)) {
                c1.setState(State.MATCHED);
                c2.setState(State.MATCHED);
            } else {
                c1.setState(State.WRONG);
                c2.setState(State.WRONG);
                // Таймер сбрасывает состояние, если пара не угадана
                new Timer(1000, e -> {
                    if (c1.getState() != State.MATCHED) c1.setState(State.NORMAL);
                    if (c2.getState() != State.MATCHED) c2.setState(State.NORMAL);
                }).start();
            }
            selectedPanel = null;
        } else {
            selectedPanel = clicked;
            c2.setState(State.SELECTED);
        }
    }
}