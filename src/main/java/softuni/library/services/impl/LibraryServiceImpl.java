package softuni.library.services.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.library.models.dto.xmls.LibraryDto;
import softuni.library.models.dto.xmls.LibraryRootDto;
import softuni.library.models.entity.Book;
import softuni.library.models.entity.Library;
import softuni.library.repositories.BookRepository;
import softuni.library.repositories.LibraryRepository;
import softuni.library.services.LibraryService;
import softuni.library.util.FileUtil;
import softuni.library.util.ValidatorUtil;
import softuni.library.util.XmlParser;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class LibraryServiceImpl implements LibraryService {

    private final static String LIBRARY_IMPORT_PATH = "src/main/resources/files/xml/libraries.xml";

    private final LibraryRepository libraryRepository;
    private final BookRepository bookRepository;
    private final XmlParser xmlParser;
    private final ModelMapper modelMapper;
    private final ValidatorUtil validatorUtil;
    private final FileUtil fileUtil;

    @Autowired
    public LibraryServiceImpl(LibraryRepository libraryRepository, BookRepository bookRepository, XmlParser xmlParser, ModelMapper modelMapper, ValidatorUtil validatorUtil, FileUtil fileUtil) {
        this.libraryRepository = libraryRepository;
        this.bookRepository = bookRepository;
        this.xmlParser = xmlParser;
        this.modelMapper = modelMapper;
        this.validatorUtil = validatorUtil;
        this.fileUtil = fileUtil;
    }

    @Override
    public boolean areImported() {
        return this.libraryRepository.count() > 0;
    }

    @Override
    public String readLibrariesFileContent() throws IOException {
        return this.fileUtil.readFile(LIBRARY_IMPORT_PATH);
    }

    @Override
    public String importLibraries() throws JAXBException {
        StringBuilder sb = new StringBuilder();

        LibraryRootDto libraryRootDto = this.xmlParser.parseXml(LibraryRootDto.class, LIBRARY_IMPORT_PATH);

        for (LibraryDto libraryDto : libraryRootDto.getLibraries()) {
            Optional<Library> byName = this.libraryRepository.findByName(libraryDto.getName());
            if (this.validatorUtil.isValid(libraryDto)) {
                Library library = this.modelMapper.map(libraryDto, Library.class);
                Book book = this.bookRepository.findById(libraryDto.getBooks().get(0).getId()).get();

                if (byName.isEmpty()) {
                    library.setBooks(List.of(book));

                    this.libraryRepository.saveAndFlush(library);
                    sb.append(String.format("Successfully added Library: %s - %s",
                            library.getName(), library.getLocation()))
                            .append(System.lineSeparator());
                } else {
                    library.setBooks(List.of(book));
                }

            } else {
                sb.append("Invalid Library").append(System.lineSeparator());
            }
        }

        return sb.toString().trim();
    }
}
