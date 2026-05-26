import controller.GameController;
import model.GameModel;
import repository.Language;
import repository.WordRepository;
import view.BoardPanel;
import view.LanguageSelectionDialog;
import view.MainFrame;
import repository.WordRepositoryFactory;

import javax.swing.*;
import java.util.Map;
import java.util.logging.Logger;
import java.util.logging.Level;

public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Map<Language, WordRepository> langs = WordRepositoryFactory.getLanguages();
            Language lang = showLanguageDialog(langs);
            if (lang == null) {
                LOGGER.log(Level.INFO, "Язык не выбран. Выход.");
                return;
            }

            WordRepository repo = langs.get(lang);
            GameModel model = new GameModel(repo, 8);
            BoardPanel board = new BoardPanel();
            MainFrame frame = new MainFrame(board);
            new GameController(model, frame, repo, langs);
            frame.setVisible(true);
        });
    }

    private static Language showLanguageDialog(Map<Language, WordRepository> langs) {
        LanguageSelectionDialog dialog = new LanguageSelectionDialog(null, langs);
        dialog.setVisible(true);
        return dialog.getSelectedLanguage();
    }
}
