/*
 * Created on 2003-11-13
 */
package net.cyklotron.cms.search.internal;

import java.util.Date;
import java.util.StringTokenizer;

import net.cyklotron.cms.CmsNodeResource;
import net.cyklotron.cms.CmsTool;
import net.cyklotron.cms.UserData;
import net.cyklotron.cms.category.CategoryService;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.integration.ResourceClassResource;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.search.IndexableResource;
import net.cyklotron.cms.search.SearchConstants;
import net.cyklotron.cms.search.SearchUtil;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.jcontainer.dna.Logger;
import org.objectledge.authentication.UserManager;
import org.objectledge.context.Context;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;

/**
 * Constructs lucene documents from Indexable resources. 
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: DocumentConstructor.java,v 1.3 2005-02-09 19:22:19 rafal Exp $
 */
public class DocumentConstructor
{
	/** integeration service */
	private IntegrationService integrationService;
    
	/** category service */
	private CategoryService categoryService;
    
    /** preferences service */
    private PreferencesService preferencesService;
    
    /** user manager */
    private UserManager userManager;
    
    /** the context */
    private Context context;
    
    /** the logger */
    private Logger logger;
    
    /**
     * 
     */
    public DocumentConstructor(Context context, Logger logger,
        PreferencesService preferencesService, UserManager userManager,
        CategoryService categoryService, IntegrationService integrationService)
    {
        this.integrationService = integrationService;
		this.categoryService = categoryService;
        this.context = context;
        this.logger = logger;
        this.preferencesService = preferencesService;
        this.userManager = userManager;
        
    }

	/**
	 * Create a lucene document from an given indexable resource.
	 *
	 * <p>Obligatory fields:</p>
	 * <ul>
	 * <li>index_title - IST - Text</li>
	 * <li>index_abbreviation - IST - Text</li>
	 * <li>index_content - IT - UnStored</li>
	 * </ul>
	 *
	 * <p>Technical (autogenerated) fields:</p>
	 * <ul>
	 * <li>id - IS - Keyword</li>
	 * <li>modification_time - IS - Keyword</li>
	 * <li>path - I</li>
	 * <li>site_name - IS - Keyword</li>
     * <li>branch_id - S - Keyword -
     *      <strong>WARN:</strong> added only by use of {@link 
     *      #setBranchField(Document,Resource)}</li>
	 * <li>resource_class_id - IS - Keyword</li>
	 * </ul>
	 *
	 * <p>Legend:</p>
	 * <ul>
	 * <li>I - indexed</li>
	 * <li>S - stored</li>
	 * <li>T - tokenized</li>
	 * </ul>
	 *
	 * @param node the indexable resource.
	 * @return the lucene document or <code>null</code> if all the document fields were empty.
	 */
	public Document createDocument(CoralSession coralSession, IndexableResource node)
	{
		Document doc = new Document();

		boolean documentEmpty = true;
		String tmp;

		// obligatory
		tmp = node.getIndexTitle();
		if (tmp != null)
		{
			doc.add(Field.Text(SearchConstants.FIELD_INDEX_TITLE, tmp));
			documentEmpty = false;
		}
		tmp = node.getIndexAbbreviation();
		if (tmp != null)
		{
			doc.add(Field.Text(SearchConstants.FIELD_INDEX_ABBREVIATION, tmp));
			documentEmpty = false;
		}
		tmp = node.getIndexContent();
		if (tmp != null)
		{
			doc.add(Field.UnStored(SearchConstants.FIELD_INDEX_CONTENT, tmp));
			documentEmpty = false;
		}

		ResourceClassResource rcr = integrationService.getResourceClass(coralSession, node.getResourceClass());
		String indexableFields = rcr.getIndexableFields();
		if(indexableFields != null)
		{
			StringTokenizer st = new StringTokenizer(indexableFields);
			while(st.hasMoreTokens())
			{
				String fieldName = st.nextToken();
				Object value = node.getFieldValue(fieldName);
                String strValue = getValueAsIndexableString(value);                     
				if(strValue != null)
				{
					boolean store = node.isStored(fieldName);
					boolean index = node.isIndexed(fieldName);
					boolean token = node.isTokenized(fieldName);
					doc.add(new Field(fieldName, strValue, store, index, token));
					documentEmpty = false;
				}
			}
		}
                
		if (!documentEmpty)
		{
			// technical
			doc.add(Field.Keyword(SearchConstants.FIELD_ID, node.getIdString()));
			doc.add(Field.Keyword(SearchConstants.FIELD_MODIFICATION_TIME, SearchUtil.dateToString(node.getModificationTime())));
			doc.add(new Field(SearchConstants.FIELD_PATH, node.getPath(), false, true, false));
			doc.add(Field.Keyword(SearchConstants.FIELD_SITE_NAME, CmsTool.getSite(node).getName()));
			doc.add(Field.Keyword(SearchConstants.FIELD_RESOURCE_CLASS_ID, node.getResourceClass().getIdString()));

            if(node instanceof CmsNodeResource)
            {
                if(((CmsNodeResource)node).getDescription() != null)
                {
                    doc.add(Field.UnStored(SearchConstants.FIELD_DESCRIPTION, ((CmsNodeResource)node).getDescription()));
                }
            }

            String ownerLogin = getValueAsIndexableString(node.getOwner());
            if(ownerLogin != null)
            {
                doc.add(Field.UnStored(SearchConstants.FIELD_OWNER, ownerLogin));
            }
            
			StringBuffer sb = new StringBuffer();
			Resource[] categories = categoryService.getCategories(coralSession, node, false);
			for(int i = 0; i < categories.length; i++)
			{
				if(i > 0)
				{
					sb.append('\n');
				}
				sb.append(categories[i].getPath());
			}
			// we don't want to store category information
			doc.add(Field.UnStored(SearchConstants.FIELD_CATEGORY, sb.toString()));
			return doc;
		}
		else
		{
			return null;
		}
	}

