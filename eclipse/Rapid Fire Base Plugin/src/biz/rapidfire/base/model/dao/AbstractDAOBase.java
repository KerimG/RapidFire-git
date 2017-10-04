/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Initial idea and development: Isaac Ramirez Herrera
 * Continued and adopted to iSphere: iSphere Project Team
 *******************************************************************************/

package biz.rapidfire.base.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import biz.rapidfire.base.Messages;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400Date;
import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;

public abstract class AbstractDAOBase {

    // protected static final String properties = "thread used=false; extendeddynamic=true; package criteria=select; package cache=true;"; //$NON-NLS-1$
    protected static final String properties = "translate hex=binary; prompt=false; extended dynamic=true; package cache=true"; //$NON-NLS-1$

    private static final String BOOLEAN_Y = "Y";
    private static final String BOOLEAN_N = "N";
    private static final String BOOLEAN_YES = "*YES";
    private static final String BOOLEAN_NO = "*NO";

    protected IBMiConnection ibmiConnection;
    private Connection connection;
    private String dateFormat;
    private String dateSeparator;
    private String timeSeparator;

    public AbstractDAOBase(String connectionName) throws Exception {
        if (connectionName != null) {
            ibmiConnection = IBMiConnection.getConnection(connectionName);
            if (ibmiConnection == null) {
                throw new Exception(Messages.bind(Messages.DAOBase_Connection_A_not_found, connectionName));
            }
            if (!ibmiConnection.isConnected()) {
                if (!ibmiConnection.connect()) {
                    throw new Exception(Messages.bind(Messages.DAOBase_Failed_to_connect_to_A, connectionName));
                }
            }

            dateFormat = ibmiConnection.getQSYSJobSubSystem().getServerJob(null).getInternationalProperties().getDateFormat();
            if (dateFormat.startsWith("*")) { //$NON-NLS-1$
                dateFormat = dateFormat.substring(1);
            }

            dateSeparator = ibmiConnection.getQSYSJobSubSystem().getServerJob(null).getInternationalProperties().getDateSeparator();
            timeSeparator = ibmiConnection.getQSYSJobSubSystem().getServerJob(null).getInternationalProperties().getTimeSeparator();

            connection = ibmiConnection.getJDBCConnection(properties, true);
            connection.setAutoCommit(false);
        } else
            throw new Exception(Messages.bind(Messages.DAOBase_Invalid_or_missing_connection_name_A, connectionName));
    }

    public void destroy() {
    }

    protected Character getTimeSeparator() {
        return timeSeparator.charAt(0);
    }

    protected Character getDateSeparator() {
        return dateSeparator.charAt(0);
    }

    protected int getDateFormat() {
        return AS400Date.toFormat(dateFormat);
    }

    protected Connection getConnection() {
        return connection;
    }

    protected String getConnectionName() {
        return ibmiConnection.getConnectionName();
    }

    protected AS400 getSystem() throws Exception {
        return ibmiConnection.getAS400ToolboxObject();
    }

    protected PreparedStatement prepareStatement(String sql) throws SQLException {
        return getConnection().prepareStatement(sql);
    }

    protected void destroy(Connection connection) throws Exception {
        if (connection != null && !connection.isClosed()) connection.close();
    }

    protected void destroy(ResultSet resultSet) throws Exception {
        if (resultSet != null) resultSet.close();
    }

    protected void destroy(PreparedStatement preparedStatement) throws Exception {
        if (preparedStatement != null) preparedStatement.close();
    }

    protected void rollback(Connection connection) throws Exception {
        if (connection != null && !connection.isClosed()) {
            if (connection.getAutoCommit() == false) connection.rollback();
        }
    }

    protected void commit(Connection connection) throws Exception {
        if (connection != null && !connection.isClosed()) {
            if (connection.getAutoCommit() == false) connection.commit();
        }
    }

    protected boolean convertYesNo(String yesNoValue) {

        boolean booleanValue;
        if (BOOLEAN_YES.equals(yesNoValue) || BOOLEAN_Y.equals(yesNoValue)) {
            booleanValue = true;
        } else {
            booleanValue = false;
        }

        return booleanValue;
    }
}