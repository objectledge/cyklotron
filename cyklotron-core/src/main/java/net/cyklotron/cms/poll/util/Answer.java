package net.cyklotron.cms.poll.util;


/**
 * An simple container
 *
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 */
public class Answer
{
    private String title;

    private long id;

    /**
     * Creates an empty containter.
     *
     */
    public Answer(String title, long id)
    {
        this.title = title;
        this.id = id;
    }
    
    public void setTitle(String title)
    {
        this.title = title;
    }
    
    public String getTitle()
    {
        return title;
    }

    public long getId()
    {
        return id;
    }
}
