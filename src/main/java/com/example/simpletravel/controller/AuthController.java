package com.example.simpletravel.controller;

import com.example.simpletravel.entity.User;
import com.example.simpletravel.entity.VerificationToken;
import com.example.simpletravel.event.SignupEventPublisher;
import com.example.simpletravel.form.SignupForm;
import com.example.simpletravel.service.UserService;
import com.example.simpletravel.service.VerificationTokenService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {
    private final UserService userService;
    private final SignupEventPublisher signupEventPublisher;
    private final VerificationTokenService verificationTokenService;

    public AuthController(UserService userService,
                          SignupEventPublisher signupEventPublisher,
                          VerificationTokenService verificationTokenService) {
        this.userService = userService;
        this.signupEventPublisher = signupEventPublisher;
        this.verificationTokenService = verificationTokenService;
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("signupForm", new SignupForm());
        return "auth/signup";
    }

    @PostMapping("/signup")
    public String signup(@ModelAttribute @Validated SignupForm signupForm,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes,
                         HttpServletRequest httpServletRequest) {
        if (this.userService.isEmailRegistered(signupForm.getEmail())) {
            FieldError fieldError = new FieldError(bindingResult.getObjectName(), "email", "すでに登録済みのメールアドレスです。");
            bindingResult.addError(fieldError);
        }
        if (!this.userService.isSamePassword(signupForm.getPassword(), signupForm.getPasswordConfirmation())) {
            FieldError fieldError = new FieldError(bindingResult.getObjectName(), "password", "パスワードが一致しません。");
            bindingResult.addError(fieldError);
        }
        if (bindingResult.hasErrors()) {
            return "auth/signup";
        }
        User createUser = this.userService.create(signupForm);
        String requestUrl = new String(httpServletRequest.getRequestURL());
        this.signupEventPublisher.publishSignupEvent(createUser, requestUrl);
        redirectAttributes.addFlashAttribute("successMessage", "ご入力いただいたメールアドレスに認証メールを送信しました。メールに記載されているリンクをクリックし、会員登録を完了してください。");
        return "redirect:/";
    }

    @GetMapping("/signup/verify")
    public String verify(@RequestParam(name = "token") String token, Model model) {
        VerificationToken verificationToken = this.verificationTokenService.getVerificationToken(token);

        if (verificationToken != null) {
            User user = verificationToken.getUser();
            userService.enableUser(user);
            String successMessage = "会員登録が完了しました。";
            model.addAttribute("successMessage", successMessage);
        } else {
            String errorMessage = "トークンが無効です。";
            model.addAttribute("errorMessage", errorMessage);
        }
        return "auth/verify";
    }
}
