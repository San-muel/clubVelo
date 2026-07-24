package be.senechal.clubVelo.model;

import be.senechal.clubVelo.dao.*;
import java.util.ArrayList;
import java.util.List;

public class Member extends Person {
	public static final double BASE_ANNUAL_FEE = 20.0;
	public static final double FEE_PER_EXTRA_CATEGORY = 5.0;

	private double balance;
	private List<Category> categories = new ArrayList<>();
	private List<Vehicle> vehicles = new ArrayList<>();
	private List<Bike> bikes = new ArrayList<>();

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public List<Category> getCategories() {
		return categories;
	}

	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}

	public List<Vehicle> getVehicles() {
		return vehicles;
	}

	public void setVehicles(List<Vehicle> vehicles) {
		this.vehicles = vehicles;
	}

	public List<Bike> getBikes() {
		return bikes;
	}

	public void setBikes(List<Bike> bikes) {
		this.bikes = bikes;
	}

	public Member(String name, String firstname, String tel, int id, String password, String username) {
		super(name, firstname, tel, id, password, username);
	}

	public Member(String name, String firstname, String tel, String password, String username) {
		super(name, firstname, tel, password, username);
	}

	public double calculateAnnualFee() {
		double montantTotal = BASE_ANNUAL_FEE;
		if (this.categories != null && this.categories.size() > 1) {
			int categoriesSupplementaires = this.categories.size() - 1;
			montantTotal += (categoriesSupplementaires * FEE_PER_EXTRA_CATEGORY);
		}
		return montantTotal;
	}

	public boolean addBike(Bike bike) {
		if (!DaoFactory.getBikeDao().create(bike, this.getId())) {
			return false;
		}
		bikes.add(bike);
		return true;
	}

	static public boolean addMember(Member member) {
		return DaoFactory.getMemberDao().create(member);
	}

	public boolean updateInfo() {
		return DaoFactory.getMemberDao().update(this);
	}

	public boolean addCategory(Category category) {
		if (categories.contains(category)) {
			return false;
		}
		categories.add(category);
		return DaoFactory.getCategoryDao().addCategoryToMember(this.getId(), category.getId());
	}

	static public List<Member> getAllMember() {
		List<Member> members = DaoFactory.getMemberDao().getAll();
		for (Member m : members) {
			m.setCategories(DaoFactory.getCategoryDao().getByMemberId(m.getId()));
		}
		return members;
	}

	public static String validateRegistration(String name, String firstname, String username,
			String password, String passwordConfirm, List<Category> selectedCategories) {
		if (isBlank(name) || isBlank(firstname) || isBlank(username) || isBlank(password)) {
			return "Champs obligatoires manquants.";
		}
		if (!password.equals(passwordConfirm)) {
			return "Les mots de passe ne correspondent pas.";
		}
		if (selectedCategories == null || selectedCategories.isEmpty()) {
			return "Veuillez sélectionner au moins une catégorie !";
		}
		return null;
	}

	private static boolean isBlank(String s) {
		return s == null || s.trim().isEmpty();
	}

	@Override
	public String toString() {
		return getFirstname() + " " + getName();
	}
}
