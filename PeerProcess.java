// public class PeerProcess {

//   // Constant for the configuration file
//   static final String COMMON_CONFIGURATIONS_CFG = "CommonConfigurations.cfg";

//   public static void main(String[] args) throws Exception {
//     // Read configurations from the file
//     Config_common.readConfig(COMMON_CONFIGURATIONS_CFG, "1");

//     // Parse the first argument to get the peer ID
//     int peerID = Integer.parseInt(args[0]);

//     // Start a new thread for CreateThreads with the parsed peer ID
//     new Thread(new CreateThreads(peerID, "1")).start();
//   }
// }







//NEW





public class PeerProcess {

  // Constant representing the path of the configuration file
  private static final String COMMON_CONFIGURATIONS_CFG = "CommonConfigurations.cfg";

  public static void main(String[] args) {
      try {
          // Read common configurations from the file
          Config_common.readConfig(COMMON_CONFIGURATIONS_CFG, "1");

          // Validate and parse the first argument to obtain the peer ID
          if (args.length > 0) {
              int peerID = Integer.parseInt(args[0]);

              // Initialize and start a new thread for CreateThreads class, passing the parsed peer ID
              Thread createThreadsThread = new Thread(new CreateThreads(peerID, "1"));
              createThreadsThread.start();
          } else {
              System.out.println("Please provide the peer ID as the first argument.");
          }
      } catch (Exception e) {
          e.printStackTrace();
          System.out.println("An error occurred: " + e.getMessage());
      }
  }
}
