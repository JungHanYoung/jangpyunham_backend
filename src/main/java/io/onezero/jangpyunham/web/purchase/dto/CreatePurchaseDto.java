package io.onezero.jangpyunham.web.purchase.dto;

import lombok.Builder;
import lombok.Data;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreatePurchaseDto {
    private Long groceryId;
}
