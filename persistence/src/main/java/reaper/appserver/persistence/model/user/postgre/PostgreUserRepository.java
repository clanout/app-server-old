package reaper.appserver.persistence.model.user.postgre;

import reaper.appserver.persistence.core.postgre.AbstractPostgreRepository;
import reaper.appserver.persistence.core.postgre.PostgreQuery;
import reaper.appserver.persistence.model.user.User;
import reaper.appserver.persistence.model.user.UserDetails;
import reaper.appserver.persistence.model.user.UserRepository;

import java.sql.*;
import java.util.List;
import java.util.UUID;

public class PostgreUserRepository extends AbstractPostgreRepository<User> implements UserRepository
{
    private static final String SQL_CREATE = PostgreQuery.load("user/create.sql");
    private static final String SQL_READ = PostgreQuery.load("user/read.sql");
    private static final String SQL_READ_USERNAME = PostgreQuery.load("user/read_username.sql");
    private static final String SQL_READ_DETAILS = PostgreQuery.load("user/read_details.sql");
    private static final String SQL_UPDATE = PostgreQuery.load("user/update.sql");
    private static final String SQL_DELETE = PostgreQuery.load("user/delete.sql");

    public PostgreUserRepository()
    {
        super(new PostgreUserMapper());
    }

    @Override
    public User getFromUsername(String username)
    {
        User user = null;

        try
        {
            Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SQL_READ_USERNAME);

            preparedStatement.setString(1, username);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
            {
                user = entityMapper.map(resultSet);
                break;
            }

            close(resultSet, preparedStatement, connection);
        }
        catch (SQLException e)
        {
            log.error("Unable to read user with username = " + username + "; [" + e.getMessage() + "]");
        }

        return user;
    }

    @Override
    public UserDetails getUserDetails(String userId)
    {
        // TODO
        return null;
    }

    @Override
    public void toggleBlock(User user, List<String> userIds)
    {
        // TODO
    }

    @Override
    public void toggleFavourite(User user, List<String> userIds)
    {
        // TODO
    }

    @Override
    public User get(String id)
    {
        User user = null;

        try
        {
            Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SQL_READ);

            try
            {
                preparedStatement.setLong(1, Long.valueOf(id));
            }
            catch (Exception e)
            {
                throw new SQLException("Invalid user_id");
            }

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
            {
                user = entityMapper.map(resultSet);
                break;
            }

            close(resultSet, preparedStatement, connection);
        }
        catch (SQLException e)
        {
            log.error("Unable to read user with user_id = " + id + "; [" + e.getMessage() + "]");
        }

        return user;
    }

    @Override
    public String create(User user)
    {
        try
        {
            Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SQL_CREATE);

            preparedStatement.setLong(1, Long.parseLong(user.getId()));
            preparedStatement.setString(2, user.getUsername());
            preparedStatement.setString(3, user.getPhone());
            preparedStatement.setString(4, user.getFirstname());
            preparedStatement.setString(5, user.getLastname());
            preparedStatement.setString(6, user.getGender().getCode());
            preparedStatement.setTimestamp(7, Timestamp.from(user.getRegistrationTime().toInstant()));
            preparedStatement.setString(8, String.valueOf(user.getStatus()));

            preparedStatement.executeUpdate();

            close(preparedStatement, connection);
        }
        catch (SQLException e)
        {
            log.error("Unable to create new user; [" + e.getMessage() + "]");
            return null;
        }

        return user.getId();
    }

    @Override
    public void update(User user)
    {
        try
        {
            Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SQL_UPDATE);

            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPhone());
            preparedStatement.setString(3, user.getFirstname());
            preparedStatement.setString(4, user.getLastname());
            preparedStatement.setString(5, user.getGender().getCode());
            preparedStatement.setTimestamp(6, Timestamp.from(user.getRegistrationTime().toInstant()));
            preparedStatement.setString(7, String.valueOf(user.getStatus()));

            try
            {
                preparedStatement.setObject(8, UUID.fromString(user.getId()));
            }
            catch (Exception e)
            {
                throw new SQLException("Invalid user_id");
            }

            preparedStatement.executeUpdate();

            close(preparedStatement, connection);
        }
        catch (SQLException e)
        {
            log.error("Unable to update user with user_id = " + user.getId() + "; [" + e.getMessage() + "]");
        }
    }

    @Override
    public void remove(User user)
    {
        try
        {
            Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SQL_DELETE);

            preparedStatement.setObject(1, UUID.fromString(user.getId()));

            preparedStatement.executeUpdate();

            user.setId(null);

            close(preparedStatement, connection);
        }
        catch (SQLException e)
        {
            log.error("Unable to delete user with user_id = " + user.getId() + "; [" + e.getMessage() + "]");
        }
    }
}
