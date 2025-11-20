package com.example.library.dao;

import com.example.library.model.Book;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcBookDAO implements BookDAO {

    private final NamedParameterJdbcTemplate namedJdbcTemplate;

    public JdbcBookDAO(NamedParameterJdbcTemplate namedJdbcTemplate) {
        this.namedJdbcTemplate = namedJdbcTemplate;
    }

    @Override
    public void save(Book book) {
        String sql = "INSERT INTO books (title, author, publication_year, cover_filename) " +
                     "VALUES (:title, :author, :year, :cover)";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("title", book.getTitle())
                .addValue("author", book.getAuthor())
                .addValue("year", book.getYear())
                .addValue("cover", book.getCoverImageFilename());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedJdbcTemplate.update(sql, params, keyHolder);

        // PostgreSQL zwraca wszystkie kolumny jako klucze, więc musimy pobrać konkretną kolumnę "id"
        Object idValue = keyHolder.getKeys().get("id");
        if (idValue != null) {
            book.setId(((Number) idValue).longValue());
        }
    }

    @Override
    public List<Book> findAll() {
        String sql = "SELECT * FROM books";
        return namedJdbcTemplate.query(sql, new BookRowMapper());
    }

    @Override
    public Optional<Book> findByTitle(String title) {
        String sql = "SELECT * FROM books WHERE title = :title";

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("title", title);

        try {
            Book book = namedJdbcTemplate.queryForObject(sql, params, new BookRowMapper());
            return Optional.ofNullable(book);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public void update(Book book) {
        String sql = "UPDATE books " +
                     "SET author = :author, publication_year = :year, cover_filename = :cover " +
                     "WHERE id = :id";

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", book.getId())
                .addValue("author", book.getAuthor())
                .addValue("year", book.getYear())
                .addValue("cover", book.getCoverImageFilename());

        namedJdbcTemplate.update(sql, params);
    }

    @Override
    public void deleteByTitle(String title) {
        String sql = "DELETE FROM books WHERE title = :title";

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("title", title);

        namedJdbcTemplate.update(sql, params);
    }

    @Override
    public List<Book> findByIds(List<Long> ids) {
        if (ids.isEmpty()) {
            return List.of();
        }

        String sql = "SELECT * FROM books WHERE id IN (:ids)";
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("ids", ids);

        return namedJdbcTemplate.query(sql, params, new BookRowMapper());
    }

    private static class BookRowMapper implements RowMapper<Book> {
        @Override
        public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
            Book book = new Book(
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getInt("publication_year")
            );
            book.setId(rs.getLong("id"));
            book.setCoverImageFilename(rs.getString("cover_filename"));
            return book;
        }
    }
}

