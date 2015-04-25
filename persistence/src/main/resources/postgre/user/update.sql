UPDATE user_info
SET username = ?,
  phone      = ?,
  firstname  = ?,
  lastname   = ?,
  gender     = ?,
  registered = ?,
  status     = ?
WHERE user_id = ?