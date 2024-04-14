import java.time.Duration;
import java.util.Map;
import java.util.stream.Stream;
import org.example.input.FilesStatistic;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileStatisticTest {

  private static final String FILES_DIRECTORY = "src/test/resources/testData/multiThreadData";
  private static final int NUMBER_OF_ATTEMPTS = 3;

  @ParameterizedTest
  @MethodSource("provideAttributesForMultiThreadTest")
  public void testMultiThreadExecution(String attribute, int numberOfElements){
    var fileStatistic = new FilesStatistic();
    fileStatistic.setAttribute(attribute);
    fileStatistic.setDirectoryPath(FILES_DIRECTORY);
    fileStatistic.run();

    int executedNumberOfElements = 0;
    for (Map.Entry<String, Integer> elem : fileStatistic.getResultMap().entrySet()){
      executedNumberOfElements += elem.getValue();
    }

    assertEquals(executedNumberOfElements, numberOfElements);
  }

  @ParameterizedTest
  @ValueSource(ints = { 1, 2, 4, 8 })
  public void testExecutionTime(int numberOfThreads){
    long duration = 0;
    String attribute = "genre";
    for (int i = 0; i < NUMBER_OF_ATTEMPTS; i++){
      var fileStatistic = new FilesStatistic();
      fileStatistic.setThreadNumber(numberOfThreads);
      fileStatistic.setAttribute(attribute);
      fileStatistic.setDirectoryPath(FILES_DIRECTORY);
      long start = System.currentTimeMillis();
      fileStatistic.run();
      long finish = System.currentTimeMillis();
      duration+= finish - start;
      System.gc();
    }
    System.out.printf("Number of threads %d, parsing duration %d ms%n", numberOfThreads, duration / NUMBER_OF_ATTEMPTS);
  }
  private static Stream<Arguments> provideAttributesForMultiThreadTest() {
    return Stream.of(
        Arguments.of("artist", 10_000_000),
        Arguments.of("genre", 20_000_000),
        Arguments.of("year_released", 10_000_000)
    );
  }
}