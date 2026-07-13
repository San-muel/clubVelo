package be.senechal.clubVelo.dao;

import be.senechal.clubVelo.model.Person;

public interface PersonDao {
    Person login(String username, String password);
    boolean usernameExists(String username);
}
