package org.clevertec.providers;

import org.clevertec.models.ProductInReceipt;
import org.clevertec.models.Receipt;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ReceiptDBProvider implements ReceiptProvider {
    private static final String COLUMN_ID = "receipt_id";
    private static final String COLUMN_DISCOUNT_CARD_ID = "discount_card_id";
    private static final String COLUMN_TOTAL_PRICE = "total_price";
    private static final String COLUMN_TOTAL_PRICE_WITH_DISCOUNT = "total_price_with_discount";
    private static final String COLUMN_CASHIER = "cashier";
    private static final String COLUMN_DATE = "receipt_date";
    private static final String COLUMN_TIME = "receipt_time";
    private static final String COLUMN_PRODUCT_ID = "product_id";
    private static final String COLUMN_UID = "uid";
    private static final String COLUMN_QUANTITY = "quantity";
    private static final String COLUMN_PRODUCT_PRICE = "price";
    private static final String COLUMN_PRODUCT_TOTAL_PRICE = "total_price";
    private static final String SELECT_RECEIPTS_SQL = "select * from receipt";
    private static final String SELECT_RECEIPT_SQL = "select * from receipt where " + COLUMN_ID + " = ?";
    private static final String SELECT_PRODUCTS_IN_RECEIPT_SQL = "select * from product_in_receipt where " + COLUMN_ID + " = ?";
    private static final String INSERT_RECEIPT_SQL = "insert into receipt" + "(" + COLUMN_ID + ", " + COLUMN_DISCOUNT_CARD_ID + ", " + COLUMN_TOTAL_PRICE + ", " + COLUMN_TOTAL_PRICE_WITH_DISCOUNT + ", " + COLUMN_CASHIER + ", " + COLUMN_DATE + ", " + COLUMN_TIME + ") values (?, ?, ?, ?, ?, ?, ?)";
    private static final String INSERT_PRODUCTS_IN_RECEIPT_SQL = "insert into product_in_receipt" + "(" + COLUMN_ID + ", " + COLUMN_PRODUCT_ID + ", " + COLUMN_QUANTITY + ", " + COLUMN_PRODUCT_PRICE + ", " + COLUMN_PRODUCT_TOTAL_PRICE + ") values (?, ?, ?, ?, ?)";
    private static final String UPDATE_RECEIPT_SQL = "update receipt set " + COLUMN_DISCOUNT_CARD_ID + " = ?, " + COLUMN_TOTAL_PRICE + " = ?, " + COLUMN_TOTAL_PRICE_WITH_DISCOUNT + " = ?, " + COLUMN_CASHIER + " = ?, " + COLUMN_DATE + " = ?, " + COLUMN_TIME + " = ?" + " where " + COLUMN_ID + " = ?";
    private static final String DELETE_BY_ID_RECEIPT_SQL = "delete from receipt where " + COLUMN_ID + " = ?";
    private final RepositoryDatabase database;
    private static ReceiptDBProvider instance;

    private ReceiptDBProvider(RepositoryDatabase database) {
        this.database = database;
    }

    public static ReceiptDBProvider getInstance(RepositoryDatabase database) {
        if (instance == null) {
            instance = new ReceiptDBProvider(database);
        }
        return instance;
    }

    @Override
    public Receipt find(int id) {
        Receipt receipt = null;
        try (PreparedStatement ps = database.getConnection().prepareStatement(SELECT_RECEIPT_SQL)) {
            ps.setInt(1, id);
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                receipt = new Receipt.ReceiptBuilder()
                        .withId(resultSet.getInt(COLUMN_ID))
                        .withDiscountCard(resultSet.getInt(COLUMN_DISCOUNT_CARD_ID))
                        .withTotalPrice(resultSet.getDouble(COLUMN_TOTAL_PRICE))
                        .withTotalPriceWithDiscount(resultSet.getDouble(COLUMN_TOTAL_PRICE_WITH_DISCOUNT))
                        .withCashier(resultSet.getInt(COLUMN_CASHIER))
                        .withDateTime(LocalDate.now(), LocalTime.now())
                        .withDateTime(resultSet.getDate(COLUMN_DATE).toLocalDate(), resultSet.getTime(COLUMN_TIME).toLocalTime())
                        .withPositions(getProductsInReceipt(id))
                        .build();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return receipt;
    }

    /**
     * Returns all product ids in specified stock
     *
     * @param id id of stock
     * @return List with product ids
     */
    private List<ProductInReceipt> getProductsInReceipt(int id) {
        List<ProductInReceipt> positionList = new ArrayList<>();

        try (PreparedStatement ps = database.getConnection().prepareStatement(SELECT_PRODUCTS_IN_RECEIPT_SQL)) {
            ps.setInt(1, id);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                ProductInReceipt position = new ProductInReceipt(
                        resultSet.getInt(COLUMN_UID),
                        resultSet.getInt(COLUMN_PRODUCT_ID),
                        resultSet.getInt(COLUMN_QUANTITY),
                        resultSet.getDouble(COLUMN_PRODUCT_PRICE),
                        resultSet.getDouble(COLUMN_PRODUCT_TOTAL_PRICE)
                );
                positionList.add(position);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return positionList;
    }

    @Override
    public List<Receipt> findAll() {
        List<Receipt> receiptList = new ArrayList<>();
        try (PreparedStatement ps = database.getConnection().prepareStatement(SELECT_RECEIPTS_SQL);
             ResultSet resultSet = ps.executeQuery()) {
            while (resultSet.next()) {
                int receiptId = resultSet.getInt(COLUMN_ID);
                Receipt receipt = new Receipt.ReceiptBuilder()
                        .withId(resultSet.getInt(COLUMN_ID))
                        .withDiscountCard(resultSet.getInt(COLUMN_DISCOUNT_CARD_ID))
                        .withTotalPrice(resultSet.getDouble(COLUMN_TOTAL_PRICE))
                        .withTotalPriceWithDiscount(resultSet.getDouble(COLUMN_TOTAL_PRICE_WITH_DISCOUNT))
                        .withCashier(resultSet.getInt(COLUMN_CASHIER))
                        .withDateTime(LocalDate.now(), LocalTime.now())
                        .withDateTime(resultSet.getDate(COLUMN_DATE).toLocalDate(), resultSet.getTime(COLUMN_TIME).toLocalTime())
                        .withPositions(getProductsInReceipt(receiptId))
                        .build();
                receiptList.add(receipt);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return receiptList;
    }

    @Override
    public int insert(Receipt entity) {
        int res;
        try (PreparedStatement ps = database.getConnection().prepareStatement(INSERT_RECEIPT_SQL)) {
            ps.setInt(1, entity.getId());
            ps.setInt(2, entity.getDiscountCardId());
            ps.setDouble(3, entity.getTotalPrice());
            ps.setDouble(4, entity.getTotalPriceWithDiscount());
            ps.setInt(5, entity.getCashier());
            ps.setDate(6, Date.valueOf(entity.getDate()));
            ps.setTime(7, Time.valueOf(entity.getTime()));
            res = ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        insertPositionsInReceipt(entity.getId(), entity.getPositions());

        return res;
    }

    private int insertPositionsInReceipt(int receiptId, List<ProductInReceipt> positions) {
        int res = 0;
        try (PreparedStatement ps = database.getConnection().prepareStatement(INSERT_PRODUCTS_IN_RECEIPT_SQL)) {
            for (ProductInReceipt position : positions) {
                ps.setInt(1, receiptId);
                ps.setInt(2, position.getIdProduct());
                ps.setInt(3, position.getQuantity());
                ps.setDouble(4, position.getPrice());
                ps.setDouble(5, position.getTotal());
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
    public int update(Receipt entity) {
        int res;
        try (PreparedStatement ps = database.getConnection().prepareStatement(UPDATE_RECEIPT_SQL)) {
            ps.setInt(1, entity.getDiscountCardId());
            ps.setDouble(2, entity.getTotalPrice());
            ps.setDouble(3, entity.getTotalPriceWithDiscount());
            ps.setInt(4, entity.getCashier());
            ps.setDate(5, Date.valueOf(entity.getDate()));
            ps.setTime(6, Time.valueOf(entity.getTime()));
            ps.setInt(7, entity.getId());
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
        try (PreparedStatement ps = database.getConnection().prepareStatement(DELETE_BY_ID_RECEIPT_SQL)) {
            ps.setInt(1, id);
            res = ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return res;
    }
}
