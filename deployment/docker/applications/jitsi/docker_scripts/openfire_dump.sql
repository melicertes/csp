--
-- PostgreSQL database dump
--

-- Dumped from database version 9.5.7
-- Dumped by pg_dump version 9.5.7

SET statement_timeout = 0;
SET lock_timeout = 0;
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
-- Name: ofextcomponentconf; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ofextcomponentconf (
    subdomain character varying(255) NOT NULL,
    wildcard integer NOT NULL,
    secret character varying(255),
    permission character varying(10) NOT NULL
);


ALTER TABLE ofextcomponentconf OWNER TO postgres;

--
-- Name: ofgroup; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ofgroup (
    groupname character varying(50) NOT NULL,
    description character varying(255)
);


ALTER TABLE ofgroup OWNER TO postgres;

--
-- Name: ofgroupprop; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ofgroupprop (
    groupname character varying(50) NOT NULL,
    name character varying(100) NOT NULL,
    propvalue text NOT NULL
);


ALTER TABLE ofgroupprop OWNER TO postgres;

--
-- Name: ofgroupuser; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ofgroupuser (
    groupname character varying(50) NOT NULL,
    username character varying(100) NOT NULL,
    administrator integer NOT NULL
);


ALTER TABLE ofgroupuser OWNER TO postgres;

--
-- Name: ofid; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ofid (
    idtype integer NOT NULL,
    id integer NOT NULL
);


ALTER TABLE ofid OWNER TO postgres;

--
-- Name: ofmucaffiliation; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ofmucaffiliation (
    roomid integer NOT NULL,
    jid character varying(1024) NOT NULL,
    affiliation integer NOT NULL
);


ALTER TABLE ofmucaffiliation OWNER TO postgres;

--
-- Name: ofmucconversationlog; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ofmucconversationlog (
    roomid integer NOT NULL,
    messageid integer NOT NULL,
    sender character varying(1024) NOT NULL,
    nickname character varying(255),
    logtime character(15) NOT NULL,
    subject character varying(255),
    body text,
    stanza text
);


ALTER TABLE ofmucconversationlog OWNER TO postgres;

--
-- Name: ofmucmember; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ofmucmember (
    roomid integer NOT NULL,
    jid character varying(1024) NOT NULL,
    nickname character varying(255),
    firstname character varying(100),
    lastname character varying(100),
    url character varying(100),
    email character varying(100),
    faqentry character varying(100)
);


ALTER TABLE ofmucmember OWNER TO postgres;

--
-- Name: ofmucroom; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ofmucroom (
    serviceid integer NOT NULL,
    roomid integer NOT NULL,
    creationdate character(15) NOT NULL,
    modificationdate character(15) NOT NULL,
    name character varying(50) NOT NULL,
    naturalname character varying(255) NOT NULL,
    description character varying(255),
    lockeddate character(15) NOT NULL,
    emptydate character(15),
    canchangesubject integer NOT NULL,
    maxusers integer NOT NULL,
    publicroom integer NOT NULL,
    moderated integer NOT NULL,
    membersonly integer NOT NULL,
    caninvite integer NOT NULL,
    roompassword character varying(50),
    candiscoverjid integer NOT NULL,
    logenabled integer NOT NULL,
    subject character varying(100),
    rolestobroadcast integer NOT NULL,
    usereservednick integer NOT NULL,
    canchangenick integer NOT NULL,
    canregister integer NOT NULL,
    allowpm integer
);


ALTER TABLE ofmucroom OWNER TO postgres;

--
-- Name: ofmucroomprop; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ofmucroomprop (
    roomid integer NOT NULL,
    name character varying(100) NOT NULL,
    propvalue text NOT NULL
);


ALTER TABLE ofmucroomprop OWNER TO postgres;

--
-- Name: ofmucservice; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ofmucservice (
    serviceid integer NOT NULL,
    subdomain character varying(255) NOT NULL,
    description character varying(255),
    ishidden integer NOT NULL
);


ALTER TABLE ofmucservice OWNER TO postgres;

