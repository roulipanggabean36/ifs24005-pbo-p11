package org.delcom.app.services;

import org.delcom.app.entities.CashFlow;
import org.delcom.app.repositories.CashFlowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CashFlowService {

    @Autowired
    private CashFlowRepository repository;

    public List<CashFlow> getAllCashFlows() {
        return repository.findAll();
    }

    public void saveCashFlow(CashFlow cashFlow) {
        repository.save(cashFlow);
    }

    public CashFlow getCashFlowById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public void deleteCashFlow(Long id) {
        repository.deleteById(id);
    }

    // --- LOGIKA HITUNG TOTAL ---
    public double getTotalIncome() {
        return getAllCashFlows().stream()
                .filter(c -> "INCOME".equals(c.getType()))
                .mapToDouble(CashFlow::getAmount)
                .sum();
    }

    public double getTotalExpense() {
        return getAllCashFlows().stream()
                .filter(c -> "EXPENSE".equals(c.getType()))
                .mapToDouble(CashFlow::getAmount)
                .sum();
    }

    public double getBalance() {
        return getTotalIncome() - getTotalExpense();
    }
}