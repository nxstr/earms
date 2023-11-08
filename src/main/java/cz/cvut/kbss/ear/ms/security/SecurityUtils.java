package cz.cvut.kbss.ear.ms.security;
import cz.cvut.kbss.ear.ms.model.Account;
import cz.cvut.kbss.ear.ms.security.model.AccountDetails;
import cz.cvut.kbss.ear.ms.security.model.AuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

public class SecurityUtils {

    /**
     * Gets current user.
     *
     * @return the current user
     */
    public static Account getCurrentUser() {
        final SecurityContext context = SecurityContextHolder.getContext();
        assert context != null;
        final AccountDetails userDetails = (AccountDetails) context.getAuthentication().getDetails();
        return userDetails.getAccount();
    }

    /**
     * Gets details of the currently authenticated user.
     *
     * @return Currently authenticated user details or null, if no one is currently authenticated
     */
    public static AccountDetails getCurrentUserDetails() {
        final SecurityContext context = SecurityContextHolder.getContext();
        if (context.getAuthentication() != null && context.getAuthentication().getDetails() instanceof AccountDetails) {
            return (AccountDetails) context.getAuthentication().getDetails();
        } else {
            return null;
        }
    }

    /**
     * Creates an authentication token based on the specified user details and sets it to the current thread's security
     * context.
     *
     * @param userDetails Details of the user to set as current
     * @return The generated authentication token
     */
    public static AuthenticationToken setCurrentUser(AccountDetails userDetails) {
        final AuthenticationToken token = new AuthenticationToken(userDetails.getAuthorities(), userDetails);
        token.setAuthenticated(true);

        final SecurityContext context = new SecurityContextImpl();
        context.setAuthentication(token);
        SecurityContextHolder.setContext(context);
        return token;
    }

    /**
     * Checks whether the current authentication token represents an anonymous user.
     *
     * @return Whether current authentication is anonymous
     */
    public static boolean isAuthenticatedAnonymously() {
        return getCurrentUserDetails() == null;
    }

}


