package reaper.appserver.persistence.model.user.neogres;

import reaper.appserver.persistence.core.RepositoryActionNotAllowed;
import reaper.appserver.persistence.core.neogres.AbstractNeogresRepository;
import reaper.appserver.persistence.core.neogres.NeogresQuery;
import reaper.appserver.persistence.model.user.User;
import reaper.appserver.persistence.model.user.UserDetails;
import reaper.appserver.persistence.model.user.UserRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NeogresUserRepository extends AbstractNeogresRepository<User> implements UserRepository
{
    private static final String SQL_CREATE = NeogresQuery.load("user/create.sql");
    private static final String SQL_READ = NeogresQuery.load("user/read.sql");
    private static final String SQL_READ_USERNAME = NeogresQuery.load("user/read_username.sql");
    private static final String SQL_UPDATE = NeogresQuery.load("user/update.sql");

    private static final String CQL_CREATE = NeogresQuery.load("user/create.cql");
    private static final String CQL_CREATE_FRIENDS = NeogresQuery.load("user/create_friends.cql");
    private static final String CQL_READ_DETAILS = NeogresQuery.load("user/read_details.cql");
    private static final String CQL_UPDATE_FAVOURITE = NeogresQuery.load("user/update_favourite.cql");
    private static final String CQL_UPDATE_BLOCK = NeogresQuery.load("user/update_block.cql");


    public NeogresUserRepository()
    {
        super(new NeogresUserMapper());
    }

    @Override
    public User get(String id)
    {
        User user = null;

        try
        {
            Connection connection = getPostgresConnection();
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
        Connection connection = null;

        try
        {
            connection = getPostgresConnection();
            connection.setAutoCommit(false);

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
            preparedStatement.close();

            Connection neo4jConnection = getNeo4jConnection();
            PreparedStatement neo4jStatement = neo4jConnection.prepareStatement(CQL_CREATE);
            neo4jStatement.setString(1, user.getId());
            neo4jStatement.setString(2, user.getFirstname() + " " + user.getLastname());

            neo4jStatement.executeUpdate();
            close(neo4jStatement, neo4jConnection);

            connection.commit();

            return user.getId();
        }
        catch (SQLException e)
        {
            if (connection != null)
            {
                try
                {
                    connection.rollback();
                    connection.close();
                }
                catch (SQLException e1)
                {
                    log.error("Unable to create new user; [" + e1.getMessage() + "]");
                }
            }
            log.error("Unable to create new user; [" + e.getMessage() + "]");
            return null;
        }
    }

    @Override
    public void update(User user)
    {
        try
        {
            Connection connection = getPostgresConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SQL_UPDATE);

            preparedStatement.setString(1, user.getPhone());
            preparedStatement.setString(2, String.valueOf(user.getStatus()));

            try
            {
                preparedStatement.setObject(3, UUID.fromString(user.getId()));
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
        throw new RepositoryActionNotAllowed();
    }

    @Override
    public User getFromUsername(String username)
    {
        User user = null;

        try
        {
            Connection connection = getPostgresConnection();
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
    public UserDetails getUserDetails(User user)
    {
        try
        {
            List<UserDetails.Friend> friends = new ArrayList<>();

            Connection connection = getNeo4jConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(CQL_READ_DETAILS);

            preparedStatement.setString(1, user.getId());

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
            {
                UserDetails.Friend friend = new UserDetails.Friend();
                friend.setId(resultSet.getString("friend.id"));
                friend.setName(resultSet.getString("friend.name"));
                friend.setBlocked(resultSet.getBoolean("is_blocked"));
                friend.setFavourite(resultSet.getBoolean("is_favourite"));

                friends.add(friend);
            }

            close(preparedStatement, connection);

            UserDetails userDetails = new UserDetails();
            userDetails.setId(user.getId());
            userDetails.setFriends(friends);

            return userDetails;

        }
        catch (SQLException e)
        {
            log.error("Unable to get details for user with user_id = " + user.getId() + "; [" + e.getMessage() + "]");
            return null;
        }
    }

    @Override
    public void toggleBlock(User user, List<String> userIds)
    {
        try
        {
            Connection connection = getNeo4jConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(CQL_UPDATE_BLOCK);

            preparedStatement.setString(1, user.getId());
            preparedStatement.setObject(2, userIds);

            preparedStatement.executeUpdate();

            close(preparedStatement, connection);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            log.error("Unable to toggle block for user with user_id = " + user.getId() + "; [" + e.getMessage() + "]");
        }
    }

    @Override
    public void toggleFavourite(User user, List<String> userIds)
    {
        try
        {
            Connection connection = getNeo4jConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(CQL_UPDATE_FAVOURITE);

            preparedStatement.setString(1, user.getId());
            preparedStatement.setObject(2, userIds);

            preparedStatement.executeUpdate();

            close(preparedStatement, connection);

        }
        catch (SQLException e)
        {
            log.error("Unable to toggle favourites for user with user_id = " + user.getId() + "; [" + e.getMessage() + "]");
        }
    }

    @Override
    public void addFriends(User user, List<String> userIds)
    {
        try
        {
            Connection connection = getNeo4jConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(CQL_CREATE_FRIENDS);

            preparedStatement.setString(1, user.getId());
            preparedStatement.setObject(2, userIds);

            preparedStatement.executeUpdate();

            close(preparedStatement, connection);

        }
        catch (SQLException e)
        {
            log.error("Unable to add friends for user with user_id = " + user.getId() + "; [" + e.getMessage() + "]");
        }
    }
}
