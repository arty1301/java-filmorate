DELETE FROM likes;
DELETE FROM film_genre;
DELETE FROM friendships;
DELETE FROM films;
DELETE FROM users;

ALTER TABLE users ALTER COLUMN user_id RESTART WITH 1;
ALTER TABLE films ALTER COLUMN film_id RESTART WITH 1;

MERGE INTO mpa (mpa_id, name) VALUES (1, 'G');
MERGE INTO mpa (mpa_id, name) VALUES (2, 'PG');
MERGE INTO mpa (mpa_id, name) VALUES (3, 'PG-13');
MERGE INTO mpa (mpa_id, name) VALUES (4, 'R');
MERGE INTO mpa (mpa_id, name) VALUES (5, 'NC-17');

MERGE INTO genres (genre_id, name) VALUES (1, 'Комедия');
MERGE INTO genres (genre_id, name) VALUES (2, 'Драма');
MERGE INTO genres (genre_id, name) VALUES (3, 'Мультфильм');
MERGE INTO genres (genre_id, name) VALUES (4, 'Триллер');
MERGE INTO genres (genre_id, name) VALUES (5, 'Документальный');
MERGE INTO genres (genre_id, name) VALUES (6, 'Боевик');

