--
-- PostgreSQL database dump
--

-- Dumped from database version 9.6.3
-- Dumped by pg_dump version 9.6.3

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: auth_group; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE auth_group (
    id integer NOT NULL,
    name character varying(80) NOT NULL
);


ALTER TABLE auth_group OWNER TO postgres;

--
-- Name: auth_group_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE auth_group_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE auth_group_id_seq OWNER TO postgres;

--
-- Name: auth_group_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE auth_group_id_seq OWNED BY auth_group.id;


--
-- Name: auth_group_permissions; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE auth_group_permissions (
    id integer NOT NULL,
    group_id integer NOT NULL,
    permission_id integer NOT NULL
);


ALTER TABLE auth_group_permissions OWNER TO postgres;

--
-- Name: auth_group_permissions_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE auth_group_permissions_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE auth_group_permissions_id_seq OWNER TO postgres;

--
-- Name: auth_group_permissions_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE auth_group_permissions_id_seq OWNED BY auth_group_permissions.id;


--
-- Name: auth_permission; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE auth_permission (
    id integer NOT NULL,
    name character varying(255) NOT NULL,
    content_type_id integer NOT NULL,
    codename character varying(100) NOT NULL
);


ALTER TABLE auth_permission OWNER TO postgres;

--
-- Name: auth_permission_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE auth_permission_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE auth_permission_id_seq OWNER TO postgres;

--
-- Name: auth_permission_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE auth_permission_id_seq OWNED BY auth_permission.id;


--
-- Name: ctc_country; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ctc_country (
    id uuid NOT NULL,
    name character varying(255) NOT NULL
);


ALTER TABLE ctc_country OWNER TO postgres;

--
-- Name: ctc_historicalcountry; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ctc_historicalcountry (
    id uuid NOT NULL,
    name character varying(255) NOT NULL,
    history_id integer NOT NULL,
    history_date timestamp with time zone NOT NULL,
    history_type character varying(1) NOT NULL,
    history_user_id integer
);


ALTER TABLE ctc_historicalcountry OWNER TO postgres;

--
-- Name: ctc_historicalcountry_history_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE ctc_historicalcountry_history_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE ctc_historicalcountry_history_id_seq OWNER TO postgres;

--
-- Name: ctc_historicalcountry_history_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE ctc_historicalcountry_history_id_seq OWNED BY ctc_historicalcountry.history_id;


--
-- Name: ctc_historicalsector; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ctc_historicalsector (
    id uuid NOT NULL,
    name character varying(255) NOT NULL,
    history_id integer NOT NULL,
    history_date timestamp with time zone NOT NULL,
    history_type character varying(1) NOT NULL,
    history_user_id integer
);


ALTER TABLE ctc_historicalsector OWNER TO postgres;

--
-- Name: ctc_historicalsector_history_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE ctc_historicalsector_history_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE ctc_historicalsector_history_id_seq OWNER TO postgres;

--
-- Name: ctc_historicalsector_history_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE ctc_historicalsector_history_id_seq OWNED BY ctc_historicalsector.history_id;


--
-- Name: ctc_historicalteam; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ctc_historicalteam (
    id uuid NOT NULL,
    short_name character varying(128) NOT NULL,
    name character varying(255) NOT NULL,
    host_organisation character varying(255) NOT NULL,
    description character varying(255) NOT NULL,
    established date NOT NULL,
    created timestamp with time zone NOT NULL,
    csp_installed boolean NOT NULL,
    csp_id character varying(255) NOT NULL,
    csp_domain character varying(255) NOT NULL,
    history_id integer NOT NULL,
    history_date timestamp with time zone NOT NULL,
    history_type character varying(1) NOT NULL,
    country_id uuid,
    history_user_id integer,
    status_id uuid,
    history_change_reason character varying(50)
);


ALTER TABLE ctc_historicalteam OWNER TO postgres;

--
-- Name: ctc_historicalteam_history_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE ctc_historicalteam_history_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE ctc_historicalteam_history_id_seq OWNER TO postgres;

--
-- Name: ctc_historicalteam_history_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE ctc_historicalteam_history_id_seq OWNED BY ctc_historicalteam.history_id;


--
-- Name: ctc_historicalteamstatus; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ctc_historicalteamstatus (
    id uuid NOT NULL,
    name character varying(255) NOT NULL,
    history_id integer NOT NULL,
    history_date timestamp with time zone NOT NULL,
    history_type character varying(1) NOT NULL,
    history_user_id integer
);


ALTER TABLE ctc_historicalteamstatus OWNER TO postgres;

--
-- Name: ctc_historicalteamstatus_history_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE ctc_historicalteamstatus_history_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE ctc_historicalteamstatus_history_id_seq OWNER TO postgres;

--
-- Name: ctc_historicalteamstatus_history_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE ctc_historicalteamstatus_history_id_seq OWNED BY ctc_historicalteamstatus.history_id;


--
-- Name: ctc_historicalteamtype; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ctc_historicalteamtype (
    id uuid NOT NULL,
    name character varying(255) NOT NULL,
    history_id integer NOT NULL,
    history_date timestamp with time zone NOT NULL,
    history_type character varying(1) NOT NULL,
    history_user_id integer
);


ALTER TABLE ctc_historicalteamtype OWNER TO postgres;

--
-- Name: ctc_historicalteamtype_history_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE ctc_historicalteamtype_history_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE ctc_historicalteamtype_history_id_seq OWNER TO postgres;

--
-- Name: ctc_historicalteamtype_history_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE ctc_historicalteamtype_history_id_seq OWNED BY ctc_historicalteamtype.history_id;


--
-- Name: ctc_historicaltrustcircle; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ctc_historicaltrustcircle (
    id uuid NOT NULL,
    short_name character varying(128) NOT NULL,
    name character varying(255) NOT NULL,
    description character varying(255) NOT NULL,
    auth_source character varying(255) NOT NULL,
    info_url character varying(200) NOT NULL,
    membership_url character varying(200) NOT NULL,
    created timestamp with time zone NOT NULL,
    history_id integer NOT NULL,
    history_date timestamp with time zone NOT NULL,
    history_type character varying(1) NOT NULL,
    history_user_id integer,
    history_change_reason character varying(50)
);


ALTER TABLE ctc_historicaltrustcircle OWNER TO postgres;

--
-- Name: ctc_historicaltrustcircle_history_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE ctc_historicaltrustcircle_history_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE ctc_historicaltrustcircle_history_id_seq OWNER TO postgres;

--
-- Name: ctc_historicaltrustcircle_history_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE ctc_historicaltrustcircle_history_id_seq OWNED BY ctc_historicaltrustcircle.history_id;


--
-- Name: ctc_sector; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ctc_sector (
    id uuid NOT NULL,
    name character varying(255) NOT NULL
);


ALTER TABLE ctc_sector OWNER TO postgres;

--
-- Name: ctc_team; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ctc_team (
    id uuid NOT NULL,
    short_name character varying(128) NOT NULL,
    name character varying(255) NOT NULL,
    host_organisation character varying(255) NOT NULL,
    description character varying(255) NOT NULL,
    established date NOT NULL,
    created timestamp with time zone NOT NULL,
    csp_installed boolean NOT NULL,
    csp_id character varying(255) NOT NULL,
    csp_domain character varying(255) NOT NULL,
    country_id uuid NOT NULL,
    status_id uuid NOT NULL
);


ALTER TABLE ctc_team OWNER TO postgres;

--
-- Name: ctc_team_additional_countries; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ctc_team_additional_countries (
    id integer NOT NULL,
    team_id uuid NOT NULL,
    country_id uuid NOT NULL
);


ALTER TABLE ctc_team_additional_countries OWNER TO postgres;

--
-- Name: ctc_team_additional_countries_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE ctc_team_additional_countries_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE ctc_team_additional_countries_id_seq OWNER TO postgres;

--
-- Name: ctc_team_additional_countries_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE ctc_team_additional_countries_id_seq OWNED BY ctc_team_additional_countries.id;


--
-- Name: ctc_team_nis_sectors; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ctc_team_nis_sectors (
    id integer NOT NULL,
    team_id uuid NOT NULL,
    sector_id uuid NOT NULL
);


ALTER TABLE ctc_team_nis_sectors OWNER TO postgres;

--
-- Name: ctc_team_nis_sectors_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE ctc_team_nis_sectors_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE ctc_team_nis_sectors_id_seq OWNER TO postgres;

