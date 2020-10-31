package com.biblioteca.backend.config.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import com.biblioteca.backend.model.Rol;
import com.biblioteca.backend.model.Usuario.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails implements UserDetails {

    private static final long serialVersionUID = 1L;

    private String username;
    private String password;
    private Rol role;
    private boolean isActivo;

    public CustomUserDetails(Usuario usuario) {
        this.username = usuario.getEmail();
        this.password = usuario.getPassword();
        this.role = usuario.getRol();
        this.isActivo = usuario.isActivo();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role.getAuthority()));
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isActivo;
    }

}