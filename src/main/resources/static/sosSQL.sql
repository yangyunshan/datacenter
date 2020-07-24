-- 储存传感器信息
CREATE TABLE procedure (procedureId SERIAL NOT NULL PRIMARY KEY, procedureDescriptionFormat VARCHAR(255) NOT NULL, identifier VARCHAR(255) NOT NULL UNIQUE, name VARCHAR(255), description VARCHAR(255), disabled CHAR(1) default 'F' NOT NULL check (disabled in ('T','F')), descriptionFile VARCHAR(255), typeOf VARCHAR(255), isType CHAR(1) default 'F' check (isType in ('T','F')), isAggregation CHAR(1) default 'T' check (isAggregation in ('T','F')), ismobile CHAR(1) default 'F' check (ismobile in ('T','F')), isinsitu CHAR(1) default 'T' check (isinsitu in ('T','F')));
COMMENT ON TABLE procedure is 'Table to store the procedure/sensor.';
COMMENT ON COLUMN procedure.procedureId is 'Table primary key, used for relations';
COMMENT ON COLUMN procedure.procedureDescriptionFormat is 'Describes the format of the procedure description.';
COMMENT ON COLUMN procedure.identifier is 'The identifier of the procedure, gml:identifier. Used as parameter for queries. Unique';
COMMENT ON COLUMN procedure.name is 'The name of the procedure, gml:name. Optional';
COMMENT ON COLUMN procedure.description is 'Description of the procedure, gml:description. Optional';
COMMENT ON COLUMN procedure.disabled is 'For later use by the SOS. Indicator if this procedure should not be provided by the SOS.';
COMMENT ON COLUMN procedure.descriptionFile is 'Field for full (XML) encoded procedure description or link to a procedure description file. Optional';
COMMENT ON COLUMN procedure.typeOf is 'Optional, contains procedureId if this procedure is typeOf another procedure';
COMMENT ON COLUMN procedure.isType is 'Flag to indicate that this procedure is a type description, has no observations.';
COMMENT ON COLUMN procedure.isAggregation is 'Flag to indicate that this procedure is an aggregation (e.g. System, PhysicalSystem).';
COMMENT ON COLUMN procedure.ismobile is 'Flag to indicate that this procedure is stationary (false) or mobile (true). Optional';
COMMENT ON COLUMN procedure.isinsitu is 'Flag to indicate that this procedure is insitu (true) or remote (false). Optional';

-- 设计相应的表仅仅为了后续的传感器检索
CREATE TABLE keyword (keyword_id SERIAL NOT NULL PRIMARY KEY, keyword_identifier VARCHAR(255) NOT NULL UNIQUE, keyword_value TEXT);
COMMENT ON TABLE keyword is '存储关键字属性';
COMMENT ON COLUMN keyword_id is '自增主键';
COMMENT ON COLUMN keyword_identifier is '映射procedure的identifier，格式为procedure_identifier:keywords';
COMMENT ON COLUMN keyword_value is '关键字的值';

CREATE TABLE identification (identification_id SERIAL NOT NULL PRIMARY KEY, identification_identifier VARCHAR(255) NOT NULL UNIQUE, identification_definition VARCHAR(255), identification_label VARCHAR(45), identification_value VARCHAR(255));
COMMENT ON TABLE identification is '储存标识信息';
COMMENT ON COLUMN identifier_id is '自增主键';
COMMENT ON COLUMN identifier_identifier is '标识,格式为procedure_identifier:identification';
COMMENT ON COLUMN identifier_definition is '标识定义';
COMMENT ON COLUMN identifier_label is '标签';
COMMENT ON COLUMN identifier_value is '值';

CREATE TABLE classification(classification_id SERIAL NOT NULL PRIMARY KEY, classification_identifier VARCHAR(255) NOT NULL UNIQUE, classification_label VARCHAR(45), classification_definition VARCHAR(255), classification_value VARCHAR(255));
COMMENT ON TABLE classification is '储存分类信息';
COMMENT ON COLUMN classifier_id is '自增主键';
COMMENT ON COLUMN classifier_identifier is '分类标识,格式为procedure_identifier:classification';
COMMENT ON COLUMN classifier_definition is '分类定义';
COMMENT ON COLUMN classifier_label is '分类标签';
COMMENT ON COLUMN classifier_value is '分类值';