--
-- Name: ctc_team_nis_sectors_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE ctc_team_nis_sectors_id_seq OWNED BY ctc_team_nis_sectors.id;


--
-- Name: ctc_team_nis_team_types; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ctc_team_nis_team_types (
    id integer NOT NULL,
    team_id uuid NOT NULL,
    teamtype_id uuid NOT NULL
);


ALTER TABLE ctc_team_nis_team_types OWNER TO postgres;

--
-- Name: ctc_team_nis_team_types_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE ctc_team_nis_team_types_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE ctc_team_nis_team_types_id_seq OWNER TO postgres;

--
-- Name: ctc_team_nis_team_types_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE ctc_team_nis_team_types_id_seq OWNED BY ctc_team_nis_team_types.id;


--
-- Name: ctc_teamstatus; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ctc_teamstatus (
    id uuid NOT NULL,
    name character varying(255) NOT NULL
);


ALTER TABLE ctc_teamstatus OWNER TO postgres;

--
-- Name: ctc_teamtype; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ctc_teamtype (
    id uuid NOT NULL,
    name character varying(255) NOT NULL
);


ALTER TABLE ctc_teamtype OWNER TO postgres;

--
-- Name: ctc_trustcircle; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ctc_trustcircle (
    id uuid NOT NULL,
    short_name character varying(128) NOT NULL,
    name character varying(255) NOT NULL,
    description character varying(255) NOT NULL,
    auth_source character varying(255) NOT NULL,
    info_url character varying(200) NOT NULL,
    membership_url character varying(200) NOT NULL,
    created timestamp with time zone NOT NULL
);


ALTER TABLE ctc_trustcircle OWNER TO postgres;

--
-- Name: ctc_trustcircle_teams; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ctc_trustcircle_teams (
    id integer NOT NULL,
    trustcircle_id uuid NOT NULL,
    team_id uuid NOT NULL
);


ALTER TABLE ctc_trustcircle_teams OWNER TO postgres;

--
-- Name: ctc_trustcircle_teams_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE ctc_trustcircle_teams_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE ctc_trustcircle_teams_id_seq OWNER TO postgres;

--
-- Name: ctc_trustcircle_teams_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE ctc_trustcircle_teams_id_seq OWNED BY ctc_trustcircle_teams.id;


--
-- Name: django_admin_log; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE django_admin_log (
    id integer NOT NULL,
    action_time timestamp with time zone NOT NULL,
    object_id text,
    object_repr character varying(200) NOT NULL,
    action_flag smallint NOT NULL,
    change_message text NOT NULL,
    content_type_id integer,
    user_id integer NOT NULL,
    CONSTRAINT django_admin_log_action_flag_check CHECK ((action_flag >= 0))
);


ALTER TABLE django_admin_log OWNER TO postgres;

--
-- Name: django_admin_log_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE django_admin_log_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE django_admin_log_id_seq OWNER TO postgres;

--
-- Name: django_admin_log_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE django_admin_log_id_seq OWNED BY django_admin_log.id;


--
-- Name: django_content_type; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE django_content_type (
    id integer NOT NULL,
    app_label character varying(100) NOT NULL,
    model character varying(100) NOT NULL
);


ALTER TABLE django_content_type OWNER TO postgres;

--
-- Name: django_content_type_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE django_content_type_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE django_content_type_id_seq OWNER TO postgres;

--
-- Name: django_content_type_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE django_content_type_id_seq OWNED BY django_content_type.id;


--
-- Name: django_migrations; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE django_migrations (
    id integer NOT NULL,
    app character varying(255) NOT NULL,
    name character varying(255) NOT NULL,
    applied timestamp with time zone NOT NULL
);


ALTER TABLE django_migrations OWNER TO postgres;

--
-- Name: django_migrations_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE django_migrations_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE django_migrations_id_seq OWNER TO postgres;

--
-- Name: django_migrations_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE django_migrations_id_seq OWNED BY django_migrations.id;


--
-- Name: django_session; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE django_session (
    session_key character varying(40) NOT NULL,
    session_data text NOT NULL,
    expire_date timestamp with time zone NOT NULL
);


ALTER TABLE django_session OWNER TO postgres;

--
-- Name: integration_changelog; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE integration_changelog (
    id integer NOT NULL,
    created timestamp with time zone NOT NULL,
    action character varying(50) NOT NULL,
    model_name character varying(50) NOT NULL,
    model_pk character varying(128) NOT NULL,
    to_share boolean NOT NULL,
    target_trustcircle character varying(255)
);


ALTER TABLE integration_changelog OWNER TO postgres;

--
-- Name: integration_changelog_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE integration_changelog_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE integration_changelog_id_seq OWNER TO postgres;

--
-- Name: integration_changelog_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE integration_changelog_id_seq OWNED BY integration_changelog.id;


--
-- Name: openam_auth_user; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE openam_auth_user (
    id integer NOT NULL,
    password character varying(128) NOT NULL,
    last_login timestamp with time zone,
    is_superuser boolean NOT NULL,
    username character varying(30) NOT NULL,
    first_name character varying(30) NOT NULL,
    last_name character varying(30) NOT NULL,
    email character varying(254) NOT NULL,
    is_staff boolean NOT NULL,
    is_active boolean NOT NULL,
    date_joined timestamp with time zone NOT NULL
);


ALTER TABLE openam_auth_user OWNER TO postgres;

--
-- Name: openam_auth_user_groups; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE openam_auth_user_groups (
    id integer NOT NULL,
    user_id integer NOT NULL,
    group_id integer NOT NULL
);


ALTER TABLE openam_auth_user_groups OWNER TO postgres;

--
-- Name: openam_auth_user_groups_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE openam_auth_user_groups_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE openam_auth_user_groups_id_seq OWNER TO postgres;

--
-- Name: openam_auth_user_groups_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE openam_auth_user_groups_id_seq OWNED BY openam_auth_user_groups.id;


--
-- Name: openam_auth_user_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE openam_auth_user_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE openam_auth_user_id_seq OWNER TO postgres;

--
-- Name: openam_auth_user_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE openam_auth_user_id_seq OWNED BY openam_auth_user.id;


--
-- Name: openam_auth_user_user_permissions; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE openam_auth_user_user_permissions (
    id integer NOT NULL,
    user_id integer NOT NULL,
    permission_id integer NOT NULL
);


ALTER TABLE openam_auth_user_user_permissions OWNER TO postgres;

--
-- Name: openam_auth_user_user_permissions_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE openam_auth_user_user_permissions_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE openam_auth_user_user_permissions_id_seq OWNER TO postgres;

--
-- Name: openam_auth_user_user_permissions_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE openam_auth_user_user_permissions_id_seq OWNED BY openam_auth_user_user_permissions.id;


--
-- Name: auth_group id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY auth_group ALTER COLUMN id SET DEFAULT nextval('auth_group_id_seq'::regclass);


--
-- Name: auth_group_permissions id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY auth_group_permissions ALTER COLUMN id SET DEFAULT nextval('auth_group_permissions_id_seq'::regclass);


--
-- Name: auth_permission id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY auth_permission ALTER COLUMN id SET DEFAULT nextval('auth_permission_id_seq'::regclass);


--
-- Name: ctc_historicalcountry history_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ctc_historicalcountry ALTER COLUMN history_id SET DEFAULT nextval('ctc_historicalcountry_history_id_seq'::regclass);


--
-- Name: ctc_historicalsector history_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ctc_historicalsector ALTER COLUMN history_id SET DEFAULT nextval('ctc_historicalsector_history_id_seq'::regclass);


--
-- Name: ctc_historicalteam history_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ctc_historicalteam ALTER COLUMN history_id SET DEFAULT nextval('ctc_historicalteam_history_id_seq'::regclass);


--
-- Name: ctc_historicalteamstatus history_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ctc_historicalteamstatus ALTER COLUMN history_id SET DEFAULT nextval('ctc_historicalteamstatus_history_id_seq'::regclass);


--
-- Name: ctc_historicalteamtype history_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ctc_historicalteamtype ALTER COLUMN history_id SET DEFAULT nextval('ctc_historicalteamtype_history_id_seq'::regclass);


--
-- Name: ctc_historicaltrustcircle history_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ctc_historicaltrustcircle ALTER COLUMN history_id SET DEFAULT nextval('ctc_historicaltrustcircle_history_id_seq'::regclass);


