/*
 * Created on 2005-01-27
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.cyklotron.cms;

import org.objectledge.pipeline.ProcessingException;
import org.objectledge.context.Context;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.tools.LinkToolFactory;
import org.objectledge.web.mvc.tools.PageTool;
import org.objectledge.web.mvc.tools.PageToolFactory;

/**
 * @author pablo
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CmsPageToolFactory extends PageToolFactory
{
    public CmsPageToolFactory(LinkToolFactory linkToolFactory, Context context)
    {
        super(linkToolFactory, context);
        
    }
    
    /**
     * {@inheritDoc}
     */
    public Object getTool()
        throws ProcessingException
    {
        return new CmsPageTool((CmsLinkTool) linkToolFactory.getTool(),
            HttpContext.getHttpContext(context));
    }
    
    /**
     * {@inheritDoc}
     */
    public void recycleTool(Object tool)
        throws ProcessingException
    {
        linkToolFactory.recycleTool( ((PageTool) tool).getLinkTool() );
    }
}
