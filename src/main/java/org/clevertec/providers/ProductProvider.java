package org.clevertec.providers;

import org.clevertec.models.Product;

import java.util.List;
import java.util.Optional;

public interface ProductProvider extends Provider<Product> {

    Optional<Product> find(int id);

    List<Product> findAllWithPagination(int page, int pageSize);
}
