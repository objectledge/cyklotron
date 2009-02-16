package net.cyklotron.cms.modules.actions.category.query;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.category.CategoryService;
import net.cyklotron.cms.category.query.CategoryQueryPoolResource;
import net.cyklotron.cms.category.query.CategoryQueryResource;
import net.cyklotron.cms.category.query.CategoryQueryService;
import net.cyklotron.cms.category.query.CategoryQueryUtil;
import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.StructureService;

/**
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: BaseCategoryQueryAction.java,v 1.2 2005-01-24 10:27:21 pablo Exp $
 */
public abstract class BaseCategoryQueryAction
    extends BaseCMSAction
{
	protected CategoryQueryService categoryQueryService;
	/** category service */
	protected CategoryService categoryService;
	/** site service */
	protected SiteService siteService;

    
    
    public BaseCategoryQueryAction(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, CategoryQueryService categoryQueryService, 
        CategoryService categoryService, SiteService siteService)
    {
        super(logger, structureService, cmsDataFactory);
        this.categoryQueryService = categoryQueryService;
		this.categoryService = categoryService;
		this.siteService = siteService;
    }

	protected CategoryQueryResource getQuery(CoralSession coralSession, Parameters parameters)
		throws ProcessingException
	{
		return CategoryQueryUtil.getQuery(coralSession, parameters);
	}

	protected CategoryQueryPoolResource getPool(CoralSession coralSession, Parameters parameters)
		throws ProcessingException
	{
		return CategoryQueryUtil.getPool(coralSession, parameters);
	}

    public boolean checkPermission(Context context, CoralSession coralSession, String permissionName)
        throws ProcessingException
    {
        Parameters parameters = RequestParameters.getRequestParameters(context);
        return CategoryQueryUtil.checkPermission(coralSession, parameters, permissionName);
    }
}
