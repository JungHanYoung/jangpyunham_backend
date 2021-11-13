package io.onezero.jangpyunham.service;

import io.onezero.jangpyunham.domain.grocery.GroceryRepository;
import io.onezero.jangpyunham.domain.purchase.Purchase;
import io.onezero.jangpyunham.domain.purchase.PurchaseRepository;
import io.onezero.jangpyunham.exception.ResourceNotFoundException;
import io.onezero.jangpyunham.web.purchase.dto.CreateAllPurchaseDto;
import io.onezero.jangpyunham.web.purchase.dto.UpdatePurchaseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final GroceryRepository groceryRepository;

    public Page<Purchase> findAll(int offset, int limit) {
        return purchaseRepository.findAll(PageRequest.of(offset / limit, limit, Sort.by(Sort.Order.desc("updatedAt"))));
    }

    public List<Purchase> findAll(boolean completed, int offset, int limit) {
        return purchaseRepository.findByCompleted(completed, PageRequest.of(offset / limit, limit, Sort.by(Sort.Order.desc("updatedAt"))));
    }

    public Purchase create(Long groceryId) {
        var purchase = new Purchase();
        var grocery = groceryRepository.findById(groceryId).orElseThrow(ResourceNotFoundException::new);
        purchase.setGrocery(grocery);
        grocery.setRecentlyUsed(LocalDate.now());
        return purchaseRepository.save(purchase);
    }

    public List<Purchase> createAll(List<Long> groceryIds) {

        return groceryIds
                .stream()
                .map(this::create)
                .collect(Collectors.toList());
    }

    public List<Purchase> findByUncompleted() {
        return purchaseRepository.findByUncompleted();
    }

    public Purchase update(UpdatePurchaseDto dto) {
        var purchase = purchaseRepository.findById(dto.getId())
                .orElseThrow();
        purchase.setCompleted(dto.isCompleted());
        return purchaseRepository.save(purchase);
    }

    public Long complete(Long purchaseId) {
        var purchase = purchaseRepository.findById(purchaseId);
        return purchase.map(entity -> {
            entity.setCompleted(true);
            purchaseRepository.save(entity);
            return entity.getId();
        }).orElseThrow(ResourceNotFoundException::new);
    }

    public List<Long> completeAll(List<Long> purchaseIds) {
        return purchaseIds
                .stream()
                .map(this::complete)
                .collect(Collectors.toList());
    }

    public long deleteById(long id) {
        try {
            purchaseRepository.deleteById(id);
        } catch(Exception e) {
            throw new ResourceNotFoundException();
        }
        return id;
    }
}
