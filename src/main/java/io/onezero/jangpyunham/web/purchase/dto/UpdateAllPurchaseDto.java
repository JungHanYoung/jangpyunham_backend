package io.onezero.jangpyunham.web.purchase.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Builder
@Getter
public class UpdateAllPurchaseDto {
    private List<UpdatePurchaseDto> purchaseList;
}
