package com.techacademy.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.techacademy.entity.Employee;
import com.techacademy.entity.Employee.Role;
import com.techacademy.constants.ErrorKinds;
import com.techacademy.constants.ErrorMessage;
import com.techacademy.entity.Report;
import com.techacademy.service.ReportService;
import com.techacademy.service.UserDetail;

@Controller
@RequestMapping("reports")
public class ReportController {

    private final ReportService reportService;

    @Autowired
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

    // 日報詳細画面を表示
    @GetMapping(value = "/{id}/")
    // @PathVariableでパスパラメータ{id}の値をInteger型の変数として取得、Modelのインスタンス化
    public String detail(@PathVariable("id") Integer id, Model model) {

        // サービスにて主キー(id)にて一件の検索結果をreportに代入
        model.addAttribute("report", reportService.findById(id));
        return "reports/detail";
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
    @PostMapping(value = "/add")
    // フォームから送信されたデータをreportエンティティにつめ、@Validatedでエンティティの設定にある入力チェックをし、結果をBindingResultに格納し、モデルのインスタンス化（再表示用）
    public String add(@AuthenticationPrincipal UserDetail userDetail, @Validated Report report, BindingResult res,
            Model model) {

        // 入力チェックにエラーがあるかどうか
        if (res.hasErrors()) {

            // あれば、日報新規登録画面を表示するメソッド呼び出し
            return create(userDetail, model, report);
        }

        // ログイン中の従業員の社員番号を取得(日報テーブル必須項目)するために、userDetailを経由してEmployeeオブジェクトを取得
        Employee employee = userDetail.getEmployee();
        // reportエンティティにemployeeプロパティをセット
        report.setEmployee(employee);

        // サービスsaveメソッドを呼び出し、エラーの種類をresultに格納
        ErrorKinds result = reportService.save(report);

        // もしエラーメッセージクラスの中に該当のエラーの種類があれば
        if (ErrorMessage.contains(result)) {

            // エラーメッセージの名称と値を取得してモデルにセット
            model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
            // 日報新規登録画面を表示するメソッド呼び出し
            return create(userDetail, model, report);
        }

        // 該当のErrorMessageなければ(ErrorKindsの列挙子がSUCCESS)日報一覧画面にリダイレクト
        return "redirect:/reports";
    }

    // 日報更新画面を表示
    @GetMapping(value = "/{id}/update")
    // @PathVariableでパスパラメータ{id}の値をInteger型の変数として取得、Modelのインスタンス化
    public String edit(@PathVariable("id") Integer id, Model model) {
        // サービスにて主キー(id)にて一件の検索結果をreportに代入
        model.addAttribute("report", reportService.findById(id));
        // 日報更新画面へ遷移
        return "reports/update";
    }

    // 日報更新処理
    @PostMapping(value = "/{id}/update")
    // フォームから送信されたデータをreportエンティティにつめ、@Validatedでエンティティの設定にある入力チェックをし、結果をBindingResultに格納し、モデルのインスタンス化（再表示用）
    public String update(@PathVariable("id") Integer id, @Validated Report report, BindingResult res, Model model) {

        // フォームの氏名の値は再送信できないため、再取得(日報IDで該当の日報を検索し、originalReportに代入）
        Report originalReport = reportService.findById(id);
        // originalReportから取得したemployeeプロパティをセットし、Employeeエンティティの全てのプロパティを取得(th:value="${report.employee.name}"に対応させるため)
        report.setEmployee(originalReport.getEmployee());

        // 入力チェックにエラーがあれば
        if (res.hasErrors()) {

            // あれば、モデルにreportのデータを渡す
            model.addAttribute("report", report);
            // モデルの情報を渡して更新画面へ遷移
            return "reports/update";
        }

        // サービスsaveメソッドを呼び出し、エラーの種類をresultに格納
        ErrorKinds result = reportService.update(id, report);

        // もしエラーメッセージクラスの中に該当のエラーの種類があれば
        if (ErrorMessage.contains(result)) {

            // エラーメッセージの名称と値を取得してモデルにセット
            model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
            // 日報更新画面を表示するメソッド呼び出し
            return "reports/update";
        }

        // 該当のErrorMessageなければ(ErrorKindsの列挙子がSUCCESS)日報一覧画面にリダイレクト
        return "redirect:/reports";
    }

    // 日報削除処理
    @PostMapping(value = "/{id}/delete")
    public String delete(@PathVariable("id") Integer id) {

        // サービスのdeleteメソッド呼び出し
        reportService.delete(id);

        // 日報一覧画面へ遷移
        return "redirect:/reports";

    }

}
