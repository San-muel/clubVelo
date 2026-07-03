package be.senechal.clubVelo.model;

import be.senechal.clubVelo.dao.DaoFactory;

public class Club {

    private int id;
    private String nom;
    private String rue;
    private String numero;
    private String codePostal;
    private String ville;

    public Club() {
    }

    public Club(int id, String nom, String rue, String numero, String codePostal, String ville) {
        this.id = id;
        setNom(nom);
        setRue(rue);
        setNumero(numero);
        setCodePostal(codePostal);
        setVille(ville);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNom() { return nom; }
    public String getRue() { return rue; }
    public String getNumero() { return numero; }
    public String getCodePostal() { return codePostal; }
    public String getVille() { return ville; }

    public void setNom(String nom) { this.nom = nom; }
    public void setRue(String rue) { this.rue = rue; }
    public void setNumero(String numero) { this.numero = numero; }
    public void setCodePostal(String codePostal) { this.codePostal = codePostal; }
    public void setVille(String ville) { this.ville = ville; }

    public String getAdresseComplete() {
        return rue + " " + numero + ", " + codePostal + " " + ville;
    }

    public static Club getClub() {
        return DaoFactory.getClubDao().getClub();
    }

    @Override
    public String toString() {
        return nom + " — " + getAdresseComplete();
    }
}
