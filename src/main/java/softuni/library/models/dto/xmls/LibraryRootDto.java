package softuni.library.models.dto.xmls;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "libraries")
@XmlAccessorType(XmlAccessType.FIELD)
public class LibraryRootDto {

    @XmlElement(name = "library")
    private List<LibraryDto> libraries;

    public LibraryRootDto() {
    }

    public List<LibraryDto> getLibraries() {
        return libraries;
    }

    public void setLibraries(List<LibraryDto> libraries) {
        this.libraries = libraries;
    }
}
