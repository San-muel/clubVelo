package be.senechal.clubVelo.dao;

import java.sql.Connection;
import java.util.List;

import be.senechal.clubVelo.model.Payment;

public interface PaymentDao {
    boolean record(Connection conn, int rideId, int memberId, double montant, String sens);
    List<Payment> getAll();
    List<Payment> getByRideId(int rideId);
}
