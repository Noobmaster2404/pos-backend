package com.increff.server.dto;

import com.increff.server.entity.Inventory;
import com.increff.server.entity.Product;
import com.increff.commons.model.InventoryForm;
import com.increff.commons.model.InventoryData;
import com.increff.server.flow.InventoryFlow;
import com.increff.server.flow.ProductFlow;
import com.increff.commons.exception.ApiException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class InventoryDto extends AbstractDto {
    
    @Autowired
    private InventoryFlow inventoryFlow;
    
    @Autowired
    private ProductFlow productFlow;

    public void add(InventoryForm form) throws ApiException {
        normalize(form);
        Product product = productFlow.get(form.getProductId());
        Inventory inventory = ConversionClass.convert(form, product);
        inventoryFlow.add(inventory);
    }

    public List<InventoryData> getAll() throws ApiException {
        return inventoryFlow.getAll()
                .stream()
                .map(ConversionClass::convert)
                .collect(Collectors.toList());
    }

    public InventoryData get(Integer inventoryId) throws ApiException {
        return ConversionClass.convert(inventoryFlow.get(inventoryId));
    }

    public InventoryData update(Integer inventoryId, InventoryForm form) throws ApiException {
        normalize(form);
        Product product = productFlow.get(form.getProductId());
        Inventory inventory = ConversionClass.convert(form, product);
        Inventory updatedInventory = inventoryFlow.update(inventoryId, inventory);
        return ConversionClass.convert(updatedInventory);
    }

    @Override
    protected String getPrefix() {
        return "Inventory: ";
    }
}
