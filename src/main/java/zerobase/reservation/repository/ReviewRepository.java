package zerobase.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zerobase.reservation.domain.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

}
