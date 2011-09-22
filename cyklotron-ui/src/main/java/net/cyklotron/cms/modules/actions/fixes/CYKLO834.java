package net.cyklotron.cms.modules.actions.fixes;

import java.util.ArrayList;
import java.util.List;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.datatypes.NodeImpl;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.InvalidResourceNameException;
import org.objectledge.coral.store.Resource;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.documents.DocumentService;
import net.cyklotron.cms.documents.FooterResource;
import net.cyklotron.cms.documents.internal.DocumentServiceImpl;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.cyklotron.cms.poll.PollResource;
import net.cyklotron.cms.poll.PollService;
import net.cyklotron.cms.poll.PollsResource;
import net.cyklotron.cms.poll.PoolResource;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.skins.SkinService;
import net.cyklotron.cms.structure.StructureService;

/**
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: ConvertCategoryQuery1B.java,v 1.3 2007-11-18 21:24:37 rafal Exp $
 */
public class CYKLO834
    extends BaseCMSAction
{

    SiteService siteService;

    DocumentService documentService;

    public CYKLO834(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, SiteService siteService, SkinService skinService,
        IntegrationService integrationService, DocumentService documentService)
    {
        super(logger, structureService, cmsDataFactory);
        this.siteService = siteService;
        this.documentService = documentService;
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
            	Resource footersRoot = getOldFootersRoot(coralSession, site);
            	if(footersRoot != null)
            	{
            		Resource documentsRoot = documentService.getDocumentsRoot(coralSession, site);
            		coralSession.getStore().setParent(footersRoot, documentsRoot);
            	}
            }
            templatingContext.put("result", "success");
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to reorganized footers resources", e);
        }
    }
    
    
    private Resource getOldFootersRoot(CoralSession coralSession, SiteResource site)
            throws InvalidResourceNameException
        {
        	Resource[] applications = coralSession.getStore().getResource(site, "applications");
        	if(applications.length != 1)
        	{
        		throw new IllegalStateException("there should be one and only one applications node in site: " + site.getName());
        	}
            Resource[] footers = coralSession.getStore().getResource(applications[0], "footers");
            if(footers.length > 1)
            {
                throw new IllegalStateException("thers should be only one footers application in site:"+site.getName());
            }
            if(footers.length == 1)
            {
                return footers[0];
            }
            return null;
        }
}
