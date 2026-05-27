package view;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;
import java.util.List;
import model.Card;
import model.SessionResult;
import repository.Language;

public class MainFrame extends JFrame {
    private final BoardPanel boardPanel;
    private final JLabel resultLabel;
    private final JMenuBar menuBar;
    private final JMenuItem newGameItem;
    private final JMenuItem endSessionItem;
    private final JMenuItem restartItem;
    private java.util.function.Consumer<Card> onCardClickedAction;

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

        gameMenu.add(newGameItem);
        gameMenu.add(restartItem);
        gameMenu.addSeparator();
        gameMenu.add(endSessionItem);
        menuBar.add(gameMenu);
        setJMenuBar(menuBar);

        add(resultLabel, BorderLayout.NORTH);
        add(boardPanel, BorderLayout.CENTER);
    }

    public void setOnCardClickedAction(java.util.function.Consumer<Card> action) {
        this.onCardClickedAction = action;
    }

    public void updateCards(List<Card> russianCards, List<Card> foreignCards) {
        boardPanel.clearCards();
        int size = Math.min(russianCards.size(), foreignCards.size());
        for (int i = 0; i < size; i++) {
            Card rc = russianCards.get(i);
            CardPanel leftCp = new CardPanel(rc);
            leftCp.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    if (onCardClickedAction != null) onCardClickedAction.accept(rc);
                }
            });

            Card fc = foreignCards.get(i);
            CardPanel rightCp = new CardPanel(fc);
            rightCp.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    if (onCardClickedAction != null) onCardClickedAction.accept(fc);
                }
            });

            boardPanel.addPair(leftCp, rightCp);
        }
    }

    public void refreshBoard() {
        boardPanel.repaint();
    }

    public Language showLanguageSelectionDialog(Collection<Language> availableLanguages) {
        LanguageSelectionDialog dialog = new LanguageSelectionDialog(this, availableLanguages);
        dialog.setVisible(true);
        return dialog.getSelectedLanguage();
    }

    public boolean showRoundCompletedPrompt() {
        int opt = JOptionPane.showOptionDialog(this,
                "Раунд завершён! Продолжить следующий раунд?",
                "Раунд завершён",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new Object[]{"Продолжить","Завершить"},
                "Продолжить");

        return opt == JOptionPane.YES_OPTION;
    }

    public void showSessionResult(SessionResult result) {
        String message = String.format("Сессия завершена!\nПравильно: %d\nНеправильно: %d",
                result.correct(), result.incorrect());
        JOptionPane.showMessageDialog(this, message, "Результаты", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showCardsMismatch(Card c1, Card c2) {
        boardPanel.setCardTransientWrong(c1, true);
        boardPanel.setCardTransientWrong(c2, true);
        Timer timer = new Timer(1000, e -> {
            boardPanel.setCardTransientWrong(c1, false);
            boardPanel.setCardTransientWrong(c2, false);
        });
        timer.setRepeats(false);
        timer.start();
    }

    public BoardPanel getBoardPanel() { return boardPanel; }

    public void setCardSelected(Card card, boolean selected) {
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