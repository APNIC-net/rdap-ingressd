package net.apnic.rdap;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.SpringApplication;

/**
 * Entry point for rdap-ingressd
 */
@SpringBootApplication
public class Application
{
    public static void main(String[] args)
        throws Exception
    {
        SpringApplication.run(Application.class, args);
    }
}
