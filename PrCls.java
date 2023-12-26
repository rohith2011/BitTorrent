import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class PrCls {

  // Class-level static variables
  static int number_of_tot_Downloads = 0;
  static HashSet<Integer> neighbours = new HashSet<>();
  static HashSet<Integer> neighboursPref = new HashSet<>();
  static int nonchokedPeerOptimal;
  static HashMap<Integer, Double> endIntrvlDwnSpd = new HashMap<>();
  static HashMap<Integer, BitSet> bitFldMp = new HashMap<>();
  static HashMap<Integer, Boolean> chkngMp = new HashMap<>();
  static HashMap<Integer, Boolean> flWrtnl = new HashMap<>();
  static BitSet piecesRequested;
  static HashMap<Integer, byte[]> flDt;

  // Instance variables
  int pid;
  String nameOfHost;
  int portnum;
  int fileBool;
  Socket sock;
  boolean chokedOrNot;

  // Constructor
  public PrCls(int pid, String nameOfHost, int portnum, int fileBool)
    throws Exception {
    this.pid = pid;
    this.nameOfHost = nameOfHost;
    this.portnum = portnum;
    this.fileBool = fileBool;
    if (this.fileBool != 1 && (pid == CreateThreads.peer_ID)) {
      flDt = new HashMap<>();
      flWrtnl.put(CreateThreads.peer_ID, false);
    }
  }

  // Start message exchange with given Socket
  public void beginMsgExchange(Socket sock) {
    this.sock = sock;
    new Thread(new MsgExc(sock)).start();
  }

  public void beginMsgExchange(Socket sock, String sockN) {
    this.sock = sock;
    new Thread(new MsgExc(sock)).start();
  }

  // Set BitSet fields for peer ID
  public void setBitFlds(int pid, byte[] bytes) {
    bitFldMp.put(pid, BitSet.valueOf(bytes));
  }

  public void setBitFld(int pid, int pcIndx) {
    bitFldMp.get(pid).set(pcIndx);
  }

  // Nested class for handling message exchange
  class MsgExc implements Runnable {

    Socket sock;

    public MsgExc(Socket sock) {
      this.sock = sock;
    }

    @Override
    public void run() {
      synchronized (this) {
        try {
          DataInputStream inpStream = new DataInputStream(
            sock.getInputStream()
          );
          DataOutputStream outStream = new DataOutputStream(
            sock.getOutputStream()
          );

          // Main loop for message handling
          while (number_of_tot_Downloads < CreateThreads.peering.size()) {
            int msgSize = inpStream.readInt();
            byte[] msgArr = new byte[msgSize];
            double strt = System.currentTimeMillis();
            inpStream.read(msgArr);
            double timer = System.currentTimeMillis() - strt;
            endIntrvlDwnSpd.put(pid, msgSize / timer);
            endIntrvlDwnSpd = sortDownSpds(endIntrvlDwnSpd);

            chokedOrNot = false;
          }
          Thread.sleep(5000);
          System.exit(0);
        } catch (SocketException so) {
          System.out.println("Socket connection was closed with " + pid);
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    }
  }

  // Utility functions
  public static byte[] getDataChunk(int chunkInd) {
    return flDt.get(chunkInd);
  }

  public static void writeChnkDt(int chunkInd, byte[] dt) {
    flDt.put(chunkInd, dt);
  }

  public void wrtFl() throws Exception {
    flDt = FileProcessingUtility.sort_file_data(flDt, "1");
    File fl = new File("./" + CreateThreads.peer_ID + "/thefile");
    if (fl.createNewFile()) {
      FileWriter flWrter = new FileWriter(
        "./" + CreateThreads.peer_ID + "/" + Config_common.nameOfTheFile,
        true
      );
      BufferedWriter buffWrter = new BufferedWriter(flWrter);

      for (HashMap.Entry<Integer, byte[]> ent : flDt.entrySet()) {
        buffWrter.write(new String(ent.getValue(), StandardCharsets.UTF_8));
      }
      buffWrter.close();
      flWrter.close();
    }
  }

  // Sorting download speeds
  public static HashMap<Integer, Double> sortDownSpds(
    HashMap<Integer, Double> map
  ) throws Exception {
    List<Map.Entry<Integer, Double>> linkedList = new LinkedList<>(
      map.entrySet()
    );
    Collections.sort(
      linkedList,
      (on1, on2) -> -1 * (on1.getValue()).compareTo(on2.getValue())
    );

    HashMap<Integer, Double> temporary = new LinkedHashMap<>();
    for (Map.Entry<Integer, Double> sort : linkedList) {
      temporary.put(sort.getKey(), sort.getValue());
    }
    return temporary;
  }
}