package be.senechal.clubVelo.dao;

import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import be.senechal.clubVelo.db.Database;
import be.senechal.clubVelo.model.Category;
import be.senechal.clubVelo.model.Member;
import be.senechal.clubVelo.util.PasswordUtil;

public class MemberDaoImpl implements MemberDao {

	private static final Logger LOGGER = Logger.getLogger(MemberDaoImpl.class.getName());

	private Map<Integer, Member> cache = new HashMap<>();

	private static final String BASE_QUERY =
			"SELECT p.PersonID, p.PersonName, p.PersonFirstname, p.tel, p.username, p.password, m.balance "
			+ "FROM Member m JOIN Person p ON m.PersonID = p.PersonID ";

	private Member buildMember(ResultSet rs) throws SQLException {
		Member m = new Member(rs.getString("PersonName"), rs.getString("PersonFirstname"), rs.getString("tel"),
				rs.getInt("PersonID"), rs.getString("password"), rs.getString("username"));
		m.setBalance(rs.getDouble("balance"));
		return m;
	}

	@Override
	public Member getById(int id) {
		if (cache.containsKey(id))
			return cache.get(id);

		try (Connection conn = Database.getConnection();
		     PreparedStatement stmt = conn.prepareStatement(BASE_QUERY + "WHERE m.PersonID = ?")) {
			stmt.setInt(1, id);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					Member m = buildMember(rs);
					cache.put(id, m);
					return m;
				}
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Erreur lors de la lecture du membre " + id, e);
		}
		return null;
	}

	@Override
	public List<Member> getAll() {
		List<Member> list = new ArrayList<>();
		try (Connection conn = Database.getConnection();
		     Statement st = conn.createStatement();
		     ResultSet rs = st.executeQuery(BASE_QUERY)) {
			while (rs.next()) {
				Member m = buildMember(rs);
				cache.put(m.getId(), m);
				list.add(m);
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Erreur lors de la lecture des membres", e);
		}
		return list;
	}

	@Override
	public Member getByUsernameAndPassword(String username, String password) {
	    String sql = BASE_QUERY + "WHERE p.username = ? AND p.password = ?";

	    try (Connection conn = Database.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {

	        stmt.setString(1, username);
	        stmt.setString(2, PasswordUtil.hash(password));

	        try (ResultSet rs = stmt.executeQuery()) {
	            if (rs.next()) {
	                Member m = buildMember(rs);

	                cache.put(m.getId(), m);
	                m.setCategories(DaoFactory.getCategoryDao().getByMemberId(m.getId()));
	                m.setVehicles(DaoFactory.getVehicleDao().getByMemberId(m.getId()));
	                m.setBikes(DaoFactory.getBikeDao().getAllBikeOfMember(m.getId()));

	                return m;
	            }
	        }

	    } catch (Exception e) {
	        LOGGER.log(Level.SEVERE, "Erreur lors du login du membre " + username, e);
	    }

	    return null;
	}

	@Override
	public List<Member> getAllPassager(int vehicleId, int rideId) {
	    List<Member> passagers = new ArrayList<>();
	    String sql = "SELECT i.member_id FROM Inscription i "
	               + "WHERE i.ride_id = ? AND i.vehicle_id = ? AND i.member_id IS NOT NULL "
	               + "AND i.member_id <> (SELECT v.member_id FROM Vehicle v WHERE v.VehicleID = ?)";

	    try (Connection conn = Database.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {

	        stmt.setInt(1, rideId);
	        stmt.setInt(2, vehicleId);
	        stmt.setInt(3, vehicleId);

	        try (ResultSet rs = stmt.executeQuery()) {
	            while (rs.next()) {
	                Member m = getById(rs.getInt("member_id"));
	                if (m != null) {
	                    passagers.add(m);
	                }
	            }
	        }

	    } catch (Exception e) {
	        LOGGER.log(Level.SEVERE, "Erreur lors de la lecture des passagers du véhicule " + vehicleId, e);
	    }

	    return passagers;
	}

	@Override
	public boolean create(Member member) {
	    Connection conn = null;
	    PreparedStatement psCheck = null;
	    PreparedStatement psPerson = null;
	    PreparedStatement psMember = null;
	    PreparedStatement psCat = null;
	    ResultSet rs = null;

	    String sqlCheck = "SELECT COUNT(*) FROM Person WHERE username = ?";
	    String sqlPerson = "INSERT INTO Person (PersonName, PersonFirstname, tel, username, password) VALUES (?, ?, ?, ?, ?)";
	    String sqlMember = "INSERT INTO Member (PersonID, balance) VALUES (?, ?)";
	    String sqlCat = "INSERT INTO MemberCategory (memberId, categoryId) VALUES (?, ?)";

	    try {
	        conn = Database.getConnection();
	        conn.setAutoCommit(false);

	        psCheck = conn.prepareStatement(sqlCheck);
	        psCheck.setString(1, member.getUsername());
	        try (ResultSet rsCheck = psCheck.executeQuery()) {
	            if (rsCheck.next() && rsCheck.getInt(1) > 0) {
	                conn.rollback();
	                return false;
	            }
	        }

	        psPerson = conn.prepareStatement(sqlPerson, Statement.RETURN_GENERATED_KEYS);
	        psPerson.setString(1, member.getName());
	        psPerson.setString(2, member.getFirstname());
	        psPerson.setString(3, member.getTel());
	        psPerson.setString(4, member.getUsername());
	        psPerson.setString(5, PasswordUtil.hash(member.getPassword()));

	        int rowsP = psPerson.executeUpdate();
	        if (rowsP == 0) throw new SQLException("Echec insert Person");

	        int personId = -1;
	        rs = psPerson.getGeneratedKeys();
	        if (rs.next()) {
	            personId = rs.getInt(1);
	            member.setId(personId);
	        } else {
	            throw new SQLException("Impossible de récupérer l'ID Person");
	        }

	        psMember = conn.prepareStatement(sqlMember);
	        psMember.setInt(1, personId);
	        psMember.setDouble(2, 0.0);

	        int rowsM = psMember.executeUpdate();
	        if (rowsM == 0) throw new SQLException("Echec insert Member");

	        if (member.getCategories() != null && !member.getCategories().isEmpty()) {
	            psCat = conn.prepareStatement(sqlCat);
	            for (Category cat : member.getCategories()) {
	                psCat.setInt(1, personId);
	                psCat.setInt(2, cat.getId());
	                psCat.addBatch();
	            }
	            psCat.executeBatch();
	        }

	        conn.commit();
	        return true;

	    } catch (Exception e) {
	        LOGGER.log(Level.SEVERE, "Erreur lors de la création du membre", e);
	        try {
	            if (conn != null) {
	                conn.rollback();
	            }
	        } catch (SQLException ex) {
	            LOGGER.log(Level.SEVERE, "Erreur lors du rollback", ex);
	        }
	        return false;
	    } finally {
	        try {
	            if (rs != null) rs.close();
	            if (psCheck != null) psCheck.close();
	            if (psPerson != null) psPerson.close();
	            if (psMember != null) psMember.close();
	            if (psCat != null) psCat.close();
	            if (conn != null) {
	                conn.setAutoCommit(true);
	                conn.close();
	            }
	        } catch (SQLException e) {
	            LOGGER.log(Level.SEVERE, "Erreur lors de la fermeture des ressources JDBC", e);
	        }
	    }
	}

	@Override
	public boolean updateBalance(int memberId, double newBalance) {
	    String sql = "UPDATE Member SET balance = ? WHERE PersonID = ?";
	    try (Connection conn = Database.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {

	        ps.setDouble(1, newBalance);
	        ps.setInt(2, memberId);

	        int rows = ps.executeUpdate();

	        if (rows > 0) {
	            if (cache.containsKey(memberId)) {
	                cache.get(memberId).setBalance(newBalance);
	            }
	            return true;
	        }
	        return false;

	    } catch (Exception e) {
	        LOGGER.log(Level.SEVERE, "Erreur lors de la mise à jour du solde du membre " + memberId, e);
	        return false;
	    }
	}

	@Override
	public boolean updateBalance(Connection conn, int memberId, double newBalance) throws SQLException {
	    String sql = "UPDATE Member SET balance = ? WHERE PersonID = ?";
	    try (PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setDouble(1, newBalance);
	        ps.setInt(2, memberId);
	        int rows = ps.executeUpdate();
	        if (rows > 0) {
	            if (cache.containsKey(memberId)) {
	                cache.get(memberId).setBalance(newBalance);
	            }
	            return true;
	        }
	        return false;
	    }
	}

	@Override
	public boolean update(Member member) {
	    String sql = "UPDATE Person SET PersonName = ?, PersonFirstname = ?, tel = ? WHERE PersonID = ?";
	    try (Connection conn = Database.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setString(1, member.getName());
	        ps.setString(2, member.getFirstname());
	        ps.setString(3, member.getTel());
	        ps.setInt(4, member.getId());
	        if (ps.executeUpdate() > 0) {
	            cache.put(member.getId(), member);
	            return true;
	        }
	        return false;
	    } catch (Exception e) {
	        LOGGER.log(Level.SEVERE, "Erreur lors de la mise à jour du membre " + member.getId(), e);
	        return false;
	    }
	}

	public void invalidate(int id) {
		cache.remove(id);
	}

	public void clearCache() {
		cache.clear();
	}
}
