package edu.keepeasy.restaurant_companion.repository;

import edu.keepeasy.restaurant_companion.domain.Meal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

@org.springframework.stereotype.Repository
public class MealRepo implements Repository<Meal> {
    private static final String selectQuery = "SELECT id, mealName, mealCost, mealAvailable from meal";
    private static final String selectByIDQuery = "SELECT id, mealName, mealCost, mealAvailable from meal where id=?";
    private static final String updateQuery = "UPDATE meal SET mealName = ?, mealCost = ?, mealAvailable = ? where id = ?";
    private static final String deleteQuery = "DELETE FROM meal where id = ?";
    @Autowired
    JdbcTemplate template;
    @Autowired
    JdbcOperations jdbcOperations;

    @Override
    public Meal create(Meal entity) {
        long id = Objects.requireNonNull(new SimpleJdbcInsert(template)
                .withTableName("Meal")
                .usingColumns("mealName", "mealCost", "mealAvailable")
                .usingGeneratedKeyColumns("id")
                .executeAndReturnKeyHolder(Map.of(
                        "mealName", entity.getMealName(),
                        "mealCost", entity.getMealCost(),
                        "mealAvailable", entity.isMealAvailable()))
                .getKey())
                .longValue();
        entity.setId(id);
        return entity;
    }

    @Override
    public Meal[] readAll() {
        ArrayList<Meal> values = new ArrayList<>();
        SqlRowSet rowSet = jdbcOperations.queryForRowSet(selectQuery);
        while (rowSet.next()) {
            values.add(new Meal(
                    rowSet.getLong("id"),
                    rowSet.getString("mealName"),
                    rowSet.getInt("mealCost"),
                    rowSet.getBoolean("mealAvailable")));
        }
        return values.toArray(new Meal[0]);
    }

    @Override
    public Meal read(long id) {
        return getResult(
                jdbcOperations.queryForRowSet(
                        selectByIDQuery,
                        new Object[]{id},
                        new int[]{Types.BIGINT}));
    }


    @Override
    public Meal update(long id, Meal entity) {
        Object[] args = new Object[]{
                entity.getId(),
                entity.getMealName(),
                entity.getMealCost(),
                entity.isMealAvailable(),
                id};
        int[] argTypes = new int[]{
                Types.BIGINT,
                Types.CHAR,
                Types.INTEGER,
                Types.BOOLEAN,
                Types.BIGINT};
        return getResult(jdbcOperations.queryForRowSet(
                updateQuery,
                args,
                argTypes));
    }

    @Override
    public Meal delete(Meal entity) {
        return getResult(
                jdbcOperations.queryForRowSet(
                        deleteQuery,
                        new Object[]{entity.getId()},
                        new int[]{Types.BIGINT}));
    }

    private Meal getResult(SqlRowSet rowSet) {
        if (rowSet.next()) {
            return new Meal(
                    rowSet.getLong("id"),
                    rowSet.getString("mealName"),
                    rowSet.getInt("mealCost"),
                    rowSet.getBoolean("mealAvailable"));
        } else {
            return null;
        }
    }
}
