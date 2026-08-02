package be.senechal.clubVelo.model;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import be.senechal.clubVelo.dao.DaoFactory;
import be.senechal.clubVelo.db.Database;

public class Treasurer extends Person {

    private static final Logger LOGGER = Logger.getLogger(Treasurer.class.getName());

    public Treasurer(String name, String firstname, String tel, int id, String password, String username) {
        super(name, firstname, tel, id, password, username);
    }

    public double calculateAnnualFeeDue(Member member) {
        return member.calculateAnnualFee();
    }

    public boolean isMemberUpToDate(Member member, int annee) {
        return DaoFactory.getCotisationDao().isPaid(member.getId(), annee);
    }

    public boolean encaisserCotisation(Member member, int annee) {
        return DaoFactory.getCotisationDao().encaisser(member.getId(), annee, calculateAnnualFeeDue(member));
    }

    public List<VehicleImpact> computeRideImpact(Ride ride) {
        List<VehicleImpact> impacts = new ArrayList<>();
        for (Vehicle v : ride.getVehicles()) {
            if (v.getDriver() == null) {
                LOGGER.log(Level.WARNING, "Véhicule " + v.getId() + " sans chauffeur résolvable, ignoré pour le ride " + ride.getNum());
                continue;
            }
            v.loadVehicle(ride);
            List<Member> passengers = v.getPassengersForRide(ride);
            double feePerPassenger = passengers.isEmpty()
                    ? 0.0
                    : Math.round((ride.getFee() / passengers.size()) * 100.0) / 100.0;
            double driverGain = passengers.size() * feePerPassenger;
            impacts.add(new VehicleImpact(v, v.getDriver(), passengers, feePerPassenger, driverGain));
        }
        return impacts;
    }

    public static class VehicleImpact {
        private final Vehicle vehicle;
        private final Member driver;
        private final List<Member> passengers;
        private final double feePerPassenger;
        private final double driverGain;

        public VehicleImpact(Vehicle vehicle, Member driver, List<Member> passengers,
                double feePerPassenger, double driverGain) {
            this.vehicle = vehicle;
            this.driver = driver;
            this.passengers = passengers;
            this.feePerPassenger = feePerPassenger;
            this.driverGain = driverGain;
        }

        public Vehicle getVehicle() { return vehicle; }
        public Member getDriver() { return driver; }
        public List<Member> getPassengers() { return passengers; }
        public double getFeePerPassenger() { return feePerPassenger; }
        public double getDriverGain() { return driverGain; }
    }
}
