package be.senechal.clubVelo.view;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import be.senechal.clubVelo.model.Category;
import be.senechal.clubVelo.model.Member;

public class CreateMemberFrame extends JFrame {

    private JTextField nameField;
    private JTextField firstnameField;
    private JTextField telField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;

    private List<JCheckBox> categoryCheckBoxes = new ArrayList<>();
    private List<Category> allCategories;

    public CreateMemberFrame() {
        setTitle("Inscription - Nouveau Membre");
        setSize(500, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Créer un compte", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

        GridBagConstraints gbcNomLabel = new GridBagConstraints();
        gbcNomLabel.insets = new Insets(8, 5, 8, 5);
        gbcNomLabel.fill = GridBagConstraints.HORIZONTAL;
        gbcNomLabel.gridx = 0;
        gbcNomLabel.gridy = 0;
        formPanel.add(new JLabel("Nom :"), gbcNomLabel);

        nameField = new JTextField();
        GridBagConstraints gbcNomField = new GridBagConstraints();
        gbcNomField.insets = new Insets(8, 5, 8, 5);
        gbcNomField.fill = GridBagConstraints.HORIZONTAL;
        gbcNomField.gridx = 1;
        gbcNomField.gridy = 0;
        gbcNomField.weightx = 1.0;
        formPanel.add(nameField, gbcNomField);

        GridBagConstraints gbcPrenomLabel = new GridBagConstraints();
        gbcPrenomLabel.insets = new Insets(8, 5, 8, 5);
        gbcPrenomLabel.fill = GridBagConstraints.HORIZONTAL;
        gbcPrenomLabel.gridx = 0;
        gbcPrenomLabel.gridy = 1;
        formPanel.add(new JLabel("Prénom :"), gbcPrenomLabel);

        firstnameField = new JTextField();
        GridBagConstraints gbcPrenomField = new GridBagConstraints();
        gbcPrenomField.insets = new Insets(8, 5, 8, 5);
        gbcPrenomField.fill = GridBagConstraints.HORIZONTAL;
        gbcPrenomField.gridx = 1;
        gbcPrenomField.gridy = 1;
        gbcPrenomField.weightx = 1.0;
        formPanel.add(firstnameField, gbcPrenomField);

        GridBagConstraints gbcTelLabel = new GridBagConstraints();
        gbcTelLabel.insets = new Insets(8, 5, 8, 5);
        gbcTelLabel.fill = GridBagConstraints.HORIZONTAL;
        gbcTelLabel.gridx = 0;
        gbcTelLabel.gridy = 2;
        formPanel.add(new JLabel("Téléphone :"), gbcTelLabel);

        telField = new JTextField();
        GridBagConstraints gbcTelField = new GridBagConstraints();
        gbcTelField.insets = new Insets(8, 5, 8, 5);
        gbcTelField.fill = GridBagConstraints.HORIZONTAL;
        gbcTelField.gridx = 1;
        gbcTelField.gridy = 2;
        gbcTelField.weightx = 1.0;
        formPanel.add(telField, gbcTelField);

        GridBagConstraints gbcSeparator = new GridBagConstraints();
        gbcSeparator.insets = new Insets(8, 5, 8, 5);
        gbcSeparator.fill = GridBagConstraints.HORIZONTAL;
        gbcSeparator.gridx = 0;
        gbcSeparator.gridy = 3;
        gbcSeparator.gridwidth = 2;
        gbcSeparator.weightx = 1.0;
        formPanel.add(new JSeparator(), gbcSeparator);

        GridBagConstraints gbcUsernameLabel = new GridBagConstraints();
        gbcUsernameLabel.insets = new Insets(8, 5, 8, 5);
        gbcUsernameLabel.fill = GridBagConstraints.HORIZONTAL;
        gbcUsernameLabel.gridx = 0;
        gbcUsernameLabel.gridy = 4;
        formPanel.add(new JLabel("Nom d'utilisateur :"), gbcUsernameLabel);

        usernameField = new JTextField();
        GridBagConstraints gbcUsernameField = new GridBagConstraints();
        gbcUsernameField.insets = new Insets(8, 5, 8, 5);
        gbcUsernameField.fill = GridBagConstraints.HORIZONTAL;
        gbcUsernameField.gridx = 1;
        gbcUsernameField.gridy = 4;
        gbcUsernameField.weightx = 1.0;
        formPanel.add(usernameField, gbcUsernameField);

        GridBagConstraints gbcPasswordLabel = new GridBagConstraints();
        gbcPasswordLabel.insets = new Insets(8, 5, 8, 5);
        gbcPasswordLabel.fill = GridBagConstraints.HORIZONTAL;
        gbcPasswordLabel.gridx = 0;
        gbcPasswordLabel.gridy = 5;
        formPanel.add(new JLabel("Mot de passe :"), gbcPasswordLabel);

        passwordField = new JPasswordField();
        GridBagConstraints gbcPasswordField = new GridBagConstraints();
        gbcPasswordField.insets = new Insets(8, 5, 8, 5);
        gbcPasswordField.fill = GridBagConstraints.HORIZONTAL;
        gbcPasswordField.gridx = 1;
        gbcPasswordField.gridy = 5;
        gbcPasswordField.weightx = 1.0;
        formPanel.add(passwordField, gbcPasswordField);

        GridBagConstraints gbcConfirmLabel = new GridBagConstraints();
        gbcConfirmLabel.insets = new Insets(8, 5, 8, 5);
        gbcConfirmLabel.fill = GridBagConstraints.HORIZONTAL;
        gbcConfirmLabel.gridx = 0;
        gbcConfirmLabel.gridy = 6;
        formPanel.add(new JLabel("Confirmer MDP :"), gbcConfirmLabel);

        confirmPasswordField = new JPasswordField();
        GridBagConstraints gbcConfirmField = new GridBagConstraints();
        gbcConfirmField.insets = new Insets(8, 5, 8, 5);
        gbcConfirmField.fill = GridBagConstraints.HORIZONTAL;
        gbcConfirmField.gridx = 1;
        gbcConfirmField.gridy = 6;
        gbcConfirmField.weightx = 1.0;
        formPanel.add(confirmPasswordField, gbcConfirmField);

        JLabel catLabel = new JLabel("Choisissez vos catégories (Min. 1) :");
        catLabel.setFont(new Font("Arial", Font.BOLD, 12));
        GridBagConstraints gbcCatLabel = new GridBagConstraints();
        gbcCatLabel.insets = new Insets(20, 5, 5, 5);
        gbcCatLabel.fill = GridBagConstraints.HORIZONTAL;
        gbcCatLabel.gridx = 0;
        gbcCatLabel.gridy = 7;
        gbcCatLabel.gridwidth = 2;
        gbcCatLabel.weightx = 1.0;
        formPanel.add(catLabel, gbcCatLabel);

        JPanel checkBoxPanel = new JPanel();
        checkBoxPanel.setLayout(new BoxLayout(checkBoxPanel, BoxLayout.Y_AXIS));

        allCategories = Category.getAllCategory();
        for (Category cat : allCategories) {
            JCheckBox cb = new JCheckBox(cat.getClass().getSimpleName());
            categoryCheckBoxes.add(cb);
            checkBoxPanel.add(cb);
        }

        JScrollPane scrollPane = new JScrollPane(checkBoxPanel);
        scrollPane.setPreferredSize(new Dimension(0, 100));
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        GridBagConstraints gbcScrollPane = new GridBagConstraints();
        gbcScrollPane.insets = new Insets(20, 5, 5, 5);
        gbcScrollPane.fill = GridBagConstraints.HORIZONTAL;
        gbcScrollPane.gridx = 0;
        gbcScrollPane.gridy = 8;
        gbcScrollPane.gridwidth = 2;
        gbcScrollPane.weightx = 1.0;
        formPanel.add(scrollPane, gbcScrollPane);

        add(formPanel, BorderLayout.CENTER);

        // Boutons
        JPanel btnPanel = new JPanel();
        JButton saveBtn = new JButton("S'inscrire");
        saveBtn.setBackground(new Color(60, 179, 113));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.addActionListener(e -> registerMember());

        JButton cancelBtn = new JButton("Annuler");
        cancelBtn.addActionListener(e -> dispose());

        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);
        add(btnPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void registerMember() {
        String name = nameField.getText().trim();
        String firstname = firstnameField.getText().trim();
        String tel = telField.getText().trim();
        String username = usernameField.getText().trim();
        String pwd = new String(passwordField.getPassword());
        String pwdConfirm = new String(confirmPasswordField.getPassword());

        List<Category> selectedCategories = new ArrayList<>();
        for (int i = 0; i < categoryCheckBoxes.size(); i++) {
            if (categoryCheckBoxes.get(i).isSelected()) {
                selectedCategories.add(allCategories.get(i));
            }
        }

        String validationError = Member.validateRegistration(name, firstname, username, pwd, pwdConfirm, selectedCategories);
        if (validationError != null) {
            JOptionPane.showMessageDialog(this, validationError, "Attention", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (Member.usernameExists(username)) {
            JOptionPane.showMessageDialog(this, "Ce nom d'utilisateur est déjà utilisé.", "Attention", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Member newMember = new Member(name, firstname, tel, pwd, username);
        newMember.setCategories(selectedCategories);
        try {
            boolean success = Member.addMember(newMember);

            if (success) {
                JOptionPane.showMessageDialog(this, "Inscription réussie !");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Erreur lors de l'inscription.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur technique : " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
