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
    private static final String DB_HOST = System.getenv("MYSQL_HOST");
    private static final String DB_PORT = System.getenv("MYSQL_PORT");
    private static final String DB_DATABASE = System.getenv("MYSQL_DATABASE");
    private static final String MSSQL_URL = System.getenv("MSSQL_JDBC_URL");
    private final static DBVendor DB_VENDOR;


    static {
        HikariConfig config = new HikariConfig();
        if(MSSQL_URL == null){
            DB_VENDOR = DBVendor.MY_SQL;
            logger.info("Using MySQL Driver");
            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            config.setJdbcUrl("jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_DATABASE);
            config.setUsername(System.getenv("MYSQL_USER"));
            config.setPassword(System.getenv("MYSQL_PASSWORD"));
        } else {
            DB_VENDOR = DBVendor.MS_SQL;
            logger.info("Using MSSQL Server driver");
            try {
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            config.setJdbcUrl(MSSQL_URL);
        }
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
            String createGroupTableSQL = getQuery(Query.CREATE_GROUP_TABLE);
            statement.execute(createGroupTableSQL);
            String createPlayerTableSQL = getQuery(Query.CREATE_PLAYERS_TABLE);
            statement.execute(createPlayerTableSQL);
            String createAnswersTableSQL = getQuery(Query.CREATE_ANSWERS_TABLE);
            statement.execute(createAnswersTableSQL);
            String createEndTimeTable = getQuery(Query.CREATE_END_TIME_TABLE);
            statement.execute(createEndTimeTable);
        } catch (SQLException e) {
            logger.error("Error setting up tables: " + e.getMessage());
        }
    }

    public static Player createPlayerAndAddToGroup(String playerName, String groupName, String nationality) {
        try (Connection connection = getConnection()) {
            Group group = getOrCreateGroup(connection, groupName);
            UUID playerID = UUID.randomUUID();
            String insertPlayerSQL = getQuery(Query.INSERT_PLAYER);
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

    public static Instant getEndTime(){
        String queryEndTimeSQL = getQuery(Query.SELECT_ENDTIME);
        try (Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(queryEndTimeSQL)){
            ResultSet resultSet = statement.executeQuery();
            if(!resultSet.next()){
                return null;
            }
            return resultSet.getTimestamp("EndTime").toInstant();
        } catch (SQLException e) {
            logger.error("Can't get end time: ", e);
            return null;
        }
    }

    public static void setEndTime(Instant instant){
        String insertEndTimeSQL = getQuery(Query.INSERT_ENDTIME);
        String deleteEndTimeSQL = getQuery(Query.DELETE_ENDTIME);
        try (Connection connection = getConnection();
            PreparedStatement updateStatement = connection.prepareStatement(insertEndTimeSQL);
            PreparedStatement deleteStatement = connection.prepareStatement(deleteEndTimeSQL)){
            deleteStatement.executeUpdate();
            updateStatement.setTimestamp(1, Timestamp.from(instant));
            updateStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Can't update end time!", e);
        }
    }

    private static Group getOrCreateGroup(Connection connection, String groupName) throws SQLException {
        String selectGroupIDSQL = getQuery(Query.SELECT_GROUPID_BY_NAME);
        PreparedStatement groupStatement = connection.prepareStatement(selectGroupIDSQL);
        groupStatement.setString(1, groupName);
        ResultSet resultSet = groupStatement.executeQuery();
        if (resultSet.next()) {
            return new Group(UUID.fromString(resultSet.getString("GroupID")), groupName, resultSet.getInt("CurrentQuest"));
        } else {
            UUID uuid = UUID.randomUUID();
            String groupID = uuid.toString();
            String insertGroupSQL = getQuery(Query.INSERT_GROUP);
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
        String selectPlayerAndGroupSQL = getQuery(Query.SELECT_PLAYER_AND_GROUP);
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
        String insertAnswerSQL = getQuery(Query.INSERT_ANSWER);
        String incrementCurrentQuestSQL = getQuery(Query.INCREMENT_QUEST);
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
        String query = getQuery(Query.SELECT_PLAYER_COUNT);
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
        String query = getQuery(Query.SELECT_GROUP_COUNT);
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
        String query = getQuery(Query.SELECT_GOUPS_DONE_COUNT);
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, Main.getInstance().getQuests().size());
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

        String sql = getQuery(Query.SELECT_ANSWERS);

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

    public static int getCurrentQuestByGroupID(String id){
        String queryCurrentQuestByGroupIDSQL = getQuery(Query.SELECT_CURRENT_QUEST_BY_GROUPID);
        try(Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(queryCurrentQuestByGroupIDSQL)){
            preparedStatement.setString(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(!resultSet.next()){
                logger.info("Unkown group requested current quest for id " + id);
                return -1;
            }
            return resultSet.getInt("CurrentQuest");
        } catch (SQLException e) {
            logger.error("Can't get group's current quest", e);
            return -1;
        }
    }

    public static HashMap<Group, List<Player>> getAllPlayersInGroups() {
        String queryPlayersAndGroupsSQL = getQuery(Query.SELECT_ALL_PLAYERS_AND_GROUPS);
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

    private static String getQuery(Query query){
        return switch (DB_VENDOR){
            case MS_SQL -> query.mssql;
            case MY_SQL -> query.mysql;
        };
    }


    public static void close() {
        if (dataSource != null) {
            dataSource.close();
        }
    }

    private enum DBVendor {
        MY_SQL,
        MS_SQL
    }

    private enum Query {
        CREATE_GROUP_TABLE(
                "CREATE TABLE IF NOT EXISTS Groups (" +
                "GroupID VARCHAR(36) PRIMARY KEY," +
                "CurrentQuest INTEGER," +
                "GroupName VARCHAR(255)" +
                ")",
                """
                        IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'Groups')BEGIN
                            CREATE TABLE Groups (
                                GroupID VARCHAR(36) PRIMARY KEY,
                                CurrentQuest INT,
                                GroupName VARCHAR(255)
                            );
                        END"""),
        CREATE_PLAYERS_TABLE("CREATE TABLE IF NOT EXISTS Players (" +
                "ID VARCHAR(36) PRIMARY KEY," +
                "GroupID VARCHAR(36)," +
                "Name VARCHAR(255)," +
                "Nationality VARCHAR(255)," +
                "FOREIGN KEY (GroupID) REFERENCES Groups(GroupID)" +
                ")",
                """
                   IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'Players')
                   BEGIN
                       CREATE TABLE Players (
                           ID VARCHAR(36) PRIMARY KEY,
                           GroupID VARCHAR(36),
                           Name VARCHAR(255),
                           Nationality VARCHAR(255),
                           FOREIGN KEY (GroupID) REFERENCES Groups(GroupID)
                       );
                   END"""),
        CREATE_ANSWERS_TABLE("CREATE TABLE IF NOT EXISTS Answers (" +
                "UserID VARCHAR(36)," +
                "QuestID VARCHAR(36)," +
                "AnswerTimestamp TIMESTAMP," +
                "QuestType VARCHAR(10)," +
                "Content TEXT," +
                "FOREIGN KEY (UserID) REFERENCES Players(ID)" +
                ")",
                """
                IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'Answers')
                BEGIN
                    CREATE TABLE Answers (
                        UserID VARCHAR(36),
                        QuestID VARCHAR(30),
                        AnswerTimestamp DATETIME,
                        QuestType VARCHAR(20),
                        Content TEXT,
                        FOREIGN KEY (UserID) REFERENCES Players(ID)
                    );
                END"""),
        CREATE_END_TIME_TABLE("CREATE TABLE IF NOT EXISTS EndTime (" +
                "EndTime TIMESTAMP" +
                ")",
                """
                        IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'EndTime')
                        BEGIN
                            CREATE TABLE EndTime (
                                EndTime DATETIME
                            );
                        END"""),
        INSERT_PLAYER("INSERT INTO Players (ID, GroupID, Name, Nationality) VALUES (?, ?, ?, ?)",
                "INSERT INTO Players (ID, GroupID, Name, Nationality) VALUES (?, ?, ?, ?);"),
        SELECT_ENDTIME("SELECT EndTime FROM EndTime LIMIT 1", "SELECT TOP 1 EndTime FROM EndTime;"),
        INSERT_ENDTIME("INSERT INTO EndTime (EndTime) VALUES (?)", "INSERT INTO EndTime (EndTime) VALUES (?);"),
        DELETE_ENDTIME("DELETE FROM EndTime", "DELETE FROM EndTime"),
        SELECT_GROUPID_BY_NAME("SELECT GroupID, CurrentQuest FROM Groups WHERE GroupName = ?", "SELECT GroupID, CurrentQuest FROM Groups WHERE GroupName = ?;"),
        INSERT_GROUP("INSERT INTO Groups (GroupID, CurrentQuest, GroupName) VALUES (?, ?, ?)", "INSERT INTO Groups (GroupID, CurrentQuest, GroupName) VALUES (?, ?, ?)"),
        SELECT_PLAYER_AND_GROUP("SELECT p.ID AS PlayerID, p.Name AS PlayerName, " +
                "p.Nationality AS PlayerNationality, " +
                "g.GroupID, g.GroupName, g.CurrentQuest " +
                "FROM Players p " +
                "INNER JOIN Groups g ON p.GroupID = g.GroupID " +
                "WHERE p.ID = ?",
                "SELECT p.ID AS PlayerID, p.Name AS PlayerName, " +
                        "p.Nationality AS PlayerNationality, " +
                        "g.GroupID, g.GroupName, g.CurrentQuest " +
                        "FROM Players p " +
                        "INNER JOIN Groups g ON p.GroupID = g.GroupID " +
                        "WHERE p.ID = ?;"),
        INSERT_ANSWER("INSERT INTO Answers (UserID, QuestID, AnswerTimestamp, QuestType, Content) VALUES (?, ?, ?, ?, ?)", "INSERT INTO Answers (UserID, QuestID, AnswerTimestamp, QuestType, Content) VALUES (?, ?, ?, ?, ?);"),
        INCREMENT_QUEST("UPDATE Groups SET CurrentQuest = CurrentQuest + 1 WHERE GroupID = ?", "UPDATE Groups SET CurrentQuest = CurrentQuest + 1 WHERE GroupID = ?;"),
        SELECT_PLAYER_COUNT("SELECT COUNT(*) AS PlayerCount FROM Players", "SELECT COUNT(*) AS PlayerCount FROM Players;"),
        SELECT_GROUP_COUNT("SELECT COUNT(*) AS GroupCount FROM `Groups`", "SELECT COUNT(*) AS GroupCount FROM Groups;"),
        SELECT_GOUPS_DONE_COUNT("SELECT COUNT(*) AS GroupsDone FROM `Groups` WHERE CurrentQuest = ?", "SELECT COUNT(*) AS GroupsDone FROM Groups WHERE CurrentQuest = ?;"),
        SELECT_ANSWERS("SELECT A.QuestID, A.AnswerTimestamp, A.QuestType, A.Content, " +
                "P.ID, P.Name, P.Nationality, G.GroupName " +
                "FROM Answers A " +
                "INNER JOIN Players P ON A.UserID = P.ID " +
                "INNER JOIN Groups G ON P.GroupID = G.GroupID", "SELECT A.QuestID, A.AnswerTimestamp, A.QuestType, A.Content, " +
                "P.ID, P.Name, P.Nationality, G.GroupName " +
                "FROM Answers A " +
                "INNER JOIN Players P ON A.UserID = P.ID " +
                "INNER JOIN Groups G ON P.GroupID = G.GroupID;"),
        SELECT_CURRENT_QUEST_BY_GROUPID("SELECT CurrentQuest FROM Groups WHERE GroupID = ?", "SELECT CurrentQuest FROM Groups WHERE GroupID = ?;"),
        SELECT_ALL_PLAYERS_AND_GROUPS("SELECT G.GroupID, G.GroupName, G.CurrentQuest, P.ID, P.Name, P.Nationality " +
                "FROM Groups G " +
                "LEFT JOIN Players P ON G.GroupID = P.GroupID", "SELECT G.GroupID, G.GroupName, G.CurrentQuest, P.ID, P.Name, P.Nationality " +
                "FROM Groups G " +
                "LEFT JOIN Players P ON G.GroupID = P.GroupID;");

        
        private final String mysql;
        private final String mssql;
        
        Query(String mysql, String mssql){
            this.mssql = mssql;
            this.mysql = mysql;
        }
    }
}
