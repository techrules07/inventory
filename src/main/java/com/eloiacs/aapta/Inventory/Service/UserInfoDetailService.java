package com.eloiacs.aapta.Inventory.Service;

import com.eloiacs.aapta.Inventory.DBHandler.AuthHandler;
import com.eloiacs.aapta.Inventory.Models.AuthModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserInfoDetailService implements UserDetailsService {

    @Autowired
    private AuthHandler authHandler;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        AuthModel authModel = authHandler.getUserbyEmail(username);

        if (authModel != null) {
            return User.withUsername(username)
                    .password(authModel.getPassword())
                    .roles("Admin")
                    .build();
        }
        throw new UsernameNotFoundException("user not found " + username);
    }
}
