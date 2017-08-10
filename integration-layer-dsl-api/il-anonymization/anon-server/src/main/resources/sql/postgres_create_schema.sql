--
-- PostgreSQL database dump
--

-- Dumped from database version 9.5.6
-- Dumped by pg_dump version 9.5.6

-- Started on 2017-08-02 20:41:45 EEST

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

DROP DATABASE anonymization;
--
-- TOC entry 2170 (class 1262 OID 17798)
-- Name: anonymization; Type: DATABASE; Schema: -; Owner: postgres
--

CREATE DATABASE anonymization WITH TEMPLATE = template0 ENCODING = 'UTF8' LC_COLLATE = 'en_US.UTF-8' LC_CTYPE = 'en_US.UTF-8';


ALTER DATABASE anonymization OWNER TO postgres;

\connect anonymization

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 1 (class 3079 OID 12399)
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner:
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- TOC entry 2173 (class 0 OID 0)
-- Dependencies: 1
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner:
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 182 (class 1259 OID 17910)
-- Name: mapping; Type: TABLE; Schema: public; Owner: anon
--

CREATE TABLE mapping (
    id bigint NOT NULL,
    csp_id character varying(255) NOT NULL,
    data_type integer,
    ruleset bigint
);


ALTER TABLE mapping OWNER TO anon;

--
-- TOC entry 181 (class 1259 OID 17908)
-- Name: mapping_id_seq; Type: SEQUENCE; Schema: public; Owner: anon
--

CREATE SEQUENCE mapping_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE mapping_id_seq OWNER TO anon;

--
-- TOC entry 2174 (class 0 OID 0)
-- Dependencies: 181
-- Name: mapping_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: anon
--

ALTER SEQUENCE mapping_id_seq OWNED BY mapping.id;


--
-- TOC entry 184 (class 1259 OID 17918)
-- Name: ruleset; Type: TABLE; Schema: public; Owner: anon
--

CREATE TABLE ruleset (
    id bigint NOT NULL,
    description character varying(255),
    file bytea,
    filename character varying(255)
);


ALTER TABLE ruleset OWNER TO anon;

--
-- TOC entry 183 (class 1259 OID 17916)
-- Name: ruleset_id_seq; Type: SEQUENCE; Schema: public; Owner: anon
--

CREATE SEQUENCE ruleset_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE ruleset_id_seq OWNER TO anon;

--
-- TOC entry 2175 (class 0 OID 0)
-- Dependencies: 183
-- Name: ruleset_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: anon
--

ALTER SEQUENCE ruleset_id_seq OWNED BY ruleset.id;


--
-- TOC entry 186 (class 1259 OID 17929)
-- Name: secret_key; Type: TABLE; Schema: public; Owner: anon
--

CREATE TABLE secret_key (
    id bigint NOT NULL,
    created_at timestamp without time zone,
    seckey character varying(255)
);


ALTER TABLE secret_key OWNER TO anon;

--
-- TOC entry 185 (class 1259 OID 17927)
-- Name: secret_key_id_seq; Type: SEQUENCE; Schema: public; Owner: anon
--

CREATE SEQUENCE secret_key_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE secret_key_id_seq OWNER TO anon;

--
-- TOC entry 2176 (class 0 OID 0)
-- Dependencies: 185
-- Name: secret_key_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: anon
--

ALTER SEQUENCE secret_key_id_seq OWNED BY secret_key.id;


--
-- TOC entry 2036 (class 2604 OID 17913)
-- Name: id; Type: DEFAULT; Schema: public; Owner: anon
--

ALTER TABLE ONLY mapping ALTER COLUMN id SET DEFAULT nextval('mapping_id_seq'::regclass);


--
-- TOC entry 2037 (class 2604 OID 17921)
-- Name: id; Type: DEFAULT; Schema: public; Owner: anon
--

ALTER TABLE ONLY ruleset ALTER COLUMN id SET DEFAULT nextval('ruleset_id_seq'::regclass);


--
-- TOC entry 2038 (class 2604 OID 17932)
-- Name: id; Type: DEFAULT; Schema: public; Owner: anon
--

ALTER TABLE ONLY secret_key ALTER COLUMN id SET DEFAULT nextval('secret_key_id_seq'::regclass);


--
-- TOC entry 2161 (class 0 OID 17910)
-- Dependencies: 182
-- Data for Name: mapping; Type: TABLE DATA; Schema: public; Owner: anon
--

INSERT INTO mapping VALUES (37, 'CERT-EU', 1, 8);
INSERT INTO mapping VALUES (38, 'CERT-BUND', 8, 2);
INSERT INTO mapping VALUES (39, 'CERT-GR', 9, 2);


--
-- TOC entry 2177 (class 0 OID 0)
-- Dependencies: 181
-- Name: mapping_id_seq; Type: SEQUENCE SET; Schema: public; Owner: anon
--

SELECT pg_catalog.setval('mapping_id_seq', 39, true);


--
-- TOC entry 2163 (class 0 OID 17918)
-- Dependencies: 184
-- Data for Name: ruleset; Type: TABLE DATA; Schema: public; Owner: anon
--

