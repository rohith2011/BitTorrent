// import java.io.DataOutputStream;
// import java.net.Socket;
// import java.net.SocketException;
// import java.util.HashSet;
// import java.util.Random;

// class RemoveChokeFromNeighbours implements Runnable {

//   // Default constructor
//   public RemoveChokeFromNeighbours() {}

//   @Override
//   public void run() {
//     synchronized (this) {
//       try {
//         // Main loop to remove choke from neighbors
//         while (PrCls.number_of_tot_Downloads < CreateThreads.peering.size()) {
//           // Create candidate sets for preferred and non-preferred neighbors
//           HashSet<Integer> preffered_Candidates = new HashSet<>(
//             PrCls.neighbours
//           );
//           HashSet<Integer> preffered_Clone = new HashSet<>(
//             PrCls.neighboursPref
//           );
//           preffered_Candidates.removeAll(preffered_Clone);

//           Random random = new Random();

//           // If candidate set is not empty, select one randomly
//           if (preffered_Candidates.size() > 0) {
//             int interestdNeighbourChosen = random.nextInt(
//               preffered_Candidates.size()
//             );
//             PrCls.nonchokedPeerOptimal =
//               (int) preffered_Candidates.toArray()[interestdNeighbourChosen];

//             // Update the status of the chosen neighbor to be unchoked
//             PrCls.chkngMp.put(PrCls.nonchokedPeerOptimal, false);
//             Socket sock = CreateThreads.peering.get(PrCls.nonchokedPeerOptimal)
//               .sock;
//             if (sock == null) {
//               break;
//             }

//             // Send an UNCHOKE message to the chosen neighbor
//             DataOutputStream OutputDataString = new DataOutputStream(
//               sock.getOutputStream()
//             );
//             OutputDataString.write(
//               MsgManager.makeMsg(MessageConstants.UNCHOKE_FLAG, null, -1)
//             );
//             OutputDataString.flush();
//           }

//           // Log information about the operation
//           CreateThreads.loggin.logInformation(
//             "Peer " +
//             CreateThreads.peer_ID +
//             " has the optimistically unchoked neighbor " +
//             PrCls.nonchokedPeerOptimal,
//             0
//           );

//           // Sleep for a certain interval before the next iteration
//           Thread.sleep(Config_common.optimistic_Unchoking_Interval * 1000);
//         }
//       } catch (SocketException ex) {
//         // Handle socket exceptions
//       } catch (Exception ex) {
//         // Handle other exceptions
//         ex.printStackTrace();
//       }
//     }
//   }
// }





//NEW


import java.io.DataOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashSet;
import java.util.Random;

class RemoveChokeFromNeighbours implements Runnable {

    @Override
    public void run() {
        synchronized (this) {
            try {
                mainLoop();
            } catch (SocketException ex) {
                // Handle socket exceptions
            } catch (Exception ex) {
                // Handle other exceptions
                ex.printStackTrace();
            }
        }
    }

    private void mainLoop() throws Exception {
        while (PrCls.number_of_tot_Downloads < CreateThreads.peering.size()) {
            HashSet<Integer> prefferedCandidates = createPreferredCandidateSet();
            unchokeRandomPreferredNeighbor(prefferedCandidates);

            logUnchokedNeighbor();
            Thread.sleep(Config_common.optimistic_Unchoking_Interval * 1000);
        }
    }

    private HashSet<Integer> createPreferredCandidateSet() {
        HashSet<Integer> prefferedCandidates = new HashSet<>(PrCls.neighbours);
        HashSet<Integer> prefferedClone = new HashSet<>(PrCls.neighboursPref);
        prefferedCandidates.removeAll(prefferedClone);
        return prefferedCandidates;
    }

    private void unchokeRandomPreferredNeighbor(HashSet<Integer> prefferedCandidates) throws Exception {
        if (prefferedCandidates.size() > 0) {
            int interestdNeighbourChosen = new Random().nextInt(prefferedCandidates.size());
            PrCls.nonchokedPeerOptimal = (int) prefferedCandidates.toArray()[interestdNeighbourChosen];
            PrCls.chkngMp.put(PrCls.nonchokedPeerOptimal, false);

            Socket sock = CreateThreads.peering.get(PrCls.nonchokedPeerOptimal).sock;
            if (sock != null) {
                DataOutputStream OutputDataString = new DataOutputStream(sock.getOutputStream());
                OutputDataString.write(MsgManager.makeMsg(MessageConstants.UNCHOKE_FLAG, null, -1));
                OutputDataString.flush();
            }
        }
    }

    private void logUnchokedNeighbor() {
        CreateThreads.loggin.logInformation(
            "Peer " + CreateThreads.peer_ID + " has the optimistically unchoked neighbor " + PrCls.nonchokedPeerOptimal,
            0
        );
    }
}
