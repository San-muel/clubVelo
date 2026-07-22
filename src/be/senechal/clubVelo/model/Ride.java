package be.senechal.clubVelo.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import be.senechal.clubVelo.dao.DaoFactory;

public class Ride {
    public static final double COUT_PAR_KM = 0.35;

    private int num;
    private String startPlace;
    private LocalDateTime startDate;
    private double fee;
    private double distanceKm;
    private boolean paid;
    private Category category;

    private List<Vehicle> vehicles = new ArrayList<>();

    public int getNum() { return num; }
    public void setNum(int num) { this.num = num; }
    public String getStartPlace() { return startPlace; }

    public void setStartPlace(String startPlace) {
        if (startPlace == null || startPlace.isBlank()) {
            throw new IllegalArgumentException("Le lieu de départ est obligatoire.");
        }
        this.startPlace = startPlace;
    }

    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    public double getFee() { return fee; }

    public void setFee(double fee) {
        if (fee < 0) {
            throw new IllegalArgumentException("Le prix d'un ride ne peut pas être négatif.");
        }
        this.fee = fee;
    }

    public double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(double distanceKm) { this.distanceKm = distanceKm; }
    public boolean isPaid() { return paid; }
    public void setPaid(boolean paid) { this.paid = paid; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    public List<Vehicle> getVehicles() { return vehicles; }
    public void setVehicles(List<Vehicle> vehicles) { this.vehicles = vehicles != null ? vehicles : new ArrayList<>(); }

    public Ride() {
    }

    public Ride(int num, String startPlace, LocalDateTime startDate, double fee, List<Vehicle> vehicles) {
        this.num = num;
        setStartPlace(startPlace);
        setStartDate(startDate);
        setFee(fee);
        setVehicles(vehicles);
    }

    public static String validateFutureDateForCreation(LocalDateTime startDate) {
        if (startDate == null) {
            return "La date de la balade est obligatoire.";
        }
        if (startDate.isBefore(LocalDateTime.now())) {
            return "La date de la balade doit être dans le futur.";
        }
        return null;
    }

    public static double computeFee(double distanceKm) {
        if (distanceKm <= 0) {
            throw new IllegalArgumentException("La distance doit être > 0.");
        }
        return Math.round((distanceKm * 2 * COUT_PAR_KM) * 100.0) / 100.0;
    }

    public int getNeededSeatNumber() {
        int count = 0;
        for (Vehicle v : vehicles) {
            count += v.getPassengersForRide(this).size();
        }
        return count;
    }

    public int getNeededBikeSpotNumber() {
        int count = 0;
        for (Vehicle v : vehicles) {
            count += v.getBikesForRide(this).size();
        }
        return count;
    }

    public int getTotalSeatCapacity() {
        int total = 0;
        for (Vehicle v : vehicles) {
            total += v.getSeatNumber();
        }
        return total;
    }

    public int getTotalBikeSpotNumber() {
        int total = 0;
        for (Vehicle v : vehicles) {
            total += v.getBikeSpotNumber();
        }
        return total;
    }

    public int getAvailableSeatNumber() {
        return getTotalSeatCapacity() - getNeededSeatNumber();
    }

    public int getAvailableBikeSpotNumber() {
        return getTotalBikeSpotNumber() - getNeededBikeSpotNumber();
    }

    public int getTotalInscriptionNumber() {
        return getNeededSeatNumber() + getNeededBikeSpotNumber();
    }

    public int getDemandedSeatNumber() {
        return DaoFactory.getParticipationDao().getByRideId(this.num).size();
    }

    public int getDemandedBikeSpotNumber() {
        return (int) DaoFactory.getParticipationDao().getByRideId(this.num).stream()
                .filter(p -> p.getBikeId() != null)
                .count();
    }

    public boolean isRegistrationOpen() {
        return startDate != null && startDate.isAfter(LocalDateTime.now());
    }

    public boolean addVehicle(Vehicle vehicle, Member member) {
        if (!isRegistrationOpen()) {
            return false;
        }
        vehicles.add(vehicle);
        vehicle.addRide(this);
        return DaoFactory.getRideDao().addVehicleToRide(this.getNum(), vehicle.getId(), member.getId());
    }

    public boolean addInscription(int vehicleId, int memberId, int bikeId) {
        if (!isRegistrationOpen()) {
            return false;
        }
        return DaoFactory.getInscriptionDao().addInscription(this.getNum(), vehicleId, memberId, bikeId);
    }

    public boolean addParticipation(Member member, Bike bike) {
        if (!isRegistrationOpen()) {
            return false;
        }
        return DaoFactory.getParticipationDao().addParticipation(this.getNum(), member.getId(),
                bike != null ? bike.getId() : null);
    }

    public boolean isMemberParticipating(Member member) {
        return DaoFactory.getParticipationDao().getByRideId(this.num).stream()
                .anyMatch(p -> p.getMemberId() == member.getId());
    }

    public void loadVehiclesOccupancy() {
        for (Vehicle v : vehicles) {
            v.loadVehicle(this);
        }
    }

    public boolean isMemberSeated(Member member) {
        if (member == null) {
            return false;
        }
        for (Vehicle v : vehicles) {
            if (member.equals(v.getDriver())) {
                return true;
            }
            if (v.getPassengersForRide(this).contains(member)) {
                return true;
            }
        }
        return false;
    }

    public boolean isMemberBikeSeated(Member member) {
        if (member == null) {
            return false;
        }
        List<Bike> memberBikes = member.getBikes();
        for (Vehicle v : vehicles) {
            for (Bike b : v.getBikesForRide(this)) {
                if (memberBikes.contains(b)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean reserveSeat(Vehicle vehicle, Member member) {
        if (!isRegistrationOpen() || vehicle == null || member == null || !vehicles.contains(vehicle)) {
            return false;
        }
        if (isMemberSeated(member)) {
            return false;
        }
        return vehicle.addPassengerForRide(this, member);
    }

    public boolean reserveBikeSpot(Vehicle vehicle, Member member, Bike bike) {
        if (!isRegistrationOpen() || vehicle == null || member == null || bike == null || !vehicles.contains(vehicle)) {
            return false;
        }
        if (isMemberBikeSeated(member)) {
            return false;
        }
        return vehicle.addBikeForRide(this, bike);
    }

    public boolean delete() {
        if (this.paid) {
            return false;
        }
        return DaoFactory.getRideDao().delete(this.num);
    }

    static public List<Ride> getAllRide(){
        return DaoFactory.getRideDao().getAll();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ride)) return false;
        Ride other = (Ride) o;
        return num == other.num;
    }

    @Override
    public int hashCode() {
        return Objects.hash(num);
    }

    @Override
    public String toString() {
        return "Ride n°" + num + " — " + startPlace + " le "
             + (startDate != null ? startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "?");
    }
}
