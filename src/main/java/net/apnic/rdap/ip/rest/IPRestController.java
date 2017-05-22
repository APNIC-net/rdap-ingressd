package net.apnic.rdap.ip.rest;

import java.util.Optional;
import javax.servlet.http.HttpServletRequest;

import net.apnic.rdap.path.rest.PathRestController;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.HandlerMapping;

/**
 * Rest controller for the RDAP ip path segment.
 */
@RestController
@RequestMapping("/ip")
public class IPRestController
    extends PathRestController
{
    /**
     *
     */
    private DeferredResult<ResponseEntity<byte[]>>
        ipSegmentHandler(String ipAddress, Optional<String> cidr)
    {
        DeferredResult<ResponseEntity<byte[]>> result =
            new DeferredResult<ResponseEntity<byte[]>>();

        String requestIp =
            ipAddress + (cidr.isPresent() ? "/" + cidr.get() : "");

        getRDAPClient().executeRawIPQuery(requestIp)
            .addCallback((ResponseEntity<byte[]> response) ->
            {
                result.setResult(response);
            },
            (Throwable ex) ->
            {
                result.setErrorResult(ex);
            });

        return result;
    }

    /**
     *
     */
    @RequestMapping(value="/{ipAddress:.+}", method=RequestMethod.GET)
    public DeferredResult<ResponseEntity<byte[]>>
        ipWithoutCIDR(@PathVariable String ipAddress)
    {
        return ipSegmentHandler(ipAddress, Optional.empty());
    }

    @RequestMapping(value="/{ipAddress:.+}/{cidr:[0-9]+}",
                    method=RequestMethod.GET)
    public DeferredResult<ResponseEntity<byte[]>>
        ipWithCIDR(@PathVariable String ipAddress, @PathVariable String cidr)
   {
       return ipSegmentHandler(ipAddress, Optional.of(cidr));
   }
}
