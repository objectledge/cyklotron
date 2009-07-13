package net.cyklotron.cms.modules.views.structure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jcontainer.dna.Logger;
import org.objectledge.authentication.UserManager;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.table.comparator.NameComparator;
import org.objectledge.diff.DetailElement;
import org.objectledge.diff.DiffUtil;
import org.objectledge.diff.Element;
import org.objectledge.diff.Sequence;
import org.objectledge.diff.Splitter;
import org.objectledge.html.HTMLParagraphSplitter;
import org.objectledge.html.HTMLService;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.category.CategoryResource;
import net.cyklotron.cms.category.CategoryService;
import net.cyklotron.cms.documents.DocumentNodeResource;
import net.cyklotron.cms.documents.DocumentNodeResourceImpl;
import net.cyklotron.cms.modules.views.documents.BaseSkinableDocumentScreen;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.related.RelatedService;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.structure.internal.ProposedDocumentData;
import net.cyklotron.cms.style.StyleService;

/**
 *
 */
public class ReviewProposedChanges
    extends BaseStructureScreen
{   
    protected UserManager userManager;
    
    private CategoryService categoryService;
    
    private  HTMLService htmlService;
      
    public ReviewProposedChanges(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, StructureService structureService,
        StyleService styleService, SiteService siteService, RelatedService relatedService,
        UserManager userManager, CategoryService categoryService, HTMLService htmlService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager,
                        structureService, styleService, siteService, relatedService);
        this.userManager = userManager;
        this.categoryService = categoryService;
        this.htmlService = htmlService;
    }
    
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            long docId = parameters.getLong("doc_id");
            templatingContext.put("doc_id", docId);
            
            DocumentNodeResource node = DocumentNodeResourceImpl.getDocumentNodeResource(coralSession, docId);

            boolean isDocEquals;
            Sequence<DetailElement<String>> title;
            Sequence<Sequence<DetailElement<String>>> docAbstract;
            Sequence<Sequence<DetailElement<String>>> content;
            DetailElement<Date> validityStart;
            DetailElement<Date> validityEnd;
            DetailElement<Date> eventStart;
            DetailElement<Date> eventEnd;
            Sequence<DetailElement<String>> eventPlace;
            Sequence<DetailElement<String>> organizedBy;
            Sequence<DetailElement<String>> organizedAddress;
            Sequence<DetailElement<String>> organizedPhone;
            Sequence<DetailElement<String>> organizedFax;
            Sequence<DetailElement<String>> organizedEmail;
            Sequence<DetailElement<String>> organizedWww;
            Sequence<DetailElement<String>> sourceName;
            Sequence<DetailElement<String>> sourceUrl;
            Sequence<DetailElement<String>> proposerCredentials;
            Sequence<DetailElement<String>> proposerEmail;
            Sequence<Sequence<DetailElement<String>>> description;
            
            CmsData cmsData = cmsDataFactory.getCmsData(context);
            ProposedDocumentData publishedData = new ProposedDocumentData();
            ProposedDocumentData proposedData = new ProposedDocumentData();
            
            proposedData.fromProposal(node, coralSession);
            Parameters screenConfig = cmsData.getEmbeddedScreenConfig(proposedData.getOrigin());
            proposedData.setConfiguration(screenConfig);
            publishedData.setConfiguration(screenConfig);
            publishedData.fromNode(node, categoryService, relatedService, coralSession);
            
            NameComparator<CategoryResource> comparator = new NameComparator<CategoryResource>(
                i18nContext.getLocale());
            long root_category_1 = screenConfig.getLong("category_id_1", -1);  
            long root_category_2 = screenConfig.getLong("category_id_2", -1);
            
            Set<CategoryResource> availableCategories = new HashSet<CategoryResource>();
            availableCategories.addAll(BaseSkinableDocumentScreen.getCategoryList(root_category_1, true, coralSession));
            availableCategories.addAll(BaseSkinableDocumentScreen.getCategoryList(root_category_2, true, coralSession)); 
            
            Set<CategoryResource> noAvalilableCategories = new HashSet<CategoryResource>();
            if(publishedData.getSelectedCategories()!=null){
                noAvalilableCategories.addAll(publishedData.getSelectedCategories());
            }
            noAvalilableCategories.removeAll(availableCategories);
            
             isDocEquals = true;
             if(!publishedData.getTitle().equals(proposedData.getTitle())){
                title = DiffUtil.diff(proposedData.getTitle(), publishedData.getTitle(), Splitter.WORD_BOUNDARY_SPLITTER);
                templatingContext.put("title",title);
                isDocEquals = false;
             }
             if(!publishedData.getAbstract().equals(proposedData.getAbstract())){
                docAbstract = DiffUtil.diff(proposedData.getAbstract(), publishedData.getAbstract(), Splitter.NEWLINE_SPLITTER,Splitter.WORD_BOUNDARY_SPLITTER);
                templatingContext.put("docAbstract",docAbstract);
                isDocEquals = false;
             }
             if(!publishedData.getContent().equals(proposedData.getContent())){
                content = DiffUtil.diff(proposedData.getContent(), publishedData.getContent(), HTMLParagraphSplitter.INSTANCE, Splitter.WORD_BOUNDARY_SPLITTER);
                templatingContext.put("content",content);
                if(proposedData.getContent() != null){
                    templatingContext.put("proposedHTMLContent",proposedData.getContent());
                }if(publishedData.getContent() != null ){
                    templatingContext.put("publishedHTMLContent",publishedData.getContent());
                }
                isDocEquals = false;
             }
             if(!publishedData.getEventPlace().equals(proposedData.getEventPlace())){
                eventPlace = DiffUtil.diff(proposedData.getEventPlace(), publishedData.getEventPlace(), Splitter.WORD_BOUNDARY_SPLITTER);
                templatingContext.put("eventPlace",eventPlace);
                isDocEquals = false;
             }
             
             
             if(publishedData.getEventStart()!= null && proposedData.getEventStart()!= null && publishedData.getEventStart().compareTo(proposedData.getEventStart())!=0){
                eventStart = new DetailElement<Date>(proposedData.getEventStart(),publishedData.getEventStart(),Element.State.CHANGED);             
                templatingContext.put("eventStart",eventStart);
                isDocEquals = false;
             }else if(publishedData.getEventStart()== null && proposedData.getEventStart()!= null){
                 eventStart = new DetailElement<Date>(proposedData.getEventStart(),publishedData.getEventStart(),Element.State.DELETED);             
                 templatingContext.put("eventStart",eventStart);
                 isDocEquals = false;
             }else if(publishedData.getEventStart()!= null && proposedData.getEventStart() == null ){
                 eventStart = new DetailElement<Date>(proposedData.getEventStart(),publishedData.getEventStart(),Element.State.ADDED);             
                 templatingContext.put("eventStart",eventStart);
                 isDocEquals = false;
             }
             
             
             if(publishedData.getEventEnd()!= null && proposedData.getEventEnd()!= null && publishedData.getEventEnd().compareTo(proposedData.getEventEnd())!= 0){
                 eventEnd = new DetailElement<Date>(proposedData.getEventEnd(),publishedData.getEventEnd(),Element.State.CHANGED);             
                 templatingContext.put("eventEnd",eventEnd);
                 isDocEquals = false;
             }else if(publishedData.getEventEnd()== null && proposedData.getEventEnd() != null ){
                 eventEnd = new DetailElement<Date>(proposedData.getEventEnd(),publishedData.getEventEnd(),Element.State.DELETED);             
                 templatingContext.put("eventEnd",eventEnd);
                 isDocEquals = false;
             }else if(publishedData.getEventEnd() != null && proposedData.getEventEnd() == null ){
                 eventEnd = new DetailElement<Date>(proposedData.getEventEnd(),publishedData.getEventEnd(),Element.State.ADDED);             
                 templatingContext.put("eventEnd",eventEnd);
                 isDocEquals = false;
             }
             
             
             if(publishedData.getValidityStart()!= null && proposedData.getValidityStart()!= null && publishedData.getValidityStart().compareTo(proposedData.getValidityStart())!=0){
                     validityStart= new DetailElement<Date>(proposedData.getValidityStart(),publishedData.getValidityStart(),Element.State.CHANGED);
                     templatingContext.put("validityStart",validityStart);
                     isDocEquals = false;
             }else if(publishedData.getValidityStart() == null && proposedData.getValidityStart() != null ){
                 validityStart= new DetailElement<Date>(proposedData.getValidityStart(),publishedData.getValidityStart(),Element.State.DELETED);
                 templatingContext.put("validityStart",validityStart);
                 isDocEquals = false;
             }else if(publishedData.getValidityStart() != null && proposedData.getValidityStart() == null ){
                 validityStart= new DetailElement<Date>(proposedData.getValidityStart(),publishedData.getValidityStart(),Element.State.ADDED);
                 templatingContext.put("validityStart",validityStart);
                 isDocEquals = false;
             }
             
             
             if(publishedData.getValidityEnd()!= null && proposedData.getValidityEnd()!= null && publishedData.getValidityEnd().compareTo(proposedData.getValidityEnd())!= 0){
                 validityEnd = new DetailElement<Date>(proposedData.getValidityEnd(),publishedData.getValidityEnd(),Element.State.CHANGED);
                 templatingContext.put("validityEnd",validityEnd);
                 isDocEquals = false;
             }else if(publishedData.getValidityEnd() == null && proposedData.getValidityEnd() != null){
                 validityEnd = new DetailElement<Date>(proposedData.getValidityEnd(),publishedData.getValidityEnd(),Element.State.DELETED);
                 templatingContext.put("validityEnd",validityEnd);
                 isDocEquals = false;
             }
             else if(publishedData.getValidityEnd() != null && proposedData.getValidityEnd() == null){
                 validityEnd = new DetailElement<Date>(proposedData.getValidityEnd(),publishedData.getValidityEnd(),Element.State.ADDED);
                 templatingContext.put("validityEnd",validityEnd);
                 isDocEquals = false;
             }
             
             
             if(!publishedData.getOrganizedBy().equals(proposedData.getOrganizedBy())){
                organizedBy = DiffUtil.diff(proposedData.getOrganizedBy(), publishedData.getOrganizedBy(), Splitter.WORD_BOUNDARY_SPLITTER);
                templatingContext.put("organizedBy",organizedBy);
                isDocEquals = false;
             }
             if(!publishedData.getOrganizedAddress().equals(proposedData.getOrganizedAddress())){
                organizedAddress = DiffUtil.diff(proposedData.getOrganizedAddress(), publishedData.getOrganizedAddress(), Splitter.WORD_BOUNDARY_SPLITTER);
                templatingContext.put("organizedAddress",organizedAddress);
                isDocEquals = false;
             }
             if(!publishedData.getOrganizedFax().equals(proposedData.getOrganizedFax())){
                organizedFax = DiffUtil.diff(proposedData.getOrganizedFax(), publishedData.getOrganizedFax(), Splitter.WORD_BOUNDARY_SPLITTER);
                templatingContext.put("organizedFax",organizedFax);
                isDocEquals = false;
             }
             if(!publishedData.getOrganizedEmail().equals(proposedData.getOrganizedEmail())){
                organizedEmail = DiffUtil.diff(proposedData.getOrganizedEmail(), publishedData.getOrganizedEmail(), Splitter.WORD_BOUNDARY_SPLITTER);
                templatingContext.put("organizedEmail",organizedEmail);
                isDocEquals = false;
             }
             if(!publishedData.getOrganizedPhone().equals(proposedData.getOrganizedPhone())){
                organizedPhone = DiffUtil.diff(proposedData.getOrganizedPhone(), publishedData.getOrganizedPhone(), Splitter.WORD_BOUNDARY_SPLITTER);
                templatingContext.put("organizedPhone",organizedPhone);
                isDocEquals = false;
             }
             if(!publishedData.getOrganizedWww().equals(proposedData.getOrganizedWww())){
                organizedWww = DiffUtil.diff(proposedData.getOrganizedWww(), publishedData.getOrganizedWww(), Splitter.WORD_BOUNDARY_SPLITTER);
                templatingContext.put("organizedWww",organizedWww);
                isDocEquals = false;
             }  
            if(!publishedData.getSourceName().equals(proposedData.getSourceName())){
               sourceName = DiffUtil.diff(proposedData.getSourceName(), publishedData.getSourceName(), Splitter.WORD_BOUNDARY_SPLITTER);
               templatingContext.put("sourceName",sourceName);
               isDocEquals = false;
            }
            if(!publishedData.getSourceUrl().equals(proposedData.getSourceUrl())){
               sourceUrl = DiffUtil.diff(proposedData.getSourceUrl(), publishedData.getSourceUrl(), Splitter.WORD_BOUNDARY_SPLITTER);
               templatingContext.put("sourceUrl",sourceUrl);
               isDocEquals = false;
            }
            if(!publishedData.getProposerCredentials().equals(proposedData.getProposerCredentials())){
               proposerCredentials = DiffUtil.diff(proposedData.getProposerCredentials(), publishedData.getProposerCredentials(), Splitter.WORD_BOUNDARY_SPLITTER);   
               templatingContext.put("proposerCredentials",proposerCredentials);
               isDocEquals = false;
            }
            if(!publishedData.getProposerEmail().equals(proposedData.getProposerEmail())){
               proposerEmail = DiffUtil.diff(proposedData.getProposerEmail(), publishedData.getProposerEmail(), Splitter.WORD_BOUNDARY_SPLITTER);
               templatingContext.put("proposerEmail",proposerEmail);
               isDocEquals = false;
            }
            if(!publishedData.getDescription().equals(proposedData.getDescription())){
               description = DiffUtil.diff(proposedData.getDescription(), publishedData.getDescription(), Splitter.NEWLINE_SPLITTER, Splitter.WORD_BOUNDARY_SPLITTER);
               templatingContext.put("description",description);
               isDocEquals = false;
            }
            
            List<CategoryResource> publishedDocCategories = new ArrayList<CategoryResource>(publishedData.getSelectedCategories());
            List<CategoryResource> proposedDocCategories = new ArrayList<CategoryResource>(proposedData.getSelectedCategories());
            proposedDocCategories.addAll(noAvalilableCategories);
            
            if((proposedDocCategories!=null && !proposedDocCategories.containsAll(publishedDocCategories)) 
               || (publishedDocCategories!=null && !publishedDocCategories.containsAll(proposedDocCategories)))
            {
                Collections.sort(publishedDocCategories,comparator);
                Collections.sort(proposedDocCategories,comparator);
                templatingContext.put("proposedDocCategories", proposedDocCategories);
                templatingContext.put("publishedDocCategories", publishedDocCategories);
                isDocEquals = false;
            }
            
            if(proposedData.getAttachments() != null && ( publishedData.getAttachments() == null ||
                !proposedData.getAttachments().containsAll(publishedData.getAttachments())))
            {
                templatingContext.put("proposedDocAttachments", proposedData.getAttachments());
                templatingContext.put("publishedDocAttachments", publishedData.getAttachments());  
                templatingContext.put("proposedDocAttachmentsDesc", proposedData.getAttachmentDescriptions());
                templatingContext.put("publishedDocAttachmentsDesc", publishedData.getAttachmentDescriptions());
                
                isDocEquals = false;
            }
            else if(publishedData.getAttachments() != null && ( proposedData.getAttachments() == null ||
                !publishedData.getAttachments().containsAll(proposedData.getAttachments())))
            {
                templatingContext.put("proposedDocAttachments", proposedData.getAttachments());
                templatingContext.put("publishedDocAttachments", publishedData.getAttachments());
                templatingContext.put("proposedDocAttachmentsDesc", proposedData.getAttachmentDescriptions());
                templatingContext.put("publishedDocAttachmentsDesc", publishedData.getAttachmentDescriptions());
                isDocEquals = false;
            }
            else if((proposedData.getAttachmentDescriptions() != null && ( publishedData.getAttachmentDescriptions()== null ||
                    !proposedData.getAttachmentDescriptions().containsAll(publishedData.getAttachmentDescriptions()))) || 
                    (publishedData.getAttachmentDescriptions() != null && (proposedData.getAttachmentDescriptions()==null ||
                    !publishedData.getAttachmentDescriptions().containsAll(proposedData.getAttachmentDescriptions()))))
            {
                templatingContext.put("proposedDocAttachments", proposedData.getAttachments());
                templatingContext.put("publishedDocAttachments", publishedData.getAttachments());
                templatingContext.put("proposedDocAttachmentsDesc", proposedData.getAttachmentDescriptions());
                templatingContext.put("publishedDocAttachmentsDesc", publishedData.getAttachmentDescriptions());
                isDocEquals = false;
            }
            if(proposedData.getEditorialNote() != null && !proposedData.getEditorialNote().equals("")){
                templatingContext.put("editorial_note", proposedData.getEditorialNote());
            }
            templatingContext.put("isDocEquals",isDocEquals); 
        }
        catch(Exception e)
        {
            throw new ProcessingException("Exception occured", e);
        }
    }
    
    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        return coralSession.getUserSubject().hasRole(getSite().getTeamMember());
    }
}
