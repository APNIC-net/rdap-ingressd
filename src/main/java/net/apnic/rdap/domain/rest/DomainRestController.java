package net.apnic.rdap.domain.rest;

import net.apnic.rdap.path.rest.PathRestController;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.async.DeferredResult;

@RestController
@RequestMapping("/domain")
public class DomainRestController
    extends PathRestController
{
    @RequestMapping(value="/{domain:.+}", method=RequestMethod.GET)
    public DeferredResult<ResponseEntity<byte[]>>
        domainGet(@PathVariable String domain)
    {
        DeferredResult<ResponseEntity<byte[]>> result =
            new DeferredResult<ResponseEntity<byte[]>>();

        getRDAPClient().executeRawDomainQuery(domain)
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
