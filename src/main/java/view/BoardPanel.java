package view;

import javax.swing.*;
import java.awt.*;

public class BoardPanel extends JPanel {
    private final JPanel leftCol = new JPanel(new GridLayout(0, 1, 10, 10));
    private final JPanel rightCol = new JPanel(new GridLayout(0, 1, 10, 10));

    public BoardPanel() {
        setLayout(new GridLayout(1, 2, 40, 0));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(leftCol); add(rightCol);
    }

    public void addPair(CardPanel leftCp, CardPanel rightCp) {
        leftCol.add(leftCp);
        rightCol.add(rightCp);
        revalidate();
        repaint();
    }

    public void clearCards() {
        leftCol.removeAll();
        rightCol.removeAll();
        revalidate();
        repaint();
    }

    public void setCardSelected(model.Card card, boolean selected) {
        for (Component c : leftCol.getComponents()) {
            if (c instanceof CardPanel cp && cp.getCardModel() == card) {
                cp.setSelected(selected);
                return;
            }
        }
        for (Component c : rightCol.getComponents()) {
            if (c instanceof CardPanel cp && cp.getCardModel() == card) {
                cp.setSelected(selected);
                return;
            }
        }
    }

    public void setCardTransientWrong(model.Card card, boolean wrong) {
        for (Component c : leftCol.getComponents()) {
            if (c instanceof CardPanel cp && cp.getCardModel() == card) {
                cp.setTransientWrong(wrong);
                return;
            }
        }
        for (Component c : rightCol.getComponents()) {
            if (c instanceof CardPanel cp && cp.getCardModel() == card) {
                cp.setTransientWrong(wrong);
                return;
            }
        }
    }
}