package reaper.appserver.core.app.service.sms.api;

import com.google.gson.annotations.SerializedName;

public class SmsApiResponse
{
    @SerializedName("response_code")
    private int responseCode;

    @SerializedName("response_msg")
    private String message;

    public int getResponseCode()
    {
        return responseCode;
    }

    public String getMessage()
    {
        return message;
    }
}
