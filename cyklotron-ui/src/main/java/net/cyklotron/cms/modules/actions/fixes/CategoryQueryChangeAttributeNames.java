package net.cyklotron.cms.modules.actions.fixes;

import net.cyklotron.cms.category.query.CategoryQueryResource;
import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.labeo.services.resource.AttributeDefinition;
import net.labeo.services.resource.ResourceClass;
import net.labeo.services.resource.Subject;
import net.labeo.services.templating.Context;
import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 * Changes the category query attribute names.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: CategoryQueryChangeAttributeNames.java,v 1.1 2005-01-24 04:34:29 pablo Exp $
 */
public class CategoryQueryChangeAttributeNames
extends BaseCMSAction
{
	/* (non-Javadoc)
	 * @see net.labeo.webcore.Action#execute(net.labeo.webcore.RunData)
	 */
	public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession) throws ProcessingException
	{
		Context context = data.getContext();
		Subject subject = coralSession.getUserSubject();
		try
		{
			ResourceClass categoryQueryResClass =
				coralSession.getSchema().getResourceClass(CategoryQueryResource.CLASS_NAME);
			AttributeDefinition requiredCategoryIds = categoryQueryResClass.getAttribute("requiredCategoryIds");
			AttributeDefinition optionalCategoryIds = categoryQueryResClass.getAttribute("optionalCategoryIds");
			coralSession.getSchema().setName(requiredCategoryIds, "requiredCategoryPaths");
			coralSession.getSchema().setName(optionalCategoryIds, "optionalCategoryPaths");
		}
		catch(Exception e)
		{
			templatingContext.put("result", "exception");
			templatingContext.put("trace", StringUtils.stackTrace(e));
		}
	}
}
