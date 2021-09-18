package softuni.library.services.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.library.models.dto.jsons.BookImportDto;
import softuni.library.models.entity.Author;
import softuni.library.models.entity.Book;
import softuni.library.repositories.AuthorRepository;
import softuni.library.repositories.BookRepository;
import softuni.library.services.BookService;
import softuni.library.util.FileUtil;
import softuni.library.util.ValidatorUtil;

import java.io.IOException;

@Service
public class BookServiceImpl implements BookService {

    private final static String BOOK_IMPORT_PATH = "src/main/resources/files/json/books.json";

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final Gson gson;
    private final ModelMapper modelMapper;
    private final ValidatorUtil validatorUtil;
    private final FileUtil fileUtil;

    @Autowired
    public BookServiceImpl(BookRepository bookRepository, AuthorRepository authorRepository, Gson gson, ModelMapper modelMapper, ValidatorUtil validatorUtil, FileUtil fileUtil) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.gson = gson;
        this.modelMapper = modelMapper;
        this.validatorUtil = validatorUtil;
        this.fileUtil = fileUtil;
    }

    @Override
    public boolean areImported() {
        return this.bookRepository.count() > 0;
    }

    @Override
    public String readBooksFileContent() throws IOException {
        return this.fileUtil.readFile(BOOK_IMPORT_PATH);
    }

    @Override
    public String importBooks() throws IOException {
        StringBuilder sb = new StringBuilder();

        BookImportDto[] bookImportDtos = this.gson
                .fromJson(this.readBooksFileContent(), BookImportDto[].class);

        for (BookImportDto bookImportDto : bookImportDtos) {
            if (this.validatorUtil.isValid(bookImportDto)) {
                Book book = this.modelMapper.map(bookImportDto, Book.class);
                Author author = this.authorRepository.findById(bookImportDto.getAuthor()).get();

                book.setWritten(bookImportDto.getWritten());
                book.setAuthor(author);
                this.bookRepository.saveAndFlush(book);
                sb.append(String.format("Successfully imported Book: %s written in %s",
                        book.getName(), book.getWritten()))
                        .append(System.lineSeparator());
            } else {
                sb.append("Invalid Book").append(System.lineSeparator());
            }
        }

        return sb.toString().trim();
    }
}
