/*
 * Created on Jun 22, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package net.cyklotron.cms.modules.actions.security;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;

import javax.naming.NamingException;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;

import net.cyklotron.cms.security.SecurityService;
import net.labeo.Labeo;
import net.labeo.services.authentication.UnknownUserException;
import net.labeo.services.authentication.UserAlreadyExistsException;
import org.jcontainer.dna.Logger;
import net.labeo.services.logging.LoggingService;
import net.labeo.services.personaldata.PersonalDataService;
import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Role;
import net.labeo.services.resource.SecurityException;
import net.labeo.services.resource.Subject;
import net.labeo.services.templating.Context;
import net.labeo.services.webcore.NotFoundException;
import net.labeo.util.StringUtils;
import net.labeo.util.configuration.Parameters;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 * @author <a href="rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: AddUser.java,v 1.2 2005-01-24 10:27:46 pablo Exp $
 */
public class AddUser extends BaseSecurityAction
{
    protected PersonalDataService personalDataService;
    protected Logger log;
    protected SecurityService cmsSecurityService;
    
    public AddUser()
    {
        personalDataService = (PersonalDataService)Labeo.getBroker().
        	getService(PersonalDataService.SERVICE_NAME);
        cmsSecurityService = (SecurityService)Labeo.getBroker().getService(SecurityService.SERVICE_NAME);
        log = ((LoggingService)Labeo.getBroker().getService(LoggingService.SERVICE_NAME)).
        	getFacility(SecurityService.LOGGING_FACILITY);
    }
    
