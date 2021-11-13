package io.onezero.jangpyunham.web.purchase.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class UpdatePurchaseDto {
    private Long id;
    private boolean completed;
}
