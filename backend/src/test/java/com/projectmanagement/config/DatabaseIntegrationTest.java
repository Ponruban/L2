package com.projectmanagement.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test to verify database schema setup and Liquibase migrations
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class DatabaseIntegrationTest {

    @Autowired
    private DataSource dataSource;

    @Test
    void shouldHaveAllRequiredTables() throws SQLException {
        // Get all table names from the database
        List<String> tableNames = new ArrayList<>();
        
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"});
            
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                tableNames.add(tableName.toLowerCase());
            }
        }

        // Verify all required tables exist
        List<String> requiredTables = List.of(
            "users",
            "projects", 
            "project_members",
            "milestones",
            "tasks",
            "comments",
            "attachments",
            "time_logs"
        );

        for (String requiredTable : requiredTables) {
            assertTrue(tableNames.contains(requiredTable), 
                "Required table '" + requiredTable + "' should exist");
        }
    }

    @Test
    void shouldHaveInitialDataSeeded() throws SQLException {
        // Verify that the initial admin user was created
        try (Connection connection = dataSource.getConnection()) {
            var statement = connection.createStatement();
            var resultSet = statement.executeQuery(
                "SELECT COUNT(*) as count FROM users WHERE email = 'admin@projectmanagement.com'"
            );
            
            assertTrue(resultSet.next());
            int count = resultSet.getInt("count");
            assertEquals(1, count, "Initial admin user should be seeded");
        }
    }

    @Test
    void shouldHaveProperIndexes() throws SQLException {
        // Verify that key indexes exist
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            
            // Check users table indexes
            ResultSet indexes = metaData.getIndexInfo(null, null, "USERS", false, false);
            List<String> userIndexes = new ArrayList<>();
            while (indexes.next()) {
                String indexName = indexes.getString("INDEX_NAME");
                if (indexName != null) {
                    userIndexes.add(indexName.toLowerCase());
                }
            }
            
            // Verify key indexes exist
            assertTrue(userIndexes.contains("idx_users_email"), 
                "Users email index should exist");
            assertTrue(userIndexes.contains("idx_users_role"), 
                "Users role index should exist");
        }
    }

    @Test
    void shouldHaveProperForeignKeys() throws SQLException {
        // Verify that foreign key constraints exist
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            
            // Check foreign keys for tasks table
            ResultSet foreignKeys = metaData.getImportedKeys(null, null, "TASKS");
            List<String> taskForeignKeys = new ArrayList<>();
            while (foreignKeys.next()) {
                String fkName = foreignKeys.getString("FK_NAME");
                taskForeignKeys.add(fkName.toLowerCase());
            }
            
            // Verify key foreign keys exist
            assertTrue(taskForeignKeys.stream().anyMatch(fk -> fk.contains("project_id")), 
                "Tasks project_id foreign key should exist");
            assertTrue(taskForeignKeys.stream().anyMatch(fk -> fk.contains("created_by")), 
                "Tasks created_by foreign key should exist");
        }
    }

    @Test
    void shouldHaveProperConstraints() throws SQLException {
        // Verify that NOT NULL constraints exist
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            
            // Check users table columns
            ResultSet columns = metaData.getColumns(null, null, "USERS", null);
            while (columns.next()) {
                String columnName = columns.getString("COLUMN_NAME");
                String isNullable = columns.getString("IS_NULLABLE");
                
                // Verify required fields are NOT NULL
                if ("email".equals(columnName) || "password".equals(columnName) || 
                    "first_name".equals(columnName) || "last_name".equals(columnName) || 
                    "role".equals(columnName)) {
                    assertEquals("NO", isNullable, 
                        "Column " + columnName + " should be NOT NULL");
                }
            }
        }
    }
} 