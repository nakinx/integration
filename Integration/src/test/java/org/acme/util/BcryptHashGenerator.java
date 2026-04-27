package org.acme.util;

import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

/**
 * Utilitário para gerar hashes BCrypt de senhas.
 */
@QuarkusTest
public class BcryptHashGenerator {

    @Test
    void generateHashes() {
        String[] passwords = {"admin123", "user123"};
        
        System.out.println("\n=== BCrypt Hash Generator ===\n");
        
        for (String password : passwords) {
            String hash = BcryptUtil.bcryptHash(password);
            System.out.println("Password: " + password);
            System.out.println("BCrypt Hash: " + hash);
            System.out.println("Verificação: " + BcryptUtil.matches(password, hash));
            System.out.println();
        }
    }
}
