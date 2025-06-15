package com.techacademy.service;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.techacademy.entity.Employee;
import com.techacademy.repository.EmployeeRepository;

@Service // Spring Securityでの認証時に使用するユーザ情報を提供するクラス
public class UserDetailService implements UserDetailsService {
    private final EmployeeRepository employeeRepository;

    public UserDetailService(EmployeeRepository repository) {
        this.employeeRepository = repository;
    }

    @Override // Spring securityが認証時に呼び出すloadUserByUsernameメソッド(ユーザー名はログイン時の従業員code)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 従業員コードで従業員を検索し、employeeデータがあるかないかをみる(Optionalはnull回避のため)
        Optional<Employee> employee = employeeRepository.findById(username);

        // 該当の従業員データがなければ例外を投げる
        if (employee.isEmpty()) {
            throw new UsernameNotFoundException("Exception:Username Not Found");
        }

        // データがあれば、従業員データをspring security用のUserDetailを生成して返す
        return new UserDetail(employee.get());
    }
}