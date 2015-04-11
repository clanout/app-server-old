package reaper.appserver.persistence.core.postgre;

import reaper.appserver.persistence.core.DatabaseAdapter;

public class PostgreDatabaseAdapter implements DatabaseAdapter
{
    @Override
    public void init() throws Exception
    {
        PostgreDataSource postgreDataSource = PostgreDataSource.getInstance();
        postgreDataSource.init();
    }

    @Override
    public void close()
    {
        PostgreDataSource postgreDataSource = PostgreDataSource.getInstance();
        postgreDataSource.close();
    }
}
