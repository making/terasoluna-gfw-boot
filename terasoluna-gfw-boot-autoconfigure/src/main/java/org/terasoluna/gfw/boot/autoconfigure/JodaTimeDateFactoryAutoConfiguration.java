package org.terasoluna.gfw.boot.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.terasoluna.gfw.common.date.jodatime.DefaultJodaTimeDateFactory;
import org.terasoluna.gfw.common.date.jodatime.JodaTimeDateFactory;

@Configuration
@ConditionalOnClass(JodaTimeDateFactory.class)
public class JodaTimeDateFactoryAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public JodaTimeDateFactory dateFactory() {
        return new DefaultJodaTimeDateFactory();
    }
}
