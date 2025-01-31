package com.increff.server.flow;

import com.increff.commons.exception.ApiException;
import com.increff.server.entity.Inventory;
import com.increff.server.api.InventoryApi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.ArrayList;

@Service
@Transactional(rollbackFor = Exception.class)
public class InventoryFlow {

    @Autowired
    private InventoryApi inventoryApi;

    public Inventory addInventory(Inventory inventory) throws ApiException {
        return inventoryApi.addInventory(inventory);
    }

    public List<Inventory> getAllInventory() {
        return inventoryApi.getAllInventory();
    }

    public Inventory getInventoryById(Integer productId) throws ApiException {
        return inventoryApi.getInventoryById(productId);
    }

    public Inventory updateInventoryById(Integer productId, Inventory inventory) throws ApiException {
        return inventoryApi.updateInventoryById(productId, inventory);
    }

    @Transactional(rollbackFor = Exception.class)
    public List<Inventory> bulkAddInventory(List<Inventory> inventories) throws ApiException {
        List<Inventory> addedInventories = new ArrayList<>();
        for (Inventory inventory : inventories) {
            addedInventories.add(addInventory(inventory));
        }
        return addedInventories;
    }
}
