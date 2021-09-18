package softuni.library.services.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.library.models.dto.jsons.AuthorImportDto;
import softuni.library.models.entity.Author;
import softuni.library.repositories.AuthorRepository;
import softuni.library.services.AuthorService;
import softuni.library.util.FileUtil;
import softuni.library.util.ValidatorUtil;

import java.io.IOException;
import java.util.Optional;

@Service
public class AuthorServiceImpl implements AuthorService {

    private final static String AUTHOR_IMPORT_PATH = "src/main/resources/files/json/authors.json";

    private final AuthorRepository authorRepository;
    private final Gson gson;
    private final ModelMapper modelMapper;
    private final ValidatorUtil validatorUtil;
    private final FileUtil fileUtil;

    @Autowired
    public AuthorServiceImpl(AuthorRepository authorRepository, Gson gson, ModelMapper modelMapper, ValidatorUtil validatorUtil, FileUtil fileUtil) {
        this.authorRepository = authorRepository;
        this.gson = gson;
        this.modelMapper = modelMapper;
        this.validatorUtil = validatorUtil;
        this.fileUtil = fileUtil;
    }

    @Override
    public boolean areImported() {
        return this.authorRepository.count() > 0;
    }

    @Override
    public String readAuthorsFileContent() throws IOException {
        return this.fileUtil.readFile(AUTHOR_IMPORT_PATH);
    }

    @Override
    public String importAuthors() throws IOException {
        StringBuilder sb = new StringBuilder();

        AuthorImportDto[] authorImportDtos =
                this.gson.fromJson(this.readAuthorsFileContent(), AuthorImportDto[].class);

        for (AuthorImportDto authorImportDto : authorImportDtos) {
            Optional<Author> byNames = this.authorRepository.
                    findByFirstNameAndLastName(authorImportDto.getFirstName(), authorImportDto.getLastName());
            if (this.validatorUtil.isValid(authorImportDto) && byNames.isEmpty()) {
                Author author = this.modelMapper.map(authorImportDto, Author.class);
                this.authorRepository.saveAndFlush(author);
                sb.append(String.format("Successfully imported Author: %s %s - %s",
                        author.getFirstName(), author.getLastName(), author.getBirthTown()))
                        .append(System.lineSeparator());
            } else {
                sb.append("Invalid Author").append(System.lineSeparator());
            }
        }

        return sb.toString().trim();
    }
}
