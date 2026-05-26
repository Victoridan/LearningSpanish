package view;

import javax.swing.*;
import java.awt.*;
import repository.Language;

public class LanguageSelectionDialog extends JDialog {
    private Language selectedLanguage = null;

    public LanguageSelectionDialog(Window owner, java.util.Map<Language, repository.WordRepository> langs) {
        super(owner, "Выберите язык", ModalityType.APPLICATION_MODAL);

        if (langs.isEmpty()) {
            return;
        }

        JComboBox<Language> combo = new JComboBox<>(langs.keySet().toArray(new Language[0]));
        JButton btnOk = new JButton("Начать");
        JButton btnCancel = new JButton("Отмена");

        btnOk.addActionListener(e -> {
            selectedLanguage = (Language) combo.getSelectedItem();
            dispose();
        });

        btnCancel.addActionListener(e -> {
            selectedLanguage = null;
            dispose();
        });

        JPanel center = new JPanel(new BorderLayout(10, 0));
        center.add(new JLabel("Язык:"), BorderLayout.WEST);
        center.add(combo, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        bottom.add(btnOk);
        bottom.add(btnCancel);

        setLayout(new BorderLayout(15, 10));
        add(center, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(owner);
        setResizable(false);
    }

    public Language getSelectedLanguage() {
        return selectedLanguage;
    }
}

