package be.senechal.clubVelo.model;

public class Participation {
    private int id;
    private int rideId;
    private int memberId;
    private Integer bikeId;

    public Participation() {
    }

    public Participation(int id, int rideId, int memberId, Integer bikeId) {
        this.id = id;
        this.rideId = rideId;
        this.memberId = memberId;
        this.bikeId = bikeId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getRideId() { return rideId; }
    public void setRideId(int rideId) { this.rideId = rideId; }
    public int getMemberId() { return memberId; }
    public void setMemberId(int memberId) { this.memberId = memberId; }
    public Integer getBikeId() { return bikeId; }
    public void setBikeId(Integer bikeId) { this.bikeId = bikeId; }
}
