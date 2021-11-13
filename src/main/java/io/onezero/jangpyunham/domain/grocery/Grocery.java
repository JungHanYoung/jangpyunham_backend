package io.onezero.jangpyunham.domain.grocery;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.onezero.jangpyunham.domain.BaseEntity;
import io.onezero.jangpyunham.web.grocery.dto.CreateGroceryDto;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "grocery")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Grocery extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;

    @Column
    private LocalDate recentlyUsed;

    public static Grocery fromDto(CreateGroceryDto dto) {
        var newEntity = new Grocery();
        newEntity.name = dto.getName();
        return newEntity;
    }
}
