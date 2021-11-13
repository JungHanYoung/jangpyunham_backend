package io.onezero.jangpyunham.web.purchase;

import io.onezero.jangpyunham.service.PurchaseService;
import io.onezero.jangpyunham.web.purchase.dto.CreateAllPurchaseDto;
import io.onezero.jangpyunham.web.purchase.dto.CreatePurchaseDto;
import io.onezero.jangpyunham.web.purchase.dto.PurchaseResDto;
import io.onezero.jangpyunham.web.validation.IncludeStringArr;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequestMapping("/purchase")
@RestController
@RequiredArgsConstructor
@Validated
public class PurchaseController {

    private final PurchaseService purchaseService;

    @GetMapping
    public List<PurchaseResDto> findAll(
            @RequestParam("completed")Optional<Boolean> completed,
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "10") int limit,
            @RequestParam(value = "order", defaultValue = "asc")
                    @IncludeStringArr(checkStrings = {"asc", "desc"}) String order
    ) {
        return completed.map(aBoolean -> purchaseService.findAll(aBoolean, offset, limit)
                .stream()
                .map(PurchaseResDto::fromEntity)
                .collect(Collectors.toList()))
                    .orElseGet(() -> purchaseService.findAll(offset, limit)
                    .stream()
                    .map(PurchaseResDto::fromEntity)
                    .collect(Collectors.toList()));
    }

    @PostMapping
    public ResponseEntity<PurchaseResDto> create(@RequestBody CreatePurchaseDto dto) {
        var created = purchaseService.create(dto.getGroceryId());
        return new ResponseEntity<>(PurchaseResDto.fromEntity(created), HttpStatus.CREATED);
    }

    @PostMapping("/all")
    public ResponseEntity<List<PurchaseResDto>> createAll(@RequestBody CreateAllPurchaseDto dto) {
        purchaseService.createAll(dto.getGroceryIds());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/complete")
    public ResponseEntity<Long> complete(@RequestParam("id") Long id) {
        return ResponseEntity.ok(purchaseService.complete(id));
    }

    @PatchMapping("/complete/all")
    public ResponseEntity<List<Long>> completeAll(@RequestParam("ids") @NotEmpty List<Long> ids) {
        return ResponseEntity.ok(purchaseService.completeAll(ids));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Long> delete(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseService.deleteById(id));
    }

}