CREATE TABLE characteristic(characteristic_id SERIAL NOT NULL PRIMARY KEY, characteristic_identifier VARCHAR(255) NOT NULL UNIQUE, characteristic_name VARCHAR(255), characteristic_label VARCHAR(45), characteristic_value VARCHAR(255));
COMMENT ON TABLE characteristic is '存储特征属性';
COMMENT ON COLUMN characteristic_id is '自增主键';
COMMENT ON COLUMN characteristic_identifier is '标识,格式为procedure_identifier:characteristic';
COMMENT ON COLUMN characteristic_name is '特征名称';
COMMENT ON COLUMN characteristic_label is '特征标签';
COMMENT ON COLUMN characteristic_value is '特征值';

CREATE TABLE capability (capability_id SERIAL NOT NULL PRIMARY KEY, capability_identifier VARCHAR(255) NOT NULL UNIQUE, capability_name VARCHAR(255), capability_label VARCHAR(45), capability_definition VARCHAR(255), capability_value VARCHAR(255));
COMMENT ON TABLE capability is '存储能力信息';
COMMENT ON COLUMN capability_id is '自增主键';
COMMENT ON COLUMN capability_identifier is '标识,格式为procedure_identifier:capability';
COMMENT ON COLUMN capability_name is '能力名称';
COMMENT ON COLUMN capability_label is '能力标签';
COMMENT ON COLUMN capability_value is '能力值';

CREATE TABLE address (address_id SERIAL NOT NULL PRIMARY KEY, address_identifier VARCHAR(255) NOT NULL UNIQUE, address_deliverypoint VARCHAR(255), address_city VARCHAR(255), address_administrativearea VARCHAR(255), address_postalcode VARCHAR(45), address_country VARCHAR(45), address_electronicMailAddress VARCHAR(255));
COMMENT ON TABLE address is '存储联系人的地址信息';

CREATE TABLE telephone (telephone_id SERIAL NOT NULL PRIMARY KEY, telephone_identifier VARCHAR(255) NOT NULL UNIQUE, telephone_voice VARCHAR(255), telephone_facsimile VARCHAR(255));
COMMENT ON TABLE telephone is '存储联系人的电话信息';

CREATE TABLE contact (contact_id SERIAL NOT NULL PRIMARY KEY, contact_identifier VARCHAR(255) NOT NULL UNIQUE, contact_individualname VARCHAR(255), contact_positionname VARCHAR(255), contact_organizationname VARCHAR(255), contact_role VARCHAR(45));
COMMENT ON TABLE contact is '存储联系人信息，自动关联地址和电话信息';

CREATE TABLE event (event_id SERIAL NOT NULL PRIMARY KEY, event_identifier VARCHAR(255) NOT NULL UNIQUE, event_label VARCHAR(255), event_documenturl VARCHAR(255), event_time TIMESTAMP);
COMMENT ON TABLE event is '存储历史信息';

CREATE TABLE position (position_id SERIAL NOT NULL PRIMARY KEY, position_identifier VARCHAR(255) NOT NULL UNIQUE, position_longitude REAL, position_latitude REAL, position_altitude REAL);
COMMENT ON TABLE position is '存储传感器精确位置信息';

CREATE TABLE validtime (validtime_id SERIAL NOT NULL PRIMARY KEY, validtime_identifier VARCHAR(255) NOT NULL UNIQUE, validtime_begin TIMESTAMP, validtime_end TIMESTAMP);
COMMENT ON TABLE validtime is '存储传感器的有效作用时间';

-- 观测数据存储
CREATE TABLE feature_of_interest (feature_of_interest_id VARCHAR(255) PRIMARY KEY, feature_of_interest_name VARCHAR(255), feature_of_interest_description TEXT, geom GEOMETRY);
COMMENT ON TABLE feature_of_interest is '表示感兴趣的观测区域并存储相关观测区域的特征信息，如观测区域的形状、相关描述信息等，例如观测的取样地点。';

CREATE TABLE observation (observation_id VARCHAR(255) PRIMARY KEY, observation_time TIMESTAMP, procedure_id VARCHAR(255), feature_of_interest_id VARCHAR(255), phenomenon_id VARCHAR(255), observation_value TEXT);
COMMENT ON TABLE observation is '表示传感观测信息表，记录传感器观测到的数据，如观测的值，传感器 id，时间，地理位置信息，观测现象类型，数据等，该表即我们所知的记录相关传感器观测数据的观测数据表。';

CREATE TABLE offering (offering_id VARCHAR(255) PRIMARY KEY, offering_name VARCHAR(255), min_time TIMESTAMP, max_time TIMESTAMP);
COMMENT ON TABLE offering is '表示观测提供信息表，记录 SOS 提供所有者的观测提供，包括观测提供的标识，名称等。观测提供也就是观测的类别表示我们所需记录的观测类型，如海拔，水速等。';

