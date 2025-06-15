package com.techacademy.service;


import java.util.List;

import org.springframework.stereotype.Service;

import com.techacademy.entity.Report;
import com.techacademy.repository.ReportRepository;

@Service
public class ReportService {
    private final ReportRepository reportRepository;

    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    // 全社員の日報一覧表示(管理者ユーザ用)
    public List<Report>findAll() {
        return reportRepository.findAll();
    }

    // 社員番号で検索して特定の社員の日報を取得(一般ユーザ用)
    public List<Report> findByEmployeeCode(String employeeCode) {
        return reportRepository.findByEmployeeCode(employeeCode);
    }

}


