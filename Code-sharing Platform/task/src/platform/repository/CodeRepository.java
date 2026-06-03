package platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import platform.model.Code;

import java.util.List;

public interface CodeRepository extends JpaRepository<Code, Long> {
    List<Code> findTop10ByOrderByIdDesc();
}
