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
     * Main funneling method for all GET handlers.
     *
     * This method will take all the different forms of IP requests and
     * fulfills the original request.
     *
     * @param ipAddress The ip address to query for
     * @param cidr Optional cidr range for the ip address.
     * @return Response entity from the proxied server.
     */
    private DeferredResult<ResponseEntity<byte[]>>
        ipGetSegmentHandler(String ipAddress, Optional<String> cidr)
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
     * Main funneling method for all HEAD handlers.
     *
     * This method will take all the different forms of IP requests and
     * fulfills the original request.
     *
     * @param ipAddress The ip address to query for
     * @param cidr Optional cidr range for the ip address.
     * @return Response entity from the proxied server.
     */
    private DeferredResult<ResponseEntity<Void>>
        ipHeadSegmentHandler(String ipAddress, Optional<String> cidr)
    {
        DeferredResult<ResponseEntity<Void>> result =
            new DeferredResult<ResponseEntity<Void>>();

        String requestIp =
            ipAddress + (cidr.isPresent() ? "/" + cidr.get() : "");

        getRDAPClient().executeIPList(requestIp)
            .addCallback((ResponseEntity<Void> response) ->
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
     * GET request path segment for ip address without CIDR's
     *
     * @param ipAddress The ip address to proxy for.
     * @return Response entity from the proxied server.
     */
    @RequestMapping(value="/{ipAddress:.+}", method=RequestMethod.GET)
    public DeferredResult<ResponseEntity<byte[]>>
        ipGetWithoutCIDR(@PathVariable String ipAddress)
    {
        return ipGetSegmentHandler(ipAddress, Optional.empty());
    }

    /**
     * GET request path segment for ip address with CIDR's
     *
     * @param ipAddress The ip address to proxy for.
     * @param cidr The cidr range to proxy with the ip address.
     * @return Response entity from the proxied server.
     */
    @RequestMapping(value="/{ipAddress:.+}/{cidr:[0-9]+}",
                    method=RequestMethod.GET)
    public DeferredResult<ResponseEntity<byte[]>>
        ipGetWithCIDR(@PathVariable String ipAddress, @PathVariable String cidr)
   {
       return ipGetSegmentHandler(ipAddress, Optional.of(cidr));
   }

    /**
     * HEAD request path segment for ip address without CIDR's
     *
     * @param ipAddress The ip address to proxy for.
     * @return Response entity from the proxied server.
     */
    @RequestMapping(value="/{ipAddress:.+}", method=RequestMethod.HEAD)
    public DeferredResult<ResponseEntity<Void>>
        ipHeadWithoutCIDR(@PathVariable String ipAddress)
    {
        return ipHeadSegmentHandler(ipAddress, Optional.empty());
    }

    /**
     * HEAD request path segment for ip address with CIDR's
     *
     * @param ipAddress The ip address to proxy for.
     * @param cidr The cidr range to proxy with the ip address.
     * @return Response entity from the proxied server.
     */
    @RequestMapping(value="/{ipAddress:.+}/{cidr:[0-9]+}",
                    method=RequestMethod.HEAD)
    public DeferredResult<ResponseEntity<Void>>
        ipHeadWithCIDR(@PathVariable String ipAddress,
                       @PathVariable String cidr)
   {
       return ipHeadSegmentHandler(ipAddress, Optional.of(cidr));
   }
}
