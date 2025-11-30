package org.delcom.app.services;

import org.delcom.app.entities.CashFlow;
import org.delcom.app.repositories.CashFlowRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CashFlowServiceTest {

    @Mock
    private CashFlowRepository repository;

    @InjectMocks
    private CashFlowService service;

    @Test
    void testGetAllCashFlows() {
        CashFlow cf = new CashFlow();
        when(repository.findAll()).thenReturn(Arrays.asList(cf));

        List<CashFlow> result = service.getAllCashFlows();
        Assertions.assertEquals(1, result.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    void testSaveCashFlow() {
        CashFlow cf = new CashFlow();
        service.saveCashFlow(cf);
        verify(repository, times(1)).save(cf);
    }

    @Test
    void testGetCashFlowById() {
        Long id = 1L;
        CashFlow cf = new CashFlow();
        cf.setId(id);
        
        when(repository.findById(id)).thenReturn(Optional.of(cf));

        CashFlow result = service.getCashFlowById(id);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(id, result.getId());
    }

    @Test
    void testGetCashFlowById_NotFound() {
        Long id = 1L;
        when(repository.findById(id)).thenReturn(Optional.empty());

        CashFlow result = service.getCashFlowById(id);
        Assertions.assertNull(result);
    }

    @Test
    void testDeleteCashFlow() {
        Long id = 1L;
        service.deleteCashFlow(id);
        verify(repository, times(1)).deleteById(id);
    }

    // --- BAGIAN INI WAJIB ADA AGAR COVERAGE 100% ---

    @Test
    void testCalculations() {
        // Setup Data
        CashFlow income1 = new CashFlow();
        income1.setType("INCOME");
        income1.setAmount(1000.0);

        CashFlow income2 = new CashFlow();
        income2.setType("INCOME");
        income2.setAmount(500.0);

        CashFlow expense1 = new CashFlow();
        expense1.setType("EXPENSE");
        expense1.setAmount(300.0);

        CashFlow other = new CashFlow(); // Case tipe null/salah
        other.setType("UNKNOWN");
        other.setAmount(100.0);

        // Mock
        when(repository.findAll()).thenReturn(Arrays.asList(income1, income2, expense1, other));

        // Income: 1000 + 500 = 1500
        Assertions.assertEquals(1500.0, service.getTotalIncome());

        // Expense: 300
        Assertions.assertEquals(300.0, service.getTotalExpense());

        // Balance: 1500 - 300 = 1200
        Assertions.assertEquals(1200.0, service.getBalance());
    }

    @Test
    void testCalculations_Empty() {
        when(repository.findAll()).thenReturn(Collections.emptyList());
        Assertions.assertEquals(0.0, service.getTotalIncome());
        Assertions.assertEquals(0.0, service.getTotalExpense());
        Assertions.assertEquals(0.0, service.getBalance());
    }
}