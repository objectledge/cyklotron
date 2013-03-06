package net.cyklotron.cms.modules.rest.login;

public class AccountStatusDto
{
    
    String uid;
    
    String accountStatus;

    public AccountStatusDto(String uid, String accountStatus)
    {
        this.uid = uid;
        this.accountStatus = accountStatus;
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
   
}
