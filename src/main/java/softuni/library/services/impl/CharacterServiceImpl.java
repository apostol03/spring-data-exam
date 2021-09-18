package softuni.library.services.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.library.models.dto.xmls.CharacterDto;
import softuni.library.models.dto.xmls.CharacterRootDto;
import softuni.library.models.entity.Book;
import softuni.library.models.entity.Character;
import softuni.library.repositories.BookRepository;
import softuni.library.repositories.CharacterRepository;
import softuni.library.services.CharacterService;
import softuni.library.util.FileUtil;
import softuni.library.util.ValidatorUtil;
import softuni.library.util.XmlParser;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.List;

@Service
public class CharacterServiceImpl implements CharacterService {

    private final static String CHARACTER_IMPORT_PATH = "src/main/resources/files/xml/characters.xml";

    private final CharacterRepository characterRepository;
    private final BookRepository bookRepository;
    private final XmlParser xmlParser;
    private final ModelMapper modelMapper;
    private final ValidatorUtil validatorUtil;
    private final FileUtil fileUtil;

    @Autowired
    public CharacterServiceImpl(CharacterRepository characterRepository, BookRepository bookRepository, XmlParser xmlParser, ModelMapper modelMapper, ValidatorUtil validatorUtil, FileUtil fileUtil) {
        this.characterRepository = characterRepository;
        this.bookRepository = bookRepository;
        this.xmlParser = xmlParser;
        this.modelMapper = modelMapper;
        this.validatorUtil = validatorUtil;
        this.fileUtil = fileUtil;
    }

    @Override
    public boolean areImported() {
        return this.characterRepository.count() > 0;
    }

    @Override
    public String readCharactersFileContent() throws IOException {
        return this.fileUtil.readFile(CHARACTER_IMPORT_PATH);
    }

    @Override
    public String importCharacters() throws JAXBException {
        StringBuilder sb = new StringBuilder();

        CharacterRootDto characterRootDto = this.xmlParser.parseXml(CharacterRootDto.class, CHARACTER_IMPORT_PATH);

        for (CharacterDto characterDto : characterRootDto.getCharacters()) {
            if (this.validatorUtil.isValid(characterDto)) {
                Character character = this.modelMapper.map(characterDto, Character.class);
                Book book = this.bookRepository.findById(characterDto.getBook().getId()).get();
                character.setBook(book);

                this.characterRepository.saveAndFlush(character);
                sb.append(String.format("Successfully imported Character: %s %s %s - age: %d",
                        character.getFirstName(), character.getMiddleName(), character.getLastName(),
                        character.getAge()))
                        .append(System.lineSeparator());
            } else {
                sb.append("Invalid Character").append(System.lineSeparator());
            }
        }

        return sb.toString().trim();
    }

    @Override
    public String findCharactersInBookOrderedByLastNameDescendingThenByAge() {
        StringBuilder sb = new StringBuilder();
        List<Character> all = this.characterRepository.findAllByAge();
        for (Character character : all) {
            sb.append(String.format("Character name %s %s %s, age %d, in book %s", character.getFirstName(), character.getMiddleName(), character.getLastName(),character.getAge() , character.getBook().getName()))
            .append(System.lineSeparator());
        }
        return sb.toString().trim();
    }
}
