package com.iris.reader.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Author: zfl
 * @Date: 2020/9/10 14:34
 * @Version: 1.0.0
 */
@ConfigurationProperties(prefix = "iris")
public class IrisReaderProperties {

    private final Zookeeper zookeeper = new Zookeeper();

    public Zookeeper getZookeeper() {
        return zookeeper;
    }

    public static class Zookeeper {
        private String connectString;

        public String getConnectString() {
            return connectString;
        }

        public void setConnectString(String connectString) {
            this.connectString = connectString;
        }
    }
}
