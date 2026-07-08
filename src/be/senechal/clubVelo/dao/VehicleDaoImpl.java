package be.senechal.clubVelo.dao;

import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import be.senechal.clubVelo.db.Database;
import be.senechal.clubVelo.model.Member;
import be.senechal.clubVelo.model.Vehicle;

public class VehicleDaoImpl implements VehicleDao {

	private static final Logger LOGGER = Logger.getLogger(VehicleDaoImpl.class.getName());

	@Override
	public Vehicle getById(int id) {
		String sql = "SELECT VehicleID, seatNumber, bikeSpotNumber, model, member_id FROM Vehicle WHERE VehicleID=?";
		try (Connection conn = Database.getConnection();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, id);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return buildVehicle(rs);
				}
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Erreur lors de la lecture du véhicule " + id, e);
		}
		return null;
	}

	private Vehicle buildVehicle(ResultSet rs) throws SQLException {
		Member driver = DaoFactory.getMemberDao().getById(rs.getInt("member_id"));
		return new Vehicle(rs.getInt("VehicleID"), rs.getInt("seatNumber"), rs.getInt("bikeSpotNumber"),
				rs.getString("model"), driver);
	}

	@Override
	public List<Vehicle> getAll() {
		List<Vehicle> list = new ArrayList<>();
		String sql = "SELECT VehicleID, seatNumber, bikeSpotNumber, model, member_id FROM Vehicle";
		try (Connection conn = Database.getConnection();
		     Statement st = conn.createStatement();
		     ResultSet rs = st.executeQuery(sql)) {
			while (rs.next()) {
				list.add(buildVehicle(rs));
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Erreur lors de la lecture des véhicules", e);
		}
		return list;
	}

	@Override
	public List<Vehicle> getByMemberId(int memberId) {
	    List<Vehicle> vehicles = new ArrayList<>();
	    String sql = "SELECT VehicleID, seatNumber, bikeSpotNumber, model, member_id FROM Vehicle WHERE member_id = ?";

	    try (Connection conn = Database.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {

	        stmt.setInt(1, memberId);

	        try (ResultSet rs = stmt.executeQuery()) {
	            while (rs.next()) {
	                vehicles.add(buildVehicle(rs));
	            }
	        }

	    } catch (Exception e) {
	        LOGGER.log(Level.SEVERE, "Erreur lors de la lecture des véhicules du membre " + memberId, e);
	    }

	    return vehicles;
	}

	@Override
	public List<Vehicle> getByRideId(int rideId) {
	    List<Vehicle> list = new ArrayList<>();

	    String sql = "SELECT DISTINCT vehicle_id FROM Inscription WHERE ride_id = ? AND vehicle_id IS NOT NULL";

	    try (Connection conn = Database.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setInt(1, rideId);
	        try (ResultSet rs = stmt.executeQuery()) {
	            while (rs.next()) {
	                Vehicle v = getById(rs.getInt("vehicle_id"));
	                if (v != null) {
	                    list.add(v);
	                }
	            }
	        }
	    } catch (Exception e) {
	        LOGGER.log(Level.SEVERE, "Erreur lors de la lecture des véhicules du ride " + rideId, e);
	    }

	    return list;
	}

	@Override
	public boolean create(Vehicle vehicle) {
	    String sql = "INSERT INTO Vehicle (member_id, seatNumber, bikeSpotNumber, model) VALUES (?, ?, ?, ?)";

	    try (Connection conn = Database.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

	        stmt.setInt(1, vehicle.getDriver().getId());
	        stmt.setInt(2, vehicle.getSeatNumber());
	        stmt.setInt(3, vehicle.getBikeSpotNumber());
	        stmt.setString(4, vehicle.getModel());

	        int rows = stmt.executeUpdate();

	        if (rows > 0) {
	            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
	                if (generatedKeys.next()) {
	                    vehicle.setId(generatedKeys.getInt(1));
	                }
	            }
	            return true;
	        }
	    } catch (Exception e) {
	        LOGGER.log(Level.SEVERE, "Erreur lors de la création du véhicule", e);
	    }
	    return false;
	}

	@Override
	public boolean delete(int vehicleId) {
		try (Connection conn = Database.getConnection();
		     PreparedStatement stmt = conn.prepareStatement("DELETE FROM Vehicle WHERE VehicleID = ?")) {
			stmt.setInt(1, vehicleId);
			return stmt.executeUpdate() > 0;
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Erreur lors de la suppression du véhicule " + vehicleId, e);
			return false;
		}
	}
}
