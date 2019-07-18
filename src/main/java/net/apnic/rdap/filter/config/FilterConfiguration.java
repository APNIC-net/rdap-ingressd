package net.apnic.rdap.filter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.filter.CorsFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 */
@Configuration
public class FilterConfiguration
{
    @Bean
    public CorsFilter corsFilter()
    {
        CorsFilter filter = new CorsFilter((HttpServletRequest req) -> {
            return new CorsConfiguration();
        });
        filter.setCorsProcessor(
            (CorsConfiguration c, HttpServletRequest req,
             HttpServletResponse res) -> {

            ServletServerHttpResponse r = new ServletServerHttpResponse(res);
            System.out.println(r.getHeaders().getAccessControlAllowOrigin());

            if(CorsUtils.isCorsRequest(req) == false ||
               res.getHeaders(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN).isEmpty() == false) {
                return true;
            }

            if(CorsUtils.isPreFlightRequest(req)) {
                res.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS,
                    "GET, HEAD, OPTION");
            }

            res.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
            return true;
        });
        return filter;
    }
}
