package com.github.professorSam.db;

import com.github.professorSam.Main;
import com.github.professorSam.db.model.Answer;
import com.github.professorSam.db.model.Group;
import com.github.professorSam.db.model.Player;
import com.github.professorSam.quest.Quest;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Database {
    private static final HikariDataSource dataSource;
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
            String createAnswersTableSQL = "CREATE TABLE IF NOT EXISTS Answers (" +
                    "UserID VARCHAR(36)," +
                    "QuestID VARCHAR(30)," +
                    "AnswerTimestamp TIMESTAMP," +
                    "QuestType VARCHAR(10)," +
                    "Content TEXT," +
                    "FOREIGN KEY (UserID) REFERENCES Players(ID)" +
                    ")";
            statement.execute(createAnswersTableSQL);
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

    public static Player getPlayer(String userID) {
        String selectPlayerAndGroupSQL = "SELECT p.ID AS PlayerID, p.Name AS PlayerName, " +
                "p.Nationality AS PlayerNationality, " +
                "g.GroupID, g.GroupName, g.CurrentQuest " +
                "FROM Players p " +
                "INNER JOIN Groups g ON p.GroupID = g.GroupID " +
                "WHERE p.ID = ?";
        try(Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(selectPlayerAndGroupSQL)){
            statement.setString(1, userID);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String playerName = resultSet.getString("PlayerName");
                String playerNationality = resultSet.getString("PlayerNationality");
                String groupID = resultSet.getString("GroupID");
                String groupName = resultSet.getString("GroupName");
                int currentQuest = resultSet.getInt("CurrentQuest");
                Group group = new Group(UUID.fromString(groupID), groupName, currentQuest);
                return new Player(UUID.fromString(userID), playerName, playerNationality, group);
            } else {
                return null;
            }
        } catch (SQLException e) {
            logger.error("SQL Exception on player query", e);
            return null;
        }
    }

    public static void addAnswerAndIncrementCurrentQuest(Answer answer){
        String insertAnswerSQL = "INSERT INTO Answers (UserID, QuestID, AnswerTimestamp, QuestType, Content) VALUES (?, ?, ?, ?, ?)";
        String incrementCurrentQuestSQL = "UPDATE Groups SET CurrentQuest = CurrentQuest + 1 WHERE GroupID = ?";
        try (Connection connection = getConnection();
             PreparedStatement insertStatement = connection.prepareStatement(insertAnswerSQL);
             PreparedStatement incrementStatement = connection.prepareStatement(incrementCurrentQuestSQL)){
            insertStatement.setString(1, answer.player().id().toString());
            insertStatement.setString(2, answer.quest().getId());
            insertStatement.setTimestamp(3, Timestamp.from(answer.answerTimestamp()));
            insertStatement.setString(4, answer.type().toString());
            insertStatement.setString(5, answer.content());
            insertStatement.executeUpdate();
            incrementStatement.setString(1, answer.player().group().id().toString());
            incrementStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("SQL Exception on answer update!", e);
        }
    }

    public static int getPlayerCount() {
        String query = "SELECT COUNT(*) AS PlayerCount FROM Players";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("PlayerCount");
            }
            return 0;
        } catch (SQLException e) {
            logger.error("Can't get player count! ", e);
            return -1;
        }
    }

    public static int getGroupCount() {
        String query = "SELECT COUNT(*) AS GroupCount FROM `Groups`";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("GroupCount");
            }
            return 0;
        } catch (SQLException e) {
            logger.error("Can't get group count! ", e);
            return -1;
        }
    }

    public static int getDoneGroups() {
        String query = "SELECT COUNT(*) AS GroupsDone FROM `Groups` WHERE CurrentQuest = " + Main.getInstance().getQuests().size();
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("GroupsDone");
            }
            return 0;
        } catch (SQLException e) {
            logger.error("Can't get done groups! ", e);
            return -1;
        }
    }

    public static HashMap<Quest, List<Answer>> getAnswers(){
        HashMap<Quest, List<Answer>> answersByQuest = new HashMap<>();

        String sql = "SELECT A.QuestID, A.AnswerTimestamp, A.QuestType, A.Content, " +
                "P.ID, P.Name, P.Nationality, G.GroupName " +
                "FROM Answers A " +
                "INNER JOIN Players P ON A.UserID = P.ID " +
                "INNER JOIN Groups G ON P.GroupID = G.GroupID";

        try (Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String questID = resultSet.getString("QuestID");
                Instant answerTimestamp = resultSet.getTimestamp("AnswerTimestamp").toInstant();
                String questType = resultSet.getString("QuestType");
                String content = resultSet.getString("Content");
                UUID playerID = UUID.fromString(resultSet.getString("ID"));
                String playerName = resultSet.getString("Name");
                String nationality = resultSet.getString("Nationality");
                String groupName = resultSet.getString("GroupName");

                Quest quest = getQuestByID(questID);
                Player player = new Player(playerID, playerName, nationality, new Group(UUID.randomUUID(), groupName, 0));
                Answer answer = new Answer(player, answerTimestamp, Answer.AnswerType.parse(questType), content, quest);
                answersByQuest.computeIfAbsent(quest, k -> new ArrayList<>()).add(answer);
            }
            return answersByQuest;
        } catch (SQLException e) {
            logger.error("Can't get answers!", e);
            return new HashMap<>();
        }
    }

    private static Quest getQuestByID(String id){
        for(Quest quest : Main.getInstance().getQuests()){
            if(quest.getId().equals(id)){
                return quest;
            }
        }
        return null;
    }

    public static HashMap<Group, List<Player>> getAllPlayersInGroups() {
        String queryPlayersAndGroupsSQL = "SELECT G.GroupID, G.GroupName, G.CurrentQuest, P.ID, P.Name, P.Nationality " +
                "FROM Groups G " +
                "LEFT JOIN Players P ON G.GroupID = P.GroupID";
        try (Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(queryPlayersAndGroupsSQL)){
            ResultSet resultSet = statement.executeQuery();
            HashMap<Group, List<Player>> groupPlayerMap = new HashMap<>();
            while (resultSet.next()){
                UUID groupID = UUID.fromString(resultSet.getString("GroupID"));
                String groupName = resultSet.getString("GroupName");
                UUID playerID = UUID.fromString(resultSet.getString("ID"));
                String playerName = resultSet.getString("Name");
                String nationality = resultSet.getString("Nationality");
                int currentQuest = resultSet.getInt("CurrentQuest");

                Group group = new Group(groupID, groupName, currentQuest);
                Player player = new Player(playerID, playerName, nationality, group);

                groupPlayerMap.computeIfAbsent(group, k -> new ArrayList<>()).add(player);
            }
            return groupPlayerMap;
        } catch (SQLException e) {
            logger.error("Can't get answers!", e);
            return new HashMap<>();
        }
    }


    public static void close() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}
