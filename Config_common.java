// import java.io.IOException;
// import java.util.ArrayList;

// public class Config_common {

//   static int preferredNeighborCount;
//   static int unchoking_Interval;
//   static int optimistic_Unchoking_Interval;
//   static String nameOfTheFile;
//   static int file_size_parameter;

//   // static int getPieceSize;
//   // static int countTotalPieces;

//   public static void readConfig(String filead, String type) throws IOException {
//     int zer = 0;
//     int on = 1;

//     ArrayList<String> l = UtilityCls.returnLinesFromFile(filead, true);
//     preferredNeighborCount = Integer.parseInt(l.get(zer).split(" ")[on]);
//     unchoking_Interval = Integer.parseInt(l.get(on).split(" ")[on]);
//     optimistic_Unchoking_Interval = Integer.parseInt(l.get(2).split(" ")[on]);
//     nameOfTheFile = "peer_" + l.get(3 * on).split(" ")[on];
//     file_size_parameter = Integer.parseInt(l.get(4).split(" ")[on]);
//     // getPieceSize = Integer.parseInt(l.get(5).split(" ")[on]);
//     // countTotalPieces = (int) Math.ceil((double) file_size_parameter / getPieceSize);
//   }
// }
// // Config_common.java
// // DeterminePreferredNeighbors.java
// // LoggingUtility.java
// // RemoveChokeFromNeighbours.java



//NEW


import java.io.IOException;
import java.util.List;

public class Config_common {
    static int preferredNeighborCount;
    static int unchoking_Interval;
    static int optimistic_Unchoking_Interval;
    static String nameOfTheFile;
    static int file_size_parameter;

    public static void readConfig(String filePath, String type) throws IOException {
        List<String> lines = UtilityCls.returnLinesFromFile(filePath, true);

        // Using constant index values for readability
        final int ZERO = 0;
        final int ONE = 1;

        String[] tokens;

        tokens = lines.get(ZERO).split(" ");
        preferredNeighborCount = Integer.parseInt(tokens[ONE]);

        tokens = lines.get(ONE).split(" ");
        unchoking_Interval = Integer.parseInt(tokens[ONE]);

        tokens = lines.get(2).split(" ");
        optimistic_Unchoking_Interval = Integer.parseInt(tokens[ONE]);

        tokens = lines.get(3 * ONE).split(" ");
        nameOfTheFile = "peer_" + tokens[ONE];

        tokens = lines.get(4).split(" ");
        file_size_parameter = Integer.parseInt(tokens[ONE]);

        // Uncomment below lines if you need these variables
        // tokens = lines.get(5).split(" ");
        // getPieceSize = Integer.parseInt(tokens[ONE]);
        // countTotalPieces = (int) Math.ceil((double) file_size_parameter / getPieceSize);
    }
}
