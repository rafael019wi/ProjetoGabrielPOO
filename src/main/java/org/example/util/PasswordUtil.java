package org.example.util;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.HexFormat;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordUtil {
    private static final String ALGORITMO = "PBKDF2WithHmacSHA256";
    private static final String PREFIXO = "pbkdf2-sha256";

    private static final int ITERACOES = 600_000;

    private static final int TAMANHO_SALT = 16;  // 128 bits
    private static final int TAMANHO_HASH = 32;  // 256 bits

    // cria um objeto que sabe converter bytes para texto hexadecimal e vice-versa
    private static final HexFormat HEX = HexFormat.of();

    public static String createHash(String password) {
        // o salt é um valor aleatório único por senha. Ele garante que duas pessoas com a mesma senha tenham hashes diferentes
        byte[] salt = generateSalt();
        byte[] hash = calculateHash(password, salt);

        return PREFIXO + "$" + ITERACOES + "$" + HEX.formatHex(salt) + "$" + HEX.formatHex(hash);
    }

    public static boolean checkHash (String password, String passwordDB) {
        if (password == null || passwordDB == null) {
            return false;
        }

        try {
            String[] partes = passwordDB.split("\\$");

            if (!validFormat(partes)) {
                return false;
            }

            byte[] salt = HEX.parseHex(partes[2]);
            byte[] hashSalvo = HEX.parseHex(partes[3]);

            if (salt.length != TAMANHO_SALT || hashSalvo.length != TAMANHO_HASH) {
                return false;
            }

            byte[] hashCalculado = calculateHash(password, salt);

            // compara as duas hashes
            return MessageDigest.isEqual(hashSalvo, hashCalculado);

        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static boolean validFormat(String[] parts) {
        return parts.length == 4 && parts[0].equals(PREFIXO) && parts[1].equals(String.valueOf(ITERACOES));
    }

    private static byte[] generateSalt() {
        byte[] salt = new byte[TAMANHO_SALT];
        new SecureRandom().nextBytes(salt); // aleatório criptograficamente seguro
        return salt;
    }

    private static byte[] calculateHash(String password, byte[] salt) {
        int tamanhoEmBits = TAMANHO_HASH * 8;
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERACOES, tamanhoEmBits);

        try {
            return SecretKeyFactory.getInstance(ALGORITMO).generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Erro ao gerar hash da senha", e);
        } finally {
            // apaga a senha da memória assim que termina de usar
            spec.clearPassword();
        }
    }
}