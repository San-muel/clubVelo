package be.senechal.clubVelo.model;

import java.util.Objects;

import be.senechal.clubVelo.dao.DaoFactory;

public class Bike {

	private int id;
	private double weight;
	private double length;
	private String type;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public double getLength() {
		return length;
	}

	public void setLength(double length) {
		this.length = length;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Bike() {
	}

	public Bike(int id, double weight, double length, String type) {
		this.id = id;
		setWeight(weight);
		setLength(length);
		setType(type);
	}

	public boolean delete() {
		return DaoFactory.getBikeDao().delete(this.id);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Bike)) return false;
		Bike other = (Bike) o;
		return id == other.id;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public String toString() {
		return type + " (" + length + "m, " + weight + "kg)";
	}
}
