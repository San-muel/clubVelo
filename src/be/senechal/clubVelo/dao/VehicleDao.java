package be.senechal.clubVelo.dao;

import java.util.List;

import be.senechal.clubVelo.model.Vehicle;

public interface VehicleDao {
	Vehicle getById(int id);

	List<Vehicle> getAll();
	List<Vehicle> getByMemberId(int memberId);
	List<Vehicle> getByRideId(int rideId);
	boolean create(Vehicle vehicle);
	boolean delete(int vehicleId);
}