--
-- Name: ctc_team_additional_countries id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ctc_team_additional_countries ALTER COLUMN id SET DEFAULT nextval('ctc_team_additional_countries_id_seq'::regclass);


--
-- Name: ctc_team_nis_sectors id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ctc_team_nis_sectors ALTER COLUMN id SET DEFAULT nextval('ctc_team_nis_sectors_id_seq'::regclass);


--
-- Name: ctc_team_nis_team_types id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ctc_team_nis_team_types ALTER COLUMN id SET DEFAULT nextval('ctc_team_nis_team_types_id_seq'::regclass);


--
-- Name: ctc_trustcircle_teams id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ctc_trustcircle_teams ALTER COLUMN id SET DEFAULT nextval('ctc_trustcircle_teams_id_seq'::regclass);


--
-- Name: django_admin_log id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY django_admin_log ALTER COLUMN id SET DEFAULT nextval('django_admin_log_id_seq'::regclass);


--
-- Name: django_content_type id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY django_content_type ALTER COLUMN id SET DEFAULT nextval('django_content_type_id_seq'::regclass);


--
-- Name: django_migrations id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY django_migrations ALTER COLUMN id SET DEFAULT nextval('django_migrations_id_seq'::regclass);


--
-- Name: integration_changelog id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY integration_changelog ALTER COLUMN id SET DEFAULT nextval('integration_changelog_id_seq'::regclass);


--
-- Name: openam_auth_user id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY openam_auth_user ALTER COLUMN id SET DEFAULT nextval('openam_auth_user_id_seq'::regclass);


--
-- Name: openam_auth_user_groups id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY openam_auth_user_groups ALTER COLUMN id SET DEFAULT nextval('openam_auth_user_groups_id_seq'::regclass);


--
-- Name: openam_auth_user_user_permissions id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY openam_auth_user_user_permissions ALTER COLUMN id SET DEFAULT nextval('openam_auth_user_user_permissions_id_seq'::regclass);


--
-- Data for Name: auth_group; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY auth_group (id, name) FROM stdin;
\.


--
-- Name: auth_group_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('auth_group_id_seq', 1, false);


--
-- Data for Name: auth_group_permissions; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY auth_group_permissions (id, group_id, permission_id) FROM stdin;
\.


--
-- Name: auth_group_permissions_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('auth_group_permissions_id_seq', 1, false);


--
-- Data for Name: auth_permission; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY auth_permission (id, name, content_type_id, codename) FROM stdin;
1	Can add log entry	1	add_logentry
2	Can change log entry	1	change_logentry
3	Can delete log entry	1	delete_logentry
4	Can add permission	2	add_permission
5	Can change permission	2	change_permission
6	Can delete permission	2	delete_permission
7	Can add group	3	add_group
8	Can change group	3	change_group
9	Can delete group	3	delete_group
10	Can add content type	4	add_contenttype
11	Can change content type	4	change_contenttype
12	Can delete content type	4	delete_contenttype
13	Can add session	5	add_session
14	Can change session	5	change_session
15	Can delete session	5	delete_session
16	Can add historical Country	6	add_historicalcountry
17	Can change historical Country	6	change_historicalcountry
18	Can delete historical Country	6	delete_historicalcountry
19	Can add Country	7	add_country
20	Can change Country	7	change_country
21	Can delete Country	7	delete_country
22	Can add historical sector	8	add_historicalsector
23	Can change historical sector	8	change_historicalsector
24	Can delete historical sector	8	delete_historicalsector
25	Can add sector	9	add_sector
26	Can change sector	9	change_sector
27	Can delete sector	9	delete_sector
28	Can add historical Team Type	10	add_historicalteamtype
29	Can change historical Team Type	10	change_historicalteamtype
30	Can delete historical Team Type	10	delete_historicalteamtype
31	Can add Team Type	11	add_teamtype
32	Can change Team Type	11	change_teamtype
33	Can delete Team Type	11	delete_teamtype
34	Can add historical Team Status	12	add_historicalteamstatus
35	Can change historical Team Status	12	change_historicalteamstatus
36	Can delete historical Team Status	12	delete_historicalteamstatus
37	Can add Team Status	13	add_teamstatus
38	Can change Team Status	13	change_teamstatus
39	Can delete Team Status	13	delete_teamstatus
40	Can add historical team	14	add_historicalteam
41	Can change historical team	14	change_historicalteam
42	Can delete historical team	14	delete_historicalteam
43	Can add team	15	add_team
44	Can change team	15	change_team
45	Can delete team	15	delete_team
46	Read-Only access to REST-API	15	api_read
47	Read-Write access to REST-API	15	api_write
48	Read-Only access to Web Frontend	15	web_write
49	Can add historical trust circle	16	add_historicaltrustcircle
50	Can change historical trust circle	16	change_historicaltrustcircle
51	Can delete historical trust circle	16	delete_historicaltrustcircle
52	Can add trust circle	17	add_trustcircle
53	Can change trust circle	17	change_trustcircle
54	Can delete trust circle	17	delete_trustcircle
55	Can add change log	18	add_changelog
56	Can change change log	18	change_changelog
57	Can delete change log	18	delete_changelog
58	Can add user	19	add_user
59	Can change user	19	change_user
60	Can delete user	19	delete_user
\.


--
-- Name: auth_permission_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('auth_permission_id_seq', 60, true);


--
-- Data for Name: ctc_country; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY ctc_country (id, name) FROM stdin;
2a56120a-debd-40ed-ad59-c68def1c6a72	Italy
41d68647-1c3a-4681-8c7e-e85cd374d2cd	Germany
6a0eff1b-4a67-43fc-91c6-6e0792e5751d	European Union
98f6508f-6706-4a74-ae5a-b35bf278c47d	Greece
a46d4921-b8ac-471e-8c45-ed2ebf77d253	United Kingdom
\.


--
-- Data for Name: ctc_historicalcountry; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY ctc_historicalcountry (id, name, history_id, history_date, history_type, history_user_id) FROM stdin;
\.


--
-- Name: ctc_historicalcountry_history_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('ctc_historicalcountry_history_id_seq', 1, false);


--
-- Data for Name: ctc_historicalsector; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY ctc_historicalsector (id, name, history_id, history_date, history_type, history_user_id) FROM stdin;
\.


--
-- Name: ctc_historicalsector_history_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('ctc_historicalsector_history_id_seq', 1, false);


--
-- Data for Name: ctc_historicalteam; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY ctc_historicalteam (id, short_name, name, host_organisation, description, established, created, csp_installed, csp_id, csp_domain, history_id, history_date, history_type, country_id, history_user_id, status_id, history_change_reason) FROM stdin;
306de7b8-5e8c-4a5e-9de2-1f837713bfc1	central-csp	central-csp	central-csp	central-csp	2010-01-27	2017-04-25 14:07:24.537+00	t	central-csp	central-csp.athens.intrasoft-intl.private	6	2017-10-03 15:38:36.225569+00	~	6a0eff1b-4a67-43fc-91c6-6e0792e5751d	4	c3d6f205-eb4d-4a1b-ac2e-37a2ef80f69d	\N
306de7b8-5e8c-4a5e-9de2-1f837713bfc1	central-csp	central-csp	central-csp	central-csp	2010-01-27	2017-04-25 14:07:24.537+00	t	central-csp	central-csp.athens.intrasoft-intl.private	7	2017-10-03 15:38:55.064387+00	~	6a0eff1b-4a67-43fc-91c6-6e0792e5751d	4	c3d6f205-eb4d-4a1b-ac2e-37a2ef80f69d	\N
578c0e4e-ebaf-455b-a2a1-faffb14be9e1	demo1-csp	CERT-Bund	German Government	CERT of the German Government	2017-03-27	2017-04-25 14:07:24.533+00	t		demo1-csp.athens.intrasoft-intl.private	8	2017-10-03 15:39:04.844954+00	-	41d68647-1c3a-4681-8c7e-e85cd374d2cd	4	bfa2069f-ebb1-426d-ab20-d8af724baa82	\N
af9d06ac-d7be-4684-86a3-808fe4f4d17c	demo2-csp	CERT-GR	CERT-GR	CERT-GR	2017-06-06	2017-06-12 11:12:10.927232+00	t		demo2-csp.athens.intrasoft-intl.private	9	2017-10-03 15:39:09.382767+00	-	98f6508f-6706-4a74-ae5a-b35bf278c47d	4	c3d6f205-eb4d-4a1b-ac2e-37a2ef80f69d	\N
\.


