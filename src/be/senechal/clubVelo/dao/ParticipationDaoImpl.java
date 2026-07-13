package be.senechal.clubVelo.dao;

import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import be.senechal.clubVelo.db.Database;
import be.senechal.clubVelo.model.Participation;

public class ParticipationDaoImpl implements ParticipationDao {

    private static final Logger LOGGER = Logger.getLogger(ParticipationDaoImpl.class.getName());

    @Override
    public List<Participation> getByRideId(int rideId) {
        List<Participation> list = new ArrayList<>();
        String sql = "SELECT participationID, ride_id, member_id, bike_id FROM Participation WHERE ride_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, rideId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int bikeId = rs.getInt("bike_id");
                    Integer bike = rs.wasNull() ? null : bikeId;
                    list.add(new Participation(rs.getInt("participationID"), rs.getInt("ride_id"),
                            rs.getInt("member_id"), bike));
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la lecture des participations du ride " + rideId, e);
        }
        return list;
    }

    @Override
    public boolean addParticipation(int rideId, int memberId, Integer bikeId) {
        String sql = "INSERT INTO Participation (ride_id, member_id, bike_id) VALUES (?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, rideId);
            stmt.setInt(2, memberId);
            if (bikeId != null) {
                stmt.setInt(3, bikeId);
            } else {
                stmt.setNull(3, Types.INTEGER);
            }
            return stmt.executeUpdate() == 1;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'ajout d'une participation", e);
            return false;
        }
    }

    @Override
    public boolean removeParticipation(int rideId, int memberId) {
        String sql = "DELETE FROM Participation WHERE ride_id = ? AND member_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, rideId);
            stmt.setInt(2, memberId);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du retrait d'une participation", e);
            return false;
        }
    }

    @Override
    public boolean deleteByRideId(int rideId) {
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM Participation WHERE ride_id = ?")) {
            stmt.setInt(1, rideId);
            stmt.executeUpdate();
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la suppression en cascade des participations du ride " + rideId, e);
            return false;
        }
    }
}
