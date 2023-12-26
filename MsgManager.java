// import java.nio.ByteBuffer;
// import java.nio.charset.StandardCharsets;

// public class MsgManager {

//   // Function to create a message
//   public static byte[] makeMsg(int typOfMsg, byte[] load, int indexOfPiece) {
//     // Call the generate method from MessageConstants to create the message
//     return MessageConstants.generate(typOfMsg, load, indexOfPiece, true);
//   }

//   // Function to make a handshake message
//   public static byte[] makeHandShake(int pid, int num) {
//     byte[] hsPack = new byte[32]; // Handshake packet

//     // Convert header and zeros to bytes
//     byte[] hsHead = "P2PFILESHARINGPROJ".getBytes();
//     byte[] zerosAppend = "0000000000".getBytes();
//     // Convert peer ID to bytes
//     byte[] pidInFormOfBytes = ByteBuffer
//       .allocate(4)
//       .put(String.valueOf(pid).getBytes())
//       .array();

//     int index = 0;
//     int in = 0;

//     // Add the header to the handshake packet
//     for (; in < hsHead.length; in++, index++) {
//       hsPack[index] = hsHead[in];
//     }
//     // Add the zeros to the handshake packet
//     for (in = 0; in < zerosAppend.length; in++, index++) {
//       hsPack[index] = zerosAppend[in];
//     }

//     // Add the peer ID to the handshake packet
//     for (in = 0; in < pidInFormOfBytes.length; in++, index++) {
//       hsPack[index] = pidInFormOfBytes[in];
//     }

//     // Log the handshake packet for debugging
//     System.out.println(
//       "Packet after the Hand Shake is--- " +
//       new String(hsPack, StandardCharsets.UTF_8)
//     );

//     return hsPack; // Return the handshake packet
//   }
// }









//NEW





import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class MsgManager {

    // Method to create a general message
    public static byte[] makeMsg(int typOfMsg, byte[] load, int indexOfPiece) {
        return MessageConstants.generate(typOfMsg, load, indexOfPiece, true);
    }

    // Method to create a handshake message
    public static byte[] makeHandShake(int pid, int num) {
        byte[] hsPack = new byte[32];  // Handshake packet of fixed size 32 bytes

        // Convert predefined handshake header and zeros to bytes
        byte[] hsHead = "P2PFILESHARINGPROJ".getBytes(StandardCharsets.UTF_8);
        byte[] zerosAppend = "0000000000".getBytes(StandardCharsets.UTF_8);

        // Convert peer ID to 4-byte array
        byte[] pidInFormOfBytes = ByteBuffer.allocate(4)
                .put(String.valueOf(pid).getBytes(StandardCharsets.UTF_8))
                .array();

        int index = 0;

        // System.arraycopy is generally more efficient than a loop for copying arrays
        System.arraycopy(hsHead, 0, hsPack, index, hsHead.length);
        index += hsHead.length;

        System.arraycopy(zerosAppend, 0, hsPack, index, zerosAppend.length);
        index += zerosAppend.length;

        System.arraycopy(pidInFormOfBytes, 0, hsPack, index, pidInFormOfBytes.length);

        // Debugging log to check the handshake packet
        System.out.println("Packet after the Hand Shake is--- " + new String(hsPack, StandardCharsets.UTF_8));

        return hsPack;
    }
}