--
-- Name: ofmucserviceprop; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ofmucserviceprop (
    serviceid integer NOT NULL,
    name character varying(100) NOT NULL,
    propvalue text NOT NULL
);


ALTER TABLE ofmucserviceprop OWNER TO postgres;

--
-- Name: ofoffline; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ofoffline (
    username character varying(64) NOT NULL,
    messageid integer NOT NULL,
    creationdate character(15) NOT NULL,
    messagesize integer NOT NULL,
    stanza text NOT NULL
);


ALTER TABLE ofoffline OWNER TO postgres;

--
-- Name: ofpresence; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ofpresence (
    username character varying(64) NOT NULL,
    offlinepresence text,
    offlinedate character varying(15) NOT NULL
);


ALTER TABLE ofpresence OWNER TO postgres;

--
-- Name: ofprivacylist; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ofprivacylist (
    username character varying(64) NOT NULL,
    name character varying(100) NOT NULL,
    isdefault integer NOT NULL,
    list text NOT NULL
);


ALTER TABLE ofprivacylist OWNER TO postgres;

--
-- Name: ofprivate; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ofprivate (
    username character varying(64) NOT NULL,
    name character varying(100) NOT NULL,
    namespace character varying(200) NOT NULL,
    privatedata text NOT NULL
);


ALTER TABLE ofprivate OWNER TO postgres;

--
-- Name: ofproperty; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ofproperty (
    name character varying(100) NOT NULL,
    propvalue character varying(4000) NOT NULL
);


ALTER TABLE ofproperty OWNER TO postgres;

--
-- Name: ofpubsubaffiliation; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ofpubsubaffiliation (
    serviceid character varying(100) NOT NULL,
    nodeid character varying(100) NOT NULL,
    jid character varying(1024) NOT NULL,
    affiliation character varying(10) NOT NULL
);


ALTER TABLE ofpubsubaffiliation OWNER TO postgres;

--
-- Name: ofpubsubdefaultconf; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ofpubsubdefaultconf (
    serviceid character varying(100) NOT NULL,
    leaf integer NOT NULL,
    deliverpayloads integer NOT NULL,
    maxpayloadsize integer NOT NULL,
    persistitems integer NOT NULL,
    maxitems integer NOT NULL,
    notifyconfigchanges integer NOT NULL,
    notifydelete integer NOT NULL,
    notifyretract integer NOT NULL,
    presencebased integer NOT NULL,
    senditemsubscribe integer NOT NULL,
    publishermodel character varying(15) NOT NULL,
    subscriptionenabled integer NOT NULL,
    accessmodel character varying(10) NOT NULL,
    language character varying(255),
    replypolicy character varying(15),
    associationpolicy character varying(15) NOT NULL,
    maxleafnodes integer NOT NULL
);


ALTER TABLE ofpubsubdefaultconf OWNER TO postgres;

--
-- Name: ofpubsubitem; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ofpubsubitem (
    serviceid character varying(100) NOT NULL,
    nodeid character varying(100) NOT NULL,
    id character varying(100) NOT NULL,
    jid character varying(1024) NOT NULL,
    creationdate character(15) NOT NULL,
    payload text
);


ALTER TABLE ofpubsubitem OWNER TO postgres;

--
-- Name: ofpubsubnode; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ofpubsubnode (
    serviceid character varying(100) NOT NULL,
    nodeid character varying(100) NOT NULL,
    leaf integer NOT NULL,
    creationdate character(15) NOT NULL,
    modificationdate character(15) NOT NULL,
    parent character varying(100),
    deliverpayloads integer NOT NULL,
    maxpayloadsize integer,
    persistitems integer,
    maxitems integer,
    notifyconfigchanges integer NOT NULL,
    notifydelete integer NOT NULL,
    notifyretract integer NOT NULL,
    presencebased integer NOT NULL,
    senditemsubscribe integer NOT NULL,
    publishermodel character varying(15) NOT NULL,
    subscriptionenabled integer NOT NULL,
    configsubscription integer NOT NULL,
    accessmodel character varying(10) NOT NULL,
    payloadtype character varying(100),
    bodyxslt character varying(100),
    dataformxslt character varying(100),
    creator character varying(1024) NOT NULL,
    description character varying(255),
    language character varying(255),
    name character varying(50),
    replypolicy character varying(15),
    associationpolicy character varying(15),
    maxleafnodes integer
);


