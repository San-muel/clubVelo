package be.senechal.clubVelo.dao;

import java.util.List;

import be.senechal.clubVelo.model.Bike;

public interface BikeDao {
	Bike getById(int id);
	List<Bike> getAllBike(int vehicleId, int rideId);
	List<Bike> getAllBikeOfMember(int member_id);
	boolean create(Bike bike, int memberId);
	boolean delete(int bikeId);
}
