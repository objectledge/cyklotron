package net.cyklotron.cms.modules.actions.structure;

import java.util.Date;

import org.objectledge.pipeline.ProcessingException;

import net.cyklotron.cms.structure.NavigationNodeResource;

/**
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: BaseAddEditNodeAction.java,v 1.2 2005-01-24 10:26:59 pablo Exp $
 */
public abstract class BaseAddEditNodeAction extends BaseStructureAction
{
    protected boolean checkParameters(RunData data)
        throws ProcessingException
    {
        // parameters setup
        String name = parameters.get("name","");
        String title = parameters.get("title","");

        // parameters check
        if(name.equals(""))
        {
            errorResult(data, "navi_name_empty");
            return false;
        }
        if(title.equals(""))
        {
            errorResult(data, "navi_title_empty");
            return false;
        }
        return true;
    }

    protected void setValidity(RunData data, NavigationNodeResource node)
    {
        if(parameters.get("validity_start").length() > 0)
        {
            node.setValidityStart(new Date(parameters.get("validity_start").asLong()));
        }
        else
        {
            node.setValidityStart(null);
        }
        
        if(parameters.get("validity_end").length() > 0)
        {
            node.setValidityEnd(new Date(parameters.get("validity_end").asLong()));
        }        
        else
        {
            node.setValidityEnd(null);
        }
    }
    
    protected abstract String getViewName();
    
    protected void errorResult(RunData data, String result)
        throws ProcessingException
    {
        data.getContext().put("result", result);
        try
        {
            mvcContext.setView(getViewName());
        }
        catch(NotFoundException e)
        {
            throw new ProcessingException("Cannot find view", e);
        }
    }
}
