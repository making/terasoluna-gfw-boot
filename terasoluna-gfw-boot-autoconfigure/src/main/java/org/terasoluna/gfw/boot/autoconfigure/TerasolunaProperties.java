package org.terasoluna.gfw.boot.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashMap;
import java.util.Properties;
import java.util.regex.Pattern;

@ConfigurationProperties(prefix = TerasolunaProperties.TERASOLUNA_PREFIX)
@Data
public class TerasolunaProperties {
    public static final String TERASOLUNA_PREFIX = "terasoluna";

    private final Mvc mvc = new Mvc();
    private final Exception exception = new Exception();

    public Mvc getMvc() {
        return this.mvc;
    }

    public Exception getException() {
        return this.exception;
    }

    @Data
    public static class Exception {
        private boolean loggingAdviserEnabled = true;
        private String defaultExceptionCode = "e.xx.fw.9001";
        private LinkedHashMap<String, String> codeMappings = new LinkedHashMap<String, String>();
    }

    @Data
    public static class Mvc {
        private boolean enabled = true;
        private Pattern codeListIdPattern = Pattern.compile("CL_.+");
        private Properties exceptionMappings = new Properties();
        private Properties statusCodes = new Properties();
        private String defaultErrorView = "error";
        private int defaultStatusCode = 500;
    }
}
