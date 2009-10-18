package net.cyklotron.cms.modules.actions.fixes;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.CmsNodeResourceImpl;
import net.cyklotron.cms.integration.ApplicationResource;
import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.cyklotron.cms.modules.actions.structure.workflow.MoveToWaitingRoom;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.skins.SkinResource;
import net.cyklotron.cms.skins.SkinService;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.StructureService;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.InvalidResourceNameException;
import org.objectledge.coral.store.Resource;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

/**
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id$
 */
public class CYKLO483 extends BaseCMSAction
{
    private final SiteService siteService;

    public CYKLO483(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, SiteService siteService)
    {
        super(logger, structureService, cmsDataFactory);
        this.siteService = siteService;
    }
    
    public void execute(Context context, Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            SiteResource[] sites = siteService.getSites(coralSession);
            for(SiteResource site : sites)
            {
                NavigationNodeResource homePage = structureService.getRootNode(coralSession, site);
                Resource[] parents = coralSession.getStore().getResource(homePage,
                    MoveToWaitingRoom.WAITING_ROOM_NAME);
                if(parents.length > 0)
                {
                    Resource[] resources = coralSession.getStore().getResource(parents[0]);
                    for(Resource resource: resources)
                    {
                        NavigationNodeResource node = (NavigationNodeResource)resource;
                        if(node.getValidityStart() != null)
                        {
                            String timeStructureType = structureService.getTimeStructureType(node);
                            if(timeStructureType.equals(StructureService.NONE_CALENDAR_TREE_STRUCTURE))
                            {
                                Resource newParent = structureService.getParent(coralSession, parents[0], 
                                    node.getValidityStart(), StructureService.MONTHLY_CALENDAR_TREE_STRUCTURE,
                                    coralSession.getUserSubject());
                                coralSession.getStore().setParent(node, newParent);
                            }
                        }
                    }
                }
            }
        }
        catch(Exception e)
        {
            throw new ProcessingException("Invalid name exception", e);
        }
        templatingContext.put("result", "success");
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
