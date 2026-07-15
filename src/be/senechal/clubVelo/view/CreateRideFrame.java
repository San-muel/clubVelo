package be.senechal.clubVelo.view;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import be.senechal.clubVelo.model.Manager;
import be.senechal.clubVelo.model.Ride;

public class CreateRideFrame extends JFrame {

    private Manager manager;
    private ManagerFrame parentFrame;

    private JTextField startPlaceField;
    private JTextField dateField;
    private JSpinner distanceSpinner;
    private JLabel feeValueLabel;

    public CreateRideFrame(Manager manager, ManagerFrame parentFrame) {
        this.manager = manager;
        this.parentFrame = parentFrame;

        setTitle("Créer un nouveau Ride");
        setSize(420, 380);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header
        String catName = manager.getCategory().getClass().getSimpleName();
        JLabel title = new JLabel("Nouveau Ride - " + catName, SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        // Formulaire
        JPanel formPanel = new JPanel(new GridBagLayout());

        // 1. Lieu de départ
        GridBagConstraints gbcPlaceLabel = new GridBagConstraints();
        gbcPlaceLabel.insets = new Insets(10, 10, 10, 10);
        gbcPlaceLabel.fill = GridBagConstraints.HORIZONTAL;
        gbcPlaceLabel.gridx = 0;
        gbcPlaceLabel.gridy = 0;
        formPanel.add(new JLabel("Lieu de départ :"), gbcPlaceLabel);

        startPlaceField = new JTextField();
        GridBagConstraints gbcPlaceField = new GridBagConstraints();
        gbcPlaceField.insets = new Insets(10, 10, 10, 10);
        gbcPlaceField.fill = GridBagConstraints.HORIZONTAL;
        gbcPlaceField.gridx = 1;
        gbcPlaceField.gridy = 0;
        gbcPlaceField.weightx = 1.0;
        formPanel.add(startPlaceField, gbcPlaceField);

        // 2. Date + heure
        GridBagConstraints gbcDateLabel = new GridBagConstraints();
        gbcDateLabel.insets = new Insets(10, 10, 10, 10);
        gbcDateLabel.fill = GridBagConstraints.HORIZONTAL;
        gbcDateLabel.gridx = 0;
        gbcDateLabel.gridy = 1;
        formPanel.add(new JLabel("Date (jj/mm/aaaa hh:mm) :"), gbcDateLabel);

        String defaultDate = LocalDateTime.now().plusDays(7).withMinute(0).withSecond(0)
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        dateField = new JTextField(defaultDate);
        GridBagConstraints gbcDateField = new GridBagConstraints();
        gbcDateField.insets = new Insets(10, 10, 10, 10);
        gbcDateField.fill = GridBagConstraints.HORIZONTAL;
        gbcDateField.gridx = 1;
        gbcDateField.gridy = 1;
        formPanel.add(dateField, gbcDateField);

        // 3. Distance aller (km)
        GridBagConstraints gbcDistanceLabel = new GridBagConstraints();
        gbcDistanceLabel.insets = new Insets(10, 10, 10, 10);
        gbcDistanceLabel.fill = GridBagConstraints.HORIZONTAL;
        gbcDistanceLabel.gridx = 0;
        gbcDistanceLabel.gridy = 2;
        formPanel.add(new JLabel("Distance aller (km) :"), gbcDistanceLabel);

        distanceSpinner = new JSpinner(new SpinnerNumberModel(10.0, 0.5, 300.0, 0.5));
        distanceSpinner.addChangeListener(e -> updateComputedFee());
        GridBagConstraints gbcDistanceSpinner = new GridBagConstraints();
        gbcDistanceSpinner.insets = new Insets(10, 10, 10, 10);
        gbcDistanceSpinner.fill = GridBagConstraints.HORIZONTAL;
        gbcDistanceSpinner.gridx = 1;
        gbcDistanceSpinner.gridy = 2;
        formPanel.add(distanceSpinner, gbcDistanceSpinner);

        // 4. Coût total calculé
        GridBagConstraints gbcFeeLabel = new GridBagConstraints();
        gbcFeeLabel.insets = new Insets(10, 10, 10, 10);
        gbcFeeLabel.fill = GridBagConstraints.HORIZONTAL;
        gbcFeeLabel.gridx = 0;
        gbcFeeLabel.gridy = 3;
        formPanel.add(new JLabel("Coût total (aller-retour) :"), gbcFeeLabel);

        feeValueLabel = new JLabel();
        feeValueLabel.setFont(feeValueLabel.getFont().deriveFont(Font.BOLD));
        GridBagConstraints gbcFeeValue = new GridBagConstraints();
        gbcFeeValue.insets = new Insets(10, 10, 10, 10);
        gbcFeeValue.fill = GridBagConstraints.HORIZONTAL;
        gbcFeeValue.gridx = 1;
        gbcFeeValue.gridy = 3;
        formPanel.add(feeValueLabel, gbcFeeValue);

        updateComputedFee();

        add(formPanel, BorderLayout.CENTER);

        // Boutons
        JPanel btnPanel = new JPanel();
        JButton saveBtn = new JButton("Créer le ride");
        saveBtn.addActionListener(e -> saveRide());

        JButton cancelBtn = new JButton("Annuler");
        cancelBtn.addActionListener(e -> dispose());

        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);
        add(btnPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void updateComputedFee() {
        double distance = (double) distanceSpinner.getValue();
        try {
            double fee = Ride.computeFee(distance);
            feeValueLabel.setText(String.format("%.2f €", fee));
        } catch (IllegalArgumentException ex) {
            feeValueLabel.setText("—");
        }
    }

    private void saveRide() {
        String place = startPlaceField.getText().trim();
        String dateStr = dateField.getText().trim();
        double distance = (double) distanceSpinner.getValue();
        double fee = Ride.computeFee(distance);

        LocalDateTime startDate;
        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            startDate = LocalDateTime.parse(dateStr, fmt);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Format de date invalide.\nUtilisez : jj/mm/aaaa hh:mm");
            return;
        }

        String dateError = Ride.validateFutureDateForCreation(startDate);
        if (dateError != null) {
            JOptionPane.showMessageDialog(this, dateError);
            return;
        }

        Ride ride;
        try {
            ride = new Ride();
            ride.setStartPlace(place);
            ride.setStartDate(startDate);
            ride.setFee(fee);
            ride.setDistanceKm(distance);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            return;
        }

        try {
            boolean success = manager.getCategory().getCalendar().addRide(ride, manager.getId(), manager.getCategory().getId());
            if (success) {
                JOptionPane.showMessageDialog(this, "Ride créé avec succès !");
                manager.getCategory().refreshCalendar();
                if (parentFrame != null) {
                    parentFrame.loadRideTable();
                }

                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Erreur lors de l'enregistrement en base de données.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur technique : " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
