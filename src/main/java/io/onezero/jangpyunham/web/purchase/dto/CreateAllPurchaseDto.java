package io.onezero.jangpyunham.web.purchase.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateAllPurchaseDto {
    @NotEmpty
    private List<Long> groceryIds;
}
