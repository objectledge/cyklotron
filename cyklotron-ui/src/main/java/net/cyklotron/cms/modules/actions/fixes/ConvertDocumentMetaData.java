package net.cyklotron.cms.modules.actions.fixes;

import java.util.Iterator;
import java.util.List;

import net.cyklotron.cms.documents.DocumentNodeResource;
import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.StructureService;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.Subject;
import net.labeo.services.templating.Context;
import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.XPath;

/**
 * Converts documents metadata attributes to fit new document schema.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: ConvertDocumentMetaData.java,v 1.1 2005-01-24 04:34:29 pablo Exp $
 */
public class ConvertDocumentMetaData
extends BaseCMSAction
{
	/** site service */
	private SiteService siteService;

	/** structure service */
	private StructureService structureService;

	private XPath isbnXPath;
	private XPath authorsXPath;

    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
    	isbnXPath = DocumentHelper.createXPath("/meta/isbn");
		authorsXPath = DocumentHelper.createXPath("/meta/authors/author");
    	
		Context context = data.getContext();
		Subject subject = coralSession.getUserSubject();
		try
		{
			siteService = (SiteService)broker.getService(SiteService.SERVICE_NAME);
			structureService = (StructureService)broker.getService(StructureService.SERVICE_NAME);
			
			SiteResource[] sites = siteService.getSites();
			for(int i = 0; i < sites.length; i++)
			{
				NavigationNodeResource root = structureService.getRootNode(sites[i]);
				fixNodes(root, subject);
			}
		}
		catch(Exception e)
		{
			templatingContext.put("result", "exception");
			templatingContext.put("trace", StringUtils.stackTrace(e));
		}
    }

    public void fixNodes(NavigationNodeResource node, Subject subject)
        throws ProcessingException
    {
    	if(node instanceof DocumentNodeResource)
    	{
    		DocumentNodeResource doc = (DocumentNodeResource)node;
    		String meta = doc.getMeta();
    		if(meta != null)
    		{
				// parse a document fragment
				Document fragment = null;
				try
				{
					fragment = DocumentHelper.parseText(meta);
	//				old doc
	//				<meta>
	//				<authors><author></author></authors>
	//				<isbn></isbn>
	//				</meta>
	
					Node isbn = isbnXPath.selectSingleNode(fragment);
					if(isbn != null) // old doc - convert it
					{
						List authorNodes = authorsXPath.selectNodes(fragment);
						String newMeta = makeMetaAttribute(authorNodes);
						doc.setMeta(newMeta);
						
						doc.update(subject);
					}
				}
				catch(org.dom4j.DocumentException e)
				{
					// discard bad attribute
					doc.setMeta(null);
					doc.update(subject);
				}
    		}
    	}
    	
    	Resource[] children = coralSession.getStore().getResource(node);
    	for(int i=0; i<children.length; i++)
    	{
    		fixNodes((NavigationNodeResource)children[i], subject);
    	}
    }
    
	private StringBuffer buf; 

    private String makeMetaAttribute(List authorNodes)
    {
    	if(buf == null)
    	{
    		buf = new StringBuffer(512);
    	}
    	buf.setLength(0);
    	
		buf.append("<meta><authors>");
		for(Iterator i=authorNodes.iterator(); i.hasNext();)
		{
			String authorName = ((Element)(i.next())).getText();
			buf.append("<author><name>");
			buf.append(authorName);
			buf.append("</name><e-mail></e-mail></author>");
		}
		buf.append("</authors><sources><source><name></name><url>http://</url></source></sources>");
		buf.append("<editor></editor><organisation><name></name><address></address><tel></tel>");		buf.append("<fax></fax><e-mail></e-mail><url>http://</url><id>0</id></organisation></meta>");

		return buf.toString();
    }

	/* 
	 * (overriden)
	 */
	public boolean checkAccess(RunData data) throws ProcessingException
	{
		return checkAdministrator(context, coralSession);
	}
}
