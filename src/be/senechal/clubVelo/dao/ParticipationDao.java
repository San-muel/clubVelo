package be.senechal.clubVelo.dao;

import java.util.List;

import be.senechal.clubVelo.model.Participation;

public interface ParticipationDao {
    List<Participation> getByRideId(int rideId);
    boolean addParticipation(int rideId, int memberId, Integer bikeId);
    boolean removeParticipation(int rideId, int memberId);
    boolean deleteByRideId(int rideId);
}
