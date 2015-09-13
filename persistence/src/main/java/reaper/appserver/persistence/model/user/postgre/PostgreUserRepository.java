package reaper.appserver.persistence.model.user.postgre;

import reaper.appserver.persistence.core.RepositoryActionNotAllowed;
import reaper.appserver.persistence.core.postgre.AbstractPostgreRepository;
import reaper.appserver.persistence.core.postgre.PostgreQuery;
import reaper.appserver.persistence.model.user.User;
import reaper.appserver.persistence.model.user.UserDetails;
import reaper.appserver.persistence.model.user.UserRepository;

import java.sql.*;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PostgreUserRepository extends AbstractPostgreRepository<User> implements UserRepository
{
    private static final String SQL_CREATE = PostgreQuery.load("user/create.sql");
    private static final String SQL_READ = PostgreQuery.load("user/read.sql");
    private static final String SQL_READ_USERNAME = PostgreQuery.load("user/read_username.sql");
    private static final String SQL_UPDATE = PostgreQuery.load("user/update.sql");

    private static final String SQL_READ_DETAILS = PostgreQuery.load("user/read_details.sql");
    private static final String SQL_READ_DETAILS_LOCAL = PostgreQuery.load("user/read_details_local.sql");
    private static final String SQL_READ_DETAILS_CONTACTS = PostgreQuery.load("user/read_details_contacts.sql");
    private static final String SQL_READ_DETAILS_CONTACTS_LOCAL = PostgreQuery.load("user/read_details_contacts_local.sql");

    private static final String SQL_READ_FRIENDS = PostgreQuery.load("user/read_friends.sql");
    private static final String SQL_CREATE_FRIEND = PostgreQuery.load("user/create_friend.sql");

    private static final String SQL_CREATE_FAVOURITE = PostgreQuery.load("user/create_favourite.sql");
    private static final String SQL_DELETE_FAVOURITE = PostgreQuery.load("user/delete_favourite.sql");

    private static final String SQL_UPDATE_BLOCK_PREPROCESS = PostgreQuery.load("user/update_block_preprocess.sql");
    private static final String SQL_UPDATE_BLOCK = PostgreQuery.load("user/update_block.sql");
    private static final String SQL_UPDATE_UNBLOCK = PostgreQuery.load("user/update_unblock.sql");

    private static final String SQL_UPDATE_PHONE = PostgreQuery.load("user/update_phone.sql");

    private static final String SQL_READ_LOCATION = PostgreQuery.load("user/read_location.sql");
    private static final String SQL_UPDATE_LOCATION = PostgreQuery.load("user/update_location.sql");

    public PostgreUserRepository()
    {
        super(new PostgreUserMapper());
    }

    @Override
    public User get(String id)
    {
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

            User user = null;

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
            {
                user = entityMapper.map(resultSet);
                break;
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();

            return user;
        }
        catch (SQLException e)
        {
            log.error("Unable to read user with user_id = " + id + "; [" + e.getMessage() + "]");
            return null;
        }
    }

    @Override
    public String create(User user)
    {
        try
        {
            Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SQL_CREATE);

            long userId;
            try
            {
                userId = Long.parseLong(user.getId());
            }
            catch (Exception e)
            {
                throw new SQLException("Invalid user_id");
            }

            preparedStatement.setLong(1, userId);
            preparedStatement.setString(2, user.getUsername());
            preparedStatement.setString(3, user.getPhone());
            preparedStatement.setString(4, user.getFirstname());
            preparedStatement.setString(5, user.getLastname());
            preparedStatement.setString(6, user.getGender().getCode());
            preparedStatement.setTimestamp(7, Timestamp.from(user.getRegistrationTime().atZoneSameInstant(ZoneOffset.UTC).toInstant()));
            preparedStatement.setString(8, String.valueOf(user.getStatus()));

            preparedStatement.executeUpdate();

            preparedStatement.close();
            connection.close();

            return user.getId();
        }
        catch (SQLException e)
        {
            log.error("Unable to create new user; [" + e.getMessage() + "]");
            return null;
        }
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
            preparedStatement.setTimestamp(6, Timestamp.from(user.getRegistrationTime().atZoneSameInstant(ZoneOffset.UTC).toInstant()));
            preparedStatement.setString(7, String.valueOf(user.getStatus()));

            try
            {
                preparedStatement.setLong(8, Long.parseLong(user.getId()));
            }
            catch (Exception e)
            {
                throw new SQLException("Invalid user_id");
            }

            preparedStatement.executeUpdate();

            preparedStatement.close();
            connection.close();
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
        try
        {
            Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SQL_READ_USERNAME);

            preparedStatement.setString(1, username);

            User user = null;

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
            {
                user = entityMapper.map(resultSet);
                break;
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();

            return user;
        }
        catch (SQLException e)
        {
            log.error("Unable to read user with username = " + username + "; [" + e.getMessage() + "]");
            return null;
        }
    }

    @Override
    public UserDetails getUserDetails(String id)
    {
        try
        {
            Long userId = null;
            try
            {
                userId = Long.parseLong(id);
            }
            catch (Exception e)
            {
                throw new SQLException("Invalid user_id");
            }

            UserDetails userDetails = new UserDetails();
            userDetails.setId(id);
            Set<UserDetails.Friend> friends = new HashSet<>();
            userDetails.setFriends(friends);

            Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SQL_READ_DETAILS);

            preparedStatement.setLong(1, userId);
            preparedStatement.setLong(2, userId);
            preparedStatement.setLong(3, userId);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
            {
                UserDetails.Friend friend = new UserDetails.Friend();
                friend.setId(resultSet.getString("friend_id"));
                friend.setName(resultSet.getString("name"));
                friend.setBlocked(resultSet.getBoolean("is_blocked"));
                friend.setFavourite(resultSet.getBoolean("is_favourite"));

                friends.add(friend);
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();

            return userDetails;
        }
        catch (SQLException e)
        {
            log.error("Unable to read details for user with user_id = " + id + "; [" + e.getMessage() + "]");
            return null;
        }
    }

    @Override
    public UserDetails getUserDetailsLocal(String id)
    {
        try
        {
            Long userId = null;
            try
            {
                userId = Long.parseLong(id);
            }
            catch (Exception e)
            {
                throw new SQLException("Invalid user_id");
            }

            String zone = null;

            Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SQL_READ_LOCATION);
            preparedStatement.setLong(1, userId);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
            {
                zone = resultSet.getString("location");
                break;
            }
            resultSet.close();
            preparedStatement.close();

            UserDetails userDetails = new UserDetails();
            userDetails.setId(id);
            Set<UserDetails.Friend> friends = new HashSet<>();
            userDetails.setFriends(friends);

            preparedStatement = connection.prepareStatement(SQL_READ_DETAILS_LOCAL);
            preparedStatement.setLong(1, userId);
            preparedStatement.setLong(2, userId);
            preparedStatement.setString(3, zone);
            preparedStatement.setLong(4, userId);

            resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
            {
                UserDetails.Friend friend = new UserDetails.Friend();
                friend.setId(resultSet.getString("friend_id"));
                friend.setName(resultSet.getString("name"));
                friend.setBlocked(resultSet.getBoolean("is_blocked"));
                friend.setFavourite(resultSet.getBoolean("is_favourite"));

                friends.add(friend);
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();

            return userDetails;
        }
        catch (SQLException e)
        {
            log.error("Unable to read details for user with user_id = " + id + "; [" + e.getMessage() + "]");
            return null;
        }
    }

    @Override
    public void block(User user, List<String> userIds)
    {
        Connection connection = null;

        try
        {
            Long userId = null;
            List<Long> friendIds = new ArrayList<>();
            try
            {
                userId = Long.parseLong(user.getId());
                for (String friendId : userIds)
                {
                    friendIds.add(Long.parseLong(friendId));
                }
            }
            catch (Exception e)
            {
                throw new SQLException("Invalid user_id/friend_ids");
            }

            connection = getConnection();
            connection.setAutoCommit(false);

            PreparedStatement preparedStatement = connection.prepareStatement(SQL_UPDATE_BLOCK_PREPROCESS);

            for (Long friendId : friendIds)
            {
                preparedStatement.setLong(1, userId);
                preparedStatement.setLong(2, friendId);
                preparedStatement.setLong(3, friendId);
                preparedStatement.setLong(4, userId);
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();

            PreparedStatement preparedStatement2 = connection.prepareStatement(SQL_UPDATE_BLOCK);
            preparedStatement2.setLong(1, userId);
            preparedStatement2.setArray(2, connection.createArrayOf("bigint", friendIds.toArray()));
            preparedStatement2.executeUpdate();

            connection.commit();

            preparedStatement.close();
            preparedStatement2.close();
            connection.close();
        }
        catch (SQLException e)
        {
            try
            {
                if (connection != null)
                {
                    connection.rollback();
                    connection.close();
                }
            }
            catch (SQLException e1)
            {
                log.error(e1.getMessage());
            }
            log.error("Unable to block friends for user with user_id = " + user.getId() + " [" + e.getMessage() + "]");
        }
    }

    @Override
    public void unblock(User user, List<String> userIds)
    {
        try
        {
            Long userId = null;
            List<Long> friendIds = new ArrayList<>();
            try
            {
                userId = Long.parseLong(user.getId());
                for (String friendId : userIds)
                {
                    friendIds.add(Long.parseLong(friendId));
                }
            }
            catch (Exception e)
            {
                throw new SQLException("Invalid user_id/friend_ids");
            }

            Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SQL_UPDATE_UNBLOCK);

            preparedStatement.setLong(1, userId);
            preparedStatement.setArray(2, connection.createArrayOf("bigint", friendIds.toArray()));

            preparedStatement.executeUpdate();

            preparedStatement.close();
            connection.close();
        }
        catch (SQLException e)
        {
            log.error("Unable to unblock friends for user with user_id = " + user.getId() + " [" + e.getMessage() + "]");
        }
    }

    @Override
    public void favourite(User user, List<String> userIds)
    {
        try
        {
            Long userId = null;
            List<Long> friendIds = new ArrayList<>();
            try
            {
                userId = Long.parseLong(user.getId());
                for (String friendId : userIds)
                {
                    friendIds.add(Long.parseLong(friendId));
                }
            }
            catch (Exception e)
            {
                throw new SQLException("Invalid user_id/friend_ids");
            }

            Connection connection = getConnection();
            connection.setAutoCommit(false);

            PreparedStatement preparedStatement = connection.prepareStatement(SQL_CREATE_FAVOURITE);

            for (Long friendId : friendIds)
            {
                preparedStatement.setLong(1, userId);
                preparedStatement.setLong(2, friendId);
                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();

            connection.commit();

            preparedStatement.close();
            connection.close();
        }
        catch (SQLException e)
        {
            log.error("Unable to add favourites for user with user_id = " + user.getId() + " [" + e.getMessage() + "]");
        }
    }

    @Override
    public void unfavourite(User user, List<String> userIds)
    {
        try
        {
            Long userId = null;
            List<Long> friendIds = new ArrayList<>();
            try
            {
                userId = Long.parseLong(user.getId());
                for (String friendId : userIds)
                {
                    friendIds.add(Long.parseLong(friendId));
                }
            }
            catch (Exception e)
            {
                throw new SQLException("Invalid user_id/friend_ids");
            }

            Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SQL_DELETE_FAVOURITE);

            preparedStatement.setLong(1, userId);
            preparedStatement.setArray(2, connection.createArrayOf("bigint", friendIds.toArray()));

            preparedStatement.executeUpdate();

            preparedStatement.close();
            connection.close();
        }
        catch (SQLException e)
        {
            log.error("Unable to remove favourites for user with user_id = " + user.getId() + " [" + e.getMessage() + "]");
        }
    }

    @Override
    public void addFriends(User user, List<String> userIds)
    {
        try
        {
            Long userId = null;
            List<String> friends = new ArrayList<>();
            try
            {
                userId = Long.parseLong(user.getId());
            }
            catch (Exception e)
            {
                throw new SQLException("Invalid user_id");
            }

            Connection connection = getConnection();
            connection.setAutoCommit(false);

            PreparedStatement preparedStatement = connection.prepareStatement(SQL_READ_FRIENDS);
            preparedStatement.setLong(1, userId);
            preparedStatement.setLong(2, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
            {
                friends.add(resultSet.getString("friend_id"));
            }
            resultSet.close();
            preparedStatement.close();

            List<Long> friendIds = new ArrayList<>();
            try
            {
                for (String friendId : userIds)
                {
                    if (!friends.contains(friendId))
                    {
                        friendIds.add(Long.parseLong(friendId));
                    }
                }
            }
            catch (Exception e)
            {
                throw new SQLException("Invalid friend_ids");
            }

            preparedStatement = connection.prepareStatement(SQL_CREATE_FRIEND);
            for (Long friendId : friendIds)
            {
                preparedStatement.setLong(1, userId);
                preparedStatement.setLong(2, friendId);
                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();

            connection.commit();

            preparedStatement.close();
            connection.close();
        }
        catch (SQLException e)
        {
            log.error("Unable to add friends for user with user_id = " + user.getId() + " [" + e.getMessage() + "]");
        }
    }

    @Override
    public UserDetails getRegisteredContacts(User user, List<String> contacts)
    {
        try
        {
            Long userId = null;
            try
            {
                userId = Long.parseLong(user.getId());
            }
            catch (Exception e)
            {
                throw new SQLException("Invalid user_id");
            }

            UserDetails userDetails = new UserDetails();
            userDetails.setId(user.getId());
            Set<UserDetails.Friend> registeredContacts = new HashSet<>();
            userDetails.setFriends(registeredContacts);

            Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SQL_READ_DETAILS_CONTACTS);

            preparedStatement.setArray(1, connection.createArrayOf("text", contacts.toArray()));

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
            {
                UserDetails.Friend registeredContact = new UserDetails.Friend();
                registeredContact.setId(resultSet.getString("user_id"));
                registeredContact.setName(resultSet.getString("name"));
                registeredContact.setBlocked(false);
                registeredContact.setFavourite(false);

                registeredContacts.add(registeredContact);
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();

            return userDetails;
        }
        catch (SQLException e)
        {
            log.error("Unable to get registered contacts [" + e.getMessage() + "]");
            return null;
        }
    }

    @Override
    public UserDetails getLocalRegisteredContacts(User user, List<String> contacts, String zone)
    {
        try
        {
            Long userId = null;
            try
            {
                userId = Long.parseLong(user.getId());
            }
            catch (Exception e)
            {
                throw new SQLException("Invalid user_id");
            }

            UserDetails userDetails = new UserDetails();
            userDetails.setId(user.getId());
            Set<UserDetails.Friend> registeredContacts = new HashSet<>();
            userDetails.setFriends(registeredContacts);

            Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SQL_READ_DETAILS_CONTACTS_LOCAL);

            preparedStatement.setArray(1, connection.createArrayOf("text", contacts.toArray()));
            preparedStatement.setString(2, zone);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
            {
                UserDetails.Friend registeredContact = new UserDetails.Friend();
                registeredContact.setId(resultSet.getString("user_id"));
                registeredContact.setName(resultSet.getString("name"));
                registeredContact.setBlocked(false);
                registeredContact.setFavourite(false);

                registeredContacts.add(registeredContact);
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();

            return userDetails;
        }
        catch (SQLException e)
        {
            log.error("Unable to get local registered contacts [" + e.getMessage() + "]");
            return null;
        }
    }

    @Override
    public void setPhone(User user, String phone)
    {
        try
        {
            Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SQL_UPDATE_PHONE);

            preparedStatement.setString(1, phone);

            try
            {
                preparedStatement.setLong(2, Long.parseLong(user.getId()));
            }
            catch (Exception e)
            {
                throw new SQLException("Invalid user_id");
            }

            preparedStatement.executeUpdate();

            preparedStatement.close();
            connection.close();
        }
        catch (SQLException e)
        {
            log.error("Unable to update phone for user with user_id = " + user.getId() + "; [" + e.getMessage() + "]");
        }
    }

    @Override
    public boolean updateLocation(User user, String zone)
    {
        try
        {
            boolean result = false;

            long userId;
            try
            {
                userId = Long.parseLong(user.getId());
            }
            catch (Exception e)
            {
                throw new SQLException("Invalid user_id");
            }

            Connection connection = getConnection();

            PreparedStatement preparedStatement = connection.prepareStatement(SQL_READ_LOCATION);
            preparedStatement.setLong(1, userId);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
            {
                if (!resultSet.getString("location").equalsIgnoreCase(zone))
                {
                    result = true;
                    break;
                }
            }
            resultSet.close();
            preparedStatement.close();

            preparedStatement = connection.prepareStatement(SQL_UPDATE_LOCATION);
            preparedStatement.setLong(1, userId);
            preparedStatement.setLong(2, userId);
            preparedStatement.setString(3, zone);

            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();

            return result;
        }
        catch (SQLException e)
        {
            log.error("Unable to update location for user with user_id = " + user.getId() + "; [" + e.getMessage() + "]");
            return false;
        }
    }
}
