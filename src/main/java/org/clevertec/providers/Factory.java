package org.clevertec.providers;

public class Factory {

    private static RepositoryDatabase database = RepositoryDatabase.getInstance();

    public static DiscountCardProvider getDiscountCardProvider() {
        return DiscountCardDBProvider.getInstance(database);
    }

    public static ProductProvider getProductProvider() {
        return ProductDBProvider.getInstance(database);
    }

    public static StockProvider getStockProvider() {
        return StockDBProvider.getInstance(database);
    }

    public static ReceiptProvider getReceiptProvider() {
        return ReceiptDBProvider.getInstance(database);
    }
}
