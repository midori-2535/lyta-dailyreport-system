package com.techacademy.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name = "reports")
// データ検索のとき、論理削除されていないレコードだけを検索できる(delete_flgがfalseのものだけをSQLのwhere句に自動設定)
@SQLRestriction("delete_flg = false")
public class Report {

    // ID (主キー)
    @Id // null禁止、UNIQUE制約で重複禁止含
    // @GeneratedValueで主キーの値を一意に自動生成 (GenerationTypeでAUTO_INCREMENTを設定)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // MYSQLに対応する変数の型を指定(Integerはnull許容と汎用性のため)
    private Integer id;

    // 日付
    @Column(nullable = false)
    @NotNull // 入力チェックでnull(データが存在しない)を確認
    // MYSQLのデータ型DATEに対応するエンティティのデータ型を設定
    private LocalDate reportDate;

    // タイトル
    @Column(length = 100, nullable = false) // データべ－スにnullを禁止
    @NotEmpty // 入力チェックで空文字がないか確認
    private String title;

    // 内容
    // MYSQLの特定のデータ型LONGTEXTに対応させるためにcolumnDefinitionを設定
    @Column(columnDefinition = "LONGTEXT", nullable = false)
    @NotEmpty // 入力チェックで空文字がないか確認
    private String content;

    // 削除フラグ(論理削除を行うため)
    // MYSQLの特定のデータ型TINYINTに対応させるためにcolumnDefinitionを設定
    @Column(columnDefinition = "TINYINT", nullable = false)
    private boolean deleteFlg;

    // 登録日時
    @Column(nullable = false)
    private LocalDateTime createdAt;

    // 更新日時
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // 日報テーブルと従業員テーブルは多:1のリレーション
    @ManyToOne
    // 日報テーブルのemployee_codeは外部キーで、従業員テーブルのcodeと紐づき、必ず存在しなければならない(codeのないemployee_codeは存在しない)
    @JoinColumn(name = "employee_code", referencedColumnName = "code", nullable = false)
    // 結合先のエンティティ
    private Employee employee;

}
