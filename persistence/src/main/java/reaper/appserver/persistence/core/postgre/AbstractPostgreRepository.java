package reaper.appserver.persistence.core.postgre;

import org.apache.log4j.Logger;
import reaper.appserver.log.LogUtil;
import reaper.appserver.persistence.core.Entity;
import reaper.appserver.persistence.core.Repository;

import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.SQLException;

public abstract class AbstractPostgreRepository<E extends Entity> implements Repository<E>
{
    protected Logger log;

    protected PostgreEntityMapper<E> entityMapper;

    public AbstractPostgreRepository(PostgreEntityMapper<E> entityMapper)
    {
        this.log = LogUtil.getLogger(this.getClass());
        this.entityMapper = entityMapper;
    }

    protected Connection getConnection() throws SQLException
    {
        PostgreDataSource postgreDataSource = PostgreDataSource.getInstance();
        return postgreDataSource.getConnection();
    }
}
