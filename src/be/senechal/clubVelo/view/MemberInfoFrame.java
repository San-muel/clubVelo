package be.senechal.clubVelo.view;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import be.senechal.clubVelo.model.Bike;
import be.senechal.clubVelo.model.Member;
import be.senechal.clubVelo.model.Category;

public class MemberInfoFrame extends JFrame {

    private Member member;
    private Runnable onCategoryAdded;

    private JTextField nameField;
    private JTextField firstNameField;
    private JTextField telField;
    private JLabel balanceLabel;
    private JTextArea categoryArea;
    private JList<Bike> bikeList;
    private DefaultListModel<Bike> bikeListModel;

    public MemberInfoFrame(Member member) {
        this(member, null);
    }

    /**
     * @wbp.parser.constructor
     */
    public MemberInfoFrame(Member member, Runnable onCategoryAdded) {
        this.member = member;
        this.onCategoryAdded = onCategoryAdded;

        setTitle("Mon Profil - " + member.getName());
        setSize(650, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // ----------------- PANEL INFO (CENTRE) -----------------
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridBagLayout());
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Nom
        GridBagConstraints gbcNomLabel = new GridBagConstraints();
        gbcNomLabel.fill = GridBagConstraints.HORIZONTAL;
        gbcNomLabel.insets = new Insets(5, 5, 5, 5);
        gbcNomLabel.gridx = 0;
        gbcNomLabel.gridy = 0;
        infoPanel.add(new JLabel("Nom :"), gbcNomLabel);

        nameField = new JTextField(member.getName());
        GridBagConstraints gbcNomField = new GridBagConstraints();
        gbcNomField.fill = GridBagConstraints.HORIZONTAL;
        gbcNomField.insets = new Insets(5, 5, 5, 5);
        gbcNomField.gridx = 1;
        gbcNomField.gridy = 0;
        gbcNomField.weightx = 1.0;
        infoPanel.add(nameField, gbcNomField);

        // Prénom
        GridBagConstraints gbcPrenomLabel = new GridBagConstraints();
        gbcPrenomLabel.fill = GridBagConstraints.HORIZONTAL;
        gbcPrenomLabel.insets = new Insets(5, 5, 5, 5);
        gbcPrenomLabel.gridx = 0;
        gbcPrenomLabel.gridy = 1;
        infoPanel.add(new JLabel("Prénom :"), gbcPrenomLabel);

        firstNameField = new JTextField(member.getFirstname());
        GridBagConstraints gbcPrenomField = new GridBagConstraints();
        gbcPrenomField.fill = GridBagConstraints.HORIZONTAL;
        gbcPrenomField.insets = new Insets(5, 5, 5, 5);
        gbcPrenomField.gridx = 1;
        gbcPrenomField.gridy = 1;
        gbcPrenomField.weightx = 1.0;
        infoPanel.add(firstNameField, gbcPrenomField);

        // Téléphone
        GridBagConstraints gbcTelLabel = new GridBagConstraints();
        gbcTelLabel.fill = GridBagConstraints.HORIZONTAL;
        gbcTelLabel.insets = new Insets(5, 5, 5, 5);
        gbcTelLabel.gridx = 0;
        gbcTelLabel.gridy = 2;
        infoPanel.add(new JLabel("Téléphone :"), gbcTelLabel);

        telField = new JTextField(member.getTel());
        GridBagConstraints gbcTelField = new GridBagConstraints();
        gbcTelField.fill = GridBagConstraints.HORIZONTAL;
        gbcTelField.insets = new Insets(5, 5, 5, 5);
        gbcTelField.gridx = 1;
        gbcTelField.gridy = 2;
        gbcTelField.weightx = 1.0;
        infoPanel.add(telField, gbcTelField);

        // Balance
        GridBagConstraints gbcBalanceLabel = new GridBagConstraints();
        gbcBalanceLabel.fill = GridBagConstraints.HORIZONTAL;
        gbcBalanceLabel.insets = new Insets(5, 5, 5, 5);
        gbcBalanceLabel.gridx = 0;
        gbcBalanceLabel.gridy = 3;
        infoPanel.add(new JLabel("Balance (€) :"), gbcBalanceLabel);

        balanceLabel = new JLabel(String.format("%.2f", member.getBalance()));
        GridBagConstraints gbcBalanceValue = new GridBagConstraints();
        gbcBalanceValue.fill = GridBagConstraints.HORIZONTAL;
        gbcBalanceValue.insets = new Insets(5, 5, 5, 5);
        gbcBalanceValue.gridx = 1;
        gbcBalanceValue.gridy = 3;
        gbcBalanceValue.weightx = 1.0;
        infoPanel.add(balanceLabel, gbcBalanceValue);

        // Catégories
        GridBagConstraints gbcCatLabel = new GridBagConstraints();
        gbcCatLabel.fill = GridBagConstraints.HORIZONTAL;
        gbcCatLabel.insets = new Insets(5, 5, 5, 5);
        gbcCatLabel.gridx = 0;
        gbcCatLabel.gridy = 4;
        gbcCatLabel.anchor = GridBagConstraints.NORTHWEST;
        infoPanel.add(new JLabel("Catégories :"), gbcCatLabel);

        JPanel catPanel = new JPanel(new BorderLayout(5, 5));

        categoryArea = new JTextArea(4, 20);
        categoryArea.setEditable(false);
        refreshCategoryDisplay();

        catPanel.add(new JScrollPane(categoryArea), BorderLayout.CENTER);

        JButton addCatBtn = new JButton("Ajouter une catégorie...");
        addCatBtn.addActionListener(e -> addCategory());
        catPanel.add(addCatBtn, BorderLayout.SOUTH);

        GridBagConstraints gbcCatPanel = new GridBagConstraints();
        gbcCatPanel.insets = new Insets(5, 5, 5, 5);
        gbcCatPanel.gridx = 1;
        gbcCatPanel.gridy = 4;
        gbcCatPanel.weightx = 1.0;
        gbcCatPanel.anchor = GridBagConstraints.NORTHWEST;
        gbcCatPanel.fill = GridBagConstraints.BOTH;
        infoPanel.add(catPanel, gbcCatPanel);

        // Vélos
        GridBagConstraints gbcVelosLabel = new GridBagConstraints();
        gbcVelosLabel.insets = new Insets(5, 5, 5, 5);
        gbcVelosLabel.gridx = 0;
        gbcVelosLabel.gridy = 5;
        gbcVelosLabel.anchor = GridBagConstraints.NORTHWEST;
        gbcVelosLabel.fill = GridBagConstraints.HORIZONTAL;
        infoPanel.add(new JLabel("Mes vélos :"), gbcVelosLabel);

        JPanel bikePanel = new JPanel(new BorderLayout(5, 5));
        bikeListModel = new DefaultListModel<>();
        refreshBikeList();
        bikeList = new JList<>(bikeListModel);
        bikePanel.add(new JScrollPane(bikeList), BorderLayout.CENTER);

        JButton removeBikeBtn = new JButton("Supprimer ce vélo");
        removeBikeBtn.addActionListener(e -> removeSelectedBike());
        bikePanel.add(removeBikeBtn, BorderLayout.SOUTH);

        GridBagConstraints gbcBikePanel = new GridBagConstraints();
        gbcBikePanel.insets = new Insets(5, 5, 5, 5);
        gbcBikePanel.gridx = 1;
        gbcBikePanel.gridy = 5;
        gbcBikePanel.weightx = 1.0;
        gbcBikePanel.anchor = GridBagConstraints.NORTHWEST;
        gbcBikePanel.fill = GridBagConstraints.BOTH;
        infoPanel.add(bikePanel, gbcBikePanel);

        add(infoPanel, BorderLayout.CENTER);

        // ----------------- PANEL BOUTONS (BAS) -----------------
        JPanel buttonPanel = new JPanel();

        // 1. Bouton Ajouter Vélo
        JButton addBikeBtn = new JButton("Ajouter un vélo");
        addBikeBtn.addActionListener(e -> {
            new CreateBikeFrame(member, this::refreshBikeList);
        });
        buttonPanel.add(addBikeBtn);

        // 2. Bouton Sauvegarder
        JButton modifyBtn = new JButton("Sauvegarder Infos");
        modifyBtn.addActionListener(e -> updateMemberInfo());
        buttonPanel.add(modifyBtn);

        // 4. Bouton Fermer
        JButton closeBtn = new JButton("Fermer");
        closeBtn.addActionListener(e -> dispose());
        buttonPanel.add(closeBtn);

        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void refreshCategoryDisplay() {
        StringBuilder sb = new StringBuilder();
        if (member.getCategories().isEmpty()) {
            sb.append("Aucune catégorie.");
        } else {
            for (Category cat : member.getCategories()) {
                sb.append("- ").append(cat.getClass().getSimpleName())
                  .append(" (N°").append(cat.getId()).append(")")
                  .append("\n");
            }
        }
        categoryArea.setText(sb.toString());
    }

    private void refreshBikeList() {
        bikeListModel.clear();
        for (Bike b : member.getBikes()) {
            bikeListModel.addElement(b);
        }
    }

    private void removeSelectedBike() {
        Bike selected = bikeList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un vélo à supprimer.");
            return;
        }
        boolean success = selected.delete();
        if (success) {
            member.getBikes().remove(selected);
            refreshBikeList();
            JOptionPane.showMessageDialog(this, "Vélo supprimé.");
        } else {
            JOptionPane.showMessageDialog(this, "Erreur lors de la suppression.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addCategory() {
        List<Category> allCategories = Category.getAllCategory();
        List<Category> memberCategories = member.getCategories();
        List<Integer> memberCategoryIds = new ArrayList<>();

        for(Category c : memberCategories) {
            memberCategoryIds.add(c.getId());
        }

        List<Category> availableCategories = new ArrayList<>();
        for (Category c : allCategories) {
            if (!memberCategoryIds.contains(c.getId())) {
                availableCategories.add(c);
            }
        }

        if (availableCategories.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vous possédez déjà toutes les catégories disponibles !");
            return;
        }

        Object[] choices = availableCategories.stream()
                .map(c -> c.getClass().getSimpleName() + " (N°" + c.getId() + ")")
                .toArray();

        String selectedStr = (String) JOptionPane.showInputDialog(
                this,
                "Choisissez une catégorie à ajouter :",
                "Ajouter une catégorie",
                JOptionPane.QUESTION_MESSAGE,
                null,
                choices,
                choices[0]
        );

        if (selectedStr != null) {
            Category selectedCat = availableCategories.stream()
                    .filter(c -> (c.getClass().getSimpleName() + " (N°" + c.getId() + ")").equals(selectedStr))
                    .findFirst()
                    .orElse(null);

            if (selectedCat != null) {
                try {
                    boolean success = member.addCategory(selectedCat);

                    if (success) {
                        JOptionPane.showMessageDialog(this, "Catégorie ajoutée avec succès !");
                        refreshCategoryDisplay();
                        if (onCategoryAdded != null) {
                            onCategoryAdded.run();
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout en base de données.");
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Erreur technique : " + ex.getMessage());
                }
            }
        }
    }

    private void updateMemberInfo() {
        member.setName(nameField.getText());
        member.setFirstname(firstNameField.getText());
        member.setTel(telField.getText());

        boolean success = member.updateInfo();
        if (success) {
            JOptionPane.showMessageDialog(this, "Infos mises à jour !");
        } else {
            JOptionPane.showMessageDialog(this, "Erreur lors de la sauvegarde en base de données.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

}
