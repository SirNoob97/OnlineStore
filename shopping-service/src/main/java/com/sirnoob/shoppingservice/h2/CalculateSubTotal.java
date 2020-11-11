package com.sirnoob.shoppingservice.h2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.h2.api.Trigger;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class CalculateSubTotal implements Trigger{

	@Override
	public void init(Connection conn, String schemaName, String triggerName, String tableName, boolean before, int type)
			throws SQLException {
	}

	@Override
	public void fire(Connection conn, Object[] oldRow, Object[] newRow) throws SQLException {
	  final String query = "UPDATE items SET sub_total = items.product_price * items.quantity";
    try (PreparedStatement statement = conn.prepareStatement(query)) {
      statement.execute();
    } catch (Exception e) {
      new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
	}

	@Override
	public void close() throws SQLException {
	}

	@Override
	public void remove() throws SQLException {
	}

}
