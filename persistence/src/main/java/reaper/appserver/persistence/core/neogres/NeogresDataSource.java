package reaper.appserver.persistence.core.neogres;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import reaper.appserver.config.Conf;
import reaper.appserver.config.ConfLoader;
import reaper.appserver.config.ConfResource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class NeogresDataSource
{
    private static NeogresDataSource instance;
    private static ComboPooledDataSource pooledDataSource;

    private static String neo4jUrl;
    private static String neo4jUsername;
    private static String neo4jPassword;

    private NeogresDataSource()
    {
    }

    public static NeogresDataSource getInstance()
    {
        if (instance == null)
        {
            instance = new NeogresDataSource();
        }

        return instance;
    }

    public void init() throws Exception
    {
        Conf dbConf = ConfLoader.getConf(ConfResource.DB);

        pooledDataSource = new ComboPooledDataSource();
        pooledDataSource.setDriverClass(dbConf.get("db.neogres.postgres.driver"));
        pooledDataSource.setJdbcUrl(dbConf.get("db.neogres.postgres.url"));
        pooledDataSource.setUser(dbConf.get("db.neogres.postgres.user"));
        pooledDataSource.setPassword(dbConf.get("db.neogres.postgres.password"));

        pooledDataSource.setMinPoolSize(Integer.parseInt(dbConf.get("db.neogres.postgres.pool.min_size")));
        pooledDataSource.setAcquireIncrement(Integer.parseInt(dbConf.get("db.neogres.postgres.pool.increment_size")));
        pooledDataSource.setMaxPoolSize(Integer.parseInt(dbConf.get("db.neogres.postgres.pool.max_size")));

        Class.forName(dbConf.get("db.neogres.neo4j.driver"));
        neo4jUrl = dbConf.get("db.neogres.neo4j.url");
        neo4jUsername = dbConf.get("db.neogres.neo4j.user");
        neo4jPassword = dbConf.get("db.neogres.neo4j.password");
    }

    public Connection getPostgresConnection() throws SQLException
    {
        return pooledDataSource.getConnection();
    }

    public Connection getNeo4jConnection() throws SQLException
    {
        Connection connection = DriverManager.getConnection(neo4jUrl, neo4jUsername, neo4jPassword);
        return connection;
    }

    public void close()
    {
        pooledDataSource.close();
    }
}
