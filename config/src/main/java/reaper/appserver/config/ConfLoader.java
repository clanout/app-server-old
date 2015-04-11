package reaper.appserver.config;

import reaper.appserver.config.implementation.ApacheConf;

public class ConfLoader
{
    public static Conf getConf(ConfResource confResource)
    {
        return new ApacheConf(confResource.getResource());
    }
}
