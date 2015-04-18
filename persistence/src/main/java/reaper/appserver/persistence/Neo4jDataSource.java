package reaper.appserver.persistence;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import reaper.appserver.config.Conf;
import reaper.appserver.config.ConfLoader;
import reaper.appserver.config.ConfResource;

import java.sql.Connection;
import java.sql.SQLException;

public class Neo4jDataSource
{
    private static Neo4jDataSource instance;

    private static ComboPooledDataSource pooledDataSource;

    private Neo4jDataSource()
    {
    }

    public static Neo4jDataSource getInstance()
    {
        if (instance == null)
        {
            instance = new Neo4jDataSource();
        }

        return instance;
    }

    public void init() throws Exception
    {
        pooledDataSource = new ComboPooledDataSource();
        pooledDataSource.setDriverClass("org.neo4j.jdbc.Driver");
        pooledDataSource.setJdbcUrl("jdbc:neo4j://localhost:7474/");
        pooledDataSource.setUser("neo4j");
        pooledDataSource.setPassword("admin");

        pooledDataSource.setMinPoolSize(50);
        pooledDataSource.setAcquireIncrement(50);
        pooledDataSource.setMaxPoolSize(500);
    }

    public Connection getConnection() throws SQLException
    {
        return pooledDataSource.getConnection();
    }

    public void close()
    {
        pooledDataSource.close();
    }
}
