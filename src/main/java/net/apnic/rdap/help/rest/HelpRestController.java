package net.apnic.rdap.help.rest;

import net.apnic.rdap.path.rest.PathRestController;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.async.DeferredResult;

@RestController
@RequestMapping("/help")
public class HelpRestController
    extends PathRestController
{
    @RequestMapping(value="/", method=RequestMethod.GET)
    public DeferredResult<ResponseEntity<byte[]>>
        helpGet()
    {
        DeferredResult<ResponseEntity<byte[]>> result =
            new DeferredResult<ResponseEntity<byte[]>>();

        getRDAPClient().executeRawHelpQuery()
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