--
-- Name: ctc_historicalteam_history_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('ctc_historicalteam_history_id_seq', 9, true);


--
-- Data for Name: ctc_historicalteamstatus; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY ctc_historicalteamstatus (id, name, history_id, history_date, history_type, history_user_id) FROM stdin;
\.


--
-- Name: ctc_historicalteamstatus_history_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('ctc_historicalteamstatus_history_id_seq', 1, false);


--
-- Data for Name: ctc_historicalteamtype; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY ctc_historicalteamtype (id, name, history_id, history_date, history_type, history_user_id) FROM stdin;
\.


--
-- Name: ctc_historicalteamtype_history_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('ctc_historicalteamtype_history_id_seq', 1, false);


--
-- Data for Name: ctc_historicaltrustcircle; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY ctc_historicaltrustcircle (id, short_name, name, description, auth_source, info_url, membership_url, created, history_id, history_date, history_type, history_user_id, history_change_reason) FROM stdin;
\.


--
-- Name: ctc_historicaltrustcircle_history_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('ctc_historicaltrustcircle_history_id_seq', 9, true);


--
-- Data for Name: ctc_sector; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY ctc_sector (id, name) FROM stdin;
2efad715-7d79-4445-89f0-d3b547277e8c	Health
38b6a409-5601-4814-b55d-93e741c71dd9	Banking
449f0792-2e05-42ab-b1e2-c78936e5eb46	Transport
8d2d2def-5ea0-42b1-b2ed-3c57a02d0d35	Energy
\.


--
-- Data for Name: ctc_team; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY ctc_team (id, short_name, name, host_organisation, description, established, created, csp_installed, csp_id, csp_domain, country_id, status_id) FROM stdin;
306de7b8-5e8c-4a5e-9de2-1f837713bfc1	central-csp	central-csp	central-csp	central-csp	2010-01-27	2017-04-25 14:07:24.537+00	t	central-csp	central-csp.athens.intrasoft-intl.private	6a0eff1b-4a67-43fc-91c6-6e0792e5751d	c3d6f205-eb4d-4a1b-ac2e-37a2ef80f69d
\.


--
-- Data for Name: ctc_team_additional_countries; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY ctc_team_additional_countries (id, team_id, country_id) FROM stdin;
\.


--
-- Name: ctc_team_additional_countries_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('ctc_team_additional_countries_id_seq', 1, false);


--
-- Data for Name: ctc_team_nis_sectors; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY ctc_team_nis_sectors (id, team_id, sector_id) FROM stdin;
\.


--
-- Name: ctc_team_nis_sectors_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('ctc_team_nis_sectors_id_seq', 8, true);


--
-- Data for Name: ctc_team_nis_team_types; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY ctc_team_nis_team_types (id, team_id, teamtype_id) FROM stdin;
\.


--
-- Name: ctc_team_nis_team_types_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('ctc_team_nis_team_types_id_seq', 8, true);


--
-- Data for Name: ctc_teamstatus; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY ctc_teamstatus (id, name) FROM stdin;
743cbb6a-60ef-4f8b-b7e6-21f30315818a	Other
bfa2069f-ebb1-426d-ab20-d8af724baa82	Known
c3d6f205-eb4d-4a1b-ac2e-37a2ef80f69d	Active
\.


--
-- Data for Name: ctc_teamtype; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY ctc_teamtype (id, name) FROM stdin;
04215624-e922-4e7d-9eb3-d8e005cdff2e	EU CSIRT
2282af0e-292d-400b-ba31-047d9d43f229	MS CSIRT
656000e7-fda8-4d98-b2e2-a008ffca8689	MS National SPoC
ce6f82fc-de9e-45e9-8d07-10d18d58421c	MS Competent Authority
\.


--
-- Data for Name: ctc_trustcircle; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY ctc_trustcircle (id, short_name, name, description, auth_source, info_url, membership_url, created) FROM stdin;
2883f242-3e07-4378-9091-0d198e4886ba	CTC::EU NIS:Basic	NIS Basic	NIS Basic Circle	ENISA			2017-04-25 14:07:24.547+00
6376712d-f767-4bac-9d9d-66dace5b2380	CTC::TI:Accredited	TI Accredited Teams	TI Accredited Teams	ENISA			2017-04-25 14:07:24.543+00
a36c31f4-dad3-4f49-b443-e6d6333649b1	CTC::CSP_ALL	CTC::CSP_ALL	CTC::CSP_ALL	ENISA			2017-06-12 10:39:14.201312+00
ba675164-7fe3-46e3-bacb-8cc04ab588d7	CTC::SHARING_DATA_THREAT	CTC::SHARING_DATA_THREAT	CTC::SHARING_DATA_THREAT	ENISA			2017-06-12 10:53:01.115198+00
4b2bb43a-c98e-440e-a4b2-2df6802f63dd	CTC::SHARING_DATA_EVENT	CTC::SHARING_DATA_EVENT	CTC::SHARING_DATA_EVENT	ENISA			2017-06-12 10:53:26.783719+00
091f38af-31cb-44ed-9953-7bc2d1e1d77f	CTC::SHARING_DATA_VULNERABILITY	CTC::SHARING_DATA_VULNERABILITY	CTC::SHARING_DATA_VULNERABILITY	ENISA			2017-06-12 10:53:43.767855+00
5bbacdfa-920b-4db2-b282-584c444416c9	CTC::SHARING_DATA_ARTEFACT	CTC::SHARING_DATA_ARTEFACT	CTC::SHARING_DATA_ARTEFACT	ENISA			2017-06-12 10:54:08.119613+00
1fbaea6b-7e41-44ff-9daa-0d6fe5ecfb19	CTC::SHARING_DATA_INCIDENT	CTC::SHARING_DATA_INCIDENT	CTC::SHARING_DATA_INCIDENT	ENISA			2017-06-12 10:54:20.124785+00
248d0094-aee2-4f5b-981b-00990207c610	CTC::SHARING_DATA_CONTACT	CTC::SHARING_DATA_CONTACT	CTC::SHARING_DATA_CONTACT	ENISA			2017-06-12 10:54:34.179186+00
5ab7f4cd-266a-4947-8cfb-ba3d6080763f	CTC::SHARING_DATA_FILE	CTC::SHARING_DATA_FILE	CTC::SHARING_DATA_FILE	ENISA			2017-06-12 10:54:45.506023+00
61ee0197-587f-43ce-afcf-310b36b5bfe9	CTC::SHARING_DATA_CHAT	CTC::SHARING_DATA_CHAT	CTC::SHARING_DATA_CHAT	ENISA			2017-06-12 10:54:56.126937+00
dc58e760-a05e-42d2-868c-cb2305825857	CTC::FIRST	FIRST Trust Circle	FIRST Members	ENISA			2017-04-25 14:07:24.539+00
\.


--
-- Data for Name: ctc_trustcircle_teams; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY ctc_trustcircle_teams (id, trustcircle_id, team_id) FROM stdin;
26	a36c31f4-dad3-4f49-b443-e6d6333649b1	306de7b8-5e8c-4a5e-9de2-1f837713bfc1
\.


--
-- Name: ctc_trustcircle_teams_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('ctc_trustcircle_teams_id_seq', 26, true);


--
-- Data for Name: django_admin_log; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY django_admin_log (id, action_time, object_id, object_repr, action_flag, change_message, content_type_id, user_id) FROM stdin;
\.


--
-- Name: django_admin_log_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('django_admin_log_id_seq', 1, false);


--
-- Data for Name: django_content_type; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY django_content_type (id, app_label, model) FROM stdin;
1	admin	logentry
2	auth	permission
3	auth	group
4	contenttypes	contenttype
5	sessions	session
6	ctc	historicalcountry
7	ctc	country
8	ctc	historicalsector
9	ctc	sector
10	ctc	historicalteamtype
11	ctc	teamtype
12	ctc	historicalteamstatus
13	ctc	teamstatus
14	ctc	historicalteam
15	ctc	team
16	ctc	historicaltrustcircle
17	ctc	trustcircle
18	integration	changelog
19	openam_auth	user
\.


--
-- Name: django_content_type_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('django_content_type_id_seq', 19, true);


