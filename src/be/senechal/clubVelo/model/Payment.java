package be.senechal.clubVelo.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Payment {
    public static final String SENS_CREDIT_CHAUFFEUR = "credit_chauffeur";
    public static final String SENS_DEBIT_PASSAGER = "debit_passager";

    private final int id;
    private final int rideId;
    private final int memberId;
    private final String memberName;
    private final String rideLabel;
    private final double montant;
    private final String sens;
    private final LocalDateTime paymentDate;

    public Payment(int id, int rideId, int memberId, String memberName, String rideLabel,
            double montant, String sens, LocalDateTime paymentDate) {
        this.id = id;
        this.rideId = rideId;
        this.memberId = memberId;
        this.memberName = memberName;
        this.rideLabel = rideLabel;
        this.montant = montant;
        this.sens = sens;
        this.paymentDate = paymentDate;
    }

    public int getId() { return id; }
    public int getRideId() { return rideId; }
    public int getMemberId() { return memberId; }
    public String getMemberName() { return memberName; }
    public String getRideLabel() { return rideLabel; }
    public double getMontant() { return montant; }
    public String getSens() { return sens; }
    public LocalDateTime getPaymentDate() { return paymentDate; }

    public boolean isDriverReimbursement() { return SENS_CREDIT_CHAUFFEUR.equals(sens); }
    public boolean isPassengerPayment() { return SENS_DEBIT_PASSAGER.equals(sens); }

    public String getSensLabel() {
        if (isDriverReimbursement()) return "Remboursement chauffeur";
        if (isPassengerPayment()) return "Paiement passager";
        return sens;
    }

    public String getFormattedDate() {
        return paymentDate != null ? paymentDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "?";
    }

    @Override
    public String toString() {
        return getFormattedDate() + " — Ride " + rideLabel + " — " + memberName + " — "
             + getSensLabel() + " : " + String.format("%.2f€", montant);
    }
}
