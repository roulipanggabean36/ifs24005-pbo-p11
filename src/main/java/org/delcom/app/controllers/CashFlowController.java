package org.delcom.app.controllers;

import org.delcom.app.dto.CashFlowForm;
import org.delcom.app.entities.CashFlow;
import org.delcom.app.services.CashFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/cashflow")
public class CashFlowController {

    @Autowired
    private CashFlowService service;

    @PostMapping("/save")
    public String saveCashFlow(@ModelAttribute CashFlowForm form) {
        CashFlow cashFlow = new CashFlow();
        // Mapping manual agar aman
        cashFlow.setDescription(form.getDescription());
        cashFlow.setAmount(form.getAmount());
        cashFlow.setType(form.getType());
        cashFlow.setDate(form.getDate());
        
        service.saveCashFlow(cashFlow);
        return "redirect:/";
    }

    @PostMapping("/update")
    public String updateCashFlow(@ModelAttribute CashFlowForm form) {
        CashFlow cashFlow = new CashFlow();
        cashFlow.setId(form.getId());
        cashFlow.setDescription(form.getDescription());
        cashFlow.setAmount(form.getAmount());
        cashFlow.setType(form.getType());
        cashFlow.setDate(form.getDate());
        
        service.saveCashFlow(cashFlow);
        return "redirect:/";
    }

    @PostMapping("/delete")
    public String deleteCashFlow(@ModelAttribute CashFlowForm form) {
        service.deleteCashFlow(form.getId());
        return "redirect:/";
    }
}