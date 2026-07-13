package be.senechal.clubVelo.dao;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import be.senechal.clubVelo.db.Database;

public class CotisationDaoImpl implements CotisationDao {

    private static final Logger LOGGER = Logger.getLogger(CotisationDaoImpl.class.getName());

    @Override
    public boolean isPaid(int memberId, int annee) {
        String sql = "SELECT COUNT(*) FROM Cotisation WHERE member_id = ? AND annee = ? AND datePaiement IS NOT NULL";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, memberId);
            ps.setInt(2, annee);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du contrôle de cotisation du membre " + memberId, e);
            return false;
        }
    }

    @Override
    public boolean encaisser(int memberId, int annee, double montant) {
        if (isPaid(memberId, annee)) {
            return false;
        }
        String sql = "INSERT INTO Cotisation (member_id, annee, montant, datePaiement) VALUES (?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, memberId);
            ps.setInt(2, annee);
            ps.setDouble(3, montant);
            ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'encaissement de la cotisation du membre " + memberId, e);
            return false;
        }
    }
}
