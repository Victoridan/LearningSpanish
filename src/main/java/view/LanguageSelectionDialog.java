package view;

import repository.LanguageManifest;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class LanguageSelectionDialog extends JDialog {
    private String selectedKey = null;

    /**
     * Получаем массив со всеми доступными языками, создаем выпадающий список
     * с названиями языков.
     */
    public LanguageSelectionDialog(Window owner) {
        super(owner, "Выберите язык", ModalityType.APPLICATION_MODAL);

        Map<String, String> langs = LanguageManifest.getLanguages();
        if (langs.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Файлы словарей не найдены в resources/", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JComboBox<String> combo = new JComboBox<>(langs.values().toArray(new String[0]));
        JButton btnOk = new JButton("Начать");
        JButton btnCancel = new JButton("Отмена");

        btnOk.addActionListener(e -> {
            String chosenName = (String) combo.getSelectedItem();
            selectedKey = findKey(langs, chosenName);
            dispose();
        });

        btnCancel.addActionListener(e -> {
            selectedKey = null;
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

    private String findKey(Map<String, String> map, String name) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (entry.getValue().equals(name)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public String getSelectedKey() {
        return selectedKey;
    }
}

