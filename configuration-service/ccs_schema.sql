/*
Navicat PGSQL Data Transfer

Source Server         : localhost
Source Server Version : 90405
Source Host           : localhost:5432
Source Database       : cspccs
Source Schema         : public

Target Server Type    : PGSQL
Target Server Version : 90405
File Encoding         : 65001

Date: 2017-08-01 05:15:51
*/


-- ----------------------------
-- Sequence structure for csp_contact_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."csp_contact_id_seq";
CREATE SEQUENCE "public"."csp_contact_id_seq"
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 162
 CACHE 1
 CYCLE;
SELECT setval('"public"."csp_contact_id_seq"', 162, true);

-- ----------------------------
-- Sequence structure for csp_info_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."csp_info_id_seq";
CREATE SEQUENCE "public"."csp_info_id_seq"
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 36
 CACHE 1
 CYCLE;
SELECT setval('"public"."csp_info_id_seq"', 36, true);

-- ----------------------------
-- Sequence structure for csp_ip_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."csp_ip_id_seq";
CREATE SEQUENCE "public"."csp_ip_id_seq"
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 351
 CACHE 1
 CYCLE;
SELECT setval('"public"."csp_ip_id_seq"', 351, true);

-- ----------------------------
-- Sequence structure for csp_management_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."csp_management_id_seq";
CREATE SEQUENCE "public"."csp_management_id_seq"
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 38
 CACHE 1
 CYCLE;
SELECT setval('"public"."csp_management_id_seq"', 38, true);

-- ----------------------------
-- Sequence structure for csp_module_info_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."csp_module_info_seq";
CREATE SEQUENCE "public"."csp_module_info_seq"
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 32
 CACHE 1
 CYCLE;
SELECT setval('"public"."csp_module_info_seq"', 32, true);

-- ----------------------------
-- Sequence structure for module_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."module_id_seq";
CREATE SEQUENCE "public"."module_id_seq"
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 64
 CACHE 1
 CYCLE;
SELECT setval('"public"."module_id_seq"', 64, true);

-- ----------------------------
-- Sequence structure for module_version_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."module_version_id_seq";
CREATE SEQUENCE "public"."module_version_id_seq"
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 34
 CACHE 1
 CYCLE;
SELECT setval('"public"."module_version_id_seq"', 34, true);

-- ----------------------------
-- Table structure for csp
-- ----------------------------
DROP TABLE IF EXISTS "public"."csp";
CREATE TABLE "public"."csp" (
"id" varchar(36) COLLATE "default" NOT NULL,
"name" varchar(255) COLLATE "default" NOT NULL,
"domain_name" varchar(255) COLLATE "default" NOT NULL,
"registration_date" varchar(24) COLLATE "default"
)
WITH (OIDS=FALSE)

;
COMMENT ON TABLE "public"."csp" IS 'Registered CSPs information in CCS';

-- ----------------------------
-- Table structure for csp_contact
-- ----------------------------
DROP TABLE IF EXISTS "public"."csp_contact";
CREATE TABLE "public"."csp_contact" (
"id" int8 DEFAULT nextval('csp_contact_id_seq'::regclass) NOT NULL,
"csp_id" varchar(36) COLLATE "default" NOT NULL,
"person_name" varchar(255) COLLATE "default" NOT NULL,
"person_email" varchar(255) COLLATE "default" NOT NULL,
"contact_type" int2 NOT NULL
)
WITH (OIDS=FALSE)

;
COMMENT ON TABLE "public"."csp_contact" IS 'Contacts per registered CSP';

-- ----------------------------
-- Table structure for csp_info
-- ----------------------------
DROP TABLE IF EXISTS "public"."csp_info";
CREATE TABLE "public"."csp_info" (
"id" int8 DEFAULT nextval('csp_info_id_seq'::regclass) NOT NULL,
"csp_id" varchar(36) COLLATE "default" NOT NULL,
"record_date_time" varchar(24) COLLATE "default" NOT NULL
)
WITH (OIDS=FALSE)

;
COMMENT ON TABLE "public"."csp_info" IS 'Information from client CSP CS heartbeat';

-- ----------------------------
-- Table structure for csp_ip
-- ----------------------------
DROP TABLE IF EXISTS "public"."csp_ip";
CREATE TABLE "public"."csp_ip" (
"csp_id" varchar(36) COLLATE "default" NOT NULL,
"ip" varchar(15) COLLATE "default" NOT NULL,
"id" int8 DEFAULT nextval('csp_ip_id_seq'::regclass) NOT NULL,
"external" int2 NOT NULL
)
WITH (OIDS=FALSE)

;
COMMENT ON TABLE "public"."csp_ip" IS 'IPs per registered CSP';
COMMENT ON COLUMN "public"."csp_ip"."external" IS '1: external, 0: internal';

-- ----------------------------
-- Table structure for csp_management
-- ----------------------------
DROP TABLE IF EXISTS "public"."csp_management";
CREATE TABLE "public"."csp_management" (
"id" int8 DEFAULT nextval('csp_management_id_seq'::regclass) NOT NULL,
"csp_id" varchar(36) COLLATE "default" NOT NULL,
"module_id" int8 NOT NULL,
"module_version_id" int8 NOT NULL,
"date_changed" varchar(24) COLLATE "default"
)
WITH (OIDS=FALSE)

;
COMMENT ON TABLE "public"."csp_management" IS 'Configuration of assigned modules and their versions per registered CSP';

