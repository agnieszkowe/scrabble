package com.example.scrabble.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.interfaces.PBEKey;
import javax.crypto.spec.PBEKeySpec;
import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Objects;

public class CreateAccController {


    @FXML
    private TextField nickTextField;

    @FXML
    private TextField passTextField;

    @FXML
    private TextField confirmPassTextField;

    @FXML
    private Button submitButton;

    public void initialize() {

        submitButton.setOnAction(event -> {
            String nick = nickTextField.getText();
            String password = passTextField.getText();
            String confirmPass = confirmPassTextField.getText();
            if (!nick.contains(";") && !password.contains(";")){
                if (password.length() >= 8) {
                    if (password.equals(confirmPass)) {
                        createAccount();
                    }
                }
            }
        });

    }

    private void createAccount(){
        BufferedWriter writer;
        try {
            String password = passTextField.getText();
            int iterations = 1000;
            char[] chars = password.toCharArray();
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            byte[] salt = new byte[16];
            sr.nextBytes(salt);
            PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 64 * 8);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] hash = skf.generateSecret(spec).getEncoded();
            String strongPassword = iterations + ":" + toHex(salt) + ":" + toHex(hash);
            writer = new BufferedWriter((new OutputStreamWriter(
                    new FileOutputStream("src/main/resources/com/example/scrabble/database.txt", true), "UTF-8")));
            writer.write(nickTextField.getText()+";"+strongPassword+";"+"0"+";");
            writer.newLine();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }

    }

    private String toHex(byte[] array) throws NoSuchAlgorithmException
    {
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);

        int paddingLength = (array.length * 2) - hex.length();
        if(paddingLength > 0)
        {
            return String.format("%0"  +paddingLength + "d", 0) + hex;
        }else{
            return hex;
        }
    }

}