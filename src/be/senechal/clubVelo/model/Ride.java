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
