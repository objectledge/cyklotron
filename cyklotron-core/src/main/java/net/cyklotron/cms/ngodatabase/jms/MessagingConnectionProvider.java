package net.cyklotron.cms.ngodatabase.jms;

import javax.jms.Connection;
import javax.jms.JMSException;

public interface MessagingConnectionProvider
{
    <C extends Connection> C createConnection(String name, Class<C> connectionClass)
        throws JMSException;
}
