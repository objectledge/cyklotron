package net.cyklotron.cms.modules.rest.forum;

/**
 * Names of fields matter. If you change them then postTable.js script needs to be changed as well.
 * 
 * @author Marek Lewandowski
 */
public class PostDto
{
    private String title;

    private int replies;

    private String url;

    private String createdAt;

    private String lastReplyAt;

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getCreatedAt()
    {
        return createdAt;
    }

    public void setCreatedAt(String createdAt)
    {
        this.createdAt = createdAt;
    }

    public int getReplies()
    {
        return replies;
    }

    public void setReplies(int replies)
    {
        this.replies = replies;
    }

    public String getLastReplyAt()
    {
        return lastReplyAt;
    }

    public void setLastReplyAt(String lastReplyAt)
    {
        this.lastReplyAt = lastReplyAt;
    }

}
