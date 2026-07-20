package be.senechal.clubVelo.view;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import be.senechal.clubVelo.model.*;
import be.senechal.clubVelo.session.Session;

public class RideDetailsFrame extends JFrame {
    private Ride ride;
    private Member loggedUser;
    private JPanel centerPanel;
    private boolean isMemberMode;

    public RideDetailsFrame(Ride ride) {
        this.ride = ride;
        Person currentUser = Session.getCurrentUser();

        if (currentUser instanceof Member) {
            this.loggedUser = (Member) currentUser;
            this.isMemberMode = true;
        } else {
            this.loggedUser = null;
            this.isMemberMode = false;
        }

        setTitle("Détails du Ride n°" + ride.getNum());
        setSize(900, 750);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // ----------- PANEL TOP : Infos ride ----------
        JPanel topPanel = new JPanel(new GridLayout(0, 1));
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        topPanel.add(new JLabel("Numéro du ride : " + ride.getNum()));
        topPanel.add(new JLabel("Balade : " + ride.getStartPlace()));
        topPanel.add(new JLabel("Date : " + (ride.getStartDate() != null ? ride.getStartDate().format(fmt) : "Non définie")));
        topPanel.add(new JLabel("Coût total (aller-retour) : " + ride.getFee() + " €"));

        Club club = Club.getClub();
        if (club != null) {
            topPanel.add(new JLabel("Rendez-vous covoiturage (adresse du club) : " + club.getAdresseComplete()));
        }

        if (!ride.isRegistrationOpen()) {
            topPanel.add(new JLabel("⚠️ Les inscriptions sont fermées pour cette balade passée."));
        }

        add(topPanel, BorderLayout.NORTH);

        // ----------- PANEL CENTER : Liste des véhicules ----------
        centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        JScrollPane scroll = new JScrollPane(centerPanel);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        loadVehicles();

        // ----------- PANEL BOTTOM : Actions globales ----------
        JPanel bottomPanel = new JPanel();

        // 1. Bouton "Proposer mon véhicule"
        JButton addMyVehicleBtn = new JButton("Proposer mon véhicule");

        if (!isMemberMode) {
            addMyVehicleBtn.setEnabled(false);
            addMyVehicleBtn.setText("Action réservée aux membres");
        } else if (!ride.isRegistrationOpen()) {
            addMyVehicleBtn.setEnabled(false);
            addMyVehicleBtn.setText("Inscriptions fermées");
        }

        addMyVehicleBtn.addActionListener(e -> proposeVehicle(scroll));

        bottomPanel.add(addMyVehicleBtn);
        bottomPanel.add(Box.createHorizontalStrut(10));

        // 4. Bouton Fermer
        JButton closeBtn = new JButton("Fermer");
        closeBtn.addActionListener(e -> dispose());
        bottomPanel.add(closeBtn);

        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void proposeVehicle(JScrollPane scroll) {
        if (!isMemberMode) return;

        List<Vehicle> myVehicles = loggedUser.getVehicles();

        if (myVehicles.isEmpty()) {
            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Vous n'avez pas encore enregistré de véhicule.\nVoulez-vous en créer un maintenant ?",
                    "Pas de véhicule",
                    JOptionPane.YES_NO_OPTION
            );

            if (choice == JOptionPane.YES_OPTION) {
                new CreateVehicleFrame();
                dispose();
            }
            return;
        }

        Vehicle myCar;
        if (myVehicles.size() == 1) {
            myCar = myVehicles.get(0);
        } else {
            Object[] choices = myVehicles.toArray();
            myCar = (Vehicle) JOptionPane.showInputDialog(this, "Quel véhicule proposer ?",
                    "Choisir un véhicule", JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]);
            if (myCar == null) return;
        }

        boolean alreadyInRide = ride.getVehicles().contains(myCar);
        if (alreadyInRide) {
            JOptionPane.showMessageDialog(this, "Ce véhicule est déjà inscrit à ce ride !");
            return;
        }

        try {
            boolean success = ride.addVehicle(myCar, loggedUser);
            if (success) {
                JOptionPane.showMessageDialog(this, "Votre véhicule a été ajouté !");
                loadVehicles();
                SwingUtilities.invokeLater(() -> scroll.getVerticalScrollBar().setValue(scroll.getVerticalScrollBar().getMaximum()));
            } else {
                JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout du véhicule.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur: " + ex.getMessage());
        }
    }

    private void loadVehicles() {
        centerPanel.removeAll();

        if (ride.getVehicles().isEmpty()) {
            JLabel noVehicles = new JLabel("Aucun véhicule pour ce ride.");
            noVehicles.setAlignmentX(Component.CENTER_ALIGNMENT);
            noVehicles.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
            centerPanel.add(noVehicles);
        } else {
            ride.loadVehiclesOccupancy();

            for (Vehicle v : ride.getVehicles()) {
                JPanel vehiclePanel = new JPanel(new GridBagLayout());
                vehiclePanel.setBorder(BorderFactory.createTitledBorder(
                        "Véhicule de " + v.getDriver().getName() + " — " + v.getModel()));
                vehiclePanel.setMaximumSize(new Dimension(800, 200));

                GridBagConstraints gbc = new GridBagConstraints();
                gbc.insets = new Insets(5, 5, 5, 5);
                gbc.fill = GridBagConstraints.HORIZONTAL;

                List<Member> passengers = v.getPassengersForRide(ride);
                List<Bike> bikes = v.getBikesForRide(ride);

                boolean isFullPassengers = v.isPassengerFull(ride);
                boolean isFullBikes = v.isBikeFull(ride);

                // --- UI INFO ---
                gbc.gridx = 0; gbc.gridy = 0;
                vehiclePanel.add(new JLabel("Conducteur : " + v.getDriver().getName()), gbc);
                gbc.gridx = 0; gbc.gridy = 1;
                vehiclePanel.add(new JLabel("Passagers : " + passengers.size() + "/" + v.getSeatNumber()), gbc);

                gbc.gridx = 1; gbc.gridy = 1;
                String passagerList = passengers.isEmpty() ? "Aucun" :
                        String.join(", ", passengers.stream().map(Member::getName).toArray(String[]::new));
                vehiclePanel.add(new JLabel(passagerList), gbc);

                gbc.gridx = 0; gbc.gridy = 2;
                vehiclePanel.add(new JLabel("Vélos : " + bikes.size() + "/" + v.getBikeSpotNumber()), gbc);

                centerPanel.add(vehiclePanel);
                centerPanel.add(Box.createVerticalStrut(10));
            }
        }
        centerPanel.revalidate();
        centerPanel.repaint();
    }
}
