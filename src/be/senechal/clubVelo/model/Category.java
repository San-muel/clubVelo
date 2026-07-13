package be.senechal.clubVelo.model;

import java.util.List;
import java.util.Objects;

import be.senechal.clubVelo.dao.DaoFactory;

public abstract class Category {
    private int id;
    private Calendar calendar;

    public Category(int id) {
        super();
        this.id = id;
    }

    public Calendar getCalendar() {
        if (calendar == null) {
            calendar = DaoFactory.getCalendarDao().getCalendarForCategory(this);
        }
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public void refreshCalendar() {
        this.calendar = null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    static public List<Category> getAllCategory() {
        return DaoFactory.getCategoryDao().getAll();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category other = (Category) o;
        return id == other.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), id);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
