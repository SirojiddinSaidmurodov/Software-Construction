package edu.keepeasy.restaurant_companion.repository;

import edu.keepeasy.restaurant_companion.domain.Order;
import edu.keepeasy.restaurant_companion.domain.OrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

@Repository
public class OrderItemRepo {
    private static final String selectByIDQuery = "SELECT id, mealID, orderID, quantity FROM orderItems where id = ?";
    private static final String selectAllQuery = "SELECT id, mealID, orderID, quantity FROM orderItems";
    private static final String selectByOrderIDQuery = "SELECT id, mealID, orderID, quantity FROM orderItems WHERE orderID=?";
    private static final String updateQuery = "UPDATE orderItems SET mealID = ?, orderID = ?, quantity = ? where id = ?";
    private static final String deleteQuery = "DELETE FROM orderItems where id = ?";
    final
    JdbcOperations jdbcOperations;
    final
    JdbcTemplate template;

    public OrderItemRepo(@Autowired JdbcOperations jdbcOperations, @Autowired JdbcTemplate template) {
        this.jdbcOperations = jdbcOperations;
        this.template = template;
    }

    public OrderItem create(OrderItem entity) {
        long id = Objects.requireNonNull(new SimpleJdbcInsert(template)
                .withTableName("orderitems")
                .usingColumns("mealID", "orderID", "quantity")
                .usingGeneratedKeyColumns("id")
                .executeAndReturnKeyHolder(Map.of(
                        "mealID", entity.getMealID(),
                        "orderID", entity.getOrderID(),
                        "quantity", entity.getQuantity()
                )).getKey()).longValue();
        entity.setId(id);
        return entity;
    }

    public OrderItem[] readAll() {
        ArrayList<OrderItem> orderItems = new ArrayList<>();
        SqlRowSet rowSet = jdbcOperations.queryForRowSet(
                selectAllQuery);
        OrderItem orderItem = getResult(rowSet);
        while (orderItem != null) {
            orderItems.add(orderItem);
            orderItem = getResult(rowSet);
        }
        return orderItems.toArray(new OrderItem[0]);
    }

    public OrderItem read(long id) {
        return getResult(jdbcOperations.queryForRowSet(
                selectByIDQuery,
                new Object[]{id},
                new int[]{Types.BIGINT}));
    }

    public OrderItem[] readByOrderID(long orderID){
        ArrayList<OrderItem> orderItems = new ArrayList<>();
        SqlRowSet rowSet = jdbcOperations.queryForRowSet(
                selectByOrderIDQuery,
                new Object[]{orderID},
                new int[]{Types.BIGINT});
        OrderItem orderItem = getResult(rowSet);
        while (orderItem != null) {
            orderItems.add(orderItem);
            orderItem = getResult(rowSet);
        }
        return orderItems.toArray(new OrderItem[0]);
    }

    public OrderItem update(long id, OrderItem entity) {
        Object[] args = new Object[]{
                entity.getMealID(),
                entity.getOrderID(),
                entity.getQuantity(),
                id};
        int[] argTypes = new int[]{
                Types.BIGINT,
                Types.BIGINT,
                Types.INTEGER,
                Types.BIGINT};
        int rows = jdbcOperations.update(
                updateQuery,
                args,
                argTypes);
        if (rows == 0) {
            return null;
        } else {
            return entity;
        }
    }

    public OrderItem delete(OrderItem entity) {
        int rows = jdbcOperations.update(
                deleteQuery,
                new Object[]{entity.getId()},
                new int[]{Types.BIGINT});
        if (rows == 0) {
            return null;
        } else {
            return entity;
        }
    }

    private OrderItem getResult(SqlRowSet rowSet) {
        if (rowSet.next()) {
            return new OrderItem(
                    rowSet.getLong("id"),
                    rowSet.getLong("mealID"),
                    rowSet.getLong("orderID"),
                    rowSet.getInt("quantity"));
        } else {
            return null;
        }
    }
}
