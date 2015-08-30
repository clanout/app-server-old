DELETE FROM user_location
WHERE user_id = ?;

INSERT INTO user_location
VALUES (?,?);