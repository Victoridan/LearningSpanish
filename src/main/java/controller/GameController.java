package controller;

import model.Card;
import model.GameModel;
import model.GameStateListener;
import model.SessionResult;
import repository.Language;
import view.MainFrame;
import java.util.List;

public class GameController implements GameStateListener {
    private final GameModel model;
    private final MainFrame view;

    public GameController(GameModel model, MainFrame view) {
        this.model = model;
        this.view = view;

        model.addListener(this);
        view.setOnCardClickedAction(this::onCardClicked);
        setupMenuActions();

        view.updateCards(model.getRussianCards(), model.getForeignCards());
        view.updateResults(0, 0);
    }

    private void setupMenuActions() {
        view.setNewGameListener(this::chooseLanguageAndRestart);
        view.setRestartListener(this::restartSameLanguage);
        view.setEndSessionListener(model::endSession);
    }

    private void onCardClicked(Card clickedCard) {
        model.selectCard(clickedCard);
    }

    public void chooseLanguageAndRestart() {
        Language lang = view.showLanguageSelectionDialog(model.getAvailableLanguages());
        if (lang == null) return;

        model.startSession(lang);
    }

    public void restartSameLanguage() {
        model.startSession(model.getCurrentLanguage());
    }

    @Override
    public void onCardsDealt(List<Card> russianCards, List<Card> foreignCards) {
        view.updateCards(russianCards, foreignCards);
    }

    @Override
    public void onCardStateChanged(Card card) {
        view.refreshBoard();
    }

    @Override
    public void onScoreUpdated(int correct, int incorrect) {
        view.updateResults(correct, incorrect);
    }

    @Override
    public void onRoundCompleted() {
        if (view.showRoundCompletedPrompt()) {
            model.startNextRound();
        } else {
            model.endSession();
        }
    }

    @Override
    public void onSessionCompleted(SessionResult result) {
        view.showSessionResult(result);
        chooseLanguageAndRestart();
    }

    @Override
    public void onCardSelectionChanged(Card card, boolean isSelected) {
        view.setCardSelected(card, isSelected);
    }

    @Override
    public void onCardsMismatch(Card c1, Card c2) {
        view.showCardsMismatch(c1, c2);
    }
}