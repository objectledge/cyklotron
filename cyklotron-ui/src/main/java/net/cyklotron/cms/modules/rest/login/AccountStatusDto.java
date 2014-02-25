package net.cyklotron.cms.modules.rest.login;

public class AccountStatusDto
{
    
    String uid;
    
    String accountStatus;
    
    Long expiration;
    
    boolean hasMultipleEmailAddresses;
    
    public AccountStatusDto(String uid, String accountStatus, Long expiration, boolean hasMultipleEmailAddresses)
    {
        this.uid = uid;
        this.accountStatus = accountStatus;
        this.expiration = expiration;
        this.hasMultipleEmailAddresses = hasMultipleEmailAddresses;
    }

    public boolean isHasMultipleEmailAddresses()
    {
        return hasMultipleEmailAddresses;
    }

    public void setHasMultipleEmailAddresses(boolean hasMultipleEmailAddresses)
    {
        this.hasMultipleEmailAddresses = hasMultipleEmailAddresses;
    }
    
    public String getUid()
    {
        return uid;
    }

    public void setUid(String uid)
    {
        this.uid = uid;
    }

    public String getAccountStatus()
    {
        return accountStatus;
    }

    public void setAccountStatus(String accountStatus)
    {
        this.accountStatus = accountStatus;
    }

    public Long getExpiration()
    {
        return expiration;
    }

    public void setExpiration(Long expiration)
    {
        this.expiration = expiration;
    }
   
}
