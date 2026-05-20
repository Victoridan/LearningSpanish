package view;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private final BoardPanel boardPanel;

    public MainFrame(BoardPanel boardPanel) {
        this.boardPanel = boardPanel;
        setTitle("Сопоставление слов");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        add(boardPanel, BorderLayout.CENTER);
    }

    public BoardPanel getBoardPanel() { return boardPanel; }
}