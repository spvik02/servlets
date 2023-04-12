package org.clevertec.providers;

import java.util.List;

public interface Provider<T> {
    List<T> findAll();

    int update(T entity);

    int insert(T entity);

    int remove(int id);
}
