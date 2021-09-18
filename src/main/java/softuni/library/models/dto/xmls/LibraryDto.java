package softuni.library.models.dto.xmls;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "library")
@XmlAccessorType(XmlAccessType.FIELD)
public class LibraryDto {

    @XmlElement
    private String name;

    @XmlElement
    private String location;

    @XmlElement
    private int rating;

    @XmlElement(name = "book", type = BookIdDto.class)
    private List<BookIdDto> books;

    public LibraryDto() {
    }

    @NotNull
    @Length(min = 3)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NotNull
    @Length(min = 5)
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Min(value = 1)
    @Max(value = 10)
    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }


    public List<BookIdDto> getBooks() {
        return books;
    }

    public void setBooks(List<BookIdDto> books) {
        this.books = books;
    }
}
