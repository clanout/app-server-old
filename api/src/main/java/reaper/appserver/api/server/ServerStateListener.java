package reaper.appserver.api.server;

import org.apache.log4j.Logger;
import reaper.appserver.core.framework.db.DatabaseAdapterFactory;
import reaper.appserver.log.LogUtil;
import reaper.appserver.persistence.core.DatabaseAdapter;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ServerStateListener implements ServletContextListener
{
    private static Logger log = LogUtil.getLogger(ServerStateListener.class);

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent)
    {
        log.info("[[ SERVER STARTED ]]");
        log.info("Initializing Database Adapter ...");

        DatabaseAdapter databaseAdapter = DatabaseAdapterFactory.getDatabaseAdapter();
        try
        {
            databaseAdapter.init();
        }
        catch (Exception e)
        {
            log.fatal("Unable to initialize Database Adapter");
            log.info("[[ SERVER STOPPED ]]");
            throw new RuntimeException();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent)
    {
        log.info("Closing Database Adapter ...");
        DatabaseAdapter databaseAdapter = DatabaseAdapterFactory.getDatabaseAdapter();
        databaseAdapter.close();
        log.info("[[ SERVER STOPPED ]]");
    }
}
