package org.example.generator;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

/**
 * This class is used to generate random json data.
 */
public class DataGenerator {
  private static final String[] TITLES = {"Bohemian Rhapsody", "Stairway to Heaven",
                                          "Hotel California", "Imagine"};
  private static final String[] ARTISTS = {"Queen", "Led Zeppelin", "Eagles", "John Lennon"};
  private static final int[] YEARS = {1975, 1971, 1976, 1971};
  private static final Path RESULT_FILE_DIRECTORY = Path.of("src", "main", "resources", "data");
  private static final String FILE_NAME = "generatedData№";

  private static final String FILE_EXTENSION = ".json";
  private static final String[] GENRES = {"Rock", "Rock, Folk Rock",
                                          "Soft Rock, Rock", "Pop, Dance-pop",
                                          "Soul, Pop", "Grunge, Alternative Rock"};


  private final Random random = new Random();


  public static void main(String[] args) {
    DataGenerator dataGenerator = new DataGenerator();
    dataGenerator.generateJson(RESULT_FILE_DIRECTORY, 10, 10_000_000);
  }

  /**This method generates json data.
   *
   *
   * @param resultDirectory Used to specify desired result directory
   * @param numberOfFiles Used to specify desired number of files
   * @param numberOfRecords Used to specife desired number of records
   */
  public void generateJson(Path resultDirectory, int numberOfFiles, int numberOfRecords) {
    createResultFileDirectory(RESULT_FILE_DIRECTORY);
    int personsPerFile = (int) Math.ceil((double) numberOfRecords / numberOfFiles);
    for (int fileNumber = 0; fileNumber < numberOfFiles && numberOfRecords > 0; fileNumber++) {
      int personsToWrite = Math.min(personsPerFile, numberOfRecords);
      createData(resultDirectory, personsToWrite, fileNumber);
      numberOfRecords -= personsToWrite;
    }


  }

  private void createData(Path resultFileDirectory, int numberOfPersons, int fileNumber) {
    ObjectMapper mapper = new ObjectMapper();
    JsonFactory factory = mapper.getFactory();

    try (JsonGenerator generator = factory.createGenerator(new FileWriter(
        getResultFile(resultFileDirectory, fileNumber)))) {
      generator.writeStartArray();
      Random random = new Random();

      for (int i = 0; i < numberOfPersons; i++) {
        generator.writeStartObject();
        generator.writeStringField("title", TITLES[getRandomElementIndex(TITLES.length)]);
        generator.writeStringField("artist", ARTISTS[getRandomElementIndex(ARTISTS.length)]);
        generator.writeNumberField("year_released", YEARS[getRandomElementIndex(YEARS.length)]);
        generator.writeStringField("genre", GENRES[getRandomElementIndex(GENRES.length)]);
        generator.writeEndObject();
      }

      generator.writeEndArray();
    } catch (IOException e) {
      System.out.println("Error occurred - " + e.getMessage());
    }
  }

  private File getResultFile(Path resultFileDirectory, int fileNumber) {
    return new File(resultFileDirectory.toFile(), FILE_NAME + fileNumber + FILE_EXTENSION);
  }

  private void createResultFileDirectory(Path resultFileDirectory) {
    try {
      Files.createDirectories(resultFileDirectory);
    } catch (IOException e) {
      System.out.println("Can not create a directory");
    }
  }

  public int getRandomElementIndex(int arrayLength) {
    return random.nextInt(arrayLength);
  }
}
