package net.cyklotron.cms.modules.hooks;

import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.i18n.I18n;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.pipeline.Valve;
import org.objectledge.web.HttpContext;

/**
 * Sets the user's prefered locale according to information in the HTTP cookies
 * and session.
 *
 * <p>To use the hook you need to register it both WebcoreService and 
 * AuthenticationService:<br/>
 * <code>service.webcore.hook.pre=net.labeo.modules.hooks.LocaleHook<br/>
 * service.authentication.hook.postLogin=net.labeo.modules.hooks.LocaleHook
 * </code></p>
 *
 * <p>Cookies are named in a following way:</p>
 *
 * <ul>
 * <li>locale cookie - <code>labeo.locale</code> or
 * <code>labeo.locale.&lt;principal name&gt;</code>,
 * see {@link net.labeo.modules.actions.webcore.SetLocale}</li>
 * <li>encoding cookie - <code>labeo.encoding</code> or
 * <code>labeo.encoding.&lt;principal name&gt;</code>,
 * see {@link net.labeo.modules.actions.webcore.SetEncoding}</li>
 * </ul>
 *
 * <p>Principal name is used to differentiate locale and encoding settings
 * for users which use the browser on a single user setup.</p>
 *
 * @author <a href="mailto:rafal@apache.org">Rafal Krzewski</a>
 * @version $Id: LocaleWithDefaultEncodingHook.java,v 1.1 2005-01-24 04:34:49 pablo Exp $
 */
public class LocaleWithDefaultEncodingHook
    implements Valve
{
    /** i18n service  */
    private I18n i18n;

    public LocaleWithDefaultEncodingHook(I18n i18n)
    {
        this.i18n = i18n;
    }
    
    /**
     * @inheritDoc{}  
     */
    public void process(Context context) throws ProcessingException
    {
        Parameters parameters = RequestParameters.getRequestParameters(context);
        HttpContext httpContext = HttpContext.getHttpContext(context);
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
       
        // set up cookie keys - neccessary for browsers with multiple
        // users on a single user system - for instance Win95/98
        /**
        String cookieKey = ".anonymous";
        java.security.Principal principal = data.getUserPrincipal();
        if(principal != null && principal.getName() != null)
        {
            cookieKey = "." + StringUtils.cookieNameSafeString(principal.getName());
        }
        String localeCookieKey = "locale" + cookieKey;
        
        String localeString = null;
        Cookie[] cookies = data.getRequest().getCookies();
        if(cookies != null)
        {
            for(int i=0; i<cookies.length; i++)
            {
                if(cookies[i].getName().equals(localeCookieKey))
                {
                    localeString = cookies[i].getValue();
                }
            }
        }
        
        Locale locale = (Locale)data.getRequest().getSession().
            getAttribute(LOCALE_SESSION_KEY);
        if(locale != null)
        {
            try
            {
                data.setLocale(locale);
            }
            catch(UnsupportedEncodingException e)
            {
                log.error("invalid encoding configured for locale "+locale, e);
            }
        }
        else
        {
            if(localeString != null)
            {
                try
                {
                    locale = StringUtils.getLocale(localeString);
                    data.getRequest().getSession().setAttribute(LOCALE_SESSION_KEY, locale);
                    data.setLocale(locale);
                }
                catch(IllegalArgumentException e)
                {
                    log.error("malformed "+localeCookieKey+" cookie '"+localeString+
                                 "' received from client "+
                                 data.getRequest().getRemoteAddr());
                    Cookie cookie = new Cookie(localeCookieKey, "");
                    cookie.setMaxAge(0);
                    data.getResponse().addCookie(cookie);
                }
                catch(UnsupportedEncodingException e)
                {
                    log.error("invalid encoding configured for locale "+localeString, e);
                    Cookie cookie = new Cookie(localeCookieKey, "");
                    cookie.setMaxAge(0);
                    data.getResponse().addCookie(cookie);
                }
            }
        }
        if(localeString == null)
        {
            if(locale == null)
            {
                locale = webcoreService.getDefaultLocale();
            }
            try
            {
                data.setLocale(locale);
                Cookie cookie = new Cookie(localeCookieKey, locale.toString());
                cookie.setMaxAge(3600*24*365);
                cookie.setPath(data.getRequest().getContextPath()+
                               data.getRequest().getServletPath());
                data.getResponse().addCookie(cookie);
            }
            catch(UnsupportedEncodingException e)
            {
                log.error("invalid encoding configured for locale "+locale, e);
            }
        }

        try
        {
            data.setEncoding(webcoreService.getDefaultEncoding());
        }
        catch(UnsupportedEncodingException e)
        {
            log.error("invalid default encoding", e);
        }
        */
    }
}
