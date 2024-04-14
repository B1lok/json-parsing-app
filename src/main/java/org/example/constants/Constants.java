package org.example.constants;

import java.util.List;
import lombok.experimental.UtilityClass;

/**
 *This is utility class which is used to collect information about constants.
 */
@UtilityClass
public class Constants {
  public static final List<String> ATTRIBUTES = List.of("artist", "year_released", "genre");
  public static final List<String> MULTIPLE_ATTRIBUTES = List.of("genre");

  /**This method is used to create error message.
   *
   * @param invalidAttribute The name of invalid attribute
   * @return message that represents information about allowed attributes
   */
  public static String getAttributesErrorMessage(String invalidAttribute) {
    StringBuilder validArguments = new StringBuilder();
    for (String attribute : ATTRIBUTES) {
      validArguments.append(String.format("  - '%s'%n", attribute));
    }
    return String.format("Error: invalid argument '%s' for '--attribute'"
            + "%nValid arguments are:%n%s",
        invalidAttribute, validArguments);
  }

  public static String getOutputFileName(String attribute) {
    return "/statistics_by_%s.xml".formatted(attribute);
  }
}