--
-- Data for Name: django_migrations; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY django_migrations (id, app, name, applied) FROM stdin;
1	contenttypes	0001_initial	2017-06-12 10:32:53.644134+00
2	contenttypes	0002_remove_content_type_name	2017-06-12 10:32:53.656688+00
3	auth	0001_initial	2017-06-12 10:32:53.695671+00
4	auth	0002_alter_permission_name_max_length	2017-06-12 10:32:53.70354+00
5	auth	0003_alter_user_email_max_length	2017-06-12 10:32:53.712549+00
6	auth	0004_alter_user_username_opts	2017-06-12 10:32:53.721608+00
7	auth	0005_alter_user_last_login_null	2017-06-12 10:32:53.730741+00
8	auth	0006_require_contenttypes_0002	2017-06-12 10:32:53.732151+00
9	openam_auth	0001_initial	2017-06-12 10:32:53.767869+00
10	admin	0001_initial	2017-06-12 10:32:53.791106+00
11	ctc	0001_initial	2017-06-12 10:32:54.000243+00
12	ctc	0002_auto_20170522_1342	2017-06-12 10:32:54.281138+00
13	integration	0001_initial	2017-06-12 10:32:54.292727+00
14	sessions	0001_initial	2017-06-12 10:32:54.307125+00
15	integration	0002_changelog_target_trustcircle	2017-10-03 14:43:47.409385+00
16	integration	0003_remove_changelog_model_data	2017-10-03 14:43:47.425412+00
\.


--
-- Name: django_migrations_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('django_migrations_id_seq', 16, true);


--
-- Data for Name: django_session; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY django_session (session_key, session_data, expire_date) FROM stdin;
k6gvhem8q880wml9hncu3alesloa458r	OTM2ODcwZTEzYzMzOGU3YWYxZTI3NTU5MGYzMWYxMDdkM2U3YWQzNjp7Il9hdXRoX3VzZXJfaGFzaCI6IjY0ZTdmYTVlYWM4NWJmYzkzZjQyZmU5ZTVkZGNkNDZhMDgyNjc4NWMiLCJfYXV0aF91c2VyX2JhY2tlbmQiOiJkamFuZ28uY29udHJpYi5hdXRoLmJhY2tlbmRzLk1vZGVsQmFja2VuZCIsIl9hdXRoX3VzZXJfaWQiOiIxIn0=	2017-06-26 10:37:41.188741+00
74wb4qvc5vdj8us3q67svxmfmwl1ky0b	OTM2ODcwZTEzYzMzOGU3YWYxZTI3NTU5MGYzMWYxMDdkM2U3YWQzNjp7Il9hdXRoX3VzZXJfaGFzaCI6IjY0ZTdmYTVlYWM4NWJmYzkzZjQyZmU5ZTVkZGNkNDZhMDgyNjc4NWMiLCJfYXV0aF91c2VyX2JhY2tlbmQiOiJkamFuZ28uY29udHJpYi5hdXRoLmJhY2tlbmRzLk1vZGVsQmFja2VuZCIsIl9hdXRoX3VzZXJfaWQiOiIxIn0=	2017-07-09 15:31:50.438368+00
p3h5i9l0kye9wryq3bq5axomwqc8ha2a	YTQxNTRkYWFmMThjMTQ1MTQ0NGQ4M2E2M2ZkOThmZGJjNzhlZDAxNzp7Il9hdXRoX3VzZXJfaGFzaCI6ImQwMTRiYTVkMTE4NmY1YzQwZmQyN2M0MGM4NTEyMTU4MjBhNzM3MjUiLCJfYXV0aF91c2VyX2JhY2tlbmQiOiJkamFuZ28uY29udHJpYi5hdXRoLmJhY2tlbmRzLlJlbW90ZVVzZXJCYWNrZW5kIiwiX2F1dGhfdXNlcl9pZCI6IjMifQ==	2017-10-17 15:00:10.362272+00
u2o67j52f3z2dqy18f989w7edi7i3i2m	YTQxNTRkYWFmMThjMTQ1MTQ0NGQ4M2E2M2ZkOThmZGJjNzhlZDAxNzp7Il9hdXRoX3VzZXJfaGFzaCI6ImQwMTRiYTVkMTE4NmY1YzQwZmQyN2M0MGM4NTEyMTU4MjBhNzM3MjUiLCJfYXV0aF91c2VyX2JhY2tlbmQiOiJkamFuZ28uY29udHJpYi5hdXRoLmJhY2tlbmRzLlJlbW90ZVVzZXJCYWNrZW5kIiwiX2F1dGhfdXNlcl9pZCI6IjMifQ==	2017-10-17 15:15:29.085577+00
acjle4r955w2wmdn5uxnrzz4rf909wot	MDgzMGI0N2EwYjE4YTNjMGM5MGJkZmM4Nzk5MTc2Njg2OTBmMzUzYzp7Il9hdXRoX3VzZXJfaGFzaCI6ImQwMTRiYTVkMTE4NmY1YzQwZmQyN2M0MGM4NTEyMTU4MjBhNzM3MjUiLCJfYXV0aF91c2VyX2JhY2tlbmQiOiJkamFuZ28uY29udHJpYi5hdXRoLmJhY2tlbmRzLlJlbW90ZVVzZXJCYWNrZW5kIiwiX2F1dGhfdXNlcl9pZCI6IjQifQ==	2017-10-17 15:34:15.2845+00
\.


--
-- Data for Name: integration_changelog; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY integration_changelog (id, created, action, model_name, model_pk, to_share, target_trustcircle) FROM stdin;
\.


--
-- Name: integration_changelog_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('integration_changelog_id_seq', 39, true);


--
-- Data for Name: openam_auth_user; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY openam_auth_user (id, password, last_login, is_superuser, username, first_name, last_name, email, is_staff, is_active, date_joined) FROM stdin;
2	pbkdf2_sha256$20000$R9BhmE9iNnXX$S0/yQzNvGUrYyczcq0BtCWctKLC2k2lJWfz0SFjh3Fs=	\N	f	user				f	t	2017-03-27 15:47:25+00
1	pbkdf2_sha256$20000$zlulgJe7Q5wJ$pDbPlwrXkwKQ1lBXqtxWdXoomkOta3ech0twWZyeN30=	2017-06-25 15:31:50.43648+00	t	admin				t	t	2017-03-27 15:41:16.352+00
3		2017-10-03 15:15:29.079609+00	f	demo				f	t	2017-10-03 15:00:09.288816+00
4		2017-10-03 15:34:15.279477+00	f	kyr				f	t	2017-10-03 15:34:15.265338+00
\.


--
-- Data for Name: openam_auth_user_groups; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY openam_auth_user_groups (id, user_id, group_id) FROM stdin;
\.


--
-- Name: openam_auth_user_groups_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('openam_auth_user_groups_id_seq', 1, false);


--
-- Name: openam_auth_user_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('openam_auth_user_id_seq', 4, true);


--
-- Data for Name: openam_auth_user_user_permissions; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY openam_auth_user_user_permissions (id, user_id, permission_id) FROM stdin;
1	2	34
\.


--
-- Name: openam_auth_user_user_permissions_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('openam_auth_user_user_permissions_id_seq', 1, true);


--
-- Name: auth_group auth_group_name_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY auth_group
    ADD CONSTRAINT auth_group_name_key UNIQUE (name);


--
-- Name: auth_group_permissions auth_group_permissions_group_id_permission_id_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY auth_group_permissions
    ADD CONSTRAINT auth_group_permissions_group_id_permission_id_key UNIQUE (group_id, permission_id);


--
-- Name: auth_group_permissions auth_group_permissions_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY auth_group_permissions
    ADD CONSTRAINT auth_group_permissions_pkey PRIMARY KEY (id);


--
-- Name: auth_group auth_group_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY auth_group
    ADD CONSTRAINT auth_group_pkey PRIMARY KEY (id);


--
-- Name: auth_permission auth_permission_content_type_id_codename_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY auth_permission
    ADD CONSTRAINT auth_permission_content_type_id_codename_key UNIQUE (content_type_id, codename);


--
-- Name: auth_permission auth_permission_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY auth_permission
    ADD CONSTRAINT auth_permission_pkey PRIMARY KEY (id);


--
-- Name: ctc_country ctc_country_name_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ctc_country
    ADD CONSTRAINT ctc_country_name_key UNIQUE (name);


