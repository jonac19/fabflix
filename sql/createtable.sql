DROP DATABASE IF EXISTS moviedb;
CREATE DATABASE moviedb;
USE moviedb;

CREATE TABLE movies(
	id VARCHAR(10),
    title VARCHAR(100) DEFAULT "" NOT NULL,
    year INTEGER NOT NULL,
    director VARCHAR(100) DEFAULT "" NOT NULL,
    PRIMARY KEY(id)
);


CREATE TABLE stars(
	id VARCHAR(10),
    name VARCHAR(100) DEFAULT "" NOT NULL,
    birthYear INTEGER,
    PRIMARY KEY(id)
);


CREATE TABLE genres(
	id INTEGER AUTO_INCREMENT,
    name VARCHAR(32) DEFAULT "" NOT NULL,
    PRIMARY KEY(id)
);


CREATE TABLE creditcards(
	id VARCHAR(20),
    firstName VARCHAR(50) DEFAULT "" NOT NULL,
    lastName VARCHAR(50) DEFAULT "" NOT NULL,
    expiration DATE NOT NULL,
    PRIMARY KEY(id)
);


CREATE TABLE customers(
	id INTEGER AUTO_INCREMENT,
    firstName VARCHAR(50) DEFAULT "" NOT NULL,
    lastName VARCHAR(50) DEFAULT "" NOT NULL,
    ccId VARCHAR(20),
    address VARCHAR(200) DEFAULT "" NOT NULL,
    email VARCHAR(50) DEFAULT "" NOT NULL,
    password VARCHAR(20) DEFAULT "" NOT NULL,
    PRIMARY KEY(id),
    FOREIGN KEY(ccId) REFERENCES creditcards(id)
);


-- RELATIONSHIPS --

CREATE TABLE stars_in_movies(
	starId VARCHAR(10),
    movieId VARCHAR(10),
    PRIMARY KEY(starId, movieId),
    FOREIGN KEY(starId) REFERENCES stars(id),
    FOREIGN KEY(movieId) REFERENCES movies(id)
);


CREATE TABLE genres_in_movies(
	genreId INTEGER,
    movieId VARCHAR(10),
    PRIMARY KEY(genreId, movieId),
    FOREIGN KEY(genreId) REFERENCES genres(id),
    FOREIGN KEY(movieId) REFERENCES movies(id)
);


CREATE TABLE sales(
	id INTEGER AUTO_INCREMENT,
    customerId INTEGER,
    movieId VARCHAR(10),
    saleDate DATE NOT NULL,
    PRIMARY KEY(id),
    FOREIGN KEY(customerId) REFERENCES customers(id),
    FOREIGN KEY(movieId) REFERENCES movies(id)
);


CREATE TABLE ratings(
	movieId VARCHAR(10),
    rating FLOAT NOT NULL,
    numVotes INTEGER NOT NULL,
    PRIMARY KEY(movieId),
    FOREIGN KEY(movieId) REFERENCES movies(id)
);


CREATE TABLE employees(
	email VARCHAR(50),
    password VARCHAR(120) NOT NULL,
    fullname VARCHAR(100),
    PRIMARY KEY(email)
);

ALTER TABLE movies ADD FULLTEXT(title);
ALTER TABLE stars ADD FULLTEXT(name);