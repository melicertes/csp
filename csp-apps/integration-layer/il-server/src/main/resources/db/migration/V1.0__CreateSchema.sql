DROP TABLE IF EXISTS `policy`;

CREATE TABLE IF NOT EXISTS `policy` (
  `id`                    INT          NOT NULL AUTO_INCREMENT,
  `active`                TINYINT(1)   NULL,
  `integration_data_type` VARCHAR(255) NOT NULL,
  `policy_condition`      TEXT         NOT NULL,
  `sharing_policy_action` VARCHAR(255) NOT NULL,

  PRIMARY KEY (`id`)
)
