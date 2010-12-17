package net.cyklotron.cms.modules.views.documents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.forms.Form;
import org.objectledge.forms.FormsException;
import org.objectledge.forms.FormsService;
import org.objectledge.forms.Instance;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;


import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.documents.DocumentException;
import net.cyklotron.cms.documents.DocumentNodeResource;
import net.cyklotron.cms.documents.DocumentService;
import net.cyklotron.cms.documents.FooterResource;
import net.cyklotron.cms.documents.table.FooterSequenceComparator;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.related.RelatedService;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;

/**
 * The screen assember for form-tool test app.
 */
public class EditDocument extends BaseDocumentScreen
{
    /** Form-tool service. */
    protected  FormsService formService;
    /** Document edit form. */
    protected Form form = null;


    
    public EditDocument(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, StructureService structureService,
        StyleService styleService, SiteService siteService, RelatedService relatedService,
        DocumentService documentService, IntegrationService integrationService,
        FormsService formsService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager,
                        structureService, styleService, siteService, relatedService,
                        documentService, integrationService);
        this.formService = formsService;
        form = documentService.getDocumentEditForm();
    }

    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        templatingContext.put("maxInactiveSessionInterval",
            httpContext.getRequest().getSession().getMaxInactiveInterval());
        
        // get processed node
        DocumentNodeResource doc = getDocument();

        // prepare needed variables
        Instance instance = getInstance(httpContext);
        Subject subject = coralSession.getUserSubject();

		// kill da instance
		if (parameters.getBoolean("from_list",false))
		{
			formService.removeInstance(httpContext, instance);
            instance = getInstance(httpContext);
		}

        // initialise instance with document data if this is the first hit
        if(!instance.isDirty())
        {
            if(!prepareInstance(doc, instance, context, templatingContext))
            {
                // operation failed
                throw new ProcessingException("Could not prepare the document with id="+doc.getIdString()
                            +" for editing");
            }

            try
            {
                form.process(instance, parameters);
            }
            catch(Exception e)
            {
                throw new ProcessingException("Document edit form processing failed", e);
            }
        }

        templatingContext.put("doc-edit-instance", instance);

        // WARN: ugly hacking
        // save view
        if(parameters.isDefined("return_view"))
        {
            String returnView = parameters.get("return_view",null);
            httpContext.setSessionAttribute("document_edit_return_view", returnView);
        }
        
        try
        {
            Resource root = documentService.getFootersRoot(coralSession, doc.getSite());
            Resource[] resources = coralSession.getStore().getResource(root);
            List footers = new ArrayList();
            for(int i = 0; i < resources.length; i++)
            {
                if(resources[i] instanceof FooterResource)
                {
                    FooterResource footer = (FooterResource)resources[i];
                    if(footer.getEnabled(false))
                    {
                        footers.add(footer);
                    }
                }
            }
            Comparator comparator = new FooterSequenceComparator();
            Collections.sort(footers,comparator);
            footers.add(0, new Footer("","-----------"));
            templatingContext.put("footerList", footers);
            templatingContext.put("templatingContext", templatingContext);
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to prepare footer list", e);
        }
    }

    protected Instance getInstance(HttpContext httpContext)
        throws ProcessingException
    {
        try
        {
            return formService.getInstance(DocumentService.FORM_NAME,httpContext);
        }
        catch(FormsException e)
        {
            throw new ProcessingException("Cannot get a form instance", e);
        }
    }

    protected boolean prepareInstance(DocumentNodeResource doc, Instance instance,
                                        Context context, TemplatingContext templatingContext)
    {
        try
        {
            documentService.copyFromDocumentNode(doc, instance.getDocument());
            // the insatnce is changed so we set it dirty
            instance.setDirty(true);
        }
        catch(DocumentException e)
        {
            templatingContext.put("result","exception");
            logger.error("DocumentException: ",e);
            templatingContext.put("trace", new StackTrace(e));
            return false;
        }
        return true;
    }
    
    
    public class Footer 
    {
        private String value;
        
        private String name;
        
        public Footer(String name, String value)
        {
            this.name = name;
            this.value = value;
        }
     
        public String getValue()
        {
            return value;
        }
        
        public String getName()
        {
            return name;
        }
    }
}
