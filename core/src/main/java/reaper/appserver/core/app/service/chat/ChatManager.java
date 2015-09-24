package reaper.appserver.core.app.service.chat;

import org.apache.log4j.Logger;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import reaper.appserver.config.ConfLoader;
import reaper.appserver.config.ConfResource;
import reaper.appserver.log.LogUtil;

import java.io.IOException;

public class ChatManager
{
    public static final String TAG = "ChatManager";

    private static Logger LOG = LogUtil.getLogger(ChatManager.class);

    private static String XMPP_SERVICE_NAME = ConfLoader.getConf(ConfResource.CHAT).get("chat.xmpp.service");
    private static String XMPP_HOST_NAME = ConfLoader.getConf(ConfResource.CHAT).get("chat.xmpp.host");
    private static int XMPP_PORT = Integer.parseInt(ConfLoader.getConf(ConfResource.CHAT).get("chat.xmpp.port"));
    private static String ADMIN_USERNAME = ConfLoader.getConf(ConfResource.CHAT).get("chat.xmpp.admin.username");
    private static String ADMIN_PASSWORD = ConfLoader.getConf(ConfResource.CHAT).get("chat.xmpp.admin.password");
    private static String ADMIN_NICKNAME = ConfLoader.getConf(ConfResource.CHAT).get("chat.xmpp.admin.nickname");
    private static String XMPP_CHATROOM_POSTFIX = ConfLoader.getConf(ConfResource.CHAT).get("chat.xmpp.chatroom_postfix");

    public static ChatManager instance;

    private AbstractXMPPConnection connection;

    private ChatManager()
    {
        synchronized (TAG)
        {
            try
            {
                XMPPTCPConnectionConfiguration configuration = XMPPTCPConnectionConfiguration.builder()
                        .setUsernameAndPassword(ADMIN_USERNAME, ADMIN_PASSWORD)
                        .setServiceName(XMPP_SERVICE_NAME)
                        .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                        .setHost(XMPP_HOST_NAME)
                        .setPort(XMPP_PORT)
                        .build();

                connection = new XMPPTCPConnection(configuration);
                connection.connect();
                connection.login();
            }
            catch (Exception e)
            {
                LOG.error("Unable to open xmpp connection ", e);
            }
        }
    }

    public static ChatManager getInstance()
    {
        if (instance == null)
        {
            instance = new ChatManager();
        }

        return instance;
    }

    public AbstractXMPPConnection getConnection() throws IOException, XMPPException, SmackException
    {
        if(!connection.isConnected())
        {
            connection.connect();
        }

        return connection;
    }
}
