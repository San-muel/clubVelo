package be.senechal.clubVelo.dao;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import be.senechal.clubVelo.db.Database;
import be.senechal.clubVelo.model.Category;
import be.senechal.clubVelo.model.Manager;
import be.senechal.clubVelo.model.Treasurer;
import be.senechal.clubVelo.model.Person;
import be.senechal.clubVelo.util.PasswordUtil;

public class PersonDaoImpl implements PersonDao {

    private static final Logger LOGGER = Logger.getLogger(PersonDaoImpl.class.getName());

    @Override
    public Person login(String username, String password) {
        int personId = -1;
        String hashedPassword = PasswordUtil.hash(password);
        String sqlPerson = "SELECT PersonID, PersonName, PersonFirstname, tel FROM Person WHERE username = ? AND password = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlPerson)) {

            ps.setString(1, username);
            ps.setString(2, hashedPassword);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    personId = rs.getInt("PersonID");
                    String name = rs.getString("PersonName");
                    String firstname = rs.getString("PersonFirstname");
                    String tel = rs.getString("tel");

                    // 1. EST-CE UN MANAGER ?
                    if (isManager(personId, conn)) {
                    	Manager m = new Manager(name, firstname, tel, personId, hashedPassword, username, null);
                        loadManagerData(m, conn);
                        return m;
                    }

                    // 2. EST-CE UN TRESORIER ?
                    else if (isTreasurer(personId, conn)) {
                        return new Treasurer(name, firstname, tel, personId, hashedPassword, username);
                    }

                    // 3. EST-CE UN MEMBER ?
                    else if (isMember(personId, conn)) {
                        return DaoFactory.getMemberDao().getByUsernameAndPassword(username, password);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du login de " + username, e);
        }

        return null;
    }

    @Override
    public boolean usernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM Person WHERE username = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du contrôle d'unicité du username " + username, e);
            return false;
        }
    }

    // --- Méthodes privées ---

    private boolean isManager(int id, Connection conn) throws SQLException {
        String sql = "SELECT count(*) FROM Manager WHERE person_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        }
        return false;
    }

    private boolean isTreasurer(int id, Connection conn) throws SQLException {
        String sql = "SELECT count(*) FROM Tresurer WHERE tresurer_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        }
        return false;
    }

    private void loadManagerData(Manager m, Connection conn) throws SQLException {
        String sql = "SELECT Category_id FROM Manager WHERE person_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, m.getId());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int catId = rs.getInt("Category_id");
                Category cat = DaoFactory.getCategoryDao().getById(catId);
                m.setCategory(cat);
            }
        }
    }

    private boolean isMember(int id, Connection conn) throws SQLException {
        String sql = "SELECT count(*) FROM Member WHERE PersonID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        }
        return false;
    }
}
