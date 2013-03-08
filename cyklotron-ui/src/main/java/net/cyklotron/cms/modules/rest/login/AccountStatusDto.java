package net.cyklotron.cms.modules.rest.login;

public class AccountStatusDto
{
    
    String uid;
    
    String accountStatus;
    
    Long expiration;

    public AccountStatusDto(String uid, String accountStatus, Long expiration)
    {
        this.uid = uid;
        this.accountStatus = accountStatus;
        this.expiration = expiration;
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
