package io.onezero.jangpyunham.domain.grocery;

import io.onezero.jangpyunham.domain.BaseEntity;
import io.onezero.jangpyunham.web.category.dto.CreateCategoryDto;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "category")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Category extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    public static Category fromCreateDto(CreateCategoryDto dto) {
        var category = new Category();
        category.setName(dto.getName());
        return category;
    }
}
