package net.apnic.rdap;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.SpringApplication;

@EnableAutoConfiguration
public class Application
{
    public static void main(String[] args)
        throws Exception
    {
        SpringApplication.run(Application.class, args);
    }
}