--
-- Name: ctc_country ctc_country_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ctc_country
    ADD CONSTRAINT ctc_country_pkey PRIMARY KEY (id);


--
-- Name: ctc_historicalcountry ctc_historicalcountry_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ctc_historicalcountry
    ADD CONSTRAINT ctc_historicalcountry_pkey PRIMARY KEY (history_id);


--
-- Name: ctc_historicalsector ctc_historicalsector_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ctc_historicalsector
    ADD CONSTRAINT ctc_historicalsector_pkey PRIMARY KEY (history_id);


--
-- Name: ctc_historicalteam ctc_historicalteam_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ctc_historicalteam
    ADD CONSTRAINT ctc_historicalteam_pkey PRIMARY KEY (history_id);


--
-- Name: ctc_historicalteamstatus ctc_historicalteamstatus_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ctc_historicalteamstatus
    ADD CONSTRAINT ctc_historicalteamstatus_pkey PRIMARY KEY (history_id);


--
-- Name: ctc_historicalteamtype ctc_historicalteamtype_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ctc_historicalteamtype
    ADD CONSTRAINT ctc_historicalteamtype_pkey PRIMARY KEY (history_id);


--
-- Name: ctc_historicaltrustcircle ctc_historicaltrustcircle_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ctc_historicaltrustcircle
    ADD CONSTRAINT ctc_historicaltrustcircle_pkey PRIMARY KEY (history_id);


--
-- Name: ctc_sector ctc_sector_name_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ctc_sector
    ADD CONSTRAINT ctc_sector_name_key UNIQUE (name);


--
-- Name: ctc_sector ctc_sector_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ctc_sector
    ADD CONSTRAINT ctc_sector_pkey PRIMARY KEY (id);


--
-- Name: ctc_team_additional_countries ctc_team_additional_countries_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ctc_team_additional_countries
    ADD CONSTRAINT ctc_team_additional_countries_pkey PRIMARY KEY (id);


--
-- Name: ctc_team_additional_countries ctc_team_additional_countries_team_id_country_id_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ctc_team_additional_countries
    ADD CONSTRAINT ctc_team_additional_countries_team_id_country_id_key UNIQUE (team_id, country_id);


--
-- Name: ctc_team ctc_team_country_id_1b8ebc5fa22e5eb6_uniq; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ctc_team
    ADD CONSTRAINT ctc_team_country_id_1b8ebc5fa22e5eb6_uniq UNIQUE (country_id, short_name);


--
-- Name: ctc_team_nis_sectors ctc_team_nis_sectors_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ctc_team_nis_sectors
    ADD CONSTRAINT ctc_team_nis_sectors_pkey PRIMARY KEY (id);


--
-- Name: ctc_team_nis_sectors ctc_team_nis_sectors_team_id_sector_id_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ctc_team_nis_sectors
    ADD CONSTRAINT ctc_team_nis_sectors_team_id_sector_id_key UNIQUE (team_id, sector_id);


--
-- Name: ctc_team_nis_team_types ctc_team_nis_team_types_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ctc_team_nis_team_types
    ADD CONSTRAINT ctc_team_nis_team_types_pkey PRIMARY KEY (id);


--
-- Name: ctc_team_nis_team_types ctc_team_nis_team_types_team_id_teamtype_id_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ctc_team_nis_team_types
    ADD CONSTRAINT ctc_team_nis_team_types_team_id_teamtype_id_key UNIQUE (team_id, teamtype_id);


--
-- Name: ctc_team ctc_team_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ctc_team
    ADD CONSTRAINT ctc_team_pkey PRIMARY KEY (id);


--
-- Name: ctc_teamstatus ctc_teamstatus_name_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ctc_teamstatus
    ADD CONSTRAINT ctc_teamstatus_name_key UNIQUE (name);


--
-- Name: ctc_teamstatus ctc_teamstatus_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ctc_teamstatus
    ADD CONSTRAINT ctc_teamstatus_pkey PRIMARY KEY (id);


--
-- Name: ctc_teamtype ctc_teamtype_name_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ctc_teamtype
    ADD CONSTRAINT ctc_teamtype_name_key UNIQUE (name);


--
-- Name: ctc_teamtype ctc_teamtype_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ctc_teamtype
    ADD CONSTRAINT ctc_teamtype_pkey PRIMARY KEY (id);


--
-- Name: ctc_trustcircle ctc_trustcircle_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ctc_trustcircle
    ADD CONSTRAINT ctc_trustcircle_pkey PRIMARY KEY (id);


--
-- Name: ctc_trustcircle ctc_trustcircle_short_name_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ctc_trustcircle
    ADD CONSTRAINT ctc_trustcircle_short_name_key UNIQUE (short_name);


--
-- Name: ctc_trustcircle_teams ctc_trustcircle_teams_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ctc_trustcircle_teams
    ADD CONSTRAINT ctc_trustcircle_teams_pkey PRIMARY KEY (id);


--
-- Name: ctc_trustcircle_teams ctc_trustcircle_teams_trustcircle_id_team_id_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ctc_trustcircle_teams
    ADD CONSTRAINT ctc_trustcircle_teams_trustcircle_id_team_id_key UNIQUE (trustcircle_id, team_id);


--
-- Name: django_admin_log django_admin_log_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY django_admin_log
    ADD CONSTRAINT django_admin_log_pkey PRIMARY KEY (id);


--
-- Name: django_content_type django_content_type_app_label_45f3b1d93ec8c61c_uniq; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY django_content_type
    ADD CONSTRAINT django_content_type_app_label_45f3b1d93ec8c61c_uniq UNIQUE (app_label, model);


--
-- Name: django_content_type django_content_type_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY django_content_type
    ADD CONSTRAINT django_content_type_pkey PRIMARY KEY (id);


--
-- Name: django_migrations django_migrations_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY django_migrations
    ADD CONSTRAINT django_migrations_pkey PRIMARY KEY (id);


--
-- Name: django_session django_session_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY django_session
    ADD CONSTRAINT django_session_pkey PRIMARY KEY (session_key);


--
-- Name: integration_changelog integration_changelog_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY integration_changelog
    ADD CONSTRAINT integration_changelog_pkey PRIMARY KEY (id);


--
-- Name: openam_auth_user_groups openam_auth_user_groups_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY openam_auth_user_groups
    ADD CONSTRAINT openam_auth_user_groups_pkey PRIMARY KEY (id);


--
-- Name: openam_auth_user_groups openam_auth_user_groups_user_id_group_id_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY openam_auth_user_groups
    ADD CONSTRAINT openam_auth_user_groups_user_id_group_id_key UNIQUE (user_id, group_id);


--
-- Name: openam_auth_user openam_auth_user_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY openam_auth_user
    ADD CONSTRAINT openam_auth_user_pkey PRIMARY KEY (id);


--
-- Name: openam_auth_user_user_permissions openam_auth_user_user_permissions_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY openam_auth_user_user_permissions
    ADD CONSTRAINT openam_auth_user_user_permissions_pkey PRIMARY KEY (id);


--
-- Name: openam_auth_user_user_permissions openam_auth_user_user_permissions_user_id_permission_id_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY openam_auth_user_user_permissions
    ADD CONSTRAINT openam_auth_user_user_permissions_user_id_permission_id_key UNIQUE (user_id, permission_id);


--
-- Name: openam_auth_user openam_auth_user_username_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY openam_auth_user
    ADD CONSTRAINT openam_auth_user_username_key UNIQUE (username);


--
-- Name: auth_group_name_253ae2a6331666e8_like; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX auth_group_name_253ae2a6331666e8_like ON auth_group USING btree (name varchar_pattern_ops);


--
-- Name: auth_group_permissions_0e939a4f; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX auth_group_permissions_0e939a4f ON auth_group_permissions USING btree (group_id);


--
-- Name: auth_group_permissions_8373b171; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX auth_group_permissions_8373b171 ON auth_group_permissions USING btree (permission_id);


--
-- Name: auth_permission_417f1b1c; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX auth_permission_417f1b1c ON auth_permission USING btree (content_type_id);


--
-- Name: ctc_country_name_301fb1f6781cd5c8_like; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ctc_country_name_301fb1f6781cd5c8_like ON ctc_country USING btree (name varchar_pattern_ops);


--
-- Name: ctc_historicalcountry_b068931c; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ctc_historicalcountry_b068931c ON ctc_historicalcountry USING btree (name);


