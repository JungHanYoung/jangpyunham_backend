package io.onezero.jangpyunham.web.grocery.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateGroceryDto {

    @NotBlank
    private String name;
    private Long categoryId;
}
