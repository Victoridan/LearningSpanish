package view;

import javax.swing.*;
import java.awt.*;

/**
 * BoardPanel - игровое поле
 * GridLayout(0, 1, 10, 10):
 * 0 строк — количество строк определяется автоматически по количеству добавленных компонентов.
 * 1 колонка — все элементы в одну колонку.
 * 10, 10 — вертикальный и горизонтальный зазоры между элементами в 10 пикселей.
 */

public class BoardPanel extends JPanel {

    private final JPanel leftCol = new JPanel(new GridLayout(0, 1, 10, 10));
    private final JPanel rightCol = new JPanel(new GridLayout(0, 1, 10, 10));

    /**
     * Компоновка панели игрового поля. Компоновка 1 строка, 2 колонки, горизонтальный
     * зазор между колонками 40 пикселей, вертикальный 0. Рамка по 20 пикселей со всех сторон.
     * Добавляем колонки./.
     */
    public BoardPanel() {
        setLayout(new GridLayout(1, 2, 40, 0));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(leftCol);
        add(rightCol);
    }

    /**
     * Добавляем карточку, если левая, то в левую колонку, иначе в правуюj
     */
    public void addCard(CardPanel cp, boolean isLeft) {
        if (isLeft) leftCol.add(cp);
        else rightCol.add(cp);
    }
}

