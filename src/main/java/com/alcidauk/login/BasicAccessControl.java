package com.alcidauk.login;

import com.alcidauk.data.bean.User;
import com.alcidauk.data.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by alcidauk on 21/08/16.
 */
public class BasicAccessControl implements AccessControl {

    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean signIn(String username, String password) {
        if (username == null || username.isEmpty())
            return false;

        List<User> users = userRepository.findByPasswordAndUsername(username, password);
        if(users.isEmpty()){
            return false;
        }

        CurrentUser.set(users.get(0));
        return true;
    }

    @Override
    public boolean isUserSignedIn() {
        return CurrentUser.get() != null;
    }

    @Override
    public boolean isUserInRole(String role) {
        if ("admin".equals(role)) {
            // Only the "admin" user is in the "admin" role
            return getPrincipalName().equals("admin");
        }

        // All users are in all non-admin roles
        return true;
    }

    @Override
    public String getPrincipalName() {
        return CurrentUser.get().getUsername();
    }

}