ALTER TABLE ofpubsubnode OWNER TO postgres;

--
-- Name: ofpubsubnodegroups; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ofpubsubnodegroups (
    serviceid character varying(100) NOT NULL,
    nodeid character varying(100) NOT NULL,
    rostergroup character varying(100) NOT NULL
);


ALTER TABLE ofpubsubnodegroups OWNER TO postgres;

--
-- Name: ofpubsubnodejids; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ofpubsubnodejids (
    serviceid character varying(100) NOT NULL,
    nodeid character varying(100) NOT NULL,
    jid character varying(1024) NOT NULL,
    associationtype character varying(20) NOT NULL
);


ALTER TABLE ofpubsubnodejids OWNER TO postgres;

--
-- Name: ofpubsubsubscription; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ofpubsubsubscription (
    serviceid character varying(100) NOT NULL,
    nodeid character varying(100) NOT NULL,
    id character varying(100) NOT NULL,
    jid character varying(1024) NOT NULL,
    owner character varying(1024) NOT NULL,
    state character varying(15) NOT NULL,
    deliver integer NOT NULL,
    digest integer NOT NULL,
    digest_frequency integer NOT NULL,
    expire character(15),
    includebody integer NOT NULL,
    showvalues character varying(30) NOT NULL,
    subscriptiontype character varying(10) NOT NULL,
    subscriptiondepth integer NOT NULL,
    keyword character varying(200)
);


ALTER TABLE ofpubsubsubscription OWNER TO postgres;

--
-- Name: ofremoteserverconf; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ofremoteserverconf (
    xmppdomain character varying(255) NOT NULL,
    remoteport integer,
    permission character varying(10) NOT NULL
);


ALTER TABLE ofremoteserverconf OWNER TO postgres;

--
-- Name: ofroster; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ofroster (
    rosterid integer NOT NULL,
    username character varying(64) NOT NULL,
    jid character varying(1024) NOT NULL,
    sub integer NOT NULL,
    ask integer NOT NULL,
    recv integer NOT NULL,
    nick character varying(255)
);


ALTER TABLE ofroster OWNER TO postgres;

--
-- Name: ofrostergroups; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ofrostergroups (
    rosterid integer NOT NULL,
    rank integer NOT NULL,
    groupname character varying(255) NOT NULL
);


ALTER TABLE ofrostergroups OWNER TO postgres;

--
-- Name: ofsaslauthorized; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ofsaslauthorized (
    username character varying(64) NOT NULL,
    principal character varying(4000) NOT NULL
);


ALTER TABLE ofsaslauthorized OWNER TO postgres;

--
-- Name: ofsecurityauditlog; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ofsecurityauditlog (
    msgid integer NOT NULL,
    username character varying(64) NOT NULL,
    entrystamp bigint NOT NULL,
    summary character varying(255) NOT NULL,
    node character varying(255) NOT NULL,
    details text
);


ALTER TABLE ofsecurityauditlog OWNER TO postgres;

--
-- Name: ofuser; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ofuser (
    username character varying(64) NOT NULL,
    storedkey character varying(32),
    serverkey character varying(32),
    salt character varying(32),
    iterations integer,
    plainpassword character varying(32),
    encryptedpassword character varying(255),
    name character varying(100),
    email character varying(100),
    creationdate character(15) NOT NULL,
    modificationdate character(15) NOT NULL
);


ALTER TABLE ofuser OWNER TO postgres;

--
-- Name: ofuserflag; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ofuserflag (
    username character varying(64) NOT NULL,
    name character varying(100) NOT NULL,
    starttime character(15),
    endtime character(15)
);


ALTER TABLE ofuserflag OWNER TO postgres;

--
-- Name: ofuserprop; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ofuserprop (
    username character varying(64) NOT NULL,
    name character varying(100) NOT NULL,
    propvalue text NOT NULL
);


ALTER TABLE ofuserprop OWNER TO postgres;

