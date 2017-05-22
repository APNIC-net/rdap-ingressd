package net.apnic.rdap;

import java.util.Properties;

import net.apnic.rdap.client.RDAPClient;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;

/**
 * Entry point for rdap-ingressd
 */
@SpringBootApplication
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
        app.setDefaultProperties(defaultProps);
        app.run(args);
    }

    @Bean
    public RDAPClient rdapClient()
    {
        return new RDAPClient("https://rdap.apnic.net");
    }
}
