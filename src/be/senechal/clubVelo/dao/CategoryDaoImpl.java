package be.senechal.clubVelo.dao;

import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import be.senechal.clubVelo.db.Database;
import be.senechal.clubVelo.model.*;

public class CategoryDaoImpl implements CategoryDao {

	private static final Logger LOGGER = Logger.getLogger(CategoryDaoImpl.class.getName());

	@Override
	public Category createCategoryInstance(int id, String type) {
		switch (type) {
		case "RoadBike":
			return new RoadBike(id);
		case "Randonneur":
			return new Randonneur(id);
		case "Trial":
			return new Trial(id);
		case "Downhill":
			return new Downhill(id);
		default:
			throw new IllegalArgumentException("Type de catégorie inconnu: " + type);
		}
	}

	@Override
	public Category getById(int id) {
		try (Connection conn = Database.getConnection();
		     PreparedStatement stmt = conn.prepareStatement("SELECT CategoryID, Type FROM Category WHERE CategoryID=?")) {
			stmt.setInt(1, id);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return createCategoryInstance(rs.getInt("CategoryID"), rs.getString("Type"));
				}
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Erreur lors de la lecture de la catégorie " + id, e);
		}
		return null;
	}

	@Override
	public List<Category> getAll() {
		List<Category> list = new ArrayList<>();
		try (Connection conn = Database.getConnection();
		     Statement stmt = conn.createStatement();
		     ResultSet rs = stmt.executeQuery("SELECT CategoryID, Type FROM Category")) {
			while (rs.next()) {
				list.add(createCategoryInstance(rs.getInt("CategoryID"), rs.getString("Type")));
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Erreur lors de la lecture des catégories", e);
		}
		return list;
	}

	@Override
	public List<Category> getByMemberId(int memberId) {
		List<Category> list = new ArrayList<>();
		String sql = "SELECT c.CategoryID, c.Type FROM Category c "
				+ "JOIN MemberCategory mc ON mc.categoryId = c.CategoryID WHERE mc.memberId = ?";
		try (Connection conn = Database.getConnection();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, memberId);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					list.add(createCategoryInstance(rs.getInt("CategoryID"), rs.getString("Type")));
				}
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Erreur lors de la lecture des catégories du membre " + memberId, e);
		}
		return list;
	}

	@Override
	public boolean addCategoryToMember(int memberId, int categoryId) {
	    String sql = "INSERT INTO MemberCategory (memberId, categoryId) VALUES (?, ?)";

	    try (Connection conn = Database.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {

	        stmt.setInt(1, memberId);
	        stmt.setInt(2, categoryId);

	        return stmt.executeUpdate() > 0;

	    } catch (Exception e) {
	        LOGGER.log(Level.SEVERE, "Erreur lors de l'ajout de la catégorie au membre " + memberId, e);
	        return false;
	    }
	}
}
