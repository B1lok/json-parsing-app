import com.fasterxml.jackson.core.JsonFactory;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Stream;
import org.example.processor.FileProcessor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.*;

public class FileProcessorTest {

  private final ConcurrentHashMap<String, Integer> resultMap = new ConcurrentHashMap<>();

  private final JsonFactory factory = new JsonFactory();
  private final CountDownLatch latch = new CountDownLatch(1);

  @AfterEach
  public void clearMap(){
    resultMap.clear();
  }

  @ParameterizedTest
  @MethodSource("provideAttributesForEmptyFile")
  public void testEmptyDataFiles(String attribute, int expectedSize) throws IOException {
    File file = new File("src/test/resources/testData/emptyDataFile.json");
    var fileProcessor = new FileProcessor(resultMap, attribute, file, latch, factory);

    fileProcessor.process();

    assertEquals(resultMap.size(), expectedSize);
    assertTrue(resultMap.isEmpty());
  }

  @Test
  public void testInvalidDataFile() throws IOException {
    File file = new File("src/test/resources/testData/invalidDataFile.json");
    var attribute = "artist";
    var fileProcessor = new FileProcessor(resultMap, attribute, file, latch, factory);

    assertThrows(IOException.class, fileProcessor::process);
  }

  @ParameterizedTest
  @MethodSource("provideAttributesForValidFile")
  public void testValidDataFile(String attribute, String fieldValue, int numberOfOccurrences) throws IOException{
    File file = new File("src/test/resources/testData/validDataFile.json");

    var fileProcessor = new FileProcessor(resultMap, attribute, file, latch, factory);
    fileProcessor.process();

    assertEquals(resultMap.get(fieldValue), numberOfOccurrences);
  }


  private static Stream<Arguments> provideAttributesForEmptyFile() {
    return Stream.of(
        Arguments.of("artist", 0),
        Arguments.of("genre", 0),
        Arguments.of("year_released", 0)
    );
  }

  private static Stream<Arguments> provideAttributesForValidFile() {
    return Stream.of(
        Arguments.of("artist", "Queen", 1),
        Arguments.of("artist", "Ed Sheeran", 1),
        Arguments.of("artist", "Nirvana", 2),
        Arguments.of("artist", "Michael Jackson", 1),
        Arguments.of("artist", "Eagles", 1),
        Arguments.of("artist", "Adele", 1),
        Arguments.of("genre", "Rock", 2),
        Arguments.of("genre", "Progressive Rock", 1),
        Arguments.of("genre", "Pop", 3),
        Arguments.of("genre", "Dance-pop", 1),
        Arguments.of("genre", "Grunge", 2),
        Arguments.of("genre", "Alternative Rock", 2),
        Arguments.of("genre", "Funk", 1),
        Arguments.of("genre", "Soft Rock", 1),
        Arguments.of("genre", "Soul", 1),
        Arguments.of("year_released","1975", 1),
        Arguments.of("year_released","2017", 1),
        Arguments.of("year_released","1991", 2),
        Arguments.of("year_released","1983", 1),
        Arguments.of("year_released","1976", 1),
        Arguments.of("year_released","2010", 1)
    );
  }
}