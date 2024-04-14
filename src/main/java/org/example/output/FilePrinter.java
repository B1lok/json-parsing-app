package org.example.output;

import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.example.constants.Constants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * The FilePrinter class is responsible for generating an XML file
 * based on the given statistics data.
 * It receives a list of key-value pairs representing statistical data,
 * a directory to write the XML file,
 * and an attribute to be included in the output file name.
 */
public class FilePrinter {

  private final List<Map.Entry<String, Integer>> result;

  private final String directoryToWrite;

  private final String attribute;

  /**
   * Constructs a FilePrinter object with the given parameters.
   *
   * @param result           The list of statistical data in the form of key-value pairs.
   * @param directoryToWrite The directory path where the XML file will be written.
   * @param attribute        The attribute to be included in the output file name.
   */
  public FilePrinter(List<Map.Entry<String, Integer>> result,
                     String directoryToWrite, String attribute) {
    this.result = result;
    this.directoryToWrite = directoryToWrite;
    this.attribute = attribute;
  }

  /**
   * Generates and writes the XML file containing the statistical data to the specified directory.
   *
   * @throws Exception If an error occurs during XML generation or file writing.
   */
  public void print() throws Exception {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();

    Document document = builder.newDocument();

    Element root = document.createElement("statistics");
    document.appendChild(root);

    for (Map.Entry<String, Integer> entry : result) {
      Element item = document.createElement("item");
      Element value = document.createElement("value");
      value.appendChild(document.createTextNode(entry.getKey()));
      Element count = document.createElement("count");
      count.appendChild(document.createTextNode(Integer.toString(entry.getValue())));
      item.appendChild(value);
      item.appendChild(count);
      root.appendChild(item);
    }

    TransformerFactory transformerFactory = TransformerFactory.newInstance();
    Transformer transformer = transformerFactory.newTransformer();
    DOMSource source = new DOMSource(document);

    String fileName = Constants.getOutputFileName(attribute);

    StreamResult result = new StreamResult(directoryToWrite
        + fileName);
    transformer.transform(source, result);
    System.out.println(fileName + " file was created at the same directory that you entered");
  }
}
