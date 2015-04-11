package reaper.appserver.log;

import org.apache.log4j.Logger;

public class LogUtil
{
    public static Logger getLogger(Class clazz)
    {
        return Logger.getLogger(clazz);
    }
}
