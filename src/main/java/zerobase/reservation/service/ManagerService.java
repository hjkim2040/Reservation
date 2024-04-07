package zerobase.reservation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import zerobase.reservation.domain.Manager;
import zerobase.reservation.domain.Reservation;
import zerobase.reservation.domain.Store;
import zerobase.reservation.dto.ManagerDto;
import zerobase.reservation.dto.ReservationConfirm;
import zerobase.reservation.dto.StoreDto;
import zerobase.reservation.exception.ManagerException;
import zerobase.reservation.exception.MemberException;
import zerobase.reservation.exception.StoreException;
import zerobase.reservation.repository.ManagerRepository;
import zerobase.reservation.repository.ReservationRepository;
import zerobase.reservation.repository.StoreRepository;
import zerobase.reservation.security.TokenProvider;
import zerobase.reservation.type.Authority;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static zerobase.reservation.type.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class ManagerService implements UserDetailsService {
    private final ManagerRepository managerRepository;
    private final StoreRepository storeRepository;
    private final ReservationRepository reservationRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;


    @Override
    public UserDetails loadUserByUsername(String mail) throws UsernameNotFoundException {
        return this.managerRepository.findByMail(mail)

                .orElseThrow(RuntimeException::new);
    }

    /**
     * 점장 회원가입
     */
    @Transactional
    public ManagerDto.RegisterResponse register(ManagerDto.RegisterRequest manager) {
        validate(manager);

        manager.setRole(String.valueOf(Authority.ROLE_MANAGER));
        manager.setPassword(this.passwordEncoder.encode(manager.getPassword()));
        return Manager.toResponse(this.managerRepository.save(manager.toEntity()));
    }

    /**
     * 회원가입 유효한지 확인
     */
    private void validate(ManagerDto.RegisterRequest manager) {
        boolean exists = this.managerRepository.existsByMail(manager.getMail());
        if (exists) {
            throw new MemberException(USER_DUPLICATED);
        }
    }

    /**
     * 로그인 유효한지 확인
     */
    public String authenticate(ManagerDto.LoginRequest manager) {
        var user = this.managerRepository.findByMail(manager.getMail())
                .orElseThrow(() -> new ManagerException(USER_NOT_FOUND));

        if (!this.passwordEncoder.matches(manager.getPassword(), user.getPassword())) {
            throw new ManagerException(PASSWORD_UNMATCHED);
        }
        return this.tokenProvider.generateToken(user.getMail(), user.getRole());
    }

    /**
     * 매장 추가
     */
    @Transactional
    public StoreDto.StoreResponse addStore(StoreDto.AddStoreRequest store, String token) {

        Manager manager = getManagerEntity(token);

        int count = storeRepository.countByStoreName(store.getStoreName());
        if (count > 0) {
            throw new StoreException(STORE_DUPLICATED);
        }

        Store storeEntity = store.toEntity();
        storeEntity.setManager(manager);

        var result = storeRepository.save(storeEntity);
        return StoreDto.StoreResponse.builder()
                .storeName(result.getStoreName())
                .createdAt(result.getCreatedAt())
                .build();
    }
    /**
     * 매장 수정
     */
    @Transactional
    public StoreDto.StoreResponse updateStore(Long storeId, StoreDto.UpdateStoreRequest store, String token) {

        Manager manager = getManagerEntity(token);

        Store storeEntity = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreException(STORE_NOT_FOUND));

        if (!storeEntity.getManager().equals(manager)) {
            throw new StoreException(NO_PERMISSION);
        }

        if (!storeEntity.getStoreName().equals(store.getStoreName())) {
            int count = storeRepository.countByStoreName(store.getStoreName());
            if (count > 0) {
                throw new StoreException(STORE_DUPLICATED);
            }
            storeEntity.setStoreName(store.getStoreName());
        }
        storeEntity.setDescription(store.getDescription());
        storeEntity.setLocation(store.getLocation());

        var result = storeRepository.save(storeEntity);
        return StoreDto.StoreResponse.builder()
                .storeName(result.getStoreName())
                .location(result.getLocation())
                .description(result.getDescription())
                .updatedAt(result.getUpdatedAt())
                .build();
    }
    /**
     * 매장 삭제
     */
    @Transactional
    public void deleteStore(Long storeId, String token) {

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreException(STORE_NOT_FOUND));

        Manager manager = getManagerEntity(token);
        if (!store.getManager().equals(manager)) {
            throw new StoreException(NO_PERMISSION);
        }

        storeRepository.delete(store);
    }

    /**
     * email로 해당 점장 Entity를 찾는 메소드
     * - private 메소드 getMailFromToken 이용해서 mail을 가져와서
     * manager 테이블에 있는지 확인.
     */
    private Manager getManagerEntity(String token) {
        String mail = getMailFromToken(token);

        Optional<Manager> optionalManager = managerRepository.findByMail(mail);
        if (optionalManager.isEmpty()) {
            throw new ManagerException(MANAGER_NOT_FOUND);
        }

        return optionalManager.get();
    }

    /**
     * 토큰에서 email 꺼내오는 메소드
     * - TokenProvider의 getMail 메소드를 이용해서 토큰에서 mail값을 꺼내옴.
     */

    private String getMailFromToken(String token) {
        if (!ObjectUtils.isEmpty(token) && token.startsWith("Bearer")) {
            token =  token.substring("Bearer".length());
        }
        return tokenProvider.getMail(token);
    }

    /**
     * 해당 점장 기준 등록된 매장 확인
     */
    public List<Store> searchStore(String token) {
        Manager manager = getManagerEntity(token);
        List<Store> storeList = storeRepository.findByManager(manager);

        return storeList;
    }

    /**
     * 해당 매장 기준 등록된 예약 확인
     */
    public List<Reservation> searchReservation(Long storeId, String token) {
        Manager manager = getManagerEntity(token);

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ManagerException(STORE_NOT_FOUND));


        if (!Objects.equals(manager.getId(), store.getManager().getId())) {
            throw new ManagerException(UNMATCHED_MANAGER_STORE);
        }

        return reservationRepository.findByStoreId(storeId)
                .orElseThrow(() -> new ManagerException(RESERVE_NOT_FOUND));
    }

    /**
     * 예약 승인/거절
     */
    @Transactional
    public Long confirmReservation(ReservationConfirm reservationConfirm, String token) {
        //매장 존재 확인
        Store store = storeRepository.findById(reservationConfirm.getStoreId())
                .orElseThrow(() -> new ManagerException(STORE_NOT_FOUND));

        //예약 존재 확인
        Reservation reservation = reservationRepository.findById(reservationConfirm.getReservationId())
                .orElseThrow(() -> new ManagerException(RESERVE_NOT_FOUND));

        //해당 매장의 예약인지 확인
        if (!Objects.equals(store.getId(), reservation.getStore().getId())) {
            throw new ManagerException(UNMATCHED_STORE_RESERVE);
        }
        Manager manager = this.getManagerEntity(token);

        //해당 점장의 매장에서 일어난 예약인지 확인
        if (!Objects.equals(reservation.getStore().getManager(), manager)) {
            throw new ManagerException(UNMATCHED_RESERVE_MANAGER);
        }
        reservation.setConfirm(reservationConfirm.isConfirmYn());
        reservationRepository.save(reservation);

        return reservationConfirm.getReservationId();
    }
}
