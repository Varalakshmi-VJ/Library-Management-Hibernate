package com.example.library.controller;

import com.example.library.model.Member;
import com.example.library.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/members")
    public String list(Model model) {
        model.addAttribute("members", memberService.listWithIssues());
        model.addAttribute("active", "members");
        return "members";
    }

    @GetMapping("/members/new")
    public String newForm(Model model) {
        model.addAttribute("member", new Member());
        model.addAttribute("active", "members");
        return "member-form";
    }

    @PostMapping("/members/save")
    public String save(@Valid @ModelAttribute("member") Member member, BindingResult result,
                       Model model, RedirectAttributes ra) {
        if (!result.hasFieldErrors("email") && memberService.emailTaken(member.getEmail())) {
            result.rejectValue("email", "duplicate", "This email is already registered");
        }
        if (result.hasErrors()) {
            model.addAttribute("active", "members");
            return "member-form";
        }
        memberService.save(member);
        ra.addFlashAttribute("success", "Member added");
        return "redirect:/members";
    }
}
