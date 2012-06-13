package net.cyklotron.ngo.it;

import junit.framework.TestCase;

import com.thoughtworks.selenium.DefaultSelenium;

public class SeleniumSmokeTest
    extends TestCase
{
    protected DefaultSelenium createSeleniumClient(String url)
        throws Exception
    {
        return new DefaultSelenium("localhost", 4444, "*firefox", url);
    }

    public void testSomethingSimple()
        throws Exception
    {
        DefaultSelenium selenium = createSeleniumClient("http://localhost:8080/");
        selenium.start();

        // What do we do now?

        selenium.stop();
    }
}
