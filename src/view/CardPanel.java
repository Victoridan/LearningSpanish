package view;
import model.Card;
import model.CardStateListener;
import model.State;
import javax.swing.*;
import java.awt.*;

public class CardPanel extends JPanel implements CardStateListener {
    private final Card card;
    private static final Font FONT = new Font("SansSerif", Font.PLAIN, 30);

    public CardPanel(Card card) {
        this.card = card;
        card.addListener(this);

        setPreferredSize(new Dimension(140, 60));
        setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public Card getCardModel() { return card; }

    @Override
    public void onStateChanged(Card card, State newState) {
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
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
        int x = (getWidth() - g.getFontMetrics().stringWidth(txt)) / 2;
        int y = getHeight() / 2 + g.getFontMetrics().getAscent() / 2;
        g.drawString(txt, x, y);
    }
}