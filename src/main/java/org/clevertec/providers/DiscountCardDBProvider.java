package org.clevertec.providers;

import org.clevertec.models.DiscountCard;
import org.clevertec.models.DiscountCardClassic;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DiscountCardDBProvider implements DiscountCardProvider {

    private static final String COLUMN_ID = "discount_card_id";
    private static final String COLUMN_DISCOUNT = "percentage_of_discount";
    private static final String COLUMN_REGISTRATION_DATE = "date_of_registration";
    private static final String SELECT_DISCOUNT_CARDS_SQL = "select * from discount_card";
    private static final String SELECT_DISCOUNT_CARD_SQL = "select * from discount_card where " + COLUMN_ID + " = ?";
    private static final String INSERT_DISCOUNT_CARD_SQL = "insert into discount_card" + "(" + COLUMN_ID + ", " + COLUMN_DISCOUNT + ", " + COLUMN_REGISTRATION_DATE + ") " + "values (?, ?, ?)";
    private static final String UPDATE_DISCOUNT_CARD_SQL = "update discount_card set " + COLUMN_DISCOUNT + " = ?, " + COLUMN_REGISTRATION_DATE + " = ? where " + COLUMN_ID + " = ?";
    private static final String DELETE_BY_ID_DISCOUNT_CARD_SQL = "delete from discount_card where " + COLUMN_ID + " = ?";
    private final RepositoryDatabase database;
    private static DiscountCardDBProvider instance;

    private DiscountCardDBProvider(RepositoryDatabase database) {
        this.database = database;
    }

    public static DiscountCardDBProvider getInstance(RepositoryDatabase database) {
        if (instance == null) {
            instance = new DiscountCardDBProvider(database);
        }
        return instance;
    }

    @Override
    public DiscountCard find(int id) {
        DiscountCard card = null;
        try (PreparedStatement ps = database.getConnection().prepareStatement(SELECT_DISCOUNT_CARD_SQL)) {
            ps.setInt(1, id);
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                card = new DiscountCardClassic(
                        resultSet.getInt(COLUMN_ID),
                        resultSet.getDouble(COLUMN_DISCOUNT),
                        resultSet.getDate(COLUMN_REGISTRATION_DATE).toLocalDate());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return card;
    }

    @Override
    public List<DiscountCard> findAll() {
        List<DiscountCard> cardList = new ArrayList<>();
        try (PreparedStatement ps = database.getConnection().prepareStatement(SELECT_DISCOUNT_CARDS_SQL);
             ResultSet resultSet = ps.executeQuery()) {
            while (resultSet.next()) {
                DiscountCard card = new DiscountCardClassic(
                        resultSet.getInt(COLUMN_ID),
                        resultSet.getDouble(COLUMN_DISCOUNT),
                        resultSet.getDate(COLUMN_REGISTRATION_DATE).toLocalDate());
                cardList.add(card);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return cardList;
    }

    @Override
    public int insert(DiscountCard entity) {
        int res;
        try (PreparedStatement ps = database.getConnection().prepareStatement(INSERT_DISCOUNT_CARD_SQL)) {
            ps.setInt(1, entity.getId());
            ps.setDouble(2, entity.getPercentageOfDiscount());
            ps.setDate(3, Date.valueOf(entity.getDateOfRegistration()));
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
    public int update(DiscountCard entity) {
        int res;
        try (PreparedStatement ps = database.getConnection().prepareStatement(UPDATE_DISCOUNT_CARD_SQL)) {
            ps.setDouble(1, entity.getPercentageOfDiscount());
            ps.setDate(2, Date.valueOf(entity.getDateOfRegistration()));
            ps.setInt(3, entity.getId());
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
        try (PreparedStatement ps = database.getConnection().prepareStatement(DELETE_BY_ID_DISCOUNT_CARD_SQL)) {
            ps.setInt(1, id);
            res = ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return res;
    }
}