INSERT INTO ruleset VALUES (2, '', '\x7b0a20202272756c6573223a205b0a202020207b0a202020202020226669656c64223a2022242e7472757374636972636c652e73686f72745f6e616d65222c0a20202020202022616374696f6e223a2022616e6f6e222c0a202020202020226669656c6474797065223a2022737472696e67220a202020207d2c0a202020207b0a202020202020226669656c64223a2022242e7472757374636972636c652e63726561746564222c0a20202020202022616374696f6e223a202270736575646f222c0a202020202020226669656c6474797065223a2022737472696e67220a202020207d2c0a20202020207b0a202020202020226669656c64223a2022242e7472757374636972636c652e7465616d735b2a5d2e6465736372697074696f6e222c0a20202020202022616374696f6e223a2022616e6f6e222c0a202020202020226669656c6474797065223a2022737472696e67220a202020207d0a20205d0a7d0a', 'rule.json');
INSERT INTO ruleset VALUES (8, '', '\x7b0a20202272756c6573223a205b0a202020207b0a202020202020226669656c64223a2022242e7472757374636972636c652e73686f72745f6e616d65222c0a20202020202022616374696f6e223a2022616e6f6e222c0a202020202020226669656c6474797065223a2022737472696e67220a202020207d2c0a202020207b0a202020202020226669656c64223a2022242e7472757374636972636c652e63726561746564222c0a20202020202022616374696f6e223a202270736575646f222c0a202020202020226669656c6474797065223a2022737472696e67220a202020207d2c0a20202020207b0a202020202020226669656c64223a2022242e7472757374636972636c652e7465616d735b2a5d2e6465736372697074696f6e222c0a20202020202022616374696f6e223a2022616e6f6e222c0a202020202020226669656c6474797065223a2022737472696e67220a202020207d2c0a202020207b0a202020202020226669656c64223a2022242e7472757374636972636c652e7465616d735b2a5d2e7468656970222c0a20202020202022616374696f6e223a2022616e6f6e222c0a202020202020226669656c6474797065223a20226970220a202020207d2c0a202020207b0a202020202020226669656c64223a2022242e7472757374636972636c652e7465616d735b2a5d2e746865656d61696c222c0a20202020202022616374696f6e223a2022616e6f6e222c0a202020202020226669656c6474797065223a2022656d61696c220a202020207d0a20205d0a7d0a', 'rule2.json');


--
-- TOC entry 2178 (class 0 OID 0)
-- Dependencies: 183
-- Name: ruleset_id_seq; Type: SEQUENCE SET; Schema: public; Owner: anon
--

SELECT pg_catalog.setval('ruleset_id_seq', 8, true);


--
-- TOC entry 2165 (class 0 OID 17929)
-- Dependencies: 186
-- Data for Name: secret_key; Type: TABLE DATA; Schema: public; Owner: anon
--

INSERT INTO secret_key VALUES (8020, '2017-08-02 20:41:35.539', '20726065-fb49-4d35-8f78-ca8f6e68a0ca');
INSERT INTO secret_key VALUES (8015, '2017-08-02 20:40:45.495', '8a403576-3cff-4e3b-baa0-f744494e9116');
INSERT INTO secret_key VALUES (8016, '2017-08-02 20:40:55.503', '6b3dfa4c-d29d-47f2-8eb2-d5b172ae396b');
INSERT INTO secret_key VALUES (8017, '2017-08-02 20:41:05.509', '27f308bb-1944-4fcb-9083-884b0e3e6627');
INSERT INTO secret_key VALUES (8018, '2017-08-02 20:41:15.519', '8b7b351c-9a0d-4fbe-8d99-0d01251b2c65');
INSERT INTO secret_key VALUES (8019, '2017-08-02 20:41:25.529', '88040427-014d-4a3f-9c35-dfdaa8a4cdb5');


--
-- TOC entry 2179 (class 0 OID 0)
-- Dependencies: 185
-- Name: secret_key_id_seq; Type: SEQUENCE SET; Schema: public; Owner: anon
--

SELECT pg_catalog.setval('secret_key_id_seq', 8020, true);


--
-- TOC entry 2040 (class 2606 OID 17915)
-- Name: mapping_pkey; Type: CONSTRAINT; Schema: public; Owner: anon
--

ALTER TABLE ONLY mapping
    ADD CONSTRAINT mapping_pkey PRIMARY KEY (id);


--
-- TOC entry 2042 (class 2606 OID 17926)
-- Name: ruleset_pkey; Type: CONSTRAINT; Schema: public; Owner: anon
--

ALTER TABLE ONLY ruleset
    ADD CONSTRAINT ruleset_pkey PRIMARY KEY (id);


--
-- TOC entry 2044 (class 2606 OID 17934)
-- Name: secret_key_pkey; Type: CONSTRAINT; Schema: public; Owner: anon
--

ALTER TABLE ONLY secret_key
    ADD CONSTRAINT secret_key_pkey PRIMARY KEY (id);


--
-- TOC entry 2045 (class 2606 OID 17935)
-- Name: fkn6iwr3nwadhvejfkbw77uosqt; Type: FK CONSTRAINT; Schema: public; Owner: anon
--

ALTER TABLE ONLY mapping
    ADD CONSTRAINT fkn6iwr3nwadhvejfkbw77uosqt FOREIGN KEY (ruleset) REFERENCES ruleset(id);


--
-- TOC entry 2172 (class 0 OID 0)
-- Dependencies: 6
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


-- Completed on 2017-08-02 20:41:45 EEST

--
-- PostgreSQL database dump complete
--

