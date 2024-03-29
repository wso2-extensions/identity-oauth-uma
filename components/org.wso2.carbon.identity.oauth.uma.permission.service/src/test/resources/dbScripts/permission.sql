CREATE TABLE IF NOT EXISTS IDN_UMA_RESOURCE (
  ID                  INTEGER AUTO_INCREMENT NOT NULL,
  RESOURCE_ID         VARCHAR(255),
  RESOURCE_NAME       VARCHAR(255),
  TIME_CREATED        TIMESTAMP DEFAULT '0',
  RESOURCE_OWNER_NAME VARCHAR(255),
  CLIENT_ID           VARCHAR(255),
  TENANT_ID           INTEGER   DEFAULT '-1234',
  USER_DOMAIN         VARCHAR(50),
  PRIMARY KEY (ID)
);

CREATE TABLE IF NOT EXISTS IDN_UMA_RESOURCE_SCOPE (
  ID                INTEGER AUTO_INCREMENT NOT NULL,
  RESOURCE_IDENTITY INTEGER                NOT NULL,
  SCOPE_NAME        VARCHAR(255),
  PRIMARY KEY (ID),
  CONSTRAINT FK_IDN_UMA_RESOURCE FOREIGN KEY (RESOURCE_IDENTITY) REFERENCES IDN_UMA_RESOURCE (ID)
  ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS IDN_UMA_PERMISSION_TICKET (
  ID              INTEGER AUTO_INCREMENT NOT NULL,
  PT              VARCHAR(255)           NOT NULL,
  TIME_CREATED    TIMESTAMP              NOT NULL,
  EXPIRY_TIME     TIMESTAMP              NOT NULL,
  TICKET_STATE    VARCHAR(25) DEFAULT 'ACTIVE',
  TENANT_ID       INTEGER     DEFAULT '-1234',
  PRIMARY KEY (ID)
);

CREATE TABLE IF NOT EXISTS IDN_UMA_PT_RESOURCE (
  ID       INTEGER AUTO_INCREMENT NOT NULL,
  PT_RESOURCE_ID INTEGER NOT NULL ,
  PT_ID    INTEGER NOT NULL ,
  PRIMARY KEY (ID),
  CONSTRAINT FK_PT FOREIGN KEY (PT_ID) REFERENCES IDN_UMA_PERMISSION_TICKET (ID) ON DELETE CASCADE,
  CONSTRAINT FK_PT_RESOURCE FOREIGN KEY (PT_RESOURCE_ID) REFERENCES IDN_UMA_RESOURCE (ID) ON DELETE CASCADE
);

CREATE TABLE IDN_UMA_PT_RESOURCE_SCOPE (
  ID             INTEGER AUTO_INCREMENT NOT NULL,
  PT_RESOURCE_ID INTEGER                NOT NULL,
  PT_SCOPE_ID    INTEGER                NOT NULL,
  PRIMARY KEY (ID),
  CONSTRAINT FK_PT_RESOURCE_ID FOREIGN KEY (PT_RESOURCE_ID) REFERENCES IDN_UMA_PT_RESOURCE (ID) ON DELETE CASCADE,
  CONSTRAINT FK_PT_SCOPE_ID FOREIGN KEY (PT_SCOPE_ID) REFERENCES IDN_UMA_RESOURCE_SCOPE (ID) ON DELETE CASCADE
);
