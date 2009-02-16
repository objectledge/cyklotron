package net.cyklotron.cms.syndication.internal;

import net.cyklotron.cms.syndication.IncomingFeedContentProcessor;

/**
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: PassThroughIncomingFeedContentProcessor.java,v 1.1 2005-06-16 11:14:21 zwierzem Exp $
 */
public class PassThroughIncomingFeedContentProcessor implements IncomingFeedContentProcessor
{
    private String contents;
    
    public PassThroughIncomingFeedContentProcessor(String contents)
    {
        this.contents = contents;
    }

    public String process()
    {
        return contents;
    }
}
