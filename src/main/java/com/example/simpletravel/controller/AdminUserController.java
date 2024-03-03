package com.example.simpletravel.controller;

import com.example.simpletravel.entity.User;
import com.example.simpletravel.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {
    private final UserRepository userRepository;

    public AdminUserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public String index(@RequestParam(name = "keyword", required = false) String keyword,
                        @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable,
                        Model model) {
        Page<User> userPage;

        if (keyword != null && !keyword.isEmpty()) {
            userPage = this.userRepository.findByNameLikeOrFuriganaLike("%" + keyword + "%", "%" + keyword + "%", pageable);
        } else {
            userPage = this.userRepository.findAll(pageable);
        }
        model.addAttribute("userPage", userPage);
        model.addAttribute("keyword", keyword);
        return "admin/users/index";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable(name = "id") Integer id, Model model) {
        User user = this.userRepository.getReferenceById(id);
        model.addAttribute("user", user);
        return "admin/users/show";
    }
}