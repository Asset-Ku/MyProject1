package com.example.demo.controller;

import com.example.demo.entity.Role;
import com.example.demo.entity.Users;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public String userList(Model model) {
        model.addAttribute("users", userService.findAll());
        return "userList";
    }

    @GetMapping("{user}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String userEditForm(@PathVariable Users user, Model model) {
        model.addAttribute("users", user);
        model.addAttribute("roles", Role.values());
        return "userEdit";
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public String userSave(
            @RequestParam String username,
            @RequestParam Map<String, String> form,
            @RequestParam("userId") Users users
    ) {
        userService.saveUser(username, form, users);
        return "redirect:/user";
    }

    @GetMapping("profile")
    public String getProfile(
            Model model,
            @AuthenticationPrincipal Users users
    ) {
        model.addAttribute("username", users.getUsername());
        model.addAttribute("email", users.getEmail());
        return "profile";
    }

    @PostMapping("profile")
    public String updateProfile(
            @AuthenticationPrincipal Users users,
            @RequestParam String password,
            @RequestParam String email,
            Model model
    ) {
        userService.updateProfile(users, password, email);
        model.addAttribute("message", "Данные успешно изменены!");
        return "profile";
    }

    @GetMapping("subscribe/{users}")
    public String subscribe(
            @AuthenticationPrincipal Users currentUser,
            @PathVariable Users users
    ) {
        userService.subscribe(currentUser, users);
        return "redirect:/user-messages/" + users.getId();
    }

    @GetMapping("unsubscribe/{users}")
    public String ubsubscribe(
            @AuthenticationPrincipal Users currentUser,
            @PathVariable Users users
    ) {
        userService.unsubscribe(currentUser, users);
        return "redirect:/user-messages/" + users.getId();
    }

    @GetMapping("{type}/{user}/list")
    public String userList(
            Model model,
            @PathVariable Users user,
            @PathVariable String type
    ) {
        model.addAttribute("userChannel", user);
        model.addAttribute("type",type);
        if ("subscriptions".equals(type)){
            model.addAttribute("users",user.getSubscriptions());
        }else {
            model.addAttribute("users",user.getSubscribers());
        }
        return "subscriptions";
    }
}
