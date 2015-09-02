package reaper.appserver.persistence.core.postgre;

import reaper.appserver.persistence.core.Entity;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface PostgreEntityMapper<E extends Entity>
{
    E map(ResultSet resultSet) throws SQLException;
}
