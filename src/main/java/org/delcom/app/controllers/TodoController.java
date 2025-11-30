package org.delcom.app.controllers;

import org.delcom.app.dto.TodoForm;
import org.delcom.app.entities.User;
import org.delcom.app.services.CashFlowService;
import org.delcom.app.services.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.UUID;

@Controller
public class TodoController {

    @Autowired
    private TodoService todoService;

    @Autowired
    private CashFlowService cashFlowService;

    // Helper untuk cek login
    private boolean isAuthenticated(Authentication auth) {
        return auth != null &&
               auth.isAuthenticated() &&
               !(auth instanceof AnonymousAuthenticationToken) &&
               auth.getPrincipal() instanceof User;
    }

    // --- HALAMAN HOME ---
    @GetMapping("/")
    public String index(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (!isAuthenticated(auth)) {
            return "redirect:/auth/logout";
        }

        User user = (User) auth.getPrincipal();
        model.addAttribute("auth", user);

        model.addAttribute("todos", todoService.getAllTodos(user.getId(), null));
        model.addAttribute("listCashFlow", cashFlowService.getAllCashFlows());

        // Fitur Baru: Ringkasan
        model.addAttribute("totalIncome", cashFlowService.getTotalIncome());
        model.addAttribute("totalExpense", cashFlowService.getTotalExpense());
        model.addAttribute("totalBalance", cashFlowService.getBalance());

        model.addAttribute("addTodoModalOpen", false);
        model.addAttribute("editTodoModalOpen", false);
        model.addAttribute("deleteTodoModalOpen", false);
        model.addAttribute("todoForm", new TodoForm());

        // PERBAIKAN: Ubah dari "pages/todos/home" menjadi "pages/home"
        return "pages/home";
    }

    // --- ADD TODO ---
    @PostMapping("/todos")
    public String addTodo(@ModelAttribute("todoForm") TodoForm form) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (isAuthenticated(auth)) {
            User user = (User) auth.getPrincipal();
            todoService.createTodo(user.getId(), form.getTitle(), form.getDescription());
        }
        return "redirect:/";
    }

    // --- EDIT TODO ---
    @PostMapping("/todos/{id}/edit")
    public String editTodo(@PathVariable UUID id, @ModelAttribute("todoForm") TodoForm form) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (isAuthenticated(auth)) {
            User user = (User) auth.getPrincipal();
            todoService.updateTodo(user.getId(), id, form.getTitle(), form.getDescription(), form.getFinished());
        }
        return "redirect:/";
    }

    // --- DELETE TODO ---
    @GetMapping("/todos/{id}/delete")
    public String deleteTodo(@PathVariable UUID id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (isAuthenticated(auth)) {
            User user = (User) auth.getPrincipal();
            todoService.deleteTodo(user.getId(), id);
        }
        return "redirect:/";
    }
}