package zerobase.reservation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import zerobase.reservation.domain.Store;
import zerobase.reservation.dto.StoreDto;
import zerobase.reservation.exception.StoreException;
import zerobase.reservation.repository.StoreRepository;

import static zerobase.reservation.type.ErrorCode.STORE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class StoreService {
    private final StoreRepository storeRepository;


    /**
     * 매장 검색
     */
    public StoreDto.SearchStoreResponse searchStore(String storeName) {
        return Store.toResponse(storeRepository.findByStoreName(storeName)
                .orElseThrow(() -> new StoreException(STORE_NOT_FOUND)));
    }
}
