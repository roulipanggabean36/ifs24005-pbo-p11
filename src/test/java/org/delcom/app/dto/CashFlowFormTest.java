package org.delcom.app.dto;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class CashFlowFormTest {

    @Test
    void testCashFlowForm() {
        Long id = 1L;
        String desc = "Bayar Listrik";
        Double amount = 50000.0;
        String type = "EXPENSE";
        LocalDate date = LocalDate.now();

        // [PENTING] Bagian ini memperbaiki Error Merah di gambar kamu
        // Kita memanggil Constructor Lengkap
        CashFlowForm form = new CashFlowForm(id, desc, amount, type, date);
        
        // Test Getter untuk memastikan Constructor bekerja
        assertEquals(id, form.getId());
        assertEquals(desc, form.getDescription());
        assertEquals(amount, form.getAmount());
        assertEquals(type, form.getType());
        assertEquals(date, form.getDate());

        // Test Setter & Constructor Kosong
        CashFlowForm emptyForm = new CashFlowForm();
        emptyForm.setId(2L);
        emptyForm.setDescription("Gaji");
        emptyForm.setAmount(1000000.0);
        emptyForm.setType("INCOME");
        emptyForm.setDate(date);
        
        assertEquals(2L, emptyForm.getId());
        assertEquals("Gaji", emptyForm.getDescription());
        assertEquals(1000000.0, emptyForm.getAmount());
        assertEquals("INCOME", emptyForm.getType());
        assertEquals(date, emptyForm.getDate());
    }
}