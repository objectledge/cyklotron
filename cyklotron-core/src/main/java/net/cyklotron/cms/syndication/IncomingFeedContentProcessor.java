package net.cyklotron.cms.syndication;

/**
 * The feed processor processes feed contents.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: IncomingFeedContentProcessor.java,v 1.1 2005-06-16 11:14:22 zwierzem Exp $
 */
public interface IncomingFeedContentProcessor
{
    public String process() throws Exception;
}
