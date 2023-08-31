package com.gamo.ecommerce1.repository;

import com.gamo.ecommerce1.model.Article;
import com.gamo.ecommerce1.model.Settlement;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CRUDSettlement implements SettlementRepository {

    private final DataSource dataSource;

    public CRUDSettlement(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Settlement> findAll() {
        List<Settlement> settlementList = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM settlements");
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                Settlement settlement = mapRowToSettlement(resultSet);
                settlementList.add(settlement);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return settlementList;
    }

    @Override
    public Settlement findById(int id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM settlements WHERE id = ?")) {

            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapRowToSettlement(resultSet);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public Settlement insert(Settlement settlement) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "INSERT INTO settlements (amount, date) VALUES (?, ?)")) {

            preparedStatement.setDouble(1, settlement.getPayment());
            preparedStatement.setDate(2, java.sql.Date.valueOf(settlement.getDate()));
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return settlement;
    }

    @Override
    public void update(Settlement settlement) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "UPDATE settlements SET amount = ?, date = ? WHERE id = ?")) {

            preparedStatement.setDouble(1, settlement.getPayment());
            preparedStatement.setTimestamp(2, Timestamp.valueOf(settlement.getDate().atStartOfDay()));
            preparedStatement.setInt(3, settlement.getId());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean delete(int id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM settlements WHERE id = ?")) {

            preparedStatement.setInt(1, id);
            return preparedStatement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private Settlement mapRowToSettlement(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        Long payment = resultSet.getLong("payment_id");
        String status = resultSet.getString("status");
        LocalDate date = resultSet.getDate("date").toLocalDate();

        return new Settlement(id, payment, status, date);
    }

    @Repository
    public static class CRUDArticle implements CRUDEligible.ArticleRepository {

        private final DataSource dataSource;

        public CRUDArticle(DataSource dataSource) {
            this.dataSource = dataSource;
        }

        public void createArticle(Article article) {
            String query = "INSERT INTO articles (name, description, size, price, gender, category, availability) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                preparedStatement.setString(1, article.getName());
                preparedStatement.setString(2, article.getDescription());
                preparedStatement.setString(3, article.getSize());
                preparedStatement.setLong(4, article.getPrice());
                preparedStatement.setString(5, article.getGender());
                preparedStatement.setString(6, article.getCategory());
                preparedStatement.setBoolean(7, article.getAvailability());

                preparedStatement.executeUpdate();

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public List<Article> findAll() {
            List<Article> articles = new ArrayList<>();

            try (Connection connection = dataSource.getConnection();
                 Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery("SELECT * FROM articles")) {

                while (resultSet.next()) {
                    Article article = new Article();
                    article.setId(resultSet.getInt("id"));
                    article.setName(resultSet.getString("name"));
                    article.setDescription(resultSet.getString("description"));
                    article.setSize(resultSet.getString("size"));
                    article.setPrice(resultSet.getLong("price"));
                    article.setGender(resultSet.getString("gender"));
                    article.setCategory(resultSet.getString("category"));
                    article.setAvailability(resultSet.getBoolean("availability"));
                    articles.add(article);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return articles;
        }

        public Article getArticleById(int id) {
            String query = "SELECT * FROM articles WHERE id = ?";
            try (Connection connection= dataSource.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                preparedStatement.setInt(1, id);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return mapRowToArticle(resultSet);
                    }
                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return null;
        }

        public void updateArticle(Article article) {
            String query = "UPDATE articles SET name = ?, description = ?, size = ?, price = ?, gender = ?, category = ?, availability = ? WHERE id = ?";
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                preparedStatement.setString(1, article.getName());
                preparedStatement.setString(2, article.getDescription());
                preparedStatement.setString(3, article.getSize());
                preparedStatement.setLong(4, article.getPrice());
                preparedStatement.setString(5, article.getGender());
                preparedStatement.setString(6, article.getCategory());
                preparedStatement.setBoolean(7, article.getAvailability());
                preparedStatement.setInt(8, article.getId());

                preparedStatement.executeUpdate();

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        public boolean deleteArticle(int id) {
            String query = "DELETE FROM articles WHERE id = ?";
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                preparedStatement.setInt(1, id);
                return preparedStatement.executeUpdate() > 0;

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        private Article mapRowToArticle(ResultSet resultSet) throws SQLException {
            Article article = new Article();
            article.setId(resultSet.getInt("id"));
            article.setName(resultSet.getString("name"));
            article.setDescription(resultSet.getString("description"));
            article.setSize(resultSet.getString("size"));
            article.setPrice(resultSet.getLong("price"));
            article.setGender(resultSet.getString("gender"));
            article.setCategory(resultSet.getString("category"));
            article.setAvailability(resultSet.getBoolean("availability"));
            return article;
        }
    }
}
