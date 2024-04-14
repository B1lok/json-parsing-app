package org.example.input;

import com.fasterxml.jackson.core.JsonFactory;
import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.SneakyThrows;
import org.example.constants.Constants;
import org.example.output.FilePrinter;
import org.example.processor.FileProcessor;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Spec;

/**
 * The FilesStatistic class represents a command-line application for processing JSON files.
 * It allows users to specify attributes to search for in the JSON files and the directory
 * containing the JSON files to be processed.
 * Upon execution, it retrieves the JSON files from the specified directory and processes them
 * concurrently using multiple threads.
 */
@Command(name = "fileStatistic", mixinStandardHelpOptions = true,
    description = {
        """
             * This class represents a command-line application for processing JSON files.
             * It allows users to specify attributes to search for in the JSON files and
             * the directory containing the JSON files to be processed.
             *\s
             * Upon execution, it retrieves the JSON files from the specified directory and
             * processes them concurrently using multiple threads.\
            """
    },
    version = {
        "FileStatistic 1.0",
        "Picocli " + CommandLine.VERSION
    })
public class FilesStatistic implements Runnable {

  @Getter
  private final ConcurrentHashMap<String, Integer> resultMap = new ConcurrentHashMap<>();
  private final JsonFactory factory = new JsonFactory();
  @Spec
  CommandSpec spec;
  private int threadNumber = Runtime.getRuntime().availableProcessors() - 1;
  private String attribute;
  private String directoryPath;

  public static void main(String[] args) {
    int exitCode = new CommandLine(new FilesStatistic()).execute(args);
    System.exit(exitCode);
  }

  @SneakyThrows
  @Override
  public void run() {
    List<File> directoryFiles = getJsonFiles(directoryPath);
    var latch = new CountDownLatch(directoryFiles.size());
    ExecutorService service = Executors.newFixedThreadPool(threadNumber);
    for (File file : directoryFiles) {
      service.submit(new FileProcessor(resultMap, attribute, file, latch, factory));
    }
    latch.await();
    service.shutdown();
    List<Map.Entry<String, Integer>> res = new ArrayList<>(resultMap.entrySet());
    res.sort(Map.Entry.comparingByValue(Collections.reverseOrder()));
    printFile(res);
  }

  /**
   * Sets the attribute to search for in the JSON files.
   *
   * @param attribute The attribute to search for.
   */
  @Option(names = {"-a", "--attribute"}, required = true,
      description = "Specify the attribute to search for in the JSON files.")
  public void setAttribute(String attribute) {
    if (!Constants.ATTRIBUTES.contains(attribute.toLowerCase())) {
      throw new ParameterException(spec.commandLine(),
          Constants.getAttributesErrorMessage(attribute));
    }
    this.attribute = attribute.toLowerCase();
  }

  /**
   * Sets the directory path containing the JSON files to be processed.
   *
   * @param directoryPath The directory path containing the JSON files.
   */
  @Option(names = {"-d", "--directory"}, required = true,
      description = "Specify the path to the directory containing the JSON files.")
  public void setDirectoryPath(String directoryPath) {
    File directory = new File(directoryPath);
    if (!directory.exists() || !directory.isDirectory()) {
      throw new ParameterException(spec.commandLine(),
          String.format("Error: '%s' is not a valid directory path.", directoryPath));
    }
    this.directoryPath = directoryPath;
  }

  /**
   * Prints the processed file containing the attribute counts to an XML file.
   *
   * @param resultList The list of attribute counts to be printed.
   */
  public void printFile(List<Map.Entry<String, Integer>> resultList) {
    FilePrinter printer = new FilePrinter(resultList, directoryPath, attribute);
    try {
      printer.print();
    } catch (Exception e) {
      System.out.println("Error occurred while writing into xml file");
    }
  }

  /**
   * Retrieves a list of JSON files from the specified directory.
   *
   * @param directoryPath The directory path containing the JSON files.
   * @return A list of JSON files.
   */
  public List<File> getJsonFiles(String directoryPath) {
    File directory = new File(directoryPath);
    File[] files = directory.listFiles();
    if (files != null) {
      return Arrays.stream(files)
          .filter(file -> file.isFile() && file.getName().toLowerCase().endsWith(".json"))
          .collect(Collectors.toList());
    }
    return Collections.emptyList();
  }

  public void setThreadNumber(int threadNumber) {
    this.threadNumber = threadNumber;
  }
}