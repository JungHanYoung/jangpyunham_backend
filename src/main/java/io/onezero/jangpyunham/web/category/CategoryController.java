package io.onezero.jangpyunham.web.category;

import io.onezero.jangpyunham.domain.grocery.Category;
import io.onezero.jangpyunham.service.GroceryService;
import io.onezero.jangpyunham.web.category.dto.CategoryResDto;
import io.onezero.jangpyunham.web.category.dto.CreateCategoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {

    private final GroceryService groceryService;

    @GetMapping
    public List<CategoryResDto> findAll() {
        return groceryService.findAllCategories()
                .stream().map(category -> CategoryResDto.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .build())
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody CreateCategoryDto dto) {
        var category = Category.fromCreateDto(dto);
        groceryService.createCategory(category);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
