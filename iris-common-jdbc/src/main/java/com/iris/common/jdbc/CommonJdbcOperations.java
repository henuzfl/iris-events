package com.iris.common.jdbc;

import com.iris.common.EventMessage;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: zfl
 * @Date: 2020/8/19 17:54
 * @Version: 1.0.0
 */
public class CommonJdbcOperations {

    private static final String RECEIVED_MESSAGES_TABLE_NAME =
            "received_messages";
    private static final String RECEIVED_MESSAGES_TABLE_INIT_SQL_PATH =
            "classpath" +
                    ":received_messages.sql";
    private static final String EVENT_MESSAGES_TABLE_NAME = "event_messages";
    private static final String EVENT_MESSAGES_TABLE_INIT_SQL_PATH =
            "classpath" +
                    ":event_messages.sql";

    private JdbcTemplate jdbcTemplate;

    public CommonJdbcOperations(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        initEventMessages();
        initReceivedMessages();
    }

    public void insertEventMessage(EventMessage eventMessage) {
        this.jdbcTemplate.update(String.format("INSERT INTO %s (msg_id," +
                "event_aggregate_id,event_aggregate_type,event_class_name," +
                "body)VALUES (?,?,?,?," +
                "?)", EVENT_MESSAGES_TABLE_NAME),
                new Object[]{eventMessage.getMsgId(),
                eventMessage.getEventAggregateId(),
                        eventMessage.getEventAggregateType(),
                        eventMessage.getEventClassName(),
                        eventMessage.getBody()});

    }

    public List<EventMessage> queryUnPublishedEventMessages() {
        return jdbcTemplate.query(String.format("select * from %s where " +
                        "published =0" +
                        " order by id ASC", EVENT_MESSAGES_TABLE_NAME),
                new EventMessageMapper());
    }

    public void setEventMessagePublished(long eventMessageId) {
        jdbcTemplate.update(String.format("update %s set published = 1 where " +
                "id = ?", EVENT_MESSAGES_TABLE_NAME), eventMessageId);
    }

    public void insertReceivedMessageToTable(String consumerId, String msgId) {
        jdbcTemplate.update(String.format("insert into %s (consumer_id," +
                        "msg_id,create_time) values (?,?,?)",
                RECEIVED_MESSAGES_TABLE_NAME), consumerId, msgId, new Date());
    }

    private class EventMessageMapper implements RowMapper<EventMessage> {

        @Override
        public EventMessage mapRow(ResultSet rs, int rowNum) throws SQLException {
            EventMessage eventMessage = new EventMessage();
            eventMessage.setId(rs.getLong("id"));
            eventMessage.setBody(rs.getString("body"));
            eventMessage.setEventAggregateType(rs.getString(
                    "event_aggregate_type"));
            eventMessage.setEventAggregateId(rs.getString("event_aggregate_id"
            ));
            eventMessage.setEventClassName(rs.getString(
                    "event_class_name"));
            eventMessage.setPublished(rs.getInt("published"));
            eventMessage.setMsgId(rs.getString("msg_id"));
            return eventMessage;
        }
    }

    private void initEventMessages() {
        if (!isTableExists(EVENT_MESSAGES_TABLE_NAME)) {
            loadSqlFromResource(EVENT_MESSAGES_TABLE_INIT_SQL_PATH)
                    .forEach(jdbcTemplate::update);
        }
    }

    private void initReceivedMessages() {
        if (!isTableExists(RECEIVED_MESSAGES_TABLE_NAME)) {
            loadSqlFromResource(RECEIVED_MESSAGES_TABLE_INIT_SQL_PATH)
                    .forEach(jdbcTemplate::update);
        }
    }

    private List<String> loadSqlFromResource(String sqlPath) {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource resource = resourceLoader.getResource(sqlPath);
        try (BufferedReader reader =
                     new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            return Arrays.asList(reader.lines().collect(Collectors.joining(
                    "\n")).split(";"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isTableExists(String tableName) {
        ResultSet rs = null;
        Connection connection = null;
        try {
            connection = jdbcTemplate.getDataSource().getConnection();
            DatabaseMetaData metaData = connection.getMetaData();
            rs = metaData.getTables(connection.getCatalog(), null, tableName,
                    new String[]{"TABLE"});
            if (rs.next()) {
                return true;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            try {
                if (null != connection) {
                    connection.close();
                }
                if (null != rs) {
                    rs.close();
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return false;
    }

}
