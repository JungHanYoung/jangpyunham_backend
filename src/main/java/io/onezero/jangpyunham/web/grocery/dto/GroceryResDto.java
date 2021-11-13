package io.onezero.jangpyunham.web.grocery.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.onezero.jangpyunham.domain.grocery.Grocery;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Builder
@Getter
public class GroceryResDto {

    private Long id;
    private String name;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate recentlyUsed;

    public static GroceryResDto fromEntity(Grocery entity) {
        return GroceryResDto.builder()
                .id(entity.getId())
                .recentlyUsed(entity.getRecentlyUsed())
                .name(entity.getName())
                .build();
    }
}
