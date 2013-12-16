package net.cyklotron.cms.sitemap;

/**
 * Thrown by {@link SitemapGenerationParticipant} on problems with retrieval of relevant resources.
 * 
 * @author rafal.krzewski@caltha.pl
 */
public class RetrievalException
    extends Exception
{
    public RetrievalException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public RetrievalException(String message)
    {
        super(message);
    }
}
