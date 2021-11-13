package io.onezero.jangpyunham.web.purchase.dto;

import io.onezero.jangpyunham.domain.grocery.Grocery;
import io.onezero.jangpyunham.domain.purchase.Purchase;
import io.onezero.jangpyunham.web.grocery.dto.GroceryResDto;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Builder
@Getter
public class PurchaseResDto {

    private Long id;
    private boolean completed;
    private GroceryResDto grocery;

    public static PurchaseResDto fromEntity(Purchase entity) {
        return PurchaseResDto.builder()
                .id(entity.getId())
                .completed(entity.isCompleted())
                .grocery(GroceryResDto.fromEntity(entity.getGrocery()))
                .build();
    }

}
