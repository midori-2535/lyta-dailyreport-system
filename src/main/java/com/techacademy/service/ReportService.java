package com.techacademy.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.entity.Report;
import com.techacademy.repository.ReportRepository;

@Service
public class ReportService {
    private final ReportRepository reportRepository;

    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    // 全社員の日報一覧表示(管理者ユーザ用)
    public List<Report> findAll() {
        return reportRepository.findAll();
    }

    // 社員番号で検索して特定の社員の日報を取得(一般ユーザ用)
    public List<Report> findByEmployeeCode(String employeeCode) {
        return reportRepository.findByEmployeeCode(employeeCode);
    }

    // 日報保存
    @Transactional
    public ErrorKinds save(Report report) {

        // ログイン中の社員番号を特定(reportエンティティのemployeeプロパティを経由してemployeeのcodeプロパティを取得)
        String employeeCode = report.getEmployee().getCode();
        // 入力した日付を特定
        LocalDate reportDate = report.getReportDate();

        // ログイン中の社員番号と入力日が同じ日報を検索
        Optional<Report> option = reportRepository.findByEmployeeCodeAndReportDate(employeeCode, reportDate);
        // OptionはJavaの構文で、取得できなかった場合はnullを返す
        Report previousReport = option.orElse(null);

        // もし重複した日報が見つかれば
        if (previousReport != null) {

            // ErrorKindsの列挙子がDATECHECK_ERROR(同一日付チェックエラー)であることを返す
            return ErrorKinds.DATECHECK_ERROR;
        }

        // 削除フラグセット（設定値：false）
        report.setDeleteFlg(false);
        // 現在日時を取得し、nowに代入
        LocalDateTime now = LocalDateTime.now();
        // 登録日時セット（設定値：現在日時）
        report.setCreatedAt(now);
        // 更新日時セット（設定値：現在日時）
        report.setUpdatedAt(now);

        // DBに保存
        reportRepository.save(report);
        // ErrorKindsの列挙子がSUCCESSであることをコントローラに戻す
        return ErrorKinds.SUCCESS;
    }

    // 1件を検索
    public Report findById(Integer id) {

        Optional<Report> option = reportRepository.findById(id);
        // OptionはJavaの構文で、取得できなかった場合はnullを返す
        Report report = option.orElse(null);
        return report;
    }

    // 日報削除
    @Transactional
    public void delete(Integer id) {

        Report report = findById(id);
        LocalDateTime now = LocalDateTime.now();
        report.setUpdatedAt(now);
        // 論理削除セット
        report.setDeleteFlg(true);
    }

}
