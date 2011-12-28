package net.cyklotron.cms.docimport;

import java.util.Set;

import net.cyklotron.cms.category.CategoryResource;
import net.cyklotron.cms.files.DirectoryResource;
import net.cyklotron.cms.structure.NavigationNodeResource;

/**
 * Configuration of remote document import.
 * 
 * @author rafal.krzewski@objectledge.org
 */
public interface ImportTargetConfiguration
{
    /**
     * The owner of the created documents and attachments.
     * 
     * @return the owner of the created documents and attachments.
     */
    String getContentOwnerLogin();

    /**
     * Target location of document import.
     * 
     * @return parent node of the imported document, or the root of calendar structure.
     */
    NavigationNodeResource getTargetLocation();

    /**
     * Calendar structure type of the target location. See
     * {@link net.cyklotron.cms.structure.StructureService#DAILY_CALENDAR_TREE_STRUCTURE} etc.
     * 
     * @return Calendar structure type of the target location (D, M, Y or N).
     */
    String getCalendarStructureType();

    /**
     * Categories to be assigned to the imported documents.
     * 
     * @return a set of Category resources.
     */
    Set<CategoryResource> getCategories();

    /**
     * Target location of document attachments.
     * 
     * @return target location of document attachments.
     */
    DirectoryResource getAttachmentsLocation();
}
