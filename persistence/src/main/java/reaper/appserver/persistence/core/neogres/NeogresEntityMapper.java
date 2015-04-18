package reaper.appserver.persistence.core.neogres;

import reaper.appserver.persistence.core.Entity;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface NeogresEntityMapper<E extends Entity>
{
    public E map(ResultSet resultSet) throws SQLException;
}
