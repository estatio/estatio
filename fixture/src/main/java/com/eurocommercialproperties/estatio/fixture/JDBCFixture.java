/**
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.eurocommercialproperties.estatio.fixture;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JOptionPane;

import com.eurocommercialproperties.estatio.dom.asset.Properties;
import com.eurocommercialproperties.estatio.dom.asset.Property;
import com.eurocommercialproperties.estatio.dom.communicationchannel.CommunicationChannels;
import com.eurocommercialproperties.estatio.dom.geography.Countries;
import com.eurocommercialproperties.estatio.dom.geography.States;
import com.eurocommercialproperties.estatio.dom.party.Owners;

import org.apache.isis.applib.fixtures.AbstractFixture;

public class JDBCFixture extends AbstractFixture {

    static String userid = "sa", password = "feli3128";
    static String url = "jdbc:microsoft:sqlserver://ams-s-sql08:1433";
    static Statement stmt;
    static Connection con;

    @Override
    public void install() {

        retrieveProperties();

    }

    public static Connection getConnection() {

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

        } catch (java.lang.ClassNotFoundException e) {
            System.err.print("ClassNotFoundException: ");
            System.err.println(e.getMessage());
        }

        try {
            con = DriverManager.getConnection(url, userid, password);

        } catch (SQLException ex) {
            System.err.println("SQLException: " + ex.getMessage());
        }

        return con;
    }

    public void retrieveProperties() {
        Connection con = getConnection();
        String result = null;
        String selectString;
        selectString = "SELECT * FROM Property";
        try {
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(selectString);
            while (rs.next()) {
                Property p = properties.newProperty(rs.getString("reference"), rs.getString("name"));
                persist(p);
            }
            stmt.close();
            con.close();

        } catch (SQLException ex) {
            System.err.println("SQLException: " + ex.getMessage());
        }
        JOptionPane.showMessageDialog(null, result);
    }

    private States states;

    public void setStateRepository(final States states) {
        this.states = states;
    }

    private Countries countries;

    public void setCountryRepository(final Countries countries) {
        this.countries = countries;
    }

    private Properties properties;

    public void setPropertyRepository(final Properties properties) {
        this.properties = properties;
    }

    private Owners owners;

    public void setOwnerRepository(final Owners owners) {
        this.owners = owners;
    }

    private CommunicationChannels communicationChannels;

    public void setCommunicationChannelsRepository(final CommunicationChannels communicationChannels) {
        this.communicationChannels = communicationChannels;
    }

}