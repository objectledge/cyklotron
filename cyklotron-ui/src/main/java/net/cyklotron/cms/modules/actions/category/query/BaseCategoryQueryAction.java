package net.cyklotron.cms.modules.actions.category.query;

import net.cyklotron.cms.category.CategoryService;
import net.cyklotron.cms.category.query.CategoryQueryPoolResource;
import net.cyklotron.cms.category.query.CategoryQueryResource;
import net.cyklotron.cms.category.query.CategoryQueryService;
import net.cyklotron.cms.category.query.CategoryQueryUtil;
import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.cyklotron.cms.site.SiteService;

import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.pipeline.ProcessingException;

/**
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: BaseCategoryQueryAction.java,v 1.1 2005-01-24 04:34:35 pablo Exp $
 */
public abstract class BaseCategoryQueryAction
    extends BaseCMSAction
{
    protected Logger log;
	protected CategoryQueryService categoryQueryService;
	/** category service */
	protected CategoryService categoryService;
	/** site service */
	protected SiteService siteService;

    public BaseCategoryQueryAction()
    {
		log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME))
			.getFacility(CategoryQueryService.LOGGING_FACILITY);
		categoryQueryService =
			(CategoryQueryService) broker.getService(CategoryQueryService.SERVICE_NAME);
		categoryService = (CategoryService)broker.getService(CategoryService.SERVICE_NAME);
		siteService = (SiteService)broker.getService(SiteService.SERVICE_NAME);
    }

	protected CategoryQueryResource getQuery(RunData data)
		throws ProcessingException
	{
		return CategoryQueryUtil.getQuery(coralSession, data);
	}

	protected CategoryQueryPoolResource getPool(RunData data)
		throws ProcessingException
	{
		return CategoryQueryUtil.getPool(coralSession, data);
	}

    public boolean checkPermission(Context context, CoralSession coralSession, String permissionName)
        throws ProcessingException
    {
        return CategoryQueryUtil.checkPermission(coralSession, data, permissionName);
    }
}
