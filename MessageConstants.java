import java.util.Arrays;

public class MessageConstants {

  public int idtype; // Identifier for message type
  public int pieceOfIndex; // Index for piece of data
  public byte[] payloadData; // Actual data in payload

  public static final int CHOKE_FLAG = 0;
  public static final int UNCHOKE_FLAG = 1;
  public static final int INTERESTED_FLG = 2;
  public static final int NOT_INTERESTED_FLG = 3;

  // Generate a message based on the type_identifier and other parameters
  public static byte[] generate(
    int type_identifier,
    byte[] payloadData,
    int pieceOfIndex,
    boolean flag
  ) {
    byte[] messByteArray; // The byte array for the message

    // Check if the message type is one of the known types
    if (
      type_identifier == CHOKE_FLAG ||
      type_identifier == INTERESTED_FLG ||
      type_identifier == UNCHOKE_FLAG ||
      type_identifier == NOT_INTERESTED_FLG
    ) {
      messByteArray = new byte[5]; // Initialize the byte array
      UtilityCls.putIntoBytArr(messByteArray, 1, 0); // Utility function call
      messByteArray[4] = (byte) type_identifier; // Set the message type in the byte array
      return messByteArray; // Return the message byte array
    }

    // If the code reaches here, no known message type matched
    return null; // Returning null as the method must return something
  }
}
//Change names