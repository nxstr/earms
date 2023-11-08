package cz.cvut.kbss.ear.ms.security;


import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;


@WebListener
public class SessionTimeoutManager implements HttpSessionListener {
    @Override
    public void sessionCreated(HttpSessionEvent se) {
        se.getSession().setMaxInactiveInterval(SecurityConstants.SESSION_TIMEOUT);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        //do nothing
    }
}

