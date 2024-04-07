package zerobase.reservation.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minidev.json.annotate.JsonIgnore;
import zerobase.reservation.dto.StoreDto;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "store")
public class Store extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String storeName;
    private String location;
    private String description;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "manager_id")
    private Manager manager;

    @OneToMany(mappedBy = "store")
    private List<Review> review = new ArrayList<>();


    public static StoreDto.SearchStoreResponse toResponse(Store store) {
        return StoreDto.SearchStoreResponse.builder()
                .storeName(store.getStoreName())
                .location(store.getLocation())
                .description(store.getDescription())
                .build();
    }
}
