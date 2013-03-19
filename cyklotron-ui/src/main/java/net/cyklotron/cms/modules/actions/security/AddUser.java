/*
 * Created on Jun 22, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package net.cyklotron.cms.modules.actions.security;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Pattern;

import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;

import org.jcontainer.dna.Logger;
import org.objectledge.authentication.UserAlreadyExistsException;
import org.objectledge.authentication.UserManager;
import org.objectledge.context.Context;
import org.objectledge.coral.security.Role;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.security.SecurityService;
import net.cyklotron.cms.structure.StructureService;

/**
 * @author <a href="rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: AddUser.java,v 1.8 2007-11-18 21:25:11 rafal Exp $
 */
public class AddUser extends BaseSecurityAction
{
    public AddUser(Logger logger, StructureService structureService, CmsDataFactory cmsDataFactory,
        SecurityService cmsSecurityService, UserManager userManager)
    {
        super(logger, structureService, cmsDataFactory, cmsSecurityService, userManager);
    }

    /**
     * {@inheritdoc}
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession) throws ProcessingException
    {
        Parameters param = parameters;

        String uid = param.get("uid","");
        String password = param.get("password","");
        String password2 = param.get("password2","");

        if(uid.equals("") || password.equals("") || password2.equals(""))
        {
            templatingContext.put("result","required_field_missing");
            return;
        }

        if(!isNameValid(uid))
        {
            templatingContext.put("result","invalid_user_login");
            return;
        }

        try
        {
            userManager.getUserByLogin(uid);
            templatingContext.put("result","user_already_exists");
            return;
        }
        catch(Exception e)
        {
            // OK
        }

        if(!password.equals(password2))
        {
            templatingContext.put("result","passwords_do_not_match");
            return;
        }

        String res = integrityCheck(param);
        if(res != null)
        {
            templatingContext.put("result", res);
            return;
        }

        String dn = userManager.createDN(param);

        try
        {
            Attributes attrs = buildAttributes(param);
            userManager.createAccount(uid, password, false, attrs);
        }
        catch(UserAlreadyExistsException e)
        {
            logger.error("User adding exception: ",e);
            templatingContext.put("result","user_already_exists");
            return;
        }
        catch(Exception e)
        {
            logger.error("User \nlogin : "+uid+"\ndn : "+dn+"\nadding exception stage 1: ",e);
            templatingContext.put("result","exception");
            templatingContext.put("trace", new StackTrace(e));
            return;
        }
       
        templatingContext.put("result","user_added_successfully");        
    }

    protected String integrityCheck(Parameters param)
    {
        String givenName = param.get("givenName","");
        String sn = param.get("sn","");

        String postalAddress1 = param.get("postalAddress1", "");
        String postalCode = param.get("postalCode", "");
        String l = param.get("l", "");
        String c = param.get("c", "");

        if(givenName.equals("") || sn.equals("") || !postalAddress1.equals("")
            && (postalCode.equals("") || l.equals("") || c.equals("")))
        {
            return "required_field_missing";
        }

        return null;
    }

    protected Attributes buildAttributes(Parameters param)
    {
        Attributes attrs = new BasicAttributes();
        String givenName = param.get("givenName","");
        String sn = param.get("sn","");

        String c = param.get("c","");
        String l = param.get("l","");
        String postalAddress1 = param.get("postalAddress1","");
        String postalCode = param.get("postalCode","");

        String telephoneNumber = param.get("telephoneNumber","");
        String mail = param.get("mail","");
        String homePage = param.get("homePage","");

        // composite fields
        String cn = givenName + " " + sn;
        String postalAddress = "";
        if(postalAddress1.length() > 0)
        {
            postalAddress = postalAddress1 + " $ " + postalCode + " " + l + " $ " + c;
        }

        if(homePage.length() > 0)
        {
            try
            {
                homePage = URLEncoder.encode(homePage, "UTF-8") + " Home page";
            }
            catch(UnsupportedEncodingException e)
            {
                throw new RuntimeException("UTF-8 not supported");
            }
        }

        // fields required by inetOrgPerson
        attrs.put("sn", sn);
        attrs.put("cn", cn);

        // optional fields
        maybePut("givenName", givenName, attrs);
        maybePut("postalAddress", postalAddress, attrs);
        maybePut("postalCode", postalCode, attrs);
        maybePut("l", l, attrs);

        maybePut("telephoneNumber", telephoneNumber, attrs);
        maybePut("labeledURI", homePage, attrs);

        if(!mail.equals(""))
        {
            BasicAttribute mailAttr = new BasicAttribute("mail");
            for(String addr : mail.split(";"))
            {
                mailAttr.add(addr.trim());
            }
            attrs.put("mail", mailAttr);
        }
        return attrs;
    }

    private void maybePut(String name, String value, Attributes attrs)
    {
        if(value.length() > 0)
        {
            attrs.put(name, value);
        }
    }

    private final Pattern UID_PATTERN = Pattern.compile("[a-z0-9-_.@]+");
    
    protected boolean isNameValid(String uid)
    {        
        return UID_PATTERN.matcher(uid).matches();
    }
    
    public boolean checkAccessRights(Context context)
	    throws ProcessingException
	{
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        if(cmsSecurityService.getAllowAddUser())
        {
            return true;
        }
        Role role = coralSession.getSecurity().getUniqueRole("cms.administrator");
        return coralSession.getUserSubject().hasRole(role);
	}
}
