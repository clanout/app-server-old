package reaper.appserver.core.app.service.sms;

import org.apache.log4j.Logger;
import reaper.appserver.config.ConfLoader;
import reaper.appserver.config.ConfResource;
import reaper.appserver.core.app.service.sms.api.ApiManager;
import reaper.appserver.core.app.service.sms.api.SmsApi;
import reaper.appserver.core.app.service.sms.api.SmsApiResponse;
import reaper.appserver.log.LogUtil;

public class SmsService
{
    private static final String TAG = "SmsService";

    private static Logger LOG = LogUtil.getLogger(SmsService.class);

    private static String SMS_SENDER_ID = ConfLoader.getConf(ConfResource.SMS).get("sms.sender_id");
    private static String SMS_USERNAME = ConfLoader.getConf(ConfResource.SMS).get("sms.username");
    private static String SMS_PASSWORD = ConfLoader.getConf(ConfResource.SMS).get("sms.password");

    private SmsApi smsApi;

    public SmsService()
    {
        smsApi = ApiManager.getApi();
    }

    public void sendInvitation(String inviterName, String inviteeMobileNumber, String eventTitle)
    {
        try
        {
            SmsApiResponse response = smsApi.sendBetaInvitation(SMS_SENDER_ID, SMS_USERNAME, SMS_PASSWORD,
                    inviteeMobileNumber, "there", inviterName, eventTitle);

            LOG.info(response.getResponseCode() + " : " + response.getMessage());
        }
        catch (Exception e)
        {
            LOG.error("SMS failed : " + e.getMessage());
        }
    }
}
