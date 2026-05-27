package com.springsecurity.example2.config;

import com.springsecurity.example2.entity.User;
import com.springsecurity.example2.entity.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

// 시큐리티가 /loginProc 주소를 낚아채서 로그인을 진행할 때,
// 로그인이 완료되면 시큐리티 session을 만들어줍니다. (Security ContextHolder)
// 이 세션에 들어갈 수 있는 오브젝트는 정해져있습니다 => Authentication 객체
// Authentication 안에 User정보가 있어야 됨. 그 User정보 객체 타입이 => UserDetails 타입이어야 함.
public class PrincipalDetails implements UserDetails {

    private User user;

    public PrincipalDetails(User user) {
        this.user = user;
    }

    // ★ 중요: 사용자의 권한을 리턴
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                // 스프링 시큐리티는 ROLE_ 접두어가 있어야 권한으로 인식함
                return "ROLE_" + user.getRole().name();
            }
        });
        return collection;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getLoginId();
    }

    public String getNickname() {
        return user.getNickname();
    }

    // 계정 만료/잠김 여부 (여기서는 모두 true로 설정)
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }

    public boolean isAdmin() {
        return user.getRole() == UserRole.ADMIN;
    }
}