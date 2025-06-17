package com.techacademy.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.techacademy.entity.Employee.Role;
import com.techacademy.entity.Report;
import com.techacademy.service.ReportService;
import com.techacademy.service.UserDetail;

@Controller
@RequestMapping("reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    // 日報一覧画面を表示
    @GetMapping // Spring Securityの@AuthenticationPrincipalでログインユーザーの情報を受け取る
    public String list(@AuthenticationPrincipal UserDetail userDetail, Model model) {

        // 日報を格納するリストの宣言
        List<Report> reportList;

        // ログインユーザーが管理者権限を持っているかチェック
        if (Role.ADMIN == (userDetail.getEmployee().getRole())) {

            // 管理者なら他従業員が登録したものを含めた全日報情報を一覧表示
            reportList = reportService.findAll();

            // 一般権限のユーザーの場合、自分が登録した日報情報のみ一覧表示
        } else {

            // userDetailを経由してEmployeeエンティティの社員番号プロパティを取得
            String employeeCode = userDetail.getEmployee().getCode();
            // 社員番号によって自分の日報のみを取得
            reportList = reportService.findByEmployeeCode(employeeCode);

        }

        // 日報の枚数をカウントして件数を"listSize"の名前でモデルにセット
        model.addAttribute("listSize", reportList.size());
        // 日報を全件検索して"reportList"の名前で全件分をモデルにセット
        model.addAttribute("reportList", reportList);
        // 日報一覧画面に遷移
        return "reports/list";

    }

    // 日報新規登録画面を表示
    @GetMapping(value = "/add") // Spring Securityの@AuthenticationPrincipalでログインユーザーの情報を受け取る
    public String create(@AuthenticationPrincipal UserDetail userDetail, Model model, Report report) {

        // userDetailを経由してEmployeeエンティティの氏名プロパティを取得してモデルにセット
        model.addAttribute("name", userDetail.getEmployee().getName());
        // モデルにreportエンティティのインスタンスを生成して(引数で受け取った空データ)をセット
        model.addAttribute("report", report);

        // 日報新規登録画面に遷移
        return "reports/new";
    }

    // 日報新規登録処理
    @PostMapping(value = "add")
    public String add(@AuthenticationPrincipal UserDetail userDetail, @Validated Report report, BindingResult res, Model model) {

        // 入力チェックにエラーがあるかどうか
        if (res.hasErrors()) {
            // エラーのあるReportオブジェクトを再表示するためにモデルに追加
            model.addAttribute("report", report);
            // 日報新規登録画面を表示するメソッド呼び出し
            return create(userDetail, model, report);

        }




        // 日報一覧画面に遷移
        return "reports/list";
    }

}
