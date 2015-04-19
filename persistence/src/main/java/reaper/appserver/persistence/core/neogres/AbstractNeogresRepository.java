package reaper.appserver.persistence.core.neogres;

import org.apache.log4j.Logger;
import reaper.appserver.log.LogUtil;
import reaper.appserver.persistence.core.Entity;
import reaper.appserver.persistence.core.Repository;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class AbstractNeogresRepository<E extends Entity> implements Repository<E>
{
    protected Logger log;

    protected NeogresEntityMapper<E> entityMapper;

    public AbstractNeogresRepository(NeogresEntityMapper<E> entityMapper)
    {
        this.log = LogUtil.getLogger(this.getClass());
        this.entityMapper = entityMapper;
    }

    protected Connection getPostgresConnection() throws SQLException
    {
        NeogresDataSource postgreDataSource = NeogresDataSource.getInstance();
        return postgreDataSource.getPostgresConnection();
    }

    protected Connection getNeo4jConnection() throws SQLException
    {
        NeogresDataSource postgreDataSource = NeogresDataSource.getInstance();
        return postgreDataSource.getNeo4jConnection();
    }
}
