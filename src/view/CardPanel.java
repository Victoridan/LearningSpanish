package view;

import model.Cards;
import javax.swing.*;
import java.awt.*;

public class CardPanel extends JPanel {
    private final Cards.Card card; // ссылка на объект карточки из модели. Содержит: id пары, слово, состояние
    private static final Font FONT = new Font("SansSerif", Font.PLAIN, 30);


    /**
     * Конструктор карточки. Размер: 140 ширина, 60 высота..
     * Рамка: сплошная линия, цвет серый (GRAY), толщина 2 пикселя.
     * Курсор мыши при наведении: рука с указательным пальцем.
     */
    public CardPanel(Cards.Card card) {
        this.card = card;
        setPreferredSize(new Dimension(140, 60));
        setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    /**
     * Возвращает ссылку на объект данных модели, связанный с этой визуальной карточкой:
     * объект Cards.Card с полями: id, text, state.
     */
    public Cards.Card getCardModel() {
        return card;
    }

    /**
     * Рисует карточку. В switch определяет цвет. Отступ 2 пикселя по x и по y. Ширина и высота -4,
     * чтобы поместились, раз рамка по 2, скругление дуги с диаметром 10.
     * Меняем цвет отрисовки на черный, устанавливаем шрифт. Получаем текст слова из объекта модели
     * Рисуем строку, используя действующий цвет и шрифт.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // очищаем область компонента от предыдущих отрисовок
        Color bg = switch (card.getState()) {
            case SELECTED -> new Color(200, 220, 255);
            case CORRECT, MATCHED -> new Color(180, 255, 180);
            case WRONG -> new Color(255, 180, 180);
            default -> Color.WHITE;
        };

        g.setColor(bg);
        g.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 10, 10);

        g.setColor(Color.BLACK);
        g.setFont(FONT);
        String txt = card.getText();
        int x = (getWidth() - g.getFontMetrics().stringWidth(txt)) / 2; // в этой строчке и ниже ничего не понятно
        int y = getHeight() / 2 + g.getFontMetrics().getAscent() / 2;
        g.drawString(txt, x, y);
    }
}