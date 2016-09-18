package com.alcidauk.login;

import com.alcidauk.data.bean.User;
import com.alcidauk.data.repository.UserRepository;
import com.vaadin.server.VaadinSession;
import com.vaadin.server.WrappedSession;

/**
 * Created by alcidauk on 21/08/16.
 */
public final class CurrentUser {

    /**
     * The attribute key used to store the username in the session.
     */
    public static final String CURRENT_USER_SESSION_ATTRIBUTE_KEY = CurrentUser.class
            .getCanonicalName();

    private CurrentUser() {
    }

    /**
     * Returns the name of the current user stored in the current session, or an
     * empty string if no user name is stored.
     *
     * @throws IllegalStateException
     *             if the current session cannot be accessed.
     */
    public static User get() {
        return (User) getCurrentHttpSession().getAttribute(
                CURRENT_USER_SESSION_ATTRIBUTE_KEY);
    }

    private static WrappedSession getCurrentHttpSession() {
        VaadinSession s = VaadinSession.getCurrent();
        if (s == null) {
            throw new IllegalStateException(
                    "No session found for current thread");
        }
        return s.getSession();
    }

    /**
     * Sets the name of the current user and stores it in the current session.
     * Using a {@code null} username will remove the username from the session.
     *
     * @throws IllegalStateException
     *             if the current session cannot be accessed.
     */
    public static void set(User currentUser) {
        if (currentUser == null) {
            getCurrentHttpSession().removeAttribute(
                    CURRENT_USER_SESSION_ATTRIBUTE_KEY);
        } else {
            getCurrentHttpSession().setAttribute(
                    CURRENT_USER_SESSION_ATTRIBUTE_KEY, currentUser);
        }
    }

}
