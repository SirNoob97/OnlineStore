package com.sirnoob.shoppingservice.h2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.h2.tools.TriggerAdapter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class CalculateSubTotal extends TriggerAdapter{

  @Override
  public void fire(Connection conn, ResultSet oldRow, ResultSet newRow) throws SQLException {
    final String query = "UPDATE items SET sub_total = items.product_price * items.quantity";
    try (PreparedStatement statement = conn.prepareStatement(query)) {
      statement.execute();

      statement.close();
      conn.close();
    } catch (Exception e) {
      new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

}
