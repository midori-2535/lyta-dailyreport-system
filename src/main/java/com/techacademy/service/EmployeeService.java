package com.techacademy.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.entity.Employee;
import com.techacademy.repository.EmployeeRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    public EmployeeService(EmployeeRepository employeeRepository, PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 従業員保存
    @Transactional
    public ErrorKinds save(Employee employee) {

        // パスワードチェック
        ErrorKinds result = employeePasswordCheck(employee);
        if (ErrorKinds.CHECK_OK != result) {
            return result;
        }

        // 従業員番号重複チェック
        if (findByCode(employee.getCode()) != null) {
            return ErrorKinds.DUPLICATE_ERROR;
        }

        employee.setDeleteFlg(false);

        LocalDateTime now = LocalDateTime.now();
        employee.setCreatedAt(now);
        employee.setUpdatedAt(now);

        employeeRepository.save(employee);
        return ErrorKinds.SUCCESS;
    }

    // 従業員更新
    @Transactional
    public ErrorKinds update(String code, Employee employee) {

        // findByCodeでDBから一件検索した結果をoriginalEmployeeに代入
        Employee originalEmployee = findByCode(code);
        // 作成日時をemployeeに設定（更新画面作成時のgetCreatedAtのnullエラー防止のため)
        employee.setCreatedAt(originalEmployee.getCreatedAt());
        // 削除フラグをemployeeに設定（null防止、boolean型のためgetではなくis)
        employee.setDeleteFlg(originalEmployee.isDeleteFlg());

        // パスワードが空文字" "かどうかをチェック、getPassword()はemployeeエンティティの@Dataで生成
        if ("".equals(employee.getPassword())) {

            // パスワードが空だった場合、 DBに設定済みの値を設定する(originalEmployeeの中からパスワードを取得し、employeeに設定)
            employee.setPassword(originalEmployee.getPassword());

            // パスワードが空でない場合は画面入力値が暗号化された値を設定
        } else {

            // パスワードチェック(employeePasswordCheckメソッド(パスワードの暗号化も行う)を呼び出し、エラー結果をresultに代入)
            ErrorKinds result = employeePasswordCheck(employee);
            // ErrorKindsがチェックOKでなければ、エラーの種類をコントローラに返す
            if (ErrorKinds.CHECK_OK != result) {
                return result;
            }
        }

        // 現在日時を取得し、nowに代入
        LocalDateTime now = LocalDateTime.now();
        // 更新日時（設定値：現在日時）をemployeeに設定
        employee.setUpdatedAt(now);

        // リポジトリのsaveメソッドを呼び出し、データベースに保存
        employeeRepository.save(employee);

        // ErrorKindsの列挙子がSUCCESSであることをコントローラに戻す
        return ErrorKinds.SUCCESS;
    }

    // 従業員削除
    @Transactional
    public ErrorKinds delete(String code, UserDetail userDetail) {

        // 自分を削除しようとした場合はエラーメッセージを表示
        if (code.equals(userDetail.getEmployee().getCode())) {
            return ErrorKinds.LOGINCHECK_ERROR;
        }
        Employee employee = findByCode(code);
        LocalDateTime now = LocalDateTime.now();
        employee.setUpdatedAt(now);
        employee.setDeleteFlg(true);
        // 論理削除セット
        return ErrorKinds.SUCCESS;
    }

    // 従業員一覧表示処理
    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    // 1件を検索
    public Employee findByCode(String code) {
        // findByIdで検索
        Optional<Employee> option = employeeRepository.findById(code);
        // OptionはJavaの構文で、取得できなかった場合はnullを返す
        Employee employee = option.orElse(null);
        return employee;
    }

    // 従業員パスワードチェック
    private ErrorKinds employeePasswordCheck(Employee employee) {

        // 従業員パスワードの半角英数字チェック処理
        if (isHalfSizeCheckError(employee)) {
            // YES(正規表現にマッチしなかった)なのでHALFSIZE_ERRORを返す
            return ErrorKinds.HALFSIZE_ERROR;
        }

        // 従業員パスワードの8文字～16文字チェック処理
        if (isOutOfRangePassword(employee)) {
            // YES(8文字未満、あるいは16文字超え)なのでRANGECHECK_ERRORを返す
            return ErrorKinds.RANGECHECK_ERROR;
        }
        // パスワードのハッシュ化
        employee.setPassword(passwordEncoder.encode(employee.getPassword()));

        return ErrorKinds.CHECK_OK;
    }

    // 従業員パスワードが半角英数字のみで構成されているかをチェック
    private boolean isHalfSizeCheckError(Employee employee) {

        // 半角英数字を正規表現パターンとして生成
        Pattern pattern = Pattern.compile("^[A-Za-z0-9]+$");
        // 従業員のパスワードが正規表現パターンにマッチするかチェック
        Matcher matcher = pattern.matcher(employee.getPassword());
        // パターンにマッチしたかどうかの結果の逆を返す(マッチしなかったらYESを返す)
        return !matcher.matches();
    }

    // 従業員パスワードが8文字～16文字かチェック
    public boolean isOutOfRangePassword(Employee employee) {

        // 従業員のパスワードの桁数を測定
        int passwordLength = employee.getPassword().length();
        // 測定結果が8文字未満、あるいは16文字超えならYESを返す
        return passwordLength < 8 || 16 < passwordLength;
    }

}
