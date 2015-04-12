--
-- Name: user_info; Type: TABLE; Schema: public; Owner: grohit; Tablespace:
--

CREATE TABLE user_info (
    user_id uuid NOT NULL,
    username text NOT NULL,
    phone text,
    firstname text,
    lastname text,
    gender character varying(2),
    registered timestamp with time zone,
    status character varying(10)
);


ALTER TABLE user_info OWNER TO grohit;

--
-- Name: TABLE user_info; Type: COMMENT; Schema: public; Owner: grohit
--

COMMENT ON TABLE user_info IS 'Primary users table';
