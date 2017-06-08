package net.apnic.rdap.util;

import java.util.concurrent.CompletableFuture;

import org.springframework.util.concurrent.ListenableFuture;

public class ConcurrentUtil
{
    public static <T> CompletableFuture<T>
        buildCompletableFuture(final ListenableFuture<T> lFuture)
    {
        CompletableFuture<T> cFuture = new CompletableFuture<T>()
        {
            @Override
            public boolean cancel(boolean interrupt)
            {
                boolean rval = lFuture.cancel(interrupt);
                super.cancel(interrupt);
                return rval;
            }
        };

        lFuture.addCallback(cFuture::complete, cFuture::completeExceptionally);
        return cFuture;
    }
}
