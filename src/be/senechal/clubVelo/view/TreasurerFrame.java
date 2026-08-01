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

    // ---------------------------------------------------------
    // PANNEAU 3 : SUIVI DES PAIEMENTS
    // ---------------------------------------------------------

}
