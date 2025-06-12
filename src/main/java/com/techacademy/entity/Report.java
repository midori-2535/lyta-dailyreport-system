package com.techacademy.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "reports")
@SQLRestriction("delete_flg = false")
public class Report {

    // ID
    @Id
    @Column
    private Integer id;

    // 日付
    @Column
    private LocalDate report_date;

    // タイトル
    @Column(length = 100, nullable = false)
    private String title;

    // 内容
    @Column(columnDefinition="LONGTEXT", nullable = false)
    private String content;

    // 社員番号
    @Column(length = 10, nullable = false)
    private String employee_code;

    // 削除フラグ(論理削除を行うため)
    @Column(columnDefinition="TINYINT", nullable = false)
    private boolean deleteFlg;

    // 登録日時
    @Column(nullable = false)
    private LocalDateTime createdAt;

    // 更新日時
    @Column(nullable = false)
    private LocalDateTime updatedAt;

}
