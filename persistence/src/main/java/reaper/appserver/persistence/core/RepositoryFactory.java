package reaper.appserver.persistence.core;

import org.apache.log4j.Logger;
import reaper.appserver.config.Conf;
import reaper.appserver.config.ConfLoader;
import reaper.appserver.config.ConfResource;
import reaper.appserver.log.LogUtil;

import java.lang.reflect.Constructor;

public class RepositoryFactory
{
    private static final String MODEL_PACKAGE = ConfLoader.getConf(ConfResource.SOURCE).get("source.package.model");

    private static Logger log = LogUtil.getLogger(RepositoryFactory.class);

    public static <R extends Repository<E>, E extends Entity> R create(Class<E> clazz)
    {
        try
        {
            Conf conf = ConfLoader.getConf(ConfResource.REPOSITORY);

            String modelName = clazz.getSimpleName().toLowerCase();
            String repositoryClassName = MODEL_PACKAGE + "." + modelName + "." + conf.get(modelName);

            Class<R> repositoryClass = (Class<R>) Class.forName(repositoryClassName);
            Constructor<R> repositoryConstructor = repositoryClass.getDeclaredConstructor();
            return repositoryConstructor.newInstance();
        }
        catch (Exception e)
        {
            log.fatal("Unable to locate repository for " + clazz.getSimpleName() + " model");
        }

        return null;
    }
}
