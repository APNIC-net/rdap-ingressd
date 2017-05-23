package net.apnic.rdap.autnum.rest;

import net.apnic.rdap.path.rest.PathRestController;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.async.DeferredResult;

/**
 * Rest controller for handling AS number path segments in RDAP.
 */
@RestController
@RequestMapping("/autnum")
public class AutnumRestController
    extends PathRestController
{
    /**
     * GET request path segment for AS numbers.
     *
     * @param autnum The AS number to proxy for.
     * @return Response entity from the proxied server.
     */
    @RequestMapping(value="/{autnum}", method=RequestMethod.GET)
    public DeferredResult<ResponseEntity<byte[]>>
        autnumGet(@PathVariable String autnum)
    {
        DeferredResult<ResponseEntity<byte[]>> result =
            new DeferredResult<ResponseEntity<byte[]>>();

        getRDAPClient().executeRawAutnumQuery(autnum)
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
     * HEAD request path segment for AS numbers.
     *
     * @autnum The AS number to proxy for.
     * @return Response entity from the proxied server
     */
    @RequestMapping(value="/{autnum}", method=RequestMethod.HEAD)
    public DeferredResult<ResponseEntity<Void>>
        autnumHead(@PathVariable String autnum)
    {
        DeferredResult<ResponseEntity<Void>> result =
            new DeferredResult<ResponseEntity<Void>>();

        getRDAPClient().executeAutnumList(autnum)
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
}
