package be.senechal.clubVelo.view;

import javax.swing.*;
import java.awt.*;
import be.senechal.clubVelo.model.*;

public class CreateBikeFrame extends JFrame {

    private Member loggedUser;
    private Runnable onBikeAdded;
    private JTextField typeField;
    private JSpinner lengthSpinner;
    private JSpinner weightSpinner;

    public CreateBikeFrame(Member member) {
        this(member, null);
    }

    /**
     * @wbp.parser.constructor
     */
    public CreateBikeFrame(Member member, Runnable onBikeAdded) {
        this.loggedUser = member;
        this.onBikeAdded = onBikeAdded;

        setTitle("Ajouter un vélo");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // ---------- TITRE ----------
        JLabel titleLabel = new JLabel("Nouveau Vélo", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        // ---------- FORMULAIRE ----------
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        GridBagConstraints gbcTypeLabel = new GridBagConstraints();
        gbcTypeLabel.insets = new Insets(10, 10, 10, 10);
        gbcTypeLabel.fill = GridBagConstraints.HORIZONTAL;
        gbcTypeLabel.gridx = 0;
        gbcTypeLabel.gridy = 0;
        formPanel.add(new JLabel("Type de vélo :"), gbcTypeLabel);

        typeField = new JTextField("Vélo");
        GridBagConstraints gbcTypeField = new GridBagConstraints();
        gbcTypeField.insets = new Insets(10, 10, 10, 10);
        gbcTypeField.fill = GridBagConstraints.HORIZONTAL;
        gbcTypeField.gridx = 1;
        gbcTypeField.gridy = 0;
        gbcTypeField.weightx = 1.0;
        formPanel.add(typeField, gbcTypeField);

        GridBagConstraints gbcLengthLabel = new GridBagConstraints();
        gbcLengthLabel.insets = new Insets(10, 10, 10, 10);
        gbcLengthLabel.fill = GridBagConstraints.HORIZONTAL;
        gbcLengthLabel.gridx = 0;
        gbcLengthLabel.gridy = 1;
        formPanel.add(new JLabel("Longueur (m) :"), gbcLengthLabel);

        lengthSpinner = new JSpinner(new SpinnerNumberModel(1.5, 0.5, 3.0, 0.1));
        GridBagConstraints gbcLengthSpinner = new GridBagConstraints();
        gbcLengthSpinner.insets = new Insets(10, 10, 10, 10);
        gbcLengthSpinner.fill = GridBagConstraints.HORIZONTAL;
        gbcLengthSpinner.gridx = 1;
        gbcLengthSpinner.gridy = 1;
        formPanel.add(lengthSpinner, gbcLengthSpinner);

        GridBagConstraints gbcWeightLabel = new GridBagConstraints();
        gbcWeightLabel.insets = new Insets(10, 10, 10, 10);
        gbcWeightLabel.fill = GridBagConstraints.HORIZONTAL;
        gbcWeightLabel.gridx = 0;
        gbcWeightLabel.gridy = 2;
        formPanel.add(new JLabel("Poids (kg) :"), gbcWeightLabel);

        weightSpinner = new JSpinner(new SpinnerNumberModel(10.0, 1.0, 50.0, 0.5));
        GridBagConstraints gbcWeightSpinner = new GridBagConstraints();
        gbcWeightSpinner.insets = new Insets(10, 10, 10, 10);
        gbcWeightSpinner.fill = GridBagConstraints.HORIZONTAL;
        gbcWeightSpinner.gridx = 1;
        gbcWeightSpinner.gridy = 2;
        formPanel.add(weightSpinner, gbcWeightSpinner);

        add(formPanel, BorderLayout.CENTER);

        // ---------- BOUTONS ----------
        JPanel buttonPanel = new JPanel();

        JButton cancelBtn = new JButton("Annuler");
        cancelBtn.addActionListener(e -> dispose());

        JButton saveBtn = new JButton("Enregistrer");
        saveBtn.addActionListener(e -> saveBike());

        buttonPanel.add(cancelBtn);
        buttonPanel.add(saveBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void saveBike() {
        String type = typeField.getText().trim();
        if (type.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez indiquer un type de vélo.");
            return;
        }
        double length = (double) lengthSpinner.getValue();
        double weight = (double) weightSpinner.getValue();

        Bike newBike = new Bike();
        newBike.setType(type);
        newBike.setWeight(weight);
        newBike.setLength(length);

        try {
            boolean success = loggedUser.addBike(newBike);
            if (success) {
                JOptionPane.showMessageDialog(this, "Vélo ajouté avec succès !");
                if (onBikeAdded != null) {
                    onBikeAdded.run();
                }
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Erreur lors de l'enregistrement en base de données.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur technique : " + ex.getMessage());
        }
    }
}
