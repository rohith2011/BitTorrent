import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class UtilityCls {

  // Static variables
  static HashMap<Integer, byte[]> flDt;
  static HashMap<Integer, BitSet> bitFldMp = new HashMap<>();
  // Constants
  public static final int CHOKE_FLAG = 0;
  public static final int UNCHOKE_FLAG = 1;
  public static final int INTERESTED_FLG = 2;
  public static final int NOT_INTERESTED_FLG = 3;

  // Convert byte array to integer
  public static int transformBytArrToInt(byte[] bA) {
    ByteBuffer buff = ByteBuffer.wrap(bA);
    return buff.getInt();
  }

  // Set bit field
  public static void setBitFld(int pid, int pcIndx) {
    bitFldMp.get(pid).set(pcIndx);
  }

  // Generate message bytes
  public static byte[] testGenerateFunc(
    int tid,
    byte[] plDt,
    int indPiece,
    boolean flg
  ) {
    byte[] messByteArray;
    if (
      tid == CHOKE_FLAG ||
      tid == INTERESTED_FLG ||
      tid == UNCHOKE_FLAG ||
      tid == NOT_INTERESTED_FLG
    ) {
      messByteArray = new byte[5];
      UtilityCls.putIntoBytArr(messByteArray, 1, 0);
      messByteArray[4] = (byte) tid;
      return messByteArray;
    } else {
      return (new byte[0]);
    }
  }

  // Sort download speeds
  public static HashMap<Integer, Double> sortDownSpds(
    HashMap<Integer, Double> map
  ) throws Exception {
    List<Map.Entry<Integer, Double>> linkedList = new LinkedList<>(
      map.entrySet()
    );
    Collections.sort(
      linkedList,
      new Comparator<Map.Entry<Integer, Double>>() {
        public int compare(
          Map.Entry<Integer, Double> on1,
          Map.Entry<Integer, Double> on2
        ) {
          return -1 * (on1.getValue()).compareTo(on2.getValue());
        }
      }
    );
    HashMap<Integer, Double> temporary = new LinkedHashMap<>();
    for (Map.Entry<Integer, Double> sort : linkedList) {
      temporary.put(sort.getKey(), sort.getValue());
    }
    return temporary;
  }

  // Get data chunk
  public static byte[] getDataChunk(int chunkInd) {
    return flDt.get(chunkInd);
  }

  // Main run method

  public static void runMth(
    Socket sock,
    boolean chokedOrNot,
    HashSet<Integer> neighboursPref,
    BitSet piecesRequested,
    int nonchokedPeerOptimal,
    HashMap<Integer, BitSet> bitFldMp,
    HashMap<Integer, Boolean> flWrtnl,
    HashSet<Integer> neighbours,
    int pid,
    int number_of_tot_Downloads,
    HashMap<Integer, Double> endIntrvlDwnSpd
  ) throws Exception {
    DataInputStream inpStream = new DataInputStream(sock.getInputStream());
    DataOutputStream outStream = new DataOutputStream(sock.getOutputStream());
    for (; number_of_tot_Downloads < CreateThreads.peering.size();) {
      int msgSize = 0;
      try {
        msgSize = inpStream.readInt();
      } catch (EOFException e) {
        continue;
      }

      byte[] msgArr = new byte[msgSize];
      double strt = System.currentTimeMillis();
      inpStream.read(msgArr);
      double timer = System.currentTimeMillis() - strt;
      endIntrvlDwnSpd.put(pid, msgSize / timer);
      endIntrvlDwnSpd = sortDownSpds(endIntrvlDwnSpd);
      BitSet piecesRequired = (BitSet) bitFldMp
        .get(CreateThreads.peer_ID)
        .clone();

      chokedOrNot = false;

      piecesRequired = (BitSet) bitFldMp.get(CreateThreads.peer_ID).clone();
      piecesRequired.xor(bitFldMp.get(pid));
      piecesRequired.andNot(bitFldMp.get(CreateThreads.peer_ID));
      if (!(piecesRequired.size() == 0)) {
        int pieceIndex = piecesRequired.nextSetBit(
          new Random().nextInt(piecesRequired.size())
        );
        if (pieceIndex < 0) {
          pieceIndex = piecesRequired.nextSetBit(0);
        }
        if (pieceIndex >= 0) {
          piecesRequested.set(pieceIndex);
          outStream.flush();
        }
      }
      CreateThreads.loggin.logInformation(
        "Peer " +
        CreateThreads.peer_ID +
        " has downloaded the piece from " +
        pid +
        ". Now the number of pieces it has is " +
        flDt.size(),
        0
      );
      if (!(piecesRequired.length() == 0) && !chokedOrNot) {
        int pieceIndex = piecesRequired.nextSetBit(0);
        piecesRequested.set(pieceIndex);
        outStream.flush();
        piecesRequired.andNot(piecesRequested);
      }
    }

    Thread.sleep(5000);
    System.exit(0);
  }

  public static void wrtFl() throws Exception {
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

  public static byte[] transformIntoBytArr(int integer) {
    int i = 4;
    byte[] aftcnvrsion = ByteBuffer.allocate(i).putInt(integer).array();
    return aftcnvrsion;
  }

  public static void putIntoBytArr(
    byte[] byteArr,
    int intConvert,
    int startingInd
  ) {
    byte[] byteLength = UtilityCls.transformIntoBytArr(intConvert);
    int i = 0;
    while (i < 4) {
      byteArr[startingInd + i] = byteLength[i];
      i++;
    }
  }

  public static String returnStrFrmByts(
    byte[] byteArray,
    int lw,
    int i,
    Integer ind
  ) {
    int arrSize = i - lw + 1;
    boolean a = arrSize <= 0 || i >= byteArray.length;

    if (a == true) {
      return "";
    }

    byte[] opStrArr = new byte[arrSize];
    System.arraycopy(byteArray, lw, opStrArr, 0, arrSize);

    return new String(opStrArr, StandardCharsets.UTF_8);
  }

  public static ArrayList<String> returnLinesFromFile(
    String fileName,
    boolean isPresent
  ) throws IOException {
    ArrayList<String> LinsArr = new ArrayList<>();
    BufferedReader bufferedReader = new BufferedReader(
      new FileReader(fileName)
    );
    String everyLine = bufferedReader.readLine();

    for (; everyLine != null;) {
      LinsArr.add(everyLine);
      everyLine = bufferedReader.readLine();
    }

    bufferedReader.close();
    return LinsArr;
  }
}
