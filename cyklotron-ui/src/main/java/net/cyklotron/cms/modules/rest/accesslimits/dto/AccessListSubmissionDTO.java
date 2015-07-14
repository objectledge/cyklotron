package net.cyklotron.cms.modules.rest.accesslimits.dto;

public class AccessListSubmissionDTO
{
    private String address;
    
    private int range;
    
    private long listId;
    
    private String description;

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public int getRange()
    {
        return range;
    }

    public void setRange(int range)
    {
        this.range = range;
    }

    public long getListId()
    {
        return listId;
    }

    public void setListId(long listId)
    {
        this.listId = listId;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }    
}
