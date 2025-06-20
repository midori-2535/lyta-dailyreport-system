package com.techacademy.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.entity.Employee;
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
    public List<Report> findByEmployee(Employee employee) {
        return reportRepository.findByEmployee(employee);
    }

    // 日報保存
    @Transactional
    public ErrorKinds save(Report report) {

        // 新規登録画面にログイン中の社員番号を特定
        Employee employee = report.getEmployee();
        // 入力した日付を特定
        LocalDate reportDate = report.getReportDate();

        // ログイン中の社員番号と入力日が同じ日報を検索
        Optional<Report> option = reportRepository.findByEmployeeAndReportDate(employee, reportDate);
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

    // 日報更新
    @Transactional
    public ErrorKinds update(Integer id, Report report) {

        // 更新対象の元のデータを取得(DBから一件検索した結果をoriginalReportに代入）
        Report originalReport = findById(id);

        // もし更新フォームで日付を変更したら重複チェック(reportの日付とoriginalReportの日付の不一致)
        if (!(report.getReportDate().equals(originalReport.getReportDate()))) {

            // 更新画面で表示中の社員番号を特定
            Employee employee = report.getEmployee();
            // 入力した日付を取得
            LocalDate reportDate = report.getReportDate();
            // 表示中の社員番号と入力日が同じ日報を検索(過去に作成済みの日報の中に日付が重複した日報がないかDBをチェック)
            Optional<Report> option = reportRepository.findByEmployeeAndReportDate(employee, reportDate);
            // OptionはJavaの構文で、取得できなかった場合はnullを返す
            Report previousReport = option.orElse(null);

            // もし重複した日報が見つかれば
            if ((previousReport != null)) {

                // もし同じ日付があれば重複のため、ErrorKindsの列挙子がDATECHECK_ERROR(同一日付チェックエラー)であることを返す
                return ErrorKinds.DATECHECK_ERROR;
            }

        }

        // 作成日時をemployeeにセット（DB保存時のgetCreatedAtのnullエラー防止のため)
        report.setCreatedAt(originalReport.getCreatedAt());
        // 削除フラグをreportにセット（null防止、boolean型のためgetではなくis)
        report.setDeleteFlg(report.isDeleteFlg());
        // 現在日時を取得し、nowに代入
        LocalDateTime now = LocalDateTime.now();
        // 更新日時セット（設定値：現在日時）
        report.setUpdatedAt(now);

        // リポジトリのsaveメソッドを呼び出し、データベースに保存
        reportRepository.save(report);

        // ErrorKindsの列挙子がSUCCESSであることをコントローラに戻す
        return ErrorKinds.SUCCESS;
    }

    // 日報削除
    @Transactional
    public void delete(Integer id) {

        Report report = findById(id);
        // 現在日時を取得し、nowに代入
        LocalDateTime now = LocalDateTime.now();
        // 更新日時セット（設定値：現在日時）
        report.setUpdatedAt(now);
        // 論理削除セット
        report.setDeleteFlg(true);
    }

    // IDによって1件を検索
    public Report findById(Integer id) {

        Optional<Report> option = reportRepository.findById(id);
        // OptionはJavaの構文で、取得できなかった場合はnullを返す
        Report report = option.orElse(null);
        return report;
    }

}
