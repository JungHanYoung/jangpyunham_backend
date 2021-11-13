package io.onezero.jangpyunham.web.grocery;

import io.onezero.jangpyunham.domain.grocery.Grocery;
import io.onezero.jangpyunham.service.GroceryService;
import io.onezero.jangpyunham.web.grocery.dto.CreateGroceryDto;
import io.onezero.jangpyunham.web.grocery.dto.GroceryResDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequestMapping("/grocery")
@RestController
@RequiredArgsConstructor
public class GroceryController {

    private final GroceryService groceryService;

    @GetMapping
    public List<GroceryResDto> findAll(@RequestParam(value = "category", required = false) Long categoryId) {
        if(categoryId != null) {
            return groceryService.findByCategoryId(categoryId)
                    .stream().map(GroceryResDto::fromEntity)
                    .collect(Collectors.toList());
        }
        return groceryService.findAll()
                .stream().map(GroceryResDto::fromEntity)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody @Valid CreateGroceryDto dto) {
        groceryService.createGrocery(dto.getName(), dto.getCategoryId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        groceryService.delete(id);
        return ResponseEntity.noContent().build();
    }


}
