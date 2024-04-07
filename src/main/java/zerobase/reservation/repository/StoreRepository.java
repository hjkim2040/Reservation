package zerobase.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zerobase.reservation.domain.Manager;
import zerobase.reservation.domain.Store;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
    int countByStoreName(String storeName);

    Optional<Store> findByStoreName(String storeName);

    List<Store> findByManager(Manager manager);
}
