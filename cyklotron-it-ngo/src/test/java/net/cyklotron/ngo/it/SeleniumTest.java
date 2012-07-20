package net.cyklotron.ngo.it;

import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverBackedSelenium;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.thoughtworks.selenium.Selenium;


public abstract class SeleniumTest
{
    protected abstract String startPage();
    
    protected Selenium selenium;

    @Before
    public void before()
        throws Exception
    {
        WebDriver driver = new FirefoxDriver();
        selenium = new WebDriverBackedSelenium(driver, startPage());
    }
    
    @After
    public void after()
        throws Exception
    {
        selenium.stop();
    }
}
