package be.senechal.clubVelo.db;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Database {

    private static final Logger LOGGER = Logger.getLogger(Database.class.getName());

    private static final String DB_FILENAME = "SenechalSamuel.accdb";

    private static final String CONFIG_FILE = "config/database.properties";
    private static final String ENV_VAR = "CLUBVELO_DB_URL";
    private static final String SYS_PROP = "clubvelo.db.url";

    private static volatile String resolvedUrl;

    private Database() {
    }

    public static Connection getConnection() throws Exception {
        return DriverManager.getConnection(resolveUrl());
    }

    private static String resolveUrl() {
        if (resolvedUrl != null) {
            return resolvedUrl;
        }

        String fromEnv = System.getenv(ENV_VAR);
        if (fromEnv != null && !fromEnv.isBlank()) {
            return resolvedUrl = fromEnv.trim();
        }

        String fromSysProp = System.getProperty(SYS_PROP);
        if (fromSysProp != null && !fromSysProp.isBlank()) {
            return resolvedUrl = fromSysProp.trim();
        }

        String fromFile = readUrlFromConfigFile();
        if (fromFile != null && !fromFile.isBlank()) {
            return resolvedUrl = fromFile.trim();
        }

        String absolutePath = new File(DB_FILENAME).getAbsolutePath();
        return resolvedUrl = "jdbc:ucanaccess://" + absolutePath;
    }

    private static String readUrlFromConfigFile() {
        Path path = Path.of(CONFIG_FILE);
        if (!Files.isRegularFile(path)) {
            return null;
        }
        try (InputStream in = Files.newInputStream(path)) {
            Properties props = new Properties();
            props.load(in);
            return props.getProperty("db.url");
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Erreur lors de la lecture du fichier de configuration " + CONFIG_FILE, e);
            return null;
        }
    }
}
