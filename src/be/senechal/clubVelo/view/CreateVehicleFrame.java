package be.senechal.clubVelo.view;

import javax.swing.*;
import java.awt.*;
import be.senechal.clubVelo.model.Member;
import be.senechal.clubVelo.model.Person;
import be.senechal.clubVelo.model.Vehicle;
import be.senechal.clubVelo.session.Session;

public class CreateVehicleFrame extends JFrame {

    private Member loggedUser;
    private JTextField typeField;
    private JSpinner seatSpinner;
    private JSpinner bikeSpinner;

    public CreateVehicleFrame() {
    	Person currentUser = Session.getCurrentUser();

        if (currentUser instanceof Member) {
            this.loggedUser = (Member) currentUser;
        } else {
            JOptionPane.showMessageDialog(this, "Erreur : Seul un membre peut créer un véhicule.");
            dispose();
            return;
        }

        setTitle("Enregistrer mon véhicule");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // ---------- HEADER ----------
        JLabel titleLabel = new JLabel("Nouveau Véhicule", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        // ---------- FORMULAIRE ----------
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // 1. Type / Modèle
        GridBagConstraints gbcModeleLabel = new GridBagConstraints();
        gbcModeleLabel.insets = new Insets(10, 10, 10, 10);
        gbcModeleLabel.fill = GridBagConstraints.HORIZONTAL;
        gbcModeleLabel.gridx = 0;
        gbcModeleLabel.gridy = 0;
        formPanel.add(new JLabel("Modèle (ex: Peugeot 208) :"), gbcModeleLabel);

        typeField = new JTextField();
        GridBagConstraints gbcModeleField = new GridBagConstraints();
        gbcModeleField.insets = new Insets(10, 10, 10, 10);
        gbcModeleField.fill = GridBagConstraints.HORIZONTAL;
        gbcModeleField.gridx = 1;
        gbcModeleField.gridy = 0;
        gbcModeleField.weightx = 1.0;
        formPanel.add(typeField, gbcModeleField);

        // 2. Nombre de places passagers
        GridBagConstraints gbcSeatsLabel = new GridBagConstraints();
        gbcSeatsLabel.insets = new Insets(10, 10, 10, 10);
        gbcSeatsLabel.fill = GridBagConstraints.HORIZONTAL;
        gbcSeatsLabel.gridx = 0;
        gbcSeatsLabel.gridy = 1;
        formPanel.add(new JLabel("Places passagers :"), gbcSeatsLabel);

        seatSpinner = new JSpinner(new SpinnerNumberModel(3, 1, 9, 1));
        GridBagConstraints gbcSeatsSpinner = new GridBagConstraints();
        gbcSeatsSpinner.insets = new Insets(10, 10, 10, 10);
        gbcSeatsSpinner.fill = GridBagConstraints.HORIZONTAL;
        gbcSeatsSpinner.gridx = 1;
        gbcSeatsSpinner.gridy = 1;
        formPanel.add(seatSpinner, gbcSeatsSpinner);

        // 3. Nombre de places vélos
        GridBagConstraints gbcBikesLabel = new GridBagConstraints();
        gbcBikesLabel.insets = new Insets(10, 10, 10, 10);
        gbcBikesLabel.fill = GridBagConstraints.HORIZONTAL;
        gbcBikesLabel.gridx = 0;
        gbcBikesLabel.gridy = 2;
        formPanel.add(new JLabel("Places vélos (Rack) :"), gbcBikesLabel);

        bikeSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
        GridBagConstraints gbcBikesSpinner = new GridBagConstraints();
        gbcBikesSpinner.insets = new Insets(10, 10, 10, 10);
        gbcBikesSpinner.fill = GridBagConstraints.HORIZONTAL;
        gbcBikesSpinner.gridx = 1;
        gbcBikesSpinner.gridy = 2;
        formPanel.add(bikeSpinner, gbcBikesSpinner);

        add(formPanel, BorderLayout.CENTER);

        // ---------- BOUTONS ----------
        JPanel buttonPanel = new JPanel();

        JButton cancelBtn = new JButton("Annuler");
        cancelBtn.addActionListener(e -> dispose());

        JButton saveBtn = new JButton("Enregistrer");
        saveBtn.addActionListener(e -> saveVehicle());

        buttonPanel.add(cancelBtn);
        buttonPanel.add(saveBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void saveVehicle() {
        String model = typeField.getText().trim();
        int seats = (int) seatSpinner.getValue();
        int bikes = (int) bikeSpinner.getValue();

        if (model.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer un modèle de véhicule.", "Erreur", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Vehicle newVehicle = new Vehicle();
            newVehicle.setSeatNumber(seats);
            newVehicle.setBikeSpotNumber(bikes);
            newVehicle.setModel(model);
            newVehicle.setDriver(loggedUser);
            boolean success = Vehicle.create(newVehicle);

            if (success) {
                JOptionPane.showMessageDialog(this, "Véhicule enregistré avec succès !");

                loggedUser.getVehicles().add(newVehicle);

                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Erreur lors de l'enregistrement en base de données.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur technique : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}
