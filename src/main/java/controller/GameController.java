package controller;

import model.Card;
import model.State;
import model.GameModel;
import model.SessionResult;
import repository.WordRepository;
import repository.Language;
import view.CardPanel;
import view.LanguageSelectionDialog;
import view.MainFrame;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;

public class GameController {
    private final GameModel model;
    private final MainFrame view;
    private WordRepository currentRepository;
    private final Map<Language, WordRepository> availableLanguages;
    private Card selected;

    public GameController(GameModel model, MainFrame view, WordRepository repository, Map<Language, WordRepository> availableLanguages) {
        this.model = model;
        this.view = view;
        this.currentRepository = repository;
        this.availableLanguages = availableLanguages;

        // Model events -> View
        model.addListener(view);

        // View events -> Controller -> Model
        view.setOnCardClickedAction(this::onCardClicked);
        view.setOnRoundCompletedAction(model::startNextRound);
        view.setOnSessionCompletedAction(model::endSession);

        setupMenuActions();

        // Initialize view with currently dealt cards (since initialized in model constructor)
        view.onCardsDealt(model.getRussianCards(), model.getForeignCards());
        view.updateResults(0, 0);
    }

    private void setupMenuActions() {
        // "Новая игра" - выбрать язык и начать
        view.setNewGameListener(this::chooseLanguageAndRestart);
        // "Перезапустить" - перезапустить сессию на текущем языке
        view.setRestartListener(this::restartSameLanguage);
        view.setEndSessionListener(model::endSession);
    }

    private void onCardClicked(Card clickedCard) {
        if (clickedCard.getState() == State.MATCHED || selected == clickedCard) return;
        
        if (selected != null) {
            Card c1 = selected;
            Card c2 = clickedCard;
            boolean isMatch = model.check(c1, c2); // Model triggers events, view repaints
            
            if (!isMatch) {
                Timer t = new Timer(1000, e -> {
                    model.resetWrongState(c1, c2);
                });
                t.setRepeats(false);
                t.start();
            }
            view.setCardSelected(selected, false);
            selected = null;
        } else {
            selected = clickedCard;
            view.setCardSelected(selected, true);
        }
    }

    /**
     * Выбрать язык через диалоговое окно и перезапустить сессию.
     */
    public void chooseLanguageAndRestart() {
        LanguageSelectionDialog dialog = new LanguageSelectionDialog(view, availableLanguages);
        dialog.setVisible(true);
        Language lang = dialog.getSelectedLanguage();
        if (lang == null) return;
        
        startNewSession(availableLanguages.get(lang));
    }

    /**
     * Начать новую игровую сессию с выбранным репозиторием.
     */
    public void startNewSession(WordRepository newRepo) {
        this.currentRepository = newRepo;
        selected = null;
        model.startSession(newRepo); // Model deals cards and triggers View
    }

    /**
     * Перезапустить текущую сессию, сохранив выбранный язык/репозиторий.
     */
    public void restartSameLanguage() {
        if (currentRepository == null) {
            chooseLanguageAndRestart();
            return;
        }
        selected = null;
        model.startSession(currentRepository);
    }
}
