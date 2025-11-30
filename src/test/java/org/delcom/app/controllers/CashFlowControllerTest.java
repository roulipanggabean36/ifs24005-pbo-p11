package org.delcom.app.controllers;

import org.delcom.app.dto.CashFlowForm;
import org.delcom.app.entities.CashFlow;
import org.delcom.app.services.CashFlowService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CashFlowControllerTest {

    @Mock
    private CashFlowService cashFlowService;

    @InjectMocks
    private CashFlowController cashFlowController;

    // 1. Test Fitur Simpan (Dari Modal Tambah)
    @Test
    void testSaveCashFlow() {
        // Siapkan data form dummy
        CashFlowForm form = new CashFlowForm();
        form.setDescription("Gaji Bulanan");
        form.setAmount(5000000.0);
        form.setType("INCOME");
        form.setDate(LocalDate.now());

        // Jalankan method saveCashFlow
        String viewName = cashFlowController.saveCashFlow(form);

        // Verifikasi: Harus redirect ke halaman utama (/)
        assertEquals("redirect:/", viewName);
        
        // Verifikasi: Service save harus terpanggil
        verify(cashFlowService).saveCashFlow(any(CashFlow.class));
    }

    // 2. Test Fitur Update (Dari Modal Edit)
    @Test
    void testUpdateCashFlow() {
        // Siapkan data form dummy dengan ID
        CashFlowForm form = new CashFlowForm();
        form.setId(10L); // ID ada berarti Update
        form.setDescription("Beli Makan Siang");
        form.setAmount(25000.0);
        form.setType("EXPENSE");
        form.setDate(LocalDate.now());

        // Jalankan method updateCashFlow
        String viewName = cashFlowController.updateCashFlow(form);

        // Verifikasi: Harus redirect ke halaman utama (/)
        assertEquals("redirect:/", viewName);
        
        // Verifikasi: Service save harus terpanggil
        verify(cashFlowService).saveCashFlow(any(CashFlow.class));
    }

    // 3. Test Fitur Hapus (Dari Modal Delete)
    @Test
    void testDeleteCashFlow() {
        // Siapkan data form dummy (cukup ID saja)
        CashFlowForm form = new CashFlowForm();
        form.setId(5L);

        // Jalankan method deleteCashFlow
        String viewName = cashFlowController.deleteCashFlow(form);

        // Verifikasi: Harus redirect ke halaman utama (/)
        assertEquals("redirect:/", viewName);
        
        // Verifikasi: Service delete harus terpanggil dengan ID yang benar
        verify(cashFlowService).deleteCashFlow(5L);
    }
}