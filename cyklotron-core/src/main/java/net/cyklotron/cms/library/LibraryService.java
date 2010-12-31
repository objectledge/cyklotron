package net.cyklotron.cms.library;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.objectledge.coral.datatypes.ResourceList;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.InvalidResourceNameException;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.table.comparator.NameComparator;
import org.objectledge.coral.table.comparator.PathComparator;

import net.cyklotron.cms.category.CategoryException;
import net.cyklotron.cms.category.CategoryResource;
import net.cyklotron.cms.category.CategoryService;
import net.cyklotron.cms.documents.DocumentNodeResource;
import net.cyklotron.cms.files.FileResource;
import net.cyklotron.cms.related.RelatedService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;

public class LibraryService
{
    private final SiteService siteService;

    private final RelatedService relatedService;

    private final CategoryService categoryService;

    /**
     * No-arg constructor for mocking.
     */
    protected LibraryService()
    {
        this.siteService = null;
        this.relatedService = null;
        this.categoryService = null;
    }

    /**
     * Create LibraryService instance.
     * 
     * @param siteService site service.
     * @param relatedService related resources service.
     * @param categoryService category service.
     */
    public LibraryService(SiteService siteService, RelatedService relatedService,
        CategoryService categoryService)
    {
        this.siteService = siteService;
        this.relatedService = relatedService;
        this.categoryService = categoryService;
    }

    /**
     * Return configuration node for library application in a given site.
     * 
     * @param site the site.
     * @param coralSession coral session.
     * @return configuration node.
     */
    public LibraryConfigResource getConfig(SiteResource site, CoralSession coralSession)
    {
        Resource[] res;
        res = coralSession.getStore().getResourceByPath(site, "applications/library");
        if(res.length > 1)
        {
            throw new IllegalStateException("multiple library nodes exist under site "
                + site.getName());
        }
        else if(res.length == 1)
        {
            return (LibraryConfigResource)res[0];
        }
        else
        {
            res = coralSession.getStore().getResourceByPath(site, "applications");
            if(res.length != 1)
            {
                throw new IllegalStateException("applications node under site " + site.getName()
                    + " missing or duplicated");
            }
            LibraryConfigResource config;
            try
            {
                config = LibraryConfigResourceImpl.createLibraryConfigResource(coralSession,
                    "library", res[0]);
            }
            catch(InvalidResourceNameException e)
            {
                throw new RuntimeException("internal error", e);
            }
            return config;
        }
    }

    /**
     * Validate a resource as candidate for a library index item.
     * 
     * @param res the resource, either {@link DocumentNodeResource} or {@link FileResource}.
     * @param site library location
     * @param coralSession coral session
     * @return set of problems, hopefully empty.
     * @throws NotConfiguredException when configuration for library in given site is missing.
     */
    public Set<Problem> validateIndexCardCandidate(Resource res, SiteResource site,
        CoralSession coralSession)
        throws NotConfiguredException
    {
        Set<Problem> problems = new HashSet<Problem>();
        if(res instanceof DocumentNodeResource)
        {
            return validateDocumentIndexCardCandidate((DocumentNodeResource)res, site, coralSession);
        }
        if(res instanceof FileResource)
        {
            return validateFileIndexCardCandidate((FileResource)res, site, coralSession);
        }
        problems.add(Problem.INVALID_CLASS);
        return problems;
    }

    /**
     * Create an index card for a given resource.
     * 
     * @param res the resource, either {@link DocumentNodeResource} or {@link FileResource}. It is
     *        expected to be problem-free accoding to
     *        {@link #validateIndexCardCandidate(Resource, SiteResource, CoralSession)}.
     * @param site library location
     * @param coralSession coral session
     * @param locale locale used for sorting downloads by name, when manual ordering is not used.
     * @return an IndexCard
     * @throws IllegalArgumentException when resource has problems.
     * @throws NotConfiguredException when configuration for library in given site is missing.
     */
    public IndexCard getIndexCard(Resource res, SiteResource site, CoralSession coralSession,
        Locale locale)
        throws IllegalArgumentException, NotConfiguredException
    {
        Set<Problem> problems = validateIndexCardCandidate(res, site, coralSession);
        if(!problems.isEmpty())
        {
            throw new IllegalArgumentException("resource " + res.getIdString() + " has problems "
                + problems.toString());
        }
        DocumentNodeResource doc;
        FileResource file;
        List<FileResource> downloads;
        if(res instanceof DocumentNodeResource)
        {
            doc = (DocumentNodeResource)res;
            downloads = findDownloads(doc, site, coralSession, locale);
            return new IndexCard(doc, downloads);
        }
        if(res instanceof FileResource)
        {
            file = (FileResource)res;
            List<DocumentNodeResource> docCandidates = findDescriptionDocs(file, site, coralSession);
            if(docCandidates.size() == 1)
            {
                doc = docCandidates.get(0);
            }
            else
            {
                throw new IllegalArgumentException("multiple description documents for file "
                    + file.toString());
            }
            downloads = findDownloads(doc, site, coralSession, locale);
            return new IndexCard(doc, downloads);
        }
        throw new IllegalArgumentException("resource " + res.getIdString() + " has invalid class");
    }

