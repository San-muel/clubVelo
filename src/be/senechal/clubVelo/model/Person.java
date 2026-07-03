package be.senechal.clubVelo.model;

import java.util.Objects;

import be.senechal.clubVelo.dao.DaoFactory;

public abstract class Person {
    private String name;
    private String firstname;
    private String tel;
    private int id;
    private String password;
    private String username;

    public Person(String name, String firstname, String tel, int id, String password, String username) {
        this.name = name;
        this.firstname = firstname;
        this.tel = tel;
        this.id = id;
        this.password = password;
        this.username = username;
    }

    protected Person(String name, String firstname, String tel, String password, String username) {
        this.name = name;
        this.firstname = firstname;
        this.tel = tel;
        this.password = password;
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getTel() {
        return tel;
    }

    public int getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public static Person loadPerson(String username, String password) {
        return DaoFactory.getPersonDao().login(username, password);
    }

    public static boolean usernameExists(String username) {
        return DaoFactory.getPersonDao().usernameExists(username);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person other = (Person) o;
        return id == other.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), id);
    }
}
