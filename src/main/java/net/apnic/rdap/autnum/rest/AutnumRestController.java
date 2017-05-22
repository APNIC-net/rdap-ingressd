package net.apnic.rdap.autnum.rest;

import net.apnic.rdap.path.rest.PathRestController;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.async.DeferredResult;

@RestController
@RequestMapping("/autnum")
public class AutnumRestController
    extends PathRestController
{
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
}
