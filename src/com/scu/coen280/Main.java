package com.scu.coen280;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.sql.*;

public class Main {

    public static void main(String[] args) {
        Main controller = new Main();

        try {
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("Loading MySql Driver Successfully");
        } catch (ClassNotFoundException e) {
            System.err.println("Failed to load MySql Driver");
            e.printStackTrace();
        }

        System.out.println("Connecting ...");

        try (
                Connection connection = DriverManager.getConnection(
                        "jdbc:mysql://127.0.0.1:3306/mySql",
                        "root",
                        "x9zi4cr@Eyjan"
                )
        ) {
            System.out.println("Successfully! Connection is built");
            controller.populateData(connection);
        } catch (SQLException e) {
            System.err.println("Failed to connect to MySql");
            e.printStackTrace();
        }
    }

    public void populateData(Connection connection) throws SQLException {
        try {
            Populate.populateBusiness(connection);
            Populate.populateReview(connection);
            Populate.populateCheckin(connection);
            Populate.populateUser(connection);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

}