--
-- Name: ofvcard; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ofvcard (
    username character varying(64) NOT NULL,
    vcard text NOT NULL
);


ALTER TABLE ofvcard OWNER TO postgres;

--
-- Name: ofversion; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ofversion (
    name character varying(50) NOT NULL,
    version integer NOT NULL
);


ALTER TABLE ofversion OWNER TO postgres;

--
-- Data for Name: ofextcomponentconf; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY ofextcomponentconf (subdomain, wildcard, secret, permission) FROM stdin;
\.


--
-- Data for Name: ofgroup; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY ofgroup (groupname, description) FROM stdin;
\.


--
-- Data for Name: ofgroupprop; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY ofgroupprop (groupname, name, propvalue) FROM stdin;
\.


--
-- Data for Name: ofgroupuser; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY ofgroupuser (groupname, username, administrator) FROM stdin;
\.


--
-- Data for Name: ofid; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY ofid (idtype, id) FROM stdin;
18	1
23	1
26	2
19	6
25	2
\.


--
-- Data for Name: ofmucaffiliation; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY ofmucaffiliation (roomid, jid, affiliation) FROM stdin;
\.


--
-- Data for Name: ofmucconversationlog; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY ofmucconversationlog (roomid, messageid, sender, nickname, logtime, subject, body, stanza) FROM stdin;
\.


--
-- Data for Name: ofmucmember; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY ofmucmember (roomid, jid, nickname, firstname, lastname, url, email, faqentry) FROM stdin;
\.


--
-- Data for Name: ofmucroom; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY ofmucroom (serviceid, roomid, creationdate, modificationdate, name, naturalname, description, lockeddate, emptydate, canchangesubject, maxusers, publicroom, moderated, membersonly, caninvite, roompassword, candiscoverjid, logenabled, subject, rolestobroadcast, usereservednick, canchangenick, canregister, allowpm) FROM stdin;
\.


--
-- Data for Name: ofmucroomprop; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY ofmucroomprop (roomid, name, propvalue) FROM stdin;
\.


--
-- Data for Name: ofmucservice; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY ofmucservice (serviceid, subdomain, description, ishidden) FROM stdin;
1	conference	\N	0
\.


--
-- Data for Name: ofmucserviceprop; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY ofmucserviceprop (serviceid, name, propvalue) FROM stdin;
1	sysadmin.jid	focus@t430
\.


--
-- Data for Name: ofoffline; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY ofoffline (username, messageid, creationdate, messagesize, stanza) FROM stdin;
admin	1	001498731298056	111	<message from="t430" to="admin@t430"><body>A server or plugin update was found: Openfire 4.1.4</body></message>
admin	2	001498731312107	111	<message from="t430" to="admin@t430"><body>A server or plugin update was found: Openfire 4.1.4</body></message>
\.


--
-- Data for Name: ofpresence; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY ofpresence (username, offlinepresence, offlinedate) FROM stdin;
\.


--
-- Data for Name: ofprivacylist; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY ofprivacylist (username, name, isdefault, list) FROM stdin;
\.


--
-- Data for Name: ofprivate; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY ofprivate (username, name, namespace, privatedata) FROM stdin;
\.