-- ----------------------------
-- Table structure for csp_module_info
-- ----------------------------
DROP TABLE IF EXISTS "public"."csp_module_info";
CREATE TABLE "public"."csp_module_info" (
"id" int8 DEFAULT nextval('csp_module_info_seq'::regclass) NOT NULL,
"csp_info_id" int8 NOT NULL,
"module_version_id" int8 NOT NULL,
"module_installed_on" varchar(24) COLLATE "default" NOT NULL,
"module_is_active" int2 NOT NULL
)
WITH (OIDS=FALSE)

;
COMMENT ON TABLE "public"."csp_module_info" IS 'Information from client CSP CS heartbeat, regarding its installed modules';

-- ----------------------------
-- Table structure for module
-- ----------------------------
DROP TABLE IF EXISTS "public"."module";
CREATE TABLE "public"."module" (
"id" int8 DEFAULT nextval('module_id_seq'::regclass) NOT NULL,
"name" varchar(255) COLLATE "default" NOT NULL,
"start_priority" int4 NOT NULL,
"is_default" int2 DEFAULT nextval('module_id_seq'::regclass) NOT NULL
)
WITH (OIDS=FALSE)

;
COMMENT ON TABLE "public"."module" IS 'Information of available modules';
COMMENT ON COLUMN "public"."module"."is_default" IS '1: Default, 0: otherwise';

-- ----------------------------
-- Table structure for module_version
-- ----------------------------
DROP TABLE IF EXISTS "public"."module_version";
CREATE TABLE "public"."module_version" (
"id" int8 DEFAULT nextval('module_version_id_seq'::regclass) NOT NULL,
"module_id" int8 NOT NULL,
"full_name" varchar(255) COLLATE "default" NOT NULL,
"version" int4 NOT NULL,
"released_on" varchar(24) COLLATE "default" DEFAULT nextval('module_version_id_seq'::regclass) NOT NULL,
"hash" varchar(255) COLLATE "default" NOT NULL,
"description" varchar(1024) COLLATE "default"
)
WITH (OIDS=FALSE)

;
COMMENT ON TABLE "public"."module_version" IS 'Information of available modules'' versions (update files)';

-- ----------------------------
-- Alter Sequences Owned By 
-- ----------------------------
ALTER SEQUENCE "public"."csp_contact_id_seq" OWNED BY "csp_contact"."id";
ALTER SEQUENCE "public"."csp_info_id_seq" OWNED BY "csp_info"."id";
ALTER SEQUENCE "public"."csp_ip_id_seq" OWNED BY "csp_ip"."csp_id";
ALTER SEQUENCE "public"."csp_management_id_seq" OWNED BY "csp_management"."id";
ALTER SEQUENCE "public"."csp_module_info_seq" OWNED BY "csp_module_info"."id";
ALTER SEQUENCE "public"."module_id_seq" OWNED BY "module"."id";
ALTER SEQUENCE "public"."module_version_id_seq" OWNED BY "module_version"."id";

-- ----------------------------
-- Uniques structure for table csp
-- ----------------------------
ALTER TABLE "public"."csp" ADD UNIQUE ("name");
ALTER TABLE "public"."csp" ADD UNIQUE ("domain_name");
ALTER TABLE "public"."csp" ADD UNIQUE ("id");

-- ----------------------------
-- Primary Key structure for table csp
-- ----------------------------
ALTER TABLE "public"."csp" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table csp_contact
-- ----------------------------
ALTER TABLE "public"."csp_contact" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table csp_info
-- ----------------------------
ALTER TABLE "public"."csp_info" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table csp_ip
-- ----------------------------
ALTER TABLE "public"."csp_ip" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table csp_management
-- ----------------------------
ALTER TABLE "public"."csp_management" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table csp_module_info
-- ----------------------------
ALTER TABLE "public"."csp_module_info" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Uniques structure for table module
-- ----------------------------
ALTER TABLE "public"."module" ADD UNIQUE ("name");

-- ----------------------------
-- Primary Key structure for table module
-- ----------------------------
ALTER TABLE "public"."module" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Uniques structure for table module_version
-- ----------------------------
ALTER TABLE "public"."module_version" ADD UNIQUE ("hash");
ALTER TABLE "public"."module_version" ADD UNIQUE ("full_name");

-- ----------------------------
-- Primary Key structure for table module_version
-- ----------------------------
ALTER TABLE "public"."module_version" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Foreign Key structure for table "public"."csp_contact"
-- ----------------------------
ALTER TABLE "public"."csp_contact" ADD FOREIGN KEY ("csp_id") REFERENCES "public"."csp" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- ----------------------------
-- Foreign Key structure for table "public"."csp_info"
-- ----------------------------
ALTER TABLE "public"."csp_info" ADD FOREIGN KEY ("csp_id") REFERENCES "public"."csp" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- ----------------------------
-- Foreign Key structure for table "public"."csp_ip"
-- ----------------------------
ALTER TABLE "public"."csp_ip" ADD FOREIGN KEY ("csp_id") REFERENCES "public"."csp" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- ----------------------------
-- Foreign Key structure for table "public"."csp_management"
-- ----------------------------
ALTER TABLE "public"."csp_management" ADD FOREIGN KEY ("module_id") REFERENCES "public"."module" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "public"."csp_management" ADD FOREIGN KEY ("module_version_id") REFERENCES "public"."module_version" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- ----------------------------
-- Foreign Key structure for table "public"."csp_module_info"
-- ----------------------------
ALTER TABLE "public"."csp_module_info" ADD FOREIGN KEY ("module_version_id") REFERENCES "public"."module_version" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "public"."csp_module_info" ADD FOREIGN KEY ("csp_info_id") REFERENCES "public"."csp_info" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- ----------------------------
-- Foreign Key structure for table "public"."module_version"
-- ----------------------------
ALTER TABLE "public"."module_version" ADD FOREIGN KEY ("module_id") REFERENCES "public"."module" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;
