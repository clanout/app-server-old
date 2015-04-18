package reaper.appserver.persistence.core.neogres;

import reaper.appserver.persistence.core.DatabaseAdapter;

public class NeogresDatabaseAdapter implements DatabaseAdapter
{
    @Override
    public void init() throws Exception
    {
        NeogresDataSource neogresDataSource = NeogresDataSource.getInstance();
        neogresDataSource.init();
    }

    @Override
    public void close()
    {
        NeogresDataSource neogresDataSource = NeogresDataSource.getInstance();
        neogresDataSource.close();
    }
}
