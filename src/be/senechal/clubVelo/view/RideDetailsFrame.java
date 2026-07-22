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

        // 2. Bouton "Je participe à cette balade"
        JButton participateBtn = new JButton("Je participe à cette balade");
        if (!isMemberMode) {
            participateBtn.setEnabled(false);
        } else if (!ride.isRegistrationOpen()) {
            participateBtn.setEnabled(false);
            participateBtn.setText("Inscriptions fermées");
        }
        participateBtn.addActionListener(e -> declareParticipation());
        bottomPanel.add(participateBtn);
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

    private void declareParticipation() {
        if (!isMemberMode) return;

        if (ride.isMemberParticipating(loggedUser)) {
            JOptionPane.showMessageDialog(this, "Vous avez déjà déclaré votre participation à cette balade.");
            return;
        }

        int wantsBike = JOptionPane.showConfirmDialog(this, "Comptez-vous amener un vélo ?",
                "Vélo", JOptionPane.YES_NO_OPTION);
        if (wantsBike == JOptionPane.CLOSED_OPTION) {
            return;
        }

        Bike chosenBike = null;
        if (wantsBike == JOptionPane.YES_OPTION) {
            List<Bike> memberBikes = loggedUser.getBikes();
            if (memberBikes.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Vous n'avez aucun vélo enregistré. Ajoutez-en un depuis votre profil avant de participer avec un vélo.",
                        "Aucun vélo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Object[] bikeChoices = memberBikes.toArray();
            chosenBike = (Bike) JOptionPane.showInputDialog(this, "Quel vélo comptez-vous amener ?",
                    "Choisir un vélo", JOptionPane.QUESTION_MESSAGE, null, bikeChoices, bikeChoices[0]);
            if (chosenBike == null) {
                return;
            }
        }

        boolean success = ride.addParticipation(loggedUser, chosenBike);
        if (!success) {
            JOptionPane.showMessageDialog(this, "Erreur lors de l'enregistrement de la participation.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Vehicle availableVehicle = ride.findAvailableVehicleFor(chosenBike);

        if (availableVehicle != null) {
            boolean seated = ride.reserveSeat(availableVehicle, loggedUser);
            boolean bikeSeated = chosenBike == null || ride.reserveBikeSpot(availableVehicle, loggedUser, chosenBike);
            loadVehicles();
            if (seated && bikeSeated) {
                JOptionPane.showMessageDialog(this, "Vous avez été inscrit automatiquement dans le véhicule de "
                        + availableVehicle.getDriver().getName() + " !");
            } else {
                JOptionPane.showMessageDialog(this, "Votre souhait de participer a bien été pris en compte.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Votre souhait de participer a bien été pris en compte.");
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
            boolean memberAlreadySeated = isMemberMode && ride.isMemberSeated(loggedUser);
            boolean memberBikeAlreadySeated = isMemberMode && ride.isMemberBikeSeated(loggedUser);

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

                boolean isDriver = isMemberMode && v.getDriver().equals(loggedUser);
                boolean isPassenger = isMemberMode && passengers.contains(loggedUser);
                boolean isAlreadySeated = isPassenger || isDriver || memberAlreadySeated;

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

                // --- BOUTONS ---

                // 1. Bouton Passager
                gbc.gridx = 0; gbc.gridy = 3;
                JButton btnPassengerOnly = new JButton("M'inscrire (Passager)");

                if (!isMemberMode) {
                    btnPassengerOnly.setEnabled(false);
                } else if (!ride.isRegistrationOpen()) {
                    btnPassengerOnly.setEnabled(false);
                    btnPassengerOnly.setText("Inscriptions fermées");
                } else if (isFullPassengers || isAlreadySeated) {
                    btnPassengerOnly.setEnabled(false);
                    String label;
                    if (isPassenger || isDriver) {
                        label = isDriver ? "Vous êtes le chauffeur" : "Déjà passager ici";
                    } else if (memberAlreadySeated) {
                        label = "Déjà inscrit sur un autre véhicule";
                    } else {
                        label = "Sièges complets";
                    }
                    btnPassengerOnly.setText(label);
                }

                btnPassengerOnly.addActionListener(e -> {
                    if(!isMemberMode) return;
                    try {
                        boolean success = ride.reserveSeat(v, loggedUser);
                        if (success) {
                            JOptionPane.showMessageDialog(this, "Place passager réservée !");
                            loadVehicles();
                            centerPanel.revalidate(); centerPanel.repaint();
                        } else if (ride.isMemberSeated(loggedUser)) {
                            JOptionPane.showMessageDialog(this,
                                    "Vous êtes déjà inscrit dans un véhicule de ce ride.");
                        } else {
                            JOptionPane.showMessageDialog(this, "Sièges complets, inscription refusée.");
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage());
                    }
                });
                vehiclePanel.add(btnPassengerOnly, gbc);

                // 2. Bouton Vélo
                gbc.gridx = 1; gbc.gridy = 3;
                JButton btnBikeOnly = new JButton("Ajouter un vélo");

                if (!isMemberMode) {
                    btnBikeOnly.setEnabled(false);
                } else if (!ride.isRegistrationOpen()) {
                    btnBikeOnly.setEnabled(false);
                    btnBikeOnly.setText("Inscriptions fermées");
                } else if (isFullBikes || memberBikeAlreadySeated) {
                    btnBikeOnly.setEnabled(false);
                    btnBikeOnly.setText(memberBikeAlreadySeated ? "Vélo déjà placé sur un autre véhicule" : "Rack vélo complet");
                }

                btnBikeOnly.addActionListener(e -> {
                    if(!isMemberMode) return;

                    List<Bike> memberBikes = loggedUser.getBikes();
                    if (memberBikes.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Vous n'avez aucun vélo enregistré !");
                        return;
                    }

                    Object[] bikeChoices = memberBikes.toArray();
                    Bike selectedBike = (Bike) JOptionPane.showInputDialog(this, "Quel vélo ?", "Choisir",
                            JOptionPane.QUESTION_MESSAGE, null, bikeChoices, bikeChoices[0]);
                    if (selectedBike == null) return;

                    try {
                        boolean success = ride.reserveBikeSpot(v, loggedUser, selectedBike);
                        if (success) {
                            JOptionPane.showMessageDialog(this, "Vélo ajouté !");
                            loadVehicles();
                            centerPanel.revalidate(); centerPanel.repaint();
                        } else if (ride.isMemberBikeSeated(loggedUser)) {
                            JOptionPane.showMessageDialog(this,
                                    "Un de vos vélos est déjà placé dans un véhicule de ce ride.");
                        } else {
                            JOptionPane.showMessageDialog(this, "Rack vélo complet, ajout refusé.");
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage());
                    }
                });
                vehiclePanel.add(btnBikeOnly, gbc);

                centerPanel.add(vehiclePanel);
                centerPanel.add(Box.createVerticalStrut(10));
            }
        }
        centerPanel.revalidate();
        centerPanel.repaint();
    }
}
