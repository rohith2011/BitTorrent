import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class FileProcessingUtility {

  public void check_peer_Id(int peerId) throws IOException {
    System.out.println("Inside");
  }

  public static byte[] getChunks(
    byte[] actual,
    int start,
    int end,
    String type
  ) {
    byte[] pinal = new byte[end - start];
    int length = Math.min(actual.length - start, end - start); // Used Math.min for clarity
    System.arraycopy(actual, start, pinal, 0, length);
    return pinal;
  }

  public static HashMap<Integer, byte[]> getChunksOfData(
    int file_size_parameter,
    int sizeOfChunks,
    String nameOfFile,
    String type
  ) throws Exception {
    HashMap<Integer, byte[]> dataFileHM = new HashMap<>();
    FileInputStream fis = new FileInputStream(
      "./" + CreateThreads.peer_ID + "/" + nameOfFile
    );
    BufferedInputStream filebuffer = new BufferedInputStream(fis);
    byte[] bA = new byte[file_size_parameter];
    int currentChunkIndex = 0, cnt = 0;

    while (currentChunkIndex < file_size_parameter) {
      cnt =
        getFileDataCount(
          file_size_parameter,
          sizeOfChunks,
          dataFileHM,
          bA,
          currentChunkIndex,
          cnt,
          "1"
        );
      currentChunkIndex += sizeOfChunks;
    }

    return dataFileHM;
  }

  private static int getFileDataCount(
    int filesSize,
    int chunk,
    HashMap<Integer, byte[]> fileDataHashMap,
    byte[] byteArray,
    int chunk_index,
    int integerCount,
    String type
  ) {
    int condition = (chunk_index + chunk <= filesSize) ? 1 : 0;
    switch (condition) { // Used a local variable for switch condition
      case 1:
        fileDataHashMap.put(
          integerCount,
          getChunks(byteArray, chunk_index, chunk_index + chunk, "1")
        );
        return ++integerCount; // Used prefix increment for conciseness
      case 0:
        fileDataHashMap.put(
          integerCount,
          getChunks(byteArray, chunk_index, filesSize, "1")
        );
        return ++integerCount;
      default:
        return integerCount;
    }
  }

  public static void generateNewDirectories(
    int peer_identifier,
    String fileName,
    String type
  ) throws IOException {
    Path filePath = Paths.get("./peer_" + peer_identifier);
    if (Files.exists(filePath)) {
      sweep(filePath, fileName);
    } else {
      Files.createDirectory(filePath);
    }
    new File("./peer_" + peer_identifier + "/logs_" + peer_identifier + ".log"); // Kept as is to maintain functionality
  }

  public static void sweep(Path way, String doc) throws IOException {
    try (Stream<Path> listOfFiles = Files.list(way)) { // Used try-with-resources for Stream
      listOfFiles.forEach(path -> {
        if (!path.getFileName().toString().equals(doc)) {
          try {
            Files.delete(path);
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      });
    }
  }

  public static HashMap<Integer, byte[]> sort_file_data(
    HashMap<Integer, byte[]> map,
    String type
  ) throws Exception {
    List<Map.Entry<Integer, byte[]>> list = new LinkedList<>(map.entrySet());
    Collections.sort(list, Map.Entry.comparingByKey()); // Used a built-in comparator
    HashMap<Integer, byte[]> sorted = new LinkedHashMap<>();
    for (Map.Entry<Integer, byte[]> entry : list) {
      sorted.put(entry.getKey(), entry.getValue());
    }
    return sorted;
  }
}









//NEW