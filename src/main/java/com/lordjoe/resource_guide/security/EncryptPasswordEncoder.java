package com.lordjoe.resource_guide.security;

import com.lordjoe.utilities.Encrypt;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Custom PasswordEncoder that matches encrypted strings using com.lordjoe.utilities.Encrypt.
 * It assumes the raw password must be encrypted before comparison.
 */
public class EncryptPasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(CharSequence rawPassword) {
        return Encrypt.encryptString(rawPassword.toString());
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encryptedPassword) {
        try {
            String encryptedAttempt = Encrypt.encryptString(rawPassword.toString());
            return encryptedPassword.equals(encryptedAttempt);
        } catch (Exception e) {
            return false;
        }
    }
}

