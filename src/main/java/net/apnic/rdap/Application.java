package net.apnic.rdap;

import java.util.Properties;

import net.apnic.rdap.directory.Directory;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.ErrorMvcAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulServer;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * Entry point for rdap-ingressd
 */
@EnableZuulProxy
@SpringBootApplication
@EnableAutoConfiguration(exclude={ErrorMvcAutoConfiguration.class})
public class Application
{
    public static void main(String[] args)
        throws Exception
    {
        SpringApplication app = new SpringApplication(Application.class);
        Properties defaultProps = new Properties();

        defaultProps.setProperty(
                "spring.mvc.throw-exception-if-no-handler-found", "true");
        defaultProps.setProperty("spring.resources.add-mappings", "false");
        defaultProps.setProperty("spring.mvc.favicon.enabled", "false");
        defaultProps.setProperty("management.add-application-context-header", "false");
        defaultProps.setProperty("zuul.SendErrorFilter.error.disable", "true");
        app.setDefaultProperties(defaultProps);
        app.run(args);
    }
}
