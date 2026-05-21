import controller.GameController;
import model.GameModel;
import repository.LanguageManifest;
import repository.WordRepository;
import view.BoardPanel;
import view.LanguageSelectionDialog;
import view.MainFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String langKey = showLanguageDialog();
            if (langKey == null) System.exit(0);

            WordRepository repo = LanguageManifest.repositoryFor(langKey);
            GameModel model = new GameModel(repo, 8);
            BoardPanel board = new BoardPanel();
            MainFrame frame = new MainFrame(board);
            new GameController(model, frame, repo, 8);
            frame.setVisible(true);
        });
    }

    private static String showLanguageDialog() {
        LanguageSelectionDialog dialog = new LanguageSelectionDialog(null);
        dialog.setVisible(true);
        return dialog.getSelectedKey();
    }
}

