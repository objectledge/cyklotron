package net.cyklotron.cms.modules.actions.structure;

import java.util.Date;

import pl.caltha.encodings.HTMLEntityEncoder;

import org.jcontainer.dna.Logger;
import net.labeo.services.logging.LoggingService;
import net.labeo.services.resource.Permission;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.Subject;
import net.labeo.services.templating.Context;
import net.labeo.util.StringUtils;
import net.labeo.util.configuration.Parameter;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.documents.DocumentNodeResource;
import net.cyklotron.cms.structure.NavigationNodeAlreadyExistException;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.NavigationNodeResourceImpl;

/**
 * Propose new navigation node in document tree.
 *
 * @author <a href="mailo:pablo@caltha.pl">Pawel Potempski</a>
 * @author <a href="mailo:mover@caltha.pl">Michal Mach</a>
 * @version $Id: ProposeDocument.java,v 1.1 2005-01-24 04:33:55 pablo Exp $
 */

public class ProposeDocument
    extends BaseAddEditNodeAction
{
	/** logging facility */
	protected Logger proposalsLog;

	public ProposeDocument()
	{
		proposalsLog = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME)).
			getFacility("propose_document");
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
        StringBuffer proposalsDump = new StringBuffer();
        HTMLEntityEncoder encoder = new HTMLEntityEncoder();
        
        try
        {
        	boolean calendarTree = parameters.getBoolean("calendar_tree", false);
            // get parameters
            String name = parameters.get("name","");
            String title = parameters.get("title","");
			String doc_abstract = parameters.get("abstract","");
            String content = parameters.get("content","");
            String event_place = parameters.get("event_place","");
			String organized_by = parameters.get("organized_by","");
			String organized_address = parameters.get("organized_address","");
			String organized_phone = parameters.get("organized_phone","");
			String organized_fax = parameters.get("organized_fax","");
			String organized_email = parameters.get("organized_email","");
			String organized_www = parameters.get("organized_www","");
			String source = parameters.get("source","");
			String proposer_credentials = parameters.get("proposer_credentials","");
			String proposer_email = parameters.get("proposer_email","");
			String description = parameters.get("description","");

			// check required parameters
			if(name.equals(""))
			{
				templatingContext.put("result","navi_name_empty");
				return;
			}
			if(title.equals(""))
			{
				templatingContext.put("result","navi_title_empty");
				return;
			}
			if(proposer_credentials.equals(""))
			{
				templatingContext.put("result", "proposer_credentials_empty");
				return;
			}
            /**
			if(proposer_email.equals(""))
			{
				templatingContext.put("result", "proposer_email_empty");
				return;
			}	
            */		

			// assemble meta attribute from captured parameters
			StringBuffer buf;

			buf = new StringBuffer(512);
			buf.setLength(0);
    		
			buf.append("<meta><authors><author><name>");
			buf.append(encoder.encodeAttribute(proposer_credentials,"UTF-8"));
			buf.append("</name><e-mail>");
			buf.append(encoder.encodeAttribute(proposer_email,"UTF-8"));
			buf.append("</e-mail></author></authors>");
			buf.append("<sources><source><name>");
			buf.append(encoder.encodeAttribute(source,"UTF-8"));
			buf.append("</name><url>http://</url></source></sources>");
			buf.append("<editor></editor><organisation><name>");
			buf.append(encoder.encodeAttribute(organized_by,"UTF-8"));
			buf.append("</name><address>");
			buf.append(encoder.encodeAttribute(organized_address,"UTF-8"));
			buf.append("</address><tel>");
			buf.append(encoder.encodeAttribute(organized_phone,"UTF-8"));
			buf.append("</tel><fax>");
			buf.append(encoder.encodeAttribute(organized_fax,"UTF-8"));
			buf.append("</fax><e-mail>");
			buf.append(encoder.encodeAttribute(organized_email,"UTF-8"));
			buf.append("</e-mail><url>");
			buf.append(encoder.encodeAttribute(organized_www,"UTF-8"));
			buf.append("</url><id>0</id></organisation></meta>");

			String meta = buf.toString();
            
			// find parent node
            long parentId = parameters.getLong("parent", -1);
            if(parentId == -1)
            {
                templatingContext.put("result","parent_not_found");
                return;
            }
            
            NavigationNodeResource parent = NavigationNodeResourceImpl
                .getNavigationNodeResource(coralSession,parentId);
                
			if(calendarTree && parameters.get("validity_start").length() > 0)
            {
            	parent = structureService.getParent(parent, new Date(parameters.get("validity_start").asLong()), subject);
            }
            
            // get greatest sequence number to put new node on top of
            // sequence-sorted list 
            int sequence = 0;
            Resource[] children = coralSession.getStore().getResource(parent);
            for(int i=0; i<children.length; i++)
            {
                Resource child = children[i];
                if(child instanceof NavigationNodeResource)
                {
                    int childSeq = ((NavigationNodeResource)child).getSequence(0);
                    sequence = sequence<childSeq ? childSeq : sequence;
                }
            }
            
            // add navigation node
            node = structureService.addDocumentNode(name, title, parent, subject);
            node.setDescription(description);
            node.setSequence(sequence);
            
			// handle dates
			Date event_start = null;
			Date event_end = null;

			if(parameters.get("event_start").length() > 0)
			{
				event_start = new Date(parameters.get("event_start").asLong());
				node.setEventStart(event_start);
			}
			else
			{
				node.setEventStart(null);
			}
        
			if(parameters.get("event_end").length() > 0)
			{
				event_end = new Date(parameters.get("event_end").asLong());
				node.setEventEnd(event_end);
			}        
			else
			{
				node.setEventEnd(null);
			}

			// set attributes to new node
            content = content.replaceAll("\n", "<br>");
            node.setContent(content);
            setValidity(data, node);
            node.setAbstract(doc_abstract);
            node.setEventPlace(event_place);
			node.setMeta(meta);

			// set the state to taken if user is redactor (logged in) 
            Permission permission = coralSession.getSecurity().getUniquePermission("cms.structure.modify_own");
            if(subject.hasPermission(node,permission))
            {
                structureService.enterState(node, "taken", subject);
            }
            else
            {
                structureService.enterState(node, "new", subject);
            }
			
			// update the node
            structureService.updateNode(node, name, subject);

			// build proposals log            
            proposalsDump.append("----------------------------------\n");
			proposalsDump.append("-----------------------------------\n");
			proposalsDump.append("Document id: " + node.getIdString() + "\n");
			proposalsDump.append("Document path: " + node.getPath() + "\n");
			proposalsDump.append("Created: " + node.getCreationTime() + "\n");
			proposalsDump.append("Created by: " + node.getCreatedBy().getName() + "\n");
			proposalsDump.append("Document title: " + title + "\n");
			if(event_start == null)
			{
				proposalsDump.append("Event start: Undefined \n");
			}
			else
			{
				proposalsDump.append("Event start: " + event_start.toString() + "\n");
			}
			if(event_end == null)
			{
				proposalsDump.append("Event end: Undefined \n");
			}
			else
			{
				proposalsDump.append("Event end: " + event_end.toString() + "\n");
			}
			if(parameters.get("validity_start").length() > 0)
			{
				proposalsDump.append("Document validity start: " + new Date(parameters.get("validity_start").asLong()).toString() + "\n");
			}
			else
			{
				proposalsDump.append("Document validity start: Undefined \n");
			}
			if(parameters.get("validity_end").length() > 0)
			{
				proposalsDump.append("Document validity end: " + new Date(parameters.get("validity_end").asLong()).toString() + "\n");
			}
			else
			{
				proposalsDump.append("Document validity end: Undefined \n");
			}
			proposalsDump.append("Organized by: " + organized_by + "\n");
			proposalsDump.append("Organizer address: " + organized_address + "\n");
			proposalsDump.append("Organizer phone: " + organized_phone + "\n");
			proposalsDump.append("Organizer fax: " + organized_fax + "\n");
			proposalsDump.append("Organizer email: " + organized_email + "\n");
			proposalsDump.append("Organizer URL: " + organized_www + "\n");
			proposalsDump.append("Source: " + source + "\n");
			proposalsDump.append("Proposer credentials: " + proposer_credentials + "\n");
			proposalsDump.append("Proposer email: " + proposer_email + "\n");
			proposalsDump.append("Administrative description: " + proposer_email + "\n");
			proposalsDump.append("Content: \n" + content + "\n");			
        }
        catch(NavigationNodeAlreadyExistException e)
        {
            templatingContext.put("result","navi_name_repeated");
            return;
            
        }
        catch(Exception e)
        {
            templatingContext.put("result","exception");
            log.error("StructureException: ",e);
            templatingContext.put("trace", StringUtils.stackTrace(e));
            return;
        }
        // make the newly created node a current node
        parameters.set("state", "Result");
        templatingContext.put("result","added_successfully");
        proposalsLog.info(proposalsDump.toString());
    }

    protected String getViewName()
    {
        return "";
    }
    
    public boolean checkAccess(RunData data)
        throws ProcessingException
    {
    	try
    	{
        	Permission permission = coralSession.getSecurity().getUniquePermission("cms.structure.submit");
        	Resource node = coralSession.getStore().getResource(parameters.get("parent_node_id").asLong(-1));
        	return coralSession.getUserSubject().hasPermission(node, permission);
    	}
    	catch(Exception e)
    	{
    		throw new ProcessingException("Exception occured during access rights checking ", e);
    	}
    }
    
    public boolean requiresLogin(RunData data)
    {
    	return false;
    }
}
