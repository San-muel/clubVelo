package be.senechal.clubVelo.dao;

public interface CotisationDao {
    boolean isPaid(int memberId, int annee);
    boolean encaisser(int memberId, int annee, double montant);
}
