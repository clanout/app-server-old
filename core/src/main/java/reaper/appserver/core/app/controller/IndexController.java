package reaper.appserver.core.app.controller;

import reaper.appserver.core.app.controller.core.BaseController;
import reaper.appserver.core.framework.db.DatabaseAdapterFactory;
import reaper.appserver.core.framework.exceptions.BadRequest;
import reaper.appserver.core.framework.exceptions.ServerError;
import reaper.appserver.core.framework.request.Request;
import reaper.appserver.core.framework.response.ResponseFactory;
import reaper.appserver.persistence.core.DatabaseAdapter;
import reaper.appserver.persistence.core.RepositoryFactory;
import reaper.appserver.persistence.core.postgre.PostgreDataSource;
import reaper.appserver.persistence.core.postgre.PostgreDatabaseAdapter;
import reaper.appserver.persistence.model.user.User;
import reaper.appserver.persistence.model.user.UserRepository;

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
        String userId = request.getSessionUser();
        UserRepository userRepository = RepositoryFactory.create(User.class);
        User user = userRepository.get(userId);

        response.set("me", user);
    }
}
