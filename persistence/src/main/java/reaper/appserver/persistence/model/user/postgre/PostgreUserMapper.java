package reaper.appserver.persistence.model.user.postgre;

import reaper.appserver.persistence.core.postgre.PostgreEntityMapper;
import reaper.appserver.persistence.model.user.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneId;

public class PostgreUserMapper implements PostgreEntityMapper<User>
{
    @Override
    public User map(ResultSet resultSet) throws SQLException
    {
        User user = new User();

        user.setId(resultSet.getString("user_id"));
        user.setUsername(resultSet.getString("username"));
        user.setPhone(resultSet.getString("phone"));
        user.setFirstname(resultSet.getString("firstname"));
        user.setLastname(resultSet.getString("lastname"));
        user.setGender(User.Gender.fromCode(resultSet.getString("gender")));

        try
        {
            user.setStatus(User.Status.valueOf(resultSet.getString("status").toUpperCase()));
        }
        catch (IllegalArgumentException e)
        {
            throw new SQLException("Unable to process status");
        }

        try
        {
            Timestamp timestamp = resultSet.getTimestamp("registered");
            OffsetDateTime time = OffsetDateTime.ofInstant(timestamp.toInstant(), ZoneId.systemDefault());
            user.setRegistrationTime(time);
        }
        catch (Exception e)
        {
            throw new SQLException("Unable to process registered (timestamp)");
        }

        return user;
    }
}
