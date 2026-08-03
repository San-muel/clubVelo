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

    public boolean processRidePayments(Ride ride) {
        if (ride.isPaid()) {
            return false;
        }

        List<VehicleImpact> impacts = computeRideImpact(ride);

        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);
            try {
                for (VehicleImpact impact : impacts) {
                    Member driver = DaoFactory.getMemberDao().getById(impact.getDriver().getId());
                    for (Member p : impact.getPassengers()) {
                        Member passenger = DaoFactory.getMemberDao().getById(p.getId());
                        claimFee(conn, ride, passenger, impact.getFeePerPassenger());
                    }
                    payDriver(conn, ride, driver, impact.getDriverGain());
                }

                DaoFactory.getRideDao().markAsPaid(conn, ride.getNum());
                conn.commit();
                ride.setPaid(true);
                return true;
            } catch (Exception e) {
                conn.rollback();
                LOGGER.log(Level.SEVERE, "Erreur lors du traitement des paiements du ride " + ride.getNum()
                        + " — rollback effectué", e);
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur de connexion lors du traitement des paiements du ride " + ride.getNum(), e);
            return false;
        }
    }

    private void claimFee(Connection conn, Ride ride, Member member, double amount) throws Exception {
        double newBalance = member.getBalance() - amount;
        DaoFactory.getMemberDao().updateBalance(conn, member.getId(), newBalance);
        member.setBalance(newBalance);
        DaoFactory.getPaymentDao().record(conn, ride.getNum(), member.getId(), amount, "debit_passager");
    }

    private void payDriver(Connection conn, Ride ride, Member member, double amount) throws Exception {
        double newBalance = member.getBalance() + amount;
        DaoFactory.getMemberDao().updateBalance(conn, member.getId(), newBalance);
        member.setBalance(newBalance);
        DaoFactory.getPaymentDao().record(conn, ride.getNum(), member.getId(), amount, "credit_chauffeur");
    }

    public List<Payment> getPaymentHistory() {
        return DaoFactory.getPaymentDao().getAll();
    }

    public double getTotalReimbursedToDrivers(List<Payment> history) {
        return history.stream().filter(Payment::isDriverReimbursement).mapToDouble(Payment::getMontant).sum();
    }

    public double getTotalCollectedFromPassengers(List<Payment> history) {
        return history.stream().filter(Payment::isPassengerPayment).mapToDouble(Payment::getMontant).sum();
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
