package reaper.appserver.persistence.core.postgre;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import reaper.appserver.config.Conf;
import reaper.appserver.config.ConfLoader;
import reaper.appserver.config.ConfResource;

import java.sql.Connection;
import java.sql.SQLException;

public class PostgreDataSource
{
    private static PostgreDataSource instance;

    private static ComboPooledDataSource pooledDataSource;

    private PostgreDataSource()
    {
    }

    public static PostgreDataSource getInstance()
    {
        if (instance == null)
        {
            instance = new PostgreDataSource();
        }

        return instance;
    }

    public void init() throws Exception
    {
        Conf dbConf = ConfLoader.getConf(ConfResource.DB);

        pooledDataSource = new ComboPooledDataSource();
        pooledDataSource.setDriverClass(dbConf.get("db.postgres.driver"));
        pooledDataSource.setJdbcUrl(dbConf.get("db.postgres.url"));
        pooledDataSource.setUser(dbConf.get("db.postgres.user"));
        pooledDataSource.setPassword(dbConf.get("db.postgres.password"));

        pooledDataSource.setMinPoolSize(Integer.parseInt(dbConf.get("db.postgres.pool.min_size")));
        pooledDataSource.setAcquireIncrement(Integer.parseInt(dbConf.get("db.postgres.pool.increment_size")));
        pooledDataSource.setMaxPoolSize(Integer.parseInt(dbConf.get("db.postgres.pool.max_size")));
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
