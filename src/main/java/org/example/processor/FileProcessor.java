package org.example.processor;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import org.example.constants.Constants;

/**
 * The FileProcessor class is responsible for processing JSON files asynchronously to extract
 * specified attributes and update a concurrent result map with the extracted attribute counts.
 * Each instance of FileProcessor is designed to process a single JSON file.
 */
public class FileProcessor implements Runnable {

  private final ConcurrentHashMap<String, Integer> resultMap;
  private final HashMap<String, Integer> localMap = new HashMap<>();
  private final String attribute;
  private final File file;

  private final JsonFactory factory;
  private final CountDownLatch latch;

  /**
   * The FileProcessor class is responsible for processing JSON files asynchronously to extract
   * specified attributes and update a concurrent result map with the extracted attribute counts.
   * Each instance of FileProcessor is designed to process a single JSON file.
   */
  public FileProcessor(ConcurrentHashMap<String, Integer> resultMap,
                       String attribute, File file, CountDownLatch latch, JsonFactory factory) {
    this.resultMap = resultMap;
    this.attribute = attribute;
    this.file = file;
    this.factory = factory;
    this.latch = latch;
  }


  @Override
  public void run() {
    try {
      process();
    } catch (IOException e) {
      System.out.println("Invalid json file format");
    }
    latch.countDown();
  }

  /**
   * Processes the JSON file, extracting the specified attribute and updating the local map with
   * the attribute counts.
   *
   * @throws IOException If an error occurs while reading the JSON file or parsing its contents.
   */
  public void process() throws IOException {
    boolean isMultipleAttribute = Constants.MULTIPLE_ATTRIBUTES.contains(attribute);
    try (JsonParser parser = factory.createParser(file)) {
      if (parser.nextToken() != JsonToken.START_ARRAY) {
        throw new IOException("Expected an array as the root");
      }

      while (parser.nextToken() != JsonToken.END_ARRAY) {
        if (parser.currentToken() == JsonToken.START_OBJECT) {
          while (parser.nextToken() != JsonToken.END_OBJECT) {
            String fieldName = parser.getValueAsString();
            parser.nextToken();
            if (attribute.equals(fieldName)) {
              String value = parser.getText();
              if (isMultipleAttribute) {
                handleMultipleAttribute(value);
              } else {
                handleSingleAttribute(value);
              }
            }
          }
        }
      }
    }
    mergeLocalMap();
  }

  private void handleSingleAttribute(String attribute) {
    if (localMap.containsKey(attribute)) {
      localMap.put(attribute, localMap.get(attribute) + 1);
    } else {
      localMap.put(attribute, 1);
    }
  }

  private void handleMultipleAttribute(String attributes) {
    Arrays.stream(attributes.split(", ")).forEach(this::handleSingleAttribute);
  }

  private void mergeLocalMap() {
    synchronized (localMap) {
      for (Map.Entry<String, Integer> entry : localMap.entrySet()) {
        resultMap.merge(entry.getKey(), entry.getValue(), Integer::sum);
      }
    }
  }
}