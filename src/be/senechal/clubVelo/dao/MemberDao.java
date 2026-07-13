package be.senechal.clubVelo.dao;

import java.sql.Connection;
import java.util.List;

import be.senechal.clubVelo.model.Member;

public interface MemberDao {
    Member getById(int id);
    List<Member> getAll();
    Member getByUsernameAndPassword(String username, String password);
    List<Member> getAllPassager(int vehicleId, int rideId);
    boolean create(Member member);
    boolean updateBalance(int memberId, double newBalance);
    boolean updateBalance(Connection conn, int memberId, double newBalance) throws java.sql.SQLException;
    boolean update(Member member);
}
