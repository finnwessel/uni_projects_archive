package social_media_platform.utils;

import social_media_platform.dto.account.AccountDto;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class SessionUtils {

    public final static String ACCOUNT = "account";

    public final static String VERIFIED = "verified";

    /**
     * Returns the current http session
     *
     * @return the session
     */
    public static HttpSession getSession() {
        return (HttpSession) FacesContext.getCurrentInstance()
                .getExternalContext().getSession(false);
    }

    /**
     * Returns the account associated with the current session
     *
     * @return the account
     */
    public static AccountDto getAccount() {
        HttpSession session = getSession();

        if (session != null) {
            return (AccountDto) session.getAttribute(ACCOUNT);
        } else {
            return null;
        }
    }

    /**
     * Associate an account with the current session
     *
     * @param account an account
     */
    public static void setAccount(AccountDto account) {
        HttpSession session = getSession();

        if (session != null) {
            session.setAttribute(ACCOUNT, account);
        }
    }

    /**
     * Sets the verification status of the current session
     *
     * @param isVerified verification status
     */
    public static void setVerified(boolean isVerified) {
        HttpSession session = getSession();

        if (session != null) {
            if (isVerified) {
                session.setAttribute(VERIFIED, true);
            } else {
                session.removeAttribute(VERIFIED);
            }
        }
    }

    /**
     * Checks if the current account is logged in
     *
     * @return login status
     */
    public static boolean isLoggedIn() {
        HttpSession session = getSession();

        if (session != null) {
            return session.getAttribute(ACCOUNT) != null;
        } else {
            return false;
        }
    }

    /**
     * Invalidates the current session
     */
    public static void invalidate() {
        HttpSession session = getSession();

        if (session != null) {
            session.invalidate();
        }
    }
}
