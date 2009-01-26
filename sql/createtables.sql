CREATE TABLE quotes (
    qid INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    mid INT NOT NULL,
    quote_text VARCHAR(140) NOT NULL,
    used TINYINT(1) NOT NULL DEFAULT 0,
    used_datestamp DATETIME
) ENGINE=InnoDb;

CREATE TABLE movies (
    mid INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    movie_title VARCHAR(255) NOT NULL,
    gid INT NOT NULL
) ENGINE=InnoDb;

CREATE TABLE genres (
    gid INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    genre_name VARCHAR(255) NOT NULL
) ENGINE=InnoDb;

CREATE TABLE people (
    pid INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    twitter_name VARCHAR(255) NOT NULL
) ENGINE=InnoDb;

CREATE TABLE winners (
    wid INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    pid INT NOT NULL,
    qid INT NOT NULL,
    datestamp DATETIME NOT NULL
) ENGINE=InnoDb;