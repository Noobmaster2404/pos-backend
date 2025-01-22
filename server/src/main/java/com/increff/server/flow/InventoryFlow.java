package com.increff.server.flow;

import com.increff.commons.exception.ApiException;
import com.increff.server.entity.Inventory;
import com.increff.server.api.InventoryApi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = ApiException.class)
public class InventoryFlow {

    @Autowired
    private InventoryApi inventoryApi;
    
    @Autowired

    // Protected method to be used only by ProductFlow
    protected void addInitial(Inventory inventory) throws ApiException {
        inventoryApi.add(inventory);
    }

    public void add(Inventory inventory) throws ApiException {
        inventoryApi.add(inventory);
    }

    public List<Inventory> getAll() {
        return inventoryApi.getAll();
    }

    public Inventory get(Integer id) throws ApiException {
        return inventoryApi.get(id);
    }

    public void update(Integer id, Inventory inventory) throws ApiException {
        inventoryApi.update(id, inventory);
    }
}
