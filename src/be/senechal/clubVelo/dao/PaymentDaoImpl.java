package be.senechal.clubVelo.dao;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import be.senechal.clubVelo.db.Database;
import be.senechal.clubVelo.model.Payment;

public class PaymentDaoImpl implements PaymentDao {

    private static final Logger LOGGER = Logger.getLogger(PaymentDaoImpl.class.getName());

    private static final String HISTORY_QUERY =
            "SELECT pay.paymentID, pay.ride_id, pay.member_id, per.PersonFirstname, per.PersonName, "
          + "r.startPlace, r.startDate, pay.montant, pay.sens, pay.paymentDate "
          + "FROM (Payment pay INNER JOIN Person per ON pay.member_id = per.PersonID) "
          + "INNER JOIN Ride r ON pay.ride_id = r.RideID ";

    private Payment buildPayment(ResultSet rs) throws SQLException {
        Timestamp ts = rs.getTimestamp("paymentDate");
        LocalDateTime paymentDate = ts != null ? ts.toLocalDateTime() : null;
        String memberName = rs.getString("PersonFirstname") + " " + rs.getString("PersonName");
        String rideLabel = "n°" + rs.getInt("ride_id") + " — " + rs.getString("startPlace");
        return new Payment(rs.getInt("paymentID"), rs.getInt("ride_id"), rs.getInt("member_id"),
                memberName, rideLabel, rs.getDouble("montant"), rs.getString("sens"), paymentDate);
    }

    @Override
    public boolean record(Connection conn, int rideId, int memberId, double montant, String sens) {
        String sql = "INSERT INTO Payment (ride_id, member_id, montant, sens, paymentDate) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, rideId);
            ps.setInt(2, memberId);
            ps.setDouble(3, montant);
            ps.setString(4, sens);
            ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'enregistrement du paiement (ride " + rideId + ")", e);
            return false;
        }
    }

    @Override
    public List<Payment> getAll() {
        List<Payment> list = new ArrayList<>();
        String sql = HISTORY_QUERY + "ORDER BY pay.paymentDate DESC";
        try (Connection conn = Database.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(buildPayment(rs));
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la lecture de l'historique des paiements", e);
        }
        return list;
    }

    @Override
    public List<Payment> getByRideId(int rideId) {
        List<Payment> list = new ArrayList<>();
        String sql = HISTORY_QUERY + "WHERE pay.ride_id = ? ORDER BY pay.paymentDate DESC";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, rideId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(buildPayment(rs));
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la lecture de l'historique des paiements du ride " + rideId, e);
        }
        return list;
    }
}
