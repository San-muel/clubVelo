package be.senechal.clubVelo.dao;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import be.senechal.clubVelo.db.Database;

public class InscriptionDaoImpl implements InscriptionDao {

    private static final Logger LOGGER = Logger.getLogger(InscriptionDaoImpl.class.getName());

    @Override
    public boolean addInscription(int rideId, int vehicleId, Integer memberId, Integer bikeId) {
        if (exists(rideId, vehicleId, memberId, bikeId)) {
            return false;
        }
        String sql = "INSERT INTO Inscription (ride_id, vehicle_id, member_id, bike_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, rideId);
            stmt.setInt(2, vehicleId);
            setNullableInt(stmt, 3, memberId);
            setNullableInt(stmt, 4, bikeId);
            return stmt.executeUpdate() == 1;
        } catch (Exception e) {
            if (isUniqueConstraintViolation(e)) {
                LOGGER.log(Level.INFO, "Inscription refusée par la contrainte d'unicité (doublon)");
                return false;
            }
            LOGGER.log(Level.SEVERE, "Erreur lors de l'ajout d'une inscription", e);
            return false;
        }
    }

    private static boolean isUniqueConstraintViolation(Throwable e) {
        for (Throwable t = e; t != null; t = t.getCause()) {
            if (t instanceof SQLIntegrityConstraintViolationException) {
                return true;
            }
            if (t.getMessage() != null && t.getMessage().toLowerCase().contains("unique constraint")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean exists(int rideId, int vehicleId, Integer memberId, Integer bikeId) {
        String sql = "SELECT COUNT(*) FROM Inscription WHERE ride_id = ? AND vehicle_id = ? "
                + "AND member_id " + (memberId == null ? "IS NULL" : "= ?")
                + " AND bike_id " + (bikeId == null ? "IS NULL" : "= ?");
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            int idx = 1;
            stmt.setInt(idx++, rideId);
            stmt.setInt(idx++, vehicleId);
            if (memberId != null) stmt.setInt(idx++, memberId);
            if (bikeId != null) stmt.setInt(idx++, bikeId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du contrôle d'existence d'une inscription", e);
            return false;
        }
    }

    @Override
    public boolean deleteInscription(int rideId, int vehicleId, Integer memberId, Integer bikeId) {
        String sql = "DELETE FROM Inscription WHERE ride_id = ? AND vehicle_id = ? "
                + "AND member_id " + (memberId == null ? "IS NULL" : "= ?")
                + " AND bike_id " + (bikeId == null ? "IS NULL" : "= ?");
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            int idx = 1;
            stmt.setInt(idx++, rideId);
            stmt.setInt(idx++, vehicleId);
            if (memberId != null) stmt.setInt(idx++, memberId);
            if (bikeId != null) stmt.setInt(idx++, bikeId);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la désinscription", e);
            return false;
        }
    }

    @Override
    public boolean deleteByRideId(int rideId) {
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM Inscription WHERE ride_id = ?")) {
            stmt.setInt(1, rideId);
            stmt.executeUpdate();
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la suppression en cascade des inscriptions du ride " + rideId, e);
            return false;
        }
    }

    private static void setNullableInt(PreparedStatement stmt, int index, Integer value) throws SQLException {
        if (value != null) {
            stmt.setInt(index, value);
        } else {
            stmt.setNull(index, Types.INTEGER);
        }
    }
}
