package softuni.library.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import softuni.library.models.entity.Library;

import java.util.Optional;

@Repository
public interface LibraryRepository extends JpaRepository<Library, Integer> {
    Optional<Library> findByName(String name);
}