--
-- Data for Name: ofproperty; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY ofproperty (name, propvalue) FROM stdin;
passwordKey	2bi4LFhxkwI3SUq
xmpp.socket.ssl.active	true
provider.admin.className	org.jivesoftware.openfire.admin.DefaultAdminProvider
xmpp.domain	XXXXMPPDOMAINXXX
xmpp.auth.anonymous	false
provider.auth.className	org.jivesoftware.openfire.auth.DefaultAuthProvider
provider.lockout.className	org.jivesoftware.openfire.lockout.DefaultLockOutProvider
provider.group.className	org.jivesoftware.openfire.group.DefaultGroupProvider
provider.vcard.className	org.jivesoftware.openfire.vcard.DefaultVCardProvider
xmpp.fqdn	XXXXMPPFQDNXXX
provider.securityAudit.className	org.jivesoftware.openfire.security.DefaultSecurityAuditProvider
provider.user.className	org.jivesoftware.openfire.user.DefaultUserProvider
adminConsole.securePort	XXXADMINCONSOLESECUREPORTXXX
locale	en
stream.management.active	true
stream.management.requestFrequency	5
connectionProvider.className	org.jivesoftware.database.DefaultConnectionProvider
database.defaultProvider.driver	org.postgresql.Driver
database.defaultProvider.serverURL	jdbc:postgresql://XXXDBHOSTXXX:XXXDBPORTXXX/jitsi
database.defaultProvider.username	XXXDBUSERNAMEXXX
database.defaultProvider.password	XXXDBPASSWORDXXX
database.defaultProvider.testSQL	select 1
database.defaultProvider.testBeforeUse	false
database.defaultProvider.testAfterUse	false
database.defaultProvider.minConnections	5
database.defaultProvider.maxConnections	25
database.defaultProvider.connectionTimeout	1.0
setup	true
sasl.scram-sha-1.iteration-count	4096
xmpp.session.conflict-limit	0
update.lastCheck	1498731312340
plugin.restapi.secret	XXX588wq84qLa6nq
org.jitsi.videobridge.ofmeet.focus.user.jid	XXXFOCUSUSERJIDXXX
org.jitsi.videobridge.ofmeet.focus.user.password	XXXFOCUSPASSXXX
plugin.restapi.enabled	XXXRESTAPIENABLEDXXX
plugin.restapi.httpAuth	basic
plugin.restapi.allowedIPs	
org.jitsi.videobridge.media.MIN_PORT_NUMBER	XXXMEDIAMINPORTXXX
org.jitsi.videobridge.media.MAX_PORT_NUMBER	XXXMEDIAMAXPORTXXX
org.jitsi.videobridge.video.srtpcryptocontext.checkreplay	XXXVIDEOSTRCRYPTOCONTEXTCHECKREPLAYXXX
org.jitsi.videobridge.nat.harvester.local.address	XXXNATLOCALADDRESSXXX
org.jitsi.videobridge.nat.harvester.public.address	XXXNATPUBLICADDRESSXXX
ofmeet.security.enabled	true
ofmeet.autorecord.enabled	false
org.jitsi.videobridge.ofmeet.media.record	false
org.jitsi.videobridge.ofmeet.iceservers	
org.jitsi.videobridge.ofmeet.useipv6	false
org.jitsi.videobridge.ofmeet.usenicks	false
org.jitsi.videobridge.ofmeet.resolution	360
org.jitsi.videobridge.ofmeet.audio.bandwidth	64
org.jitsi.videobridge.ofmeet.video.bandwidth	512
org.jitsi.videobridge.ofmeet.audio.mixer	XXXOFMEETAUDIOMIXERXXX
org.jitsi.videobridge.ofmeet.recording.secret	XXXOFMEETRECORDINGSECRETXXX
org.jitsi.videobridge.ofmeet.recording.path	XXXOFMEETRECORDINGPATHXXX
org.jitsi.videobridge.ofmeet.adaptive.lastn	XXXOFMEETADAPTIVELASTNXXX
org.jitsi.videobridge.ofmeet.adaptive.simulcast	XXXOFMEETADAPTIVESIMULCASTXXX
org.jitsi.videobridge.ofmeet.enable.simulcast	XXXOFMEETENABLESIMULCASTXXX
org.jitsi.videobridge.ofmeet.canvas.extra	104
org.jitsi.videobridge.ofmeet.canvas.radius	7
org.jitsi.videobridge.ofmeet.shadow.color	#ffffff
org.jitsi.videobridge.ofmeet.initial.toolbar.timeout	20000
org.jitsi.videobridge.ofmeet.toolbar.timeout	4000
org.jitsi.videobridge.ofmeet.default.remote.displayname	Change Me
org.jitsi.videobridge.ofmeet.default.speaker.displayname	Speaker
org.jitsi.videobridge.ofmeet.default.local.displayname	Me
org.jitsi.videobridge.ofmeet.watermark.link	
org.jitsi.videobridge.ofmeet.show.watermark	false
org.jitsi.videobridge.ofmeet.brand.watermark.link	
org.jitsi.videobridge.ofmeet.brand.show.watermark	false
org.jitsi.videobridge.ofmeet.show.poweredby	false
org.jitsi.videobridge.ofmeet.random.roomnames	true
org.jitsi.videobridge.ofmeet.application.name	Openfire Meetings
org.jitsi.videobridge.ofmeet.active.speaker.avatarsize	100
xmpp.socket.ssl.keypass	changeit
xmpp.socket.ssl.client.keypass	changeit
xmpp.bosh.ssl.client.keypass	changeit
xmpp.component.keypass	changeit
xmpp.multiplex.keypass	changeit
httpbind.port.secure	17443
\.


