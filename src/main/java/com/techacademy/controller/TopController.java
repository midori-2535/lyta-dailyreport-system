
package com.techacademy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TopController {

    // ログイン画面表示
    @GetMapping(value = "/login")
    public String login() {
        return "login/login";
    }

    // ログイン後のトップページ表示 (ログイン成功時は、日報一覧画面に当たる「/reports」にリダイレクト)
    @GetMapping(value = "/")
    public String top() {
        return "redirect:/reports";
    }

}
