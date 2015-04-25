DELETE FROM user_favourites
WHERE faver_id = ?
      AND favee_id = ANY (?)