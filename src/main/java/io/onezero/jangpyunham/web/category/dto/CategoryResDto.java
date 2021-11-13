package io.onezero.jangpyunham.web.category.dto;

import io.onezero.jangpyunham.domain.grocery.Category;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Builder
@Getter
public class CategoryResDto {

    private Long id;
    private String name;

    public static CategoryResDto fromEntity(Category category) {
        return CategoryResDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}