--
-- Name: ctc_historicalcountry_b80bb774; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ctc_historicalcountry_b80bb774 ON ctc_historicalcountry USING btree (id);


--
-- Name: ctc_historicalcountry_e7d5ef68; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ctc_historicalcountry_e7d5ef68 ON ctc_historicalcountry USING btree (history_user_id);


--
-- Name: ctc_historicalcountry_name_29ae3f75c8769616_like; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ctc_historicalcountry_name_29ae3f75c8769616_like ON ctc_historicalcountry USING btree (name varchar_pattern_ops);


--
-- Name: ctc_historicalsector_b068931c; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ctc_historicalsector_b068931c ON ctc_historicalsector USING btree (name);


--
-- Name: ctc_historicalsector_b80bb774; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ctc_historicalsector_b80bb774 ON ctc_historicalsector USING btree (id);


--
-- Name: ctc_historicalsector_e7d5ef68; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ctc_historicalsector_e7d5ef68 ON ctc_historicalsector USING btree (history_user_id);


--
-- Name: ctc_historicalsector_name_7ba1ec2fb64ccf41_like; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ctc_historicalsector_name_7ba1ec2fb64ccf41_like ON ctc_historicalsector USING btree (name varchar_pattern_ops);


--
-- Name: ctc_historicalteam_93bfec8a; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ctc_historicalteam_93bfec8a ON ctc_historicalteam USING btree (country_id);


--
-- Name: ctc_historicalteam_b80bb774; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ctc_historicalteam_b80bb774 ON ctc_historicalteam USING btree (id);


--
-- Name: ctc_historicalteam_dc91ed4b; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ctc_historicalteam_dc91ed4b ON ctc_historicalteam USING btree (status_id);


--
-- Name: ctc_historicalteam_e7d5ef68; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ctc_historicalteam_e7d5ef68 ON ctc_historicalteam USING btree (history_user_id);


--
-- Name: ctc_historicalteamstatus_b068931c; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ctc_historicalteamstatus_b068931c ON ctc_historicalteamstatus USING btree (name);


--
-- Name: ctc_historicalteamstatus_b80bb774; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ctc_historicalteamstatus_b80bb774 ON ctc_historicalteamstatus USING btree (id);


--
-- Name: ctc_historicalteamstatus_e7d5ef68; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ctc_historicalteamstatus_e7d5ef68 ON ctc_historicalteamstatus USING btree (history_user_id);


--
-- Name: ctc_historicalteamstatus_name_255d1464687883a2_like; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ctc_historicalteamstatus_name_255d1464687883a2_like ON ctc_historicalteamstatus USING btree (name varchar_pattern_ops);


--
-- Name: ctc_historicalteamtype_b068931c; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ctc_historicalteamtype_b068931c ON ctc_historicalteamtype USING btree (name);


--
-- Name: ctc_historicalteamtype_b80bb774; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ctc_historicalteamtype_b80bb774 ON ctc_historicalteamtype USING btree (id);


--
-- Name: ctc_historicalteamtype_e7d5ef68; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ctc_historicalteamtype_e7d5ef68 ON ctc_historicalteamtype USING btree (history_user_id);


--
-- Name: ctc_historicalteamtype_name_7db17f73f7b9cb3e_like; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ctc_historicalteamtype_name_7db17f73f7b9cb3e_like ON ctc_historicalteamtype USING btree (name varchar_pattern_ops);


--
-- Name: ctc_historicaltrustcircle_4698bac7; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ctc_historicaltrustcircle_4698bac7 ON ctc_historicaltrustcircle USING btree (short_name);


--
-- Name: ctc_historicaltrustcircle_b80bb774; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ctc_historicaltrustcircle_b80bb774 ON ctc_historicaltrustcircle USING btree (id);


--
-- Name: ctc_historicaltrustcircle_e7d5ef68; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ctc_historicaltrustcircle_e7d5ef68 ON ctc_historicaltrustcircle USING btree (history_user_id);


--
-- Name: ctc_historicaltrustcircle_short_name_62a068a5f1c12f75_like; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ctc_historicaltrustcircle_short_name_62a068a5f1c12f75_like ON ctc_historicaltrustcircle USING btree (short_name varchar_pattern_ops);


--
-- Name: ctc_sector_name_5bd2d43b8c6693f1_like; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ctc_sector_name_5bd2d43b8c6693f1_like ON ctc_sector USING btree (name varchar_pattern_ops);


--
-- Name: ctc_team_93bfec8a; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ctc_team_93bfec8a ON ctc_team USING btree (country_id);


--
-- Name: ctc_team_additional_countries_93bfec8a; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ctc_team_additional_countries_93bfec8a ON ctc_team_additional_countries USING btree (country_id);


--
-- Name: ctc_team_additional_countries_f6a7ca40; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ctc_team_additional_countries_f6a7ca40 ON ctc_team_additional_countries USING btree (team_id);


--
-- Name: ctc_team_dc91ed4b; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ctc_team_dc91ed4b ON ctc_team USING btree (status_id);


--
-- Name: ctc_team_nis_sectors_5b1d2adf; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ctc_team_nis_sectors_5b1d2adf ON ctc_team_nis_sectors USING btree (sector_id);


--
-- Name: ctc_team_nis_sectors_f6a7ca40; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ctc_team_nis_sectors_f6a7ca40 ON ctc_team_nis_sectors USING btree (team_id);


--
-- Name: ctc_team_nis_team_types_8d7d5c21; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ctc_team_nis_team_types_8d7d5c21 ON ctc_team_nis_team_types USING btree (teamtype_id);


--
-- Name: ctc_team_nis_team_types_f6a7ca40; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ctc_team_nis_team_types_f6a7ca40 ON ctc_team_nis_team_types USING btree (team_id);


--
-- Name: ctc_teamstatus_name_1076928e0ba8c834_like; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ctc_teamstatus_name_1076928e0ba8c834_like ON ctc_teamstatus USING btree (name varchar_pattern_ops);


--
-- Name: ctc_teamtype_name_9f97825b589b200_like; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ctc_teamtype_name_9f97825b589b200_like ON ctc_teamtype USING btree (name varchar_pattern_ops);


--
-- Name: ctc_trustcircle_short_name_274988fe4a3e5121_like; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ctc_trustcircle_short_name_274988fe4a3e5121_like ON ctc_trustcircle USING btree (short_name varchar_pattern_ops);


--
-- Name: ctc_trustcircle_teams_a6ab87a3; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ctc_trustcircle_teams_a6ab87a3 ON ctc_trustcircle_teams USING btree (trustcircle_id);


--
-- Name: ctc_trustcircle_teams_f6a7ca40; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ctc_trustcircle_teams_f6a7ca40 ON ctc_trustcircle_teams USING btree (team_id);


--
-- Name: django_admin_log_417f1b1c; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX django_admin_log_417f1b1c ON django_admin_log USING btree (content_type_id);


--
-- Name: django_admin_log_e8701ad4; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX django_admin_log_e8701ad4 ON django_admin_log USING btree (user_id);


--
-- Name: django_session_de54fa62; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX django_session_de54fa62 ON django_session USING btree (expire_date);


--
-- Name: django_session_session_key_461cfeaa630ca218_like; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX django_session_session_key_461cfeaa630ca218_like ON django_session USING btree (session_key varchar_pattern_ops);


--
-- Name: openam_auth_user_groups_0e939a4f; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX openam_auth_user_groups_0e939a4f ON openam_auth_user_groups USING btree (group_id);


--
-- Name: openam_auth_user_groups_e8701ad4; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX openam_auth_user_groups_e8701ad4 ON openam_auth_user_groups USING btree (user_id);


--
-- Name: openam_auth_user_user_permissions_8373b171; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX openam_auth_user_user_permissions_8373b171 ON openam_auth_user_user_permissions USING btree (permission_id);


--
-- Name: openam_auth_user_user_permissions_e8701ad4; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX openam_auth_user_user_permissions_e8701ad4 ON openam_auth_user_user_permissions USING btree (user_id);


--
-- Name: openam_auth_user_username_38856b75b52b8394_like; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX openam_auth_user_username_38856b75b52b8394_like ON openam_auth_user USING btree (username varchar_pattern_ops);


