package be.senechal.clubVelo.model;

public class Manager extends Person {
    
    private Category category;

    
    public Manager(String name, String firstname, String tel, int id, String password, String username, Category category) {
        super(name, firstname, tel, id, password, username);
        this.category = category;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}