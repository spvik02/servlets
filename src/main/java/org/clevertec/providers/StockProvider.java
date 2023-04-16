package org.clevertec.providers;

import org.clevertec.models.Stock;

import java.util.Optional;

public interface StockProvider extends Provider<Stock> {

    Optional<Stock> find(int idProduct);
}
