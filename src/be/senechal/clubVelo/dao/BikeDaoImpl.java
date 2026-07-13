package be.senechal.clubVelo.dao;

import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import be.senechal.clubVelo.db.Database;
import be.senechal.clubVelo.model.Bike;

public class BikeDaoImpl implements BikeDao {

	private static final Logger LOGGER = Logger.getLogger(BikeDaoImpl.class.getName());

	@Override
	public Bike getById(int id) {
	    String sql = "SELECT bikeID, weight, type, lenght FROM Bike WHERE BikeID = ?";
	    try (Connection conn = Database.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setInt(1, id);
	        try (ResultSet rs = stmt.executeQuery()) {
	            if (rs.next()) {
	                return buildBike(rs);
	            }
	        }
	    } catch (Exception e) {
	        LOGGER.log(Level.SEVERE, "Erreur lors de la lecture du vélo " + id, e);
	    }
	    return null;
	}

	private Bike buildBike(ResultSet rs) throws SQLException {
		return new Bike(rs.getInt("bikeID"), rs.getDouble("weight"), rs.getDouble("lenght"), rs.getString("type"));
	}

	@Override
	public List<Bike> getAllBike(int vehicleId, int rideId) {
	    List<Bike> bikes = new ArrayList<>();
	    String sql = "SELECT bike_id FROM Inscription WHERE ride_id = ? AND vehicle_id = ? AND bike_id IS NOT NULL";

	    try (Connection conn = Database.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {

	        stmt.setInt(1, rideId);
	        stmt.setInt(2, vehicleId);

	        try (ResultSet rs = stmt.executeQuery()) {
	            while (rs.next()) {
	                Bike b = getById(rs.getInt("bike_id"));
	                if (b != null) {
	                    bikes.add(b);
	                }
	            }
	        }
	    } catch (Exception e) {
	        LOGGER.log(Level.SEVERE, "Erreur lors de la lecture des vélos du véhicule " + vehicleId, e);
	    }

	    return bikes;
	}

	@Override
	public List<Bike> getAllBikeOfMember(int member_id){
		List<Bike> bikes = new ArrayList<>();

	    String sql = "SELECT bikeid FROM Bike WHERE member_id = ?";

	    try (Connection conn = Database.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {

	        stmt.setInt(1, member_id);

	        try (ResultSet rs = stmt.executeQuery()) {
	            while (rs.next()) {
	                Bike b = getById(rs.getInt("bikeid"));
	                if (b != null) {
	                    bikes.add(b);
	                }
	            }
	        }

	    } catch (Exception e) {
	        LOGGER.log(Level.SEVERE, "Erreur lors de la lecture des vélos du membre " + member_id, e);
	    }

	    return bikes;
	}

	@Override
	public boolean create(Bike bike, int memberId) {
	    String sql = "INSERT INTO Bike (member_id, type, lenght, weight) VALUES (?, ?, ?, ?)";

	    try (Connection conn = Database.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

	        stmt.setInt(1, memberId);
	        stmt.setString(2, bike.getType());
	        stmt.setDouble(3, bike.getLength());
	        stmt.setDouble(4, bike.getWeight());

	        int rows = stmt.executeUpdate();

	        if (rows > 0) {
	            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
	                if (generatedKeys.next()) {
	                    bike.setId(generatedKeys.getInt(1));
	                }
	            }
	            return true;
	        }
	    } catch (Exception e) {
	        LOGGER.log(Level.SEVERE, "Erreur lors de la création du vélo", e);
	    }
	    return false;
	}

	@Override
	public boolean delete(int bikeId) {
		try (Connection conn = Database.getConnection();
		     PreparedStatement stmt = conn.prepareStatement("DELETE FROM Bike WHERE BikeID = ?")) {
			stmt.setInt(1, bikeId);
			return stmt.executeUpdate() > 0;
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Erreur lors de la suppression du vélo " + bikeId, e);
			return false;
		}
	}
}
