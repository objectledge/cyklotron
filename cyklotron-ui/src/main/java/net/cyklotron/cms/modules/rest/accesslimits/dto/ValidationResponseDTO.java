package net.cyklotron.cms.modules.rest.accesslimits.dto;

public class ValidationResponseDTO
{
    private boolean valid;

    private String error;

    public ValidationResponseDTO(boolean valid)
    {
        this.valid = valid;
    }

    public ValidationResponseDTO(boolean valid, String error)
    {
        this.valid = valid;
        this.error = error;
    }

    public boolean isValid()
    {
        return valid;
    }

    public void setValid(boolean valid)
    {
        this.valid = valid;
    }

    public String getError()
    {
        return error;
    }

    public void setError(String error)
    {
        this.error = error;
    }
}