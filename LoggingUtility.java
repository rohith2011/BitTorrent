// import java.io.IOException;
// import java.util.Date;
// import java.util.logging.*;

// public class LoggingUtility {

//   Logger logger; // Declare a Logger
//   FileHandler fileHandler; // Declare a FileHandler

//   // Constructor
//   LoggingUtility(String peerIdentifier) throws IOException {
//     logger = Logger.getLogger(peerIdentifier); // Initialize Logger

//     // Set the filename for logging
//     String Handler_File_Name =
//       "./" + peerIdentifier + "/logs_" + peerIdentifier + ".log";
//     // Initialize FileHandler
//     fileHandler = new FileHandler(Handler_File_Name);

//     // Set formatter for the FileHandler
//     fileHandler.setFormatter(new NewFormater());

//     // Add FileHandler to Logger
//     logger.addHandler(fileHandler);
//   }

//   // Log informational messages
//   public void logInformation(String message, Integer num) {
//     // Create and log a new LogRecord with Level.INFO
//     logger.log(new LogRecord(Level.INFO, message));
//   }

//   // Log error messages
//   public void loggingError(String message) {
//     // Create and log a new LogRecord with Level.SEVERE
//     logger.log(new LogRecord(Level.SEVERE, message));
//   }

//   // Custom formatter for logging
//   class NewFormater extends Formatter {

//     @Override
//     public String format(LogRecord loggingRecord) {
//       // Format log messages
//       return (
//         new Date(loggingRecord.getMillis()) +
//         " : " +
//         loggingRecord.getMessage() +
//         "\n"
//       );
//     }
//   }
// }


//NEW

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class LoggingUtility {

    // Declare and initialize the Logger and FileHandler
    private final Logger logger;
    private final FileHandler fileHandler;

    // Constructor to initialize Logger and FileHandler
    public LoggingUtility(String peerIdentifier) throws IOException {
        // Initialize Logger
        logger = Logger.getLogger(peerIdentifier);

        // Generate the file name for the log
        String Handler_File_Name = String.format("./%s/logs_%s.log", peerIdentifier, peerIdentifier);

        // Initialize FileHandler
        fileHandler = new FileHandler(Handler_File_Name);

        // Set custom formatter to FileHandler
        fileHandler.setFormatter(new NewFormater());

        // Add the FileHandler to Logger
        logger.addHandler(fileHandler);
    }

    // Method to log informational messages
    public void logInformation(String message, Integer num) {
        logMessage(Level.INFO, message);
    }

    // Method to log error messages
    public void loggingError(String message) {
        logMessage(Level.SEVERE, message);
    }

    // Private utility method to log messages
    private void logMessage(Level level, String message) {
        logger.log(new LogRecord(level, message));
    }

    // Custom formatter class for logs
    private static class NewFormater extends Formatter {

        // SimpleDateFormat for better date representation
        private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // Override format method for custom formatting
        @Override
        public String format(LogRecord loggingRecord) {
            return String.format("%s : %s%n", dateFormat.format(new Date(loggingRecord.getMillis())), loggingRecord.getMessage());
        }
    }
}
