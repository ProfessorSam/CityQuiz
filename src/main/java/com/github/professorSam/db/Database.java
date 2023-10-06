package com.github.professorSam.db;

import com.github.professorSam.db.model.Group;
import com.github.professorSam.db.model.Player;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.UUID;

public class Database {
    private static HikariDataSource dataSource;
    private static final Logger logger = LoggerFactory.getLogger("Database");

    static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://mysql:3306/cityquiz");
        config.setUsername(System.getenv("MYSQL_USER"));
        config.setPassword(System.getenv("MYSQL_PASSWORD"));
        config.setMaximumPoolSize(10);
        config.setAutoCommit(true);
        dataSource = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static boolean testConnection(){
        try (Connection connection = getConnection()){
            return connection.isValid(10);
        } catch (SQLException e){
            return false;
        }
    }

    public static void setupTables() {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            // Create the Group table
            String createGroupTableSQL = "CREATE TABLE IF NOT EXISTS Groups (" +
                    "GroupID VARCHAR(36) PRIMARY KEY," +
                    "CurrentQuest INTEGER," +
                    "GroupName VARCHAR(255)" +
                    ")";
            statement.execute(createGroupTableSQL);
            //Create Players table
            String createPlayerTableSQL = "CREATE TABLE IF NOT EXISTS Players (" +
                    "ID VARCHAR(36) PRIMARY KEY," +
                    "GroupID VARCHAR(36)," +
                    "Name VARCHAR(255)," +
                    "Nationality VARCHAR(255)," +
                    "FOREIGN KEY (GroupID) REFERENCES Groups(GroupID)" +
                    ")";
            statement.execute(createPlayerTableSQL);
        } catch (SQLException e) {
            logger.error("Error setting up tables: " + e.getMessage());
        }
    }

    public static Player createPlayerAndAddToGroup(String playerName, String groupName, String nationality) {
        try (Connection connection = getConnection()) {
            Group group = getOrCreateGroup(connection, groupName);
            // Create a new player
            UUID playerID = UUID.randomUUID();
            String insertPlayerSQL = "INSERT INTO Players (ID, GroupID, Name, Nationality) VALUES (?, ?, ?, ?)";
            PreparedStatement playerStatement = connection.prepareStatement(insertPlayerSQL);
            playerStatement.setString(1, playerID.toString());
            playerStatement.setString(2, group.id().toString());
            playerStatement.setString(3, playerName);
            playerStatement.setString(4, nationality);
            playerStatement.executeUpdate();
            playerStatement.close();
            logger.info(playerName + " was added to group " + groupName);
            return new Player(playerID, playerName, nationality, group);
        } catch (SQLException e) {
            logger.error("Error creating player: " + e.getMessage());
            return null;
        }
    }

    private static Group getOrCreateGroup(Connection connection, String groupName) throws SQLException {
        String selectGroupIDSQL = "SELECT GroupID, CurrentQuest FROM Groups WHERE GroupName = ?";
        PreparedStatement groupStatement = connection.prepareStatement(selectGroupIDSQL);
        groupStatement.setString(1, groupName);
        ResultSet resultSet = groupStatement.executeQuery();
        if (resultSet.next()) {
            return new Group(UUID.fromString(resultSet.getString("GroupID")), groupName, resultSet.getInt("CurrentQuest"));
        } else {
            UUID uuid = UUID.randomUUID();
            String groupID = uuid.toString();
            String insertGroupSQL = "INSERT INTO Groups (GroupID, CurrentQuest, GroupName) VALUES (?, ?, ?)";
            PreparedStatement insertGroupStatement = connection.prepareStatement(insertGroupSQL);
            insertGroupStatement.setString(1, groupID);
            insertGroupStatement.setInt(2, 0); // Initialize CurrentQuest to 0
            insertGroupStatement.setString(3, groupName);
            insertGroupStatement.executeUpdate();
            insertGroupStatement.close();
            return new Group(uuid, groupName, 0);
        }
    }

    public static void close() {
        if (dataSource != null) {
            dataSource.close();
        }
    }

}
