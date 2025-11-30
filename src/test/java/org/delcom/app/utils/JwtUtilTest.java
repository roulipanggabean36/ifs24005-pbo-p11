package org.delcom.app.utils;

import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    @Test
    void testConstructor() {
        assertNotNull(new JwtUtil());
    }

    @Test
    void testGetKey() {
        assertNotNull(JwtUtil.getKey());
    }

    @Test
    void testGenerateAndExtractToken() {
        UUID userId = UUID.randomUUID();
        String token = JwtUtil.generateToken(userId);
        
        assertNotNull(token);
        assertFalse(token.isEmpty());

        UUID extractedId = JwtUtil.extractUserId(token);
        assertEquals(userId, extractedId);
    }

    @Test
    void testExtractUserId_InvalidToken() {
        UUID result = JwtUtil.extractUserId("invalid.token.here");
        assertNull(result);
    }

    @Test
    void testValidateToken_Success() {
        UUID userId = UUID.randomUUID();
        String token = JwtUtil.generateToken(userId);
        
        assertTrue(JwtUtil.validateToken(token, false));
    }

    @Test
    void testValidateToken_Invalid() {
        assertFalse(JwtUtil.validateToken("invalid.token", false));
    }
}