import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

class Client {
  public static void main(String args[]) throws Exception {
    Socket s = new Socket("localhost", 3333);
    DataInputStream din = new DataInputStream(s.getInputStream());
    DataOutputStream dout = new DataOutputStream(s.getOutputStream());
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    PrivateKey clientPrivate = PrivateKeyReader.get("client_private.key");
    PublicKey serverPublic = PublicKeyReader.get("server_public.key");

    KeyGenerator generator = Utils.getGenerator();

    String clientMessage = "";
    while (!clientMessage.equals("stop")) {
      // generate a new secret key each message
      SecretKey secKey = generator.generateKey();

      clientMessage = br.readLine();
      Utils.sendEncryptedMessage(serverPublic, secKey, clientMessage, dout);
      String plainText = Utils.readEncryptedMessage(clientPrivate, din);
      System.out.println("Server echo: " + plainText);
    }

    dout.close();
    s.close();
  }
}