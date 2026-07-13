package be.senechal.clubVelo.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import be.senechal.clubVelo.model.*;
import be.senechal.clubVelo.session.Session;

public class HomeFrame extends JFrame {

    private Member loggedUser;
    private Category selectedCategory;
    private JTable rideTable;
    private DefaultTableModel rideTableModel;
    private JPanel topPanel;
    private JButton profileBtn;

    public HomeFrame() {
        Person currentUser = Session.getCurrentUser();
        if (currentUser instanceof Member) {
            this.loggedUser = (Member) currentUser;
        } else {
            JOptionPane.showMessageDialog(this, "Erreur critique : Accès non autorisé (Ce n'est pas un membre).");
            dispose();
            return;
        }
        setTitle("Club Vélo - Accueil - Bonjour " + loggedUser.getFirstname());
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(1, 4, 10, 10));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        profileBtn = new JButton("Mon Profil");
        profileBtn.addActionListener(e -> {
            new MemberInfoFrame(loggedUser, this::refreshCategoryButtons);
        });

        refreshCategoryButtons();

        add(topPanel, BorderLayout.NORTH);

        // ---------- TABLE ----------
        String[] columns = { "Num", "Départ", "Date", "Prix (€)" };
        rideTableModel = new DefaultTableModel(columns, 0);
        rideTable = new JTable(rideTableModel);
        rideTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(rideTable);
        add(scrollPane, BorderLayout.CENTER);

        // ---------- PANNEAU DU BAS : détails + déconnexion ----------
        JPanel bottomPanel = new JPanel();

        JButton detailsBtn = new JButton("Voir les détails du ride");
        detailsBtn.addActionListener(e -> openRideDetails());
        bottomPanel.add(detailsBtn);

        JButton logoutBtn = new JButton("Déconnexion");
        logoutBtn.addActionListener(e -> {
            Session.logout();
            dispose();
            new LoginFrame().setVisible(true);
        });
        bottomPanel.add(Box.createHorizontalStrut(20));
        bottomPanel.add(logoutBtn);

        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void refreshCategoryButtons() {
        topPanel.removeAll();

        if (loggedUser.getCategories() != null) {
            for (Category cat : loggedUser.getCategories()) {
                JButton btn = new JButton(cat.getClass().getSimpleName());
                btn.addActionListener(e -> {
                    selectedCategory = cat;
                    loadRideTable(cat);
                });
                topPanel.add(btn);
            }
        }

        topPanel.add(profileBtn);
        topPanel.revalidate();
        topPanel.repaint();
    }

    private void loadRideTable(Category cat) {
        rideTableModel.setRowCount(0);

        if (cat.getCalendar() != null && cat.getCalendar().getRides() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            for (Ride ride : cat.getCalendar().getRides()) {
                rideTableModel.addRow(new Object[]{
                        ride.getNum(),
                        ride.getStartPlace(),
                        ride.getStartDate() != null ? ride.getStartDate().format(formatter) : "Non définie",
                        ride.getFee()
                });
            }
        }
    }

    private void openRideDetails() {
        int row = rideTable.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un ride.");
            return;
        }

        int rideNum = (int) rideTableModel.getValueAt(row, 0);
        Ride selectedRide = selectedCategory.getCalendar()
                .getRides()
                .stream()
                .filter(r -> r.getNum() == rideNum)
                .findFirst()
                .orElse(null);

        if (selectedRide == null) {
            JOptionPane.showMessageDialog(this, "Erreur : ride introuvable.");
            return;
        }
        new RideDetailsFrame(selectedRide);
    }
}
