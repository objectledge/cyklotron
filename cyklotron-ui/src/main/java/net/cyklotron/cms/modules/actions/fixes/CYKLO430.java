package net.cyklotron.cms.modules.actions.fixes;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.datatypes.ResourceList;
import org.objectledge.coral.query.MalformedQueryException;
import org.objectledge.coral.query.QueryResults;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.table.comparator.NameComparator;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.category.query.CategoryQueryPoolResource;
import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.cyklotron.cms.structure.StructureService;

public class CYKLO430
    extends BaseCMSAction
{
    public CYKLO430(Logger logger, StructureService structureService, CmsDataFactory cmsDataFactory)
    {
        super(logger, structureService, cmsDataFactory);
    }

    @Override
    public void execute(Context context, Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            I18nContext i18nContext = context.getAttribute(I18nContext.class);
            Comparator comp = new NameComparator(i18nContext.getLocale());
            QueryResults res = coralSession.getQuery().executeQuery(
                "FIND RESOURCE FROM category.query.pool");
            List<Resource> queryPools = res.getList(1);
            for(Resource r : queryPools)
            {
                CategoryQueryPoolResource queryPool = (CategoryQueryPoolResource)r;
                ResourceList queries = queryPool.getQueries();
                Collections.sort(queries, comp);
                queryPool.setQueries(queries);
                queryPool.update();
            }  
            templatingContext.put("result", "success");
            templatingContext.put("info", "updated " + queryPools.size() + " query pools");
        }
        catch(MalformedQueryException e)
        {
            throw new ProcessingException("unexpected exception", e);
        }
    }

    /**
     * @{inheritDoc}
     */
    public boolean checkAccessRights(Context context)
        throws Exception
    {
        CoralSession coralSession = context.getAttribute(CoralSession.class);
        return (coralSession.getUserSubject().getId() == Subject.ROOT);
    }

}