    /**
     * {@inheritdoc}
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession) throws ProcessingException, NotFoundException
    {
        Context context = data.getContext();
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
            authenticationService.getUserByLogin(uid);
            templatingContext.put("result","user_already_exists");
            return;
        }
        catch(UnknownUserException e)
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

        String dn = personalDataService.getDN(param);

        try
        {
            Subject admin = coralSession.getUserSubject();
            if(!data.isUserAuthenticated())
            {
                admin = coralSession.getSecurity().getSubject(Subject.ROOT);
            }
            addUser(uid, dn, password, admin);
        }
        catch(ProcessingException e)
        {
            log.error("User adding exception stage 1: ",e);
            templatingContext.put("result","exception");
            templatingContext.put("trace", new StackTrace(e));
            return;
        }
        catch(UserAlreadyExistsException e)
        {
            log.error("User adding exception: ",e);
            templatingContext.put("result","user_already_exists");
            return;
        }
        catch(Exception e)
        {
            log.error("User \nlogin : "+uid+"\ndn : "+dn+"\nadding exception stage 1: ",e);
            templatingContext.put("result","exception");
            templatingContext.put("trace", new StackTrace(e));
            return;
        }
        try
        {
            DirContext ctx = personalDataService.getContext(dn);
            List temp = buildModificationItems(param, false, null);
            temp.add(new ModificationItem(DirContext.ADD_ATTRIBUTE,
                                          new BasicAttribute("objectClass", "cyklotronPerson")));
            ModificationItem[] items = new ModificationItem[temp.size()];
            temp.toArray(items);
            ctx.modifyAttributes("", items);
			personalDataService.reloadContainer(dn);
        }
        catch(NamingException e)
        {
            log.error("User adding exception stage 2: ",e);
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            return;
        }
        
        
        templatingContext.put("result","user_added_successfully");        
    }

    protected String integrityCheck(Parameters param)
    {
        String givenName = param.get("givenName","");
        String sn = param.get("sn","");

        String c = param.get("c","");
        String l = param.get("l","");
        String postalAddress1 = param.get("postalAddress1","");
        String postalCode = param.get("postalCode","");
        int birthDay = param.get("birthDay").asInt(-1);
        int birthMonth = param.get("birthMonth").asInt(-1);
        int birthYear = param.get("birthYear").asInt(-1);

        if(!isDateValid(birthDay, birthMonth, birthYear))
        {
            return "invalid_date";
        }

        if(givenName.equals("") || sn.equals("") || postalAddress1.equals("") || 
           postalCode.equals("") || l.equals("") || c.equals(""))
        {
            return "required_field_missing";
        }

        return null;
    }

    protected boolean isDateValid(int day, int month, int year)
    {
        if(day + month + year == -3 && day * month * year == -1)
        {
            // unset date is ok
            return true;
        }
        if(day < 1 || day > 31 || month < 1 || month > 12 || year < 1)
        {
            return false;
        }
        if(year < 1000 || year > 3000)
        {
            return false;
        }
        Calendar time = Calendar.getInstance();
        time.set(Calendar.YEAR, year);
        time.set(Calendar.MONTH,month-1);
        time.set(Calendar.DAY_OF_MONTH,day);
        time.set(Calendar.SECOND,0);
        time.set(Calendar.MILLISECOND,0);
        time.set(Calendar.HOUR_OF_DAY,0);
        time.set(Calendar.MINUTE,0);
        // checks whether month was incremented due to extra days in month
        if(time.get(Calendar.MONTH)!=(month-1))
        {
            return false;
        }
        return true;
    }

    protected List buildModificationItems(Parameters param, boolean update, String localMail)
    {
        int mode = update ? DirContext.REPLACE_ATTRIBUTE : DirContext.ADD_ATTRIBUTE;

        String givenName = param.get("givenName","");
        String sn = param.get("sn","");

        String c = param.get("c","");
        String l = param.get("l","");
        String postalAddress1 = param.get("postalAddress1","");
        String postalCode = param.get("postalCode","");

        String sex = param.get("sex","");
        int birthDay = param.get("birthDay").asInt(-1);
        int birthMonth = param.get("birthMonth").asInt(-1);
        int birthYear = param.get("birthYear").asInt(-1);
        String profession = param.get("profession","");
        String businessCategory = param.get("businessCategory","");
        String educationLevel = param.get("educationLevel","");
        String hobby = param.get("hobby","");

        String telephoneNumber = param.get("telephoneNumber","");
        String altMail = param.get("altMail","");
        String mail = param.get("mail","");
        String homePage = param.get("homePage","");

        // composite fields
        String cn = givenName + " " + sn;
        String postalAddress = postalAddress1 + " $ " + postalCode + " " + l + " $ " + c;
        String birthDate = getLDAPDate(birthDay, birthMonth, birthYear);

        List temp = new ArrayList();
        // required fields
        temp.add(new ModificationItem(mode, new BasicAttribute("givenName", givenName)));
        temp.add(new ModificationItem(mode, new BasicAttribute("sn", sn)));
        temp.add(new ModificationItem(mode, new BasicAttribute("cn", cn)));
        temp.add(new ModificationItem(mode, new BasicAttribute("postalAddress", postalAddress)));
        temp.add(new ModificationItem(mode, new BasicAttribute("postalCode", postalCode)));
        temp.add(new ModificationItem(mode, new BasicAttribute("l", l)));
        temp.add(new ModificationItem(mode, new BasicAttribute("c", c)));
        // optional fields
        if(!sex.equals(""))
        {
            temp.add(new ModificationItem(mode, new BasicAttribute("sex", sex)));
        }
        else
        {
            if(update)
            {
                temp.add(new ModificationItem(mode, new BasicAttribute("sex")));
            }
        }
        if(birthDate != null && !birthDate.equals(""))
        {
            temp.add(new ModificationItem(mode, new BasicAttribute("birthDate", birthDate)));
        }
        else
        {
            if(update)
            {
                temp.add(new ModificationItem(mode, new BasicAttribute("birthDate")));
            }
        }
        if(!profession.equals(""))
        {
            temp.add(new ModificationItem(mode, new BasicAttribute("profession", profession)));
        }
        else
        {
            if(update)
            {
                temp.add(new ModificationItem(mode, new BasicAttribute("profession")));
            }
        }
        if(!businessCategory.equals(""))
        {
            temp.add(new ModificationItem(mode, new BasicAttribute("businessCategory",
                businessCategory)));
        }
        else
        {
            if(update)
            {
                temp.add(new ModificationItem(mode, new BasicAttribute("businessCategory")));
            }
        }
        if(!educationLevel.equals(""))
        {
            temp.add(new ModificationItem(mode,
                new BasicAttribute("educationLevel", educationLevel)));
        }
        else
        {
            if(update)
            {
                temp.add(new ModificationItem(mode, new BasicAttribute("educationLevel")));
            }
        }
        if(!hobby.equals(""))
        {
            temp.add(new ModificationItem(mode, new BasicAttribute("hobby")));
        }
        else
        {
            if(update)
            {
                temp.add(new ModificationItem(mode, new BasicAttribute("hobby")));
            }
        }
        if(!telephoneNumber.equals(""))
        {
            temp.add(new ModificationItem(mode, new BasicAttribute("telephoneNumber",
                telephoneNumber)));
        }
        else
        {
            if(update)
            {
                temp.add(new ModificationItem(mode, new BasicAttribute("telephoneNumber")));
            }
        }
        if(!homePage.equals(""))
        {
            temp.add(new ModificationItem(mode, new BasicAttribute("homePage", homePage)));
        }
        else
        {
            if(update)
            {
                temp.add(new ModificationItem(mode, new BasicAttribute("homePage")));
            }
        }
        // mail addresses
        List altMailList = new ArrayList();
        if(!altMail.equals(""))
        {
            BasicAttribute attr = new BasicAttribute("altMail");
            StringTokenizer st = new StringTokenizer(altMail, ";, ");
            while(st.hasMoreTokens())
            {
                String t = st.nextToken();
                attr.add(t);
                altMailList.add(t);
            }
            temp.add(new ModificationItem(mode, attr));
        }
        else
        {
            if(update)
            {
                temp.add(new ModificationItem(mode, new BasicAttribute("altMail")));
            }
        }
        if(!mail.equals(""))
        {
            BasicAttribute mailAttr;
            if(localMail == null && altMailList.size() == 0)
            {
                mailAttr = new BasicAttribute("mail");
            }
            else
            {
                if(!mail.equals(localMail) && !altMailList.contains(mail))
                {
                    mail = (String)altMailList.get(0);
                }
                mailAttr = new BasicAttribute("mail", mail);
            }
            temp.add(new ModificationItem(mode, mailAttr));
        }
        else
        {
            if(altMailList.size() > 0)
            {
                temp
                    .add(new ModificationItem(mode, new BasicAttribute("mail", altMailList.get(0))));
            }
            else
            {
                if(update)
                {
                    temp.add(new ModificationItem(mode, new BasicAttribute("mail")));
                }
            }
        }
        return temp;
    }

    protected String getLDAPDate(int day, int month, int year)
    {
        if(day + month + year == -3 && day * month * year == -1)
        {
            return null;
        }
        StringBuffer buff = new StringBuffer();
        if(year < 10)
        {
            buff.append('0');
        }
        if(year < 100)
        {
            buff.append('0');
        }
        if(year < 1000)
        {
            buff.append('0');
        }
        buff.append(year);
        if(month < 10)
        {
            buff.append('0');
        }
        buff.append(month);
        if(day < 10)
        {
            buff.append('0');
        } 
        buff.append(day);
        buff.append("000000Z");
        return buff.toString();
    }

    protected boolean isNameValid(String uid)
    {
        for(int i=0; i<uid.length(); i++)
        {
            char c = uid.charAt(i);
            if(!((c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || (c == '_')))
            {
                return false;
            }
        }
        return true;
    }
    
    protected void addUser(String login, String name, String password, Subject admin) 
	    throws ProcessingException, UserAlreadyExistsException
	{
	    authenticationService.createUser(login, name, password);
	    Subject subject = null;
	    try
	    {
	        subject = coralSession.getSecurity().getSubject(name);
	        Role role = coralSession.getSecurity().getUniqueRole("cms.registered");
	        coralSession.getSecurity().grant(role, subject, false, admin);
	    }
	    catch (EntityDoesNotExistException e)
	    {
	        throw new ProcessingException("User was not found in ARL",e);
	    }
	    catch (SecurityException e)
	    {
	        throw new ProcessingException("Granting cms.registerd role failed",e);
	    }

	}
    
    public boolean checkAccessRights(Context context)
	    throws ProcessingException
	{
        if(cmsSecurityService.getAllowAddUser())
        {
            return true;
        }
        Role role = coralSession.getSecurity().getUniqueRole("cms.administrator");
        return coralSession.getUserSubject().hasRole(role);
	}
}