    public void setBranchField(Document doc, Resource branch)
    {
        if(doc != null)
        {
            doc.removeFields(SearchConstants.FIELD_BRANCH_ID);
            doc.add(Field.Keyword(SearchConstants.FIELD_BRANCH_ID, branch.getIdString()));
        }
    }

    /**
     * Create a lucene document from an given indexable resource and set branch field in it.
     * 
     * @return the lucene document or <code>null</code> if all the document fields were empty.
     */
    public Document createDocument(CoralSession coralSession, IndexableResource node, Resource branch)
    {
        Document doc = createDocument(coralSession, node);
        setBranchField(doc, branch);
        return doc;
    }


	// implementation //////////////////////////////////////////////////////////////////////////////
	
	private String getValueAsIndexableString(Object value)
	{
		if(value == null)
		{
			return null;
		}
		
		String str = null;
		if(value instanceof String)
		{
			str = (String)value;
		}
		else
		if(value instanceof Date)
		{
			str = SearchUtil.dateToString((Date)value);
		}
		else
		if(value instanceof Subject)
		{
			UserData userData = new UserData(context, logger, 
                preferencesService, userManager, (Subject)value);
			try
			{
				str = userData.getLogin();
			}
			catch (Exception e)
			{
				return null;
			}
		}
		else
		if(value instanceof Integer)
		{
			str = ((Integer)value).toString();
		}
		else
		if(value instanceof Long)
		{
			str = ((Long)value).toString();
		}
		else
		if(value instanceof Double)
		{
			str = ((Double)value).toString();
		}
		else
		if(value instanceof Float)
		{
			str = ((Float)value).toString();
		}
		// TODO add support for other non string values (ie - daterange)
		/*else
		if(value instanceof DateRange)
		{
			attributeValue = ((DateRange)value).;
		}*/
		return str;
	}	
}
