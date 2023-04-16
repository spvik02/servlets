package org.clevertec.providers;

import org.clevertec.utils.ParametersUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

public class RepositoryDatabase {

    private Connection connection;

    private static RepositoryDatabase instance;

    private RepositoryDatabase(String url, String user, String password) {
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns instance of database if it's exists otherwise creates it with the properties (url, user, password)
     * specified in app.properties.
     *
     * @return instance of RepositoryDatabase
     */
    public static RepositoryDatabase getInstance() {
        if (instance == null) {
            Map<String, Object> yamlMap = ParametersUtil.getMapFromYaml("application.yml");
            Map<String, Object> dbMap = (Map<String, Object>) yamlMap.get("database");
            String url = (String) dbMap.get("url");
            String user = (String) dbMap.get("user");
            String password = (String) dbMap.get("password");
            instance = new RepositoryDatabase(url, user, password);
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}
