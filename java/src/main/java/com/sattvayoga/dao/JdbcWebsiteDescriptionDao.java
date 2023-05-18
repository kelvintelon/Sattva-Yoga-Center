package com.sattvayoga.dao;

import com.sattvayoga.model.WebsiteDescription;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

@Component
public class JdbcWebsiteDescriptionDao implements WebsiteDescriptionDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcWebsiteDescriptionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public String getNewsAndEvents() {

        String sql = "SELECT * FROM website_descriptions WHERE location_name = 'News and Events'";
        WebsiteDescription websiteDescription = new WebsiteDescription();
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql);
        if (result.next()) {
            websiteDescription = mapRowToWebsiteDescription(result);
        }

        return websiteDescription.getDescription();
    }

    @Override
    public String getClassSchedule() {
        String sql = "SELECT * FROM website_descriptions WHERE location_name = 'Class Schedule'";
        WebsiteDescription websiteDescription = new WebsiteDescription();
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql);
        if (result.next()) {
            websiteDescription = mapRowToWebsiteDescription(result);
        }

        return websiteDescription.getDescription();
    }

    @Override
    public void updateClassSchedule(String newDescription) {
        String sql = "UPDATE website_descriptions SET description = ? WHERE location_name = 'Class Schedule'";
        jdbcTemplate.update(sql, newDescription);
    }

    @Override
    public void updateNewsAndEvents(String newDescription) {
        String sql = "UPDATE website_descriptions SET description = ? WHERE location_name = 'News and Events'";
        jdbcTemplate.update(sql, newDescription);
    }

    private WebsiteDescription mapRowToWebsiteDescription(SqlRowSet rs) {
        WebsiteDescription websiteDescription = new WebsiteDescription();
        websiteDescription.setWebDescriptionID(rs.getInt("webdescription_id"));
        websiteDescription.setLocationName(rs.getString("location_name"));
        websiteDescription.setDescription(rs.getString("description"));
        return websiteDescription;
    }
}
