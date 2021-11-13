package io.onezero.jangpyunham.service;

import io.onezero.jangpyunham.domain.grocery.Category;
import io.onezero.jangpyunham.domain.grocery.CategoryRepository;
import io.onezero.jangpyunham.domain.grocery.Grocery;
import io.onezero.jangpyunham.domain.grocery.GroceryRepository;
import io.onezero.jangpyunham.web.category.dto.CategoryResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class GroceryService {

    private final GroceryRepository groceryRepository;
    private final CategoryRepository categoryRepository;

    public List<Grocery> findAll() {
        return groceryRepository.findAll();
    }

    public List<Grocery> findByCategoryId(Long categoryId) {
        return groceryRepository.findByCategoryId(categoryId);
    }

    public List<Category> findAllCategories() {
        return categoryRepository.findAll();
    }

    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    public Grocery createGrocery(String name, Long categoryId) {
        var category = categoryRepository.findById(categoryId);
        var newGrocery = new Grocery();
        newGrocery.setName(name);
        newGrocery.setCategory(category.orElseThrow());

        return groceryRepository.save(newGrocery);
    }

    public boolean delete(long groceryId) {
        try {
            groceryRepository.deleteById(groceryId);
            return true;
        } catch(Exception e) {
            return false;
        }
    }
}
