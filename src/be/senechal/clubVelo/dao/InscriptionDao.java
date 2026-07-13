package be.senechal.clubVelo.dao;

public interface InscriptionDao {
    boolean addInscription(int rideId, int vehicleId, Integer memberId, Integer bikeId);
    boolean exists(int rideId, int vehicleId, Integer memberId, Integer bikeId);
    boolean deleteInscription(int rideId, int vehicleId, Integer memberId, Integer bikeId);
    boolean deleteByRideId(int rideId);
}
