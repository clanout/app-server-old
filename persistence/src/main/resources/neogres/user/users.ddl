CREATE TABLE user_info (
  user_id    BIGINT                   NOT NULL PRIMARY KEY,
  username   TEXT                     NOT NULL UNIQUE,
  phone      TEXT UNIQUE,
  firstname  TEXT                     NOT NULL,
  lastname   TEXT                     NOT NULL,
  gender     CHARACTER VARYING(2)     NOT NULL,
  registered TIMESTAMP WITH TIME ZONE NOT NULL,
  status     CHARACTER VARYING(10)    NOT NULL
);

-- CREATE CONSTRAINT ON (user:User) ASSERT user.id IS UNIQUE