--
-- Name: auth_permission auth_content_type_id_508cf46651277a81_fk_django_content_type_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY auth_permission
    ADD CONSTRAINT auth_content_type_id_508cf46651277a81_fk_django_content_type_id FOREIGN KEY (content_type_id) REFERENCES django_content_type(id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: auth_group_permissions auth_group_permissio_group_id_689710a9a73b7457_fk_auth_group_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY auth_group_permissions
    ADD CONSTRAINT auth_group_permissio_group_id_689710a9a73b7457_fk_auth_group_id FOREIGN KEY (group_id) REFERENCES auth_group(id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: auth_group_permissions auth_group_permission_id_1f49ccbbdc69d2fc_fk_auth_permission_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY auth_group_permissions
    ADD CONSTRAINT auth_group_permission_id_1f49ccbbdc69d2fc_fk_auth_permission_id FOREIGN KEY (permission_id) REFERENCES auth_permission(id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: ctc_historicaltrustcircle ctc_his_history_user_id_3b3efc6fff831126_fk_openam_auth_user_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ctc_historicaltrustcircle
    ADD CONSTRAINT ctc_his_history_user_id_3b3efc6fff831126_fk_openam_auth_user_id FOREIGN KEY (history_user_id) REFERENCES openam_auth_user(id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: ctc_historicalteamstatus ctc_his_history_user_id_44e907d2f1f1c63a_fk_openam_auth_user_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ctc_historicalteamstatus
    ADD CONSTRAINT ctc_his_history_user_id_44e907d2f1f1c63a_fk_openam_auth_user_id FOREIGN KEY (history_user_id) REFERENCES openam_auth_user(id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: ctc_historicalteam ctc_his_history_user_id_535651082ae51f7a_fk_openam_auth_user_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ctc_historicalteam
    ADD CONSTRAINT ctc_his_history_user_id_535651082ae51f7a_fk_openam_auth_user_id FOREIGN KEY (history_user_id) REFERENCES openam_auth_user(id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: ctc_historicalcountry ctc_his_history_user_id_5bca0bba406fd8ae_fk_openam_auth_user_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ctc_historicalcountry
    ADD CONSTRAINT ctc_his_history_user_id_5bca0bba406fd8ae_fk_openam_auth_user_id FOREIGN KEY (history_user_id) REFERENCES openam_auth_user(id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: ctc_historicalsector ctc_his_history_user_id_65648b2b91c7cf83_fk_openam_auth_user_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ctc_historicalsector
    ADD CONSTRAINT ctc_his_history_user_id_65648b2b91c7cf83_fk_openam_auth_user_id FOREIGN KEY (history_user_id) REFERENCES openam_auth_user(id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: ctc_historicalteamtype ctc_his_history_user_id_77223a254bff901a_fk_openam_auth_user_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ctc_historicalteamtype
    ADD CONSTRAINT ctc_his_history_user_id_77223a254bff901a_fk_openam_auth_user_id FOREIGN KEY (history_user_id) REFERENCES openam_auth_user(id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: ctc_team_additional_countries ctc_team_addition_country_id_1b567f22352153c9_fk_ctc_country_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ctc_team_additional_countries
    ADD CONSTRAINT ctc_team_addition_country_id_1b567f22352153c9_fk_ctc_country_id FOREIGN KEY (country_id) REFERENCES ctc_country(id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: ctc_team_additional_countries ctc_team_additional_coun_team_id_39dfc8e807345b3_fk_ctc_team_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ctc_team_additional_countries
    ADD CONSTRAINT ctc_team_additional_coun_team_id_39dfc8e807345b3_fk_ctc_team_id FOREIGN KEY (team_id) REFERENCES ctc_team(id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: ctc_team ctc_team_country_id_1f93c8e4f154eb0f_fk_ctc_country_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ctc_team
    ADD CONSTRAINT ctc_team_country_id_1f93c8e4f154eb0f_fk_ctc_country_id FOREIGN KEY (country_id) REFERENCES ctc_country(id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: ctc_team_nis_sectors ctc_team_nis_sectors_sector_id_1c84ecb4cbae9ef_fk_ctc_sector_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ctc_team_nis_sectors
    ADD CONSTRAINT ctc_team_nis_sectors_sector_id_1c84ecb4cbae9ef_fk_ctc_sector_id FOREIGN KEY (sector_id) REFERENCES ctc_sector(id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: ctc_team_nis_sectors ctc_team_nis_sectors_team_id_cbefac5024de78a_fk_ctc_team_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ctc_team_nis_sectors
    ADD CONSTRAINT ctc_team_nis_sectors_team_id_cbefac5024de78a_fk_ctc_team_id FOREIGN KEY (team_id) REFERENCES ctc_team(id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: ctc_team_nis_team_types ctc_team_nis_tea_teamtype_id_6e701146cd89601_fk_ctc_teamtype_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ctc_team_nis_team_types
    ADD CONSTRAINT ctc_team_nis_tea_teamtype_id_6e701146cd89601_fk_ctc_teamtype_id FOREIGN KEY (teamtype_id) REFERENCES ctc_teamtype(id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: ctc_team_nis_team_types ctc_team_nis_team_types_team_id_6304087abaac9c03_fk_ctc_team_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ctc_team_nis_team_types
    ADD CONSTRAINT ctc_team_nis_team_types_team_id_6304087abaac9c03_fk_ctc_team_id FOREIGN KEY (team_id) REFERENCES ctc_team(id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: ctc_team ctc_team_status_id_2d98e482a8b9f0ee_fk_ctc_teamstatus_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ctc_team
    ADD CONSTRAINT ctc_team_status_id_2d98e482a8b9f0ee_fk_ctc_teamstatus_id FOREIGN KEY (status_id) REFERENCES ctc_teamstatus(id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: ctc_trustcircle_teams ctc_trust_trustcircle_id_59be5e71e2ac6a92_fk_ctc_trustcircle_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ctc_trustcircle_teams
    ADD CONSTRAINT ctc_trust_trustcircle_id_59be5e71e2ac6a92_fk_ctc_trustcircle_id FOREIGN KEY (trustcircle_id) REFERENCES ctc_trustcircle(id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: ctc_trustcircle_teams ctc_trustcircle_teams_team_id_53590091c1192758_fk_ctc_team_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ctc_trustcircle_teams
    ADD CONSTRAINT ctc_trustcircle_teams_team_id_53590091c1192758_fk_ctc_team_id FOREIGN KEY (team_id) REFERENCES ctc_team(id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: django_admin_log djan_content_type_id_697914295151027a_fk_django_content_type_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY django_admin_log
    ADD CONSTRAINT djan_content_type_id_697914295151027a_fk_django_content_type_id FOREIGN KEY (content_type_id) REFERENCES django_content_type(id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: django_admin_log django_admin_lo_user_id_52fdd58701c5f563_fk_openam_auth_user_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY django_admin_log
    ADD CONSTRAINT django_admin_lo_user_id_52fdd58701c5f563_fk_openam_auth_user_id FOREIGN KEY (user_id) REFERENCES openam_auth_user(id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: openam_auth_user_user_permissions openam_aut_permission_id_16c6cc47575fed96_fk_auth_permission_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY openam_auth_user_user_permissions
    ADD CONSTRAINT openam_aut_permission_id_16c6cc47575fed96_fk_auth_permission_id FOREIGN KEY (permission_id) REFERENCES auth_permission(id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: openam_auth_user_groups openam_auth_use_user_id_23caa9f15f60167a_fk_openam_auth_user_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY openam_auth_user_groups
    ADD CONSTRAINT openam_auth_use_user_id_23caa9f15f60167a_fk_openam_auth_user_id FOREIGN KEY (user_id) REFERENCES openam_auth_user(id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: openam_auth_user_user_permissions openam_auth_use_user_id_3034d56a09674d30_fk_openam_auth_user_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY openam_auth_user_user_permissions
    ADD CONSTRAINT openam_auth_use_user_id_3034d56a09674d30_fk_openam_auth_user_id FOREIGN KEY (user_id) REFERENCES openam_auth_user(id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: openam_auth_user_groups openam_auth_user_gro_group_id_3a222c968ad40a41_fk_auth_group_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY openam_auth_user_groups
    ADD CONSTRAINT openam_auth_user_gro_group_id_3a222c968ad40a41_fk_auth_group_id FOREIGN KEY (group_id) REFERENCES auth_group(id) DEFERRABLE INITIALLY DEFERRED;


--
-- PostgreSQL database dump complete
--