    /**
     * Create a problem report for library in a given site.
     * 
     * @param site the site.
     * @param coralSession coral session.
     * @param locale locale used for sorting downloads by name, when manual ordering is not used.
     * @return report contents.
     * @throws NotConfiguredException when configuration for library in given site is missing.
     * @throws CategoryException when category service error occurs.
     */
    public List<ProblemReportItem> getProblemReport(SiteResource site, CoralSession coralSession,
        Locale locale)
        throws NotConfiguredException, CategoryException
    {
        List<ProblemReportItem> report = new ArrayList<ProblemReportItem>();
        CategoryResource libraryCategory = getConfig(site, coralSession).getCategory();
        if(libraryCategory == null)
        {
            throw new NotConfiguredException("library category is not set");
        }
        Resource[] all = categoryService.getResources(coralSession, libraryCategory, false);
        for(Resource res : all)
        {
            Set<Problem> problems = validateIndexCardCandidate(res, site, coralSession);
            if(!problems.isEmpty())
            {
                List<DocumentNodeResource> descriptionDocCandidates = null;
                List<FileResource> downloads = null;
                if(res instanceof DocumentNodeResource)
                {
                    downloads = findDownloads((DocumentNodeResource)res, site, coralSession, locale);
                }
                if(res instanceof FileResource)
                {
                    descriptionDocCandidates = findDescriptionDocs((FileResource)res, site,
                        coralSession);
                }
                report
                    .add(new ProblemReportItem(res, descriptionDocCandidates, downloads, problems));
            }
        }
        Collections.sort(report, new ProblemReportItem.Comparator(locale));
        return report;
    }

    /**
     * Retrieve all library index items for a given site.
     * 
     * @param site the site.
     * @param coralSession coral session.
     * @param locale locale used for sorting downloads by name, when manual ordering is not used.
     * @return unordered list of index items, intended to be used with {@link IndexCardTableModel}.
     * @throws NotConfiguredException when configuration for library in given site is missing.
     * @throws CategoryException when category service error occurs.
     */
    public List<IndexCard> getAllLibraryItems(SiteResource site, CoralSession coralSession, Locale locale)
        throws NotConfiguredException, CategoryException
    {        
        CategoryResource libraryCategory = getConfig(site, coralSession).getCategory();
        if(libraryCategory == null)
        {
            throw new NotConfiguredException("library category is not set");
        }
        Resource[] all = categoryService.getResources(coralSession, libraryCategory, false);
        // a set is used to make sure we have a single index card per description document
        Set<IndexCard> indexCards = new HashSet<IndexCard>();
        for(Resource res : all)
        {
            Set<Problem> problems = validateIndexCardCandidate(res, site, coralSession);
            if(problems.isEmpty())
            {
                indexCards.add(getIndexCard(res, site, coralSession, locale));
            }
        }
        return new ArrayList<IndexCard>(indexCards);
    }

    /**
     * Validate a document as candidate for a library index item.
     * 
     * @param doc the document.
     * @param site library location
     * @param coralSession coral session
     * @return set of problems, hopefully empty.
     * @throws NotConfiguredException when configuration for library in given site is missing.
     */
    private Set<Problem> validateDocumentIndexCardCandidate(DocumentNodeResource doc,
        SiteResource site, CoralSession coralSession)
        throws NotConfiguredException
    {
        Set<Problem> problems = new HashSet<Problem>();
        CategoryResource libraryCategory = getConfig(site, coralSession).getCategory();
        if(libraryCategory == null)
        {
            throw new NotConfiguredException("library category is not set");
        }
        List<CategoryResource> categories = Arrays.asList(categoryService.getCategories(
            coralSession, doc, true));
        if(!categories.contains(libraryCategory))
        {
            problems.add(Problem.LIBRARY_CATEGORY_MISSING);
        }

        Document metaDOM = null;
        if(doc.getMeta() == null || doc.getMeta().trim().length() == 0)
        {
            problems.add(Problem.INVALID_METADATA);
        }
        else
        {
            try
            {
                metaDOM = DocumentHelper.parseText(doc.getMeta());
            }
            catch(org.dom4j.DocumentException e)
            {
                problems.add(Problem.INVALID_METADATA);
            }
        }

        if(metaDOM != null)
        {
            @SuppressWarnings("unchecked")
            List<Element> authorNames = metaDOM.selectNodes("/meta/authors/author/name");
            if(authorNames.size() == 0)
            {
                problems.add(Problem.MISSING_AUTHOR);
            }
        }

        if(!doc.isValidityStartDefined())
        {
            problems.add(Problem.VALIDITY_START_UNSET);
        }
        return problems;
    }

