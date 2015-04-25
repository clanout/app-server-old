CREATE TABLE user_info
(
  user_id    BIGINT                   NOT NULL,
  username   TEXT                     NOT NULL,
  phone      TEXT,
  firstname  TEXT                     NOT NULL,
  lastname   TEXT                     NOT NULL,
  gender     CHARACTER VARYING(2)     NOT NULL,
  registered TIMESTAMP WITH TIME ZONE NOT NULL,
  status     CHARACTER VARYING(10)    NOT NULL,
  CONSTRAINT user_info_pkey PRIMARY KEY (user_id),
  CONSTRAINT user_info_phone_key UNIQUE (phone),
  CONSTRAINT user_info_username_key UNIQUE (username)
);

CREATE TABLE user_relationships
(
  user_id1 BIGINT,
  user_id2 BIGINT,
  status   BOOLEAN,
  CONSTRAINT user_relationships_user_id1_fkey FOREIGN KEY (user_id1)
  REFERENCES user_info (user_id) MATCH SIMPLE
  ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT user_relationships_user_id2_fkey FOREIGN KEY (user_id2)
  REFERENCES user_info (user_id) MATCH SIMPLE
  ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE user_favourites
(
  faver_id BIGINT,
  favee_id BIGINT,
  CONSTRAINT user_favourites_favee_id_fkey FOREIGN KEY (favee_id)
  REFERENCES user_info (user_id) MATCH SIMPLE
  ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT user_favourites_faver_id_fkey FOREIGN KEY (faver_id)
  REFERENCES user_info (user_id) MATCH SIMPLE
  ON UPDATE CASCADE ON DELETE CASCADE
)