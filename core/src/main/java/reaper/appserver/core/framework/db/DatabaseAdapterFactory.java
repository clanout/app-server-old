package reaper.appserver.core.framework.db;

import reaper.appserver.config.Conf;
import reaper.appserver.config.ConfLoader;
import reaper.appserver.config.ConfResource;
import reaper.appserver.persistence.core.DatabaseAdapter;

import java.lang.reflect.Constructor;

public class DatabaseAdapterFactory
{
    public static DatabaseAdapter getDatabaseAdapter()
    {
        try
        {
            Conf sourceConf = ConfLoader.getConf(ConfResource.SOURCE);
            String databaseAdapterClassName = sourceConf.get("source.class.db_adapter");
            Class<? extends DatabaseAdapter> databaseAdapterClass = (Class<? extends DatabaseAdapter>) Class.forName(databaseAdapterClassName);
            Constructor<? extends DatabaseAdapter> databaseAdapterConstructor = databaseAdapterClass.getDeclaredConstructor();
            DatabaseAdapter databaseAdapter = databaseAdapterConstructor.newInstance();
            return databaseAdapter;
        }
        catch (Exception e)
        {
            return null;
        }
    }
}
