package view;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MainFrame extends JFrame implements model.GameStateListener {
    private final BoardPanel boardPanel;
    private final JLabel resultLabel;
    private final JMenuBar menuBar;
    private final JMenuItem newGameItem;
    private final JMenuItem endSessionItem;
    private final JMenuItem restartItem;
    private Runnable onRoundCompletedAction;
    private Runnable onSessionCompletedAction;
    private java.util.function.Consumer<model.Card> onCardClickedAction;

    public MainFrame(BoardPanel boardPanel) {
        this.boardPanel = boardPanel;
        setTitle("Сопоставление слов");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        resultLabel = new JLabel("Правильно: 0 | Неправильно: 0", SwingConstants.CENTER);
        resultLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));

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
    
    public void setOnRoundCompletedAction(Runnable action) {
        this.onRoundCompletedAction = action;
    }
    
    public void setOnSessionCompletedAction(Runnable action) {
        this.onSessionCompletedAction = action;
    }
    
    public void setOnCardClickedAction(java.util.function.Consumer<model.Card> action) {
        this.onCardClickedAction = action;
    }

    @Override
    public void onCardsDealt(List<model.Card> russianCards, List<model.Card> foreignCards) {
        boardPanel.clearCards();
        int size = Math.min(russianCards.size(), foreignCards.size());
        for (int i = 0; i < size; i++) {
            model.Card rc = russianCards.get(i);
            CardPanel leftCp = new CardPanel(rc);
            leftCp.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent e) { 
                    if (onCardClickedAction != null) onCardClickedAction.accept(rc); 
                }
            });

            model.Card fc = foreignCards.get(i);
            CardPanel rightCp = new CardPanel(fc);
            rightCp.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent e) { 
                    if (onCardClickedAction != null) onCardClickedAction.accept(fc); 
                }
            });

            boardPanel.addPair(leftCp, rightCp);
        }
    }

    @Override
    public void onCardStateChanged(model.Card card) {
        boardPanel.repaint();
    }

    @Override
    public void onScoreUpdated(int correct, int incorrect) {
        updateResults(correct, incorrect);
    }

    @Override
    public void onRoundCompleted() {
        int opt = JOptionPane.showOptionDialog(this,
                "Раунд завершён! Продолжить следующий раунд?",
                "Раунд завершён",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new Object[]{"Продолжить","Завершить"},
                "Продолжить");

        if (opt == JOptionPane.YES_OPTION) {
            if (onRoundCompletedAction != null) onRoundCompletedAction.run();
        } else {
            if (onSessionCompletedAction != null) onSessionCompletedAction.run();
        }
    }

    @Override
    public void onSessionCompleted(model.SessionResult result) {
        String message = String.format("Сессия завершена!\nПравильно: %d\nНеправильно: %d",
                result.correct(), result.incorrect());
        JOptionPane.showMessageDialog(this, message, "Результаты", JOptionPane.INFORMATION_MESSAGE);
        dispose();
        System.exit(0);
    }

    public BoardPanel getBoardPanel() { return boardPanel; }
    
    public void setCardSelected(model.Card card, boolean selected) {
        boardPanel.setCardSelected(card, selected);
    }

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
