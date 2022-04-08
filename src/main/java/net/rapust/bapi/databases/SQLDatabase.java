package net.rapust.bapi.databases;

import lombok.Getter;
import net.rapust.bapi.BossesAPI;
import net.rapust.bapi.utils.Logger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class SQLDatabase {

    @Getter
    private static SQLDatabase instance;
    public String url;

    public SQLDatabase() {
        instance = this;

        BossesAPI plugin = BossesAPI.getInstance();
        url = "jdbc:sqlite:"+plugin.getConfig().getString("sqlite.path").replace("%dataFolder%", plugin.getDataFolder().getAbsolutePath());

        try {
            Class.forName("org.sqlite.JDBC").newInstance();
        } catch (Exception e) {
            Logger.warn("Can't create SQLite database!");
            e.printStackTrace();
        }

        try {
            Connection connection = SQLDatabase.getInstance().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS BossesAPITable (boss_id INT, at_time BIGINT, players TEXT);");
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        } catch (Exception e) {
            Logger.warn("Error while setting up tables! (SQLite)");
            e.printStackTrace();
        }

    }

    public Connection getConnection() {
        try {
            return DriverManager.getConnection(url);
        } catch (Exception e) {
            Logger.warn("Can't create connection to the SQLite database!");
            e.printStackTrace();
            return null;
        }
    }

}
