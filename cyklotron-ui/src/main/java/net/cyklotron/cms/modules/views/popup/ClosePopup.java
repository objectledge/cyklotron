package net.cyklotron.cms.modules.views.popup;

import net.labeo.services.templating.Context;
import net.labeo.services.webcore.NotFoundException;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;
import net.labeo.webcore.Screen;

/**
 * Screen which automaticaly closes it's window.
 *
 * <p>It is useful when a popup has a form which when posted must close the popup,
 * but cannot change the state of the opening window.</p>
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: ClosePopup.java,v 1.1 2005-01-24 04:34:11 pablo Exp $
 */
public class ClosePopup implements Screen
{
    public static String CLOSE_POPUP_SCREEN_CONTENT =
        "<script type=\"text/javascript\">\n"+
        "<!--\n"+
        "function closePopup()\n"+
        "{\n"+
        "    window.close();\n"+
        "}\n"+
        "closePopup();\n"+
        "// -->\n"+
        "</script>\n";
    
    public static String CLOSE_POPUP_RELOAD_OPENER_SCREEN_CONTENT =
        "<script type=\"text/javascript\">\n"+
        "<!--\n"+
        "function closePopup()\n"+
        "{\n"+
        "    window.opener.location.href = window.opener.location.href;\n"+
        "    window.close();\n"+
        "}\n"+
        "closePopup();\n"+
        "// -->\n"+
        "</script>\n";
    
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession)
        throws ProcessingException
    {
    }
    
    public String build(RunData data) throws ProcessingException, SecurityException
    {
        if(parameters.get("close_popup_reload").isDefined())
        {
            return CLOSE_POPUP_RELOAD_OPENER_SCREEN_CONTENT;
        }
        else
        {
            return CLOSE_POPUP_SCREEN_CONTENT;
        }
    }
    
    public Screen route(RunData data) throws ProcessingException, NotFoundException
    {
        return this;
    }
}
