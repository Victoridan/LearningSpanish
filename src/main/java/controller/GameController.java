package controller;

import model.Card;
import model.State;
import model.GameModel;
import model.SessionResult;
import repository.LanguageManifest;
import repository.WordRepository;
import view.CardPanel;
import view.LanguageSelectionDialog;
import view.MainFrame;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GameController {
    private final GameModel model;
    private final MainFrame view;
    private CardPanel selectedPanel;
    private WordRepository currentRepository;
    private int pairCount;

    public GameController(GameModel model, MainFrame view, WordRepository repository, int pairCount) {
        this.model = model;
        this.view = view;
        this.currentRepository = repository;
        this.pairCount = pairCount;

        for (Card c : model.getRussianCards()) addCard(c, true);
        for (Card c : model.getForeignCards()) addCard(c, false);
        updateResultDisplay();

        setupMenuActions();
    }

    private void setupMenuActions() {
        // "Новая игра" - выбрать язык и начать
        view.setNewGameListener(() -> chooseLanguageAndRestart());
        // "Перезапустить" - перезапустить сессию на текущем языке
        view.setRestartListener(() -> restartSameLanguage());
        view.setEndSessionListener(() -> endSession());
    }

    private void addCard(Card card, boolean left) {
        CardPanel panel = new CardPanel(card);
        view.getBoardPanel().addCard(panel, left);
        panel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { onClick(panel); }
        });
    }

    private void onClick(CardPanel clicked) {
        Card c2 = clicked.getCardModel();
        if (c2.getState() == State.MATCHED || selectedPanel == clicked) return;
        if (selectedPanel != null) {
            Card c1 = selectedPanel.getCardModel();
            if (model.check(c1, c2)) {
                c1.setState(State.MATCHED);
                c2.setState(State.MATCHED);
            } else {
                c1.setState(State.WRONG);
                c2.setState(State.WRONG);
                Timer t = new Timer(1000, e -> {
                    if (c1.getState() != State.MATCHED) c1.setState(State.NORMAL);
                    if (c2.getState() != State.MATCHED) c2.setState(State.NORMAL);
                });
                t.setRepeats(false);
                t.start();
            }
            selectedPanel = null;
            updateResultDisplay();
            // Проверяем, завершён ли раунд (все пары найдены)
            if (model.isRoundComplete()) {
                // Предложим продолжить (следующий раунд) или завершить сессию
                int opt = JOptionPane.showOptionDialog(view,
                        "Раунд завершён! Продолжить следующий раунд?",
                        "Раунд завершён",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        new Object[]{"Продолжить","Завершить"},
                        "Продолжить");

                if (opt == JOptionPane.YES_OPTION) {
                    // начать следующий раунд, не сбрасывая общую статистику
                    model.startNextRound();
                    selectedPanel = null;
                    view.getBoardPanel().clearCards();
                    for (Card c : model.getRussianCards()) addCard(c, true);
                    for (Card c : model.getForeignCards()) addCard(c, false);
                    updateResultDisplay();
                } else {
                    endSession();
                }
            }
        } else {
            selectedPanel = clicked;
            c2.setState(State.SELECTED);
        }
    }

    /**
     * Выбрать язык через диалоговое окно и перезапустить сессию.
     */
    public void chooseLanguageAndRestart() {
        LanguageSelectionDialog dialog = new LanguageSelectionDialog(view);
        dialog.setVisible(true);
        String langKey = dialog.getSelectedKey();
        if (langKey == null) return;

        startNewSession(langKey);
    }

    /**
     * Начать новую игровую сессию с указанным языком.
     */
    public void startNewSession(String langKey) {
        WordRepository newRepo = LanguageManifest.repositoryFor(langKey);
        this.currentRepository = newRepo;
        model.restartSession(newRepo);

        view.getBoardPanel().clearCards();

        for (Card c : model.getRussianCards()) addCard(c, true);
        for (Card c : model.getForeignCards()) addCard(c, false);
        updateResultDisplay();
    }

    /**
     * Перезапустить текущую сессию, сохранив выбранный язык/репозиторий.
     */
    public void restartSameLanguage() {
        if (currentRepository == null) {
            // Попробуем предложить выбор языка
            chooseLanguageAndRestart();
            return;
        }

        model.restartSession(currentRepository);
        view.getBoardPanel().clearCards();

        for (Card c : model.getRussianCards()) addCard(c, true);
        for (Card c : model.getForeignCards()) addCard(c, false);
        updateResultDisplay();
    }

    public void endSession() {
        SessionResult result = model.getSessionResult();
        String message = String.format("Сессия завершена!\nПравильно: %d\nНеправильно: %d",
                result.correct(), result.incorrect());
        JOptionPane.showMessageDialog(view, message, "Результаты", JOptionPane.INFORMATION_MESSAGE);
        // Закрыть окно и завершить приложение после показа результатов
        view.dispose();
        System.exit(0);
    }

    private void updateResultDisplay() {
        SessionResult result = model.getSessionResult();
        view.updateResults(result.correct(), result.incorrect());
    }
}
