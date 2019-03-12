package com.github.nesz.fancybot.objects.database;

import com.github.nesz.fancybot.FancyBot;
import com.github.nesz.fancybot.config.Config;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Consumer;

public class Database {

    private static HikariDataSource dataSource;

    public Database() {
        dataSource = new HikariDataSource();

        String host = Config.MYSQL_HOST;
        String base = Config.MYSQL_BASE;
        String user = Config.MYSQL_USER;
        String pass = Config.MYSQL_PASS;
        int    port = Integer.valueOf(Config.MYSQL_PORT); //ik d:

        dataSource.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + base);
        dataSource.setUsername(user);
        dataSource.setPassword(pass);

        dataSource.addDataSourceProperty("cachePrepStmts", true);
        dataSource.addDataSourceProperty("prepStmtCacheSize", 250);
        dataSource.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        dataSource.addDataSourceProperty("useServerPrepStmts", true);
        dataSource.addDataSourceProperty("rewriteBatchedStatements", true);

        dataSource.setConnectionTimeout(15000);
        dataSource.setMaximumPoolSize(5);
    }

    public HikariDataSource getDataSource() {
        return dataSource;
    }

    public void executeQuery(String query, Consumer<ResultSet> action) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet result = statement.executeQuery()) {

            action.accept(result);
        } catch (SQLException e) {
            FancyBot.LOG.error("Could not execute query", e);
        }
    }

    public int executeUpdate(String query) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            if (statement == null) {
                return 0;
            }

            return statement.executeUpdate();
        } catch (SQLException e) {
            FancyBot.LOG.error("Could not execute update", e);
        }
        return 0;
    }

    public ResultSet executeQuery(PreparedStatement preparedStatement) {
        ResultSet resultSet = null;
        try {
            resultSet = preparedStatement.executeQuery();
            resultSet.beforeFirst();
        }
        catch (SQLException e) {
            FancyBot.LOG.error("Could not execute query", e);
        }
        finally {
            try {
                preparedStatement.close();
                preparedStatement.getConnection().close();
            }
            catch (SQLException e) {
                FancyBot.LOG.error("Error occurred while trying to close connection", e);
            }
        }
        return resultSet;
    }

    public void executeUpdate(PreparedStatement preparedStatement) {
        try {
            preparedStatement.executeUpdate();
        }
        catch (SQLException e) {
            FancyBot.LOG.error("Could not execute update", e);
        }
        finally {
            try {
                preparedStatement.close();
                preparedStatement.getConnection().close();
            }
            catch (SQLException e) {
                FancyBot.LOG.error("Error occurred while trying to close connection", e);
            }
        }
    }
}