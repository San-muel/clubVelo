package be.senechal.clubVelo.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

import be.senechal.clubVelo.model.Manager;
import be.senechal.clubVelo.model.Person;
import be.senechal.clubVelo.model.Ride;
import be.senechal.clubVelo.session.Session;

public class ManagerFrame extends JFrame {

    private Manager loggedManager;
    private JTable rideTable;
    private DefaultTableModel rideTableModel;

    public ManagerFrame() {
        Person currentUser = Session.getCurrentUser();
        if (currentUser instanceof Manager) {
            this.loggedManager = (Manager) currentUser;
        } else {
            JOptionPane.showMessageDialog(this, "Accès refusé : Vous n'êtes pas Manager.");
            dispose();
            return;
        }

        setTitle("Espace Manager - " + loggedManager.getName());
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ---------- HEADER ----------
        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel welcomeLabel = new JLabel("Bienvenue, " + loggedManager.getFirstname());
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);

        String catName = (loggedManager.getCategory() != null) ?
                         loggedManager.getCategory().getClass().getSimpleName() : "Aucune";
        JLabel catLabel = new JLabel("Gestion de la catégorie : " + catName);
        catLabel.setHorizontalAlignment(SwingConstants.CENTER);

        topPanel.add(welcomeLabel);
        topPanel.add(catLabel);
        add(topPanel, BorderLayout.NORTH);

        // ---------- TABLEAU DES RIDES ----------
        String[] columns = { "Num", "Départ", "Date", "Prix (€)" };
        rideTableModel = new DefaultTableModel(columns, 0);
        rideTable = new JTable(rideTableModel);
        rideTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(rideTable);
        add(scrollPane, BorderLayout.CENTER);

        loadRideTable();

        // ---------- BOUTONS (BAS) ----------
        JPanel bottomPanel = new JPanel();

        JButton createRideBtn = new JButton("Créer un nouveau Ride");
        createRideBtn.addActionListener(e -> {
            if (loggedManager.getCategory() == null) {
                JOptionPane.showMessageDialog(this,
                        "Aucune catégorie n'est assignée à votre compte : impossible de créer un ride.",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            new CreateRideFrame(loggedManager, this);
        });

        bottomPanel.add(refreshBtn);
        bottomPanel.add(Box.createHorizontalStrut(20));
        bottomPanel.add(logoutBtn);

        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    public void loadRideTable() {
        rideTableModel.setRowCount(0);

        if (loggedManager.getCategory() != null && loggedManager.getCategory().getCalendar() != null) {
            List<Ride> rides = loggedManager.getCategory().getCalendar().getRides();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            for (Ride ride : rides) {
                String dateStr = (ride.getStartDate() != null) ? ride.getStartDate().format(formatter) : "Non définie";

                rideTableModel.addRow(new Object[]{
                        ride.getNum(),
                        ride.getStartPlace(),
                        dateStr,
                        ride.getFee()
                });
            }
        }
    }

}
