// import java.io.DataOutputStream;
// import java.net.Socket;
// import java.net.SocketException;
// import java.util.HashMap;

// class DeterminePreferredNeighbors implements Runnable {

//   public DeterminePreferredNeighbors() {}

//   @Override
//   public void run() {
//     synchronized (this) {
//       try {
//         while (PrCls.number_of_tot_Downloads < CreateThreads.peering.size()) {
//           int Neighbors_calc = Config_common.preferredNeighborCount;
//           PrCls.neighboursPref.clear();

//           int k = 0; 
//           if (PrCls.neighbours.size() > Neighbors_calc) {
//             for (HashMap.Entry<Integer, Double> entry : PrCls.endIntrvlDwnSpd.entrySet()) {
//               PrCls.neighboursPref.add(entry.getKey());
//               k++;
//               if (k >= Neighbors_calc) {
//                 break;
//               }
//             }
//           } else {
//             for (Integer peerID : PrCls.neighbours) {
//               PrCls.neighboursPref.add(peerID);
//             }
//           }

//           PrCls.endIntrvlDwnSpd.replaceAll((key, value) -> 0.0);

//           for (HashMap.Entry<Integer, Boolean> entry : PrCls.chkngMp.entrySet()) {
//             Socket socket = CreateThreads.peering.get(entry.getKey()).sock;
//             if (socket == null) continue; // Removed curly braces for a single statement

//             DataOutputStream dos = new DataOutputStream(
//               socket.getOutputStream()
//             ); // Renamed dataOutputStream to dos
//             if (PrCls.neighboursPref.contains(entry.getKey())) {
//               dos.write(
//                 MsgManager.makeMsg(MessageConstants.UNCHOKE_FLAG, null, -1)
//               );
//               dos.flush();
//               PrCls.chkngMp.put(entry.getKey(), false);
//             } else {
//               dos.write(
//                 MsgManager.makeMsg(MessageConstants.CHOKE_FLAG, null, -1)
//               );
//               dos.flush();
//               PrCls.chkngMp.put(entry.getKey(), true);
//             }
//           }

//           CreateThreads.loggin.logInformation(
//             "Peer " +
//             CreateThreads.peer_ID +
//             " has the preferred neighbors " +
//             PrCls.neighboursPref.toString(),
//             0
//           );

//           Thread.sleep(Config_common.unchoking_Interval * 1000); // Converted to milliseconds
//         }
//       } catch (SocketException e) {
//         // Silent catch
//       } catch (Exception e) {
//         e.printStackTrace();
//       }
//       System.exit(0);
//     }
//   }
// }








//NEW





import java.io.DataOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;

class DeterminePreferredNeighbors implements Runnable {

    public DeterminePreferredNeighbors() {}

    @Override
    public void run() {
        synchronized (this) {
            try {
                while (PrCls.number_of_tot_Downloads < CreateThreads.peering.size()) {
                    determinePreferredNeighbors();
                    Thread.sleep(Config_common.unchoking_Interval * 1000);
                }
            } catch (SocketException e) {
                // Silent catch
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.exit(0);
        }
    }

    private void determinePreferredNeighbors() throws Exception {
        int neighborCount = Config_common.preferredNeighborCount;
        PrCls.neighboursPref.clear();
        populatePreferredNeighbors(neighborCount);

        PrCls.endIntrvlDwnSpd.replaceAll((key, value) -> 0.0);

        updateChokingStatus();

        CreateThreads.loggin.logInformation(
                "Peer " + CreateThreads.peer_ID + " has the preferred neighbors " + PrCls.neighboursPref.toString(),
                0);
    }

    private void populatePreferredNeighbors(int neighborCount) {
        int counter = 0;
        if (PrCls.neighbours.size() > neighborCount) {
            for (HashMap.Entry<Integer, Double> entry : PrCls.endIntrvlDwnSpd.entrySet()) {
                PrCls.neighboursPref.add(entry.getKey());
                counter++;
                if (counter >= neighborCount) {
                    break;
                }
            }
        } else {
            PrCls.neighboursPref.addAll(PrCls.neighbours);
        }
    }

    private void updateChokingStatus() throws Exception {
        for (HashMap.Entry<Integer, Boolean> entry : PrCls.chkngMp.entrySet()) {
            Socket socket = CreateThreads.peering.get(entry.getKey()).sock;
            if (socket == null) {
                continue;
            }

            try (DataOutputStream dos = new DataOutputStream(socket.getOutputStream())) {
                if (PrCls.neighboursPref.contains(entry.getKey())) {
                    dos.write(MsgManager.makeMsg(MessageConstants.UNCHOKE_FLAG, null, -1));
                    dos.flush();
                    PrCls.chkngMp.put(entry.getKey(), false);
                } else {
                    dos.write(MsgManager.makeMsg(MessageConstants.CHOKE_FLAG, null, -1));
                    dos.flush();
                    PrCls.chkngMp.put(entry.getKey(), true);
                }
            }
        }
    }
}