--
-- Data for Name: ofpubsubaffiliation; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY ofpubsubaffiliation (serviceid, nodeid, jid, affiliation) FROM stdin;
pubsub		t430	owner
\.


--
-- Data for Name: ofpubsubdefaultconf; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY ofpubsubdefaultconf (serviceid, leaf, deliverpayloads, maxpayloadsize, persistitems, maxitems, notifyconfigchanges, notifydelete, notifyretract, presencebased, senditemsubscribe, publishermodel, subscriptionenabled, accessmodel, language, replypolicy, associationpolicy, maxleafnodes) FROM stdin;
pubsub	1	1	5120	0	1	1	1	1	0	1	publishers	1	open	English	\N	all	-1
pubsub	0	0	0	0	0	1	1	1	0	0	publishers	1	open	English	\N	all	-1
\.


--
-- Data for Name: ofpubsubitem; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY ofpubsubitem (serviceid, nodeid, id, jid, creationdate, payload) FROM stdin;
\.


--
-- Data for Name: ofpubsubnode; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY ofpubsubnode (serviceid, nodeid, leaf, creationdate, modificationdate, parent, deliverpayloads, maxpayloadsize, persistitems, maxitems, notifyconfigchanges, notifydelete, notifyretract, presencebased, senditemsubscribe, publishermodel, subscriptionenabled, configsubscription, accessmodel, payloadtype, bodyxslt, dataformxslt, creator, description, language, name, replypolicy, associationpolicy, maxleafnodes) FROM stdin;
pubsub		0	001498731276933	001498731276933	\N	0	0	0	0	1	1	1	0	0	publishers	1	0	open				t430		English		\N	all	-1
\.


--
-- Data for Name: ofpubsubnodegroups; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY ofpubsubnodegroups (serviceid, nodeid, rostergroup) FROM stdin;
\.


--
-- Data for Name: ofpubsubnodejids; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY ofpubsubnodejids (serviceid, nodeid, jid, associationtype) FROM stdin;
\.


--
-- Data for Name: ofpubsubsubscription; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY ofpubsubsubscription (serviceid, nodeid, id, jid, owner, state, deliver, digest, digest_frequency, expire, includebody, showvalues, subscriptiontype, subscriptiondepth, keyword) FROM stdin;
\.


--
-- Data for Name: ofremoteserverconf; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY ofremoteserverconf (xmppdomain, remoteport, permission) FROM stdin;
\.


--
-- Data for Name: ofroster; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY ofroster (rosterid, username, jid, sub, ask, recv, nick) FROM stdin;
\.


--
-- Data for Name: ofrostergroups; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY ofrostergroups (rosterid, rank, groupname) FROM stdin;
\.


--
-- Data for Name: ofsaslauthorized; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY ofsaslauthorized (username, principal) FROM stdin;
\.


--
-- Data for Name: ofsecurityauditlog; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY ofsecurityauditlog (msgid, username, entrystamp, summary, node, details) FROM stdin;
1	admin	1498731430479	uploaded plugin ofmeet.jar	t430	\N
\.


