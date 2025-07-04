package com.techacademy.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.techacademy.entity.Employee;

public class UserDetail implements UserDetails {
    private static final long serialVersionUID = 1L;

    private final Employee employee;
    private final List<SimpleGrantedAuthority> authorities;

    // UserDetailServiceから受け取ったemployeeエンティティを元にインスタンス化
    public UserDetail(Employee employee) {

        // 渡されたemployeeエンティティを丸ごと格納
        this.employee = employee;

        // employeeの権限プロパティを元にリストをインスタンス化、初期化
        List<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
        // 従業員の権限(ADMIN/GENERAL)をSimpleGrantedAuthorityのauthoritiesリストに登録
        authorities.add(new SimpleGrantedAuthority(employee.getRole().toString()));
        // 権限リストをフィールドに格納
        this.authorities = authorities;
    }

    public Employee getEmployee() {
        return employee;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return employee.getPassword();
    }

    @Override
    public String getUsername() {
        return employee.getCode();
    }

    @Override
    public boolean isAccountNonExpired() {
        // 従業員が期限切れでなければtrueを返す
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // 従業員がロックされていなければtrueを返す
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // 従業員のパスワードが期限切れでなければtrueを返す
        return true;
    }

    @Override
    public boolean isEnabled() {
        // 従業員が有効であればtrueを返す
        return true;
    }
}