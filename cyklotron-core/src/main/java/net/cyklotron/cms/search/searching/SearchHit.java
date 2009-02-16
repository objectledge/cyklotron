package net.cyklotron.cms.search.searching;

import java.util.Date;

/**
 * This interface defines a generic search hit.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: SearchHit.java,v 1.2 2005-06-03 07:29:40 pablo Exp $
 */
public interface SearchHit
{
    /**
     * Returns an arbitrary search hit field upon it's name.
     * If this hit is not accessible it will return a <code>NO_ACCESS</code> string.
     *
     * @param fieldName name of the field to be retrieved.
     * @return the value of the chosen field
     */
    public String get(String fieldName);

    /**
     * Returns an arbitrary search hit field upon it's name and converts it into Date object.
     *
     * @param fieldName name of the field to be retrieved.
     * @return the date value of the chosen field
     */
    public Date getAsDate(String fieldName);

    /**
     * Returns the hit title.
     *
     * @return the title of a resource represented by this hit.
     */
    public String getTitle();

    /**
     * Returns the hit abbreviation.
     *
     * @return the abbreviation of a resource represented by this hit.
     */
    public String getAbbreviation();

    /**
     * Returns the hit url.
     *
     * @return the url of a resource represented by this hit.
     */
    public String getUrl();
    
    /**
     * Returns the hit edit url.
     *
     * @return the url of a resource represented by this hit.
     */
    public String getEditUrl();

    /**
     * Returns the modification time of the resource represented by search hit.
     *
     * @return the object representing the modification time of the resource represented by this
     * search hit.
     */
    public Date getModificationTime()
    throws Exception;

    /**
     * Returns hit score in promiles.
     *
     * @return the number representing promiles of search relevancy.
     */
    public int getScore();

    /**
     * Returns the size of the resource.
     *
     * @return string representaion.
     */
    //public int getSize();
}
