package net.cyklotron.cms.search;

/**
 *
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: SearchConstants.java,v 1.2 2005-06-13 14:25:43 zwierzem Exp $
 */
public interface SearchConstants
{
    // field names ////////////////////////////////////////////////////////////////////////////////
    
    /** index_title - IST, obligatory */
    public static String FIELD_INDEX_TITLE = "index_title";
    /** index_abbreviation - IST, obligatory */
    public static String FIELD_INDEX_ABBREVIATION = "index_abbreviation";
    /** index_content - IT, obligatory */
    public static String FIELD_INDEX_CONTENT = "index_content";
    
    /** id - IS, technical */
    public static String FIELD_ID = "id";
    /** modification_time - IS, technical */
    public static String FIELD_MODIFICATION_TIME = "modification_time";
    /** path - I, technical */
    public static String FIELD_PATH = "path";
    /** site_name - IS, technical */
    public static String FIELD_SITE_NAME = "site_name";
    /** branch_id - S, technical */
    public static String FIELD_BRANCH_ID = "branch_id";
    /** resource_class_id - IS, technical */
    public static String FIELD_RESOURCE_CLASS_ID = "resource_class_id";
    /** owner field - synthetic */
    public static String FIELD_OWNER = "owner";
    /** created by field - synthetic */
    public static String FIELD_CREATED_BY = "created_by";
    /** description field - for NodeResource subclasses */
    public static String FIELD_DESCRIPTION = "description";
    /** categories field - synthetic */
    public static String FIELD_CATEGORY = "category";
}
