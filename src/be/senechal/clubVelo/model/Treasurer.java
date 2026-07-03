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

}
