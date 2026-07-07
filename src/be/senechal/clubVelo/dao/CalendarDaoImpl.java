package be.senechal.clubVelo.dao;

import java.util.List;
import be.senechal.clubVelo.model.*;
import be.senechal.clubVelo.model.Calendar;

public class CalendarDaoImpl implements CalendarDao {

    @Override
    public Calendar getCalendarForCategory(Category category) {
        List<Ride> rides = DaoFactory.getRideDao().getByCategoryId(category.getId());
        for (Ride ride : rides) {
            List<Vehicle> vehicles = DaoFactory.getVehicleDao().getByRideId(ride.getNum());
            ride.setVehicles(vehicles);
        }
        return new Calendar(category, rides);
    }
}
