package greencity.repository;

import greencity.entity.InitiativeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InitiativeTypeRepo extends JpaRepository<InitiativeType, Long> {
    Optional<InitiativeType> findByName(String name);
}
