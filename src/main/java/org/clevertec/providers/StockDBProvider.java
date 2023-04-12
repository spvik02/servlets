package org.clevertec.providers;

import org.clevertec.models.Stock;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StockDBProvider implements StockProvider {
    private static final String COLUMN_ID = "stock_id";
    private static final String COLUMN_PRODUCT_ID = "product_id";
    private static final String COLUMN_QUANTITY = "quantity";
    private static final String COLUMN_SALE = "sale";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String SELECT_STOCKS_SQL = "select * from stock";
    private static final String SELECT_STOCK_SQL = "select * from stock where " + COLUMN_ID + " = ?";
    private static final String SELECT_PRODUCTS_IN_STOCK_SQL = "select * from product_in_stock where " + COLUMN_ID + " = ?";
    private static final String INSERT_STOCK_SQL = "insert into stock" + "(" + COLUMN_ID + ", " + COLUMN_QUANTITY + ", " + COLUMN_SALE + ", " + COLUMN_DESCRIPTION + ") values (?, ?, ?, ?)";
    private static final String INSERT_PRODUCTS_IN_STOCK_SQL = "insert into product_in_stock" + "(" + COLUMN_ID + ", " + COLUMN_PRODUCT_ID + ") values (?, ?)";
    private static final String UPDATE_STOCK_SQL = "update stock set " + COLUMN_QUANTITY + " = ?, " + COLUMN_SALE + " = ?, " + COLUMN_DESCRIPTION + " = ? where " + COLUMN_ID + " = ?";
    private static final String DELETE_BY_ID_STOCK_SQL = "delete from stock where " + COLUMN_ID + " = ?";
    private final RepositoryDatabase database;
    private static StockDBProvider instance;

    private StockDBProvider(RepositoryDatabase database) {
        this.database = database;
    }

    public static StockDBProvider getInstance(RepositoryDatabase database) {
        if (instance == null) {
            instance = new StockDBProvider(database);
        }
        return instance;
    }

    @Override
    public Optional<Stock> find(int id) {
        Stock stock = null;
        try (PreparedStatement ps = database.getConnection().prepareStatement(SELECT_STOCK_SQL)) {
            ps.setInt(1, id);
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                stock = new Stock(
                        resultSet.getInt(COLUMN_ID),
                        resultSet.getInt(COLUMN_QUANTITY),
                        resultSet.getInt(COLUMN_SALE),
                        resultSet.getString(COLUMN_DESCRIPTION),
                        getProductsInStock(id));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(stock);
    }

    /**
     * Returns all product ids in specified stock
     *
     * @param id id of stock
     * @return List with product ids
     */
    private List<Integer> getProductsInStock(int id) {
        List<Integer> productList = new ArrayList<>();

        try (PreparedStatement ps = database.getConnection().prepareStatement(SELECT_PRODUCTS_IN_STOCK_SQL)) {
            ps.setInt(1, id);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                productList.add(resultSet.getInt(COLUMN_PRODUCT_ID));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return productList;
    }

    @Override
    public List<Stock> findAll() {
        List<Stock> stockList = new ArrayList<>();
        try (PreparedStatement ps = database.getConnection().prepareStatement(SELECT_STOCKS_SQL);
             ResultSet resultSet = ps.executeQuery()) {
            while (resultSet.next()) {
                int stockId = resultSet.getInt(COLUMN_ID);
                Stock stock = new Stock(
                        stockId,
                        resultSet.getInt(COLUMN_QUANTITY),
                        resultSet.getInt(COLUMN_SALE),
                        resultSet.getString(COLUMN_DESCRIPTION),
                        getProductsInStock(stockId));
                stockList.add(stock);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return stockList;
    }

    @Override
    public int insert(Stock entity) {
        int res;
        try (PreparedStatement ps = database.getConnection().prepareStatement(INSERT_STOCK_SQL)) {
            ps.setInt(1, entity.getId());
            ps.setInt(2, entity.getQuantity());
            ps.setInt(3, entity.getSale());
            ps.setString(4, entity.getDescription());
            res = ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        insertProductsInStock(entity.getId(), entity.getProducts());

        return res;
    }

    private int insertProductsInStock(int stockId, List<Integer> productIds) {
        int res = 0;
        try (PreparedStatement ps = database.getConnection().prepareStatement(INSERT_PRODUCTS_IN_STOCK_SQL)) {
            for (int id : productIds) {
                ps.setInt(1, stockId);
                ps.setInt(2, id);
                res += ps.executeUpdate();
            }
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
    public int update(Stock entity) {
        int res;
        try (PreparedStatement ps = database.getConnection().prepareStatement(UPDATE_STOCK_SQL)) {
            ps.setInt(1, entity.getQuantity());
            ps.setInt(2, entity.getSale());
            ps.setString(3, entity.getDescription());
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
        try (PreparedStatement ps = database.getConnection().prepareStatement(DELETE_BY_ID_STOCK_SQL)) {
            ps.setInt(1, id);
            res = ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return res;
    }
}
