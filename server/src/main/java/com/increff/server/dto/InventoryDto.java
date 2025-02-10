package com.increff.server.dto;

import com.increff.server.entity.Inventory;
import com.increff.server.entity.Product;
import com.increff.commons.model.InventoryForm;
import com.increff.commons.model.InventoryData;
import com.increff.server.flow.InventoryFlow;
import com.increff.server.helper.ConversionHelper;
import com.increff.server.api.ProductApi;
import com.increff.commons.exception.ApiException;
import com.increff.commons.model.PaginatedData;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.Objects;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.ArrayList;

@Service
public class InventoryDto extends AbstractDto {
    
    @Autowired
    private InventoryFlow inventoryFlow;
    
    @Autowired
    private ProductApi productApi;

    @Value("${PAGE_SIZE}")
    private Integer PAGE_SIZE;

    public InventoryData addInventory(InventoryForm form) throws ApiException {
        checkValid(form);
        normalize(form);
        Product product = productApi.getCheckProductByBarcode(form.getBarcode());
        Inventory inventory = ConversionHelper.convertToInventory(form, product);

        Inventory addedInventory=inventoryFlow.addInventory(inventory);

        return ConversionHelper.convertToInventoryData(addedInventory,product.getBarcode());
    }

    public PaginatedData<InventoryData> getAllInventory(Integer page) throws ApiException {
        List<Inventory> inventories = inventoryFlow.getAllInventory(page);

        List<Integer> productIds = inventories.stream()
            .map(inventory -> inventory.getProduct().getProductId())
            .collect(Collectors.toList());

        Map<Integer, String> productIdBarcodeMap = productApi.getBarcodesByProductIds(productIds);
        List<InventoryData> inventoryData = ConversionHelper.convertToInventoryData(inventories, productIdBarcodeMap);

        return new PaginatedData<>(inventoryData, page, PAGE_SIZE);
    }

    public InventoryData getInventoryById(Integer productId) throws ApiException {
        Inventory inventory = inventoryFlow.getInventoryByProductId(productId);
        Product product = productApi.getCheckProductById(productId);
        
        return ConversionHelper.convertToInventoryData(inventory, product.getBarcode());
    }

    public InventoryData getInventoryByBarcode(String barcode) throws ApiException {
        Product product = productApi.getCheckProductByBarcode(barcode);
        Inventory inventory = inventoryFlow.getInventoryByProductId(product.getProductId());

        return ConversionHelper.convertToInventoryData(inventory, product.getBarcode());
    }

    public InventoryData updateInventoryById(Integer inventoryId, InventoryForm form) throws ApiException {
        checkValid(form);
        normalize(form);
        Product product = productApi.getCheckProductByBarcode(form.getBarcode());

        Inventory inventory = ConversionHelper.convertToInventory(form, product);
        Inventory updatedInventory = inventoryFlow.updateInventoryById(inventoryId, inventory);

        return ConversionHelper.convertToInventoryData(updatedInventory, product.getBarcode());
    }

    public List<InventoryData> bulkAddInventory(List<InventoryForm> forms) throws ApiException {
        if (forms.size() > 5000) {
            throw new ApiException("Cannot process more than 5000 inventory items at once");
        }

        List<InventoryForm> mergedForms = normalizeBulkForms(forms);
        List<String> barcodes = mergedForms.stream()
            .map(InventoryForm::getBarcode)
            .collect(Collectors.toList());
            
        List<Product> products = productApi.getCheckProductsByBarcodes(barcodes);
        List<Inventory> inventories = ConversionHelper.convertToInventory(mergedForms, products);

        List<Inventory> addedInventories = inventoryFlow.bulkAddInventory(inventories);

        return ConversionHelper.convertToInventoryData(addedInventories, products);
    }

    private List<InventoryForm> normalizeBulkForms(List<InventoryForm> forms) throws ApiException {
        for (InventoryForm form : forms) {
            normalize(form);
            checkValid(form);
        }

        Map<String, InventoryForm> consolidatedForms = forms.stream()
            .collect(Collectors.groupingBy(
                InventoryForm::getBarcode,
                Collectors.reducing(
                    null,
                    (form1, form2) -> {
                        if (Objects.isNull(form1)) return form2;
                        
                        InventoryForm combined = new InventoryForm();
                        combined.setBarcode(form2.getBarcode());
                        combined.setQuantity(form1.getQuantity() + form2.getQuantity());
                        return combined;
                    }
                )
            ));

        return new ArrayList<>(consolidatedForms.values());
    }
}
