package net.cyklotron.ngo.it;

import com.thoughtworks.selenium.Selenium;

public abstract class Page
{

    protected static String DEFAULT_PAGE_LOAD_TIME = "30000";

    protected static String SLOW_PAGE_LOAD_TIME = "300000";

    protected Selenium selenium;

    public Page(Selenium selenium)
    {

        this.selenium = selenium;

    }

    public Selenium getPage()
    {

        return selenium;

    }

    public void Close()
    {

        selenium.close();

    }

}
