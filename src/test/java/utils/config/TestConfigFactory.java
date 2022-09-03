package utils.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigBeanFactory;
import com.typesafe.config.ConfigFactory;

public final class TestConfigFactory {
    private static volatile Config config;
    private static volatile WebConfig webConfig;
    private static String CONFIG_FILE_NAME = "test.conf";
    private static String WEB_CONFIG_SECTION = "web";
    private static final TestConfigFactory instance = new TestConfigFactory();

    private TestConfigFactory(){
        config = ConfigFactory.systemProperties()
                .withFallback(ConfigFactory.systemEnvironment())
                .withFallback(ConfigFactory.parseResources(CONFIG_FILE_NAME));
    }

    public static synchronized WebConfig getWebConfig(){
        if(webConfig == null){
            synchronized (TestConfigFactory.class) {
                if(webConfig == null) {
                    webConfig = ConfigBeanFactory.create(config.getConfig(WEB_CONFIG_SECTION), WebConfig.class);
                }
            }
        }
        return webConfig;
    }

    public synchronized static TestConfigFactory getInstance(){
        return instance;
    }
}
