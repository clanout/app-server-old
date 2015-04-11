package reaper.appserver.config;

import java.util.List;

public interface Conf
{
    public String get(String key);

    public List<String> getList(String key);
}