--
-- Data for Name: ofuser; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY ofuser (username, storedkey, serverkey, salt, iterations, plainpassword, encryptedpassword, name, email, creationdate, modificationdate) FROM stdin;
admin	WeD/iBqvZrnyrZTMSBTiUHhrdWM=	xyCUc2eIKMsY5/shKesf98YUe4w=	\N	4096	XXXADMINPASSXXX	\N	Administrator	XXXADMINEMAILXXX	001498731274529	0              
focus	CnlLwMqcnm39jMEnT+y7XED99zA=	8Og2QmgTo4SdTlnrOCeXeGYbknE=	\N	4096	XXXFOCUSPASSXXX	\N	Openfire Meetings Focus User	focus@XXXOWNHOSTXXX	001498731430409	001498731430409
\.


--
-- Data for Name: ofuserflag; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY ofuserflag (username, name, starttime, endtime) FROM stdin;
\.


--
-- Data for Name: ofuserprop; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY ofuserprop (username, name, propvalue) FROM stdin;
\.


--
-- Data for Name: ofvcard; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY ofvcard (username, vcard) FROM stdin;
\.


--
-- Data for Name: ofversion; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY ofversion (name, version) FROM stdin;
openfire	25
\.


--
-- Name: ofextcomponentconf_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ofextcomponentconf
    ADD CONSTRAINT ofextcomponentconf_pk PRIMARY KEY (subdomain);


--
-- Name: ofgroup_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ofgroup
    ADD CONSTRAINT ofgroup_pk PRIMARY KEY (groupname);


--
-- Name: ofgroupprop_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ofgroupprop
    ADD CONSTRAINT ofgroupprop_pk PRIMARY KEY (groupname, name);


--
-- Name: ofgroupuser_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ofgroupuser
    ADD CONSTRAINT ofgroupuser_pk PRIMARY KEY (groupname, username, administrator);


--
-- Name: ofid_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ofid
    ADD CONSTRAINT ofid_pk PRIMARY KEY (idtype);


--
-- Name: ofmucaffiliation_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ofmucaffiliation
    ADD CONSTRAINT ofmucaffiliation_pk PRIMARY KEY (roomid, jid);


--
-- Name: ofmucmember_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ofmucmember
    ADD CONSTRAINT ofmucmember_pk PRIMARY KEY (roomid, jid);


--
-- Name: ofmucroom_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ofmucroom
    ADD CONSTRAINT ofmucroom_pk PRIMARY KEY (serviceid, name);


--
-- Name: ofmucroomprop_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ofmucroomprop
    ADD CONSTRAINT ofmucroomprop_pk PRIMARY KEY (roomid, name);


--
-- Name: ofmucservice_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ofmucservice
    ADD CONSTRAINT ofmucservice_pk PRIMARY KEY (subdomain);


--
-- Name: ofmucserviceprop_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ofmucserviceprop
    ADD CONSTRAINT ofmucserviceprop_pk PRIMARY KEY (serviceid, name);


--
-- Name: ofoffline_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ofoffline
    ADD CONSTRAINT ofoffline_pk PRIMARY KEY (username, messageid);


--
-- Name: ofpresence_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ofpresence
    ADD CONSTRAINT ofpresence_pk PRIMARY KEY (username);


--
-- Name: ofprivacylist_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ofprivacylist
    ADD CONSTRAINT ofprivacylist_pk PRIMARY KEY (username, name);


--
-- Name: ofprivate_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ofprivate
    ADD CONSTRAINT ofprivate_pk PRIMARY KEY (username, name, namespace);


--
-- Name: ofproperty_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ofproperty
    ADD CONSTRAINT ofproperty_pk PRIMARY KEY (name);


--
-- Name: ofpubsubaffiliation_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ofpubsubaffiliation
    ADD CONSTRAINT ofpubsubaffiliation_pk PRIMARY KEY (serviceid, nodeid, jid);


--
-- Name: ofpubsubdefaultconf_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ofpubsubdefaultconf
    ADD CONSTRAINT ofpubsubdefaultconf_pk PRIMARY KEY (serviceid, leaf);


--
-- Name: ofpubsubitem_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ofpubsubitem
    ADD CONSTRAINT ofpubsubitem_pk PRIMARY KEY (serviceid, nodeid, id);


--
-- Name: ofpubsubnode_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ofpubsubnode
    ADD CONSTRAINT ofpubsubnode_pk PRIMARY KEY (serviceid, nodeid);


