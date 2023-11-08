package cz.cvut.kbss.ear.ms.security;

public class SecurityConstants {
    private SecurityConstants() {
        throw new AssertionError();
    }

    public static final String SESSION_COOKIE_NAME = "JSESSIONID";

    public static final String REMEMBER_ME_COOKIE_NAME = "remember_me";

    public static final String USERNAME_PARAM = "username";

    public static final String PASSWORD_PARAM = "password";

    public static final String SECURITY_CHECK_URI = "/security_check";

    public static final String LOGOUT_URI = "/logout";

    public static final String COOKIE_URI = "/";

    public static final int SESSION_TIMEOUT = 60 * 60;
}

