package net.cyklotron.cms.modules.components.documents;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.Templating;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;
import org.objectledge.web.mvc.finders.MVCFinder;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.modules.components.SkinableCMSComponent;
import net.cyklotron.cms.skins.SkinService;

/**
 * Document print component displays a link to document printing page.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: PrintDocument.java,v 1.2 2005-01-25 11:24:19 pablo Exp $
 */
public class PrintDocument
    extends SkinableCMSComponent
{
    public PrintDocument(Context context, Logger logger, Templating templating,
        CmsDataFactory cmsDataFactory, SkinService skinService, MVCFinder mvcFinder)
    {
        super(context, logger, templating, cmsDataFactory, skinService, mvcFinder);
        // TODO Auto-generated constructor stub
    }

    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        CmsData cmsData = getCmsData();
		try
		{
			Resource parent = cmsData.getHomePage().getParent();
			Parameters conf = cmsData.getComponent().getConfiguration();
			String path = conf.get("printNodePath",null);
			if(path == null)
			{
				cmsData.getComponent().error("print node not configured", null);
				return;
			}
			Resource[] nodes = coralSession.getStore().getResourceByPath(parent, path);
			if(nodes.length == 0)
			{
				cmsData.getComponent().error("cannot find configured print node", null);
				return;
			}
			if(nodes.length > 1)
			{
				cmsData.getComponent().error("too many print nodes with the same path", null);
				return;
			}
			templatingContext.put("print_node", nodes[0]);
		}
		catch(Exception e)
		{
			cmsData.getComponent().error("cannot find configured print node", e);
		}
    }
}