CREATE TABLE phenomenon (phenomenon_id VARCHAR(255) PRIMARY KEY, phenomenon_description TEXT, unit VARCHAR(45));
COMMENT ON TABLE phenomenon is '现象表，记录能被观测和测量的现象的信息，包含观测现象标识符、观测现象描述、观测现象的测量单位类型、观测所得相关的值类型等.';

CREATE TABLE proc_foi (procedure_id VARCHAR(255) PRIMARY KEY, feature_of_interest_id VARCHAR(255));
COMMENT ON TABLE proc_foi is '';

CREATE TABLE phen_off (phenomenon_id VARCHAR(255) PRIMARY KEY, offering_id VARCHAR(255));
COMMENT ON TABLE phen_off is '';

CREATE TABLE foi_off (feature_of_interest_id VARCHAR(255) PRIMARY KEY, offering_id VARCHAR(255));
COMMENT ON TABLE foi_off is '';

CREATE TABLE proc_phen (procedure_id VARCHAR(255) PRIMARY KEY, phenomenon_id VARCHAR(255));
COMMENT ON TABLE proc_phen is '';

CREATE TABLE proc_off (procedure_id VARCHAR(255) PRIMARY KEY, offering_id VARCHAR(255));
COMMENT ON TABLE proc_off is '';

-- Truncate Table
TRUNCATE TABLE address;
TRUNCATE TABLE capability;
TRUNCATE TABLE characteristic;
TRUNCATE TABLE classification;
TRUNCATE TABLE contact;
TRUNCATE TABLE event;
TRUNCATE TABLE identification;
TRUNCATE TABLE keyword;
TRUNCATE TABLE position;
TRUNCATE TABLE procedure;
TRUNCATE TABLE telephone;
TRUNCATE TABLE validtime;

TRUNCATE TABLE feature_of_interest;
TRUNCATE TABLE observation;
TRUNCATE TABLE offering;
TRUNCATE TABLE phenomenon;
TRUNCATE TABLE proc_foi;
TRUNCATE TABLE phen_off;
TRUNCATE TABLE foi_off;
TRUNCATE TABLE proc_phen;
TRUNCATE TABLE proc_off;

-- Drop table
DROP TABLE address;
DROP TABLE capability;
DROP TABLE characteristic;
DROP TABLE classification;
DROP TABLE contact;
DROP TABLE event;
DROP TABLE identification;
DROP TABLE keyword;
DROP TABLE position;
DROP TABLE procedure;
DROP TABLE telephone;
DROP TABLE validtime;

DROP TABLE feature_of_interest;
DROP TABLE observation;
DROP TABLE offering;
DROP TABLE phenomenon;
DROP TABLE proc_foi;
DROP TABLE phen_off;
DROP TABLE foi_off;
DROP TABLE proc_phen;
DROP TABLE proc_off;




-- 数据库修改
CREATE TABLE procedure (procedure_id VARCHAR(255) NOT NULL PRIMARY KEY, procedure_name VARCHAR(255), procedure_description TEXT, description_format VARCHAR(255) NOT NULL, description_file VARCHAR(255) NOT NULL);

CREATE TABLE offering (offering_id VARCHAR(255) PRIMARY KEY, offering_name VARCHAR(255), procedure_id VARCHAR(255), observable_property TEXT);



CREATE TABLE feature_of_interest (feature_of_interest_id VARCHAR(255) PRIMARY KEY, feature_of_interest_name VARCHAR(255), feature_of_interest_description TEXT, geom GEOMETRY);

CREATE TABLE observation (observation_id VARCHAR(255) PRIMARY KEY, observation_description TEXT, observation_time TIMESTAMP, procedure_id VARCHAR(255), offering_id VARCHAR(255), feature_of_interest_id VARCHAR(255), observed_property VARCHAR(255), observation_value TEXT);



CREATE TABLE phenomenon (phenomenon_id VARCHAR(255) PRIMARY KEY, phenomenon_description TEXT, unit VARCHAR(45));

-- Truncate Table
TRUNCATE TABLE procedure;
TRUNCATE TABLE feature_of_interest;
TRUNCATE TABLE observation;
TRUNCATE TABLE offering;

-- Drop table
DROP TABLE procedure;
DROP TABLE feature_of_interest;
DROP TABLE observation;
DROP TABLE offering;
