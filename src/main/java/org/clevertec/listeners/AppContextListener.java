package org.clevertec.listeners;

import org.clevertec.providers.RepositoryDatabase;
import org.clevertec.utils.ParametersUtil;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

@WebListener
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("listener start");

        boolean createFlag;
        boolean insertFlag;
        boolean defaultFlagValue = false;
        Map<String, Object> yamlMap = ParametersUtil.getMapFromYaml("application.yml");


        if (yamlMap.containsKey("database") && yamlMap.get("database") != null) {
            Map<String, Object> dbMap = (Map<String, Object>) yamlMap.get("database");
            createFlag = (dbMap.containsKey("create") && dbMap.get("create") != null)
                    ? (boolean) dbMap.get("create")
                    : defaultFlagValue;
            insertFlag = (dbMap.containsKey("insert") && dbMap.get("insert") != null)
                    ? (boolean) dbMap.get("insert")
                    : defaultFlagValue;

            System.out.println(insertFlag);
        } else {
            createFlag = defaultFlagValue;
            insertFlag = defaultFlagValue;
        }
        Connection connection = RepositoryDatabase.getInstance().getConnection();
        if (createFlag) {
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("create table if not exists discount_card(\n" +
                        "\tdiscount_card_id bigint, \n" +
                        "\tpercentage_of_discount numeric(5, 2) not null,\n" +
                        "\tdate_of_registration date not null,\n" +
                        "\tconstraint discount_card_pk primary key (discount_card_id)\n" +
                        ");\n" +
                        "\n" +
                        "create table if not exists product(\n" +
                        "\tproduct_id bigint, \n" +
                        "\tproduct_name text not null,\n" +
                        "\tprice numeric(8, 2) not null,\n" +
                        "\tis_at_discount BOOLEAN not null DEFAULT true,\n" +
                        "\tconstraint product_pk primary key (product_id)\n" +
                        ");\n" +
                        "\n" +
                        "create table if not exists receipt(\n" +
                        "\treceipt_id bigint, \n" +
                        "\tdiscount_card_id bigint, \n" +
                        "\ttotal_price numeric(8, 2) not null,\n" +
                        "\ttotal_price_with_discount numeric(8, 2) not null,\n" +
                        "\tcashier int not null,\n" +
                        "\treceipt_date date not null,\n" +
                        "\treceipt_time time not null,\n" +
                        "\tconstraint receipt_pk primary key (receipt_id),\n" +
                        "\tconstraint discount_card_fk foreign key (discount_card_id)\n" +
                        "\t\treferences discount_card(discount_card_id)\n" +
                        ");\n" +
                        "\n" +
                        "create table if not exists product_in_receipt(\n" +
                        "\tuid SERIAL,\n" +
                        "\treceipt_id bigint,\n" +
                        "\tproduct_id bigint,\n" +
                        "\tquantity int not null,\n" +
                        "\tprice numeric(8, 2) not null,\n" +
                        "\ttotal_price numeric(8, 2) not null,\n" +
                        "\tconstraint pir_pk primary key (uid),\n" +
                        "\tconstraint receipt_fk foreign key (receipt_id)\n" +
                        "\t\treferences receipt(receipt_id) on delete cascade,\n" +
                        "\tconstraint product_fk foreign key (product_id)\n" +
                        "\t\treferences product(product_id)\n" +
                        ");\n" +
                        "\n" +
                        "create table if not exists stock(\n" +
                        "\tstock_id bigint,\n" +
                        "\tquantity int not null,\n" +
                        "\tsale numeric(5, 2) not null,\n" +
                        "\tdescription text not null,\n" +
                        "\tconstraint stock_pk primary key (stock_id)\n" +
                        ");\n" +
                        "\n" +
                        "create table if not exists product_in_stock(\n" +
                        "\tuid SERIAL,\n" +
                        "\tstock_id bigint,\n" +
                        "\tproduct_id bigint,\n" +
                        "\tconstraint pis_pk primary key (uid),\n" +
                        "\tconstraint stock_fk foreign key (stock_id)\n" +
                        "\t\treferences stock(stock_id) on delete cascade,\n" +
                        "\tconstraint product_fk foreign key (product_id)\n" +
                        "\t\treferences product(product_id)\n" +
                        ");");

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        if (insertFlag) {
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("insert into discount_card(discount_card_id, percentage_of_discount, date_of_registration)" +
                        "values (1, 10, '2018-10-23'), (2, 15, '2018-11-23')");
                statement.executeUpdate("insert into product(product_id, product_name, price, is_at_discount)" +
                        "values (1, 'product1', 1, false)," +
                        "\t(2, 'product2', 2, false),\n" +
                        "\t(3, 'product3', 2, false),\n" +
                        "\t(4, 'product4', 3, false),\n" +
                        "\t(5, 'product5', 4, false),\n" +
                        "\t(6, 'product6', 5, false),\n" +
                        "\t(7, 'product7', 7, false),\n" +
                        "\t(8, 'product8', 8, false),\n" +
                        "\t(9, 'product9', 9, false)");
                statement.executeUpdate("insert into receipt(receipt_id, discount_card_id, total_price, \n" +
                        "total_price_with_discount, cashier, receipt_date, receipt_time)\n" +
                        "VALUES (1, 1, 3, 3, 1456, '2021-10-23', '08:00:00'), \n" +
                        "\t(2, 2, 4, 4, 1456, '2021-10-23', '09:00:00')");
                statement.executeUpdate("insert into product_in_receipt(receipt_id, product_id, quantity, price, total_price)\n" +
                        "values (1, 1, 1, 1, 1), (1, 2, 1, 2, 2), (2, 2, 2, 4, 4)");
                statement.executeUpdate("insert into stock(stock_id, quantity, sale, description)\n" +
                        "values (1, 5, 15, 'bue > 5'), (2, 3, 9, 'buy > 3'), (3, 4, 12, 'buy')");
                statement.executeUpdate("insert into product_in_stock(stock_id, product_id)\n" +
                        "values (1, 5), (1, 6), (1, 7), (2, 7), (2, 8), (3, 6), (1, 7)");
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
