package org.terasoluna.gfw.boot.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashMap;

@ConfigurationProperties(prefix = TerasolunaProperties.TERASOLUNA_PREFIX)
@Data
public class TerasolunaProperties {
    public static final String TERASOLUNA_PREFIX = "terasoluna";

    private final Exception exception = new Exception();

    public Exception getException() {
        return this.exception;
    }

    @Data
    public static class Exception {
        private boolean loggingAdviserEnabled = true;
        private String defaultExceptionCode = "e.xx.fw.9001";
        private LinkedHashMap<String, String> codeMappings = new LinkedHashMap<String, String>();
    }
}
