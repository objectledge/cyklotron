/*
 * Created on 2005-01-27
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.cyklotron.cms;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.objectledge.context.Context;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.tools.LinkToolFactory;
import org.objectledge.web.mvc.tools.PageTool;
import org.objectledge.web.mvc.tools.PageToolFactory;

import net.cyklotron.cms.canonical.CanonicalLinksService;

/**
 * @author pablo To change the template for this generated type comment go to Window - Preferences -
 *         Java - Code Style - Code Templates
 */
public class CmsPageToolFactory
    extends PageToolFactory
{
    private final CanonicalLinksService canonicalLinksService;

    public CmsPageToolFactory(Configuration config, LinkToolFactory linkToolFactory,
        Context context, CanonicalLinksService canonicalLinksService)
        throws ConfigurationException
    {
        super(config, linkToolFactory, context);
        this.canonicalLinksService = canonicalLinksService;
    }

    /**
     * {@inheritDoc}
     */
    public Object getTool()
        throws ProcessingException
    {
        return new CmsPageTool((CmsLinkTool)linkToolFactory.getTool(),
            HttpContext.getHttpContext(context), pageToolConfiguration, canonicalLinksService,
            context);
    }

    /**
     * {@inheritDoc}
     */
    public void recycleTool(Object tool)
        throws ProcessingException
    {
        linkToolFactory.recycleTool(((PageTool)tool).getLinkTool());
    }
}
