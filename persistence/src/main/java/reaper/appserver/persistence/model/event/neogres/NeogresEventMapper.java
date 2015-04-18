package reaper.appserver.persistence.model.event.neogres;

import reaper.appserver.persistence.core.neogres.NeogresEntityMapper;
import reaper.appserver.persistence.model.event.Event;

import java.sql.ResultSet;
import java.sql.SQLException;

public class NeogresEventMapper implements NeogresEntityMapper<Event>
{
    @Override
    public Event map(ResultSet resultSet) throws SQLException
    {
        return null;
    }
}
