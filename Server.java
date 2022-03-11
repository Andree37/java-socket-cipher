import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

class Server {

  public static void main(String args[]) throws Exception {
    ServerSocket ss = new ServerSocket(3333);
    Socket s = ss.accept();
    DataInputStream din = new DataInputStream(s.getInputStream());
    DataOutputStream dout = new DataOutputStream(s.getOutputStream());

    PrivateKey serverPrivate = PrivateKeyReader.get("server_private.key");
    PublicKey clientPublic = PublicKeyReader.get("client_public.key");
    KeyGenerator generator = Utils.getGenerator();

    String clientMessage = "";
    while (!clientMessage.equals("stop")) {
      // generate a new secret key each message
      SecretKey secKey = generator.generateKey();

      // Let's check the signature
      clientMessage = Utils.readEncryptedMessage(serverPrivate, din);
      System.out.println("Client Message: " + clientMessage);
      Utils.sendEncryptedMessage(clientPublic, secKey, clientMessage, dout);
    }
    din.close();
    s.close();
    ss.close();
  }
}