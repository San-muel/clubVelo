package be.senechal.clubVelo.dao;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import be.senechal.clubVelo.db.Database;
import be.senechal.clubVelo.model.Club;

public class ClubDaoImpl implements ClubDao {

    private static final Logger LOGGER = Logger.getLogger(ClubDaoImpl.class.getName());

    @Override
    public Club getClub() {
        String sql = "SELECT ClubID, nom, rue, numero, codePostal, ville FROM Club";
        try (Connection conn = Database.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                return new Club(rs.getInt("ClubID"), rs.getString("nom"), rs.getString("rue"),
                        rs.getString("numero"), rs.getString("codePostal"), rs.getString("ville"));
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la lecture de l'adresse du club", e);
        }
        return null;
    }
}
