package gitlet;

import java.io.Serializable;
import java.util.Map;

/**
 * @description: 配置类
 * @author: 杨怀龙
 * @create: 2025-06-18 16:00
 **/
public class Config implements Serializable {

    /**
     * 远程配置
     */
    private Map<String, String> remoteConfig;

    public Map<String, String> getRemoteConfig() {
        return remoteConfig;
    }

    public void setRemoteConfig(Map<String, String> remoteConfig) {
        this.remoteConfig = remoteConfig;
    }

    public Config(Map<String, String> remoteConfig) {
        this.remoteConfig = remoteConfig;
    }
}
