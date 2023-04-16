package org.clevertec.providers;

import org.clevertec.models.Product;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductDBProvider implements ProductProvider {

    private static final String COLUMN_ID = "product_id";
    private static final String COLUMN_NAME = "product_name";
    private static final String COLUMN_PRICE = "price";
    private static final String COLUMN_DISCOUNTABLE = "is_at_discount";
    private static final String SELECT_PRODUCTS_SQL = "select * from product";
    private static final String SELECT_LIMIT_PRODUCTS_SQL = "select * from product order by " + COLUMN_ID + " offset ? limit ?";
    private static final String SELECT_PRODUCT_SQL = "select * from product where " + COLUMN_ID + " = ?";
    private static final String INSERT_PRODUCT_SQL = "insert into product" + "(" + COLUMN_ID + ", " + COLUMN_NAME + ", " + COLUMN_PRICE + ", " + COLUMN_DISCOUNTABLE + ") " + "values (?, ?, ?, ?)";
    private static final String UPDATE_PRODUCT_SQL = "update product set " + COLUMN_NAME + " = ?, " + COLUMN_PRICE + " = ?, " + COLUMN_DISCOUNTABLE + " = ? where " + COLUMN_ID + " = ?";
    private static final String DELETE_BY_ID_PRODUCT_SQL = "delete from product where " + COLUMN_ID + " = ?";
    private final RepositoryDatabase database;
    private static ProductDBProvider instance;

    private ProductDBProvider(RepositoryDatabase database) {
        this.database = database;
    }

    public static ProductDBProvider getInstance(RepositoryDatabase database) {
        if (instance == null) {
            instance = new ProductDBProvider(database);
        }
        return instance;
    }

    @Override
    public Optional<Product> find(int id) {
        Product product = null;
        try (PreparedStatement ps = database.getConnection().prepareStatement(SELECT_PRODUCT_SQL)) {
            ps.setInt(1, id);
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                product = new Product(
                        resultSet.getInt(COLUMN_ID),
                        resultSet.getString(COLUMN_NAME),
                        resultSet.getDouble(COLUMN_PRICE),
                        resultSet.getBoolean(COLUMN_DISCOUNTABLE));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(product);
    }

    @Override
    public List<Product> findAll() {
        List<Product> productList = new ArrayList<>();
        try (PreparedStatement ps = database.getConnection().prepareStatement(SELECT_PRODUCTS_SQL);
             ResultSet resultSet = ps.executeQuery()) {
            while (resultSet.next()) {
                Product product = new Product(
                        resultSet.getInt(COLUMN_ID),
                        resultSet.getString(COLUMN_NAME),
                        resultSet.getDouble(COLUMN_PRICE),
                        resultSet.getBoolean(COLUMN_DISCOUNTABLE));
                productList.add(product);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return productList;
    }

    /**
     * Writes to response all products with offset page * pageSize and limit pageSize
     *
     * @param page     used as a multiplier for offset. Since in ui pages are numerated starting from 1, there are --page in calculations
     * @param pageSize num of records to select
     * @return
     */
    @Override
    public List<Product> findAllWithPagination(int page, int pageSize) {
        List<Product> productList = new ArrayList<>();
        try (PreparedStatement ps = database.getConnection().prepareStatement(SELECT_LIMIT_PRODUCTS_SQL)) {
            ps.setInt(1, --page * pageSize);
            ps.setInt(2, pageSize);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                Product product = new Product(
                        resultSet.getInt(COLUMN_ID),
                        resultSet.getString(COLUMN_NAME),
                        resultSet.getDouble(COLUMN_PRICE),
                        resultSet.getBoolean(COLUMN_DISCOUNTABLE));
                productList.add(product);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return productList;
    }

    /**
     * Inserts passed product into product table
     *
     * @param entity product to be inserted
     * @return 1 if product was inserted, otherwise 0
     */
    @Override
    public int insert(Product entity) {
        int res;
        try (PreparedStatement ps = database.getConnection().prepareStatement(INSERT_PRODUCT_SQL)) {
            ps.setInt(1, entity.getId());
            ps.setString(2, entity.getName());
            ps.setDouble(3, entity.getPrice());
            ps.setBoolean(4, entity.isAtDiscount());
            res = ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return res;
    }

    /***
     * Updates all fields except id
     * @param entity with new data for update
     * @return 1 if entity was updated, 0 if element with the specified id wasn't found
     */
    @Override
    public int update(Product entity) {
        int res;
        try (PreparedStatement ps = database.getConnection().prepareStatement(UPDATE_PRODUCT_SQL)) {
            ps.setString(1, entity.getName());
            ps.setDouble(2, entity.getPrice());
            ps.setBoolean(3, entity.isAtDiscount());
            ps.setInt(4, entity.getId());
            res = ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return res;
    }

    /***
     * Removes element with specified id
     * @param id id of the element to be deleted
     * @return 1 if the element with passed id was deleted, otherwise 0
     */
    @Override
    public int remove(int id) {
        int res;
        try (PreparedStatement ps = database.getConnection().prepareStatement(DELETE_BY_ID_PRODUCT_SQL)) {
            ps.setInt(1, id);
            res = ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return res;
    }
}
