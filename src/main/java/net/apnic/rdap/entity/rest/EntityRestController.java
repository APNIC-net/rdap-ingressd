package net.apnic.rdap.entity.rest;

import net.apnic.rdap.path.rest.PathRestController;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.async.DeferredResult;

/**
 * Rest controller for handling entity path segment requests in RDAP
 */
@RestController
@RequestMapping("/entity")
public class EntityRestController
    extends PathRestController
{
    /**
     * GET request path segment for entities.
     *
     * @entity The entity to proxy for.
     * @return Response entity from the proxied server
     */
    @RequestMapping(value="/{entity}", method=RequestMethod.GET)
    public DeferredResult<ResponseEntity<byte[]>>
        entityGet(@PathVariable String entity)
    {
        DeferredResult<ResponseEntity<byte[]>> result =
            new DeferredResult<ResponseEntity<byte[]>>();

        getRDAPClient().executeRawEntityQuery(entity)
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
     * HEAD request path segment for entities.
     *
     * @entity The entity to proxy for.
     * @return Response entity from the proxied server
     */
    @RequestMapping(value="/{entity}", method=RequestMethod.HEAD)
    public DeferredResult<ResponseEntity<Void>>
        entityHead(@PathVariable String entity)
    {
        DeferredResult<ResponseEntity<Void>> result =
            new DeferredResult<ResponseEntity<Void>>();

        getRDAPClient().executeEntityList(entity)
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
