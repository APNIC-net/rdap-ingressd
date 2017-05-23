package net.apnic.rdap.nameserver.rest;

import net.apnic.rdap.path.rest.PathRestController;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.async.DeferredResult;

/**
 * Rest controller for handling name server path segments in RDAP.
 */
@RestController
@RequestMapping("/nameserver")
public class NameserverRestController
    extends PathRestController
{
    /**
     * GET request path segment for name server.
     *
     * @param nameserver The name server to proxy for
     * @return Response entity from the proxied server.
     */
    @RequestMapping(value="/{nameserver:.+}", method=RequestMethod.GET)
    public DeferredResult<ResponseEntity<byte[]>>
        nameserverGet(@PathVariable String nameserver)
    {
        DeferredResult<ResponseEntity<byte[]>> result =
            new DeferredResult<ResponseEntity<byte[]>>();

        getRDAPClient().executeRawNameserverQuery(nameserver)
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
     * HEAD request path segment for name server.
     *
     * @param nameserver The name server to proxy for
     * @return Response entity from the proxied server.
     */
    @RequestMapping(value="/{nameserver:.+}", method=RequestMethod.HEAD)
    public DeferredResult<ResponseEntity<Void>>
        nameserverHead(@PathVariable String nameserver)
    {
        DeferredResult<ResponseEntity<Void>> result =
            new DeferredResult<ResponseEntity<Void>>();

        getRDAPClient().executeNameserverList(nameserver)
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
