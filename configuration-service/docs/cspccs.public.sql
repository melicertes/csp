/*
Navicat PGSQL Data Transfer

Source Server         : csp.dangerduck.gr
Source Server Version : 90507
Source Host           : localhost:5432
Source Database       : cspccs
Source Schema         : public

Target Server Type    : PGSQL
Target Server Version : 90507
File Encoding         : 65001

Date: 2017-06-22 13:51:10
*/


-- ----------------------------
-- Table structure for csp
-- ----------------------------
DROP TABLE IF EXISTS "public"."csp";
CREATE TABLE "public"."csp" (
"id" varchar(36) COLLATE "default" NOT NULL,
"name" varchar(255) COLLATE "default" NOT NULL,
"domainName" varchar(255) COLLATE "default" NOT NULL,
"registrationDate" timestamptz(6)
)
WITH (OIDS=FALSE)

;
COMMENT ON TABLE "public"."csp" IS 'Registered CSPs information in CCS';

-- ----------------------------
-- Table structure for csp_contact
-- ----------------------------
DROP TABLE IF EXISTS "public"."csp_contact";
CREATE TABLE "public"."csp_contact" (
"id" int4 NOT NULL,
"cspId" varchar(36) COLLATE "default" NOT NULL,
"personName" varchar(255) COLLATE "default" NOT NULL,
"personEmail" varchar(255) COLLATE "default" NOT NULL,
"contactType" varchar(16) COLLATE "default" NOT NULL
)
WITH (OIDS=FALSE)

;
COMMENT ON TABLE "public"."csp_contact" IS 'Contacts per registered CSP';
COMMENT ON COLUMN "public"."csp_contact"."contactType" IS 'Enum';

-- ----------------------------
-- Table structure for csp_info
-- ----------------------------
DROP TABLE IF EXISTS "public"."csp_info";
CREATE TABLE "public"."csp_info" (
"id" int4 NOT NULL,
"cspId" varchar(36) COLLATE "default" NOT NULL,
"recordDateTime" timestamptz(6) NOT NULL
)
WITH (OIDS=FALSE)

;
COMMENT ON TABLE "public"."csp_info" IS 'Information from client CSP CS heartbeat';

-- ----------------------------
-- Table structure for csp_ip
-- ----------------------------
DROP TABLE IF EXISTS "public"."csp_ip";
CREATE TABLE "public"."csp_ip" (
"id" int4 NOT NULL,
"cspId" varchar(36) COLLATE "default" NOT NULL,
"ip" varchar(15) COLLATE "default" NOT NULL,
"external" bit(1) NOT NULL
)
WITH (OIDS=FALSE)

;
COMMENT ON TABLE "public"."csp_ip" IS 'IPs per registered CSP';
COMMENT ON COLUMN "public"."csp_ip"."external" IS '1: External, 0: Internal';

-- ----------------------------
-- Table structure for csp_module
-- ----------------------------
DROP TABLE IF EXISTS "public"."csp_module";
CREATE TABLE "public"."csp_module" (
"id" int4 NOT NULL,
"cspId" varchar(36) COLLATE "default" NOT NULL,
"moduleId" int4 NOT NULL,
"moduleVersionId" int4 NOT NULL
)
WITH (OIDS=FALSE)

;
COMMENT ON TABLE "public"."csp_module" IS 'Configuration of assigned modules and their versions per registered CSP';

-- ----------------------------
-- Table structure for csp_module_info
-- ----------------------------
DROP TABLE IF EXISTS "public"."csp_module_info";
CREATE TABLE "public"."csp_module_info" (
"id" int4 NOT NULL,
"cspInfoId" int4 NOT NULL,
"moduleVersionId" int4 NOT NULL,
"moduleInstalledOn" timestamptz(6) NOT NULL,
"moduleIsActive" bit(1) NOT NULL
)
WITH (OIDS=FALSE)

;
COMMENT ON TABLE "public"."csp_module_info" IS 'Information from client CSP CS heartbeat, regarding its installed modules';
COMMENT ON COLUMN "public"."csp_module_info"."moduleIsActive" IS '1: Active, 0: Not Active';

-- ----------------------------
-- Table structure for module
-- ----------------------------
DROP TABLE IF EXISTS "public"."module";
CREATE TABLE "public"."module" (
"id" int4 NOT NULL,
"name" varchar(255) COLLATE "default" NOT NULL,
"isDefault" bit(1) NOT NULL,
"startPriority" int2 NOT NULL
)
WITH (OIDS=FALSE)

;
COMMENT ON TABLE "public"."module" IS 'Information of available modules';
COMMENT ON COLUMN "public"."module"."isDefault" IS '1: Default';

-- ----------------------------
-- Table structure for module_version
-- ----------------------------
DROP TABLE IF EXISTS "public"."module_version";
CREATE TABLE "public"."module_version" (
"id" int4 NOT NULL,
"moduleId" int4 NOT NULL,
"fullName" varchar(255) COLLATE "default" NOT NULL,
"version" int4 NOT NULL,
"releasedOn" timestamptz(6) NOT NULL,
"hash" varchar(255) COLLATE "default" NOT NULL
)
WITH (OIDS=FALSE)

;
COMMENT ON TABLE "public"."module_version" IS 'Information of available modules'' versions (update files)';

-- ----------------------------
-- Alter Sequences Owned By 
-- ----------------------------

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
-- Primary Key structure for table csp_module
-- ----------------------------
ALTER TABLE "public"."csp_module" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table csp_module_info
-- ----------------------------
ALTER TABLE "public"."csp_module_info" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table module
-- ----------------------------
ALTER TABLE "public"."module" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table module_version
-- ----------------------------
ALTER TABLE "public"."module_version" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Foreign Key structure for table "public"."csp_contact"
-- ----------------------------
ALTER TABLE "public"."csp_contact" ADD FOREIGN KEY ("cspId") REFERENCES "public"."csp" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- ----------------------------
-- Foreign Key structure for table "public"."csp_info"
-- ----------------------------
ALTER TABLE "public"."csp_info" ADD FOREIGN KEY ("cspId") REFERENCES "public"."csp" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- ----------------------------
-- Foreign Key structure for table "public"."csp_ip"
-- ----------------------------
ALTER TABLE "public"."csp_ip" ADD FOREIGN KEY ("cspId") REFERENCES "public"."csp" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- ----------------------------
-- Foreign Key structure for table "public"."csp_module"
-- ----------------------------
ALTER TABLE "public"."csp_module" ADD FOREIGN KEY ("moduleVersionId") REFERENCES "public"."module_version" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "public"."csp_module" ADD FOREIGN KEY ("moduleId") REFERENCES "public"."module" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- ----------------------------
-- Foreign Key structure for table "public"."csp_module_info"
-- ----------------------------
ALTER TABLE "public"."csp_module_info" ADD FOREIGN KEY ("moduleVersionId") REFERENCES "public"."module_version" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "public"."csp_module_info" ADD FOREIGN KEY ("cspInfoId") REFERENCES "public"."csp_info" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- ----------------------------
-- Foreign Key structure for table "public"."module_version"
-- ----------------------------
ALTER TABLE "public"."module_version" ADD FOREIGN KEY ("moduleId") REFERENCES "public"."module" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;
