package com.techacademy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.constants.ErrorMessage;

import com.techacademy.entity.Employee;
import com.techacademy.service.EmployeeService;
import com.techacademy.service.UserDetail;

@Controller
@RequestMapping("employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // 従業員一覧画面を表示
    @GetMapping
    public String list(Model model) {

        // 従業員数をカウントして件数をモデルに登録
        model.addAttribute("listSize", employeeService.findAll().size());
        // 従業員を全件検索してモデルに登録
        model.addAttribute("employeeList", employeeService.findAll());

        return "employees/list";
    }

    // 従業員詳細画面を表示
    @GetMapping(value = "/{code}/")
    public String detail(@PathVariable("code") String code, Model model) {

        model.addAttribute("employee", employeeService.findByCode(code));
        return "employees/detail";
    }

    // 従業員新規登録画面を表示
    @GetMapping(value = "/add")
    public String create(@ModelAttribute Employee employee) {

        return "employees/new";
    }

    // 従業員新規登録処理
    @PostMapping(value = "/add")
    public String add(@Validated Employee employee, BindingResult res, Model model) {

        // パスワード空白チェック
        /*
         * エンティティ側の入力チェックでも実装は行えるが、更新の方でパスワードが空白でもチェックエラーを出さずに
         * 更新出来る仕様となっているため上記を考慮した場合に別でエラーメッセージを出す方法が簡単だと判断
         */
        if ("".equals(employee.getPassword())) {
            // パスワードが空白だった場合
            model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.BLANK_ERROR),
                    ErrorMessage.getErrorValue(ErrorKinds.BLANK_ERROR));

            return create(employee);

        }

        // もし入力チェックでエラーがあれば、
        if (res.hasErrors()) {
            // 従業員新規登録画面を表示するメソッド呼び出し
            return create(employee);
        }

        // 論理削除を行った従業員番号を指定すると例外となるためtry~catchで対応
        // (findByIdでは削除フラグがTRUEのデータが取得出来ないため)
        try {
            ErrorKinds result = employeeService.save(employee);

            if (ErrorMessage.contains(result)) {
                model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
                return create(employee);
            }

        } catch (DataIntegrityViolationException e) {
            model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.DUPLICATE_EXCEPTION_ERROR),
                    ErrorMessage.getErrorValue(ErrorKinds.DUPLICATE_EXCEPTION_ERROR));
            return create(employee);
        }

        return "redirect:/employees";
    }

    // 従業員更新画面を表示
    @GetMapping(value = "/{code}/update")
    // @PathVariableでパスパラメータ{code}の値をString型の変数として取得、Modelのインスタンス化
    public String edit(@PathVariable("code") String code, Model model) {
        // サービスにて主キー(code)にて一件の検索結果をemployeeに代入
        Employee employee = employeeService.findByCode(code);
        // パスワードを空文字にし、画面に非表示にする
        employee.setPassword("");
        // Modelに"employee"という名前で、employeeのデータを格納
        model.addAttribute("employee", employee);
        // 更新画面へ遷移
        return "employees/update";
    }

    // 従業員更新処理
    @PostMapping(value = "/{code}/update")
    // フォームから送信されたデータをEmployeeエンティティにつめ、@Validatedでエンティティの設定にある入力チェックをし、結果をBindingResultに格納し、モデルのインスタンス化（再表示用）
    public String update(@PathVariable("code") String code, @Validated Employee employee, BindingResult res,
            Model model) {

        // 入力チェックにエラーがあるかどうか
        if (res.hasErrors()) {
            // あれば、モデルにemployeeのデータを渡す
            model.addAttribute("employee", employee);
            // モデルの情報を渡して更新画面へ遷移
            return "employees/update";
        }

        // サービスのupdateメソッドを呼び出し、ErrorKindsの列挙子をresultに代入
        ErrorKinds result = employeeService.update(code, employee);

        // エラーメッセージクラスの中にErrorKindsの列挙子があるかどうか(ErrorMessageクラスcontainsメソッド)をチェック
        if (ErrorMessage.contains(result)) {
            // あれば、エラーメッセージの名称(getErrorNameメソッド)と値(getErrorValueメソッド)を取得してモデルに渡す
            model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
            // モデルの情報を渡して更新画面へ遷移
            return "employees/update";
        }

        // 従業員一覧画面へリダイレクト
        return "redirect:/employees";
    }

    // 従業員削除処理
    @PostMapping(value = "/{code}/delete") // Spring Securityの@AuthenticationPrincipalでログインユーザーの情報を取得
    public String delete(@PathVariable("code") String code, @AuthenticationPrincipal UserDetail userDetail,
            Model model) {

        ErrorKinds result = employeeService.delete(code, userDetail);

        if (ErrorMessage.contains(result)) {
            model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
            model.addAttribute("employee", employeeService.findByCode(code));
            return detail(code, model);
        }

        return "redirect:/employees";
    }

}
