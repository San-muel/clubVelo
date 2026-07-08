package be.senechal.clubVelo.dao;

import be.senechal.clubVelo.model.Calendar;
import be.senechal.clubVelo.model.Category;

public interface CalendarDao {
	Calendar getCalendarForCategory(Category category);
}
