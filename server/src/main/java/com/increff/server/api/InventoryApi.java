package com.increff.server.api;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.increff.server.dao.InventoryDao;
import com.increff.server.entity.Inventory;
import com.increff.commons.exception.ApiException;

@Service
public class InventoryApi {

    @Autowired
    private InventoryDao dao;

    @Transactional(rollbackFor = ApiException.class)
    public void add(Inventory inventory) throws ApiException {
        checkValid(inventory);
        Optional<Inventory> existing = dao.selectByProductId(inventory.getProduct().getProductId());
        if (existing.isPresent()) {
            throw new ApiException("Inventory for product ID " + inventory.getProduct().getProductId() + " already exists");
        }
        dao.insert(inventory);
    }

    @Transactional(readOnly = true)
    public List<Inventory> getAll() {
        return dao.selectAll();
    }

    @Transactional(rollbackFor = ApiException.class)
    public void update(Integer id, Inventory inventory) throws ApiException {
        checkValid(inventory);
        Inventory existingInventory = get(id);
        
        existingInventory.setProduct(inventory.getProduct());
        existingInventory.setQuantity(inventory.getQuantity());
        dao.update(existingInventory);
    }

    @Transactional(readOnly = true)
    public Inventory get(Integer id) throws ApiException {
        Inventory inventory = dao.select(id);
        if (Objects.isNull(inventory)) {
            throw new ApiException("Inventory with id " + id + " not found");
        }
        return inventory;
    }

    private void checkValid(Inventory inventory) throws ApiException {
        if (Objects.isNull(inventory)) {
            throw new ApiException("Inventory cannot be null");
        }
        if (Objects.isNull(inventory.getProduct())) {
            throw new ApiException("Product reference cannot be null");
        }
        if (!StringUtils.hasText(inventory.getProductBarcode())) {
            throw new ApiException("Product barcode cannot be empty");
        }
        if (Objects.isNull(inventory.getQuantity())) {
            throw new ApiException("Quantity cannot be null");
        }
        if (inventory.getQuantity() < 0) {
            throw new ApiException("Quantity cannot be negative");
        }
    }
}
