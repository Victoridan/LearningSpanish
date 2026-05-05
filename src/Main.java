import controller.GameController;
import model.Cards;
import model.GameModel;
import view.BoardPanel;
import view.MainFrame;

import javax.swing.*;
import java.util.List;
//.
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            List<Cards.Card> cards = Cards.getRandomPairs(8);
            GameModel model = new GameModel(cards);
            BoardPanel boardPanel = new BoardPanel();
            MainFrame frame = new MainFrame(boardPanel);
            new GameController(model, frame);
            frame.setVisible(true);
        });
    }
}