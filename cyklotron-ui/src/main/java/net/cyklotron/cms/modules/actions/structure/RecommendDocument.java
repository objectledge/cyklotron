package net.cyklotron.cms.modules.actions.structure;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;

import org.jcontainer.dna.Logger;
import net.labeo.services.mail.LabeoMessage;
import net.labeo.services.mail.MailService;
import net.labeo.services.resource.Subject;
import net.labeo.services.templating.Context;
import net.labeo.services.templating.Template;
import net.labeo.services.templating.TemplatingService;
import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.documents.DocumentNodeResource;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.NavigationNodeResourceImpl;

/**
 * Recommend the document
 *
 * @author <a href="mailo:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: RecommendDocument.java,v 1.2 2005-01-24 10:26:59 pablo Exp $
 */

public class RecommendDocument
    extends BaseStructureAction
{
	/** logging facility */
	protected Logger proposalsLog;

	/** mail service */
	protected MailService mailService;

	/** mail service */
	protected TemplatingService templatingService;

	/** tool service */
	//private ToolService toolService;
    
	public RecommendDocument()
	{
		mailService = (MailService)broker.getService(MailService.SERVICE_NAME);
		templatingService = (TemplatingService)broker.getService(TemplatingService.SERVICE_NAME);
	}
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        // basic setup
        Context context = data.getContext();
        Subject subject = coralSession.getUserSubject();
        DocumentNodeResource node = null;
        
        try
        {
        	// get parameters
            String firstName = parameters.get("first_name","");
            String secondName = parameters.get("second_name","");
			String from = parameters.get("from","");
            String to = parameters.get("to","");
            String content = parameters.get("content","");
			
			// check required parameters
			if(firstName.equals("") || secondName.equals("") || from.equals("") || to.equals(""))
			{
				templatingContext.put("result","field_empty");
				parameters.remove("state");
				return;
			}
			
			if(!mailService.isValidEmailAddress(from))
			{
				templatingContext.put("result","illegal_from_param");
				parameters.remove("state");
				return;
			}
			if(!mailService.isValidEmailAddress(to))
			{
				templatingContext.put("result","illegal_to_param");
				parameters.remove("state");
				return;
			}
			
			// find recommend node
            long nodeId = parameters.getLong("parent_node", -1);
            if(nodeId == -1)
            {
                templatingContext.put("result","parent_not_found");
				parameters.remove("state");
                return;
            }
            
            NavigationNodeResource parent = NavigationNodeResourceImpl
                .getNavigationNodeResource(coralSession,nodeId);
            
			LabeoMessage message = mailService.newMessage();
			Context ctx = message.getContext();
			
			//toolService.populate(ctx,data);
			ctx.put("content",content);
			ctx.put("first_name", firstName);
			ctx.put("second_name", secondName);
			ctx.put("document",parent);
			ctx.put("from",from);
			ctx.put("to",to);
			ctx.put("context",context);
			Template template = templatingService.getTemplate("cms",i18nContext.getLocale()().toString()+
					"_PLAIN/messages/documents/recommend_document_subject");
			String title = template.merge(ctx);
			message.getMessage().setSubject(title);
			message.setEncoding(data.getEncoding());
			message.getMessage().setFrom(new InternetAddress(from));
			message.getMessage().setRecipient(Message.RecipientType.TO, new InternetAddress(to));
			message.setTemplate(data, "PLAIN", "documents/recommend_document");
			message.send(true);
        }
        catch(Exception e)
        {
            templatingContext.put("result","exception");
            log.error("StructureException: ",e);
            templatingContext.put("trace", new StackTrace(e));
			parameters.remove("state");
            return;
        }
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
    	return true;
    }
    
	public boolean requiresLogin(RunData data)
			throws ProcessingException
	{
		return false;
	}
    
}
