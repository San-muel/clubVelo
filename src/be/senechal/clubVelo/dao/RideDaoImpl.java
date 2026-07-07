package be.senechal.clubVelo.dao;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import be.senechal.clubVelo.db.Database;
import be.senechal.clubVelo.model.*;

public class RideDaoImpl implements RideDao {

	private static final Logger LOGGER = Logger.getLogger(RideDaoImpl.class.getName());

	private static final String BASE_COLUMNS =
			"RideID, startPlace, startDate, fee, category_id, paid, distanceKm";

	@Override
	public Ride getById(int id) {
		try (Connection conn = Database.getConnection();
		     PreparedStatement stmt = conn.prepareStatement(
				     "SELECT " + BASE_COLUMNS + " FROM Ride WHERE RideID=?")) {
			stmt.setInt(1, id);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return buildAndEnrichRide(rs);
				}
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Erreur lors de la lecture du ride " + id, e);
		}
		return null;
	}

	private Ride buildAndEnrichRide(ResultSet rs) throws SQLException {
		Timestamp ts = rs.getTimestamp("startDate");
		LocalDateTime startDate = ts != null ? ts.toLocalDateTime() : null;

		Ride ride = new Ride(rs.getInt("RideID"), rs.getString("startPlace"), startDate,
				rs.getDouble("fee"), new ArrayList<>());
		ride.setDistanceKm(rs.getDouble("distanceKm"));
		ride.setPaid(rs.getBoolean("paid"));

		int rideId = ride.getNum();

		ride.getVehicles().addAll(DaoFactory.getVehicleDao().getByRideId(rideId));

		int categoryId = rs.getInt("category_id");
		if (!rs.wasNull()) {
			ride.setCategory(DaoFactory.getCategoryDao().getById(categoryId));
		}

		return ride;
	}

	@Override
	public List<Ride> getAll() {
		List<Ride> list = new ArrayList<>();
		try (Connection conn = Database.getConnection();
		     Statement stmt = conn.createStatement();
		     ResultSet rs = stmt.executeQuery("SELECT " + BASE_COLUMNS + " FROM Ride")) {
			while (rs.next()) {
				list.add(buildAndEnrichRide(rs));
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Erreur lors de la lecture des rides", e);
		}
		return list;
	}

	@Override
	public List<Ride> getByCategoryId(int categoryId) {
		List<Ride> list = new ArrayList<>();
		try (Connection conn = Database.getConnection();
		     PreparedStatement stmt = conn.prepareStatement(
				     "SELECT " + BASE_COLUMNS + " FROM Ride WHERE category_id = ?")) {
			stmt.setInt(1, categoryId);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					list.add(buildAndEnrichRide(rs));
				}
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Erreur lors de la lecture des rides de la catégorie " + categoryId, e);
		}
		return list;
	}

	@Override
	public boolean addVehicleToRide(int rideNum, int vehicleId, int memberid) {
		try (Connection conn = Database.getConnection();
		     PreparedStatement ps = conn.prepareStatement(
				     "INSERT INTO Inscription (ride_id, vehicle_id, member_id) VALUES (?, ?, ?)")) {
			ps.setInt(1, rideNum);
			ps.setInt(2, vehicleId);
			ps.setInt(3, memberid);

			return ps.executeUpdate() == 1;
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Erreur lors de l'ajout du véhicule " + vehicleId + " au ride " + rideNum, e);
			return false;
		}
	}

	@Override
	public boolean create(Ride ride, int managerId, int categoryId) {
	    String sql = "INSERT INTO Ride (startPlace, startDate, fee, manager_id, category_id, distanceKm, paid) "
	            + "VALUES (?, ?, ?, ?, ?, ?, ?)";
	    try (Connection conn = Database.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

	        ps.setString(1, ride.getStartPlace());

	        if (ride.getStartDate() != null) {
	            ps.setTimestamp(2, Timestamp.valueOf(ride.getStartDate()));
	        } else {
	            ps.setNull(2, java.sql.Types.TIMESTAMP);
	        }

	        ps.setDouble(3, ride.getFee());
	        ps.setInt(4, managerId);
	        ps.setInt(5, categoryId);
	        ps.setDouble(6, ride.getDistanceKm());
	        ps.setBoolean(7, ride.isPaid());

	        int rows = ps.executeUpdate();

	        if (rows > 0) {
	            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
	                if (generatedKeys.next()) {
	                    ride.setNum(generatedKeys.getInt(1));
	                }
	            }
	            return true;
	        }
	    } catch (Exception e) {
	        LOGGER.log(Level.SEVERE, "Erreur lors de la création du ride", e);
	    }
	    return false;
	}

	@Override
	public boolean markAsPaid(int rideNum) {
		try (Connection conn = Database.getConnection();
		     PreparedStatement ps = conn.prepareStatement("UPDATE Ride SET paid = ? WHERE RideID = ?")) {
			ps.setBoolean(1, true);
			ps.setInt(2, rideNum);
			return ps.executeUpdate() > 0;
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Erreur lors du marquage payé du ride " + rideNum, e);
			return false;
		}
	}

	@Override
	public boolean markAsPaid(Connection conn, int rideNum) throws SQLException {
		try (PreparedStatement ps = conn.prepareStatement("UPDATE Ride SET paid = ? WHERE RideID = ?")) {
			ps.setBoolean(1, true);
			ps.setInt(2, rideNum);
			return ps.executeUpdate() > 0;
		}
	}

	@Override
	public boolean delete(int rideNum) {
		DaoFactory.getInscriptionDao().deleteByRideId(rideNum);
		DaoFactory.getParticipationDao().deleteByRideId(rideNum);
		try (Connection conn = Database.getConnection();
		     PreparedStatement ps = conn.prepareStatement("DELETE FROM Ride WHERE RideID = ?")) {
			ps.setInt(1, rideNum);
			return ps.executeUpdate() > 0;
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Erreur lors de la suppression du ride " + rideNum, e);
			return false;
		}
	}
}
