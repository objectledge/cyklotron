package net.cyklotron.cms.modules.actions.search;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.cyklotron.cms.search.ExternalPoolResource;
import net.cyklotron.cms.search.IndexResource;
import net.cyklotron.cms.search.PoolResource;
import net.cyklotron.cms.search.SearchService;
import net.cyklotron.cms.search.SearchUtil;
import net.cyklotron.cms.structure.StructureService;

/**
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: BaseSearchAction.java,v 1.2 2005-01-24 10:27:13 pablo Exp $
 */
public abstract class BaseSearchAction
    extends BaseCMSAction
{
    /** search service */
    protected SearchService searchService;

    
    public BaseSearchAction(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, SearchService searchService)
    {
        super(logger, structureService, cmsDataFactory);
        this.searchService = searchService;
    }

    public IndexResource getIndex(CoralSession coralSession, Parameters parameters)
        throws ProcessingException
    {
        return SearchUtil.getIndex(coralSession, parameters);
    }

    public PoolResource getPool(CoralSession coralSession, Parameters parameters)
        throws ProcessingException
    {
        return SearchUtil.getPool(coralSession, parameters);
    }

    public ExternalPoolResource getExternalPool(CoralSession coralSession, Parameters parameters)
        throws ProcessingException
    {
        return SearchUtil.getExternalPool(coralSession, parameters);
    }

    public boolean checkPermission(Context context, CoralSession coralSession, String permissionName)
        throws ProcessingException
    {
        Parameters parameters = RequestParameters.getRequestParameters(context);
        return SearchUtil.checkPermission(coralSession, parameters, permissionName);
    }
}
