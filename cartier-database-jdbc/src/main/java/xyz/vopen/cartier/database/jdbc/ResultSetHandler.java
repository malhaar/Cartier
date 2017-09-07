package xyz.vopen.cartier.database.jdbc;


import java.sql.ResultSet;

public interface ResultSetHandler<T> {
    T handle (ResultSet rs);
}
