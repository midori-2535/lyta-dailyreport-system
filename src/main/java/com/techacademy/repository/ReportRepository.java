package com.techacademy.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;

public interface ReportRepository extends JpaRepository<Report, Integer> {

    // 社員番号で検索
    List<Report> findByEmployee(Employee employee);

    // ログイン中の社員番号と入力した日付で検索
    Optional<Report> findByEmployeeCodeAndReportDate(String employeeCode, LocalDate reportDate);

}
