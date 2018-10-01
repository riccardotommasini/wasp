package it.polimi.sr.wasp.utils;

import lombok.extern.java.Log;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import java.util.Iterator;

@Log
public class Config {

    /* Properties */
    private static final String NAME = "rsp_engine.name";
    private static final String RUN = "rsp_engine.run.uuid";
    private static final String HOST = "rsp_server.host";
    private static final String PORT = "rsp_server.port";
    private static final String VERSION = "rsp_engine.version";
    private static final String RESOURCE_PATH = "rsp_engine.static_resources.path";
    private static final String TIMESTAMP = "rsp_engine.enable_timestamp_function";
    private static final String EMPTY_RESULTS = "rsp_engine.send_empty_results";
    private static final String OUTPUT_FORMAT = "rsp_engine.output";
    private static final String ENABLE_INFERENCE = "rsp_engine.enable_inference";
    private static final String RULES_FILE = "rsp_engine.rules";
    private static final String ONTOLOGY_FILE = "rsp_engine.tbox";
    private static final String MESSAGE_LOG = "rsp_engine.message_log";
    private static final String BACK_LOOP = "rsp_engine.back_loop";
    private static final String SGRAPH_QUERY = "rsp_engine.sgraph_query";
    private static Config instance = null;
    private static Configuration config = null;
    private static String rules = null;
    private static String ontology = null;
    private static String messageLog = null;

    private static String def_query = "PREFIX sld: <http://streamreasoning.org/ontologies/SLD4TripleWave#> " +
            "SELECT ?wsurl ?tboxurl ?aboxurl " +
            "WHERE {" +
            "?sGraph sld:streamLocation ?wsurl . " +
            "OPTIONAL { ?sGraph sld:tBoxLocation ?tboxurl . } " +
            "OPTIONAL { ?sGraph sld::staticaBoxLoxation ?aboxurl . } " +
            "}";

    private Config(String propertiesFilePath) {
        try {
            config = new PropertiesConfiguration(propertiesFilePath);
            Iterator<String> iterator = config.getKeys();
            while (iterator.hasNext()) {
                String property = iterator.next();
                String sysValue = System.getProperty(property);
                if (sysValue != null) {
                    config.setProperty(property, sysValue);
                }
            }
        } catch (ConfigurationException e) {
            log.severe("Error while reading the configuration file");
        }
    }

    public static void initialize(String propertiesFilePath) {
        instance = new Config(propertiesFilePath);
    }

    public static Config getInstance() {
        if (instance == null) {
            log.severe("Configuration not yet initialized!");
        }
        return instance;
    }

    public String getServerVersion() {
        return config.getString(VERSION);
    }

    public String getServerRunUUID() {
        return config.getString(RUN);
    }

    public String getServerName() {
        return config.getString(NAME);
    }

    public int getServerPort() {
        return config.getInt(PORT);
    }

    public String getHostName() {
        return config.getString(HOST);
    }

    public boolean getEnableTSFunction() {
        return config.getBoolean(TIMESTAMP);
    }

    public boolean getSendEmptyResultsProperty() {
        return config.getBoolean(EMPTY_RESULTS);
    }

    public String getResourcesPath() {
        String resourcesPath = config.getString(RESOURCE_PATH);
        if (resourcesPath == null || resourcesPath.isEmpty()) {
            resourcesPath = System.getProperty("user.home");
        }
        return resourcesPath;
    }


    public String getMessageLog() {
        if (messageLog == null) {
            messageLog = config.getString(MESSAGE_LOG);
        }
        return messageLog;
    }


    public boolean isBackLoopActive() {
        return config.getBoolean(BACK_LOOP, false);
    }

    public static String getQuery() {
        return config.containsKey(SGRAPH_QUERY) ? config.getString(SGRAPH_QUERY) : def_query;
    }


}
