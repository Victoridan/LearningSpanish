package view;

import model.Card;
import javax.swing.*;
import java.awt.*;

public class CardPanel extends JPanel {
    private final Card card;
    private boolean selected = false;
    private boolean transientWrong = false; // ПРАВКА 9: Визуальный флаг ошибки
    private static final Font FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 20);

    public CardPanel(Card card) {
        this.card = card;

        setPreferredSize(new Dimension(140, 60));
        setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public Card getCardModel() { return card; }

    public void setSelected(boolean selected) {
        this.selected = selected;
        repaint();
    }

    public void setTransientWrong(boolean wrong) {
        this.transientWrong = wrong;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Color bg;
        if (selected) {
            bg = new Color(200, 220, 255);
        } else if (transientWrong) {
            bg = new Color(255, 180, 180);
        } else {
            bg = switch (card.getState()) {
                case MATCHED -> new Color(180, 255, 180);
                default -> Color.WHITE;
            };
        }
        g.setColor(bg);
        g.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 15, 15);

        g.setColor(Color.BLACK);
        g.setFont(FONT);
        FontMetrics fm = g.getFontMetrics();
        String text = card.getText();
        int x = (getWidth() - fm.stringWidth(text)) / 2;
        int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
        g.drawString(text, x, y);
    }
}