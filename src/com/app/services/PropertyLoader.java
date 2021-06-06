package com.app.services;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;

public class PropertyLoader {

    private Properties properties;

    static String DYNAMIC_PREFIX = "${";
    static String DYNAMIC_SUFFIX = "}";

    public PropertyLoader(String configFile) throws IOException {
        properties = new Properties();
        loadProperties(configFile);
        replaceVariables();
    }

    private void loadProperties(String configFile) throws IOException {
        FileInputStream fis = new FileInputStream(configFile);
        properties.load(fis);
    }

    public String getProperty(String key){
        return properties.getProperty(key);
    }

    private void replaceVariables() {
        Set<String> keys = properties.stringPropertyNames();
        keys.forEach(key -> {
            String value = properties.get(key).toString();
            while (value.contains(DYNAMIC_PREFIX)) {
                int si = value.indexOf(DYNAMIC_PREFIX);
                int ei = value.indexOf(DYNAMIC_SUFFIX) + 1;
                String dynamicKey = value.substring(si + 2, ei - 1);
                value = value.replace(DYNAMIC_PREFIX + dynamicKey + DYNAMIC_SUFFIX, properties.get(dynamicKey).toString());
                properties.setProperty(key, value);
            }
        });
    }
}
