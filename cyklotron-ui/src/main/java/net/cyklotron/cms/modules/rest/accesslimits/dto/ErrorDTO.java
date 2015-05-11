package net.cyklotron.cms.modules.rest.accesslimits.dto;

public class ErrorDTO 
{
    private final String message;
    
    public ErrorDTO(Exception e)
    {
        this.message = e.getMessage();
    }

    public String getMessage()
    {
        return message;
    }
}