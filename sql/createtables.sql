CREATE TABLE people (
    person_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    twitter_name VARCHAR(255) NOT NULL
) ENGINE=InnoDb;

CREATE TABLE movie_quotes (
    quote_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    movie_id INT NOT NULL,
    quote_text VARCHAR(140) NOT NULL,
    used TINYINT(1) NOT NULL DEFAULT 0,
    used_datestamp DATETIME
) ENGINE=InnoDb;

CREATE TABLE movies (
    movie_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    movie_title VARCHAR(255) NOT NULL,
    genre_id INT NOT NULL
) ENGINE=InnoDb;

CREATE TABLE movie_genres (
    genre_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    genre_name VARCHAR(255) NOT NULL
) ENGINE=InnoDb;

CREATE TABLE movie_winners (
    movie_win_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    person_id INT NOT NULL,
    quote_id INT NOT NULL,
    datestamp DATETIME NOT NULL
) ENGINE=InnoDb;

CREATE TABLE trivia_questions (
    trivia_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    category_id INT NOT NULL,
    question_text VARCHAR(255) NOT NULL,
    used TINYINT(1) NOT NULL DEFAULT 0,
    used_datestamp DATETIME
) ENGINE=InnoDb;

CREATE TABLE trivia_category (
    category_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(255) NOT NULL
) ENGINE=InnoDb;

CREATE TABLE trivia_winners (
    trivia_win_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    person_id INT NOT NULL,
    trivia_id INT NOT NULL,
    datestamp DATETIME NOT NULL
) ENGINE=InnoDb;