--
-- Name: ofpubsubnodejids_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ofpubsubnodejids
    ADD CONSTRAINT ofpubsubnodejids_pk PRIMARY KEY (serviceid, nodeid, jid);


--
-- Name: ofpubsubsubscription_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ofpubsubsubscription
    ADD CONSTRAINT ofpubsubsubscription_pk PRIMARY KEY (serviceid, nodeid, id);


--
-- Name: ofremoteserverconf_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ofremoteserverconf
    ADD CONSTRAINT ofremoteserverconf_pk PRIMARY KEY (xmppdomain);


--
-- Name: ofroster_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ofroster
    ADD CONSTRAINT ofroster_pk PRIMARY KEY (rosterid);


--
-- Name: ofrostergroups_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ofrostergroups
    ADD CONSTRAINT ofrostergroups_pk PRIMARY KEY (rosterid, rank);


--
-- Name: ofsaslauthorized_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ofsaslauthorized
    ADD CONSTRAINT ofsaslauthorized_pk PRIMARY KEY (username, principal);


--
-- Name: ofsecurityauditlog_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ofsecurityauditlog
    ADD CONSTRAINT ofsecurityauditlog_pk PRIMARY KEY (msgid);


--
-- Name: ofuser_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ofuser
    ADD CONSTRAINT ofuser_pk PRIMARY KEY (username);


--
-- Name: ofuserflag_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ofuserflag
    ADD CONSTRAINT ofuserflag_pk PRIMARY KEY (username, name);


--
-- Name: ofuserprop_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ofuserprop
    ADD CONSTRAINT ofuserprop_pk PRIMARY KEY (username, name);


--
-- Name: ofvcard_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ofvcard
    ADD CONSTRAINT ofvcard_pk PRIMARY KEY (username);


--
-- Name: ofversion_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ofversion
    ADD CONSTRAINT ofversion_pk PRIMARY KEY (name);


--
-- Name: ofmucconversationlog_msg_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ofmucconversationlog_msg_id ON ofmucconversationlog USING btree (messageid);


--
-- Name: ofmucconversationlog_time_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ofmucconversationlog_time_idx ON ofmucconversationlog USING btree (logtime);


--
-- Name: ofmucroom_roomid_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ofmucroom_roomid_idx ON ofmucroom USING btree (roomid);


--
-- Name: ofmucroom_serviceid_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ofmucroom_serviceid_idx ON ofmucroom USING btree (serviceid);


--
-- Name: ofmucservice_serviceid_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ofmucservice_serviceid_idx ON ofmucservice USING btree (serviceid);


--
-- Name: ofprivacylist_default_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ofprivacylist_default_idx ON ofprivacylist USING btree (username, isdefault);


--
-- Name: ofpubsubnodegroups_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ofpubsubnodegroups_idx ON ofpubsubnodegroups USING btree (serviceid, nodeid);


--
-- Name: ofroster_jid_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ofroster_jid_idx ON ofroster USING btree (jid);


--
-- Name: ofroster_username_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ofroster_username_idx ON ofroster USING btree (username);


--
-- Name: ofrostergroups_rosterid_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ofrostergroups_rosterid_idx ON ofrostergroups USING btree (rosterid);


--
-- Name: ofsecurityauditlog_tstamp_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ofsecurityauditlog_tstamp_idx ON ofsecurityauditlog USING btree (entrystamp);


--
-- Name: ofsecurityauditlog_uname_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ofsecurityauditlog_uname_idx ON ofsecurityauditlog USING btree (username);


--
-- Name: ofuser_cdate_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ofuser_cdate_idx ON ofuser USING btree (creationdate);


--
-- Name: ofuserflag_etime_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ofuserflag_etime_idx ON ofuserflag USING btree (endtime);


--
-- Name: ofuserflag_stime_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ofuserflag_stime_idx ON ofuserflag USING btree (starttime);


--
-- Name: ofrostergroups_rosterid_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ofrostergroups
    ADD CONSTRAINT ofrostergroups_rosterid_fk FOREIGN KEY (rosterid) REFERENCES ofroster(rosterid) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--

