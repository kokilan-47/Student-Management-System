package com.example.nithuinstitueapp.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordUtilTest {

    @Test
    void verifiesCorrectPassword() {
        String stored = PasswordUtil.hash("secret123");
        assertTrue(PasswordUtil.verify("secret123", stored));
    }

    @Test
    void rejectsWrongPassword() {
        String stored = PasswordUtil.hash("secret123");
        assertFalse(PasswordUtil.verify("secret124", stored));
    }

    @Test
    void samePasswordProducesDifferentHashes() {
        assertNotEquals(PasswordUtil.hash("secret123"), PasswordUtil.hash("secret123"));
    }

    @Test
    void rejectsPlainTextOrMalformedStoredValues() {
        assertFalse(PasswordUtil.verify("admin123", "admin123"));
        assertFalse(PasswordUtil.verify("admin123", "pbkdf2$abc$def$ghi"));
        assertFalse(PasswordUtil.verify("admin123", null));
    }

    @Test
    void seedHashesInSchemaMatchDocumentedPasswords() {
        assertTrue(PasswordUtil.verify("admin123",
                "pbkdf2$210000$6Gzugu8fdcnc+Jk+kgYFTg==$Rj2vBVhGJVntiUa6/fuxNDgkqNbDTiq+IoW7K4pvypA="));
        assertTrue(PasswordUtil.verify("staff123",
                "pbkdf2$210000$L5qzX+MN+ZF3wCJoo7bekw==$SG7sJ0L+eqrzyAbiGfhoODY/gktxXo3jsrQ1B7vfIMA="));
    }
}
