package com.increff.server.api;

import java.util.List;
import java.util.Objects;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.increff.server.dao.InventoryDao;
import com.increff.server.entity.Inventory;
import com.increff.commons.exception.ApiException;

@Service
public class InventoryApi {

    @Autowired
    private InventoryDao dao;

    @Transactional(rollbackFor = Exception.class)
    public Inventory addInventory(Inventory inventory) throws ApiException {
        Inventory existingInventory = dao.selectByProductId(inventory.getProduct().getProductId());
        if (Objects.isNull(existingInventory)) {
            existingInventory = new Inventory();
            existingInventory.setProduct(inventory.getProduct());
            existingInventory.setQuantity(0);
            dao.insert(existingInventory);
        }

        existingInventory.setQuantity(existingInventory.getQuantity() + inventory.getQuantity());
        dao.update(existingInventory);

        return existingInventory;
    }

    @Transactional(readOnly = true)
    public List<Inventory> getAllInventory(Integer page) {
        return dao.selectAll(page);
    }

    @Transactional(rollbackFor = Exception.class)
    public Inventory updateInventoryById(Integer inventoryId, Inventory inventory) throws ApiException {
        Inventory existingInventory = dao.select(inventoryId);
        if (Objects.isNull(existingInventory)) {
            throw new ApiException("Inventory with id " + inventoryId + " not found");
        }

        existingInventory.setQuantity(inventory.getQuantity());
        dao.update(existingInventory);
        return existingInventory;
    }

    @Transactional(readOnly = true)
    public Inventory getCheckInventoryByProductId(Integer productId) throws ApiException {
        Inventory inventory = dao.selectByProductId(productId);
        if (Objects.isNull(inventory)) {
            throw new ApiException("Inventory with id " + productId + " not found");
        }
        return inventory;
    }

    @Transactional(readOnly = true)
    public long getTotalCount() {
        return dao.count();
    }
}
