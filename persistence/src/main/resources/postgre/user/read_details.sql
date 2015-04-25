SELECT
  friend_list.friend_id,
  (info.firstname || ' ' || info.lastname) AS name,
  friend_list.is_blocked,
  CASE WHEN friend_list.favee_id IS NOT NULL THEN TRUE
  ELSE FALSE END                           AS is_favourite
FROM user_info info,
  (
    SELECT
      a.*,
      b.*
    FROM
      (
        (
          SELECT
            user_id2     AS friend_id,
            (NOT status) AS is_blocked
          FROM user_relationships
          WHERE user_id1 = ?
        )
        UNION ALL
        (
          SELECT
            user_id1 AS friend_id,
            FALSE    AS is_blocked
          FROM user_relationships
          WHERE user_id2 = ?
                AND status <> FALSE
        )
      ) a
      LEFT OUTER JOIN
      (
        SELECT favee_id
        FROM user_favourites
        WHERE faver_id = ?
      ) b
        ON a.friend_id = b.favee_id
  ) AS friend_list
WHERE friend_list.friend_id = info.user_id