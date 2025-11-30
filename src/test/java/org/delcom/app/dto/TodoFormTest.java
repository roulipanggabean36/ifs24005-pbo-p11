package org.delcom.app.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.UUID;

public class TodoFormTest {

    @Test
    void testTodoFormComplete() {
        // 1. Test Constructor Lengkap
        UUID id = UUID.randomUUID();
        String title = "Judul Test";
        String description = "Deskripsi Test";
        boolean finished = true;
        String confirmTitle = "Judul Test";

        TodoForm form = new TodoForm(id, title, description, finished, confirmTitle);

        // 2. Test Getters
        Assertions.assertEquals(id, form.getId());
        Assertions.assertEquals(title, form.getTitle());
        Assertions.assertEquals(description, form.getDescription());
        Assertions.assertTrue(form.getFinished());
        Assertions.assertEquals(confirmTitle, form.getConfirmTitle());

        // 3. Test Helper Method (PENTING untuk coverage)
        Assertions.assertTrue(form.getIsFinished());

        // 4. Test Setters
        UUID newId = UUID.randomUUID();
        form.setId(newId);
        form.setTitle("Judul Baru");
        form.setDescription("Deskripsi Baru");
        form.setFinished(false);
        form.setConfirmTitle("Judul Baru");

        Assertions.assertEquals(newId, form.getId());
        Assertions.assertEquals("Judul Baru", form.getTitle());
        Assertions.assertEquals("Deskripsi Baru", form.getDescription());
        Assertions.assertFalse(form.getFinished());
        Assertions.assertEquals("Judul Baru", form.getConfirmTitle());

        // 5. Test Setter Helper
        form.setIsFinished(true);
        Assertions.assertTrue(form.getFinished());
    }

    @Test
    void testTodoFormEmptyConstructor() {
        // Test Constructor Kosong
        TodoForm form = new TodoForm();
        Assertions.assertNull(form.getId());
        Assertions.assertNull(form.getTitle());
    }
}