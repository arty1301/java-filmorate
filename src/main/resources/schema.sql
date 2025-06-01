  CREATE TABLE IF NOT EXISTS mpa (
      mpa_id INTEGER PRIMARY KEY,
      name VARCHAR(10) NOT NULL
  );

  CREATE TABLE IF NOT EXISTS genres (
      genre_id INTEGER PRIMARY KEY,
      name VARCHAR(50) NOT NULL
  );

  CREATE TABLE IF NOT EXISTS films (
      film_id BIGINT PRIMARY KEY AUTO_INCREMENT,
      name VARCHAR(100) NOT NULL,
      description VARCHAR(200),
      release_date DATE NOT NULL,
      duration INTEGER NOT NULL,
      mpa_id INTEGER REFERENCES mpa (mpa_id)
  );

  CREATE TABLE IF NOT EXISTS film_genre (
      film_id BIGINT NOT NULL REFERENCES films (film_id),
      genre_id INTEGER NOT NULL REFERENCES genres (genre_id),
      PRIMARY KEY (film_id, genre_id)
  );

  CREATE TABLE IF NOT EXISTS users (
      user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
      email VARCHAR(50) NOT NULL UNIQUE,
      login VARCHAR(50) NOT NULL UNIQUE,
      name VARCHAR(50),
      birthday DATE NOT NULL
  );

  CREATE TABLE IF NOT EXISTS friendships (
      user_id BIGINT NOT NULL REFERENCES users (user_id),
      friend_id BIGINT NOT NULL REFERENCES users (user_id),
      confirmed BOOLEAN DEFAULT FALSE,
      PRIMARY KEY (user_id, friend_id)
  );

  CREATE TABLE IF NOT EXISTS likes (
      film_id BIGINT NOT NULL REFERENCES films (film_id),
      user_id BIGINT NOT NULL REFERENCES users (user_id),
      PRIMARY KEY (film_id, user_id)
  );