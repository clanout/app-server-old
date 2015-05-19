package reaper.appserver.core.app.service.chat;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.FormField;
import reaper.appserver.config.ConfLoader;
import reaper.appserver.config.ConfResource;
import reaper.appserver.core.framework.exceptions.ServerError;
import reaper.appserver.persistence.model.user.User;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatService
{
    private static String XMPP_SERVICE_NAME = ConfLoader.getConf(ConfResource.CHAT).get("chat.xmpp.service");
    private static String XMPP_HOST_NAME = ConfLoader.getConf(ConfResource.CHAT).get("chat.xmpp.host");
    private static int XMPP_PORT = Integer.parseInt(ConfLoader.getConf(ConfResource.CHAT).get("chat.xmpp.port"));

    private static String ADMIN_USERNAME = ConfLoader.getConf(ConfResource.CHAT).get("chat.xmpp.admin.username");
    private static String ADMIN_PASSWORD = ConfLoader.getConf(ConfResource.CHAT).get("chat.xmpp.admin.password");
    private static String ADMIN_NICKNAME = ConfLoader.getConf(ConfResource.CHAT).get("chat.xmpp.admin.nickname");

    private static String XMPP_CHATROOM_POSTFIX = ConfLoader.getConf(ConfResource.CHAT).get("chat.xmpp.chatroom_postfix");

    private AbstractXMPPConnection getXMPPConnection() throws IOException, XMPPException, SmackException
    {
        XMPPTCPConnectionConfiguration configuration = XMPPTCPConnectionConfiguration.builder()
                .setUsernameAndPassword(ADMIN_USERNAME, ADMIN_PASSWORD)
                .setServiceName(XMPP_SERVICE_NAME)
                .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                .setHost(XMPP_HOST_NAME)
                .setPort(XMPP_PORT)
                .build();

        AbstractXMPPConnection connection = new XMPPTCPConnection(configuration);
        connection.connect();
        connection.login();

        return connection;
    }

    public String createChatroom(String eventId)
    {
        try
        {
            AbstractXMPPConnection connection = getXMPPConnection();

            MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);
            MultiUserChat muc = manager.getMultiUserChat(eventId + XMPP_CHATROOM_POSTFIX);
            muc.create(ADMIN_NICKNAME);

            Form form = muc.getConfigurationForm();
            Form submitForm = form.createAnswerForm();
            List<FormField> fields = form.getFields();
            for (FormField field : fields)
            {
                if (!FormField.Type.hidden.equals(field.getType()) && field.getVariable() != null)
                {
                    submitForm.setDefaultAnswer(field.getVariable());
                }
                submitForm.setAnswer("muc#roomconfig_publicroom", true);
                submitForm.setAnswer("muc#roomconfig_persistentroom", true);
                muc.sendConfigurationForm(submitForm);
            }

            return eventId;
        }
        catch (Exception e)
        {
            throw new ServerError(e.getMessage());
        }
    }

    public void createUser(User user)
    {
        try
        {
            AccountManager accountManager = AccountManager.getInstance(getXMPPConnection());
            accountManager.sensitiveOperationOverInsecureConnection(true);

            Map<String, String> accountAttributes = new HashMap<>();
            accountAttributes.put("name", user.getFirstname() + " " + user.getLastname());

            accountManager.createAccount(user.getId(), user.getId(), accountAttributes);
        }
        catch (Exception e)
        {
            throw new ServerError(e.getMessage());
        }
    }

    public void postMessages(String eventId, String message)
    {
        try
        {
            AbstractXMPPConnection connection = getXMPPConnection();
            MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);
            MultiUserChat muc = manager.getMultiUserChat(eventId + XMPP_CHATROOM_POSTFIX);

            muc.join(ADMIN_NICKNAME);

            muc.sendMessage(message);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new ServerError(e.getMessage());
        }
    }

    public void readHistory(String eventId) throws XMPPException, IOException, SmackException
    {
        AbstractXMPPConnection connection = getXMPPConnection();
        MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);
        MultiUserChat muc = manager.getMultiUserChat(eventId + XMPP_CHATROOM_POSTFIX);

        DiscussionHistory history = new DiscussionHistory();
        history.setMaxStanzas(100);
        muc.join(ADMIN_NICKNAME, null, history, connection.getPacketReplyTimeout());

        Message message = null;
        while ((message = muc.nextMessage()) != null)
        {
            System.out.print(muc.getOccupant(message.getFrom()).getNick() + " : ");
            System.out.println(message.getBody());
        }
    }
}