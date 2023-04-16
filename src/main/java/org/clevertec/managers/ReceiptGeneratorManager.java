package org.clevertec.managers;

import org.clevertec.models.Receipt;
import org.clevertec.providers.DiscountCardProvider;
import org.clevertec.providers.Factory;
import org.clevertec.providers.ProductProvider;
import org.clevertec.providers.StockProvider;
import org.clevertec.utils.ParametersUtil;

import java.time.LocalDate;
import java.time.LocalTime;

public class ReceiptGeneratorManager {
    public Receipt generateReceiptFromParameters(String[] parameters) {

        ProductProvider productProvider = Factory.getProductProvider();
        StockProvider stockProvider = Factory.getStockProvider();
        DiscountCardProvider discountCardProvider = Factory.getDiscountCardProvider();

        Receipt receipt = new Receipt.ReceiptBuilder()
                .withCashier(717)
                .withDateTime(LocalDate.now(), LocalTime.now())
                .build();

        ParametersUtil.parseParameters(parameters, receipt);

        receipt.calculateTotal(productProvider, stockProvider, discountCardProvider);
        return receipt;
    }
}
