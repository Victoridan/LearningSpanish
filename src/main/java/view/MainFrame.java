package view;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private final BoardPanel boardPanel;
    private final JLabel resultLabel;
    private final JMenuBar menuBar;
    private final JMenuItem newGameItem;
    private final JMenuItem endSessionItem;
    private final JMenuItem restartItem;

    public MainFrame(BoardPanel boardPanel) {
        this.boardPanel = boardPanel;
        setTitle("Сопоставление слов");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        resultLabel = new JLabel("Правильно: 0 | Неправильно: 0", SwingConstants.CENTER);
        resultLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));

        menuBar = new JMenuBar();
        JMenu gameMenu = new JMenu("Игра");

        newGameItem = new JMenuItem("Новая игра");
        restartItem = new JMenuItem("Перезапустить");
        endSessionItem = new JMenuItem("Завершить сессию");

        // Порядок меню: новая игра (начать с выбора языка), перезапустить (тек. язык), завершить
        gameMenu.add(newGameItem);
        gameMenu.add(restartItem);
        gameMenu.addSeparator();
        gameMenu.add(endSessionItem);
        menuBar.add(gameMenu);
        setJMenuBar(menuBar);

        add(resultLabel, BorderLayout.NORTH);
        add(boardPanel, BorderLayout.CENTER);
    }

    public BoardPanel getBoardPanel() { return boardPanel; }

    public void updateResults(int correct, int incorrect) {
        resultLabel.setText("Правильно: " + correct + " | Неправильно: " + incorrect);
    }

    public void setNewGameListener(Runnable action) {
        newGameItem.addActionListener(e -> action.run());
    }

    public void setEndSessionListener(Runnable action) {
        endSessionItem.addActionListener(e -> action.run());
    }

    public void setRestartListener(Runnable action) {
        restartItem.addActionListener(e -> action.run());
    }
}
