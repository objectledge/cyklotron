package net.cyklotron.cms.modules.actions.site;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.pipeline.ProcessingException;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.StructureService;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: BaseSiteAction.java,v 1.2 2005-01-24 10:27:50 pablo Exp $
 */
public abstract class BaseSiteAction
    extends BaseCMSAction
{
    /** structure service */
    protected SiteService ss;

    
    public BaseSiteAction(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, SiteService siteService)
    {
        super(logger, structureService, cmsDataFactory);
        ss = siteService;
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        return checkAdministrator(context);
    }
}