    /**
     * Validate a file as candidate for a library index item.
     * 
     * @param file the file.
     * @param site library location
     * @param coralSession coral session
     * @return set of problems, hopefully empty.
     * @throws NotConfiguredException when configuration for library in given site is missing.
     */
    private Set<Problem> validateFileIndexCardCandidate(FileResource file, SiteResource site,
        CoralSession coralSession)
        throws NotConfiguredException
    {
        Set<Problem> problems = new HashSet<Problem>();
        CategoryResource libraryCategory = getConfig(site, coralSession).getCategory();
        if(libraryCategory == null)
        {
            throw new NotConfiguredException("library category is not set");
        }
        List<CategoryResource> categories = Arrays.asList(categoryService.getCategories(
            coralSession, file, true));
        if(!categories.contains(libraryCategory))
        {
            problems.add(Problem.LIBRARY_CATEGORY_MISSING);
        }

        List<DocumentNodeResource> descriptionDocCandidates = findDescriptionDocs(file, site,
            coralSession);
        if(descriptionDocCandidates.size() < 1)
        {
            problems.add(Problem.MISSING_DESCRIPTION_DOC);
        }
        else if(descriptionDocCandidates.size() > 1)
        {
            problems.add(Problem.MULTIPLE_DESCRIPTION_DOCS);
        }
        else
        {
            problems.addAll(validateDocumentIndexCardCandidate(descriptionDocCandidates.get(0),
                site, coralSession));
        }
        return problems;
    }

    /**
     * Find description document for a file.
     * 
     * @param file the file
     * @param site library location
     * @param coralSession coral session
     * @return a set of resources, may be empty.
     * @throws NotConfiguredException when configuration for library in given site is missing.
     */
    private List<DocumentNodeResource> findDescriptionDocs(FileResource file, SiteResource site,
        CoralSession coralSession)
        throws NotConfiguredException
    {
        CategoryResource libraryCategory = getConfig(site, coralSession).getCategory();
        if(libraryCategory == null)
        {
            throw new NotConfiguredException("library category is not set");
        }

        Resource relatedFrom[] = relatedService.getRelatedFrom(coralSession, file);

        List<DocumentNodeResource> descriptionDocCandidates = new ArrayList<DocumentNodeResource>();
        for(Resource res : relatedFrom)
        {
            if(res instanceof DocumentNodeResource)
            {
                List<CategoryResource> categories = Arrays.asList(categoryService.getCategories(
                    coralSession, res, true));
                if(categories.contains(libraryCategory))
                {
                    descriptionDocCandidates.add((DocumentNodeResource)res);
                }
            }
        }
        return descriptionDocCandidates;
    }

    /**
     * Find downloadable files for a description document.
     * 
     * @param doc description document
     * @param site library location
     * @param coralSession coral session
     * @return a set of files, may be empty
     * @throws NotConfiguredException when configuration for library in given site is missing.
     */
    private List<FileResource> findDownloads(DocumentNodeResource doc, SiteResource site,
        CoralSession coralSession, Locale locale)
        throws NotConfiguredException
    {
        CategoryResource libraryCategory = getConfig(site, coralSession).getCategory();
        if(libraryCategory == null)
        {
            throw new NotConfiguredException("library category is not set");
        }

        Comparator<Resource> autoSequence = null;
        if(locale != null)
        {
            autoSequence = new NameComparator<Resource>(locale);
        }
        @SuppressWarnings("unchecked")
        ResourceList<Resource> maualSequence = doc.getRelatedResourcesSequence();
        Resource relatedTo[] = relatedService.getRelatedTo(coralSession, doc, maualSequence,
            autoSequence);

        List<FileResource> downloads = new ArrayList<FileResource>();
        for(Resource res : relatedTo)
        {
            if(res instanceof FileResource)
            {
                List<CategoryResource> categories = Arrays.asList(categoryService.getCategories(
                    coralSession, res, true));
                if(categories.contains(libraryCategory))
                {
                    downloads.add((FileResource)res);
                }
            }
        }
        return downloads;
    }
}
