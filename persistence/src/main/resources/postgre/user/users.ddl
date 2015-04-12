CREATE TABLE user_info (
    user_id     uuid NOT NULL PRIMARY KEY,
    username    text NOT NULL UNIQUE ,
    phone       text UNIQUE ,
    firstname   text NOT NULL ,
    lastname    text NOT NULL ,
    gender      character varying(2) NOT NULL,
    registered  timestamp with time zone NOT NULL ,
    status      character varying(10) NOT NULL
);