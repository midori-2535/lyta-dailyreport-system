package com.techacademy.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.SQLRestriction;
import org.hibernate.validator.constraints.Length;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
@Entity
@Table(name = "employees")
@SQLRestriction("delete_flg = false")
public class Employee {

    // Employeeクラス内でRole列挙型(enum)を定義
    public static enum Role {
        //列挙子と日本語のラベルを対応付け
        GENERAL("一般"), ADMIN("管理者");

        // 各Roleに対応する日本語を格納するフィールド
        private String name;

        // 列挙子のコンストラクタ
        private Role(String name) {
            this.name = name;
        }

        // 日本語のラベルを取得するメソッド
        public String getValue() {
            return this.name;
        }
    }

    // 社員番号 ID(主キー）
    @Id
    @Column(length = 10)
    @NotEmpty
    @Length(max = 10)
    private String code;

    // 名前
    @Column(length = 20, nullable = false)
    @NotEmpty
    @Length(max = 20)
    private String name;

    // 権限  VARCHAR(10)でRole列挙型の列挙子は最大10文字まで
    @Column(columnDefinition = "VARCHAR(10)", nullable = false)
    // テーブルのカラムの列挙子を文字列型(ADMIN/GENERAL)に指定
    @Enumerated(EnumType.STRING)
    private Role role;

    // パスワード
    @Column(length = 255, nullable = false)
    private String password;

    // 削除フラグ(論理削除を行うため)
    @Column(columnDefinition = "TINYINT", nullable = false)
    private boolean deleteFlg;

    // 登録日時
    @Column(nullable = false)
    private LocalDateTime createdAt;

    // 更新日時
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // 従業員テーブルと日報テーブルは1:多、親子関係のテーブルである、CascadeType.ALL従業員テーブル(親)全ての操作(従業員削除や更新操作など)を日報テーブル(子)に伝播
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    // 結合先のエンティティが子(複数)
    private List<Report> reportList;
}