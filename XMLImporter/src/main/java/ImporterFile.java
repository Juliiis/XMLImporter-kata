import static java.nio.file.Files.walk;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import xmlmodels.Company;

public class ImporterFile {
  static void addFileIntoACompany(List<Path> paths, ArrayList<Company> companies) throws JAXBException {
    for (Path path : paths) {
      File file = new File(path.toString());
      JAXBContext jaxbContext = JAXBContext.newInstance(Company.class);
      Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
      Company company = (Company) jaxbUnmarshaller.unmarshal(file);
      companies.add(company);
    }
  }

  static List<Path> getPathList(Path folderPath, String fileExtension) throws IOException {
      List<Path> paths;
      try (Stream<Path> pathStream = walk(folderPath)
        .filter(Files::isRegularFile)
        .filter(filePath ->
          filePath.toString()
            .endsWith(fileExtension))) {
          paths = pathStream
            .collect(Collectors.toList());
      }
      return paths;
  }
}