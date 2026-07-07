package be.senechal.clubVelo.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import be.senechal.clubVelo.model.Ride;

public interface RideDao {
	Ride getById(int id);
	List<Ride> getAll();
	List<Ride> getByCategoryId(int categoryId);
	boolean addVehicleToRide(int rideNum, int vehicleId, int memberid);
	boolean create(Ride ride, int managerId, int categoryId);
	boolean markAsPaid(int rideNum);
	boolean markAsPaid(Connection conn, int rideNum) throws SQLException;
	boolean delete(int rideNum);
}
