package zerobase.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zerobase.reservation.domain.Reservation;
import zerobase.reservation.domain.Store;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    int countByReservedAt(LocalDateTime reservedAt);

    Optional<Reservation> findByReservationNum(String reserveNum);

    Optional<List<Reservation>> findByStoreId(Long storeId);

    Optional<List<Reservation>> findByStore(Store store);
}
