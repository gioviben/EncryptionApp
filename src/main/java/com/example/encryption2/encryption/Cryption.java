package com.example.encryption2.encryption;


import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;


public class Cryption {
    Path input;
    Path output;
    private String EncryptionKey;

    public void setEncryptionKey(String encryptionKey) {
        EncryptionKey = encryptionKey;
    }

    public void encrypt() throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
            byte[] fileBytes = new byte[0];
            fileBytes = Files.readAllBytes(input);
            SecretKeySpec secretKey = new SecretKeySpec(EncryptionKey.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(fileBytes);
            FileOutputStream outputStream = new FileOutputStream(output.toFile());
            outputStream.write(encryptedBytes);
            outputStream.close();
    }

    public void decrypt() throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
            byte[] encryptedBytes = Files.readAllBytes(input);
            SecretKeySpec secretKey = new SecretKeySpec(EncryptionKey.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            FileOutputStream outputStream = new FileOutputStream(output.toFile());
            outputStream.write(decryptedBytes);
            outputStream.close();
    }

    public Path getInput() {
        return input;
    }

    public void setInput(Path input) {
        this.input = input;
    }

    public Path getOutput() {
        return output;
    }

    public void setOutput(Path output) {
        this.output = output;
    }
}
