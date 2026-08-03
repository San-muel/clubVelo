package be.senechal.clubVelo.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

import be.senechal.clubVelo.model.Member;
import be.senechal.clubVelo.model.Payment;
import be.senechal.clubVelo.model.Ride;
import be.senechal.clubVelo.model.Treasurer;
import be.senechal.clubVelo.session.Session;

public class TreasurerFrame extends JFrame {

    private Treasurer loggedTreasurer;
    private final int currentYear = LocalDate.now().getYear();

    private JTable cotisTable;
    private DefaultTableModel cotisModel;
    private List<Member> displayedMembers;

    private JComboBox<Ride> rideSelector;
    private JTextArea rideDetailsArea;
    private JButton processRidePaymentBtn;

    private JTable paymentsTable;
    private DefaultTableModel paymentsModel;
    private JLabel paymentsSummaryLabel;

    public TreasurerFrame() {
        if (Session.getCurrentUser() instanceof Treasurer) {
            this.loggedTreasurer = (Treasurer) Session.getCurrentUser();
        } else {
            dispose();
            return;
        }

        setTitle("Espace Trésorier - " + loggedTreasurer.getName());
        setSize(950, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout());

        // Header
        JLabel header = new JLabel("Administration Financière", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 20));
        header.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        getContentPane().add(header, BorderLayout.NORTH);

        // --- ONGLETS ---
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("État des Cotisations", createCotisationPanel());
        tabbedPane.addTab("Gestion Covoiturage", createCarpoolPanel());
        tabbedPane.addTab("Suivi des Paiements", createPaymentsHistoryPanel());
        getContentPane().add(tabbedPane, BorderLayout.CENTER);

