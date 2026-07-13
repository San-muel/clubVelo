package be.senechal.clubVelo.dao;

import java.util.List;

import be.senechal.clubVelo.model.Category;

public interface CategoryDao {
	Category createCategoryInstance(int id, String type);
	Category getById(int id);
	List<Category> getAll();
	List<Category> getByMemberId(int memberId);
	boolean addCategoryToMember(int memberId, int categoryId);
}
