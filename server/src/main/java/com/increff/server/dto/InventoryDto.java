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

    public InventoryData addInventory(InventoryForm form) throws ApiException {
        checkValid(form);
        normalize(form);
        Product product = productFlow.getProductByBarcode(form.getBarcode());
        Inventory inventory = ConversionClass.convertToInventory(form, product);
        return ConversionClass.convertToInventoryData(inventoryFlow.addInventory(inventory));
    }

    public List<InventoryData> getAllInventory() throws ApiException {
        return inventoryFlow.getAllInventory()
                .stream()
                .map(inventory -> {
                    try {
                        return ConversionClass.convertToInventoryData(inventory);
                    } catch (ApiException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    public InventoryData getInventoryById(Integer productId) throws ApiException {
        return ConversionClass.convertToInventoryData(inventoryFlow.getInventoryById(productId));
    }

    public InventoryData updateInventoryById(Integer productId, InventoryForm form) throws ApiException {
        checkValid(form);
        normalize(form);
        Product product = productFlow.getProductByBarcode(form.getBarcode());
        Inventory inventory = ConversionClass.convertToInventory(form, product);
        Inventory updatedInventory = inventoryFlow.updateInventoryById(productId, inventory);
        return ConversionClass.convertToInventoryData(updatedInventory);
    }

    public List<InventoryData> bulkAddInventory(List<InventoryForm> forms) throws ApiException {
        if (forms.size() > 5000) {
            throw new ApiException(getPrefix() + "Cannot process more than 5000 inventory items at once");
        }

        for (InventoryForm form : forms) {
            normalize(form);
        }
        
        List<Inventory> addedInventory = inventoryFlow.bulkAddInventory(forms.stream()
                .map(form -> {
                    try {
                        Product product = productFlow.getProductByBarcode(form.getBarcode());
                        return ConversionClass.convertToInventory(form, product);
                    } catch (ApiException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList()));

        return addedInventory.stream()
                .map(inventory -> {
                    try {
                        InventoryData data = ConversionClass.convertToInventoryData(inventory);
                        Product product = inventory.getProduct();
                        data.setBarcode(product.getBarcode());
                        data.setProductId(product.getProductId());
                        data.setQuantity(inventory.getQuantity());
                        return data;
                    } catch (ApiException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    @Override
    protected String getPrefix() {
        return "Inventory: ";
    }
}
