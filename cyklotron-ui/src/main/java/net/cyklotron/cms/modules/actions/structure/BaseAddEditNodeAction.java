package net.cyklotron.cms.modules.actions.structure;

import java.util.Date;

import org.jcontainer.dna.Logger;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;

/**
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: BaseAddEditNodeAction.java,v 1.4 2005-03-08 10:54:17 pablo Exp $
 */
public abstract class BaseAddEditNodeAction extends BaseStructureAction
{
    
    public BaseAddEditNodeAction(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, StyleService styleService)
    {
        super(logger, structureService, cmsDataFactory, styleService);
        
    }
    
    protected boolean checkParameters(Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext)
        throws ProcessingException
    {
        // parameters setup
        String name = parameters.get("name","");
        String title = parameters.get("title","");

        // parameters check
        if(name.equals(""))
        {
            route(mvcContext, templatingContext, getViewName(), "navi_name_empty");
            return false;
        }
        if(title.equals(""))
        {
            route(mvcContext, templatingContext, getViewName(), "navi_title_empty");
            return false;
        }
        return true;
    }

    protected void setValidity(Parameters parameters, NavigationNodeResource node)
    {
        if(parameters.get("validity_start").length() > 0)
        {
            node.setValidityStart(new Date(parameters.getLong("validity_start")));
        }
        else
        {
            node.setValidityStart(null);
        }
        
        if(parameters.get("validity_end").length() > 0)
        {
            node.setValidityEnd(new Date(parameters.getLong("validity_end")));
        }        
        else
        {
            node.setValidityEnd(null);
        }
    }
    
    protected abstract String getViewName();
   
}
