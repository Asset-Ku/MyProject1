package com.example.demo.controller;

import com.example.demo.entity.Users;
import com.example.demo.entity.dto.CaptchaResponseDto;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.util.StringUtils;

import javax.validation.Valid;
import java.util.Collections;
import java.util.Map;

@Controller
@RequestMapping("/")
public class RegistrationController {
    @Autowired
    private UserService userService;

    @Value("${recaptcha.secret}")
    private String secret;
    @Autowired
    private RestTemplate restTemplate;
    private final static String CAPTCHA_URL = "https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s";

    @GetMapping("/registration")
    public String registration() {
        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(
            @RequestParam("password2") String passwordConfirm,
            @RequestParam("g-recaptcha-response") String captchaResponce,
            @Valid Users users,
            BindingResult bindingResult,
            Model model
    ) {
        String url = String.format(CAPTCHA_URL, secret, captchaResponce);
        CaptchaResponseDto responseDto = new CaptchaResponseDto();
        responseDto.setSuccess(Boolean.TRUE);
//        restTemplate.postForObject(url, Collections.emptyList(), CaptchaResponseDto.class);

        if (!responseDto.isSuccess()) {
            model.addAttribute("captchaError", "Введите капчу");
            return "registration";
        }
        boolean isConfirmEmpty = StringUtils.isEmpty(passwordConfirm);
        if (isConfirmEmpty) {
            model.addAttribute("password2Error", "Поле 'Подтверждение пароля' должно быть заполнено!");
            return "registration";
        }
        if (users.getPassword() != null && !users.getPassword().equals(passwordConfirm)) {
            model.addAttribute("passwordError", "Пароли не совпадают!");
            return "registration";
        }
        if (isConfirmEmpty || bindingResult.hasErrors() || !responseDto.isSuccess()) {
            Map<String, String> errors = ControllerUtils.getErrors(bindingResult);
            model.mergeAttributes(errors);
            return "registration";
        }
        if (!userService.addUser(users)) {
            model.addAttribute("usernameError", "User exists!");
            return "registration";
        }
        model.addAttribute("message", "Активационный код выслан вам на email!");
        return "success";
    }

    @GetMapping("/activate/{code}")
    public String activate(
            Model model,
            @PathVariable String code
    ) {
        boolean isActivated = userService.activateUser(code);
        if (isActivated) {
            model.addAttribute("messageType", "success");
            model.addAttribute("message", "Пользователь активирован!");
        } else {
            model.addAttribute("messageType", "danger");
            model.addAttribute("message", "Активационный код не найден!");
        }
        return "login";
    }
}
