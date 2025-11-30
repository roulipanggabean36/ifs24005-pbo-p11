package org.delcom.app.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ConstUtilTest {

    @Test
    void testConstants() {
        // Panggil constructor untuk coverage
        assertNotNull(new ConstUtil());

        // Cek value
        assertEquals("AUTH_TOKEN", ConstUtil.KEY_AUTH_TOKEN);
        assertEquals("USER_ID", ConstUtil.KEY_USER_ID);
        assertEquals("pages/auth/login", ConstUtil.TEMPLATE_PAGES_AUTH_LOGIN);
        assertEquals("pages/auth/register", ConstUtil.TEMPLATE_PAGES_AUTH_REGISTER);
        assertEquals("pages/home", ConstUtil.TEMPLATE_PAGES_HOME);
        assertEquals("pages/todos/detail", ConstUtil.TEMPLATE_PAGES_TODOS_DETAIL);
    }
}