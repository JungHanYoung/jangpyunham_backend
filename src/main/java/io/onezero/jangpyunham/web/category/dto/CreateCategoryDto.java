package io.onezero.jangpyunham.web.category.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCategoryDto {
    @NotBlank
    private String name;
}
