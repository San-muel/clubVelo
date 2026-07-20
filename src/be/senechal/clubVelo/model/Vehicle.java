package be.senechal.clubVelo.model;

import java.util.*;
import be.senechal.clubVelo.dao.DaoFactory;

public class Vehicle {

    public static final int MIN_SEATS = 1;
    public static final int MAX_SEATS = 9;
    public static final int MIN_BIKE_SPOTS = 0;
    public static final int MAX_BIKE_SPOTS = 10;

    private int id;
    private int seatNumber;
    private int bikeSpotNumber;
    private String model;
    private Member driver;
    private Map<Ride, List<Member>> passengersPerRide = new HashMap<>();
    private Map<Ride, List<Bike>> bikesPerRide = new HashMap<>();

    public int getId() { return id; }
    public int getSeatNumber() { return seatNumber; }
    public int getBikeSpotNumber() { return bikeSpotNumber; }
    public String getModel() { return model; }
    public Member getDriver() { return driver; }

    public Map<Ride, List<Member>> getPassengersPerRide() {
		return passengersPerRide;
	}
	public void setPassengersPerRide(Map<Ride, List<Member>> passengersPerRide) {
		this.passengersPerRide = passengersPerRide;
	}
	public Map<Ride, List<Bike>> getBikesPerRide() {
		return bikesPerRide;
	}
	public void setBikesPerRide(Map<Ride, List<Bike>> bikesPerRide) {
		this.bikesPerRide = bikesPerRide;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setSeatNumber(int seatNumber) {
		this.seatNumber = validateSeatNumber(seatNumber);
	}
	public void setBikeSpotNumber(int bikeSpotNumber) {
		this.bikeSpotNumber = validateBikeSpotNumber(bikeSpotNumber);
	}
	public void setModel(String model) {
		this.model = model;
	}
	public void setDriver(Member driver) {
		this.driver = driver;
	}

	public Vehicle() {
	}

	public Vehicle(int id, int seatNumber, int bikeSpotNumber, String model, Member driver) {
        this.id = id;
        setSeatNumber(seatNumber);
        setBikeSpotNumber(bikeSpotNumber);
        setModel(model);
        setDriver(driver);
    }


    private static int validateSeatNumber(int seatNumber) {
        if (seatNumber < MIN_SEATS || seatNumber > MAX_SEATS) {
            throw new IllegalArgumentException(
                    "Le nombre de places passagers doit être compris entre " + MIN_SEATS + " et " + MAX_SEATS);
        }
        return seatNumber;
    }

    private static int validateBikeSpotNumber(int bikeSpotNumber) {
        if (bikeSpotNumber < MIN_BIKE_SPOTS || bikeSpotNumber > MAX_BIKE_SPOTS) {
            throw new IllegalArgumentException(
                    "Le nombre de racks à vélo doit être compris entre " + MIN_BIKE_SPOTS + " et " + MAX_BIKE_SPOTS);
        }
        return bikeSpotNumber;
    }

    public void addRide(Ride ride) {
        if (!passengersPerRide.containsKey(ride)) {
            passengersPerRide.put(ride, new ArrayList<>());
            bikesPerRide.put(ride, new ArrayList<>());
        }
    }

    public List<Member> getPassengersForRide(Ride ride) {
        return passengersPerRide.getOrDefault(ride, new ArrayList<>());
    }
    public List<Bike> getBikesForRide(Ride ride) {
        return bikesPerRide.getOrDefault(ride, new ArrayList<>());
    }

    public boolean isPassengerFull(Ride ride) {
        return getPassengersForRide(ride).size() >= seatNumber;
    }

    public boolean isBikeFull(Ride ride) {
        return getBikesForRide(ride).size() >= bikeSpotNumber;
    }

    public boolean addPassengerForRide(Ride ride, Member member) {
        List<Member> list = passengersPerRide.computeIfAbsent(ride, r -> new ArrayList<>());

        if (list.size() >= seatNumber)
            return false;

        if (!list.contains(member)) {
            if (!uploadPassenger(ride.getNum(), member.getId())) {
                return false;
            }
            list.add(member);
        }

        return true;
    }

    public boolean addBikeForRide(Ride ride, Bike bike) {
        List<Bike> list = bikesPerRide.computeIfAbsent(ride, r -> new ArrayList<>());

        if (list.size() >= bikeSpotNumber)
            return false;

        if (!list.contains(bike)) {
            if (!uploadBike(ride.getNum(), bike.getId())) {
                return false;
            }
            list.add(bike);
        }

        return true;
    }

    public void loadVehicle(Ride ride) {
    	List<Member> members = DaoFactory.getMemberDao().getAllPassager(id, ride.getNum());
        passengersPerRide.put(ride, members != null ? members : new ArrayList<>());

        List<Bike> bikes = DaoFactory.getBikeDao().getAllBike(id, ride.getNum());
    	bikesPerRide.put(ride, bikes != null ? bikes : new ArrayList<>());
    }

    private boolean uploadPassenger(int rideId, int memberId) {
    	return DaoFactory.getInscriptionDao().addInscription(rideId, id, memberId, null);
    }

    private boolean uploadBike(int rideId, int bikeId) {
        return DaoFactory.getInscriptionDao().addInscription(rideId, id, null, bikeId);
    }

    public static boolean create(Vehicle vehicle) {
    	return DaoFactory.getVehicleDao().create(vehicle);
    }

    public boolean delete() {
        return DaoFactory.getVehicleDao().delete(this.id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vehicle)) return false;
        Vehicle other = (Vehicle) o;
        return id == other.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return (model != null && !model.isBlank() ? model : "Véhicule") + " — " + seatNumber + " places";
    }
}
