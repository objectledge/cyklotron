package net.cyklotron.cms.search.util;

public interface Cancellable
{
    /**
     * Called when another resource has been processed. Return true to continue, false to abort and
     * rollback processing.
     * 
     * @return
     */
    boolean isCancelled();
}
