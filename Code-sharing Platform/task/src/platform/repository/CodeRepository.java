package platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import platform.model.Code;

import java.util.List;

public interface CodeRepository extends JpaRepository<Code, String> {
    List<Code> findTop10ByTimeLimitAndViewsLimitOrderByCreatedAtDesc(long timeLimit, int viewsLimit);
}
