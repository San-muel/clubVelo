package be.senechal.clubVelo.model;

import java.util.ArrayList;
import java.util.List;

import be.senechal.clubVelo.dao.DaoFactory;

public class Calendar {
	
	private Category category;
	private List<Ride> rides = new ArrayList<>();

	public Calendar(Category category, List<Ride> rides) {
		super();
		this.category = category;
		this.rides = rides;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public List<Ride> getRides() {
		return rides;
	}

	public void setRides(List<Ride> rides) {
		this.rides = rides;
	}

}
