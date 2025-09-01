package com.veracode.verademo.commands;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

public class IgnoreCommand implements BlabberCommand {
	private static final Logger logger = LogManager.getLogger("VeraDemo:IgnoreCommand");

	private Connection connect;

	private String username;

	public IgnoreCommand(Connection connect, String username) {
		super();
		this.connect = connect;
		this.username = username;
	}
	@Override
	public void execute(String blabberUsername) {
		String sqlQuery = "DELETE FROM listeners WHERE blabber=? AND listener=?;";
		logger.info(sqlQuery);
		PreparedStatement action;
		try {
			action = connect.prepareStatement(sqlQuery);

			action.setString(1, blabberUsername);
			action.setString(2, username);
			action.execute();

			sqlQuery = "SELECT blab_name FROM users WHERE username = '" + blabberUsername + "'";
			PreparedStatement sqlStatement = connect.prepareStatement(sqlQuery, ResultSet.TYPE_SCROLL_INSENSITIVE,
			ResultSet.CONCUR_READ_ONLY);
			sqlStatement.setString(1, username);
			logger.info(StringUtils.normalizeSpace(sqlQuery));
			ResultSet result = sqlStatement.executeQuery();
			result.next();
			/* START EXAMPLE VULNERABILITY */
			String event = username + " is now ignoring " + result.getString(1) + " (" + result.getString(2) + ")";
			sqlQuery = "INSERT INTO users_history (blabber, event) VALUES (?,?)";
			logger.info(StringEscapeUtils.escapeJava(sqlQuery));
			PreparedStatement sqlStatement2 = connect.prepareStatement(sqlQuery);
			sqlStatement2.setString(1, username);
			sqlStatement2.setString(2, event);
			sqlStatement2.execute(sqlQuery);
			/* END EXAMPLE VULNERABILITY */
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
