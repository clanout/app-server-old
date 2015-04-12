package reaper.appserver.core.app.controller;

import reaper.appserver.core.app.controller.core.BaseController;
import reaper.appserver.core.framework.db.DatabaseAdapterFactory;
import reaper.appserver.core.framework.exceptions.BadRequest;
import reaper.appserver.core.framework.exceptions.ServerError;
import reaper.appserver.core.framework.request.Request;
import reaper.appserver.core.framework.response.ResponseFactory;
import reaper.appserver.persistence.core.DatabaseAdapter;
import reaper.appserver.persistence.core.postgre.PostgreDataSource;
import reaper.appserver.persistence.core.postgre.PostgreDatabaseAdapter;

import java.beans.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class IndexController extends BaseController
{
    public IndexController(Request request, ResponseFactory responseFactory)
    {
        super(request, responseFactory);
    }

    public void mainAction()
    {
        try
        {
            String userId = request.getSessionUser();

            response.set("message", "HelloWorld");

            PostgreDataSource dataSource = PostgreDataSource.getInstance();
            Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT facebook_id FROM users WHERE user_id = ?");
            preparedStatement.setString(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
            {
                response.set("facebook_id", resultSet.getString(1));
                break;
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();
        }
        catch (Exception e)
        {
            throw new ServerError();
        }
    }
}
