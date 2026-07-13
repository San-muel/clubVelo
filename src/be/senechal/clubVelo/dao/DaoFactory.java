package be.senechal.clubVelo.dao;

public class DaoFactory {

    // ---------- SINGLETON DAO INSTANCES ----------

    private static final MemberDao memberDao = new MemberDaoImpl();
    private static final CategoryDao categoryDao = new CategoryDaoImpl();
    private static final VehicleDao vehicleDao = new VehicleDaoImpl();
    private static final RideDao rideDao = new RideDaoImpl();
    private static final InscriptionDao inscriptionDao = new InscriptionDaoImpl();
    private static final CalendarDao calendarDao = new CalendarDaoImpl();
    private static final BikeDao bikeDao = new BikeDaoImpl();
    private static final PersonDao personDao = new PersonDaoImpl();
    private static final ParticipationDao participationDao = new ParticipationDaoImpl();
    private static final ClubDao clubDao = new ClubDaoImpl();
    private static final CotisationDao cotisationDao = new CotisationDaoImpl();
    private static final PaymentDao paymentDao = new PaymentDaoImpl();

    // ---------- GETTERS ----------

    public static MemberDao getMemberDao() {
        return memberDao;
    }

    public static CategoryDao getCategoryDao() {
        return categoryDao;
    }

    public static VehicleDao getVehicleDao() {
        return vehicleDao;
    }

    public static RideDao getRideDao() {
        return rideDao;
    }

    public static InscriptionDao getInscriptionDao() {
        return inscriptionDao;
    }

    public static CalendarDao getCalendarDao() {
        return calendarDao;
    }

    public static BikeDao getBikeDao() {
        return bikeDao;
    }

    public static PersonDao getPersonDao() {
        return personDao;
    }

    public static ParticipationDao getParticipationDao() {
        return participationDao;
    }

    public static ClubDao getClubDao() {
        return clubDao;
    }

    public static CotisationDao getCotisationDao() {
        return cotisationDao;
    }

    public static PaymentDao getPaymentDao() {
        return paymentDao;
    }
}
