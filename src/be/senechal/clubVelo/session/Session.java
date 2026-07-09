package be.senechal.clubVelo.session;

import be.senechal.clubVelo.model.Person;
import be.senechal.clubVelo.model.Manager;
import be.senechal.clubVelo.model.Member;
import be.senechal.clubVelo.model.Treasurer;

public class Session {

    private static Person currentUser;

    public static void login(Person user) {
        currentUser = user;
    }

    public static void logout() {
        currentUser = null;
    }

    public static Person getCurrentUser() {
        return currentUser;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static boolean isManager() {
        return currentUser instanceof Manager;
    }

    public static boolean isTreasurer() {
        return currentUser instanceof Treasurer;
    }

    public static boolean isMember() {
        return currentUser instanceof Member;
    }
}
