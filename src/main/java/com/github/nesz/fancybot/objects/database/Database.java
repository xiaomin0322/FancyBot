package com.github.nesz.fancybot.objects.database;

import com.github.nesz.fancybot.FancyBot;
import com.github.nesz.fancybot.config.Config;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Consumer;

public class Database
{

    private static HikariDataSource dataSource;

    public Database()
    {
        dataSource = new HikariDataSource();

        final String host = Config.MYSQL_HOST;
        final String base = Config.MYSQL_BASE;
        final String user = Config.MYSQL_USER;
        final String pass = Config.MYSQL_PASS;
        final String port = Config.MYSQL_PORT;

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

    public HikariDataSource getDataSource()
    {
        return dataSource;
    }

    public void executeQuery(final String query, final Consumer<ResultSet> action)
    {
        try (final Connection connection = dataSource.getConnection();
             final PreparedStatement statement = connection.prepareStatement(query);
             final ResultSet result = statement.executeQuery())
        {

            action.accept(result);
        }
        catch (final SQLException e)
        {
            FancyBot.LOGGER.error("[Database] Could not execute query", e);
        }
    }

    public int executeUpdate(final String query)
    {
        try (final Connection connection = dataSource.getConnection();
             final PreparedStatement statement = connection.prepareStatement(query))
        {

            if (statement == null)
            {
                return 0;
            }

            return statement.executeUpdate();
        }
        catch (SQLException e)
        {
            FancyBot.LOGGER.error("[Database] Could not execute update", e);
        }
        return 0;
    }
}