package org.delcom.app.entities;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;

public class CashFlowTest {

    @Test
    void testCashFlowEntity() {
        Long id = 1L;
        String desc = "Makan Siang";
        Double amount = 15000.0;
        String type = "EXPENSE";
        LocalDate date = LocalDate.now();

        // Test Constructor Kosong & Setter
        CashFlow cf = new CashFlow();
        cf.setId(id);
        cf.setDescription(desc);
        cf.setAmount(amount);
        cf.setType(type);
        cf.setDate(date);

        // Test Getter
        Assertions.assertEquals(id, cf.getId());
        Assertions.assertEquals(desc, cf.getDescription());
        Assertions.assertEquals(amount, cf.getAmount());
        Assertions.assertEquals(type, cf.getType());
        Assertions.assertEquals(date, cf.getDate());

        // Test Constructor Parameter
        CashFlow cfParam = new CashFlow(desc, amount, type, date);
        Assertions.assertEquals(desc, cfParam.getDescription());
    }
}