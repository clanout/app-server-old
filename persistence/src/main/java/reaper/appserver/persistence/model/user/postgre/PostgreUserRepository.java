package reaper.appserver.persistence.model.user.postgre;

import org.apache.log4j.Logger;
import reaper.appserver.log.LogUtil;
import reaper.appserver.persistence.core.postgre.PostgreDataSource;
import reaper.appserver.persistence.core.postgre.PostgreQuery;
import reaper.appserver.persistence.model.user.User;
import reaper.appserver.persistence.model.user.UserRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PostgreUserRepository implements UserRepository
{
    private static Logger log = LogUtil.getLogger(PostgreUserRepository.class);

    private static final String SQL_CREATE = PostgreQuery.load("user/create.sql");
    private static final String SQL_READ = PostgreQuery.load("user/read.sql");
    private static final String SQL_READ_USERNAME = PostgreQuery.load("user/read_username.sql");
    private static final String SQL_UPDATE = PostgreQuery.load("user/update.sql");
    private static final String SQL_DELETE = PostgreQuery.load("user/delete.sql");

    @Override
    public User getFromUsername(String username)
    {
        User user = null;

        try
        {
            PostgreDataSource postgreDataSource = PostgreDataSource.getInstance();
            Connection connection = postgreDataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SQL_READ_USERNAME);
            preparedStatement.setString(1, username);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
            {
                user = createFromResultSet(resultSet);
                break;
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();
        }
        catch (SQLException e)
        {
            log.error("Unable to read user with username = " + username + "; [" + e.getMessage() + "]");
        }

        return user;
    }

    @Override
    public User get(String id)
    {
        User user = null;

        try
        {
            PostgreDataSource postgreDataSource = PostgreDataSource.getInstance();
            Connection connection = postgreDataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SQL_READ);
            preparedStatement.setString(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
            {
                user = createFromResultSet(resultSet);
                break;
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();
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
        return null;
    }

    @Override
    public void update(User entity)
    {

    }

    @Override
    public void remove(User entity)
    {

    }

    private User createFromResultSet(ResultSet resultSet)
    {
        return null;
    }
}
