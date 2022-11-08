SET GLOBAL log_bin_trust_function_creators = 1;
use moviedb;
DROP PROCEDURE IF EXISTS add_movie;
DELIMITER $$ 

CREATE PROCEDURE add_movie ( IN iTitle VARCHAR(100), IN iYear INTEGER, IN iDirector VARCHAR(100),
							IN starName VARCHAR(100), IN starBirthYear INTEGER, IN genre VARCHAR(32),
                            INOUT message VARCHAR(100))
proc_label: BEGIN
	DECLARE newMovieId VARCHAR(10);
	DECLARE newStarId VARCHAR(10);
	DECLARE newGenreId INTEGER DEFAULT NULL;
    -- flags for output message response
    DECLARE starExisted VARCHAR(20) DEFAULT "(EXISTING)";
    DECLARE genreExisted VARCHAR(20) DEFAULT "(EXISTING)";
    
	-- Check if movie exists already. Exit procedure/cancel insertion if so
	SELECT search_movies(iTitle, iYear, iDirector) INTO newMovieId;
	IF (newMovieId IS NOT NULL) THEN
        SET message = CONCAT("Insertion failed! Movie already exists (id: ", newMovieId, ")");
		LEAVE proc_label;
	END IF;
    
    -- Set new Movie ID and INSERT into 'movies' table
	SELECT CONCAT("tt", SUBSTRING(max(id), 3, 7)+1) INTO newMovieId FROM movies WHERE id LIKE BINARY "tt%";
    INSERT INTO movies VALUES (newMovieId, iTitle, iYear, iDirector);

    
    -- Set newStarId. If no star found, create it with id=MAX(stars.id)+1
    IF (starBirthYear = 0) THEN SET starBirthYear = NULL; END IF;
    SELECT search_stars(starName, starBirthYear) INTO newStarId;
    IF (newStarId IS NULL) THEN
		SET starExisted = "(NEW)";
		SELECT MAX(id) INTO newStarId FROM stars WHERE id LIKE BINARY "nm%";
		SELECT CONCAT("nm", SUBSTRING(newStarId, 3, 7) + 1) INTO newStarId;
		INSERT INTO stars VALUES (newStarId, starName, starBirthYear);
    END IF;
    
    -- Set newGenreId. If genre not found, create it with id=MAX(genres.id)+1
    SELECT search_genres(genre) INTO newGenreId;
    IF (newGenreId IS NULL) THEN
		SET genreExisted = "(NEW)";
		INSERT INTO genres VALUES (NULL, genre);	-- No MAX()+1 logic needed thanks to AUTO INCREMENT
	END IF;
    SELECT search_genres(genre) INTO newGenreId;	-- Search+assign newId after so it can't be NULL
    
    
    -- Link Star and Genre to movie
    INSERT INTO stars_in_movies VALUES(newStarId, newMovieId);
    INSERT INTO genres_in_movies VALUES(newGenreId, newMovieId);
    INSERT INTO ratings VALUES (newMovieId, 0, 0);
    
    
	SET message = CONCAT("Movie added with Movie ID: ", newMovieId, 
		" - Star ID: ", newStarId, " ", starExisted,
        " - Genre ID: ", newGenreId, " ", genreExisted);

   
END$$

DELIMITER ; 



-- ******** HELPER FUNCTIONS ********

DROP FUNCTION IF EXISTS search_movies;
DELIMITER $$ 
-- If movie exists in table, return its ID. Else return NULL
CREATE FUNCTION  search_movies (iTitle VARCHAR(100), iYear INTEGER, iDirector VARCHAR(100))
RETURNS VARCHAR(10)
BEGIN
	RETURN(
		SELECT id
		FROM movies AS m
		WHERE m.title=iTitle AND m.year=iYear AND m.director=iDirector
	);
END$$
DELIMITER ;


DROP FUNCTION IF EXISTS search_stars;
DELIMITER $$ 
-- If star exists in table, return their ID. Else return NULL
CREATE FUNCTION  search_stars (iName VARCHAR(100), iBirthYear INTEGER)
RETURNS VARCHAR(10)
BEGIN
	RETURN (
		SELECT id
		FROM stars AS s
		WHERE s.name=iName
    );
END$$
DELIMITER ;


DROP FUNCTION IF EXISTS search_genres;
DELIMITER $$ 
-- If genre exists in table, return its ID. Else return NULL
CREATE FUNCTION  search_genres (iName VARCHAR(32))
RETURNS INTEGER
BEGIN
	RETURN (
		SELECT id
		FROM genres AS g
		WHERE g.name=iName
    );
END$$
DELIMITER ;

