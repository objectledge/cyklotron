package net.cyklotron.cms.modules.actions.structure;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.relation.Relation;
import org.objectledge.coral.relation.RelationModification;
import org.objectledge.coral.security.Permission;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.encodings.HTMLEntityEncoder;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.category.CategoryResource;
import net.cyklotron.cms.category.CategoryResourceImpl;
import net.cyklotron.cms.category.CategoryService;
import net.cyklotron.cms.documents.DocumentNodeResource;
import net.cyklotron.cms.structure.NavigationNodeAlreadyExistException;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.NavigationNodeResourceImpl;
import net.cyklotron.cms.structure.StructureException;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;

/**
 * Propose new navigation node in document tree.
 *
 * @author <a href="mailo:pablo@caltha.pl">Pawel Potempski</a>
 * @author <a href="mailo:mover@caltha.pl">Michal Mach</a>
 * @version $Id: ProposeDocument.java,v 1.16 2008-10-28 17:24:23 rafal Exp $
 */

public class ProposeDocument
    extends BaseAddEditNodeAction
{
    private static final HTMLEntityEncoder ENCODER = new HTMLEntityEncoder();
    private CategoryService categoryService;
    
    public ProposeDocument(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, StyleService styleService,
        CategoryService categoryService)
    {
        super(logger, structureService, cmsDataFactory, styleService);
        this.categoryService = categoryService;
    }
    
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        // basic setup
        
        Subject subject = coralSession.getUserSubject();
        DocumentNodeResource node = null;
        
        try
        {
            // get parameters
        	boolean calendarTree = parameters.getBoolean("calendar_tree", false);
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

			// find parent node
			long[] parentsId = parameters.getLongs("parent");
			if(parentsId.length == 0)
			{
			    templatingContext.put("result","parent_not_found");
			    return;
			}
			long parentId = parentsId[0];
			
			NavigationNodeResource parent = NavigationNodeResourceImpl
			.getNavigationNodeResource(coralSession,parentId);
			
			if(calendarTree && parameters.get("validity_start").length() > 0)
			{
			    parent = structureService.getParent(coralSession, parent, 
			        new Date(parameters.getLong("validity_start")), 
			        StructureService.DAILY_CALENDAR_TREE_STRUCTURE, subject);
			}
            
            // add navigation node
            node = structureService.addDocumentNode(coralSession, enc(name), enc(title), parent, subject);
            // set attributes to new node
            node.setDescription(enc(description));
            int sequence = getMaxSequence(coralSession, parent);
            node.setSequence(sequence);            
            content = setContent(node, content);     
            node.setAbstract(enc(doc_abstract));
            setValidity(parameters, node);
            setEventDates(parameters, node);
            node.setEventPlace(enc(event_place));
            String meta = buildMeta(organized_by, organized_address, organized_phone,
                organized_fax, organized_email, organized_www, source, proposer_credentials,
                proposer_email);            
			node.setMeta(meta);
			setState(coralSession, subject, node);			
			// update the node
            structureService.updateNode(coralSession, node, enc(name), true, subject);
            assignCategories(parameters, coralSession, subject, node, parentId);            
            
			logProposal(parameters, node, title, content, organized_by, organized_address,
                organized_phone, organized_fax, organized_email, organized_www, source,
                proposer_credentials, proposer_email);
        }
        catch(NavigationNodeAlreadyExistException e)
        {
            templatingContext.put("result","navi_name_repeated");
            return;
            
        }
        catch(Exception e)
        {
            templatingContext.put("result","exception");
            logger.error("StructureException: ",e);
            templatingContext.put("trace", new StackTrace(e));
            return;
        }
        // make the newly created node a current node
        parameters.set("state", "Result");
        templatingContext.put("result","added_successfully");
    }

    private void setEventDates(Parameters parameters, DocumentNodeResource node)
    {
        // handle dates

        if(parameters.get("event_start").length() > 0)
        {
            Date event_start = null;
        	event_start = new Date(parameters.getLong("event_start"));
        	node.setEventStart(event_start);
        }
        else
        {
        	node.setEventStart(null);
        }
      
        if(parameters.get("event_end").length() > 0)
        {
            Date event_end = null;
        	event_end = new Date(parameters.getLong("event_end"));
        	node.setEventEnd(event_end);
        }        
        else
        {
        	node.setEventEnd(null);
        }
    }

    private void setState(CoralSession coralSession, Subject subject, DocumentNodeResource node)
        throws StructureException
    {
        // set the state to taken if user is redactor (logged in) 
        Permission permission = coralSession.getSecurity().getUniquePermission("cms.structure.modify_own");
        if(subject.hasPermission(node,permission))
        {
            structureService.enterState(coralSession, node, "taken", subject);
        }
        else
        {
            structureService.enterState(coralSession, node, "new", subject);
        }
    }

    private void logProposal(Parameters parameters, DocumentNodeResource node, String title,
        String content, String organized_by, String organized_address, String organized_phone,
        String organized_fax, String organized_email, String organized_www, String source,
        String proposer_credentials, String proposer_email)
    {
        // build proposals log   
        StringBuilder proposalsDump = new StringBuilder();
        proposalsDump.append("----------------------------------\n");
        proposalsDump.append("-----------------------------------\n");
        proposalsDump.append("Document id: " + node.getIdString() + "\n");
        proposalsDump.append("Document path: " + node.getPath() + "\n");
        proposalsDump.append("Created: " + node.getCreationTime() + "\n");
        proposalsDump.append("Created by: " + node.getCreatedBy().getName() + "\n");
        proposalsDump.append("Document title: " + title + "\n");
        if(parameters.get("event_start").length() > 0)
        {
        	proposalsDump.append("Event start: " + new Date(parameters.getLong("event_start")).toString() + "\n");
        }
        else
        {
        	proposalsDump.append("Event start: Undefined \n");
        }
        if(parameters.get("event_end").length() > 0)
        {
        	proposalsDump.append("Event end: " + new Date(parameters.getLong("event_end")).toString() + "\n");
        }
        else
        {
        	proposalsDump.append("Event end: Undefined \n");
        }
        if(parameters.get("validity_start").length() > 0)
        {
        	proposalsDump.append("Document validity start: " + new Date(parameters.getLong("validity_start")).toString() + "\n");
        }
        else
        {
        	proposalsDump.append("Document validity start: Undefined \n");
        }
        if(parameters.get("validity_end").length() > 0)
        {
        	proposalsDump.append("Document validity end: " + new Date(parameters.getLong("validity_end")).toString() + "\n");
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
        logger.debug(proposalsDump.toString());
    }

    private void assignCategories(Parameters parameters, CoralSession coralSession, Subject subject,
        DocumentNodeResource node, long parentId)
        throws EntityDoesNotExistException
    {
        long[] catIds = parameters.getLongs("category_id");
        List<Long> catIdsList = new ArrayList<Long>();
        for(long id: catIds)
        {
            if(id != -1)
            {
                catIdsList.add(id);
            }
        }
        boolean inheritCategories = parameters.getBoolean("inherit_categories",false);

        NavigationNodeResource parent;
        if(inheritCategories || catIdsList.size() > 0)
        {
            Relation refs = categoryService.getResourcesRelation(coralSession);
            RelationModification diff = new RelationModification();
            Permission classifyPermission = coralSession.getSecurity().getUniquePermission("cms.category.classify");
            if(inheritCategories)
            {
                parent = NavigationNodeResourceImpl
                    .getNavigationNodeResource(coralSession,parentId);
                CategoryResource[] categories = categoryService.getCategories(coralSession, parent, false);
                for(int i = 0; i< categories.length; i++)
                {
                    if(subject.hasPermission(categories[i], classifyPermission))
                    {
                        diff.add(categories[i], node);
                    }
                }
            }
            for(long id: catIdsList)
            {
                CategoryResource categoryResource = CategoryResourceImpl.
                    getCategoryResource(coralSession, id);
                if(subject.hasPermission(categoryResource, classifyPermission))
                {
                    diff.add(categoryResource, node);
                }
            }
            coralSession.getRelationManager().updateRelation(refs, diff);
        }
    }

    private String setContent(DocumentNodeResource node, String content)
    {
        content = content.replaceAll("\r\n", "\n");
        content = content.replaceAll("\n", "</p>\n<p>");
        content = "<p>" + content + "</p>";
        content = content.replaceAll("<p>\\s*</p>", "");
        node.setContent(content);
        return content;
    }

    private int getMaxSequence(CoralSession coralSession, NavigationNodeResource parent)
    {
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
                sequence = sequence < childSeq ? childSeq : sequence;
            }
        }
        return sequence;
    }

    private String buildMeta(String organized_by, String organized_address, String organized_phone,
        String organized_fax, String organized_email, String organized_www, String source,
        String proposer_credentials, String proposer_email)
    {
        // assemble meta attribute from captured parameters
        StringBuilder buf = new StringBuilder();
        buf.append("<meta><authors><author><name>");
        buf.append(enc(proposer_credentials));
        buf.append("</name><e-mail>");
        buf.append(enc(proposer_email));
        buf.append("</e-mail></author></authors>");
        buf.append("<sources><source><name>");
        buf.append(enc(source));
        buf.append("</name><url>http://</url></source></sources>");
        buf.append("<editor></editor><organisation><name>");
        buf.append(enc(organized_by));
        buf.append("</name><address>");
        buf.append(enc(organized_address));
        buf.append("</address><tel>");
        buf.append(enc(organized_phone));
        buf.append("</tel><fax>");
        buf.append(enc(organized_fax));
        buf.append("</fax><e-mail>");
        buf.append(enc(organized_email));
        buf.append("</e-mail><url>");
        buf.append(enc(organized_www));
        buf.append("</url><id>0</id></organisation></meta>");
        return buf.toString();
    }

    protected String getViewName()
    {
        return "";
    }
    
    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        Parameters parameters = RequestParameters.getRequestParameters(context);
    	try
    	{
        	Permission permission = coralSession.getSecurity().getUniquePermission("cms.structure.submit");
        	Resource node = coralSession.getStore().getResource(parameters.getLong("parent_node_id",-1));
        	return coralSession.getUserSubject().hasPermission(node, permission);
    	}
    	catch(Exception e)
    	{
    		throw new ProcessingException("Exception occured during access rights checking ", e);
    	}
    }

    /**
     * @{inheritDoc}
     */
    public boolean requiresAuthenticatedUser(Context context)
        throws Exception
    {
        return false;
    }
    
    private String enc(String s)
    {
        s = s.replaceAll("<[^>]*?>", " "); // strip html tags
        return ENCODER.encodeAttribute(s, "UTF-16");
    }
}
