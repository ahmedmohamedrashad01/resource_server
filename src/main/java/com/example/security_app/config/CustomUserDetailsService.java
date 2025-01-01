package com.example.security_app.config;

import com.example.security_app.model.UserEntity;
import com.example.security_app.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = this.userRepo.findByEmail(username)
        .orElseThrow(()-> new UsernameNotFoundException("User does not found"));

        // تحقق من حالة الحساب
        if (!userEntity.isEnabled()) {
            throw new DisabledException("Account is not activated. Please activate your account.");
        }

        List<GrantedAuthority> authorities = userEntity.getRoles().stream()
        .map(auc-> new SimpleGrantedAuthority(auc.getName())).collect(Collectors.toList());
        return new User(userEntity.getEmail(), userEntity.getPassword(),authorities);
    }
}
