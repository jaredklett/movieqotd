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

CREATE TABLE movie_guesses (
    movie_guess_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    quote_id INT NOT NULL,
    person_id INT NOT NULL,
    guess_text VARCHAR(255) NOT NULL,
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

INSERT INTO movie_genres (genre_name) VALUES ('Action');
INSERT INTO movie_genres (genre_name) VALUES ('Adventure');
INSERT INTO movie_genres (genre_name) VALUES ('Animation');
INSERT INTO movie_genres (genre_name) VALUES ('Comedy');
INSERT INTO movie_genres (genre_name) VALUES ('Crime');
INSERT INTO movie_genres (genre_name) VALUES ('Documentary');
INSERT INTO movie_genres (genre_name) VALUES ('Drama');
INSERT INTO movie_genres (genre_name) VALUES ('Fantasy');
INSERT INTO movie_genres (genre_name) VALUES ('Family');
INSERT INTO movie_genres (genre_name) VALUES ('Film-Noir');
INSERT INTO movie_genres (genre_name) VALUES ('Horror');
INSERT INTO movie_genres (genre_name) VALUES ('Musical');
INSERT INTO movie_genres (genre_name) VALUES ('Mystery');
INSERT INTO movie_genres (genre_name) VALUES ('Romance');
INSERT INTO movie_genres (genre_name) VALUES ('Sci-Fi');
INSERT INTO movie_genres (genre_name) VALUES ('Short');
INSERT INTO movie_genres (genre_name) VALUES ('Thriller');
INSERT INTO movie_genres (genre_name) VALUES ('War');
INSERT INTO movie_genres (genre_name) VALUES ('Western');
