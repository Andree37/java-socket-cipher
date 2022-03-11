import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Utils {
  public static String readEncryptedMessage(PrivateKey privateKey, DataInputStream din) {
    // read byte cipher text
    try {
      int byteCipherTextLength;
      byteCipherTextLength = din.readInt();
      byte[] byteCipherText = new byte[byteCipherTextLength];
      din.readFully(byteCipherText, 0, byteCipherText.length);

      // read encrypted key
      int encryptedKeyLength = din.readInt();
      byte[] encryptedKey = new byte[encryptedKeyLength];
      din.readFully(encryptedKey, 0, encryptedKey.length);

      // decrypt key
      Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
      cipher.init(Cipher.PRIVATE_KEY, privateKey);
      byte[] decryptedKey = cipher.doFinal(encryptedKey);

      // decrypt the cipher text
      SecretKey originalKey = new SecretKeySpec(decryptedKey, 0, decryptedKey.length, "AES");
      Cipher aesCipher = Cipher.getInstance("AES");
      aesCipher.init(Cipher.DECRYPT_MODE, originalKey);
      byte[] bytePlainText = aesCipher.doFinal(byteCipherText);
      String plainText = new String(bytePlainText);

      return plainText;
    } catch (IllegalBlockSizeException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (BadPaddingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (InvalidKeyException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (NoSuchAlgorithmException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (NoSuchPaddingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return "";
  }

  public static void sendEncryptedMessage(PublicKey publicKey, SecretKey secKey, String message,
      DataOutputStream dout) {
    try {

      // cipher plain text with secret key
      Cipher aesCipher = Cipher.getInstance("AES");
      aesCipher.init(Cipher.ENCRYPT_MODE, secKey);
      byte[] byteCipherText = aesCipher.doFinal(message.getBytes());

      // encrypt generated key
      Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
      cipher.init(Cipher.PUBLIC_KEY, publicKey);
      byte[] encryptedKey = cipher.doFinal(secKey.getEncoded());

      // send byteCipherText
      dout.writeInt(byteCipherText.length);
      dout.write(byteCipherText);
      dout.flush();

      // send encryptedKey
      dout.writeInt(encryptedKey.length);
      dout.write(encryptedKey);
      dout.flush();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IllegalBlockSizeException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (BadPaddingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (InvalidKeyException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (NoSuchAlgorithmException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (NoSuchPaddingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public static KeyGenerator getGenerator() {
    KeyGenerator generator = null;
    try {
      generator = KeyGenerator.getInstance("AES");

      generator.init(128);
      return generator;
    } catch (NoSuchAlgorithmException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return generator;

  }
}
