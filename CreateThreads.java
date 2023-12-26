import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

class CreateThreads implements Runnable {

  static final String PEER_INFO_PATH = "PeerInformation.cfg";

  static int peer_ID;
  static TreeMap<Integer, PrCls> peering;
  static LoggingUtility loggin;
  static HashMap<Integer, byte[]> file_Data;

  public CreateThreads(int id_of_peer, String type) throws Exception {
    CreateThreads.peer_ID = id_of_peer;
    peering = readingPeersInform("1");

    // Creating Peer directories

    try {
      FileProcessingUtility.generateNewDirectories(
        Integer.parseInt("peer_" + id_of_peer),
        Config_common.nameOfTheFile,
        "1"
      );
      loggin = new LoggingUtility(String.valueOf(CreateThreads.peer_ID));
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  @Override
  public void run() {
    new Thread(new Startup_Clnt()).start();
    new Thread(new StartupServer()).start();
    new Thread(new DeterminePreferredNeighbors()).start();
    new Thread(new RemoveChokeFromNeighbours()).start();
  }

  private class StartupServer implements Runnable {

    byte[] handshakePacket = new byte[32];

    @Override
    public void run() {
      try {
        int port_ = peering.get(peer_ID).portnum;
        ServerSocket server_Sockt = new ServerSocket(port_);
        loggin.logInformation(
          "Server: " +
          CreateThreads.peer_ID +
          " Started on Port Number :" +
          port_,
          0
        );
        boolean new_Peering_ = false;
        for (Map.Entry<Integer, PrCls> neigb : peering.entrySet()) {
          if (new_Peering_) {
            Socket sockt = server_Sockt.accept();
            ObjectInputStream serverInputStream = new ObjectInputStream(
              sockt.getInputStream()
            );
            ObjectOutputStream serverOutputStream = new ObjectOutputStream(
              sockt.getOutputStream()
            );

            serverInputStream.read(handshakePacket);

            serverOutputStream.write(MsgManager.makeHandShake(peer_ID, 0));
            serverOutputStream.flush();

            loggin.logInformation(
              "Peer :" + peer_ID + " makes a connection to" + neigb.getKey(),
              0
            );
            neigb.getValue().beginMsgExchange(sockt);
          }
          if (peer_ID == neigb.getKey()) new_Peering_ = true;
        }

        server_Sockt.close();
      } catch (Exception exc) {
        exc.printStackTrace();
      }
    }
  }

  private class Startup_Clnt implements Runnable {

    @Override
    public void run() {
      try {
        for (Map.Entry<Integer, PrCls> peer : peering.entrySet()) {
          if (peer.getKey() == peer_ID) break;
          PrCls neigb = peer.getValue();
          Socket sockt = new Socket(neigb.nameOfHost, neigb.portnum);

          ObjectOutputStream clientOutputStream = new ObjectOutputStream(
            sockt.getOutputStream()
          );
          ObjectInputStream clientInputStream = new ObjectInputStream(
            sockt.getInputStream()
          );

          byte[] handshakePacket = MsgManager.makeHandShake(peer_ID, 0);
          clientOutputStream.write(handshakePacket);
          clientOutputStream.flush();

          clientInputStream.readFully(handshakePacket);
          String messageHeader = UtilityCls.returnStrFrmByts(
            handshakePacket,
            0,
            17,
            0
          );
          String messagePeerID = UtilityCls.returnStrFrmByts(
            handshakePacket,
            28,
            31,
            0
          );

          if (
            messageHeader.equals("P2PFILESHARINGPROJ") &&
            Integer.parseInt(messagePeerID) == peer.getKey()
          ) {
            neigb.beginMsgExchange(sockt);
          } else {
            sockt.close();
          }
        }
      } catch (IOException exception) {
        exception.printStackTrace();
      }
    }
  }

  public static TreeMap<Integer, PrCls> readingPeersInform(String type)
    throws Exception {
    ArrayList<String> lines = UtilityCls.returnLinesFromFile(
      PEER_INFO_PATH,
      true
    );
    TreeMap<Integer, PrCls> peers_info = new TreeMap<>();
    for (String line : lines) {
      String[] wordings = line.split(" ");
      peers_info.put(
        Integer.valueOf(wordings[0]),
        new PrCls(
          Integer.parseInt(wordings[0]),
          wordings[1],
          Integer.valueOf(wordings[2]),
          Integer.parseInt(wordings[3])
        )
      );
    }
    return peers_info;
  }
}
