/*
 * Created on 2003-11-13
 */
package net.cyklotron.cms.search.internal;

import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.jcontainer.dna.Logger;
import org.objectledge.authentication.UserManager;
import org.objectledge.context.Context;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.html.HTMLException;

import net.cyklotron.cms.CmsNodeResource;
import net.cyklotron.cms.CmsTool;
import net.cyklotron.cms.UserData;
import net.cyklotron.cms.category.CategoryService;
import net.cyklotron.cms.documents.DocumentMetadataHelper;
import net.cyklotron.cms.documents.DocumentNodeResource;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.integration.ResourceClassResource;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.search.IndexableResource;
import net.cyklotron.cms.search.SearchConstants;
import net.cyklotron.cms.search.SearchUtil;
import net.cyklotron.cms.structure.internal.OrganizationData;

/**
 * Constructs lucene documents from Indexable resources. 
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: DocumentConstructor.java,v 1.7 2005-06-13 14:25:42 zwierzem Exp $
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
            doc.add(getFieldText(SearchConstants.FIELD_INDEX_TITLE, tmp));
            documentEmpty = false;
        }
        tmp = node.getIndexAbbreviation();
        if (tmp != null)
        {
            doc.add(getFieldText(SearchConstants.FIELD_INDEX_ABBREVIATION, tmp));
            documentEmpty = false;
        }
        tmp = node.getIndexContent();
        if (tmp != null)
        {
            doc.add(getFieldUnstored(SearchConstants.FIELD_INDEX_CONTENT, tmp));
            documentEmpty = false;
        }

        ResourceClassResource rcr = integrationService.getResourceClass(coralSession, node
            .getResourceClass());
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
                    Index index = Index.NO;
                    if(node.isIndexed(fieldName)){
                        index = node.isTokenized(fieldName) ? Index.ANALYZED : Index.NOT_ANALYZED;
                    }
                    Store store = node.isStored(fieldName) ? Store.YES : Store.NO;

                    doc.add(new Field(fieldName, strValue, store, index));
                    documentEmpty = false;
                }
            }
        }

        if(!documentEmpty)
        {
            // technical
            doc.add(this.getFieldKeyword(SearchConstants.FIELD_ID, node.getIdString()));
            doc.add(this.getFieldKeyword(SearchConstants.FIELD_MODIFICATION_TIME, SearchUtil
                .dateToString(node.getModificationTime())));
            doc.add(new Field(SearchConstants.FIELD_PATH, node.getPath(), Store.NO,
                Index.NOT_ANALYZED));
            doc.add(this.getFieldKeyword(SearchConstants.FIELD_SITE_NAME, CmsTool.getSite(node)
                .getName()));
            doc.add(this.getFieldKeyword(SearchConstants.FIELD_RESOURCE_CLASS_ID, node
                .getResourceClass().getIdString()));

            if(node instanceof CmsNodeResource)
            {
                if(((CmsNodeResource)node).getDescription() != null)
                {
                    doc.add(this.getFieldUnstored(SearchConstants.FIELD_DESCRIPTION,
                        ((CmsNodeResource)node).getDescription()));
                }
            }

            String loginAndName = getValueAsIndexableString(node.getOwner());
            if(loginAndName != null)
            {
                doc.add(this.getFieldUnstored(SearchConstants.FIELD_OWNER, loginAndName));
            }
            loginAndName = getValueAsIndexableString(node.getCreatedBy());
            if(loginAndName != null)
            {
                doc.add(this.getFieldUnstored(SearchConstants.FIELD_CREATED_BY, loginAndName));
            }

            StringBuilder sb = new StringBuilder();
            Resource[] categories = categoryService.getCategories(coralSession, node, false);
            for (int i = 0; i < categories.length; i++)
            {
                if(i > 0)
                {
                    sb.append('\n');
                }
                sb.append(categories[i].getPath());
            }
            // we don't want to store category information
            doc.add(this.getFieldUnstored(SearchConstants.FIELD_CATEGORY, sb.toString()));
            
            if(node instanceof DocumentNodeResource)
            {
                setOrganizationNameField(doc, (DocumentNodeResource)node);
            }
            
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
            doc.add(this.getFieldKeyword(SearchConstants.FIELD_BRANCH_ID, branch.getIdString()));
        }
    }
    
    public void setOrganizationNameField(Document doc, DocumentNodeResource node)
    {
        if(doc != null)
        {
            StringBuilder sb = new StringBuilder();
            try
            {
                List<OrganizationData> organizationData = OrganizationData.fromMeta(
                    DocumentMetadataHelper.textToDom4j(node.getMeta()), "/meta/organizations");
                for(int i = 0; i < organizationData.size(); i++)
                {
                    if(i > 0)
                    {
                        sb.append('\n');
                    }
                    sb.append(organizationData.get(i).getName());
                }
            }
            catch(HTMLException e)
            {
                // do nothing.
            }
            doc.add(this.getFieldTermVector(SearchConstants.FIELD_ORGANIZATION_NAME, sb.toString()));
        }
    }

    /**
     * Create a lucene document from an given indexable resource and set branch field in it.
     * 
     * @return the lucene document or <code>null</code> if all the document fields were empty, or
     *         document creation failed.
     */
    public Document createDocument(CoralSession coralSession, IndexableResource node,
        Resource branch)
    {
        Document doc;
        try
        {
            doc = createDocument(coralSession, node);
            setBranchField(doc, branch);
            return doc;
        }
        catch(Exception e)
        {
            logger.error("failed to create Lucene document for resource " + node, e);
            return null;
        }
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
        else if(value instanceof Date)
        {
            str = SearchUtil.dateToString((Date)value);
        }
        else if(value instanceof Subject)
        {
            UserData userData = new UserData(context, logger, preferencesService, userManager,
                (Subject)value);
            try
            {
                str = userData.getLogin() + " " + userData.getPersonalData().get("givenName", "")
                    + " " + userData.getPersonalData().get("sn", "");
            }
            catch(Exception e)
            {
                return null;
            }
        }
        else if(value instanceof Integer)
        {
            str = ((Integer)value).toString();
        }
        else if(value instanceof Long)
        {
            str = ((Long)value).toString();
        }
        else if(value instanceof Double)
        {
            str = ((Double)value).toString();
        }
        else if(value instanceof Float)
        {
            str = ((Float)value).toString();
        }
        // add support for other non string values (ie - daterange)
        /*
         * else if(value instanceof DateRange) { attributeValue = ((DateRange)value).; }
         */
        return str;
    }

    private Field getFieldText(String name, String value)
    {
        return new Field(name, value, Field.Store.YES, Field.Index.ANALYZED);
    }

    private Field getFieldKeyword(String name, String value)
    {
        return new Field(name, value, Field.Store.YES, Field.Index.NOT_ANALYZED);
    }

    private Field getFieldUnstored(String name, String value)
    {
        return new Field(name, value, Store.NO, Index.ANALYZED);
    }
    
    private Field getFieldTermVector(String name, String value)
    {
        return new Field(name, value, Field.Store.YES, Field.Index.ANALYZED,
            Field.TermVector.WITH_POSITIONS_OFFSETS);
    }
}