        // Footer
        JButton logout = new JButton("Déconnexion");
        logout.addActionListener(e -> {
            Session.logout();
            dispose();
            new LoginFrame().setVisible(true);
        });
        getContentPane().add(logout, BorderLayout.SOUTH);
    }

    // ---------------------------------------------------------
    // PANNEAU 1 : COTISATIONS
    // ---------------------------------------------------------
    private JPanel createCotisationPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] cols = {"ID", "Nom", "Prénom", "NB Catégories", "Cotisation due (€)", "Statut " + currentYear};
        cotisModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        cotisTable = new JTable(cotisModel);
        loadCotisationData();

        panel.add(new JScrollPane(cotisTable), BorderLayout.CENTER);

        JPanel south = new JPanel();
        JButton refreshBtn = new JButton("Actualiser la liste");
        refreshBtn.addActionListener(e -> loadCotisationData());
        south.add(refreshBtn);

        JButton encaisserBtn = new JButton("Encaisser la cotisation");
        encaisserBtn.addActionListener(e -> encaisserCotisation());
        south.add(encaisserBtn);

        panel.add(south, BorderLayout.SOUTH);

        return panel;
    }

    private void loadCotisationData() {
        cotisModel.setRowCount(0);
        displayedMembers = Member.getAllMember();
        for (Member m : displayedMembers) {
            double due = loggedTreasurer.calculateAnnualFeeDue(m);
            boolean isOk = loggedTreasurer.isMemberUpToDate(m, currentYear);

            cotisModel.addRow(new Object[]{
                m.getId(), m.getName(), m.getFirstname(),
                m.getCategories().size(),
                String.format("%.2f", due),
                isOk ? "EN ORDRE" : "DOIT PAYER"
            });
        }
    }

    private void encaisserCotisation() {
        int row = cotisTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un membre.");
            return;
        }
        Member member = displayedMembers.get(row);

        if (loggedTreasurer.isMemberUpToDate(member, currentYear)) {
            JOptionPane.showMessageDialog(this, "Ce membre est déjà en ordre pour " + currentYear + ".");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Encaisser " + String.format("%.2f", loggedTreasurer.calculateAnnualFeeDue(member))
                        + " € de cotisation " + currentYear + " pour " + member + " ?",
                "Confirmer l'encaissement", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        boolean success = loggedTreasurer.encaisserCotisation(member, currentYear);
        if (success) {
            JOptionPane.showMessageDialog(this, "Cotisation encaissée !");
            loadCotisationData();
        } else {
            JOptionPane.showMessageDialog(this, "Erreur lors de l'encaissement.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ---------------------------------------------------------
    // PANNEAU 2 : COVOITURAGE
    // ---------------------------------------------------------
    private JPanel createCarpoolPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel top = new JPanel();
        top.add(new JLabel("Sélectionner un Ride :"));
        rideSelector = new JComboBox<>();
        try {
            List<Ride> rides = Ride.getAllRide();

            for(Ride r : rides) rideSelector.addItem(r);
        } catch(Exception e) {}

        rideSelector.addActionListener(e -> showRideImpact());
        top.add(rideSelector);
        panel.add(top, BorderLayout.NORTH);

        // Centre
        rideDetailsArea = new JTextArea();
        rideDetailsArea.setEditable(false);
        panel.add(new JScrollPane(rideDetailsArea), BorderLayout.CENTER);

        // Bas
        processRidePaymentBtn = new JButton("Valider et transférer les fonds");
        processRidePaymentBtn.setBackground(new Color(220, 20, 60));
        processRidePaymentBtn.setForeground(Color.WHITE);
        processRidePaymentBtn.addActionListener(e -> processPayment());
        panel.add(processRidePaymentBtn, BorderLayout.SOUTH);

        return panel;
    }

    private void showRideImpact() {
        Ride ride = (Ride) rideSelector.getSelectedItem();
        if(ride == null) return;

        processRidePaymentBtn.setEnabled(!ride.isPaid());
        processRidePaymentBtn.setText(ride.isPaid() ? "Balade déjà payée" : "Valider et transférer les fonds");

        StringBuilder sb = new StringBuilder();
        sb.append("Ride: ").append(ride.getStartPlace()).append(" | Coût total (aller-retour): ").append(ride.getFee()).append("€");
        if (ride.isPaid()) {
            sb.append("  [PAYÉE]");
        }
        sb.append("\n\n");

        for (Treasurer.VehicleImpact impact : loggedTreasurer.computeRideImpact(ride)) {
            sb.append("Véhicule de ").append(impact.getDriver().getName()).append(":\n");
            sb.append("  + ").append(impact.getDriverGain()).append("€ pour le chauffeur.\n");
            for (Member p : impact.getPassengers()) {
                sb.append("  - ").append(impact.getFeePerPassenger()).append("€ pour ").append(p.getName()).append("\n");
            }
            sb.append("\n");
        }
        rideDetailsArea.setText(sb.toString());
    }

    private void processPayment() {
        Ride ride = (Ride) rideSelector.getSelectedItem();
        if(ride == null) return;

        if (ride.isPaid()) {
            JOptionPane.showMessageDialog(this, "Cette balade a déjà été payée.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Confirmer les transactions ?", "Paiement", JOptionPane.YES_NO_OPTION);
        if(confirm != JOptionPane.YES_OPTION) return;

        boolean success = loggedTreasurer.processRidePayments(ride);

        if (success) {
            JOptionPane.showMessageDialog(this, "Transactions effectuées !");
        } else {
            JOptionPane.showMessageDialog(this, "Erreur lors du traitement des paiements (aucune transaction n'a été appliquée).", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
        showRideImpact();
        loadCotisationData();
        loadPaymentsHistory();
    }

    // ---------------------------------------------------------
    // PANNEAU 3 : SUIVI DES PAIEMENTS
    // ---------------------------------------------------------
    private JPanel createPaymentsHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        paymentsSummaryLabel = new JLabel(" ", SwingConstants.CENTER);
        paymentsSummaryLabel.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        panel.add(paymentsSummaryLabel, BorderLayout.NORTH);

        String[] cols = {"Date", "Balade", "Membre", "Mouvement", "Montant (€)"};
        paymentsModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        paymentsTable = new JTable(paymentsModel);
        panel.add(new JScrollPane(paymentsTable), BorderLayout.CENTER);

        JPanel south = new JPanel();
        JButton refreshBtn = new JButton("Actualiser l'historique");
        refreshBtn.addActionListener(e -> loadPaymentsHistory());
        south.add(refreshBtn);
        panel.add(south, BorderLayout.SOUTH);

        loadPaymentsHistory();
        return panel;
    }

    private void loadPaymentsHistory() {
        if (paymentsModel == null) return;
        paymentsModel.setRowCount(0);
        List<Payment> history = loggedTreasurer.getPaymentHistory();
        for (Payment p : history) {
            paymentsModel.addRow(new Object[]{
                p.getFormattedDate(), p.getRideLabel(), p.getMemberName(),
                p.getSensLabel(), String.format("%.2f", p.getMontant())
            });
        }
        double toDrivers = loggedTreasurer.getTotalReimbursedToDrivers(history);
        double fromPassengers = loggedTreasurer.getTotalCollectedFromPassengers(history);
        paymentsSummaryLabel.setText(String.format(
                "Total remboursé aux chauffeurs : %.2f €      |      Total encaissé sur les passagers : %.2f €",
                toDrivers, fromPassengers));
    }
}
