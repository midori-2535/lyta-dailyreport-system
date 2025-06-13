package com.techacademy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("reports")
public class ReportController {

    // 日報一覧画面
    @GetMapping
    public String list() {

        return "reports/list";

    }
}
