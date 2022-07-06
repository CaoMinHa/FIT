prompt
prompt Creating table APBALANCE
prompt ========================
prompt
create table APBALANCE
(
  id                VARCHAR2(50) not null,
  corporation_code  VARCHAR2(50),
  year              VARCHAR2(50),
  period            VARCHAR2(50),
  voucher           VARCHAR2(50),
  invoice_date      VARCHAR2(50),
  doc_date          VARCHAR2(50),
  due_date          VARCHAR2(50),
  invoice_no        VARCHAR2(50),
  supplier          VARCHAR2(50),
  supplier_type     VARCHAR2(50),
  acceptance        VARCHAR2(50),
  category          VARCHAR2(50),
  currency          VARCHAR2(50),
  src_balance       VARCHAR2(50),
  curr_balance      VARCHAR2(50),
  rate              VARCHAR2(50),
  curr_rate         VARCHAR2(50),
  overdue_days      VARCHAR2(50),
  aging             VARCHAR2(50),
  department        VARCHAR2(50),
  payment_condition VARCHAR2(200),
  summary           VARCHAR2(300)
)
;

prompt
prompt Creating table BIEERATE_ACCOUNT
prompt ===============================
prompt
create table BIEERATE_ACCOUNT
(
  id             INTEGER not null,
  label          NVARCHAR2(100) not null,
  parentid       INTEGER not null,
  parentlabel    NVARCHAR2(100),
  description    NVARCHAR2(256),
  userdefined1   NVARCHAR2(256),
  userdefined2   NVARCHAR2(256),
  userdefined3   NVARCHAR2(256),
  isshared       INTEGER not null,
  iscalculated   INTEGER not null,
  isconsolidated INTEGER not null,
  isicp          INTEGER not null,
  accounttype    NVARCHAR2(256) not null,
  isleaf         INTEGER not null
)
;
alter table BIEERATE_ACCOUNT
  add primary key (ID, PARENTID);

prompt
prompt Creating table BIEERATE_CUSTOM1
prompt ===============================
prompt
create table BIEERATE_CUSTOM1
(
  id           INTEGER not null,
  label        NVARCHAR2(100) not null,
  parentid     INTEGER not null,
  parentlabel  NVARCHAR2(100),
  description  NVARCHAR2(256),
  userdefined1 NVARCHAR2(256),
  userdefined2 NVARCHAR2(256),
  userdefined3 NVARCHAR2(256),
  isshared     INTEGER not null,
  iscalculated INTEGER not null,
  switchsign   INTEGER not null,
  switchtype   INTEGER not null,
  aggweight    FLOAT not null,
  isleaf       INTEGER not null
)
;
alter table BIEERATE_CUSTOM1
  add primary key (ID, PARENTID);

prompt
prompt Creating table BIEERATE_CUSTOM2
prompt ===============================
prompt
create table BIEERATE_CUSTOM2
(
  id           INTEGER not null,
  label        NVARCHAR2(100) not null,
  parentid     INTEGER not null,
  parentlabel  NVARCHAR2(100),
  description  NVARCHAR2(256),
  userdefined1 NVARCHAR2(256),
  userdefined2 NVARCHAR2(256),
  userdefined3 NVARCHAR2(256),
  isshared     INTEGER not null,
  iscalculated INTEGER not null,
  switchsign   INTEGER not null,
  switchtype   INTEGER not null,
  aggweight    FLOAT not null,
  isleaf       INTEGER not null
)
;
alter table BIEERATE_CUSTOM2
  add primary key (ID, PARENTID);

prompt
prompt Creating table BIEERATE_CUSTOM3
prompt ===============================
prompt
create table BIEERATE_CUSTOM3
(
  id           INTEGER not null,
  label        NVARCHAR2(100) not null,
  parentid     INTEGER not null,
  parentlabel  NVARCHAR2(100),
  description  NVARCHAR2(256),
  userdefined1 NVARCHAR2(256),
  userdefined2 NVARCHAR2(256),
  userdefined3 NVARCHAR2(256),
  isshared     INTEGER not null,
  iscalculated INTEGER not null,
  switchsign   INTEGER not null,
  switchtype   INTEGER not null,
  aggweight    FLOAT not null,
  isleaf       INTEGER not null
)
;
alter table BIEERATE_CUSTOM3
  add primary key (ID, PARENTID);

prompt
prompt Creating table BIEERATE_CUSTOM4
prompt ===============================
prompt
create table BIEERATE_CUSTOM4
(
  id           INTEGER not null,
  label        NVARCHAR2(100) not null,
  parentid     INTEGER not null,
  parentlabel  NVARCHAR2(100),
  description  NVARCHAR2(256),
  userdefined1 NVARCHAR2(256),
  userdefined2 NVARCHAR2(256),
  userdefined3 NVARCHAR2(256),
  isshared     INTEGER not null,
  iscalculated INTEGER not null,
  switchsign   INTEGER not null,
  switchtype   INTEGER not null,
  aggweight    FLOAT not null,
  isleaf       INTEGER not null
)
;
alter table BIEERATE_CUSTOM4
  add primary key (ID, PARENTID);

prompt
prompt Creating table BIEERATE_ENTITY
prompt ==============================
prompt
create table BIEERATE_ENTITY
(
  id              INTEGER not null,
  label           NVARCHAR2(100) not null,
  parentid        INTEGER not null,
  parentlabel     NVARCHAR2(100),
  description     NVARCHAR2(256),
  userdefined1    NVARCHAR2(256),
  userdefined2    NVARCHAR2(256),
  userdefined3    NVARCHAR2(256),
  isshared        INTEGER not null,
  isicp           INTEGER not null,
  defaultcurrency INTEGER not null,
  isleaf          INTEGER not null
)
;
alter table BIEERATE_ENTITY
  add primary key (ID, PARENTID);

prompt
prompt Creating table BIEERATE_FACT
prompt ============================
prompt
create table BIEERATE_FACT
(
  scenarioid INTEGER not null,
  yearid     INTEGER not null,
  periodid   INTEGER not null,
  viewid     INTEGER not null,
  entityid   INTEGER not null,
  parentid   INTEGER not null,
  valueid    INTEGER not null,
  accountid  INTEGER not null,
  icpid      INTEGER not null,
  custom1id  INTEGER not null,
  custom2id  INTEGER not null,
  custom3id  INTEGER not null,
  custom4id  INTEGER not null,
  ddata      FLOAT not null
)
;

prompt
prompt Creating table BIEERATE_ICP
prompt ===========================
prompt
create table BIEERATE_ICP
(
  id          INTEGER not null,
  label       NVARCHAR2(100) not null,
  parentid    INTEGER not null,
  parentlabel NVARCHAR2(100),
  description NVARCHAR2(256),
  isshared    INTEGER not null,
  isleaf      INTEGER not null
)
;
alter table BIEERATE_ICP
  add primary key (ID, PARENTID);

prompt
prompt Creating table BIEERATE_PARENT
prompt ==============================
prompt
create table BIEERATE_PARENT
(
  id              INTEGER not null,
  label           NVARCHAR2(100) not null,
  parentid        INTEGER not null,
  parentlabel     NVARCHAR2(100),
  description     NVARCHAR2(256),
  userdefined1    NVARCHAR2(256),
  userdefined2    NVARCHAR2(256),
  userdefined3    NVARCHAR2(256),
  isshared        INTEGER not null,
  isicp           INTEGER not null,
  defaultcurrency INTEGER not null,
  isleaf          INTEGER not null
)
;
alter table BIEERATE_PARENT
  add primary key (ID, PARENTID);

prompt
prompt Creating table BIEERATE_PERIOD
prompt ==============================
prompt
create table BIEERATE_PERIOD
(
  id          INTEGER not null,
  label       NVARCHAR2(100) not null,
  parentid    INTEGER not null,
  parentlabel NVARCHAR2(100),
  description NVARCHAR2(256),
  isshared    INTEGER not null,
  isleaf      INTEGER not null
)
;
alter table BIEERATE_PERIOD
  add primary key (ID, PARENTID);

prompt
prompt Creating table BIEERATE_SCENARIO
prompt ================================
prompt
create table BIEERATE_SCENARIO
(
  id           INTEGER not null,
  label        NVARCHAR2(100) not null,
  parentid     INTEGER not null,
  parentlabel  NVARCHAR2(100),
  description  NVARCHAR2(256),
  userdefined1 NVARCHAR2(256),
  userdefined2 NVARCHAR2(256),
  userdefined3 NVARCHAR2(256),
  isshared     INTEGER not null,
  defaultview  INTEGER not null,
  isleaf       INTEGER not null
)
;
alter table BIEERATE_SCENARIO
  add primary key (ID, PARENTID);

prompt
prompt Creating table BIEERATE_VALUE
prompt =============================
prompt
create table BIEERATE_VALUE
(
  id          INTEGER not null,
  label       NVARCHAR2(100) not null,
  parentid    INTEGER not null,
  parentlabel NVARCHAR2(100),
  description NVARCHAR2(256),
  isshared    INTEGER not null,
  isleaf      INTEGER not null
)
;
alter table BIEERATE_VALUE
  add primary key (ID, PARENTID);

prompt
prompt Creating table BIEERATE_VIEW
prompt ============================
prompt
create table BIEERATE_VIEW
(
  id          INTEGER not null,
  label       NVARCHAR2(100) not null,
  parentid    INTEGER not null,
  parentlabel NVARCHAR2(100),
  description NVARCHAR2(256),
  isshared    INTEGER not null,
  isleaf      INTEGER not null
)
;
alter table BIEERATE_VIEW
  add primary key (ID, PARENTID);

prompt
prompt Creating table BIEERATE_YEAR
prompt ============================
prompt
create table BIEERATE_YEAR
(
  id          INTEGER not null,
  label       NVARCHAR2(100) not null,
  parentid    INTEGER not null,
  parentlabel NVARCHAR2(100),
  description NVARCHAR2(256),
  isshared    INTEGER not null,
  isleaf      INTEGER not null
)
;
alter table BIEERATE_YEAR
  add primary key (ID, PARENTID);

prompt
prompt Creating table BI_ETL_LOG
prompt =========================
prompt
create table BI_ETL_LOG
(
  procname VARCHAR2(255) not null,
  line     INTEGER not null,
  proctime DATE default sysdate not null,
  proclog  VARCHAR2(2048)
)
;
create index IND_BI_ETL_LOG on BI_ETL_LOG (PROCNAME);

prompt
prompt Creating table BMRP_ISFORECAST
prompt ==============================
prompt
create table BMRP_ISFORECAST
(
  uuid                  VARCHAR2(100),
  legalentity           VARCHAR2(20),
  isyear                VARCHAR2(8),
  version               VARCHAR2(20),
  sbu                   VARCHAR2(60),
  itemclass             VARCHAR2(20),
  m1amt                 NUMBER,
  m1percent             NUMBER,
  m2amt                 NUMBER,
  m2percent             NUMBER,
  m3amt                 NUMBER,
  m3percent             NUMBER,
  m4amt                 NUMBER,
  m4percent             NUMBER,
  m5amt                 NUMBER,
  m5percent             NUMBER,
  m6amt                 NUMBER,
  m6percent             NUMBER,
  m7amt                 NUMBER,
  m7percent             NUMBER,
  m8amt                 NUMBER,
  m8percent             NUMBER,
  m9amt                 NUMBER,
  m9percent             NUMBER,
  m10amt                NUMBER,
  m10percent            NUMBER,
  m11amt                NUMBER,
  m11percent            NUMBER,
  m12amt                NUMBER,
  m12percent            NUMBER,
  q1amt                 NUMBER,
  q1percent             NUMBER,
  q2amt                 NUMBER,
  q2percent             NUMBER,
  q3amt                 NUMBER,
  q3percent             NUMBER,
  q4amt                 NUMBER,
  q4percent             NUMBER,
  yearcurrentaddamt     NUMBER,
  yearcurrentaddpercent NUMBER,
  yearamt               NUMBER,
  yearpercent           NUMBER,
  bestyearamt           NUMBER,
  bestyearpercent       NUMBER,
  lastyear1amt          NUMBER,
  lastyear1percent      NUMBER,
  lastyear2amt          NUMBER,
  lastyear2percent      NUMBER,
  datacreator           VARCHAR2(100),
  datacreatedate        VARCHAR2(100),
  lastmodifier          VARCHAR2(100),
  lastmodifieddate      VARCHAR2(100)
)
;

prompt
prompt Creating table BMRP_ITEMCLASS
prompt =============================
prompt
create table BMRP_ITEMCLASS
(
  uuid               VARCHAR2(100),
  orderid            NUMBER,
  classid            VARCHAR2(100),
  description        VARCHAR2(100),
  foreigndescription VARCHAR2(100),
  itembgcolor        VARCHAR2(100),
  contentbgcolor     VARCHAR2(100),
  datacreator        VARCHAR2(100),
  datacreatedate     VARCHAR2(100),
  lastmodifier       VARCHAR2(100),
  lastmodifieddate   VARCHAR2(100)
)
;

prompt
prompt Creating table COMMA4DIM
prompt ========================
prompt
create table COMMA4DIM
(
  scenario_code   VARCHAR2(80),
  year_code       VARCHAR2(80),
  period_code     VARCHAR2(80),
  view_code       VARCHAR2(80),
  entity_code     VARCHAR2(80),
  value_code      VARCHAR2(80),
  account_code    VARCHAR2(80),
  icp_code        VARCHAR2(80),
  product_code    VARCHAR2(80),
  customers_code  VARCHAR2(80),
  channel_code    VARCHAR2(80),
  unitsflows_code VARCHAR2(80),
  datavalue       NUMBER
)
;

prompt
prompt Creating table CUX_AR_AGING
prompt ===========================
prompt
create table CUX_AR_AGING
(
  id            VARCHAR2(50) default sys_guid() not null,
  year          VARCHAR2(4) not null,
  period        VARCHAR2(10) not null,
  entity_code   VARCHAR2(50) not null,
  entity_name   VARCHAR2(240),
  account_code  VARCHAR2(50) not null,
  account_name  VARCHAR2(240),
  cust_code     VARCHAR2(50),
  cust_name     VARCHAR2(240),
  isicp         VARCHAR2(1),
  soursys       VARCHAR2(50),
  lc            VARCHAR2(10) not null,
  lc_amount     NUMBER default 0 not null,
  lc_ag01       NUMBER default 0 not null,
  lc_ag02       NUMBER default 0 not null,
  lc_ag03       NUMBER default 0 not null,
  lc_ag04       NUMBER default 0 not null,
  lc_ag05       NUMBER default 0 not null,
  lc_ag06       NUMBER default 0 not null,
  lc_ag07       NUMBER default 0 not null,
  lc_ag08       NUMBER default 0 not null,
  lc_ag09       NUMBER default 0 not null,
  creation_date DATE,
  created_by    VARCHAR2(150)
)
;
comment on column CUX_AR_AGING.id
  is 'ID';
comment on column CUX_AR_AGING.year
  is '年份';
comment on column CUX_AR_AGING.period
  is '月份';
comment on column CUX_AR_AGING.entity_code
  is '公司编码';
comment on column CUX_AR_AGING.entity_name
  is '公司名称';
comment on column CUX_AR_AGING.account_code
  is '数据源科目编码';
comment on column CUX_AR_AGING.account_name
  is '科目名称';
comment on column CUX_AR_AGING.cust_code
  is '客商编码';
comment on column CUX_AR_AGING.cust_name
  is '客商名称';
comment on column CUX_AR_AGING.isicp
  is '是否关联方，Y/N';
comment on column CUX_AR_AGING.soursys
  is '数据来源';
comment on column CUX_AR_AGING.lc
  is '本币币种';
comment on column CUX_AR_AGING.lc_amount
  is '本币余额';
comment on column CUX_AR_AGING.lc_ag01
  is '0-30天';
comment on column CUX_AR_AGING.lc_ag02
  is '31-60天';
comment on column CUX_AR_AGING.lc_ag03
  is '61-90天';
comment on column CUX_AR_AGING.lc_ag04
  is '91-120天';
comment on column CUX_AR_AGING.lc_ag05
  is '121-150天';
comment on column CUX_AR_AGING.lc_ag06
  is '151-180天';
comment on column CUX_AR_AGING.lc_ag07
  is '181-210天';
comment on column CUX_AR_AGING.lc_ag08
  is '211-240天';
comment on column CUX_AR_AGING.lc_ag09
  is '241天以上';
comment on column CUX_AR_AGING.creation_date
  is '创建日期';
comment on column CUX_AR_AGING.created_by
  is '创建用户';
alter table CUX_AR_AGING
  add constraint PK_CUX_AR_AGING_ID primary key (ID);

prompt
prompt Creating table CUX_BAL
prompt ======================
prompt
create table CUX_BAL
(
  id            VARCHAR2(50) default sys_guid() not null,
  year          VARCHAR2(4) not null,
  period        VARCHAR2(10) not null,
  entity_code   VARCHAR2(50) not null,
  entity_name   VARCHAR2(240),
  account_code  VARCHAR2(50) not null,
  account_name  VARCHAR2(240),
  cust_code     VARCHAR2(50),
  cust_name     VARCHAR2(240),
  isicp         VARCHAR2(1),
  soursys       VARCHAR2(50),
  lc            VARCHAR2(10) not null,
  lc_bbal       NUMBER default 0 not null,
  lc_dr         NUMBER default 0 not null,
  lc_cr         NUMBER default 0 not null,
  lc_ebal       NUMBER default 0 not null,
  tc            VARCHAR2(10) not null,
  tc_bbal       NUMBER default 0 not null,
  tc_dr         NUMBER default 0 not null,
  tc_cr         NUMBER default 0 not null,
  tc_ebal       NUMBER default 0 not null,
  creation_date DATE,
  created_by    VARCHAR2(150)
)
;
comment on column CUX_BAL.id
  is 'ID';
comment on column CUX_BAL.year
  is '年份';
comment on column CUX_BAL.period
  is '月份';
comment on column CUX_BAL.entity_code
  is '公司编码';
comment on column CUX_BAL.entity_name
  is '公司名称';
comment on column CUX_BAL.account_code
  is '数据源科目编码';
comment on column CUX_BAL.account_name
  is '科目名称';
comment on column CUX_BAL.cust_code
  is '客商编码';
comment on column CUX_BAL.cust_name
  is '客商名称';
comment on column CUX_BAL.isicp
  is '是否关联方，Y/N';
comment on column CUX_BAL.soursys
  is '数据来源';
comment on column CUX_BAL.lc
  is '本币币种';
comment on column CUX_BAL.lc_bbal
  is '本币年初余额';
comment on column CUX_BAL.lc_dr
  is '本币借方累计';
comment on column CUX_BAL.lc_cr
  is '本币贷方累计';
comment on column CUX_BAL.lc_ebal
  is '本币期末余额';
comment on column CUX_BAL.tc
  is '交易币种';
comment on column CUX_BAL.tc_bbal
  is '交易币期初余额';
comment on column CUX_BAL.tc_dr
  is '交易币借方累计';
comment on column CUX_BAL.tc_cr
  is '交易币贷方累计';
comment on column CUX_BAL.tc_ebal
  is '交易币期末余额';
comment on column CUX_BAL.creation_date
  is '创建日期';
comment on column CUX_BAL.created_by
  is '创建用户';
alter table CUX_BAL
  add constraint PK_CUX_BAL_ID primary key (ID);

prompt
prompt Creating table CUX_DATAMAP
prompt ==========================
prompt
create table CUX_DATAMAP
(
  entity                VARCHAR2(64) not null,
  dimname               VARCHAR2(64) not null,
  srckey                VARCHAR2(64) not null,
  srcdesc               VARCHAR2(240),
  targkey               VARCHAR2(64) not null,
  targdesc              VARCHAR2(240),
  whereclausetype       VARCHAR2(240),
  whereclausevalue      VARCHAR2(240),
  changesign            VARCHAR2(1) default 'F' not null,
  agecalc               VARCHAR2(1) default 'F',
  datakey               NUMBER(10),
  tdatamaptype          VARCHAR2(64) default 'ERP' not null,
  system_generated_flag VARCHAR2(1),
  rule_id               NUMBER(15),
  creation_date         DATE,
  created_by            VARCHAR2(64),
  id                    VARCHAR2(50) default sys_guid() not null
)
;
comment on column CUX_DATAMAP.entity
  is '公司编码';
comment on column CUX_DATAMAP.dimname
  is '维度名称，科目：ACCOUNT，公司：ENTITY，关联方：ICP，自定义：UD';
comment on column CUX_DATAMAP.srckey
  is '映射源值';
comment on column CUX_DATAMAP.srcdesc
  is '映射源值名称';
comment on column CUX_DATAMAP.targkey
  is '映射目标值';
comment on column CUX_DATAMAP.targdesc
  is '映射目标名称';
comment on column CUX_DATAMAP.whereclausetype
  is '条件类型';
comment on column CUX_DATAMAP.whereclausevalue
  is '条件子句';
comment on column CUX_DATAMAP.changesign
  is '是否变号(''T''--是,''F''--否)';
comment on column CUX_DATAMAP.agecalc
  is '計算賬齡(''T''--是,''F''--否)';
comment on column CUX_DATAMAP.creation_date
  is '创建日期';
comment on column CUX_DATAMAP.created_by
  is '创建用户';
comment on column CUX_DATAMAP.id
  is 'ID';
create index IX_CUX_DATAMAP on CUX_DATAMAP (ENTITY);
create unique index UK_CUX_DATAMAP on CUX_DATAMAP (ENTITY, DIMNAME, TDATAMAPTYPE, SRCKEY);
alter table CUX_DATAMAP
  add constraint PK_CUX_DATAMAP_ID primary key (ID);

prompt
prompt Creating table CUX_LOOKUP_VALUES
prompt ================================
prompt
create table CUX_LOOKUP_VALUES
(
  lookup_type      VARCHAR2(150) not null,
  lookup_code      VARCHAR2(150) not null,
  meaning          VARCHAR2(150) not null,
  description      VARCHAR2(240),
  enabled          VARCHAR2(30),
  creation_date    DATE,
  created_by       VARCHAR2(150),
  last_update_date DATE,
  last_updated_by  VARCHAR2(150)
)
;

prompt
prompt Creating table CUX_PA_RP
prompt ========================
prompt
create table CUX_PA_RP
(
  id            VARCHAR2(50) default sys_guid() not null,
  year          VARCHAR2(4) not null,
  period        VARCHAR2(10) not null,
  entity_code   VARCHAR2(50) not null,
  entity_name   VARCHAR2(240),
  account_code  VARCHAR2(50) not null,
  account_name  VARCHAR2(240),
  cust_code     VARCHAR2(50),
  cust_name     VARCHAR2(240),
  isicp         VARCHAR2(1),
  soursys       VARCHAR2(50),
  purchase_item VARCHAR2(150),
  purchase_date DATE,
  lc            VARCHAR2(10) not null,
  lc_amount     NUMBER default 0 not null,
  tc            VARCHAR2(10) not null,
  tc_amount     NUMBER default 0 not null,
  creation_date DATE,
  created_by    VARCHAR2(150)
)
;
comment on column CUX_PA_RP.id
  is 'ID';
comment on column CUX_PA_RP.year
  is '年份';
comment on column CUX_PA_RP.period
  is '月份';
comment on column CUX_PA_RP.entity_code
  is '公司编码';
comment on column CUX_PA_RP.entity_name
  is '公司名称';
comment on column CUX_PA_RP.account_code
  is '数据源科目编码';
comment on column CUX_PA_RP.account_name
  is '科目名称';
comment on column CUX_PA_RP.cust_code
  is '客商编码';
comment on column CUX_PA_RP.cust_name
  is '客商名称';
comment on column CUX_PA_RP.isicp
  is '是否关联方，Y/N';
comment on column CUX_PA_RP.soursys
  is '数据来源';
comment on column CUX_PA_RP.purchase_item
  is '购入项目';
comment on column CUX_PA_RP.purchase_date
  is '购入日期';
comment on column CUX_PA_RP.lc
  is '本币币种';
comment on column CUX_PA_RP.lc_amount
  is '本币金额';
comment on column CUX_PA_RP.tc
  is '交易币种';
comment on column CUX_PA_RP.tc_amount
  is '交易币金额';
comment on column CUX_PA_RP.creation_date
  is '创建日期';
comment on column CUX_PA_RP.created_by
  is '创建用户';
alter table CUX_PA_RP
  add constraint PK_CUX_PA_RP_ID primary key (ID);

prompt
prompt Creating table CUX_SA_RP
prompt ========================
prompt
create table CUX_SA_RP
(
  id            VARCHAR2(50) default sys_guid() not null,
  year          VARCHAR2(4) not null,
  period        VARCHAR2(10) not null,
  entity_code   VARCHAR2(50) not null,
  entity_name   VARCHAR2(240),
  account_code  VARCHAR2(50) not null,
  account_name  VARCHAR2(240),
  cust_code     VARCHAR2(50),
  cust_name     VARCHAR2(240),
  isicp         VARCHAR2(1),
  soursys       VARCHAR2(50),
  sales_item    VARCHAR2(150),
  sales_date    VARCHAR2(15),
  lc            VARCHAR2(10) not null,
  lc_price      NUMBER default 0 not null,
  lc_book       NUMBER default 0 not null,
  lc_loss       NUMBER default 0 not null,
  tc            VARCHAR2(10) not null,
  tc_amount     NUMBER default 0 not null,
  creation_date DATE,
  created_by    VARCHAR2(150)
)
;
comment on column CUX_SA_RP.id
  is 'ID';
comment on column CUX_SA_RP.year
  is '年份';
comment on column CUX_SA_RP.period
  is '月份';
comment on column CUX_SA_RP.entity_code
  is '公司编码';
comment on column CUX_SA_RP.entity_name
  is '公司名称';
comment on column CUX_SA_RP.account_code
  is '数据源科目编码';
comment on column CUX_SA_RP.account_name
  is '科目名称';
comment on column CUX_SA_RP.cust_code
  is '客商编码';
comment on column CUX_SA_RP.cust_name
  is '客商名称';
comment on column CUX_SA_RP.isicp
  is '是否关联方，Y/N';
comment on column CUX_SA_RP.soursys
  is '数据来源';
comment on column CUX_SA_RP.sales_item
  is '出售项目';
comment on column CUX_SA_RP.sales_date
  is '出售日期';
comment on column CUX_SA_RP.lc
  is '本币币种';
comment on column CUX_SA_RP.lc_price
  is '售价';
comment on column CUX_SA_RP.lc_book
  is '账面价值';
comment on column CUX_SA_RP.lc_loss
  is '出售损益';
comment on column CUX_SA_RP.tc
  is '交易币种';
comment on column CUX_SA_RP.tc_amount
  is '交易币金额';
comment on column CUX_SA_RP.creation_date
  is '创建日期';
comment on column CUX_SA_RP.created_by
  is '创建用户';
alter table CUX_SA_RP
  add constraint PK_CUX_SA_RP_ID primary key (ID);

prompt
prompt Creating table CUX_TRANS_RP
prompt ===========================
prompt
create table CUX_TRANS_RP
(
  id            VARCHAR2(50) default sys_guid() not null,
  year          VARCHAR2(4) not null,
  period        VARCHAR2(10) not null,
  entity_code   VARCHAR2(50) not null,
  entity_name   VARCHAR2(240),
  account_code  VARCHAR2(50) not null,
  account_name  VARCHAR2(240),
  cust_code     VARCHAR2(50),
  cust_name     VARCHAR2(240),
  isicp         VARCHAR2(1),
  soursys       VARCHAR2(50),
  trans_nature  VARCHAR2(150),
  lc            VARCHAR2(10) not null,
  lc_amount     NUMBER default 0 not null,
  tc            VARCHAR2(10) not null,
  tc_amount     NUMBER default 0 not null,
  creation_date DATE,
  created_by    VARCHAR2(150)
)
;
comment on column CUX_TRANS_RP.id
  is 'ID';
comment on column CUX_TRANS_RP.year
  is '年份';
comment on column CUX_TRANS_RP.period
  is '月份';
comment on column CUX_TRANS_RP.entity_code
  is '公司编码';
comment on column CUX_TRANS_RP.entity_name
  is '公司名称';
comment on column CUX_TRANS_RP.account_code
  is '数据源科目编码';
comment on column CUX_TRANS_RP.account_name
  is '科目名称';
comment on column CUX_TRANS_RP.cust_code
  is '客商编码';
comment on column CUX_TRANS_RP.cust_name
  is '客商名称';
comment on column CUX_TRANS_RP.isicp
  is '是否关联方，Y/N';
comment on column CUX_TRANS_RP.soursys
  is '数据来源';
comment on column CUX_TRANS_RP.trans_nature
  is '性质';
comment on column CUX_TRANS_RP.lc
  is '本币币种';
comment on column CUX_TRANS_RP.lc_amount
  is '本币金额';
comment on column CUX_TRANS_RP.tc
  is '交易币种';
comment on column CUX_TRANS_RP.tc_amount
  is '交易币金额';
comment on column CUX_TRANS_RP.creation_date
  is '创建日期';
comment on column CUX_TRANS_RP.created_by
  is '创建用户';
alter table CUX_TRANS_RP
  add constraint PK_CUX_TRANS_RP_ID primary key (ID);

prompt
prompt Creating table DIM_TIME
prompt =======================
prompt
create table DIM_TIME
(
  date_id          DATE not null,
  data_name        VARCHAR2(255),
  year_id          NUMBER(10),
  month_id         NUMBER(10),
  month_name       VARCHAR2(255),
  quarter_name     VARCHAR2(255),
  week_inyear_name VARCHAR2(255),
  weekday_name     VARCHAR2(255),
  creator_name     VARCHAR2(255),
  createdate       DATE,
  update_name      VARCHAR2(255),
  last_updatedate  DATE,
  valid_flag       NUMBER(10) default 1
)
;
comment on table DIM_TIME
  is '时间维表';
comment on column DIM_TIME.date_id
  is '日期ID';
comment on column DIM_TIME.data_name
  is '日期名称';
comment on column DIM_TIME.year_id
  is '年份ID';
comment on column DIM_TIME.month_id
  is '月份ID';
comment on column DIM_TIME.month_name
  is '月份名称';
comment on column DIM_TIME.quarter_name
  is '季度名称';
comment on column DIM_TIME.week_inyear_name
  is '年周名称';
comment on column DIM_TIME.weekday_name
  is '星期';
comment on column DIM_TIME.creator_name
  is '创建人';
comment on column DIM_TIME.createdate
  is '创建日期';
comment on column DIM_TIME.update_name
  is '更新人';
comment on column DIM_TIME.last_updatedate
  is '最后修改时间';
comment on column DIM_TIME.valid_flag
  is '有效标记 1-有效；-1-无效';
alter table DIM_TIME
  add constraint PK_DIM_TIME_DATE_ID primary key (DATE_ID);

prompt
prompt Creating table E$_NOT_WRITEOFF_DETAIL_NEW
prompt =========================================
prompt
create table E$_NOT_WRITEOFF_DETAIL_NEW
(
  odi_row_id           UROWID(4000),
  odi_err_type         VARCHAR2(1 CHAR),
  odi_err_mess         VARCHAR2(250 CHAR),
  odi_check_date       DATE,
  id                   VARCHAR2(50),
  corporationcode      VARCHAR2(50),
  year                 VARCHAR2(50),
  period               VARCHAR2(50),
  ap_arnumber          VARCHAR2(50),
  voucher              VARCHAR2(50),
  recorddate           VARCHAR2(50),
  enddate              VARCHAR2(50),
  merchantcode         VARCHAR2(50),
  suppliercategory     VARCHAR2(50),
  itemcode             VARCHAR2(50),
  transactioncurrency  VARCHAR2(50),
  originalbalance      VARCHAR2(50),
  currentbalance       VARCHAR2(50),
  currentassessbalance VARCHAR2(50),
  overdueday           VARCHAR2(50),
  apaccount            VARCHAR2(50),
  departmentcode       VARCHAR2(50),
  summary              VARCHAR2(50),
  odi_origin           VARCHAR2(100 CHAR),
  odi_cons_name        VARCHAR2(128 CHAR),
  odi_cons_type        VARCHAR2(2 CHAR),
  odi_pk               VARCHAR2(32 CHAR) not null,
  odi_sess_no          VARCHAR2(19 CHAR)
)
;
create index E$_NOT_WRITEOFF_DETAIL_NEW_IDX on E$_NOT_WRITEOFF_DETAIL_NEW (ODI_ROW_ID);
alter table E$_NOT_WRITEOFF_DETAIL_NEW
  add primary key (ODI_PK);

prompt
prompt Creating table E$_ORA_INC_FILE
prompt ==============================
prompt
create table E$_ORA_INC_FILE
(
  odi_row_id     UROWID(4000),
  odi_err_type   VARCHAR2(1 CHAR),
  odi_err_mess   VARCHAR2(250 CHAR),
  odi_check_date DATE,
  inc01          VARCHAR2(200),
  inc02          VARCHAR2(200),
  inc03          VARCHAR2(200),
  inc04          NUMBER,
  inc05          VARCHAR2(200),
  inc06          VARCHAR2(200),
  inc07          VARCHAR2(200),
  inc08          VARCHAR2(200),
  inc09          VARCHAR2(200),
  attribute1     VARCHAR2(200),
  attribute2     VARCHAR2(200),
  attribute3     VARCHAR2(200),
  attribute4     VARCHAR2(200),
  attribute5     VARCHAR2(200),
  odi_origin     VARCHAR2(100 CHAR),
  odi_cons_name  VARCHAR2(128 CHAR),
  odi_cons_type  VARCHAR2(2 CHAR),
  odi_pk         VARCHAR2(32 CHAR) not null,
  odi_sess_no    VARCHAR2(19 CHAR)
)
;
create index E$_ORA_INC_FILE_IDX on E$_ORA_INC_FILE (ODI_ROW_ID);
alter table E$_ORA_INC_FILE
  add primary key (ODI_PK);

prompt
prompt Creating table FIT1HFMVAL_ACCOUNT
prompt =================================
prompt
create table FIT1HFMVAL_ACCOUNT
(
  id             INTEGER not null,
  label          NVARCHAR2(100) not null,
  parentid       INTEGER not null,
  parentlabel    NVARCHAR2(100),
  description    NVARCHAR2(256),
  userdefined1   NVARCHAR2(256),
  userdefined2   NVARCHAR2(256),
  userdefined3   NVARCHAR2(256),
  isshared       INTEGER not null,
  iscalculated   INTEGER not null,
  isconsolidated INTEGER not null,
  isicp          INTEGER not null,
  accounttype    NVARCHAR2(256) not null,
  isleaf         INTEGER not null
)
;
alter table FIT1HFMVAL_ACCOUNT
  add primary key (ID, PARENTID);

prompt
prompt Creating table FIT1HFMVAL_CUSTOM1
prompt =================================
prompt
create table FIT1HFMVAL_CUSTOM1
(
  id           INTEGER not null,
  label        NVARCHAR2(100) not null,
  parentid     INTEGER not null,
  parentlabel  NVARCHAR2(100),
  description  NVARCHAR2(256),
  userdefined1 NVARCHAR2(256),
  userdefined2 NVARCHAR2(256),
  userdefined3 NVARCHAR2(256),
  isshared     INTEGER not null,
  iscalculated INTEGER not null,
  switchsign   INTEGER not null,
  switchtype   INTEGER not null,
  aggweight    FLOAT not null,
  isleaf       INTEGER not null
)
;
alter table FIT1HFMVAL_CUSTOM1
  add primary key (ID, PARENTID);

prompt
prompt Creating table FIT1HFMVAL_CUSTOM2
prompt =================================
prompt
create table FIT1HFMVAL_CUSTOM2
(
  id           INTEGER not null,
  label        NVARCHAR2(100) not null,
  parentid     INTEGER not null,
  parentlabel  NVARCHAR2(100),
  description  NVARCHAR2(256),
  userdefined1 NVARCHAR2(256),
  userdefined2 NVARCHAR2(256),
  userdefined3 NVARCHAR2(256),
  isshared     INTEGER not null,
  iscalculated INTEGER not null,
  switchsign   INTEGER not null,
  switchtype   INTEGER not null,
  aggweight    FLOAT not null,
  isleaf       INTEGER not null
)
;
alter table FIT1HFMVAL_CUSTOM2
  add primary key (ID, PARENTID);

prompt
prompt Creating table FIT1HFMVAL_CUSTOM3
prompt =================================
prompt
create table FIT1HFMVAL_CUSTOM3
(
  id           INTEGER not null,
  label        NVARCHAR2(100) not null,
  parentid     INTEGER not null,
  parentlabel  NVARCHAR2(100),
  description  NVARCHAR2(256),
  userdefined1 NVARCHAR2(256),
  userdefined2 NVARCHAR2(256),
  userdefined3 NVARCHAR2(256),
  isshared     INTEGER not null,
  iscalculated INTEGER not null,
  switchsign   INTEGER not null,
  switchtype   INTEGER not null,
  aggweight    FLOAT not null,
  isleaf       INTEGER not null
)
;
alter table FIT1HFMVAL_CUSTOM3
  add primary key (ID, PARENTID);

prompt
prompt Creating table FIT1HFMVAL_CUSTOM4
prompt =================================
prompt
create table FIT1HFMVAL_CUSTOM4
(
  id           INTEGER not null,
  label        NVARCHAR2(100) not null,
  parentid     INTEGER not null,
  parentlabel  NVARCHAR2(100),
  description  NVARCHAR2(256),
  userdefined1 NVARCHAR2(256),
  userdefined2 NVARCHAR2(256),
  userdefined3 NVARCHAR2(256),
  isshared     INTEGER not null,
  iscalculated INTEGER not null,
  switchsign   INTEGER not null,
  switchtype   INTEGER not null,
  aggweight    FLOAT not null,
  isleaf       INTEGER not null
)
;
alter table FIT1HFMVAL_CUSTOM4
  add primary key (ID, PARENTID);

prompt
prompt Creating table FIT1HFMVAL_ENTITY
prompt ================================
prompt
create table FIT1HFMVAL_ENTITY
(
  id              INTEGER not null,
  label           NVARCHAR2(100) not null,
  parentid        INTEGER not null,
  parentlabel     NVARCHAR2(100),
  description     NVARCHAR2(256),
  userdefined1    NVARCHAR2(256),
  userdefined2    NVARCHAR2(256),
  userdefined3    NVARCHAR2(256),
  isshared        INTEGER not null,
  isicp           INTEGER not null,
  defaultcurrency INTEGER not null,
  isleaf          INTEGER not null
)
;
alter table FIT1HFMVAL_ENTITY
  add primary key (ID, PARENTID);

prompt
prompt Creating table FIT1HFMVAL_FACT
prompt ==============================
prompt
create table FIT1HFMVAL_FACT
(
  scenarioid INTEGER not null,
  yearid     INTEGER not null,
  periodid   INTEGER not null,
  viewid     INTEGER not null,
  entityid   INTEGER not null,
  parentid   INTEGER not null,
  valueid    INTEGER not null,
  accountid  INTEGER not null,
  icpid      INTEGER not null,
  custom1id  INTEGER not null,
  custom2id  INTEGER not null,
  custom3id  INTEGER not null,
  custom4id  INTEGER not null,
  ddata      FLOAT not null
)
;

prompt
prompt Creating table FIT1HFMVAL_ICP
prompt =============================
prompt
create table FIT1HFMVAL_ICP
(
  id          INTEGER not null,
  label       NVARCHAR2(100) not null,
  parentid    INTEGER not null,
  parentlabel NVARCHAR2(100),
  description NVARCHAR2(256),
  isshared    INTEGER not null,
  isleaf      INTEGER not null
)
;
alter table FIT1HFMVAL_ICP
  add primary key (ID, PARENTID);

prompt
prompt Creating table FIT1HFMVAL_PARENT
prompt ================================
prompt
create table FIT1HFMVAL_PARENT
(
  id              INTEGER not null,
  label           NVARCHAR2(100) not null,
  parentid        INTEGER not null,
  parentlabel     NVARCHAR2(100),
  description     NVARCHAR2(256),
  userdefined1    NVARCHAR2(256),
  userdefined2    NVARCHAR2(256),
  userdefined3    NVARCHAR2(256),
  isshared        INTEGER not null,
  isicp           INTEGER not null,
  defaultcurrency INTEGER not null,
  isleaf          INTEGER not null
)
;
alter table FIT1HFMVAL_PARENT
  add primary key (ID, PARENTID);

prompt
prompt Creating table FIT1HFMVAL_PERIOD
prompt ================================
prompt
create table FIT1HFMVAL_PERIOD
(
  id          INTEGER not null,
  label       NVARCHAR2(100) not null,
  parentid    INTEGER not null,
  parentlabel NVARCHAR2(100),
  description NVARCHAR2(256),
  isshared    INTEGER not null,
  isleaf      INTEGER not null
)
;
alter table FIT1HFMVAL_PERIOD
  add primary key (ID, PARENTID);

prompt
prompt Creating table FIT1HFMVAL_SCENARIO
prompt ==================================
prompt
create table FIT1HFMVAL_SCENARIO
(
  id           INTEGER not null,
  label        NVARCHAR2(100) not null,
  parentid     INTEGER not null,
  parentlabel  NVARCHAR2(100),
  description  NVARCHAR2(256),
  userdefined1 NVARCHAR2(256),
  userdefined2 NVARCHAR2(256),
  userdefined3 NVARCHAR2(256),
  isshared     INTEGER not null,
  defaultview  INTEGER not null,
  isleaf       INTEGER not null
)
;
alter table FIT1HFMVAL_SCENARIO
  add primary key (ID, PARENTID);

prompt
prompt Creating table FIT1HFMVAL_VALUE
prompt ===============================
prompt
create table FIT1HFMVAL_VALUE
(
  id          INTEGER not null,
  label       NVARCHAR2(100) not null,
  parentid    INTEGER not null,
  parentlabel NVARCHAR2(100),
  description NVARCHAR2(256),
  isshared    INTEGER not null,
  isleaf      INTEGER not null
)
;
alter table FIT1HFMVAL_VALUE
  add primary key (ID, PARENTID);

prompt
prompt Creating table FIT1HFMVAL_VIEW
prompt ==============================
prompt
create table FIT1HFMVAL_VIEW
(
  id          INTEGER not null,
  label       NVARCHAR2(100) not null,
  parentid    INTEGER not null,
  parentlabel NVARCHAR2(100),
  description NVARCHAR2(256),
  isshared    INTEGER not null,
  isleaf      INTEGER not null
)
;
alter table FIT1HFMVAL_VIEW
  add primary key (ID, PARENTID);

prompt
prompt Creating table FIT1HFMVAL_YEAR
prompt ==============================
prompt
create table FIT1HFMVAL_YEAR
(
  id          INTEGER not null,
  label       NVARCHAR2(100) not null,
  parentid    INTEGER not null,
  parentlabel NVARCHAR2(100),
  description NVARCHAR2(256),
  isshared    INTEGER not null,
  isleaf      INTEGER not null
)
;
alter table FIT1HFMVAL_YEAR
  add primary key (ID, PARENTID);

prompt
prompt Creating table FIT1_ACCOUNT
prompt ===========================
prompt
create table FIT1_ACCOUNT
(
  id             INTEGER not null,
  label          NVARCHAR2(100) not null,
  parentid       INTEGER not null,
  parentlabel    NVARCHAR2(100),
  description    NVARCHAR2(256),
  userdefined1   NVARCHAR2(256),
  userdefined2   NVARCHAR2(256),
  userdefined3   NVARCHAR2(256),
  isshared       INTEGER not null,
  iscalculated   INTEGER not null,
  isconsolidated INTEGER not null,
  isicp          INTEGER not null,
  accounttype    NVARCHAR2(256) not null,
  isleaf         INTEGER not null
)
;
alter table FIT1_ACCOUNT
  add primary key (ID, PARENTID);

prompt
prompt Creating table FIT1_CUSTOM1
prompt ===========================
prompt
create table FIT1_CUSTOM1
(
  id           INTEGER not null,
  label        NVARCHAR2(100) not null,
  parentid     INTEGER not null,
  parentlabel  NVARCHAR2(100),
  description  NVARCHAR2(256),
  userdefined1 NVARCHAR2(256),
  userdefined2 NVARCHAR2(256),
  userdefined3 NVARCHAR2(256),
  isshared     INTEGER not null,
  iscalculated INTEGER not null,
  switchsign   INTEGER not null,
  switchtype   INTEGER not null,
  aggweight    FLOAT not null,
  isleaf       INTEGER not null
)
;
alter table FIT1_CUSTOM1
  add primary key (ID, PARENTID);

prompt
prompt Creating table FIT1_CUSTOM2
prompt ===========================
prompt
create table FIT1_CUSTOM2
(
  id           INTEGER not null,
  label        NVARCHAR2(100) not null,
  parentid     INTEGER not null,
  parentlabel  NVARCHAR2(100),
  description  NVARCHAR2(256),
  userdefined1 NVARCHAR2(256),
  userdefined2 NVARCHAR2(256),
  userdefined3 NVARCHAR2(256),
  isshared     INTEGER not null,
  iscalculated INTEGER not null,
  switchsign   INTEGER not null,
  switchtype   INTEGER not null,
  aggweight    FLOAT not null,
  isleaf       INTEGER not null
)
;
alter table FIT1_CUSTOM2
  add primary key (ID, PARENTID);

prompt
prompt Creating table FIT1_CUSTOM3
prompt ===========================
prompt
create table FIT1_CUSTOM3
(
  id           INTEGER not null,
  label        NVARCHAR2(100) not null,
  parentid     INTEGER not null,
  parentlabel  NVARCHAR2(100),
  description  NVARCHAR2(256),
  userdefined1 NVARCHAR2(256),
  userdefined2 NVARCHAR2(256),
  userdefined3 NVARCHAR2(256),
  isshared     INTEGER not null,
  iscalculated INTEGER not null,
  switchsign   INTEGER not null,
  switchtype   INTEGER not null,
  aggweight    FLOAT not null,
  isleaf       INTEGER not null
)
;
alter table FIT1_CUSTOM3
  add primary key (ID, PARENTID);

prompt
prompt Creating table FIT1_CUSTOM4
prompt ===========================
prompt
create table FIT1_CUSTOM4
(
  id           INTEGER not null,
  label        NVARCHAR2(100) not null,
  parentid     INTEGER not null,
  parentlabel  NVARCHAR2(100),
  description  NVARCHAR2(256),
  userdefined1 NVARCHAR2(256),
  userdefined2 NVARCHAR2(256),
  userdefined3 NVARCHAR2(256),
  isshared     INTEGER not null,
  iscalculated INTEGER not null,
  switchsign   INTEGER not null,
  switchtype   INTEGER not null,
  aggweight    FLOAT not null,
  isleaf       INTEGER not null
)
;
alter table FIT1_CUSTOM4
  add primary key (ID, PARENTID);

prompt
prompt Creating table FIT1_ENTITY
prompt ==========================
prompt
create table FIT1_ENTITY
(
  id              INTEGER not null,
  label           NVARCHAR2(100) not null,
  parentid        INTEGER not null,
  parentlabel     NVARCHAR2(100),
  description     NVARCHAR2(256),
  userdefined1    NVARCHAR2(256),
  userdefined2    NVARCHAR2(256),
  userdefined3    NVARCHAR2(256),
  isshared        INTEGER not null,
  isicp           INTEGER not null,
  defaultcurrency INTEGER not null,
  isleaf          INTEGER not null
)
;
alter table FIT1_ENTITY
  add primary key (ID, PARENTID);

prompt
prompt Creating table FIT1_FACT
prompt ========================
prompt
create table FIT1_FACT
(
  scenarioid INTEGER not null,
  yearid     INTEGER not null,
  periodid   INTEGER not null,
  viewid     INTEGER not null,
  entityid   INTEGER not null,
  parentid   INTEGER not null,
  valueid    INTEGER not null,
  accountid  INTEGER not null,
  icpid      INTEGER not null,
  custom1id  INTEGER not null,
  custom2id  INTEGER not null,
  custom3id  INTEGER not null,
  custom4id  INTEGER not null,
  ddata      FLOAT not null
)
;

prompt
prompt Creating table FIT1_ICP
prompt =======================
prompt
create table FIT1_ICP
(
  id          INTEGER not null,
  label       NVARCHAR2(100) not null,
  parentid    INTEGER not null,
  parentlabel NVARCHAR2(100),
  description NVARCHAR2(256),
  isshared    INTEGER not null,
  isleaf      INTEGER not null
)
;
alter table FIT1_ICP
  add primary key (ID, PARENTID);

prompt
prompt Creating table FIT1_PARENT
prompt ==========================
prompt
create table FIT1_PARENT
(
  id              INTEGER not null,
  label           NVARCHAR2(100) not null,
  parentid        INTEGER not null,
  parentlabel     NVARCHAR2(100),
  description     NVARCHAR2(256),
  userdefined1    NVARCHAR2(256),
  userdefined2    NVARCHAR2(256),
  userdefined3    NVARCHAR2(256),
  isshared        INTEGER not null,
  isicp           INTEGER not null,
  defaultcurrency INTEGER not null,
  isleaf          INTEGER not null
)
;
alter table FIT1_PARENT
  add primary key (ID, PARENTID);

prompt
prompt Creating table FIT1_PERIOD
prompt ==========================
prompt
create table FIT1_PERIOD
(
  id          INTEGER not null,
  label       NVARCHAR2(100) not null,
  parentid    INTEGER not null,
  parentlabel NVARCHAR2(100),
  description NVARCHAR2(256),
  isshared    INTEGER not null,
  isleaf      INTEGER not null
)
;
alter table FIT1_PERIOD
  add primary key (ID, PARENTID);

prompt
prompt Creating table FIT1_SCENARIO
prompt ============================
prompt
create table FIT1_SCENARIO
(
  id           INTEGER not null,
  label        NVARCHAR2(100) not null,
  parentid     INTEGER not null,
  parentlabel  NVARCHAR2(100),
  description  NVARCHAR2(256),
  userdefined1 NVARCHAR2(256),
  userdefined2 NVARCHAR2(256),
  userdefined3 NVARCHAR2(256),
  isshared     INTEGER not null,
  defaultview  INTEGER not null,
  isleaf       INTEGER not null
)
;
alter table FIT1_SCENARIO
  add primary key (ID, PARENTID);

prompt
prompt Creating table FIT1_VALUE
prompt =========================
prompt
create table FIT1_VALUE
(
  id          INTEGER not null,
  label       NVARCHAR2(100) not null,
  parentid    INTEGER not null,
  parentlabel NVARCHAR2(100),
  description NVARCHAR2(256),
  isshared    INTEGER not null,
  isleaf      INTEGER not null
)
;
alter table FIT1_VALUE
  add primary key (ID, PARENTID);

prompt
prompt Creating table FIT1_VIEW
prompt ========================
prompt
create table FIT1_VIEW
(
  id          INTEGER not null,
  label       NVARCHAR2(100) not null,
  parentid    INTEGER not null,
  parentlabel NVARCHAR2(100),
  description NVARCHAR2(256),
  isshared    INTEGER not null,
  isleaf      INTEGER not null
)
;
alter table FIT1_VIEW
  add primary key (ID, PARENTID);

prompt
prompt Creating table FIT1_YEAR
prompt ========================
prompt
create table FIT1_YEAR
(
  id          INTEGER not null,
  label       NVARCHAR2(100) not null,
  parentid    INTEGER not null,
  parentlabel NVARCHAR2(100),
  description NVARCHAR2(256),
  isshared    INTEGER not null,
  isleaf      INTEGER not null
)
;
alter table FIT1_YEAR
  add primary key (ID, PARENTID);

prompt
prompt Creating table FIT_AP_BALANCE_INVOICE
prompt =====================================
prompt
create table FIT_AP_BALANCE_INVOICE
(
  id                VARCHAR2(50) not null,
  corporation_code  VARCHAR2(50) not null,
  year              VARCHAR2(4) not null,
  period            VARCHAR2(2) not null,
  document          VARCHAR2(100),
  summons           VARCHAR2(100),
  invoice           VARCHAR2(100),
  supplier          VARCHAR2(100),
  supplier_name     VARCHAR2(100),
  supplier_type     VARCHAR2(100),
  item_code         VARCHAR2(100),
  item_desc         VARCHAR2(100),
  currency          VARCHAR2(100),
  src_amount        VARCHAR2(100),
  src_tax           VARCHAR2(100),
  src_untax         VARCHAR2(100),
  currency_amount   VARCHAR2(100),
  currency_tax      VARCHAR2(100),
  currency_untax    VARCHAR2(100),
  exchange_rate     VARCHAR2(100),
  currency_examount VARCHAR2(100),
  currency_extax    VARCHAR2(100),
  currency_exuntax  VARCHAR2(100),
  doc_date          VARCHAR2(100),
  due_date          VARCHAR2(100),
  aging             VARCHAR2(100),
  condition         VARCHAR2(100),
  summary           VARCHAR2(300),
  note              VARCHAR2(300),
  department        VARCHAR2(100),
  borrow_item_code  VARCHAR2(100),
  borrow_item_desc  VARCHAR2(100)
)
;
comment on table FIT_AP_BALANCE_INVOICE
  is 'AP余额（發票）';
comment on column FIT_AP_BALANCE_INVOICE.corporation_code
  is '法人编码';
comment on column FIT_AP_BALANCE_INVOICE.year
  is '年';
comment on column FIT_AP_BALANCE_INVOICE.period
  is '期間';
comment on column FIT_AP_BALANCE_INVOICE.document
  is '帳款編號';
comment on column FIT_AP_BALANCE_INVOICE.summons
  is '傳票編號';
comment on column FIT_AP_BALANCE_INVOICE.invoice
  is '發票號碼';
comment on column FIT_AP_BALANCE_INVOICE.supplier
  is '供應商代码';
comment on column FIT_AP_BALANCE_INVOICE.supplier_name
  is '供應商名稱';
comment on column FIT_AP_BALANCE_INVOICE.supplier_type
  is '供應商類型';
comment on column FIT_AP_BALANCE_INVOICE.item_code
  is '貸方科目代碼';
comment on column FIT_AP_BALANCE_INVOICE.item_desc
  is '貸方科目描述';
comment on column FIT_AP_BALANCE_INVOICE.currency
  is '交易币别';
comment on column FIT_AP_BALANCE_INVOICE.src_amount
  is '原幣含稅應付金額';
comment on column FIT_AP_BALANCE_INVOICE.src_tax
  is '原幣稅額';
comment on column FIT_AP_BALANCE_INVOICE.src_untax
  is '原幣未稅額';
comment on column FIT_AP_BALANCE_INVOICE.currency_amount
  is '本幣含稅應付金額';
comment on column FIT_AP_BALANCE_INVOICE.currency_tax
  is '本幣稅額';
comment on column FIT_AP_BALANCE_INVOICE.currency_untax
  is '本幣未稅額';
comment on column FIT_AP_BALANCE_INVOICE.exchange_rate
  is '重評匯率';
comment on column FIT_AP_BALANCE_INVOICE.currency_examount
  is '本幣重評含稅應付金額';
comment on column FIT_AP_BALANCE_INVOICE.currency_extax
  is '重評稅額';
comment on column FIT_AP_BALANCE_INVOICE.currency_exuntax
  is '重評未稅額';
comment on column FIT_AP_BALANCE_INVOICE.doc_date
  is '入帳日期';
comment on column FIT_AP_BALANCE_INVOICE.due_date
  is '到期日';
comment on column FIT_AP_BALANCE_INVOICE.aging
  is '帳齡';
comment on column FIT_AP_BALANCE_INVOICE.condition
  is '付款條件';
comment on column FIT_AP_BALANCE_INVOICE.summary
  is '摘要';
comment on column FIT_AP_BALANCE_INVOICE.note
  is '备注';
comment on column FIT_AP_BALANCE_INVOICE.department
  is '部門代碼';
comment on column FIT_AP_BALANCE_INVOICE.borrow_item_code
  is '借方科目代碼';
comment on column FIT_AP_BALANCE_INVOICE.borrow_item_desc
  is '借方科目描述';
alter table FIT_AP_BALANCE_INVOICE
  add constraint PK_FIT_AP_BALANCE_INVOICE_ID primary key (ID);

prompt
prompt Creating table FIT_AP_BALANCE_STORAGE
prompt =====================================
prompt
create table FIT_AP_BALANCE_STORAGE
(
  id                    VARCHAR2(50) not null,
  corporation_code      VARCHAR2(50) not null,
  year                  VARCHAR2(4) not null,
  period                VARCHAR2(2) not null,
  document              VARCHAR2(100),
  summons               VARCHAR2(100),
  strike_no             VARCHAR2(100),
  no                    VARCHAR2(100),
  category              VARCHAR2(100),
  supplier              VARCHAR2(100),
  supplier_name         VARCHAR2(100),
  supplier_type         VARCHAR2(100),
  item_code             VARCHAR2(100),
  item_desc             VARCHAR2(100),
  currency              VARCHAR2(100),
  src_untax_amount      VARCHAR2(100),
  currency_untax_amount VARCHAR2(100),
  department            VARCHAR2(100),
  summary               VARCHAR2(100),
  borrow_item_code      VARCHAR2(100),
  borrow_item_desc      VARCHAR2(100)
)
;
comment on table FIT_AP_BALANCE_STORAGE
  is 'AP余额（驗收入庫單）';
comment on column FIT_AP_BALANCE_STORAGE.corporation_code
  is '法人编码';
comment on column FIT_AP_BALANCE_STORAGE.year
  is '年';
comment on column FIT_AP_BALANCE_STORAGE.period
  is '期間';
comment on column FIT_AP_BALANCE_STORAGE.document
  is '帳款編號';
comment on column FIT_AP_BALANCE_STORAGE.summons
  is '傳票編號';
comment on column FIT_AP_BALANCE_STORAGE.strike_no
  is '沖賬單號';
comment on column FIT_AP_BALANCE_STORAGE.no
  is '進貨驗收單/入庫單/退貨單號';
comment on column FIT_AP_BALANCE_STORAGE.category
  is '項次';
comment on column FIT_AP_BALANCE_STORAGE.supplier
  is '供應商代码';
comment on column FIT_AP_BALANCE_STORAGE.supplier_name
  is '供應商名稱';
comment on column FIT_AP_BALANCE_STORAGE.supplier_type
  is '供應商類型';
comment on column FIT_AP_BALANCE_STORAGE.item_code
  is '貸方科目代碼';
comment on column FIT_AP_BALANCE_STORAGE.item_desc
  is '貸方科目描述';
comment on column FIT_AP_BALANCE_STORAGE.currency
  is '交易币别';
comment on column FIT_AP_BALANCE_STORAGE.src_untax_amount
  is '原幣未稅應付金額';
comment on column FIT_AP_BALANCE_STORAGE.currency_untax_amount
  is '本幣未稅應付金額';
comment on column FIT_AP_BALANCE_STORAGE.department
  is '部門代碼';
comment on column FIT_AP_BALANCE_STORAGE.summary
  is '摘要';
comment on column FIT_AP_BALANCE_STORAGE.borrow_item_code
  is '借方科目代碼';
comment on column FIT_AP_BALANCE_STORAGE.borrow_item_desc
  is '借方科目描述';
alter table FIT_AP_BALANCE_STORAGE
  add constraint PK_FIT_AP_BALANCE_STORAGE_ID primary key (ID);

prompt
prompt Creating table FIT_AP_PAYMENT
prompt =============================
prompt
create table FIT_AP_PAYMENT
(
  id                VARCHAR2(50) not null,
  corporation_code  VARCHAR2(50) not null,
  year              VARCHAR2(4) not null,
  period            VARCHAR2(2) not null,
  document          VARCHAR2(100),
  summons           VARCHAR2(100),
  supplier          VARCHAR2(100),
  supplier_name     VARCHAR2(100),
  supplier_type     VARCHAR2(100),
  item_code         VARCHAR2(100),
  item_desc         VARCHAR2(100),
  currency          VARCHAR2(100),
  src_amount        VARCHAR2(100),
  src_alamount      VARCHAR2(100),
  src_unamount      VARCHAR2(100),
  currency_amount   VARCHAR2(100),
  currency_alamount VARCHAR2(100),
  currency_unamount VARCHAR2(100),
  condition         VARCHAR2(100),
  summary           VARCHAR2(300),
  note              VARCHAR2(300),
  borrow_item_code  VARCHAR2(100),
  borrow_item_desc  VARCHAR2(100)
)
;
comment on table FIT_AP_PAYMENT
  is 'AP已付款';
comment on column FIT_AP_PAYMENT.corporation_code
  is '法人编码';
comment on column FIT_AP_PAYMENT.year
  is '年';
comment on column FIT_AP_PAYMENT.period
  is '期間';
comment on column FIT_AP_PAYMENT.document
  is '帳款編號';
comment on column FIT_AP_PAYMENT.summons
  is '傳票編號';
comment on column FIT_AP_PAYMENT.supplier
  is '供應商代码';
comment on column FIT_AP_PAYMENT.supplier_name
  is '供應商名稱';
comment on column FIT_AP_PAYMENT.supplier_type
  is '供應商類型';
comment on column FIT_AP_PAYMENT.item_code
  is '貸方科目代碼';
comment on column FIT_AP_PAYMENT.item_desc
  is '貸方科目描述';
comment on column FIT_AP_PAYMENT.currency
  is '交易币别';
comment on column FIT_AP_PAYMENT.src_amount
  is '原幣含稅應付金額';
comment on column FIT_AP_PAYMENT.src_alamount
  is '原幣含稅已付金額';
comment on column FIT_AP_PAYMENT.src_unamount
  is '原幣含稅未付金額';
comment on column FIT_AP_PAYMENT.currency_amount
  is '本幣含稅應付金額';
comment on column FIT_AP_PAYMENT.currency_alamount
  is '本幣含稅已付金額';
comment on column FIT_AP_PAYMENT.currency_unamount
  is '本幣含稅未付金額';
comment on column FIT_AP_PAYMENT.condition
  is '付款條件';
comment on column FIT_AP_PAYMENT.summary
  is '摘要';
comment on column FIT_AP_PAYMENT.note
  is '备注';
comment on column FIT_AP_PAYMENT.borrow_item_code
  is '借方科目代碼';
comment on column FIT_AP_PAYMENT.borrow_item_desc
  is '借方科目描述';
alter table FIT_AP_PAYMENT
  add constraint PK_FIT_AP_PAYMENT_ID primary key (ID);

prompt
prompt Creating table FIT_AP_TRADE_INVOICE
prompt ===================================
prompt
create table FIT_AP_TRADE_INVOICE
(
  id                    VARCHAR2(50) not null,
  corporation_code      VARCHAR2(50) not null,
  year                  VARCHAR2(4) not null,
  period                VARCHAR2(2) not null,
  document              VARCHAR2(100),
  summons               VARCHAR2(100),
  invoice               VARCHAR2(100),
  supplier              VARCHAR2(100),
  supplier_name         VARCHAR2(100),
  supplier_type         VARCHAR2(100),
  item_code             VARCHAR2(100),
  item_desc             VARCHAR2(100),
  currency              VARCHAR2(100),
  tax_src_amount        VARCHAR2(100),
  tax_samount           VARCHAR2(100),
  untax_src_amount      VARCHAR2(100),
  tax_currency_amount   VARCHAR2(100),
  tax_camount           VARCHAR2(100),
  untax_currency_amount VARCHAR2(100),
  department            VARCHAR2(100),
  summary               VARCHAR2(300),
  borrow_item_code      VARCHAR2(100),
  borrow_item_desc      VARCHAR2(100)
)
;
comment on table FIT_AP_TRADE_INVOICE
  is 'AP交易额（發票）';
comment on column FIT_AP_TRADE_INVOICE.corporation_code
  is '法人编码';
comment on column FIT_AP_TRADE_INVOICE.year
  is '年';
comment on column FIT_AP_TRADE_INVOICE.period
  is '期間';
comment on column FIT_AP_TRADE_INVOICE.document
  is '帳款編號';
comment on column FIT_AP_TRADE_INVOICE.summons
  is '傳票編號';
comment on column FIT_AP_TRADE_INVOICE.invoice
  is '發票號碼';
comment on column FIT_AP_TRADE_INVOICE.supplier
  is '供應商代码';
comment on column FIT_AP_TRADE_INVOICE.supplier_name
  is '供應商名稱';
comment on column FIT_AP_TRADE_INVOICE.supplier_type
  is '供應商類型';
comment on column FIT_AP_TRADE_INVOICE.item_code
  is '貸方科目代碼';
comment on column FIT_AP_TRADE_INVOICE.item_desc
  is '貸方科目描述';
comment on column FIT_AP_TRADE_INVOICE.currency
  is '交易币别';
comment on column FIT_AP_TRADE_INVOICE.tax_src_amount
  is '含稅額（原幣）';
comment on column FIT_AP_TRADE_INVOICE.tax_samount
  is '稅額（原幣）';
comment on column FIT_AP_TRADE_INVOICE.untax_src_amount
  is '未稅額（原幣）';
comment on column FIT_AP_TRADE_INVOICE.tax_currency_amount
  is '含稅額（本幣）';
comment on column FIT_AP_TRADE_INVOICE.tax_camount
  is '稅額（本幣）';
comment on column FIT_AP_TRADE_INVOICE.untax_currency_amount
  is '未稅額（本幣）';
comment on column FIT_AP_TRADE_INVOICE.department
  is '部門代碼';
comment on column FIT_AP_TRADE_INVOICE.summary
  is '摘要';
comment on column FIT_AP_TRADE_INVOICE.borrow_item_code
  is '借方科目代碼';
comment on column FIT_AP_TRADE_INVOICE.borrow_item_desc
  is '借方科目描述';
alter table FIT_AP_TRADE_INVOICE
  add constraint PK_FIT_AP_TRADE_INVOICE_ID primary key (ID);

prompt
prompt Creating table FIT_AP_TRADE_STORAGE
prompt ===================================
prompt
create table FIT_AP_TRADE_STORAGE
(
  id                    VARCHAR2(50) not null,
  corporation_code      VARCHAR2(50) not null,
  year                  VARCHAR2(4) not null,
  period                VARCHAR2(2) not null,
  document              VARCHAR2(100),
  summons               VARCHAR2(100),
  strike_no             VARCHAR2(100),
  no                    VARCHAR2(100),
  category              VARCHAR2(100),
  supplier              VARCHAR2(100),
  supplier_name         VARCHAR2(100),
  supplier_type         VARCHAR2(100),
  item_code             VARCHAR2(100),
  item_desc             VARCHAR2(100),
  currency              VARCHAR2(100),
  src_untax_amount      VARCHAR2(100),
  currency_untax_amount VARCHAR2(100),
  department            VARCHAR2(100),
  summary               VARCHAR2(300),
  borrow_item_code      VARCHAR2(100),
  borrow_item_desc      VARCHAR2(100)
)
;
comment on table FIT_AP_TRADE_STORAGE
  is 'AP交易额（驗收入庫單）';
comment on column FIT_AP_TRADE_STORAGE.corporation_code
  is '法人编码';
comment on column FIT_AP_TRADE_STORAGE.year
  is '年';
comment on column FIT_AP_TRADE_STORAGE.period
  is '期間';
comment on column FIT_AP_TRADE_STORAGE.document
  is '帳款編號';
comment on column FIT_AP_TRADE_STORAGE.summons
  is '傳票編號';
comment on column FIT_AP_TRADE_STORAGE.strike_no
  is '沖賬單號';
comment on column FIT_AP_TRADE_STORAGE.no
  is '進貨驗收單/入庫單/退貨單號';
comment on column FIT_AP_TRADE_STORAGE.category
  is '項次';
comment on column FIT_AP_TRADE_STORAGE.supplier
  is '供應商代码';
comment on column FIT_AP_TRADE_STORAGE.supplier_name
  is '供應商名稱';
comment on column FIT_AP_TRADE_STORAGE.supplier_type
  is '供應商類型';
comment on column FIT_AP_TRADE_STORAGE.item_code
  is '貸方科目代碼';
comment on column FIT_AP_TRADE_STORAGE.item_desc
  is '貸方科目描述';
comment on column FIT_AP_TRADE_STORAGE.currency
  is '交易币别';
comment on column FIT_AP_TRADE_STORAGE.src_untax_amount
  is '未稅額（原幣）';
comment on column FIT_AP_TRADE_STORAGE.currency_untax_amount
  is '未稅額（本幣）';
comment on column FIT_AP_TRADE_STORAGE.department
  is '部門代碼';
comment on column FIT_AP_TRADE_STORAGE.summary
  is '摘要';
comment on column FIT_AP_TRADE_STORAGE.borrow_item_code
  is '借方科目代碼';
comment on column FIT_AP_TRADE_STORAGE.borrow_item_desc
  is '借方科目描述';
alter table FIT_AP_TRADE_STORAGE
  add constraint PK_FIT_AP_TRADE_STORAGE_ID primary key (ID);

prompt
prompt Creating table FIT_AR_BALANCE_INVOICE
prompt =====================================
prompt
create table FIT_AR_BALANCE_INVOICE
(
  id                VARCHAR2(50) not null,
  corporation_code  VARCHAR2(50) not null,
  year              VARCHAR2(4) not null,
  period            VARCHAR2(2) not null,
  document          VARCHAR2(100),
  summons           VARCHAR2(100),
  invoice           VARCHAR2(100),
  customer          VARCHAR2(100),
  customer_name     VARCHAR2(100),
  customer_type     VARCHAR2(100),
  item_code         VARCHAR2(100),
  item_desc         VARCHAR2(100),
  currency          VARCHAR2(100),
  src_amount        VARCHAR2(100),
  src_tax           VARCHAR2(100),
  src_untax         VARCHAR2(100),
  currency_amount   VARCHAR2(100),
  currency_tax      VARCHAR2(100),
  currency_untax    VARCHAR2(100),
  exchange_rate     VARCHAR2(100),
  currency_examount VARCHAR2(100),
  currency_extax    VARCHAR2(100),
  currency_exuntax  VARCHAR2(100),
  doc_date          VARCHAR2(100),
  due_date          VARCHAR2(100),
  overdue_days      VARCHAR2(100),
  aging             VARCHAR2(100),
  department        VARCHAR2(100),
  condition         VARCHAR2(100),
  summary           VARCHAR2(300),
  invoice_date      VARCHAR2(100),
  due_aging         VARCHAR2(100)
)
;
comment on table FIT_AR_BALANCE_INVOICE
  is 'AR余额（發票）';
comment on column FIT_AR_BALANCE_INVOICE.corporation_code
  is '法人编码';
comment on column FIT_AR_BALANCE_INVOICE.year
  is '年';
comment on column FIT_AR_BALANCE_INVOICE.period
  is '期間';
comment on column FIT_AR_BALANCE_INVOICE.document
  is '帳款編號';
comment on column FIT_AR_BALANCE_INVOICE.summons
  is '傳票編號';
comment on column FIT_AR_BALANCE_INVOICE.invoice
  is '發票號碼/銷售折讓單號';
comment on column FIT_AR_BALANCE_INVOICE.customer
  is '客商代码';
comment on column FIT_AR_BALANCE_INVOICE.customer_name
  is '客商名稱';
comment on column FIT_AR_BALANCE_INVOICE.customer_type
  is '客戶類型';
comment on column FIT_AR_BALANCE_INVOICE.item_code
  is '科目代碼';
comment on column FIT_AR_BALANCE_INVOICE.item_desc
  is '科目描述';
comment on column FIT_AR_BALANCE_INVOICE.currency
  is '交易币别';
comment on column FIT_AR_BALANCE_INVOICE.src_amount
  is '原幣含稅應收金額';
comment on column FIT_AR_BALANCE_INVOICE.src_tax
  is '原幣稅額';
comment on column FIT_AR_BALANCE_INVOICE.src_untax
  is '原幣未稅額';
comment on column FIT_AR_BALANCE_INVOICE.currency_amount
  is '本幣含稅應收金額';
comment on column FIT_AR_BALANCE_INVOICE.currency_tax
  is '本幣稅額';
comment on column FIT_AR_BALANCE_INVOICE.currency_untax
  is '本幣未稅額';
comment on column FIT_AR_BALANCE_INVOICE.exchange_rate
  is '重評匯率';
comment on column FIT_AR_BALANCE_INVOICE.currency_examount
  is '本幣重評含稅應收金額';
comment on column FIT_AR_BALANCE_INVOICE.currency_extax
  is '本幣重評稅額';
comment on column FIT_AR_BALANCE_INVOICE.currency_exuntax
  is '本幣重評未稅額';
comment on column FIT_AR_BALANCE_INVOICE.doc_date
  is '入帳日期';
comment on column FIT_AR_BALANCE_INVOICE.due_date
  is '到期日';
comment on column FIT_AR_BALANCE_INVOICE.overdue_days
  is '逾期天數';
comment on column FIT_AR_BALANCE_INVOICE.aging
  is '發票日帳齡';
comment on column FIT_AR_BALANCE_INVOICE.department
  is '部門代碼';
comment on column FIT_AR_BALANCE_INVOICE.condition
  is '收款條件';
comment on column FIT_AR_BALANCE_INVOICE.summary
  is '摘要';
comment on column FIT_AR_BALANCE_INVOICE.invoice_date
  is '發票日期';
comment on column FIT_AR_BALANCE_INVOICE.due_aging
  is '到期日賬齡';
alter table FIT_AR_BALANCE_INVOICE
  add constraint PK_FIT_AR_BALANCE_INVOICE_ID primary key (ID);

prompt
prompt Creating table FIT_AR_BALANCE_SALE
prompt ==================================
prompt
create table FIT_AR_BALANCE_SALE
(
  id                VARCHAR2(50) not null,
  corporation_code  VARCHAR2(50) not null,
  year              VARCHAR2(4) not null,
  period            VARCHAR2(2) not null,
  document          VARCHAR2(100),
  summons           VARCHAR2(100),
  sale              VARCHAR2(100),
  category          VARCHAR2(100),
  customer          VARCHAR2(100),
  customer_name     VARCHAR2(100),
  customer_type     VARCHAR2(100),
  item_code         VARCHAR2(100),
  item_desc         VARCHAR2(100),
  currency          VARCHAR2(100),
  src_amount        VARCHAR2(100),
  currency_amount   VARCHAR2(100),
  exchange_rate     VARCHAR2(100),
  currency_examount VARCHAR2(100),
  department        VARCHAR2(100),
  condition         VARCHAR2(100),
  summary           VARCHAR2(300)
)
;
comment on table FIT_AR_BALANCE_SALE
  is 'AR余额（銷單）';
comment on column FIT_AR_BALANCE_SALE.corporation_code
  is '法人编码';
comment on column FIT_AR_BALANCE_SALE.year
  is '年';
comment on column FIT_AR_BALANCE_SALE.period
  is '期間';
comment on column FIT_AR_BALANCE_SALE.document
  is '帳款編號';
comment on column FIT_AR_BALANCE_SALE.summons
  is '傳票編號';
comment on column FIT_AR_BALANCE_SALE.sale
  is '銷單/出貨單';
comment on column FIT_AR_BALANCE_SALE.category
  is '項次';
comment on column FIT_AR_BALANCE_SALE.customer
  is '客商代码';
comment on column FIT_AR_BALANCE_SALE.customer_name
  is '客商名稱';
comment on column FIT_AR_BALANCE_SALE.customer_type
  is '客戶類型';
comment on column FIT_AR_BALANCE_SALE.item_code
  is '科目代碼';
comment on column FIT_AR_BALANCE_SALE.item_desc
  is '科目描述';
comment on column FIT_AR_BALANCE_SALE.currency
  is '交易币别';
comment on column FIT_AR_BALANCE_SALE.src_amount
  is '原幣含稅應收金額';
comment on column FIT_AR_BALANCE_SALE.currency_amount
  is '本幣含稅應收金額';
comment on column FIT_AR_BALANCE_SALE.exchange_rate
  is '重評匯率';
comment on column FIT_AR_BALANCE_SALE.currency_examount
  is '本幣重評含稅應收金額';
comment on column FIT_AR_BALANCE_SALE.department
  is '部門代碼';
comment on column FIT_AR_BALANCE_SALE.condition
  is '收款條件';
comment on column FIT_AR_BALANCE_SALE.summary
  is '摘要';
alter table FIT_AR_BALANCE_SALE
  add constraint PK_FIT_AR_BALANCE_SALE_ID primary key (ID);

prompt
prompt Creating table FIT_AR_RECEIVE
prompt =============================
prompt
create table FIT_AR_RECEIVE
(
  id                VARCHAR2(50) not null,
  corporation_code  VARCHAR2(50) not null,
  year              VARCHAR2(4) not null,
  period            VARCHAR2(2) not null,
  document          VARCHAR2(100),
  summons           VARCHAR2(100),
  customer          VARCHAR2(100),
  customer_name     VARCHAR2(100),
  customer_type     VARCHAR2(100),
  item_code         VARCHAR2(100),
  item_desc         VARCHAR2(100),
  currency          VARCHAR2(100),
  src_amount        VARCHAR2(100),
  src_alamount      VARCHAR2(100),
  src_unamount      VARCHAR2(100),
  currency_amount   VARCHAR2(100),
  currency_alamount VARCHAR2(100),
  currency_unamount VARCHAR2(100)
)
;
comment on table FIT_AR_RECEIVE
  is 'AR已收款';
comment on column FIT_AR_RECEIVE.corporation_code
  is '法人编码';
comment on column FIT_AR_RECEIVE.year
  is '年';
comment on column FIT_AR_RECEIVE.period
  is '期間';
comment on column FIT_AR_RECEIVE.document
  is '帳款編號';
comment on column FIT_AR_RECEIVE.summons
  is '傳票編號';
comment on column FIT_AR_RECEIVE.customer
  is '客商代码';
comment on column FIT_AR_RECEIVE.customer_name
  is '客商名稱';
comment on column FIT_AR_RECEIVE.customer_type
  is '客戶類型';
comment on column FIT_AR_RECEIVE.item_code
  is '科目代碼';
comment on column FIT_AR_RECEIVE.item_desc
  is '科目描述';
comment on column FIT_AR_RECEIVE.currency
  is '交易币别';
comment on column FIT_AR_RECEIVE.src_amount
  is '原幣含稅應收金額';
comment on column FIT_AR_RECEIVE.src_alamount
  is '原幣含稅已收金額';
comment on column FIT_AR_RECEIVE.src_unamount
  is '原幣含稅未收金額';
comment on column FIT_AR_RECEIVE.currency_amount
  is '本幣含稅應收金額';
comment on column FIT_AR_RECEIVE.currency_alamount
  is '本幣含稅已收金額';
comment on column FIT_AR_RECEIVE.currency_unamount
  is '本幣含稅未收金額';
alter table FIT_AR_RECEIVE
  add constraint PK_FIT_AR_RECEIVE_ID primary key (ID);

prompt
prompt Creating table FIT_AR_TRADE_INVOICE
prompt ===================================
prompt
create table FIT_AR_TRADE_INVOICE
(
  id                    VARCHAR2(50) not null,
  corporation_code      VARCHAR2(50) not null,
  year                  VARCHAR2(4) not null,
  period                VARCHAR2(2) not null,
  document              VARCHAR2(100),
  summons               VARCHAR2(100),
  invoice               VARCHAR2(100),
  customer              VARCHAR2(100),
  customer_name         VARCHAR2(100),
  customer_type         VARCHAR2(100),
  item_code             VARCHAR2(100),
  item_desc             VARCHAR2(100),
  currency              VARCHAR2(100),
  tax_src_amount        VARCHAR2(100),
  tax_samount           VARCHAR2(100),
  untax_src_amount      VARCHAR2(100),
  tax_currency_amount   VARCHAR2(100),
  tax_camount           VARCHAR2(100),
  untax_currency_amount VARCHAR2(100),
  department            VARCHAR2(100),
  summary               VARCHAR2(300),
  borrow_item_code      VARCHAR2(100),
  borrow_item_desc      VARCHAR2(100)
)
;
comment on table FIT_AR_TRADE_INVOICE
  is 'AR交易额（發票）';
comment on column FIT_AR_TRADE_INVOICE.corporation_code
  is '法人编码';
comment on column FIT_AR_TRADE_INVOICE.year
  is '年';
comment on column FIT_AR_TRADE_INVOICE.period
  is '期間';
comment on column FIT_AR_TRADE_INVOICE.document
  is '帳款編號';
comment on column FIT_AR_TRADE_INVOICE.summons
  is '傳票編號';
comment on column FIT_AR_TRADE_INVOICE.invoice
  is '發票號碼/銷售折讓單號';
comment on column FIT_AR_TRADE_INVOICE.customer
  is '客商代码';
comment on column FIT_AR_TRADE_INVOICE.customer_name
  is '客商名稱';
comment on column FIT_AR_TRADE_INVOICE.customer_type
  is '客戶類型';
comment on column FIT_AR_TRADE_INVOICE.item_code
  is '貸方科目代碼';
comment on column FIT_AR_TRADE_INVOICE.item_desc
  is '貸方科目描述';
comment on column FIT_AR_TRADE_INVOICE.currency
  is '交易币别';
comment on column FIT_AR_TRADE_INVOICE.tax_src_amount
  is '含稅額（原幣）';
comment on column FIT_AR_TRADE_INVOICE.tax_samount
  is '稅額（原幣）';
comment on column FIT_AR_TRADE_INVOICE.untax_src_amount
  is '未稅額（原幣）';
comment on column FIT_AR_TRADE_INVOICE.tax_currency_amount
  is '含稅額（本幣）';
comment on column FIT_AR_TRADE_INVOICE.tax_camount
  is '稅額（本幣）';
comment on column FIT_AR_TRADE_INVOICE.untax_currency_amount
  is '未稅額（本幣）';
comment on column FIT_AR_TRADE_INVOICE.department
  is '部門代碼';
comment on column FIT_AR_TRADE_INVOICE.summary
  is '摘要';
comment on column FIT_AR_TRADE_INVOICE.borrow_item_code
  is '借方科目代碼';
comment on column FIT_AR_TRADE_INVOICE.borrow_item_desc
  is '借方科目描述';
alter table FIT_AR_TRADE_INVOICE
  add constraint PK_FIT_AR_TRADE_INVOICE_ID primary key (ID);

prompt
prompt Creating table FIT_AR_TRADE_SALE
prompt ================================
prompt
create table FIT_AR_TRADE_SALE
(
  id                    VARCHAR2(50) not null,
  corporation_code      VARCHAR2(50) not null,
  year                  VARCHAR2(4) not null,
  period                VARCHAR2(2) not null,
  document              VARCHAR2(100),
  summons               VARCHAR2(100),
  sale                  VARCHAR2(100),
  category              VARCHAR2(100),
  customer              VARCHAR2(100),
  customer_name         VARCHAR2(100),
  customer_type         VARCHAR2(100),
  item_code             VARCHAR2(100),
  item_desc             VARCHAR2(100),
  currency              VARCHAR2(100),
  untax_src_amount      VARCHAR2(100),
  untax_currency_amount VARCHAR2(100),
  department            VARCHAR2(100),
  summary               VARCHAR2(300),
  borrow_item_code      VARCHAR2(100),
  borrow_item_desc      VARCHAR2(100)
)
;
comment on table FIT_AR_TRADE_SALE
  is 'AR交易额（銷單）';
comment on column FIT_AR_TRADE_SALE.corporation_code
  is '法人编码';
comment on column FIT_AR_TRADE_SALE.year
  is '年';
comment on column FIT_AR_TRADE_SALE.period
  is '期間';
comment on column FIT_AR_TRADE_SALE.document
  is '帳款編號';
comment on column FIT_AR_TRADE_SALE.summons
  is '傳票編號';
comment on column FIT_AR_TRADE_SALE.sale
  is '銷單/出貨單';
comment on column FIT_AR_TRADE_SALE.category
  is '項次';
comment on column FIT_AR_TRADE_SALE.customer
  is '客商代码';
comment on column FIT_AR_TRADE_SALE.customer_name
  is '客商名稱';
comment on column FIT_AR_TRADE_SALE.customer_type
  is '客戶類型';
comment on column FIT_AR_TRADE_SALE.item_code
  is '貸方科目代碼';
comment on column FIT_AR_TRADE_SALE.item_desc
  is '貸方科目描述';
comment on column FIT_AR_TRADE_SALE.currency
  is '交易币别';
comment on column FIT_AR_TRADE_SALE.untax_src_amount
  is '未稅額（原幣）';
comment on column FIT_AR_TRADE_SALE.untax_currency_amount
  is '未稅額（本幣）';
comment on column FIT_AR_TRADE_SALE.department
  is '部門代碼';
comment on column FIT_AR_TRADE_SALE.summary
  is '摘要';
comment on column FIT_AR_TRADE_SALE.borrow_item_code
  is '借方科目代碼';
comment on column FIT_AR_TRADE_SALE.borrow_item_desc
  is '借方科目描述';
alter table FIT_AR_TRADE_SALE
  add constraint PK_FIT_AR_TRADE_SALE_ID primary key (ID);

prompt
prompt Creating table FIT_AUDIT_AP_AGING
prompt =================================
prompt
create table FIT_AUDIT_AP_AGING
(
  generatetype    VARCHAR2(2) not null,
  year            VARCHAR2(4) not null,
  period          VARCHAR2(2) not null,
  entity          VARCHAR2(50) not null,
  entityname      VARCHAR2(240) not null,
  custcode        VARCHAR2(100) not null,
  custname        VARCHAR2(240) not null,
  custtype        VARCHAR2(100) not null,
  paymentterms    VARCHAR2(100) not null,
  transcurrency   VARCHAR2(3) not null,
  unpaidtrans     NUMBER not null,
  defaultcurrency VARCHAR2(3) not null,
  unpaiddefault   NUMBER not null,
  invoicedate     DATE not null,
  subsequentcode  VARCHAR2(100),
  invoice         VARCHAR2(100) not null,
  paid            NUMBER,
  unpaid          NUMBER
)
;
comment on table FIT_AUDIT_AP_AGING
  is 'AP aging_AP賬齡明細表';
comment on column FIT_AUDIT_AP_AGING.generatetype
  is 'GenerateType_標識';
comment on column FIT_AUDIT_AP_AGING.year
  is 'Year_年份';
comment on column FIT_AUDIT_AP_AGING.period
  is 'Period_月份';
comment on column FIT_AUDIT_AP_AGING.entity
  is 'Company code_法人編碼';
comment on column FIT_AUDIT_AP_AGING.entityname
  is 'Company name_法人名稱';
comment on column FIT_AUDIT_AP_AGING.custcode
  is 'Supplier_供應商代碼';
comment on column FIT_AUDIT_AP_AGING.custname
  is 'Supplier name_供應商名稱';
comment on column FIT_AUDIT_AP_AGING.custtype
  is 'FIT group/related party/3rd party_合併事業體/非合併事業體及關係人/3rd party';
comment on column FIT_AUDIT_AP_AGING.paymentterms
  is 'Payment term_付款條件';
comment on column FIT_AUDIT_AP_AGING.transcurrency
  is 'Transaction currency_原幣幣別';
comment on column FIT_AUDIT_AP_AGING.unpaidtrans
  is 'Transaction amount_原幣未付金額';
comment on column FIT_AUDIT_AP_AGING.defaultcurrency
  is 'Functional currency_本幣幣別';
comment on column FIT_AUDIT_AP_AGING.unpaiddefault
  is 'Functional currency amount_本幣未付金額';
comment on column FIT_AUDIT_AP_AGING.invoicedate
  is 'Invoice date_發票日期';
comment on column FIT_AUDIT_AP_AGING.subsequentcode
  is 'Subsequent account code_期後帳款編號';
comment on column FIT_AUDIT_AP_AGING.invoice
  is 'Invoice number_發票號碼';
comment on column FIT_AUDIT_AP_AGING.paid
  is 'Subsequent paid_期後已付金額';
comment on column FIT_AUDIT_AP_AGING.unpaid
  is 'Subsequent unpaid_期後未付金額';
alter table FIT_AUDIT_AP_AGING
  add constraint FIT_AUDIT_AP_AGING_PK primary key (YEAR, PERIOD, ENTITY, CUSTCODE, TRANSCURRENCY, INVOICEDATE, INVOICE)
  disable
  novalidate;

prompt
prompt Creating table FIT_AUDIT_AP_AGING_C
prompt ===================================
prompt
create table FIT_AUDIT_AP_AGING_C
(
  year            VARCHAR2(4) not null,
  period          VARCHAR2(2) not null,
  entity          VARCHAR2(50) not null,
  entityname      VARCHAR2(240) not null,
  custcode        VARCHAR2(100) not null,
  custname        VARCHAR2(240) not null,
  custtype        VARCHAR2(100) not null,
  group_name      VARCHAR2(100),
  paymentterms    VARCHAR2(100) not null,
  transcurrency   VARCHAR2(3) not null,
  unpaidtrans     NUMBER not null,
  defaultcurrency VARCHAR2(3) not null,
  unpaiddefault   NUMBER not null,
  invoicedate     DATE not null,
  agedays         VARCHAR2(100),
  clorate         NUMBER,
  unpaidusd       NUMBER,
  age3m           NUMBER,
  age3t4m         NUMBER,
  age4t6m         NUMBER,
  age6t12m        NUMBER,
  age1t2y         NUMBER,
  age2t3y         NUMBER,
  age3y           NUMBER,
  subsequentcode  VARCHAR2(100),
  invoice         VARCHAR2(100) not null,
  paid            NUMBER,
  unpaid          NUMBER
)
;
comment on table FIT_AUDIT_AP_AGING_C
  is 'AP aging_AP賬齡明細表';
comment on column FIT_AUDIT_AP_AGING_C.year
  is 'Year_年份';
comment on column FIT_AUDIT_AP_AGING_C.period
  is 'Period_月份';
comment on column FIT_AUDIT_AP_AGING_C.entity
  is 'Company code_法人編碼';
comment on column FIT_AUDIT_AP_AGING_C.entityname
  is 'Company name_法人名稱';
comment on column FIT_AUDIT_AP_AGING_C.custcode
  is 'Supplier_供應商代碼';
comment on column FIT_AUDIT_AP_AGING_C.custname
  is 'Supplier name_供應商名稱';
comment on column FIT_AUDIT_AP_AGING_C.custtype
  is 'FIT group/related party/3rd party_合併事業體/非合併事業體及關係人/3rd party';
comment on column FIT_AUDIT_AP_AGING_C.group_name
  is 'Group_所屬集團';
comment on column FIT_AUDIT_AP_AGING_C.paymentterms
  is 'Payment term_付款條件';
comment on column FIT_AUDIT_AP_AGING_C.transcurrency
  is 'Transaction currency_原幣幣別';
comment on column FIT_AUDIT_AP_AGING_C.unpaidtrans
  is 'Transaction amount_原幣未付金額';
comment on column FIT_AUDIT_AP_AGING_C.defaultcurrency
  is 'Functional currency_本幣幣別';
comment on column FIT_AUDIT_AP_AGING_C.unpaiddefault
  is 'Functional currency amount_本幣未付金額';
comment on column FIT_AUDIT_AP_AGING_C.invoicedate
  is 'Invoice date_發票日期';
comment on column FIT_AUDIT_AP_AGING_C.agedays
  is 'Ageing_賬齡天數';
comment on column FIT_AUDIT_AP_AGING_C.clorate
  is 'Rate_匯率';
comment on column FIT_AUDIT_AP_AGING_C.unpaidusd
  is 'Unpaid USD_美金未付';
comment on column FIT_AUDIT_AP_AGING_C.age3m
  is 'Aging within 3m_賬齡(3個月以內)';
comment on column FIT_AUDIT_AP_AGING_C.age3t4m
  is 'Aging 3-4m_賬齡(3~4個月)';
comment on column FIT_AUDIT_AP_AGING_C.age4t6m
  is 'Aging 4-6m_賬齡(4~6個月)';
comment on column FIT_AUDIT_AP_AGING_C.age6t12m
  is 'Aging 6-12m_賬齡(6~12個月)';
comment on column FIT_AUDIT_AP_AGING_C.age1t2y
  is 'Aging 1-2y_賬齡(1~2年)';
comment on column FIT_AUDIT_AP_AGING_C.age2t3y
  is 'Aging 2-3y_賬齡(2~3年)';
comment on column FIT_AUDIT_AP_AGING_C.age3y
  is 'Aging over 3y_賬齡(3年以上)';
comment on column FIT_AUDIT_AP_AGING_C.subsequentcode
  is 'Subsequent account code_期後帳款編號';
comment on column FIT_AUDIT_AP_AGING_C.invoice
  is 'Invoice number_發票號碼';
comment on column FIT_AUDIT_AP_AGING_C.paid
  is 'Paid_期後付款（已付款金額）';
comment on column FIT_AUDIT_AP_AGING_C.unpaid
  is 'Unpaid_期後付款（未付款金額）';
alter table FIT_AUDIT_AP_AGING_C
  add constraint FIT_AUDIT_AP_AGING_C_PK primary key (YEAR, PERIOD, ENTITY, CUSTCODE, TRANSCURRENCY, INVOICEDATE, INVOICE);

prompt
prompt Creating table FIT_AUDIT_AP_AGING_T
prompt ===================================
prompt
create table FIT_AUDIT_AP_AGING_T
(
  generatetype    VARCHAR2(2) not null,
  year            VARCHAR2(4) not null,
  period          VARCHAR2(2) not null,
  entity          VARCHAR2(50) not null,
  entityname      VARCHAR2(240) not null,
  custcode        VARCHAR2(100) not null,
  custname        VARCHAR2(240) not null,
  custtype        VARCHAR2(100) not null,
  paymentterms    VARCHAR2(100) not null,
  transcurrency   VARCHAR2(3) not null,
  unpaidtrans     NUMBER not null,
  defaultcurrency VARCHAR2(3) not null,
  unpaiddefault   NUMBER not null,
  invoicedate     DATE not null,
  subsequentcode  VARCHAR2(100),
  invoice         VARCHAR2(100) not null,
  paid            NUMBER,
  unpaid          NUMBER
)
;
comment on table FIT_AUDIT_AP_AGING_T
  is 'AP aging_AP賬齡明細表';
comment on column FIT_AUDIT_AP_AGING_T.generatetype
  is 'GenerateType_標識';
comment on column FIT_AUDIT_AP_AGING_T.year
  is 'Year_年份';
comment on column FIT_AUDIT_AP_AGING_T.period
  is 'Period_月份';
comment on column FIT_AUDIT_AP_AGING_T.entity
  is 'Company code_法人編碼';
comment on column FIT_AUDIT_AP_AGING_T.entityname
  is 'Company name_法人名稱';
comment on column FIT_AUDIT_AP_AGING_T.custcode
  is 'Supplier_供應商代碼';
comment on column FIT_AUDIT_AP_AGING_T.custname
  is 'Supplier name_供應商名稱';
comment on column FIT_AUDIT_AP_AGING_T.custtype
  is 'FIT group/related party/3rd party_合併事業體/非合併事業體及關係人/3rd party';
comment on column FIT_AUDIT_AP_AGING_T.paymentterms
  is 'Payment term_付款條件';
comment on column FIT_AUDIT_AP_AGING_T.transcurrency
  is 'Transaction currency_原幣幣別';
comment on column FIT_AUDIT_AP_AGING_T.unpaidtrans
  is 'Transaction amount_原幣未付金額';
comment on column FIT_AUDIT_AP_AGING_T.defaultcurrency
  is 'Functional currency_本幣幣別';
comment on column FIT_AUDIT_AP_AGING_T.unpaiddefault
  is 'Functional currency amount_本幣未付金額';
comment on column FIT_AUDIT_AP_AGING_T.invoicedate
  is 'Invoice date_發票日期';
comment on column FIT_AUDIT_AP_AGING_T.subsequentcode
  is 'Subsequent account code_期後帳款編號';
comment on column FIT_AUDIT_AP_AGING_T.invoice
  is 'Invoice number_發票號碼';
comment on column FIT_AUDIT_AP_AGING_T.paid
  is 'Subsequent paid_期後已付金額';
comment on column FIT_AUDIT_AP_AGING_T.unpaid
  is 'Subsequent unpaid_期後未付金額';
alter table FIT_AUDIT_AP_AGING_T
  add constraint FIT_AUDIT_AP_AGING_T_PK primary key (YEAR, PERIOD, ENTITY, CUSTCODE, TRANSCURRENCY, INVOICEDATE, INVOICE)
  disable
  novalidate;

prompt
prompt Creating table FIT_AUDIT_AP_GROUP
prompt =================================
prompt
create table FIT_AUDIT_AP_GROUP
(
  sort_nbr        NUMBER,
  year            VARCHAR2(4) not null,
  period          VARCHAR2(2) not null,
  entity          VARCHAR2(240) not null,
  isparent        VARCHAR2(1) not null,
  balance         NUMBER,
  agewiththreem   NUMBER,
  agethreetofourm NUMBER,
  agefourtosixm   NUMBER,
  agesixtotwelvem NUMBER,
  ageonetotwoy    NUMBER,
  agetwotothreey  NUMBER,
  ageoverthreey   NUMBER
)
;
comment on table FIT_AUDIT_AP_GROUP
  is 'AP by group';
comment on column FIT_AUDIT_AP_GROUP.sort_nbr
  is 'Sort';
comment on column FIT_AUDIT_AP_GROUP.year
  is 'Year_年份(審計時間)';
comment on column FIT_AUDIT_AP_GROUP.period
  is 'Period_月份';
comment on column FIT_AUDIT_AP_GROUP.entity
  is '列標籤';
comment on column FIT_AUDIT_AP_GROUP.isparent
  is 'isparent';
comment on column FIT_AUDIT_AP_GROUP.balance
  is '加總 - 美幣未付';
comment on column FIT_AUDIT_AP_GROUP.agewiththreem
  is '加總 - Within 3 months (3個月以內)';
comment on column FIT_AUDIT_AP_GROUP.agethreetofourm
  is '加總 - 3 to 4 months (3~4個月)';
comment on column FIT_AUDIT_AP_GROUP.agefourtosixm
  is '加總 - 4 to 6 months (4~6個月)';
comment on column FIT_AUDIT_AP_GROUP.agesixtotwelvem
  is '加總 - 6 to 12 months (6~12個月)';
comment on column FIT_AUDIT_AP_GROUP.ageonetotwoy
  is '加總 -  1 to 2 years ';
comment on column FIT_AUDIT_AP_GROUP.agetwotothreey
  is '加總 - 2 to 3 years ';
comment on column FIT_AUDIT_AP_GROUP.ageoverthreey
  is '加總 - Over 3 years (3年以上)';

prompt
prompt Creating table FIT_AUDIT_AP_TOP5
prompt ================================
prompt
create table FIT_AUDIT_AP_TOP5
(
  sort_nbr        NUMBER,
  year            VARCHAR2(4) not null,
  period          VARCHAR2(2) not null,
  cust            VARCHAR2(240),
  balance         NUMBER default 0,
  creditterms     VARCHAR2(240),
  agewiththreem   NUMBER default 0,
  agethreetosixm  NUMBER default 0,
  agethreetofourm NUMBER default 0,
  agefourtosixm   NUMBER default 0,
  agesixtotwelvem NUMBER default 0,
  ageonetotwoy    NUMBER default 0,
  agetwotothreey  NUMBER default 0,
  ageoverthreey   NUMBER default 0,
  paidafterdue    NUMBER default 0
)
;
comment on table FIT_AUDIT_AP_TOP5
  is 'AP Top5';
comment on column FIT_AUDIT_AP_TOP5.sort_nbr
  is 'Sort';
comment on column FIT_AUDIT_AP_TOP5.year
  is 'Year_年份(審計時間)';
comment on column FIT_AUDIT_AP_TOP5.period
  is 'Period_月份';
comment on column FIT_AUDIT_AP_TOP5.cust
  is 'Name of suppliers(供應商名稱)';
comment on column FIT_AUDIT_AP_TOP5.balance
  is 'Balance(餘額)';
comment on column FIT_AUDIT_AP_TOP5.creditterms
  is 'Credit terms(賒帳期限)';
comment on column FIT_AUDIT_AP_TOP5.agewiththreem
  is 'Within 3 months(3個月以內)';
comment on column FIT_AUDIT_AP_TOP5.agethreetosixm
  is '3 to 6 months(3~6個月)';
comment on column FIT_AUDIT_AP_TOP5.agethreetofourm
  is '3 to 4 months(3~4個月)';
comment on column FIT_AUDIT_AP_TOP5.agefourtosixm
  is '4 to 6 months(4~6個月)';
comment on column FIT_AUDIT_AP_TOP5.agesixtotwelvem
  is '6 to 12 months(6~12個月)';
comment on column FIT_AUDIT_AP_TOP5.ageonetotwoy
  is '1 to 2 years(1~2年)';
comment on column FIT_AUDIT_AP_TOP5.agetwotothreey
  is '2 to 3 years(2~3年)';
comment on column FIT_AUDIT_AP_TOP5.ageoverthreey
  is 'Over 3 years(3年以上)';
comment on column FIT_AUDIT_AP_TOP5.paidafterdue
  is 'Subsequent Payment_期後付款（已付款金額）';

prompt
prompt Creating table FIT_AUDIT_AR_AGING
prompt =================================
prompt
create table FIT_AUDIT_AR_AGING
(
  generatetype      VARCHAR2(2) not null,
  year              VARCHAR2(4) not null,
  period            VARCHAR2(2) not null,
  entity            VARCHAR2(50) not null,
  entityname        VARCHAR2(240) not null,
  custcode          VARCHAR2(100) not null,
  custname          VARCHAR2(240) not null,
  custtype          VARCHAR2(100) not null,
  paymentterms      VARCHAR2(100) not null,
  transcurrency     VARCHAR2(3) not null,
  unreceivedtrans   NUMBER not null,
  defaultcurrency   VARCHAR2(3) not null,
  unreceiveddefault NUMBER not null,
  invoicedate       DATE not null,
  duedate           DATE not null,
  subsequentcode    VARCHAR2(100),
  invoice           VARCHAR2(100) not null,
  recamt            NUMBER,
  unrec             NUMBER
)
;
comment on table FIT_AUDIT_AR_AGING
  is 'AR aging_AR賬齡明細表';
comment on column FIT_AUDIT_AR_AGING.generatetype
  is 'GenerateType_標識';
comment on column FIT_AUDIT_AR_AGING.year
  is 'Year_年份';
comment on column FIT_AUDIT_AR_AGING.period
  is 'Period_月份';
comment on column FIT_AUDIT_AR_AGING.entity
  is 'Company code_法人編碼';
comment on column FIT_AUDIT_AR_AGING.entityname
  is 'Company name_法人名稱';
comment on column FIT_AUDIT_AR_AGING.custcode
  is 'Customer_客户代碼';
comment on column FIT_AUDIT_AR_AGING.custname
  is 'Customer name_客户名稱';
comment on column FIT_AUDIT_AR_AGING.custtype
  is 'FIT group/related party/3rd party_合併事業體/非合併事業體及關係人/3rd party';
comment on column FIT_AUDIT_AR_AGING.paymentterms
  is 'Payment term_收款條件';
comment on column FIT_AUDIT_AR_AGING.transcurrency
  is 'Transaction currency_原幣幣別';
comment on column FIT_AUDIT_AR_AGING.unreceivedtrans
  is 'Transaction amount_原幣未收金額';
comment on column FIT_AUDIT_AR_AGING.defaultcurrency
  is 'Functional currency_本幣幣別';
comment on column FIT_AUDIT_AR_AGING.unreceiveddefault
  is 'Functional currency amount_本幣未收金額';
comment on column FIT_AUDIT_AR_AGING.invoicedate
  is 'Invoice date_發票日期';
comment on column FIT_AUDIT_AR_AGING.duedate
  is 'Due date_到期日期';
comment on column FIT_AUDIT_AR_AGING.subsequentcode
  is 'Subsequent account code_期後帳款編號';
comment on column FIT_AUDIT_AR_AGING.invoice
  is 'Invoice number_發票號碼';
comment on column FIT_AUDIT_AR_AGING.recamt
  is 'Subsequent received_期後已收金额';
comment on column FIT_AUDIT_AR_AGING.unrec
  is 'Subsequent unreceived_期後未收金额';
alter table FIT_AUDIT_AR_AGING
  add constraint PK_FIT_AUDIT_AR_AGING primary key (YEAR, PERIOD, ENTITY, CUSTCODE, TRANSCURRENCY, INVOICEDATE, INVOICE);

prompt
prompt Creating table FIT_AUDIT_AR_AGING_C
prompt ===================================
prompt
create table FIT_AUDIT_AR_AGING_C
(
  year              VARCHAR2(4) not null,
  period            VARCHAR2(2) not null,
  entity            VARCHAR2(50) not null,
  entityname        VARCHAR2(240) not null,
  custcode          VARCHAR2(100) not null,
  custname          VARCHAR2(240) not null,
  custtype          VARCHAR2(100) not null,
  group_name        VARCHAR2(100),
  paymentterms      VARCHAR2(100) not null,
  transcurrency     VARCHAR2(3) not null,
  unreceivedtrans   NUMBER not null,
  defaultcurrency   VARCHAR2(3) not null,
  unreceiveddefault NUMBER not null,
  invoicedate       DATE not null,
  duedate           DATE not null,
  agedays           VARCHAR2(100),
  duedays           VARCHAR2(100),
  clorate           NUMBER,
  unrecusd          NUMBER,
  age3m             NUMBER,
  age3t4m           NUMBER,
  age4t6m           NUMBER,
  age6t12m          NUMBER,
  age1t2y           NUMBER,
  age2t3y           NUMBER,
  age3y             NUMBER,
  unrecusd1         NUMBER,
  undueamt          NUMBER,
  due3m             NUMBER,
  due3t4m           NUMBER,
  due4t6m           NUMBER,
  due6t12m          NUMBER,
  due1t2y           NUMBER,
  due2t3y           NUMBER,
  due3y             NUMBER,
  subsequentcode    VARCHAR2(100),
  invoice           VARCHAR2(100) not null,
  recamt            NUMBER,
  unrec             NUMBER
)
;
comment on table FIT_AUDIT_AR_AGING_C
  is 'AR aging_AR賬齡明細表';
comment on column FIT_AUDIT_AR_AGING_C.year
  is 'Year_年份';
comment on column FIT_AUDIT_AR_AGING_C.period
  is 'Period_月份';
comment on column FIT_AUDIT_AR_AGING_C.entity
  is 'Company code_法人編碼';
comment on column FIT_AUDIT_AR_AGING_C.entityname
  is 'Entity name_法人名稱';
comment on column FIT_AUDIT_AR_AGING_C.custcode
  is 'Customer_客户代碼';
comment on column FIT_AUDIT_AR_AGING_C.custname
  is 'Customer name_客户名稱';
comment on column FIT_AUDIT_AR_AGING_C.custtype
  is 'FIT group/related party/3rd party_合併事業體/非合併事業體及關係人/3rd party';
comment on column FIT_AUDIT_AR_AGING_C.group_name
  is 'Group_所屬集團';
comment on column FIT_AUDIT_AR_AGING_C.paymentterms
  is 'Payment term_收款條件';
comment on column FIT_AUDIT_AR_AGING_C.transcurrency
  is 'Transaction currency_原幣幣別';
comment on column FIT_AUDIT_AR_AGING_C.unreceivedtrans
  is 'Transaction amount_原幣未收金額';
comment on column FIT_AUDIT_AR_AGING_C.defaultcurrency
  is 'Functional currency_本幣幣別';
comment on column FIT_AUDIT_AR_AGING_C.unreceiveddefault
  is 'Functional currency amount_本幣未收金額';
comment on column FIT_AUDIT_AR_AGING_C.invoicedate
  is 'Invoice date_發票日期';
comment on column FIT_AUDIT_AR_AGING_C.duedate
  is 'Due date_到期日期';
comment on column FIT_AUDIT_AR_AGING_C.agedays
  is '賬齡天數';
comment on column FIT_AUDIT_AR_AGING_C.duedays
  is '逾期';
comment on column FIT_AUDIT_AR_AGING_C.clorate
  is '匯率';
comment on column FIT_AUDIT_AR_AGING_C.unrecusd
  is '美幣未收';
comment on column FIT_AUDIT_AR_AGING_C.age3m
  is '賬齡(3個月以內)';
comment on column FIT_AUDIT_AR_AGING_C.age3t4m
  is '賬齡(3 to 4個月)';
comment on column FIT_AUDIT_AR_AGING_C.age4t6m
  is '賬齡(4~6個月)';
comment on column FIT_AUDIT_AR_AGING_C.age6t12m
  is '賬齡(6~12個月)';
comment on column FIT_AUDIT_AR_AGING_C.age1t2y
  is '賬齡(1 to 2 years)';
comment on column FIT_AUDIT_AR_AGING_C.age2t3y
  is '賬齡(2 to 3 years)';
comment on column FIT_AUDIT_AR_AGING_C.age3y
  is '賬齡(Over 3 years)';
comment on column FIT_AUDIT_AR_AGING_C.unrecusd1
  is '美幣未收';
comment on column FIT_AUDIT_AR_AGING_C.undueamt
  is '未逾期金額';
comment on column FIT_AUDIT_AR_AGING_C.due3m
  is '賬齡(3個月以內)';
comment on column FIT_AUDIT_AR_AGING_C.due3t4m
  is '賬齡(3~4個月)';
comment on column FIT_AUDIT_AR_AGING_C.due4t6m
  is '賬齡(4~6個月)';
comment on column FIT_AUDIT_AR_AGING_C.due6t12m
  is '賬齡(6~12個月)';
comment on column FIT_AUDIT_AR_AGING_C.due1t2y
  is '賬齡(1~2年)';
comment on column FIT_AUDIT_AR_AGING_C.due2t3y
  is '賬齡(2~3年)';
comment on column FIT_AUDIT_AR_AGING_C.due3y
  is '賬齡(3年以上)';
comment on column FIT_AUDIT_AR_AGING_C.subsequentcode
  is 'Subsequent account code_期後帳款編號';
comment on column FIT_AUDIT_AR_AGING_C.invoice
  is 'Invoice number_發票號碼';
comment on column FIT_AUDIT_AR_AGING_C.recamt
  is '期後收款（已收款金額）';
comment on column FIT_AUDIT_AR_AGING_C.unrec
  is '期後收款（未收款金額）';
alter table FIT_AUDIT_AR_AGING_C
  add constraint PK_FIT_AUDIT_AR_AGING_C primary key (YEAR, PERIOD, ENTITY, CUSTCODE, TRANSCURRENCY, INVOICEDATE, INVOICE);

prompt
prompt Creating table FIT_AUDIT_AR_AGING_T
prompt ===================================
prompt
create table FIT_AUDIT_AR_AGING_T
(
  generatetype      VARCHAR2(2) not null,
  year              VARCHAR2(4) not null,
  period            VARCHAR2(2) not null,
  entity            VARCHAR2(50) not null,
  entityname        VARCHAR2(240) not null,
  custcode          VARCHAR2(100) not null,
  custname          VARCHAR2(240) not null,
  custtype          VARCHAR2(100) not null,
  paymentterms      VARCHAR2(100) not null,
  transcurrency     VARCHAR2(3) not null,
  unreceivedtrans   NUMBER not null,
  defaultcurrency   VARCHAR2(3) not null,
  unreceiveddefault NUMBER not null,
  invoicedate       DATE not null,
  duedate           DATE not null,
  subsequentcode    VARCHAR2(100),
  invoice           VARCHAR2(100) not null,
  recamt            NUMBER,
  unrec             NUMBER
)
;
comment on table FIT_AUDIT_AR_AGING_T
  is 'AR aging_AR賬齡明細表';
comment on column FIT_AUDIT_AR_AGING_T.generatetype
  is 'GenerateType_標識';
comment on column FIT_AUDIT_AR_AGING_T.year
  is 'Year_年份';
comment on column FIT_AUDIT_AR_AGING_T.period
  is 'Period_月份';
comment on column FIT_AUDIT_AR_AGING_T.entity
  is 'Company code_法人編碼';
comment on column FIT_AUDIT_AR_AGING_T.entityname
  is 'Entity name_法人名稱';
comment on column FIT_AUDIT_AR_AGING_T.custcode
  is 'Customer_客户代碼';
comment on column FIT_AUDIT_AR_AGING_T.custname
  is 'Customer name_客户名稱';
comment on column FIT_AUDIT_AR_AGING_T.custtype
  is 'FIT group/related party/3rd party_合併事業體/非合併事業體及關係人/3rd party';
comment on column FIT_AUDIT_AR_AGING_T.paymentterms
  is 'Payment term_收款條件';
comment on column FIT_AUDIT_AR_AGING_T.transcurrency
  is 'Transaction currency_原幣幣別';
comment on column FIT_AUDIT_AR_AGING_T.unreceivedtrans
  is 'Transaction amount_原幣未收金額';
comment on column FIT_AUDIT_AR_AGING_T.defaultcurrency
  is 'Functional currency_本幣幣別';
comment on column FIT_AUDIT_AR_AGING_T.unreceiveddefault
  is 'Functional currency amount_本幣未收金額';
comment on column FIT_AUDIT_AR_AGING_T.invoicedate
  is 'Invoice date_發票日期';
comment on column FIT_AUDIT_AR_AGING_T.duedate
  is 'Due date_到期日期';
comment on column FIT_AUDIT_AR_AGING_T.subsequentcode
  is 'Subsequent account code_期後帳款編號';
comment on column FIT_AUDIT_AR_AGING_T.invoice
  is 'Invoice number_發票號碼';
comment on column FIT_AUDIT_AR_AGING_T.recamt
  is 'Subsequent received_期後已收金额';
comment on column FIT_AUDIT_AR_AGING_T.unrec
  is 'Subsequent unreceived_期後未收金额';
alter table FIT_AUDIT_AR_AGING_T
  add constraint PK_FIT_AUDIT_AR_AGING_T primary key (YEAR, PERIOD, ENTITY, CUSTCODE, TRANSCURRENCY, INVOICEDATE, INVOICE);

prompt
prompt Creating table FIT_AUDIT_AR_GROUP
prompt =================================
prompt
create table FIT_AUDIT_AR_GROUP
(
  sort_nbr        NUMBER,
  year            VARCHAR2(4) not null,
  period          VARCHAR2(2) not null,
  entity          VARCHAR2(240) not null,
  isparent        VARCHAR2(1) not null,
  balance         NUMBER,
  agewiththreem   NUMBER,
  agethreetofourm NUMBER,
  agefourtosixm   NUMBER,
  agesixtotwelvem NUMBER,
  ageonetotwoy    NUMBER,
  agetwotothreey  NUMBER,
  ageoverthreey   NUMBER
)
;
comment on table FIT_AUDIT_AR_GROUP
  is 'AP by group';
comment on column FIT_AUDIT_AR_GROUP.sort_nbr
  is 'Sort';
comment on column FIT_AUDIT_AR_GROUP.year
  is 'Year_年份(審計時間)';
comment on column FIT_AUDIT_AR_GROUP.period
  is 'Period_月份';
comment on column FIT_AUDIT_AR_GROUP.entity
  is '列標籤';
comment on column FIT_AUDIT_AR_GROUP.isparent
  is 'isparent';
comment on column FIT_AUDIT_AR_GROUP.balance
  is '加總 - 美幣未收';
comment on column FIT_AUDIT_AR_GROUP.agewiththreem
  is '加總 - Within 3 months (3個月以內)';
comment on column FIT_AUDIT_AR_GROUP.agethreetofourm
  is '加總 - 3 to 4 months (3~4個月)';
comment on column FIT_AUDIT_AR_GROUP.agefourtosixm
  is '加總 - 4 to 6 months (4~6個月)';
comment on column FIT_AUDIT_AR_GROUP.agesixtotwelvem
  is '加總 - 6 to 12 months (6~12個月)';
comment on column FIT_AUDIT_AR_GROUP.ageonetotwoy
  is '加總 -  1 to 2 years ';
comment on column FIT_AUDIT_AR_GROUP.agetwotothreey
  is '加總 - 2 to 3 years ';
comment on column FIT_AUDIT_AR_GROUP.ageoverthreey
  is '加總 - Over 3 years (3年以上)';

prompt
prompt Creating table FIT_AUDIT_AR_TOP5
prompt ================================
prompt
create table FIT_AUDIT_AR_TOP5
(
  sort_nbr         NUMBER,
  year             VARCHAR2(4) not null,
  period           VARCHAR2(2) not null,
  cust             VARCHAR2(240),
  balance          NUMBER default 0,
  creditterms      VARCHAR2(240),
  agewiththreem    NUMBER default 0,
  agethreetosixm   NUMBER default 0,
  agethreetofourm  NUMBER default 0,
  agefourtosixm    NUMBER default 0,
  agesixtotwelvem  NUMBER default 0,
  ageonetotwoy     NUMBER default 0,
  agetwotothreey   NUMBER default 0,
  ageoverthreey    NUMBER default 0,
  receivedafterdue NUMBER default 0
)
;
comment on table FIT_AUDIT_AR_TOP5
  is 'AR Top5';
comment on column FIT_AUDIT_AR_TOP5.sort_nbr
  is 'Sort';
comment on column FIT_AUDIT_AR_TOP5.year
  is 'Year_年份(審計時間)';
comment on column FIT_AUDIT_AR_TOP5.period
  is 'Period_月份';
comment on column FIT_AUDIT_AR_TOP5.cust
  is 'Name of the customers(客戶名稱)';
comment on column FIT_AUDIT_AR_TOP5.balance
  is 'Balance(餘額)';
comment on column FIT_AUDIT_AR_TOP5.creditterms
  is 'Credit terms(賒帳期限)';
comment on column FIT_AUDIT_AR_TOP5.agewiththreem
  is 'Within 3 months(3個月以內)';
comment on column FIT_AUDIT_AR_TOP5.agethreetosixm
  is '3 to 6 months(3~6個月)';
comment on column FIT_AUDIT_AR_TOP5.agethreetofourm
  is '3 to 4 months(3~4個月)';
comment on column FIT_AUDIT_AR_TOP5.agefourtosixm
  is '4 to 6 months(4~6個月)';
comment on column FIT_AUDIT_AR_TOP5.agesixtotwelvem
  is '6 to 12 months(6~12個月)';
comment on column FIT_AUDIT_AR_TOP5.ageonetotwoy
  is ' 1 to 2 years(1~2年)';
comment on column FIT_AUDIT_AR_TOP5.agetwotothreey
  is '2 to 3 years(2~3年)';
comment on column FIT_AUDIT_AR_TOP5.ageoverthreey
  is 'Over 3 years(3年以上)';
comment on column FIT_AUDIT_AR_TOP5.receivedafterdue
  is 'Subsequent Received_期後收款（已收款金額）';

prompt
prompt Creating table FIT_AUDIT_BANK_DETAIL
prompt ====================================
prompt
create table FIT_AUDIT_BANK_DETAIL
(
  generatetype         VARCHAR2(2) not null,
  year                 VARCHAR2(4) not null,
  period               VARCHAR2(2) not null,
  entity               VARCHAR2(50) not null,
  entityname           VARCHAR2(240) not null,
  nature               VARCHAR2(100) not null,
  contractnumber       VARCHAR2(240) not null,
  bankname             VARCHAR2(240) not null,
  custtype             VARCHAR2(100) not null,
  originalcurrency     VARCHAR2(3) not null,
  originalamount       NUMBER not null,
  functionalcurrency   VARCHAR2(3) not null,
  functionalamount     NUMBER not null,
  startdate            DATE not null,
  enddate              DATE not null,
  rate                 NUMBER not null,
  interestexoriginal   NUMBER not null,
  interestexfunctional NUMBER not null,
  mortgagedproperty    VARCHAR2(240)
)
;
comment on table FIT_AUDIT_BANK_DETAIL
  is 'Bank borrowings_銀行借款明細表';
comment on column FIT_AUDIT_BANK_DETAIL.generatetype
  is 'GenerateType_標識';
comment on column FIT_AUDIT_BANK_DETAIL.year
  is 'Year_年份';
comment on column FIT_AUDIT_BANK_DETAIL.period
  is 'Period_月份';
comment on column FIT_AUDIT_BANK_DETAIL.entity
  is 'Company code_法人編碼';
comment on column FIT_AUDIT_BANK_DETAIL.entityname
  is 'Company name_法人名稱';
comment on column FIT_AUDIT_BANK_DETAIL.nature
  is 'Nature of bank borrowing_貸款性質';
comment on column FIT_AUDIT_BANK_DETAIL.contractnumber
  is 'contract number_貸款合同號';
comment on column FIT_AUDIT_BANK_DETAIL.bankname
  is 'Bank/Related party name_銀行/關係人名稱';
comment on column FIT_AUDIT_BANK_DETAIL.custtype
  is 'FIT group/related party/3rd party_合併事業體/非合併事業體及關係人/3rd party';
comment on column FIT_AUDIT_BANK_DETAIL.originalcurrency
  is 'Original currency_原幣幣別';
comment on column FIT_AUDIT_BANK_DETAIL.originalamount
  is 'Original amount_原幣金額';
comment on column FIT_AUDIT_BANK_DETAIL.functionalcurrency
  is 'Functional currency_本幣幣別';
comment on column FIT_AUDIT_BANK_DETAIL.functionalamount
  is 'Functional amount_本幣金額';
comment on column FIT_AUDIT_BANK_DETAIL.startdate
  is 'Start date_起始日';
comment on column FIT_AUDIT_BANK_DETAIL.enddate
  is 'End date_到期日';
comment on column FIT_AUDIT_BANK_DETAIL.rate
  is 'Rate_年利率';
comment on column FIT_AUDIT_BANK_DETAIL.interestexoriginal
  is 'Interest expense original_利息支出原幣';
comment on column FIT_AUDIT_BANK_DETAIL.interestexfunctional
  is 'Interest expense functional_利息支出本幣';
comment on column FIT_AUDIT_BANK_DETAIL.mortgagedproperty
  is 'Mortgaged property_抵押物';
alter table FIT_AUDIT_BANK_DETAIL
  add constraint FIT_AUDIT_BANK_DETAIL_PK primary key (YEAR, PERIOD, ENTITY, CONTRACTNUMBER);

prompt
prompt Creating table FIT_AUDIT_BANK_DETAIL_T
prompt ======================================
prompt
create table FIT_AUDIT_BANK_DETAIL_T
(
  generatetype         VARCHAR2(2) not null,
  year                 VARCHAR2(4) not null,
  period               VARCHAR2(2) not null,
  entity               VARCHAR2(50) not null,
  entityname           VARCHAR2(240) not null,
  nature               VARCHAR2(100) not null,
  contractnumber       VARCHAR2(240) not null,
  bankname             VARCHAR2(240) not null,
  custtype             VARCHAR2(100) not null,
  originalcurrency     VARCHAR2(3) not null,
  originalamount       NUMBER not null,
  functionalcurrency   VARCHAR2(3) not null,
  functionalamount     NUMBER not null,
  startdate            DATE not null,
  enddate              DATE not null,
  rate                 NUMBER not null,
  interestexoriginal   NUMBER not null,
  interestexfunctional NUMBER not null,
  mortgagedproperty    VARCHAR2(240)
)
;
comment on table FIT_AUDIT_BANK_DETAIL_T
  is 'Bank borrowings_銀行借款明細表';
comment on column FIT_AUDIT_BANK_DETAIL_T.generatetype
  is 'GenerateType_標識';
comment on column FIT_AUDIT_BANK_DETAIL_T.year
  is 'Year_年份';
comment on column FIT_AUDIT_BANK_DETAIL_T.period
  is 'Period_月份';
comment on column FIT_AUDIT_BANK_DETAIL_T.entity
  is 'Company code_法人編碼';
comment on column FIT_AUDIT_BANK_DETAIL_T.entityname
  is 'Company name_法人名稱';
comment on column FIT_AUDIT_BANK_DETAIL_T.nature
  is 'Nature of bank borrowing_貸款性質';
comment on column FIT_AUDIT_BANK_DETAIL_T.contractnumber
  is 'contract number_貸款合同號';
comment on column FIT_AUDIT_BANK_DETAIL_T.bankname
  is 'Bank/Related party name_銀行/關係人名稱';
comment on column FIT_AUDIT_BANK_DETAIL_T.custtype
  is 'FIT group/related party/3rd party_合併事業體/非合併事業體及關係人/3rd party';
comment on column FIT_AUDIT_BANK_DETAIL_T.originalcurrency
  is 'Original currency_原幣幣別';
comment on column FIT_AUDIT_BANK_DETAIL_T.originalamount
  is 'Original amount_原幣金額';
comment on column FIT_AUDIT_BANK_DETAIL_T.functionalcurrency
  is 'Functional currency_本幣幣別';
comment on column FIT_AUDIT_BANK_DETAIL_T.functionalamount
  is 'Functional amount_本幣金額';
comment on column FIT_AUDIT_BANK_DETAIL_T.startdate
  is 'Start date_起始日';
comment on column FIT_AUDIT_BANK_DETAIL_T.enddate
  is 'End date_到期日';
comment on column FIT_AUDIT_BANK_DETAIL_T.rate
  is 'Rate_年利率';
comment on column FIT_AUDIT_BANK_DETAIL_T.interestexoriginal
  is 'Interest expense original_利息支出原幣';
comment on column FIT_AUDIT_BANK_DETAIL_T.interestexfunctional
  is 'Interest expense functional_利息支出本幣';
comment on column FIT_AUDIT_BANK_DETAIL_T.mortgagedproperty
  is 'Mortgaged property_抵押物';
alter table FIT_AUDIT_BANK_DETAIL_T
  add constraint FIT_AUDIT_BANK_DETAIL_T_PK primary key (YEAR, PERIOD, ENTITY, CONTRACTNUMBER);

prompt
prompt Creating table FIT_AUDIT_BANK_MOVE
prompt ==================================
prompt
create table FIT_AUDIT_BANK_MOVE
(
  generatetype             VARCHAR2(2) not null,
  year                     VARCHAR2(4) not null,
  period                   VARCHAR2(2) not null,
  entity                   VARCHAR2(50) not null,
  entityname               VARCHAR2(240) not null,
  bankname                 VARCHAR2(240) not null,
  addition_repayment       VARCHAR2(20) not null,
  originalcurrency         VARCHAR2(3) not null,
  originalamount           NUMBER not null,
  functionalcurrency       VARCHAR2(3) not null,
  functionalcurrencyamount NUMBER not null
)
;
comment on table FIT_AUDIT_BANK_MOVE
  is 'Bank borrowings movement_銀行借款變動';
comment on column FIT_AUDIT_BANK_MOVE.generatetype
  is 'GenerateType_標識';
comment on column FIT_AUDIT_BANK_MOVE.year
  is 'Year_年份';
comment on column FIT_AUDIT_BANK_MOVE.period
  is 'Period_月份';
comment on column FIT_AUDIT_BANK_MOVE.entity
  is 'Company code_法人編碼';
comment on column FIT_AUDIT_BANK_MOVE.entityname
  is 'Company name_法人名稱';
comment on column FIT_AUDIT_BANK_MOVE.bankname
  is 'Bank name_銀行名稱';
comment on column FIT_AUDIT_BANK_MOVE.addition_repayment
  is 'addition/repayment_借款/還款';
comment on column FIT_AUDIT_BANK_MOVE.originalcurrency
  is 'Original currency_原幣幣別';
comment on column FIT_AUDIT_BANK_MOVE.originalamount
  is 'Original amount_原幣金額';
comment on column FIT_AUDIT_BANK_MOVE.functionalcurrency
  is 'Functional currency_本幣幣別';
comment on column FIT_AUDIT_BANK_MOVE.functionalcurrencyamount
  is 'Functional currency amount_本幣金額';
alter table FIT_AUDIT_BANK_MOVE
  add constraint FIT_AUDIT_BANK_MOVE_PK primary key (YEAR, PERIOD, ENTITY, BANKNAME, ADDITION_REPAYMENT, ORIGINALCURRENCY);

prompt
prompt Creating table FIT_AUDIT_BANK_MOVE_T
prompt ====================================
prompt
create table FIT_AUDIT_BANK_MOVE_T
(
  generatetype             VARCHAR2(2) not null,
  year                     VARCHAR2(4) not null,
  period                   VARCHAR2(2) not null,
  entity                   VARCHAR2(50) not null,
  entityname               VARCHAR2(240) not null,
  bankname                 VARCHAR2(240) not null,
  addition_repayment       VARCHAR2(20) not null,
  originalcurrency         VARCHAR2(3) not null,
  originalamount           NUMBER not null,
  functionalcurrency       VARCHAR2(3) not null,
  functionalcurrencyamount NUMBER not null
)
;
comment on table FIT_AUDIT_BANK_MOVE_T
  is 'Bank borrowings movement_銀行借款變動';
comment on column FIT_AUDIT_BANK_MOVE_T.generatetype
  is 'GenerateType_標識';
comment on column FIT_AUDIT_BANK_MOVE_T.year
  is 'Year_年份';
comment on column FIT_AUDIT_BANK_MOVE_T.period
  is 'Period_月份';
comment on column FIT_AUDIT_BANK_MOVE_T.entity
  is 'Company code_法人編碼';
comment on column FIT_AUDIT_BANK_MOVE_T.entityname
  is 'Company name_法人名稱';
comment on column FIT_AUDIT_BANK_MOVE_T.bankname
  is 'Bank name_銀行名稱';
comment on column FIT_AUDIT_BANK_MOVE_T.addition_repayment
  is 'addition/repayment_借款/還款';
comment on column FIT_AUDIT_BANK_MOVE_T.originalcurrency
  is 'Original currency_原幣幣別';
comment on column FIT_AUDIT_BANK_MOVE_T.originalamount
  is 'Original amount_原幣金額';
comment on column FIT_AUDIT_BANK_MOVE_T.functionalcurrency
  is 'Functional currency_本幣幣別';
comment on column FIT_AUDIT_BANK_MOVE_T.functionalcurrencyamount
  is 'Functional currency amount_本幣金額';
alter table FIT_AUDIT_BANK_MOVE_T
  add constraint FIT_AUDIT_BANK_MOVE_T_PK primary key (YEAR, PERIOD, ENTITY, BANKNAME, ADDITION_REPAYMENT, ORIGINALCURRENCY);

prompt
prompt Creating table FIT_AUDIT_CAPITAL_LEASE
prompt ======================================
prompt
create table FIT_AUDIT_CAPITAL_LEASE
(
  generatetype     VARCHAR2(2) not null,
  year             VARCHAR2(4) not null,
  period           VARCHAR2(2) not null,
  entity           VARCHAR2(50) not null,
  entityname       VARCHAR2(240) not null,
  premises         VARCHAR2(500) not null,
  referenceno      VARCHAR2(240) not null,
  originalcurrency VARCHAR2(3) not null,
  contractprice_oc NUMBER not null,
  exchangerate     NUMBER not null,
  contractprice    NUMBER not null,
  settledamount    NUMBER not null,
  unsettledamount  NUMBER not null
)
;
comment on table FIT_AUDIT_CAPITAL_LEASE
  is 'Capital lease_資本租賃';
comment on column FIT_AUDIT_CAPITAL_LEASE.generatetype
  is 'GenerateType_標識';
comment on column FIT_AUDIT_CAPITAL_LEASE.year
  is 'Year_年份';
comment on column FIT_AUDIT_CAPITAL_LEASE.period
  is 'Period_月份';
comment on column FIT_AUDIT_CAPITAL_LEASE.entity
  is 'Company code_法人編碼';
comment on column FIT_AUDIT_CAPITAL_LEASE.entityname
  is 'Company name_法人名稱';
comment on column FIT_AUDIT_CAPITAL_LEASE.premises
  is 'Premises(Both Local add \ English Add)/Lease object_(當地地址\英文地址)/租賃標的';
comment on column FIT_AUDIT_CAPITAL_LEASE.referenceno
  is 'Contract Reference No._合約號碼';
comment on column FIT_AUDIT_CAPITAL_LEASE.originalcurrency
  is 'Original Currency_原幣別';
comment on column FIT_AUDIT_CAPITAL_LEASE.contractprice_oc
  is 'Contract price (Original currency)_契約價格 (原別幣)';
comment on column FIT_AUDIT_CAPITAL_LEASE.exchangerate
  is 'Exchange rate_匯率';
comment on column FIT_AUDIT_CAPITAL_LEASE.contractprice
  is 'Contract price_契約價格';
comment on column FIT_AUDIT_CAPITAL_LEASE.settledamount
  is 'Settled Amount_Settled Amount';
comment on column FIT_AUDIT_CAPITAL_LEASE.unsettledamount
  is 'Unsettled Amount_Unsettled Amount';
alter table FIT_AUDIT_CAPITAL_LEASE
  add constraint FIT_AUDIT_CAPITAL_LEASE_PK primary key (YEAR, PERIOD, ENTITY, REFERENCENO);

prompt
prompt Creating table FIT_AUDIT_CAPITAL_LEASE_T
prompt ========================================
prompt
create table FIT_AUDIT_CAPITAL_LEASE_T
(
  generatetype     VARCHAR2(2) not null,
  year             VARCHAR2(4) not null,
  period           VARCHAR2(2) not null,
  entity           VARCHAR2(50) not null,
  entityname       VARCHAR2(240) not null,
  premises         VARCHAR2(500) not null,
  referenceno      VARCHAR2(240) not null,
  originalcurrency VARCHAR2(3) not null,
  contractprice_oc NUMBER not null,
  exchangerate     NUMBER not null,
  contractprice    NUMBER not null,
  settledamount    NUMBER not null,
  unsettledamount  NUMBER not null
)
;
comment on table FIT_AUDIT_CAPITAL_LEASE_T
  is 'Capital lease_資本租賃';
comment on column FIT_AUDIT_CAPITAL_LEASE_T.generatetype
  is 'GenerateType_標識';
comment on column FIT_AUDIT_CAPITAL_LEASE_T.year
  is 'Year_年份';
comment on column FIT_AUDIT_CAPITAL_LEASE_T.period
  is 'Period_月份';
comment on column FIT_AUDIT_CAPITAL_LEASE_T.entity
  is 'Company code_法人編碼';
comment on column FIT_AUDIT_CAPITAL_LEASE_T.entityname
  is 'Company name_法人名稱';
comment on column FIT_AUDIT_CAPITAL_LEASE_T.premises
  is 'Premises(Both Local add \ English Add)/Lease object_(當地地址\英文地址)/租賃標的';
comment on column FIT_AUDIT_CAPITAL_LEASE_T.referenceno
  is 'Contract Reference No._合約號碼';
comment on column FIT_AUDIT_CAPITAL_LEASE_T.originalcurrency
  is 'Original Currency_原幣別';
comment on column FIT_AUDIT_CAPITAL_LEASE_T.contractprice_oc
  is 'Contract price (Original currency)_契約價格 (原別幣)';
comment on column FIT_AUDIT_CAPITAL_LEASE_T.exchangerate
  is 'Exchange rate_匯率';
comment on column FIT_AUDIT_CAPITAL_LEASE_T.contractprice
  is 'Contract price_契約價格';
comment on column FIT_AUDIT_CAPITAL_LEASE_T.settledamount
  is 'Settled Amount_Settled Amount';
comment on column FIT_AUDIT_CAPITAL_LEASE_T.unsettledamount
  is 'Unsettled Amount_Unsettled Amount';
alter table FIT_AUDIT_CAPITAL_LEASE_T
  add constraint FIT_AUDIT_CAPITAL_LEASE_T_PK primary key (YEAR, PERIOD, ENTITY, REFERENCENO);

prompt
prompt Creating table FIT_AUDIT_CHECK_DEPOSIT
prompt ======================================
prompt
create table FIT_AUDIT_CHECK_DEPOSIT
(
  generatetype                 VARCHAR2(2) not null,
  year                         VARCHAR2(4) not null,
  period                       VARCHAR2(2) not null,
  entity                       VARCHAR2(20) not null,
  entityname                   VARCHAR2(240) not null,
  accountcode                  VARCHAR2(20) not null,
  accountname                  VARCHAR2(50) not null,
  bank                         VARCHAR2(80) not null,
  bankaccount                  VARCHAR2(100) not null,
  originalcurrency             VARCHAR2(3) not null,
  balance_originalcurrency     NUMBER not null,
  exchangerate                 NUMBER not null,
  presentationcurrency         VARCHAR2(3) not null,
  balance_presentationcurrency NUMBER not null,
  bank_prc                     VARCHAR2(50) not null
)
;
comment on table FIT_AUDIT_CHECK_DEPOSIT
  is 'Checking deposit_支存明細表';
comment on column FIT_AUDIT_CHECK_DEPOSIT.generatetype
  is 'GenerateType_標識';
comment on column FIT_AUDIT_CHECK_DEPOSIT.year
  is 'Year_年份';
comment on column FIT_AUDIT_CHECK_DEPOSIT.period
  is 'Period_月份';
comment on column FIT_AUDIT_CHECK_DEPOSIT.entity
  is 'Company code_法人編碼';
comment on column FIT_AUDIT_CHECK_DEPOSIT.entityname
  is 'Company name_法人名稱';
comment on column FIT_AUDIT_CHECK_DEPOSIT.accountcode
  is 'Account code_科目明細';
comment on column FIT_AUDIT_CHECK_DEPOSIT.accountname
  is 'Account name_科目名稱';
comment on column FIT_AUDIT_CHECK_DEPOSIT.bank
  is 'Bank_銀行名稱';
comment on column FIT_AUDIT_CHECK_DEPOSIT.bankaccount
  is 'Bank account_銀行賬號';
comment on column FIT_AUDIT_CHECK_DEPOSIT.originalcurrency
  is 'Original currency_原幣幣別';
comment on column FIT_AUDIT_CHECK_DEPOSIT.balance_originalcurrency
  is 'Balance in original currency _原幣金額';
comment on column FIT_AUDIT_CHECK_DEPOSIT.exchangerate
  is 'Exchange rate_匯率';
comment on column FIT_AUDIT_CHECK_DEPOSIT.presentationcurrency
  is 'Company''s presentational currency_記帳幣別';
comment on column FIT_AUDIT_CHECK_DEPOSIT.balance_presentationcurrency
  is 'Balance in company''s presentational currency_月底餘額';
comment on column FIT_AUDIT_CHECK_DEPOSIT.bank_prc
  is 'Bank in PRC_內地銀行';
alter table FIT_AUDIT_CHECK_DEPOSIT
  add constraint FIT_AUDIT_CHECK_DEPOSIT_PK primary key (YEAR, PERIOD, ENTITY, ACCOUNTCODE, BANKACCOUNT, ORIGINALCURRENCY);

prompt
prompt Creating table FIT_AUDIT_CHECK_DEPOSIT_T
prompt ========================================
prompt
create table FIT_AUDIT_CHECK_DEPOSIT_T
(
  generatetype                 VARCHAR2(2) not null,
  year                         VARCHAR2(4) not null,
  period                       VARCHAR2(2) not null,
  entity                       VARCHAR2(20) not null,
  entityname                   VARCHAR2(240) not null,
  accountcode                  VARCHAR2(20) not null,
  accountname                  VARCHAR2(50) not null,
  bank                         VARCHAR2(80) not null,
  bankaccount                  VARCHAR2(100) not null,
  originalcurrency             VARCHAR2(3) not null,
  balance_originalcurrency     NUMBER not null,
  exchangerate                 NUMBER not null,
  presentationcurrency         VARCHAR2(3) not null,
  balance_presentationcurrency NUMBER not null,
  bank_prc                     VARCHAR2(50) not null
)
;
comment on table FIT_AUDIT_CHECK_DEPOSIT_T
  is 'Checking deposit_支存明細表';
comment on column FIT_AUDIT_CHECK_DEPOSIT_T.generatetype
  is 'GenerateType_標識';
comment on column FIT_AUDIT_CHECK_DEPOSIT_T.year
  is 'Year_年份';
comment on column FIT_AUDIT_CHECK_DEPOSIT_T.period
  is 'Period_月份';
comment on column FIT_AUDIT_CHECK_DEPOSIT_T.entity
  is 'Company code_法人編碼';
comment on column FIT_AUDIT_CHECK_DEPOSIT_T.entityname
  is 'Company name_法人名稱';
comment on column FIT_AUDIT_CHECK_DEPOSIT_T.accountcode
  is 'Account code_科目明細';
comment on column FIT_AUDIT_CHECK_DEPOSIT_T.accountname
  is 'Account name_科目名稱';
comment on column FIT_AUDIT_CHECK_DEPOSIT_T.bank
  is 'Bank_銀行名稱';
comment on column FIT_AUDIT_CHECK_DEPOSIT_T.bankaccount
  is 'Bank account_銀行賬號';
comment on column FIT_AUDIT_CHECK_DEPOSIT_T.originalcurrency
  is 'Original currency_原幣幣別';
comment on column FIT_AUDIT_CHECK_DEPOSIT_T.balance_originalcurrency
  is 'Balance in original currency _原幣金額';
comment on column FIT_AUDIT_CHECK_DEPOSIT_T.exchangerate
  is 'Exchange rate_匯率';
comment on column FIT_AUDIT_CHECK_DEPOSIT_T.presentationcurrency
  is 'Company''s presentational currency_記帳幣別';
comment on column FIT_AUDIT_CHECK_DEPOSIT_T.balance_presentationcurrency
  is 'Balance in company''s presentational currency_月底餘額';
comment on column FIT_AUDIT_CHECK_DEPOSIT_T.bank_prc
  is 'Bank in PRC_內地銀行';
alter table FIT_AUDIT_CHECK_DEPOSIT_T
  add constraint FIT_AUDIT_CHECK_DEPOSIT_T_PK primary key (YEAR, PERIOD, ENTITY, ACCOUNTCODE, BANKACCOUNT, ORIGINALCURRENCY);

prompt
prompt Creating table FIT_AUDIT_CONSOL_CONFIG
prompt ======================================
prompt
create table FIT_AUDIT_CONSOL_CONFIG
(
  tablename     VARCHAR2(500) not null,
  filename      VARCHAR2(500) not null,
  procedurename VARCHAR2(500)
)
;
comment on table FIT_AUDIT_CONSOL_CONFIG
  is '審計資料輸出模板配置表';
comment on column FIT_AUDIT_CONSOL_CONFIG.tablename
  is '表單名';
comment on column FIT_AUDIT_CONSOL_CONFIG.filename
  is '文件名';
comment on column FIT_AUDIT_CONSOL_CONFIG.procedurename
  is '存儲過程名';

prompt
prompt Creating table FIT_AUDIT_CUST_MAPPING
prompt =====================================
prompt
create table FIT_AUDIT_CUST_MAPPING
(
  custcode  VARCHAR2(100) not null,
  custgroup VARCHAR2(240) not null,
  region    VARCHAR2(100) not null
)
;
comment on table FIT_AUDIT_CUST_MAPPING
  is '客戶及供應商mapping表';
comment on column FIT_AUDIT_CUST_MAPPING.custcode
  is '客商代碼';
comment on column FIT_AUDIT_CUST_MAPPING.custgroup
  is '客商所屬集團';
comment on column FIT_AUDIT_CUST_MAPPING.region
  is '客商地區別';
alter table FIT_AUDIT_CUST_MAPPING
  add constraint FIT_AUDIT_CUST_MAPPING_PK primary key (CUSTCODE);

prompt
prompt Creating table FIT_AUDIT_DEMAND_DEPOSIT
prompt =======================================
prompt
create table FIT_AUDIT_DEMAND_DEPOSIT
(
  generatetype                 VARCHAR2(2) not null,
  year                         VARCHAR2(4) not null,
  period                       VARCHAR2(2) not null,
  entity                       VARCHAR2(20) not null,
  entityname                   VARCHAR2(240) not null,
  accountcode                  VARCHAR2(20) not null,
  accountname                  VARCHAR2(50) not null,
  bank                         VARCHAR2(80) not null,
  bankaccount                  VARCHAR2(100) not null,
  originalcurrency             VARCHAR2(3) not null,
  balance_originalcurrency     NUMBER not null,
  exchangerate                 NUMBER not null,
  presentationcurrency         VARCHAR2(3) not null,
  balance_presentationcurrency NUMBER not null,
  bank_prc                     VARCHAR2(50) not null
)
;
comment on table FIT_AUDIT_DEMAND_DEPOSIT
  is 'Demand deposit_活存明細表';
comment on column FIT_AUDIT_DEMAND_DEPOSIT.generatetype
  is 'GenerateType_標識';
comment on column FIT_AUDIT_DEMAND_DEPOSIT.year
  is 'Year_年份';
comment on column FIT_AUDIT_DEMAND_DEPOSIT.period
  is 'Period_月份';
comment on column FIT_AUDIT_DEMAND_DEPOSIT.entity
  is 'Company code_法人編碼';
comment on column FIT_AUDIT_DEMAND_DEPOSIT.entityname
  is 'Company name_法人名稱';
comment on column FIT_AUDIT_DEMAND_DEPOSIT.accountcode
  is 'Account code_科目明細';
comment on column FIT_AUDIT_DEMAND_DEPOSIT.accountname
  is 'Account name_科目名稱';
comment on column FIT_AUDIT_DEMAND_DEPOSIT.bank
  is 'Bank_銀行名稱';
comment on column FIT_AUDIT_DEMAND_DEPOSIT.bankaccount
  is 'Bank account_銀行賬號';
comment on column FIT_AUDIT_DEMAND_DEPOSIT.originalcurrency
  is 'Original currency_原幣幣別';
comment on column FIT_AUDIT_DEMAND_DEPOSIT.balance_originalcurrency
  is 'Balance in original currency _原幣金額';
comment on column FIT_AUDIT_DEMAND_DEPOSIT.exchangerate
  is 'Exchange rate_匯率';
comment on column FIT_AUDIT_DEMAND_DEPOSIT.presentationcurrency
  is 'Company''s presentational currency_記帳幣別';
comment on column FIT_AUDIT_DEMAND_DEPOSIT.balance_presentationcurrency
  is 'Balance in company''s presentational currency_月底餘額';
comment on column FIT_AUDIT_DEMAND_DEPOSIT.bank_prc
  is 'Bank in PRC_內地銀行';
alter table FIT_AUDIT_DEMAND_DEPOSIT
  add constraint FIT_AUDIT_DEMAND_DEPOSIT_PK primary key (YEAR, PERIOD, ENTITY, ACCOUNTCODE, BANKACCOUNT, ORIGINALCURRENCY);

prompt
prompt Creating table FIT_AUDIT_DEMAND_DEPOSIT_T
prompt =========================================
prompt
create table FIT_AUDIT_DEMAND_DEPOSIT_T
(
  generatetype                 VARCHAR2(2) not null,
  year                         VARCHAR2(4) not null,
  period                       VARCHAR2(2) not null,
  entity                       VARCHAR2(20) not null,
  entityname                   VARCHAR2(240) not null,
  accountcode                  VARCHAR2(20) not null,
  accountname                  VARCHAR2(50) not null,
  bank                         VARCHAR2(80) not null,
  bankaccount                  VARCHAR2(100) not null,
  originalcurrency             VARCHAR2(3) not null,
  balance_originalcurrency     NUMBER not null,
  exchangerate                 NUMBER not null,
  presentationcurrency         VARCHAR2(3) not null,
  balance_presentationcurrency NUMBER not null,
  bank_prc                     VARCHAR2(50) not null
)
;
comment on table FIT_AUDIT_DEMAND_DEPOSIT_T
  is 'Demand deposit_活存明細表';
comment on column FIT_AUDIT_DEMAND_DEPOSIT_T.generatetype
  is 'GenerateType_標識';
comment on column FIT_AUDIT_DEMAND_DEPOSIT_T.year
  is 'Year_年份';
comment on column FIT_AUDIT_DEMAND_DEPOSIT_T.period
  is 'Period_月份';
comment on column FIT_AUDIT_DEMAND_DEPOSIT_T.entity
  is 'Company code_法人編碼';
comment on column FIT_AUDIT_DEMAND_DEPOSIT_T.entityname
  is 'Company name_法人名稱';
comment on column FIT_AUDIT_DEMAND_DEPOSIT_T.accountcode
  is 'Account code_科目明細';
comment on column FIT_AUDIT_DEMAND_DEPOSIT_T.accountname
  is 'Account name_科目名稱';
comment on column FIT_AUDIT_DEMAND_DEPOSIT_T.bank
  is 'Bank_銀行名稱';
comment on column FIT_AUDIT_DEMAND_DEPOSIT_T.bankaccount
  is 'Bank account_銀行賬號';
comment on column FIT_AUDIT_DEMAND_DEPOSIT_T.originalcurrency
  is 'Original currency_原幣幣別';
comment on column FIT_AUDIT_DEMAND_DEPOSIT_T.balance_originalcurrency
  is 'Balance in original currency _原幣金額';
comment on column FIT_AUDIT_DEMAND_DEPOSIT_T.exchangerate
  is 'Exchange rate_匯率';
comment on column FIT_AUDIT_DEMAND_DEPOSIT_T.presentationcurrency
  is 'Company''s presentational currency_記帳幣別';
comment on column FIT_AUDIT_DEMAND_DEPOSIT_T.balance_presentationcurrency
  is 'Balance in company''s presentational currency_月底餘額';
comment on column FIT_AUDIT_DEMAND_DEPOSIT_T.bank_prc
  is 'Bank in PRC_內地銀行';
alter table FIT_AUDIT_DEMAND_DEPOSIT_T
  add constraint FIT_AUDIT_DEMAND_DEPOSIT_T_PK primary key (YEAR, PERIOD, ENTITY, ACCOUNTCODE, BANKACCOUNT, ORIGINALCURRENCY);

prompt
prompt Creating table FIT_AUDIT_DTA
prompt ============================
prompt
create table FIT_AUDIT_DTA
(
  generatetype          VARCHAR2(2) not null,
  year                  VARCHAR2(4) not null,
  period                VARCHAR2(2) not null,
  entity                VARCHAR2(50) not null,
  entityname            VARCHAR2(240) not null,
  unrealizedexchange    NUMBER not null,
  allowance             NUMBER not null,
  provisionloss         NUMBER not null,
  provisiondebt         NUMBER not null,
  unpaidbonus           NUMBER not null,
  unrealizedproduct     NUMBER not null,
  payroll               NUMBER not null,
  expenses              NUMBER not null,
  unrealizedsalesprofit NUMBER not null,
  depreciationexpense   NUMBER not null,
  retirementfunds       NUMBER not null,
  unrealizedgains       NUMBER not null,
  impairmentassets      NUMBER not null,
  revaluation           NUMBER not null,
  differences           NUMBER not null,
  transdifferences      NUMBER not null,
  unrealizedgain        NUMBER not null,
  cfhedges              NUMBER not null,
  netinvestmenthedges   NUMBER not null,
  actuarialgain         NUMBER not null,
  gaininvestments       NUMBER not null,
  oci                   NUMBER not null,
  investmenttaxcredits  NUMBER not null,
  losscarryforwards     NUMBER not null
)
;
comment on table FIT_AUDIT_DTA
  is '1840 Deferred tax working_1840 Deferred tax working';
comment on column FIT_AUDIT_DTA.generatetype
  is 'GenerateType_標識';
comment on column FIT_AUDIT_DTA.year
  is 'Year_年份';
comment on column FIT_AUDIT_DTA.period
  is 'Period_月份';
comment on column FIT_AUDIT_DTA.entity
  is 'Company code_法人編碼';
comment on column FIT_AUDIT_DTA.entityname
  is 'Company name_法人名稱';
comment on column FIT_AUDIT_DTA.unrealizedexchange
  is '1840 Unrealized exchange gain or loss_1840未實現兌換損益
';
comment on column FIT_AUDIT_DTA.allowance
  is '1840 Allowance for sales discounts and allowances_1840備抵銷貨折讓
';
comment on column FIT_AUDIT_DTA.provisionloss
  is '1840 Provision for inventory valuation loss and obsolescence_1840備抵存貨跌價及呆滯損失
';
comment on column FIT_AUDIT_DTA.provisiondebt
  is '1840 Provision for excessive bad debts_1840備抵呆帳超限數
';
comment on column FIT_AUDIT_DTA.unpaidbonus
  is '1840 Unpaid bonus_1840未休假獎金
';
comment on column FIT_AUDIT_DTA.unrealizedproduct
  is '1840 Unrealized product/post-sale warranty_1840未實現售後服務費
';
comment on column FIT_AUDIT_DTA.payroll
  is '1840 Payroll payable_1840應付職工薪資
';
comment on column FIT_AUDIT_DTA.expenses
  is '1840 Expenses carried forward from previous years_1840以前年度費用遞轉
';
comment on column FIT_AUDIT_DTA.unrealizedsalesprofit
  is '1840 Unrealized sales profit_1840存貨未實現銷貨利潤
';
comment on column FIT_AUDIT_DTA.depreciationexpense
  is '1840 Depreciation expense_1840折舊費用
';
comment on column FIT_AUDIT_DTA.retirementfunds
  is '1840 Retirement funds_1840退休金
';
comment on column FIT_AUDIT_DTA.unrealizedgains
  is '1840 Unrealized gains from affiliated companies_1840聯屬公司未實現利益
';
comment on column FIT_AUDIT_DTA.impairmentassets
  is '1840 Impairment of assets_1840資產減損
';
comment on column FIT_AUDIT_DTA.revaluation
  is '1840 Revaluation gains (losses)_1840重估價之利益(損失)
';
comment on column FIT_AUDIT_DTA.differences
  is '1840 Other temporary differences_1840其他暫時性差異
';
comment on column FIT_AUDIT_DTA.transdifferences
  is '1840 Financial statements translation differences of foreign operations_1840國外營運機構財務報表換算之兌換差額
';
comment on column FIT_AUDIT_DTA.unrealizedgain
  is '1840 Unrealized gain (loss) on valuation of available-for-sale financial assets_1840備供出售金融資產未實現評價損益
';
comment on column FIT_AUDIT_DTA.cfhedges
  is '1840 Cash flow hedges_1840現金流量避險
';
comment on column FIT_AUDIT_DTA.netinvestmenthedges
  is '1840 Foreign operations net investment hedges_1840國外營運機構淨投資避險
';
comment on column FIT_AUDIT_DTA.actuarialgain
  is '1840 Actuarial gain (loss) on defined benefit plan_1840確定福利計畫精算利益(損失)
';
comment on column FIT_AUDIT_DTA.gaininvestments
  is '1840 Gain (loss) on investments in equity instruments_1840權益工具投資之利益(損失)
';
comment on column FIT_AUDIT_DTA.oci
  is '1840 Other comprehensive income (loss)_1840其他綜合損益
';
comment on column FIT_AUDIT_DTA.investmenttaxcredits
  is '1840 Investment tax credits_1840投資抵減
';
comment on column FIT_AUDIT_DTA.losscarryforwards
  is '1840 Loss carryforwards_1840虧損扣抵
';
alter table FIT_AUDIT_DTA
  add constraint FIT_AUDIT_DTA_PK primary key (YEAR, PERIOD, ENTITY);

prompt
prompt Creating table FIT_AUDIT_DTA_T
prompt ==============================
prompt
create table FIT_AUDIT_DTA_T
(
  generatetype          VARCHAR2(2) not null,
  year                  VARCHAR2(4) not null,
  period                VARCHAR2(2) not null,
  entity                VARCHAR2(50) not null,
  entityname            VARCHAR2(240) not null,
  unrealizedexchange    NUMBER not null,
  allowance             NUMBER not null,
  provisionloss         NUMBER not null,
  provisiondebt         NUMBER not null,
  unpaidbonus           NUMBER not null,
  unrealizedproduct     NUMBER not null,
  payroll               NUMBER not null,
  expenses              NUMBER not null,
  unrealizedsalesprofit NUMBER not null,
  depreciationexpense   NUMBER not null,
  retirementfunds       NUMBER not null,
  unrealizedgains       NUMBER not null,
  impairmentassets      NUMBER not null,
  revaluation           NUMBER not null,
  differences           NUMBER not null,
  transdifferences      NUMBER not null,
  unrealizedgain        NUMBER not null,
  cfhedges              NUMBER not null,
  netinvestmenthedges   NUMBER not null,
  actuarialgain         NUMBER not null,
  gaininvestments       NUMBER not null,
  oci                   NUMBER not null,
  investmenttaxcredits  NUMBER not null,
  losscarryforwards     NUMBER not null
)
;
comment on table FIT_AUDIT_DTA_T
  is '1840 Deferred tax working_1840 Deferred tax working';
comment on column FIT_AUDIT_DTA_T.generatetype
  is 'GenerateType_標識';
comment on column FIT_AUDIT_DTA_T.year
  is 'Year_年份';
comment on column FIT_AUDIT_DTA_T.period
  is 'Period_月份';
comment on column FIT_AUDIT_DTA_T.entity
  is 'Company code_法人編碼';
comment on column FIT_AUDIT_DTA_T.entityname
  is 'Company name_法人名稱';
comment on column FIT_AUDIT_DTA_T.unrealizedexchange
  is '1840 Unrealized exchange gain or loss_1840未實現兌換損益
';
comment on column FIT_AUDIT_DTA_T.allowance
  is '1840 Allowance for sales discounts and allowances_1840備抵銷貨折讓
';
comment on column FIT_AUDIT_DTA_T.provisionloss
  is '1840 Provision for inventory valuation loss and obsolescence_1840備抵存貨跌價及呆滯損失
';
comment on column FIT_AUDIT_DTA_T.provisiondebt
  is '1840 Provision for excessive bad debts_1840備抵呆帳超限數
';
comment on column FIT_AUDIT_DTA_T.unpaidbonus
  is '1840 Unpaid bonus_1840未休假獎金
';
comment on column FIT_AUDIT_DTA_T.unrealizedproduct
  is '1840 Unrealized product/post-sale warranty_1840未實現售後服務費
';
comment on column FIT_AUDIT_DTA_T.payroll
  is '1840 Payroll payable_1840應付職工薪資
';
comment on column FIT_AUDIT_DTA_T.expenses
  is '1840 Expenses carried forward from previous years_1840以前年度費用遞轉
';
comment on column FIT_AUDIT_DTA_T.unrealizedsalesprofit
  is '1840 Unrealized sales profit_1840存貨未實現銷貨利潤
';
comment on column FIT_AUDIT_DTA_T.depreciationexpense
  is '1840 Depreciation expense_1840折舊費用
';
comment on column FIT_AUDIT_DTA_T.retirementfunds
  is '1840 Retirement funds_1840退休金
';
comment on column FIT_AUDIT_DTA_T.unrealizedgains
  is '1840 Unrealized gains from affiliated companies_1840聯屬公司未實現利益
';
comment on column FIT_AUDIT_DTA_T.impairmentassets
  is '1840 Impairment of assets_1840資產減損
';
comment on column FIT_AUDIT_DTA_T.revaluation
  is '1840 Revaluation gains (losses)_1840重估價之利益(損失)
';
comment on column FIT_AUDIT_DTA_T.differences
  is '1840 Other temporary differences_1840其他暫時性差異
';
comment on column FIT_AUDIT_DTA_T.transdifferences
  is '1840 Financial statements translation differences of foreign operations_1840國外營運機構財務報表換算之兌換差額
';
comment on column FIT_AUDIT_DTA_T.unrealizedgain
  is '1840 Unrealized gain (loss) on valuation of available-for-sale financial assets_1840備供出售金融資產未實現評價損益
';
comment on column FIT_AUDIT_DTA_T.cfhedges
  is '1840 Cash flow hedges_1840現金流量避險
';
comment on column FIT_AUDIT_DTA_T.netinvestmenthedges
  is '1840 Foreign operations net investment hedges_1840國外營運機構淨投資避險
';
comment on column FIT_AUDIT_DTA_T.actuarialgain
  is '1840 Actuarial gain (loss) on defined benefit plan_1840確定福利計畫精算利益(損失)
';
comment on column FIT_AUDIT_DTA_T.gaininvestments
  is '1840 Gain (loss) on investments in equity instruments_1840權益工具投資之利益(損失)
';
comment on column FIT_AUDIT_DTA_T.oci
  is '1840 Other comprehensive income (loss)_1840其他綜合損益
';
comment on column FIT_AUDIT_DTA_T.investmenttaxcredits
  is '1840 Investment tax credits_1840投資抵減
';
comment on column FIT_AUDIT_DTA_T.losscarryforwards
  is '1840 Loss carryforwards_1840虧損扣抵
';
alter table FIT_AUDIT_DTA_T
  add constraint FIT_AUDIT_DTA_TA_PK primary key (YEAR, PERIOD, ENTITY);

prompt
prompt Creating table FIT_AUDIT_DTL
prompt ============================
prompt
create table FIT_AUDIT_DTL
(
  generatetype        VARCHAR2(2) not null,
  year                VARCHAR2(4) not null,
  period              VARCHAR2(2) not null,
  entity              VARCHAR2(50) not null,
  entityname          VARCHAR2(240) not null,
  unrealizedexchange  NUMBER not null,
  investmentgain      NUMBER not null,
  revaluationgains    NUMBER not null,
  others              NUMBER not null,
  transdifferences    NUMBER not null,
  unrealizedgain      NUMBER not null,
  actuarialgain       NUMBER not null,
  gaininvestments     NUMBER not null,
  depreciationexpense NUMBER not null,
  oci                 NUMBER not null
)
;
comment on table FIT_AUDIT_DTL
  is '2570 Deferred tax working_2570 Deferred tax working';
comment on column FIT_AUDIT_DTL.generatetype
  is 'GenerateType_標識';
comment on column FIT_AUDIT_DTL.year
  is 'Year_年份';
comment on column FIT_AUDIT_DTL.period
  is 'Period_月份';
comment on column FIT_AUDIT_DTL.entity
  is 'Company code_法人編碼';
comment on column FIT_AUDIT_DTL.entityname
  is 'Company name_法人名稱';
comment on column FIT_AUDIT_DTL.unrealizedexchange
  is '2570 Unrealized exchange gain or loss_2570未實現兌換損益
';
comment on column FIT_AUDIT_DTL.investmentgain
  is '2570 Investment gain_2570投資利益
';
comment on column FIT_AUDIT_DTL.revaluationgains
  is '2570 Revaluation gains (losses)_2570重估價之利益(損失)
';
comment on column FIT_AUDIT_DTL.others
  is '2570 Others_2570其他
';
comment on column FIT_AUDIT_DTL.transdifferences
  is '2570 Financial statements translation differences of foreign operations_2570國外營運機構財務報表換算之兌換差額
';
comment on column FIT_AUDIT_DTL.unrealizedgain
  is '2570 Unrealized gain (loss) on valuation of available-for-sale financial assets_2570備供出售金融資產未實現評價損益
';
comment on column FIT_AUDIT_DTL.actuarialgain
  is '2570 Actuarial gain (loss) on defined benefit plan_2570確定福利計畫精算利益(損失)
';
comment on column FIT_AUDIT_DTL.gaininvestments
  is '2570 Gain (loss) on investments in equity instruments_2570權益工具投資之利益(損失)
';
comment on column FIT_AUDIT_DTL.depreciationexpense
  is '2570 Depreciation expense_2570折舊費用
';
comment on column FIT_AUDIT_DTL.oci
  is '2570 Other comprehensive income (loss)_2570其他綜合損益
';
alter table FIT_AUDIT_DTL
  add constraint FIT_AUDIT_DTL_PK primary key (YEAR, PERIOD, ENTITY);

prompt
prompt Creating table FIT_AUDIT_DTL_T
prompt ==============================
prompt
create table FIT_AUDIT_DTL_T
(
  generatetype        VARCHAR2(2) not null,
  year                VARCHAR2(4) not null,
  period              VARCHAR2(2) not null,
  entity              VARCHAR2(50) not null,
  entityname          VARCHAR2(240) not null,
  unrealizedexchange  NUMBER not null,
  investmentgain      NUMBER not null,
  revaluationgains    NUMBER not null,
  others              NUMBER not null,
  transdifferences    NUMBER not null,
  unrealizedgain      NUMBER not null,
  actuarialgain       NUMBER not null,
  gaininvestments     NUMBER not null,
  depreciationexpense NUMBER not null,
  oci                 NUMBER not null
)
;
comment on table FIT_AUDIT_DTL_T
  is '2570 Deferred tax working_2570 Deferred tax working';
comment on column FIT_AUDIT_DTL_T.generatetype
  is 'GenerateType_標識';
comment on column FIT_AUDIT_DTL_T.year
  is 'Year_年份';
comment on column FIT_AUDIT_DTL_T.period
  is 'Period_月份';
comment on column FIT_AUDIT_DTL_T.entity
  is 'Company code_法人編碼';
comment on column FIT_AUDIT_DTL_T.entityname
  is 'Company name_法人名稱';
comment on column FIT_AUDIT_DTL_T.unrealizedexchange
  is '2570 Unrealized exchange gain or loss_2570未實現兌換損益
';
comment on column FIT_AUDIT_DTL_T.investmentgain
  is '2570 Investment gain_2570投資利益
';
comment on column FIT_AUDIT_DTL_T.revaluationgains
  is '2570 Revaluation gains (losses)_2570重估價之利益(損失)
';
comment on column FIT_AUDIT_DTL_T.others
  is '2570 Others_2570其他
';
comment on column FIT_AUDIT_DTL_T.transdifferences
  is '2570 Financial statements translation differences of foreign operations_2570國外營運機構財務報表換算之兌換差額
';
comment on column FIT_AUDIT_DTL_T.unrealizedgain
  is '2570 Unrealized gain (loss) on valuation of available-for-sale financial assets_2570備供出售金融資產未實現評價損益
';
comment on column FIT_AUDIT_DTL_T.actuarialgain
  is '2570 Actuarial gain (loss) on defined benefit plan_2570確定福利計畫精算利益(損失)
';
comment on column FIT_AUDIT_DTL_T.gaininvestments
  is '2570 Gain (loss) on investments in equity instruments_2570權益工具投資之利益(損失)
';
comment on column FIT_AUDIT_DTL_T.depreciationexpense
  is '2570 Depreciation expense_2570折舊費用
';
comment on column FIT_AUDIT_DTL_T.oci
  is '2570 Other comprehensive income (loss)_2570其他綜合損益
';
alter table FIT_AUDIT_DTL_T
  add constraint FIT_AUDIT_DTL_T_PK primary key (YEAR, PERIOD, ENTITY);

prompt
prompt Creating table FIT_AUDIT_GAIN_LOSS
prompt ==================================
prompt
create table FIT_AUDIT_GAIN_LOSS
(
  generatetype   VARCHAR2(2) not null,
  year           VARCHAR2(4) not null,
  period         VARCHAR2(2) not null,
  entity         VARCHAR2(50) not null,
  entityname     VARCHAR2(240) not null,
  unrealizedgain NUMBER not null,
  realizedgain   NUMBER not null,
  unrealizedloss NUMBER not null,
  realizedloss   NUMBER not null
)
;
comment on table FIT_AUDIT_GAIN_LOSS
  is 'Other gain and loss_其他利益及損失';
comment on column FIT_AUDIT_GAIN_LOSS.generatetype
  is 'GenerateType_標識';
comment on column FIT_AUDIT_GAIN_LOSS.year
  is 'Year_年份';
comment on column FIT_AUDIT_GAIN_LOSS.period
  is 'Period_月份';
comment on column FIT_AUDIT_GAIN_LOSS.entity
  is 'Company code_法人編碼';
comment on column FIT_AUDIT_GAIN_LOSS.entityname
  is 'Company name_法人名稱';
comment on column FIT_AUDIT_GAIN_LOSS.unrealizedgain
  is 'Foreign currency exchange gain(unrealized)_外幣兌換利益(未實現)';
comment on column FIT_AUDIT_GAIN_LOSS.realizedgain
  is 'Foreign currency exchange gain(realized)_外幣兌換利益(已實現)';
comment on column FIT_AUDIT_GAIN_LOSS.unrealizedloss
  is 'Foreign currency exchange loss(unrealized)_外幣兌換損失(未實現)';
comment on column FIT_AUDIT_GAIN_LOSS.realizedloss
  is 'Foreign currency exchange loss(realized)_外幣兌換損失(已實現)';
alter table FIT_AUDIT_GAIN_LOSS
  add constraint FIT_AUDIT_GAIN_LOSS_PK primary key (YEAR, PERIOD, ENTITY);

prompt
prompt Creating table FIT_AUDIT_GAIN_LOSS_T
prompt ====================================
prompt
create table FIT_AUDIT_GAIN_LOSS_T
(
  generatetype   VARCHAR2(2) not null,
  year           VARCHAR2(4) not null,
  period         VARCHAR2(2) not null,
  entity         VARCHAR2(50) not null,
  entityname     VARCHAR2(240) not null,
  unrealizedgain NUMBER not null,
  realizedgain   NUMBER not null,
  unrealizedloss NUMBER not null,
  realizedloss   NUMBER not null
)
;
comment on table FIT_AUDIT_GAIN_LOSS_T
  is 'Other gain and loss_其他利益及損失';
comment on column FIT_AUDIT_GAIN_LOSS_T.generatetype
  is 'GenerateType_標識';
comment on column FIT_AUDIT_GAIN_LOSS_T.year
  is 'Year_年份';
comment on column FIT_AUDIT_GAIN_LOSS_T.period
  is 'Period_月份';
comment on column FIT_AUDIT_GAIN_LOSS_T.entity
  is 'Company code_法人編碼';
comment on column FIT_AUDIT_GAIN_LOSS_T.entityname
  is 'Company name_法人名稱';
comment on column FIT_AUDIT_GAIN_LOSS_T.unrealizedgain
  is 'Foreign currency exchange gain(unrealized)_外幣兌換利益(未實現)';
comment on column FIT_AUDIT_GAIN_LOSS_T.realizedgain
  is 'Foreign currency exchange gain(realized)_外幣兌換利益(已實現)';
comment on column FIT_AUDIT_GAIN_LOSS_T.unrealizedloss
  is 'Foreign currency exchange loss(unrealized)_外幣兌換損失(未實現)';
comment on column FIT_AUDIT_GAIN_LOSS_T.realizedloss
  is 'Foreign currency exchange loss(realized)_外幣兌換損失(已實現)';
alter table FIT_AUDIT_GAIN_LOSS_T
  add constraint FIT_AUDIT_GAIN_LOSS_T_PK primary key (YEAR, PERIOD, ENTITY);

prompt
prompt Creating table FIT_AUDIT_INVENTORY
prompt ==================================
prompt
create table FIT_AUDIT_INVENTORY
(
  generatetype       VARCHAR2(2) not null,
  year               VARCHAR2(4) not null,
  period             VARCHAR2(2) not null,
  entity             VARCHAR2(50) not null,
  entityname         VARCHAR2(240) not null,
  type               VARCHAR2(50) not null,
  grossamount_threem NUMBER not null,
  grossamount_fourm  NUMBER not null,
  grossamount_sevenm NUMBER not null,
  grossamount_oney   NUMBER not null,
  provision_threem   NUMBER not null,
  provision_fourm    NUMBER not null,
  provision_sevenm   NUMBER not null,
  provision_oney     NUMBER not null
)
;
comment on table FIT_AUDIT_INVENTORY
  is 'Inventory_存貨庫齡表';
comment on column FIT_AUDIT_INVENTORY.generatetype
  is 'GenerateType_標識';
comment on column FIT_AUDIT_INVENTORY.year
  is 'Year_年份';
comment on column FIT_AUDIT_INVENTORY.period
  is 'Period_月份';
comment on column FIT_AUDIT_INVENTORY.entity
  is 'Company code_法人編碼';
comment on column FIT_AUDIT_INVENTORY.entityname
  is 'Company name_法人名稱';
comment on column FIT_AUDIT_INVENTORY.type
  is 'raw materials/work in process/finished goods_原料/在製品/製成品';
comment on column FIT_AUDIT_INVENTORY.grossamount_threem
  is 'Inventory gross amount-Within 3 months_存貨原值-3個月以內';
comment on column FIT_AUDIT_INVENTORY.grossamount_fourm
  is 'Inventory gross amount-4 to 6 months_存貨原值-4~6個月';
comment on column FIT_AUDIT_INVENTORY.grossamount_sevenm
  is 'Inventory gross amount-7 to 12 months_存貨原值-7~12個月';
comment on column FIT_AUDIT_INVENTORY.grossamount_oney
  is 'Inventory gross amount-over 1 year_存貨原值-1年以上';
comment on column FIT_AUDIT_INVENTORY.provision_threem
  is 'Provision-within 3 months_存貨减值準備-3個月以內';
comment on column FIT_AUDIT_INVENTORY.provision_fourm
  is 'Provision-4 to 6 months_存貨减值準備-4~6個月';
comment on column FIT_AUDIT_INVENTORY.provision_sevenm
  is 'Provision-7 to 12 months_存貨减值準備-7~12個月';
comment on column FIT_AUDIT_INVENTORY.provision_oney
  is 'Provision-over 1 year_存貨减值準備-1年以上';
alter table FIT_AUDIT_INVENTORY
  add constraint FIT_AUDIT_INVENTORY_PK primary key (YEAR, PERIOD, ENTITY, TYPE);

prompt
prompt Creating table FIT_AUDIT_INVENTORY_T
prompt ====================================
prompt
create table FIT_AUDIT_INVENTORY_T
(
  generatetype       VARCHAR2(2) not null,
  year               VARCHAR2(4) not null,
  period             VARCHAR2(2) not null,
  entity             VARCHAR2(50) not null,
  entityname         VARCHAR2(240) not null,
  type               VARCHAR2(50) not null,
  grossamount_threem NUMBER not null,
  grossamount_fourm  NUMBER not null,
  grossamount_sevenm NUMBER not null,
  grossamount_oney   NUMBER not null,
  provision_threem   NUMBER not null,
  provision_fourm    NUMBER not null,
  provision_sevenm   NUMBER not null,
  provision_oney     NUMBER not null
)
;
comment on table FIT_AUDIT_INVENTORY_T
  is 'Inventory_存貨庫齡表';
comment on column FIT_AUDIT_INVENTORY_T.generatetype
  is 'GenerateType_標識';
comment on column FIT_AUDIT_INVENTORY_T.year
  is 'Year_年份';
comment on column FIT_AUDIT_INVENTORY_T.period
  is 'Period_月份';
comment on column FIT_AUDIT_INVENTORY_T.entity
  is 'Company code_法人編碼';
comment on column FIT_AUDIT_INVENTORY_T.entityname
  is 'Company name_法人名稱';
comment on column FIT_AUDIT_INVENTORY_T.type
  is 'raw materials/work in process/finished goods_原料/在製品/製成品';
comment on column FIT_AUDIT_INVENTORY_T.grossamount_threem
  is 'Inventory gross amount-Within 3 months_存貨原值-3個月以內';
comment on column FIT_AUDIT_INVENTORY_T.grossamount_fourm
  is 'Inventory gross amount-4 to 6 months_存貨原值-4~6個月';
comment on column FIT_AUDIT_INVENTORY_T.grossamount_sevenm
  is 'Inventory gross amount-7 to 12 months_存貨原值-7~12個月';
comment on column FIT_AUDIT_INVENTORY_T.grossamount_oney
  is 'Inventory gross amount-over 1 year_存貨原值-1年以上';
comment on column FIT_AUDIT_INVENTORY_T.provision_threem
  is 'Provision-within 3 months_存貨减值準備-3個月以內';
comment on column FIT_AUDIT_INVENTORY_T.provision_fourm
  is 'Provision-4 to 6 months_存貨减值準備-4~6個月';
comment on column FIT_AUDIT_INVENTORY_T.provision_sevenm
  is 'Provision-7 to 12 months_存貨减值準備-7~12個月';
comment on column FIT_AUDIT_INVENTORY_T.provision_oney
  is 'Provision-over 1 year_存貨减值準備-1年以上';
alter table FIT_AUDIT_INVENTORY_T
  add constraint FIT_AUDIT_INVENTORY_T_PK primary key (YEAR, PERIOD, ENTITY, TYPE);

prompt
prompt Creating table FIT_AUDIT_LESSEE
prompt ===============================
prompt
create table FIT_AUDIT_LESSEE
(
  generatetype        VARCHAR2(2) not null,
  year                VARCHAR2(4) not null,
  period              VARCHAR2(2) not null,
  entity              VARCHAR2(50) not null,
  entityname          VARCHAR2(240) not null,
  premises            VARCHAR2(500) not null,
  referenceno         VARCHAR2(50) not null,
  rentalperiodfrom    DATE not null,
  rentalperiodto      DATE not null,
  originalcurrency    VARCHAR2(3) not null,
  totalrental         NUMBER not null,
  monthlyrental       NUMBER not null,
  exchangerate        NUMBER not null,
  monthlyrentalusd    NUMBER not null,
  contractrequirement VARCHAR2(50) not null,
  includeotherexpense VARCHAR2(50) not null,
  rentalrenewable     VARCHAR2(50) not null,
  rate                NUMBER not null,
  loworshort          VARCHAR2(50) not null,
  note                VARCHAR2(240)
)
;
comment on table FIT_AUDIT_LESSEE
  is 'Lessee_承租人';
comment on column FIT_AUDIT_LESSEE.generatetype
  is 'GenerateType_標識';
comment on column FIT_AUDIT_LESSEE.year
  is 'Year_年份';
comment on column FIT_AUDIT_LESSEE.period
  is 'Period_月份';
comment on column FIT_AUDIT_LESSEE.entity
  is 'Company code_法人編碼';
comment on column FIT_AUDIT_LESSEE.entityname
  is 'Company name_法人名稱';
comment on column FIT_AUDIT_LESSEE.premises
  is 'Premises(Both Local add \ English Add)/Lease object_(當地地址\英文地址)/租賃標的';
comment on column FIT_AUDIT_LESSEE.referenceno
  is 'Contract Reference No._合約號碼';
comment on column FIT_AUDIT_LESSEE.rentalperiodfrom
  is 'Rental Period (from)_起租日';
comment on column FIT_AUDIT_LESSEE.rentalperiodto
  is 'Rental Period (to)_迄租日';
comment on column FIT_AUDIT_LESSEE.originalcurrency
  is 'Original Currency_原幣別';
comment on column FIT_AUDIT_LESSEE.totalrental
  is 'Total Rental (Original currency)_總期間租金(原幣別)';
comment on column FIT_AUDIT_LESSEE.monthlyrental
  is 'Monthly Rental (Original currency)_每月租金 (原幣別)';
comment on column FIT_AUDIT_LESSEE.exchangerate
  is 'Exchange rate_匯率';
comment on column FIT_AUDIT_LESSEE.monthlyrentalusd
  is 'Monthly Rental (USD)_每月租金(換算成USD)
';
comment on column FIT_AUDIT_LESSEE.contractrequirement
  is 'Contract Requirement(rental included tax / rental excluded tax)_合約條件(租金是否含稅)';
comment on column FIT_AUDIT_LESSEE.includeotherexpense
  is 'Rental include other expense_每月租金是否包含變動給付';
comment on column FIT_AUDIT_LESSEE.rentalrenewable
  is 'Rental renewable_是否有續租權
(是否有延長租賃之選擇權)';
comment on column FIT_AUDIT_LESSEE.rate
  is 'Lease rate / incremental borrowing rate_租賃隱含利率 / 承租人借款利率';
comment on column FIT_AUDIT_LESSEE.loworshort
  is 'Low value (less USD5,000) or short lease (less than one year)_是否屬低價值(小於USD5,000)或是短期租賃(小於一年)';
comment on column FIT_AUDIT_LESSEE.note
  is 'Note_備註';
alter table FIT_AUDIT_LESSEE
  add constraint FIT_AUDIT_LESSEE_PK primary key (YEAR, PERIOD, ENTITY, REFERENCENO);

prompt
prompt Creating table FIT_AUDIT_LESSEE_C
prompt =================================
prompt
create table FIT_AUDIT_LESSEE_C
(
  seq                 VARCHAR2(10) not null,
  year                VARCHAR2(4) not null,
  period              VARCHAR2(2) not null,
  entity              VARCHAR2(50) not null,
  entityname          VARCHAR2(240) not null,
  premises            VARCHAR2(500) not null,
  referenceno         VARCHAR2(50) not null,
  rentalperiodfrom    DATE not null,
  rentalperiodto      DATE not null,
  originalcurrency    VARCHAR2(3) not null,
  totalrental         NUMBER not null,
  monthlyrental       NUMBER not null,
  exchangerate        NUMBER not null,
  monthlyrentalusd    NUMBER not null,
  monthleft           NUMBER,
  rentless1y          NUMBER,
  rent1t2y            NUMBER,
  rent2t3y            NUMBER,
  rent3t4y            NUMBER,
  rent4t5y            NUMBER,
  rentover5y          NUMBER,
  renttotal           NUMBER,
  rentdueleft         VARCHAR2(50),
  contractrequirement VARCHAR2(50) not null,
  includeotherexpense VARCHAR2(50) not null,
  rentalrenewable     VARCHAR2(50) not null,
  rate                NUMBER not null,
  loworshort          VARCHAR2(50) not null,
  note                VARCHAR2(240)
)
;
comment on table FIT_AUDIT_LESSEE_C
  is 'Lessee_承租人';
comment on column FIT_AUDIT_LESSEE_C.year
  is 'Year_年份';
comment on column FIT_AUDIT_LESSEE_C.period
  is 'Period_月份';
comment on column FIT_AUDIT_LESSEE_C.entity
  is 'Company code_法人編碼';
comment on column FIT_AUDIT_LESSEE_C.entityname
  is 'Company name_法人名稱';
comment on column FIT_AUDIT_LESSEE_C.premises
  is 'Premises(Both Local add \ English Add)/Lease object_(當地地址\英文地址)/租賃標的';
comment on column FIT_AUDIT_LESSEE_C.referenceno
  is 'Contract Reference No._合約號碼';
comment on column FIT_AUDIT_LESSEE_C.rentalperiodfrom
  is 'Rental Period (from)_起租日';
comment on column FIT_AUDIT_LESSEE_C.rentalperiodto
  is 'Rental Period (to)_迄租日';
comment on column FIT_AUDIT_LESSEE_C.originalcurrency
  is 'Original Currency_原幣別';
comment on column FIT_AUDIT_LESSEE_C.totalrental
  is 'Total Rental (Original currency)_總期間租金(原幣別)';
comment on column FIT_AUDIT_LESSEE_C.monthlyrental
  is 'Monthly Rental (Original currency)_每月租金 (原幣別)';
comment on column FIT_AUDIT_LESSEE_C.exchangerate
  is 'Exchange rate_匯率';
comment on column FIT_AUDIT_LESSEE_C.monthlyrentalusd
  is 'Monthly Rental (USD)_每月租金(換算成USD)
';
comment on column FIT_AUDIT_LESSEE_C.contractrequirement
  is 'Contract Requirement(rental included tax / rental excluded tax)_合約條件(租金是否含稅)';
comment on column FIT_AUDIT_LESSEE_C.includeotherexpense
  is 'Rental include other expense_每月租金是否包含變動給付';
comment on column FIT_AUDIT_LESSEE_C.rentalrenewable
  is 'Rental renewable_是否有續租權
(是否有延長租賃之選擇權)';
comment on column FIT_AUDIT_LESSEE_C.rate
  is 'Lease rate / incremental borrowing rate_租賃隱含利率 / 承租人借款利率';
comment on column FIT_AUDIT_LESSEE_C.loworshort
  is 'Low value (less USD5,000) or short lease (less than one year)_是否屬低價值(小於USD5,000)或是短期租賃(小於一年)';
comment on column FIT_AUDIT_LESSEE_C.note
  is 'Note_備註';
alter table FIT_AUDIT_LESSEE_C
  add constraint FIT_AUDIT_LESSEE_C_PK primary key (YEAR, PERIOD, ENTITY, REFERENCENO);

prompt
prompt Creating table FIT_AUDIT_LESSEE_T
prompt =================================
prompt
create table FIT_AUDIT_LESSEE_T
(
  generatetype        VARCHAR2(2) not null,
  year                VARCHAR2(4) not null,
  period              VARCHAR2(2) not null,
  entity              VARCHAR2(50) not null,
  entityname          VARCHAR2(240) not null,
  premises            VARCHAR2(500) not null,
  referenceno         VARCHAR2(50) not null,
  rentalperiodfrom    DATE not null,
  rentalperiodto      DATE not null,
  originalcurrency    VARCHAR2(3) not null,
  totalrental         NUMBER not null,
  monthlyrental       NUMBER not null,
  exchangerate        NUMBER not null,
  monthlyrentalusd    NUMBER not null,
  contractrequirement VARCHAR2(50) not null,
  includeotherexpense VARCHAR2(50) not null,
  rentalrenewable     VARCHAR2(50) not null,
  rate                NUMBER not null,
  loworshort          VARCHAR2(50) not null,
  note                VARCHAR2(240)
)
;
comment on table FIT_AUDIT_LESSEE_T
  is 'Lessee_承租人';
comment on column FIT_AUDIT_LESSEE_T.generatetype
  is 'GenerateType_標識';
comment on column FIT_AUDIT_LESSEE_T.year
  is 'Year_年份';
comment on column FIT_AUDIT_LESSEE_T.period
  is 'Period_月份';
comment on column FIT_AUDIT_LESSEE_T.entity
  is 'Company code_法人編碼';
comment on column FIT_AUDIT_LESSEE_T.entityname
  is 'Company name_法人名稱';
comment on column FIT_AUDIT_LESSEE_T.premises
  is 'Premises(Both Local add \ English Add)/Lease object_(當地地址\英文地址)/租賃標的';
comment on column FIT_AUDIT_LESSEE_T.referenceno
  is 'Contract Reference No._合約號碼';
comment on column FIT_AUDIT_LESSEE_T.rentalperiodfrom
  is 'Rental Period (from)_起租日';
comment on column FIT_AUDIT_LESSEE_T.rentalperiodto
  is 'Rental Period (to)_迄租日';
comment on column FIT_AUDIT_LESSEE_T.originalcurrency
  is 'Original Currency_原幣別';
comment on column FIT_AUDIT_LESSEE_T.totalrental
  is 'Total Rental (Original currency)_總期間租金(原幣別)';
comment on column FIT_AUDIT_LESSEE_T.monthlyrental
  is 'Monthly Rental (Original currency)_每月租金 (原幣別)';
comment on column FIT_AUDIT_LESSEE_T.exchangerate
  is 'Exchange rate_匯率';
comment on column FIT_AUDIT_LESSEE_T.monthlyrentalusd
  is 'Monthly Rental (USD)_每月租金(換算成USD)';
comment on column FIT_AUDIT_LESSEE_T.contractrequirement
  is 'Contract Requirement(rental included tax / rental excluded tax)_合約條件(租金是否含稅)';
comment on column FIT_AUDIT_LESSEE_T.includeotherexpense
  is 'Rental include other expense_每月租金是否包含變動給付';
comment on column FIT_AUDIT_LESSEE_T.rentalrenewable
  is 'Rental renewable_是否有續租權
(是否有延長租賃之選擇權)';
comment on column FIT_AUDIT_LESSEE_T.rate
  is 'Lease rate / incremental borrowing rate_租賃隱含利率 / 承租人借款利率';
comment on column FIT_AUDIT_LESSEE_T.loworshort
  is 'Low value (less USD5,000) or short lease (less than one year)_是否屬低價值(小於USD5,000)或是短期租賃(小於一年)';
comment on column FIT_AUDIT_LESSEE_T.note
  is 'Note_備註';
alter table FIT_AUDIT_LESSEE_T
  add constraint FIT_AUDIT_LESSEE_T_PK primary key (YEAR, PERIOD, ENTITY, REFERENCENO);

prompt
prompt Creating table FIT_AUDIT_LESSOR
prompt ===============================
prompt
create table FIT_AUDIT_LESSOR
(
  generatetype        VARCHAR2(2) not null,
  year                VARCHAR2(4) not null,
  period              VARCHAR2(2) not null,
  entity              VARCHAR2(50) not null,
  entityname          VARCHAR2(240) not null,
  premises            VARCHAR2(500) not null,
  referenceno         VARCHAR2(50) not null,
  rentalperiodfrom    DATE not null,
  rentalperiodto      DATE not null,
  originalcurrency    VARCHAR2(3) not null,
  totalrental         NUMBER not null,
  monthlyrental       NUMBER not null,
  exchangerate        NUMBER not null,
  monthlyrentalusd    NUMBER not null,
  contractrequirement VARCHAR2(50) not null,
  includeotherexpense VARCHAR2(50) not null,
  rentalrenewable     VARCHAR2(50) not null,
  rate                NUMBER not null,
  loworshort          VARCHAR2(50) not null,
  note                VARCHAR2(240)
)
;
comment on table FIT_AUDIT_LESSOR
  is 'Lessor_出租人';
comment on column FIT_AUDIT_LESSOR.generatetype
  is 'GenerateType_標識';
comment on column FIT_AUDIT_LESSOR.year
  is 'Year_年份';
comment on column FIT_AUDIT_LESSOR.period
  is 'Period_月份';
comment on column FIT_AUDIT_LESSOR.entity
  is 'Company code_法人編碼';
comment on column FIT_AUDIT_LESSOR.entityname
  is 'Company name_法人名稱';
comment on column FIT_AUDIT_LESSOR.premises
  is 'Premises(Both Local add \ English Add)/Lease object_(當地地址\英文地址)/租賃標的';
comment on column FIT_AUDIT_LESSOR.referenceno
  is 'Contract Reference No._合約號碼';
comment on column FIT_AUDIT_LESSOR.rentalperiodfrom
  is 'Rental Period (from)_起租日';
comment on column FIT_AUDIT_LESSOR.rentalperiodto
  is 'Rental Period (to)_迄租日';
comment on column FIT_AUDIT_LESSOR.originalcurrency
  is 'Original Currency_原幣別';
comment on column FIT_AUDIT_LESSOR.totalrental
  is 'Total Rental (Original currency)_總期間租金(原幣別)';
comment on column FIT_AUDIT_LESSOR.monthlyrental
  is 'Monthly Rental (Original currency)_每月租金 (原幣別)';
comment on column FIT_AUDIT_LESSOR.exchangerate
  is 'Exchange rate_匯率';
comment on column FIT_AUDIT_LESSOR.monthlyrentalusd
  is 'Monthly Rental (USD)_每月租金(換算成USD)';
comment on column FIT_AUDIT_LESSOR.contractrequirement
  is 'Contract Requirement(rental included tax / rental excluded tax)_合約條件(租金是否含稅)';
comment on column FIT_AUDIT_LESSOR.includeotherexpense
  is 'Rental include other expense_每月租金是否包含變動給付';
comment on column FIT_AUDIT_LESSOR.rentalrenewable
  is 'Rental renewable_是否有續租權
(是否有延長租賃之選擇權)';
comment on column FIT_AUDIT_LESSOR.rate
  is 'Lease rate / incremental borrowing rate_租賃隱含利率 / 承租人借款利率';
comment on column FIT_AUDIT_LESSOR.loworshort
  is 'Low value (less USD5,000) or short lease (less than one year)_是否屬低價值(小於USD5,000)或是短期租賃(小於一年)';
comment on column FIT_AUDIT_LESSOR.note
  is 'Note_備註';
alter table FIT_AUDIT_LESSOR
  add constraint FIT_AUDIT_LESSOR_PK primary key (YEAR, PERIOD, ENTITY, REFERENCENO);

prompt
prompt Creating table FIT_AUDIT_LESSOR_C
prompt =================================
prompt
create table FIT_AUDIT_LESSOR_C
(
  seq                 VARCHAR2(10) not null,
  year                VARCHAR2(4) not null,
  period              VARCHAR2(2) not null,
  entity              VARCHAR2(50) not null,
  entityname          VARCHAR2(240) not null,
  premises            VARCHAR2(500) not null,
  referenceno         VARCHAR2(50) not null,
  rentalperiodfrom    DATE not null,
  rentalperiodto      DATE not null,
  originalcurrency    VARCHAR2(3) not null,
  totalrental         NUMBER not null,
  monthlyrental       NUMBER not null,
  exchangerate        NUMBER not null,
  monthlyrentalusd    NUMBER not null,
  monthleft           NUMBER,
  rentless1y          NUMBER,
  rent1t2y            NUMBER,
  rent2t3y            NUMBER,
  rent3t4y            NUMBER,
  rent4t5y            NUMBER,
  rentover5y          NUMBER,
  renttotal           NUMBER,
  rentdueleft         VARCHAR2(50),
  contractrequirement VARCHAR2(50) not null,
  includeotherexpense VARCHAR2(50) not null,
  rentalrenewable     VARCHAR2(50) not null,
  rate                NUMBER not null,
  loworshort          VARCHAR2(50) not null,
  note                VARCHAR2(240)
)
;
comment on table FIT_AUDIT_LESSOR_C
  is 'Lessee_承租人';
comment on column FIT_AUDIT_LESSOR_C.year
  is 'Year_年份';
comment on column FIT_AUDIT_LESSOR_C.period
  is 'Period_月份';
comment on column FIT_AUDIT_LESSOR_C.entity
  is 'Company code_法人編碼';
comment on column FIT_AUDIT_LESSOR_C.entityname
  is 'Company name_法人名稱';
comment on column FIT_AUDIT_LESSOR_C.premises
  is 'Premises(Both Local add \ English Add)/Lease object_(當地地址\英文地址)/租賃標的';
comment on column FIT_AUDIT_LESSOR_C.referenceno
  is 'Contract Reference No._合約號碼';
comment on column FIT_AUDIT_LESSOR_C.rentalperiodfrom
  is 'Rental Period (from)_起租日';
comment on column FIT_AUDIT_LESSOR_C.rentalperiodto
  is 'Rental Period (to)_迄租日';
comment on column FIT_AUDIT_LESSOR_C.originalcurrency
  is 'Original Currency_原幣別';
comment on column FIT_AUDIT_LESSOR_C.totalrental
  is 'Total Rental (Original currency)_總期間租金(原幣別)';
comment on column FIT_AUDIT_LESSOR_C.monthlyrental
  is 'Monthly Rental (Original currency)_每月租金 (原幣別)';
comment on column FIT_AUDIT_LESSOR_C.exchangerate
  is 'Exchange rate_匯率';
comment on column FIT_AUDIT_LESSOR_C.monthlyrentalusd
  is 'Monthly Rental (USD)_每月租金(換算成USD)
';
comment on column FIT_AUDIT_LESSOR_C.contractrequirement
  is 'Contract Requirement(rental included tax / rental excluded tax)_合約條件(租金是否含稅)';
comment on column FIT_AUDIT_LESSOR_C.includeotherexpense
  is 'Rental include other expense_每月租金是否包含變動給付';
comment on column FIT_AUDIT_LESSOR_C.rentalrenewable
  is 'Rental renewable_是否有續租權
(是否有延長租賃之選擇權)';
comment on column FIT_AUDIT_LESSOR_C.rate
  is 'Lease rate / incremental borrowing rate_租賃隱含利率 / 承租人借款利率';
comment on column FIT_AUDIT_LESSOR_C.loworshort
  is 'Low value (less USD5,000) or short lease (less than one year)_是否屬低價值(小於USD5,000)或是短期租賃(小於一年)';
comment on column FIT_AUDIT_LESSOR_C.note
  is 'Note_備註';
alter table FIT_AUDIT_LESSOR_C
  add constraint FIT_AUDIT_LESSOR_C_PK primary key (YEAR, PERIOD, ENTITY, REFERENCENO);

prompt
prompt Creating table FIT_AUDIT_LESSOR_T
prompt =================================
prompt
create table FIT_AUDIT_LESSOR_T
(
  generatetype        VARCHAR2(2) not null,
  year                VARCHAR2(4) not null,
  period              VARCHAR2(2) not null,
  entity              VARCHAR2(50) not null,
  entityname          VARCHAR2(240) not null,
  premises            VARCHAR2(500) not null,
  referenceno         VARCHAR2(50) not null,
  rentalperiodfrom    DATE not null,
  rentalperiodto      DATE not null,
  originalcurrency    VARCHAR2(3) not null,
  totalrental         NUMBER not null,
  monthlyrental       NUMBER not null,
  exchangerate        NUMBER not null,
  monthlyrentalusd    NUMBER not null,
  contractrequirement VARCHAR2(50) not null,
  includeotherexpense VARCHAR2(50) not null,
  rentalrenewable     VARCHAR2(50) not null,
  rate                NUMBER not null,
  loworshort          VARCHAR2(50) not null,
  note                VARCHAR2(240)
)
;
comment on table FIT_AUDIT_LESSOR_T
  is 'Lessor_出租人';
comment on column FIT_AUDIT_LESSOR_T.generatetype
  is 'GenerateType_標識';
comment on column FIT_AUDIT_LESSOR_T.year
  is 'Year_年份';
comment on column FIT_AUDIT_LESSOR_T.period
  is 'Period_月份';
comment on column FIT_AUDIT_LESSOR_T.entity
  is 'Company code_法人編碼';
comment on column FIT_AUDIT_LESSOR_T.entityname
  is 'Company name_法人名稱';
comment on column FIT_AUDIT_LESSOR_T.premises
  is 'Premises(Both Local add \ English Add)/Lease object_(當地地址\英文地址)/租賃標的';
comment on column FIT_AUDIT_LESSOR_T.referenceno
  is 'Contract Reference No._合約號碼';
comment on column FIT_AUDIT_LESSOR_T.rentalperiodfrom
  is 'Rental Period (from)_起租日';
comment on column FIT_AUDIT_LESSOR_T.rentalperiodto
  is 'Rental Period (to)_迄租日';
comment on column FIT_AUDIT_LESSOR_T.originalcurrency
  is 'Original Currency_原幣別';
comment on column FIT_AUDIT_LESSOR_T.totalrental
  is 'Total Rental (Original currency)_總期間租金(原幣別)';
comment on column FIT_AUDIT_LESSOR_T.monthlyrental
  is 'Monthly Rental (Original currency)_每月租金 (原幣別)';
comment on column FIT_AUDIT_LESSOR_T.exchangerate
  is 'Exchange rate_匯率';
comment on column FIT_AUDIT_LESSOR_T.monthlyrentalusd
  is 'Monthly Rental (USD)_每月租金(換算成USD)';
comment on column FIT_AUDIT_LESSOR_T.contractrequirement
  is 'Contract Requirement(rental included tax / rental excluded tax)_合約條件(租金是否含稅)';
comment on column FIT_AUDIT_LESSOR_T.includeotherexpense
  is 'Rental include other expense_每月租金是否包含變動給付';
comment on column FIT_AUDIT_LESSOR_T.rentalrenewable
  is 'Rental renewable_是否有續租權
(是否有延長租賃之選擇權)';
comment on column FIT_AUDIT_LESSOR_T.rate
  is 'Lease rate / incremental borrowing rate_租賃隱含利率 / 承租人借款利率';
comment on column FIT_AUDIT_LESSOR_T.loworshort
  is 'Low value (less USD5,000) or short lease (less than one year)_是否屬低價值(小於USD5,000)或是短期租賃(小於一年)';
comment on column FIT_AUDIT_LESSOR_T.note
  is 'Note_備註';
alter table FIT_AUDIT_LESSOR_T
  add constraint FIT_AUDIT_LESSOR_T_PK primary key (YEAR, PERIOD, ENTITY, REFERENCENO);

prompt
prompt Creating table FIT_AUDIT_OP_DETAIL
prompt ==================================
prompt
create table FIT_AUDIT_OP_DETAIL
(
  generatetype             VARCHAR2(2) not null,
  year                     VARCHAR2(4) not null,
  period                   VARCHAR2(2) not null,
  entity                   VARCHAR2(50) not null,
  entityname               VARCHAR2(240) not null,
  voucherno                VARCHAR2(20) not null,
  voucherdate              DATE not null,
  packageacc               VARCHAR2(10) not null,
  acctcode                 VARCHAR2(20) not null,
  acctname                 VARCHAR2(50) not null,
  departmentno             VARCHAR2(50) not null,
  debit_credit             VARCHAR2(20) not null,
  originalcurrency         VARCHAR2(3) not null,
  originalamount           NUMBER not null,
  functionalcurrency       VARCHAR2(3) not null,
  functionalcurrencyamount NUMBER not null,
  note                     VARCHAR2(300) not null
)
;
comment on table FIT_AUDIT_OP_DETAIL
  is 'Other payables_其他應付款明細表';
comment on column FIT_AUDIT_OP_DETAIL.generatetype
  is 'GenerateType_標識';
comment on column FIT_AUDIT_OP_DETAIL.year
  is 'Year_年份';
comment on column FIT_AUDIT_OP_DETAIL.period
  is 'Period_月份';
comment on column FIT_AUDIT_OP_DETAIL.entity
  is 'Company code_法人編碼';
comment on column FIT_AUDIT_OP_DETAIL.entityname
  is 'Company name_法人名稱';
comment on column FIT_AUDIT_OP_DETAIL.voucherno
  is 'Voucher No._傳票編號';
comment on column FIT_AUDIT_OP_DETAIL.voucherdate
  is 'Voucher date_傳票日期';
comment on column FIT_AUDIT_OP_DETAIL.packageacc
  is 'Package account_Package科目';
comment on column FIT_AUDIT_OP_DETAIL.acctcode
  is 'Account code_科目編號';
comment on column FIT_AUDIT_OP_DETAIL.acctname
  is 'Account name_科目名稱';
comment on column FIT_AUDIT_OP_DETAIL.departmentno
  is 'Department number_部門代碼';
comment on column FIT_AUDIT_OP_DETAIL.debit_credit
  is 'D/C_借/貸';
comment on column FIT_AUDIT_OP_DETAIL.originalcurrency
  is 'Original currency_原幣幣別';
comment on column FIT_AUDIT_OP_DETAIL.originalamount
  is 'Original amount_原幣金額';
comment on column FIT_AUDIT_OP_DETAIL.functionalcurrency
  is 'Functional currency_本幣幣別';
comment on column FIT_AUDIT_OP_DETAIL.functionalcurrencyamount
  is 'Functional currency amount_本幣金額';
comment on column FIT_AUDIT_OP_DETAIL.note
  is 'Voucher description
_傳票摘要';
alter table FIT_AUDIT_OP_DETAIL
  add constraint FIT_AUDIT_OP_PK primary key (YEAR, PERIOD, ENTITY, VOUCHERNO, ACCTCODE, ORIGINALCURRENCY, DEPARTMENTNO);

prompt
prompt Creating table FIT_AUDIT_OP_DETAIL_T
prompt ====================================
prompt
create table FIT_AUDIT_OP_DETAIL_T
(
  generatetype             VARCHAR2(2) not null,
  year                     VARCHAR2(4) not null,
  period                   VARCHAR2(2) not null,
  entity                   VARCHAR2(50) not null,
  entityname               VARCHAR2(240) not null,
  voucherno                VARCHAR2(20) not null,
  voucherdate              DATE not null,
  packageacc               VARCHAR2(10) not null,
  acctcode                 VARCHAR2(20) not null,
  acctname                 VARCHAR2(50) not null,
  departmentno             VARCHAR2(50) not null,
  debit_credit             VARCHAR2(20) not null,
  originalcurrency         VARCHAR2(3) not null,
  originalamount           NUMBER not null,
  functionalcurrency       VARCHAR2(3) not null,
  functionalcurrencyamount NUMBER not null,
  note                     VARCHAR2(300) not null
)
;
comment on table FIT_AUDIT_OP_DETAIL_T
  is 'Other payables_其他應付款明細表';
comment on column FIT_AUDIT_OP_DETAIL_T.generatetype
  is 'GenerateType_標識';
comment on column FIT_AUDIT_OP_DETAIL_T.year
  is 'Year_年份';
comment on column FIT_AUDIT_OP_DETAIL_T.period
  is 'Period_月份';
comment on column FIT_AUDIT_OP_DETAIL_T.entity
  is 'Company code_法人編碼';
comment on column FIT_AUDIT_OP_DETAIL_T.entityname
  is 'Company name_法人名稱';
comment on column FIT_AUDIT_OP_DETAIL_T.voucherno
  is 'Voucher No._傳票編號';
comment on column FIT_AUDIT_OP_DETAIL_T.voucherdate
  is 'Voucher date_傳票日期';
comment on column FIT_AUDIT_OP_DETAIL_T.packageacc
  is 'Package account_Package科目';
comment on column FIT_AUDIT_OP_DETAIL_T.acctcode
  is 'Account code_科目編號';
comment on column FIT_AUDIT_OP_DETAIL_T.acctname
  is 'Account name_科目名稱';
comment on column FIT_AUDIT_OP_DETAIL_T.departmentno
  is 'Department number_部門代碼';
comment on column FIT_AUDIT_OP_DETAIL_T.debit_credit
  is 'D/C_借/貸';
comment on column FIT_AUDIT_OP_DETAIL_T.originalcurrency
  is 'Original currency_原幣幣別';
comment on column FIT_AUDIT_OP_DETAIL_T.originalamount
  is 'Original amount_原幣金額';
comment on column FIT_AUDIT_OP_DETAIL_T.functionalcurrency
  is 'Functional currency_本幣幣別';
comment on column FIT_AUDIT_OP_DETAIL_T.functionalcurrencyamount
  is 'Functional currency amount_本幣金額';
comment on column FIT_AUDIT_OP_DETAIL_T.note
  is 'Voucher description
_傳票摘要';
alter table FIT_AUDIT_OP_DETAIL_T
  add constraint FIT_AUDIT_OP_DETAIL_T_PK primary key (YEAR, PERIOD, ENTITY, VOUCHERNO, ACCTCODE, ORIGINALCURRENCY, DEPARTMENTNO);

prompt
prompt Creating table FIT_AUDIT_OR_DETAIL
prompt ==================================
prompt
create table FIT_AUDIT_OR_DETAIL
(
  generatetype             VARCHAR2(2) not null,
  year                     VARCHAR2(4) not null,
  period                   VARCHAR2(2) not null,
  entity                   VARCHAR2(50) not null,
  entityname               VARCHAR2(240) not null,
  packageacc               VARCHAR2(10) not null,
  originalcurrency         VARCHAR2(3) not null,
  originalamount           NUMBER not null,
  functionalcurrency       VARCHAR2(3) not null,
  functionalcurrencyamount NUMBER not null
)
;
comment on table FIT_AUDIT_OR_DETAIL
  is 'Other receivables_其他應收款明細表';
comment on column FIT_AUDIT_OR_DETAIL.generatetype
  is 'GenerateType_標識';
comment on column FIT_AUDIT_OR_DETAIL.year
  is 'Year_年份';
comment on column FIT_AUDIT_OR_DETAIL.period
  is 'Period_月份';
comment on column FIT_AUDIT_OR_DETAIL.entity
  is 'Company code_法人編碼';
comment on column FIT_AUDIT_OR_DETAIL.entityname
  is 'Company name_法人名稱';
comment on column FIT_AUDIT_OR_DETAIL.packageacc
  is 'Package account_Package科目';
comment on column FIT_AUDIT_OR_DETAIL.originalcurrency
  is 'Original currency_原幣幣別';
comment on column FIT_AUDIT_OR_DETAIL.originalamount
  is 'Original amount_原幣金額';
comment on column FIT_AUDIT_OR_DETAIL.functionalcurrency
  is 'Functional currency_本幣幣別';
comment on column FIT_AUDIT_OR_DETAIL.functionalcurrencyamount
  is 'Functional currency amount_本幣金額';
alter table FIT_AUDIT_OR_DETAIL
  add constraint FIT_AUDIT_OR_DETAIL_PK primary key (YEAR, PERIOD, ENTITY, PACKAGEACC, ORIGINALCURRENCY);

prompt
prompt Creating table FIT_AUDIT_OR_DETAIL_T
prompt ====================================
prompt
create table FIT_AUDIT_OR_DETAIL_T
(
  generatetype             VARCHAR2(2) not null,
  year                     VARCHAR2(4) not null,
  period                   VARCHAR2(2) not null,
  entity                   VARCHAR2(50) not null,
  entityname               VARCHAR2(240) not null,
  packageacc               VARCHAR2(10) not null,
  originalcurrency         VARCHAR2(3) not null,
  originalamount           NUMBER not null,
  functionalcurrency       VARCHAR2(3) not null,
  functionalcurrencyamount NUMBER not null
)
;
comment on table FIT_AUDIT_OR_DETAIL_T
  is 'Other receivables_其他應收款明細表';
comment on column FIT_AUDIT_OR_DETAIL_T.generatetype
  is 'GenerateType_標識';
comment on column FIT_AUDIT_OR_DETAIL_T.year
  is 'Year_年份';
comment on column FIT_AUDIT_OR_DETAIL_T.period
  is 'Period_月份';
comment on column FIT_AUDIT_OR_DETAIL_T.entity
  is 'Company code_法人編碼';
comment on column FIT_AUDIT_OR_DETAIL_T.entityname
  is 'Company name_法人名稱';
comment on column FIT_AUDIT_OR_DETAIL_T.packageacc
  is 'Package account_Package科目';
comment on column FIT_AUDIT_OR_DETAIL_T.originalcurrency
  is 'Original currency_原幣幣別';
comment on column FIT_AUDIT_OR_DETAIL_T.originalamount
  is 'Original amount_原幣金額';
comment on column FIT_AUDIT_OR_DETAIL_T.functionalcurrency
  is 'Functional currency_本幣幣別';
comment on column FIT_AUDIT_OR_DETAIL_T.functionalcurrencyamount
  is 'Functional currency amount_本幣金額';
alter table FIT_AUDIT_OR_DETAIL_T
  add constraint FIT_AUDIT_OR_DETAIL_T_PK primary key (YEAR, PERIOD, ENTITY, PACKAGEACC, ORIGINALCURRENCY);

prompt
prompt Creating table FIT_AUDIT_PURCHASE_DETAIL
prompt ========================================
prompt
create table FIT_AUDIT_PURCHASE_DETAIL
(
  generatetype      VARCHAR2(2) not null,
  year              VARCHAR2(4) not null,
  period            VARCHAR2(2) not null,
  entity            VARCHAR2(50) not null,
  entityname        VARCHAR2(240) not null,
  custcode          VARCHAR2(100) not null,
  custname          VARCHAR2(240) not null,
  custtype          VARCHAR2(100) not null,
  paymentterms      VARCHAR2(100) not null,
  currency          VARCHAR2(3) not null,
  transactionamount NUMBER not null,
  defaultcurrency   VARCHAR2(3) not null,
  defaultamount     NUMBER not null
)
;
comment on table FIT_AUDIT_PURCHASE_DETAIL
  is 'Purchase detail_進貨明細表';
comment on column FIT_AUDIT_PURCHASE_DETAIL.generatetype
  is 'GenerateType_標識';
comment on column FIT_AUDIT_PURCHASE_DETAIL.year
  is 'Year_年份';
comment on column FIT_AUDIT_PURCHASE_DETAIL.period
  is 'Period_月份';
comment on column FIT_AUDIT_PURCHASE_DETAIL.entity
  is 'Company code_法人編碼';
comment on column FIT_AUDIT_PURCHASE_DETAIL.entityname
  is 'Company name_法人名稱';
comment on column FIT_AUDIT_PURCHASE_DETAIL.custcode
  is 'Supplier code_供應商代碼';
comment on column FIT_AUDIT_PURCHASE_DETAIL.custname
  is 'Supplier name_供應商名稱';
comment on column FIT_AUDIT_PURCHASE_DETAIL.custtype
  is 'FIT group/related party/3rd party_合併事業體/非合併事業體及關係人/3rd party';
comment on column FIT_AUDIT_PURCHASE_DETAIL.paymentterms
  is 'Payment term_付款條件';
comment on column FIT_AUDIT_PURCHASE_DETAIL.currency
  is 'Original currency_交易原幣幣別';
comment on column FIT_AUDIT_PURCHASE_DETAIL.transactionamount
  is 'Original amount_交易原幣金額';
comment on column FIT_AUDIT_PURCHASE_DETAIL.defaultcurrency
  is 'Functional currency_交易本幣幣別';
comment on column FIT_AUDIT_PURCHASE_DETAIL.defaultamount
  is 'Functional currency amount_交易本幣金額';
alter table FIT_AUDIT_PURCHASE_DETAIL
  add constraint PK_FIT_AUDIT_PURCHASE_DETAIL primary key (YEAR, PERIOD, ENTITY, CUSTCODE, CURRENCY)
  disable
  novalidate;

prompt
prompt Creating table FIT_AUDIT_PURCHASE_DETAIL_T
prompt ==========================================
prompt
create table FIT_AUDIT_PURCHASE_DETAIL_T
(
  generatetype      VARCHAR2(2) not null,
  year              VARCHAR2(4) not null,
  period            VARCHAR2(2) not null,
  entity            VARCHAR2(50) not null,
  entityname        VARCHAR2(240) not null,
  custcode          VARCHAR2(100) not null,
  custname          VARCHAR2(240) not null,
  custtype          VARCHAR2(100) not null,
  paymentterms      VARCHAR2(100) not null,
  currency          VARCHAR2(3) not null,
  transactionamount NUMBER not null,
  defaultcurrency   VARCHAR2(3) not null,
  defaultamount     NUMBER not null
)
;
comment on table FIT_AUDIT_PURCHASE_DETAIL_T
  is 'Purchase detail_進貨明細表';
comment on column FIT_AUDIT_PURCHASE_DETAIL_T.generatetype
  is 'GenerateType_標識';
comment on column FIT_AUDIT_PURCHASE_DETAIL_T.year
  is 'Year_年份';
comment on column FIT_AUDIT_PURCHASE_DETAIL_T.period
  is 'Period_月份';
comment on column FIT_AUDIT_PURCHASE_DETAIL_T.entity
  is 'Company code_法人編碼';
comment on column FIT_AUDIT_PURCHASE_DETAIL_T.entityname
  is 'Company name_法人名稱';
comment on column FIT_AUDIT_PURCHASE_DETAIL_T.custcode
  is 'Supplier code_供應商代碼';
comment on column FIT_AUDIT_PURCHASE_DETAIL_T.custname
  is 'Supplier name_供應商名稱';
comment on column FIT_AUDIT_PURCHASE_DETAIL_T.custtype
  is 'FIT group/related party/3rd party_合併事業體/非合併事業體及關係人/3rd party';
comment on column FIT_AUDIT_PURCHASE_DETAIL_T.paymentterms
  is 'Payment term_付款條件';
comment on column FIT_AUDIT_PURCHASE_DETAIL_T.currency
  is 'Original currency_交易原幣幣別';
comment on column FIT_AUDIT_PURCHASE_DETAIL_T.transactionamount
  is 'Original amount_交易原幣金額';
comment on column FIT_AUDIT_PURCHASE_DETAIL_T.defaultcurrency
  is 'Functional currency_交易本幣幣別';
comment on column FIT_AUDIT_PURCHASE_DETAIL_T.defaultamount
  is 'Functional currency amount_交易本幣金額';
alter table FIT_AUDIT_PURCHASE_DETAIL_T
  add constraint PK_FIT_AUDIT_PURCHASE_DETAIL_T primary key (YEAR, PERIOD, ENTITY, CUSTCODE, CURRENCY)
  disable
  novalidate;

prompt
prompt Creating table FIT_AUDIT_PURCHASE_TOP20
prompt =======================================
prompt
create table FIT_AUDIT_PURCHASE_TOP20
(
  sort_nbr   NUMBER,
  year       VARCHAR2(4) not null,
  period     VARCHAR2(2) not null,
  cust       VARCHAR2(240),
  amount     NUMBER default 0,
  proportion NUMBER,
  custtype   VARCHAR2(240)
)
;
comment on table FIT_AUDIT_PURCHASE_TOP20
  is 'Purchase Top20';
comment on column FIT_AUDIT_PURCHASE_TOP20.sort_nbr
  is '排行榜';
comment on column FIT_AUDIT_PURCHASE_TOP20.year
  is 'Year_年份(審計時間)';
comment on column FIT_AUDIT_PURCHASE_TOP20.period
  is 'Period_月份';
comment on column FIT_AUDIT_PURCHASE_TOP20.cust
  is '供應商所屬集團';
comment on column FIT_AUDIT_PURCHASE_TOP20.amount
  is '金額-USD';
comment on column FIT_AUDIT_PURCHASE_TOP20.proportion
  is '%';

prompt
prompt Creating table FIT_AUDIT_PURCHASE_TOP5
prompt ======================================
prompt
create table FIT_AUDIT_PURCHASE_TOP5
(
  sort_nbr   NUMBER,
  year       VARCHAR2(4) not null,
  period     VARCHAR2(2) not null,
  cust       VARCHAR2(240),
  amount     NUMBER default 0,
  proportion NUMBER,
  custtype   VARCHAR2(240)
)
;
comment on table FIT_AUDIT_PURCHASE_TOP5
  is 'Purchase Top20';
comment on column FIT_AUDIT_PURCHASE_TOP5.sort_nbr
  is '排行榜';
comment on column FIT_AUDIT_PURCHASE_TOP5.year
  is 'Year_年份(審計時間)';
comment on column FIT_AUDIT_PURCHASE_TOP5.period
  is 'Period_月份';
comment on column FIT_AUDIT_PURCHASE_TOP5.cust
  is '供應商所屬集團';
comment on column FIT_AUDIT_PURCHASE_TOP5.amount
  is '金額-USD';
comment on column FIT_AUDIT_PURCHASE_TOP5.proportion
  is '%';

prompt
prompt Creating table FIT_AUDIT_SALE_DETAIL
prompt ====================================
prompt
create table FIT_AUDIT_SALE_DETAIL
(
  generatetype      VARCHAR2(2) not null,
  year              VARCHAR2(4) not null,
  period            VARCHAR2(2) not null,
  entity            VARCHAR2(50) not null,
  entityname        VARCHAR2(240) not null,
  custcode          VARCHAR2(100) not null,
  custname          VARCHAR2(240) not null,
  custtype          VARCHAR2(100) not null,
  paymentterms      VARCHAR2(100) not null,
  currency          VARCHAR2(3) not null,
  transactionamount NUMBER not null,
  defaultcurrency   VARCHAR2(3) not null,
  defaultamount     NUMBER not null
)
;
comment on table FIT_AUDIT_SALE_DETAIL
  is 'Sales detail_銷貨明細表';
comment on column FIT_AUDIT_SALE_DETAIL.generatetype
  is 'GenerateType_標識';
comment on column FIT_AUDIT_SALE_DETAIL.year
  is 'Year_年份';
comment on column FIT_AUDIT_SALE_DETAIL.period
  is 'Period_月份';
comment on column FIT_AUDIT_SALE_DETAIL.entity
  is 'Company code_法人編碼';
comment on column FIT_AUDIT_SALE_DETAIL.entityname
  is 'Company name_法人名稱';
comment on column FIT_AUDIT_SALE_DETAIL.custcode
  is 'Customer code_客戶代碼';
comment on column FIT_AUDIT_SALE_DETAIL.custname
  is 'Customer name_客戶名稱';
comment on column FIT_AUDIT_SALE_DETAIL.custtype
  is 'FIT group/related party/3rd party_合併事業體/非合併事業體及關係人/3rd party';
comment on column FIT_AUDIT_SALE_DETAIL.paymentterms
  is 'Payment term_收款條件';
comment on column FIT_AUDIT_SALE_DETAIL.currency
  is 'Original currency_交易原幣幣別';
comment on column FIT_AUDIT_SALE_DETAIL.transactionamount
  is 'Original amount_交易原幣金額';
comment on column FIT_AUDIT_SALE_DETAIL.defaultcurrency
  is 'Functional currency_交易本幣幣別';
comment on column FIT_AUDIT_SALE_DETAIL.defaultamount
  is 'Functional currency amount_交易本幣金額';
alter table FIT_AUDIT_SALE_DETAIL
  add constraint PK_FIT_AUDIT_SALE_DETAIL primary key (YEAR, PERIOD, ENTITY, CUSTCODE, CURRENCY);

prompt
prompt Creating table FIT_AUDIT_SALE_DETAIL_T
prompt ======================================
prompt
create table FIT_AUDIT_SALE_DETAIL_T
(
  generatetype      VARCHAR2(2) not null,
  year              VARCHAR2(4) not null,
  period            VARCHAR2(2) not null,
  entity            VARCHAR2(50) not null,
  entityname        VARCHAR2(240) not null,
  custcode          VARCHAR2(100) not null,
  custname          VARCHAR2(240) not null,
  custtype          VARCHAR2(100) not null,
  paymentterms      VARCHAR2(100) not null,
  currency          VARCHAR2(3) not null,
  transactionamount NUMBER not null,
  defaultcurrency   VARCHAR2(3) not null,
  defaultamount     NUMBER not null
)
;
comment on table FIT_AUDIT_SALE_DETAIL_T
  is 'Sales detail_銷貨明細表';
comment on column FIT_AUDIT_SALE_DETAIL_T.generatetype
  is 'GenerateType_標識';
comment on column FIT_AUDIT_SALE_DETAIL_T.year
  is 'Year_年份';
comment on column FIT_AUDIT_SALE_DETAIL_T.period
  is 'Period_月份';
comment on column FIT_AUDIT_SALE_DETAIL_T.entity
  is 'Company code_法人編碼';
comment on column FIT_AUDIT_SALE_DETAIL_T.entityname
  is 'Company name_法人名稱';
comment on column FIT_AUDIT_SALE_DETAIL_T.custcode
  is 'Customer code_客戶代碼';
comment on column FIT_AUDIT_SALE_DETAIL_T.custname
  is 'Customer name_客戶名稱';
comment on column FIT_AUDIT_SALE_DETAIL_T.custtype
  is 'FIT group/related party/3rd party_合併事業體/非合併事業體及關係人/3rd party';
comment on column FIT_AUDIT_SALE_DETAIL_T.paymentterms
  is 'Payment term_收款條件';
comment on column FIT_AUDIT_SALE_DETAIL_T.currency
  is 'Original currency_交易原幣幣別';
comment on column FIT_AUDIT_SALE_DETAIL_T.transactionamount
  is 'Original amount_交易原幣金額';
comment on column FIT_AUDIT_SALE_DETAIL_T.defaultcurrency
  is 'Functional currency_交易本幣幣別';
comment on column FIT_AUDIT_SALE_DETAIL_T.defaultamount
  is 'Functional currency amount_交易本幣金額';
alter table FIT_AUDIT_SALE_DETAIL_T
  add constraint PK_FIT_AUDIT_SALE_DETAIL_T primary key (YEAR, PERIOD, ENTITY, CUSTCODE, CURRENCY);

prompt
prompt Creating table FIT_AUDIT_SALE_TOP10
prompt ===================================
prompt
create table FIT_AUDIT_SALE_TOP10
(
  sort_nbr   NUMBER,
  year       VARCHAR2(4) not null,
  period     VARCHAR2(2) not null,
  region     VARCHAR2(240),
  amount     NUMBER default 0,
  proportion NUMBER
)
;
comment on table FIT_AUDIT_SALE_TOP10
  is 'Purchase Top20';
comment on column FIT_AUDIT_SALE_TOP10.sort_nbr
  is '排行榜';
comment on column FIT_AUDIT_SALE_TOP10.year
  is 'Year_年份(審計時間)';
comment on column FIT_AUDIT_SALE_TOP10.period
  is 'Period_月份';
comment on column FIT_AUDIT_SALE_TOP10.region
  is '地區';
comment on column FIT_AUDIT_SALE_TOP10.amount
  is '金額-USD';
comment on column FIT_AUDIT_SALE_TOP10.proportion
  is '%';

prompt
prompt Creating table FIT_AUDIT_SALE_TOP5
prompt ==================================
prompt
create table FIT_AUDIT_SALE_TOP5
(
  sort_nbr   NUMBER,
  year       VARCHAR2(4) not null,
  period     VARCHAR2(2) not null,
  cust       VARCHAR2(240),
  amount     NUMBER default 0,
  proportion NUMBER,
  custtype   VARCHAR2(240)
)
;
comment on table FIT_AUDIT_SALE_TOP5
  is 'Purchase Top20';
comment on column FIT_AUDIT_SALE_TOP5.sort_nbr
  is '排行榜';
comment on column FIT_AUDIT_SALE_TOP5.year
  is 'Year_年份(審計時間)';
comment on column FIT_AUDIT_SALE_TOP5.period
  is 'Period_月份';
comment on column FIT_AUDIT_SALE_TOP5.cust
  is 'Name of the customers(客戶名稱)';
comment on column FIT_AUDIT_SALE_TOP5.amount
  is '金額-USD';
comment on column FIT_AUDIT_SALE_TOP5.proportion
  is '%';
comment on column FIT_AUDIT_SALE_TOP5.custtype
  is '與集團關係';

prompt
prompt Creating table FIT_AUDIT_SEGMENT_INFO
prompt =====================================
prompt
create table FIT_AUDIT_SEGMENT_INFO
(
  generatetype     VARCHAR2(2) not null,
  year             VARCHAR2(4) not null,
  period           VARCHAR2(2) not null,
  entity           VARCHAR2(20) not null,
  entityname       VARCHAR2(240) not null,
  area             VARCHAR2(20) not null,
  fixedassets      NUMBER not null,
  intangibleassets NUMBER not null,
  dta              NUMBER not null,
  landuseright     NUMBER not null,
  deposits         NUMBER not null,
  ltexpenses       NUMBER not null,
  disposalofass    NUMBER not null,
  deferredexpenses NUMBER not null
)
;
comment on table FIT_AUDIT_SEGMENT_INFO
  is 'Segment Information_分部信息';
comment on column FIT_AUDIT_SEGMENT_INFO.generatetype
  is 'GenerateType_標識';
comment on column FIT_AUDIT_SEGMENT_INFO.year
  is 'Year_年份';
comment on column FIT_AUDIT_SEGMENT_INFO.period
  is 'Period_月份';
comment on column FIT_AUDIT_SEGMENT_INFO.entity
  is 'Company code_法人編碼';
comment on column FIT_AUDIT_SEGMENT_INFO.entityname
  is 'Company name_法人名稱';
comment on column FIT_AUDIT_SEGMENT_INFO.area
  is 'Area(Taiwan/Mainland China/Others)_所在地區(Taiwan/Mainland China/Others)';
comment on column FIT_AUDIT_SEGMENT_INFO.fixedassets
  is '1600 Property, plant and equipment_1600不動產、廠房及設備';
comment on column FIT_AUDIT_SEGMENT_INFO.intangibleassets
  is '1780 Intangible assets_1780無形資產';
comment on column FIT_AUDIT_SEGMENT_INFO.dta
  is '1840 Deferred income tax assets_1840遞延所得稅資產';
comment on column FIT_AUDIT_SEGMENT_INFO.landuseright
  is '1900 Land use right_1900長期預付租金';
comment on column FIT_AUDIT_SEGMENT_INFO.deposits
  is '1900 Deposits, prepayments and other receivables_1900存款、預付款項和其他應收款項';
comment on column FIT_AUDIT_SEGMENT_INFO.ltexpenses
  is '1900 Long-term deferred expenses_1900長期待攤費用';
comment on column FIT_AUDIT_SEGMENT_INFO.disposalofass
  is '1900 Disposal of fixed assets_1900固資清理';
comment on column FIT_AUDIT_SEGMENT_INFO.deferredexpenses
  is '1900 Deferred expenses_1900待攤費用';
alter table FIT_AUDIT_SEGMENT_INFO
  add constraint FIT_AUDIT_SEGMENT_INFO_PK primary key (YEAR, PERIOD, ENTITY, AREA);

prompt
prompt Creating table FIT_AUDIT_SEGMENT_INFO_T
prompt =======================================
prompt
create table FIT_AUDIT_SEGMENT_INFO_T
(
  generatetype     VARCHAR2(2) not null,
  year             VARCHAR2(4) not null,
  period           VARCHAR2(2) not null,
  entity           VARCHAR2(20) not null,
  entityname       VARCHAR2(240) not null,
  area             VARCHAR2(20) not null,
  fixedassets      NUMBER not null,
  intangibleassets NUMBER not null,
  dta              NUMBER not null,
  landuseright     NUMBER not null,
  deposits         NUMBER not null,
  ltexpenses       NUMBER not null,
  disposalofass    NUMBER not null,
  deferredexpenses NUMBER not null
)
;
comment on table FIT_AUDIT_SEGMENT_INFO_T
  is 'Segment Information_分部信息';
comment on column FIT_AUDIT_SEGMENT_INFO_T.generatetype
  is 'GenerateType_標識';
comment on column FIT_AUDIT_SEGMENT_INFO_T.year
  is 'Year_年份';
comment on column FIT_AUDIT_SEGMENT_INFO_T.period
  is 'Period_月份';
comment on column FIT_AUDIT_SEGMENT_INFO_T.entity
  is 'Company code_法人編碼';
comment on column FIT_AUDIT_SEGMENT_INFO_T.entityname
  is 'Company name_法人名稱';
comment on column FIT_AUDIT_SEGMENT_INFO_T.area
  is 'Area(Taiwan/Mainland China/Others)_所在地區(Taiwan/Mainland China/Others)';
comment on column FIT_AUDIT_SEGMENT_INFO_T.fixedassets
  is '1600 Property, plant and equipment_1600不動產、廠房及設備';
comment on column FIT_AUDIT_SEGMENT_INFO_T.intangibleassets
  is '1780 Intangible assets_1780無形資產';
comment on column FIT_AUDIT_SEGMENT_INFO_T.dta
  is '1840 Deferred income tax assets_1840遞延所得稅資產';
comment on column FIT_AUDIT_SEGMENT_INFO_T.landuseright
  is '1900 Land use right_1900長期預付租金';
comment on column FIT_AUDIT_SEGMENT_INFO_T.deposits
  is '1900 Deposits, prepayments and other receivables_1900存款、預付款項和其他應收款項';
comment on column FIT_AUDIT_SEGMENT_INFO_T.ltexpenses
  is '1900 Long-term deferred expenses_1900長期待攤費用';
comment on column FIT_AUDIT_SEGMENT_INFO_T.disposalofass
  is '1900 Disposal of fixed assets_1900固資清理';
comment on column FIT_AUDIT_SEGMENT_INFO_T.deferredexpenses
  is '1900 Deferred expenses_1900待攤費用';
alter table FIT_AUDIT_SEGMENT_INFO_T
  add constraint FIT_AUDIT_SEGMENT_INFO_T_PK primary key (YEAR, PERIOD, ENTITY, AREA);

prompt
prompt Creating table FIT_AUDIT_ST_DEPOSIT
prompt ===================================
prompt
create table FIT_AUDIT_ST_DEPOSIT
(
  generatetype                 VARCHAR2(2) not null,
  year                         VARCHAR2(4) not null,
  period                       VARCHAR2(2) not null,
  entity                       VARCHAR2(20) not null,
  entityname                   VARCHAR2(240) not null,
  accountcode                  VARCHAR2(20) not null,
  accountname                  VARCHAR2(50) not null,
  nature                       VARCHAR2(100) not null,
  depositno                    VARCHAR2(50) not null,
  bank                         VARCHAR2(80) not null,
  bankaccount                  VARCHAR2(100) not null,
  startdate                    DATE not null,
  enddate                      DATE not null,
  rate                         NUMBER not null,
  originalcurrency             VARCHAR2(3) not null,
  balance_originalcurrency     NUMBER not null,
  exchangerate                 NUMBER not null,
  presentationcurrency         VARCHAR2(3) not null,
  balance_presentationcurrency NUMBER not null,
  bank_prc                     VARCHAR2(50) not null
)
;
comment on table FIT_AUDIT_ST_DEPOSIT
  is 'Short-term deposit over 3months_三個月以上定存明細表';
comment on column FIT_AUDIT_ST_DEPOSIT.generatetype
  is 'GenerateType_標識';
comment on column FIT_AUDIT_ST_DEPOSIT.year
  is 'Year_年份';
comment on column FIT_AUDIT_ST_DEPOSIT.period
  is 'Period_月份';
comment on column FIT_AUDIT_ST_DEPOSIT.entity
  is 'Company code_法人編碼';
comment on column FIT_AUDIT_ST_DEPOSIT.entityname
  is 'Company name_法人名稱';
comment on column FIT_AUDIT_ST_DEPOSIT.accountcode
  is 'Account code_科目明細';
comment on column FIT_AUDIT_ST_DEPOSIT.accountname
  is 'Account name_科目名稱';
comment on column FIT_AUDIT_ST_DEPOSIT.nature
  is 'Nature(deposit over 3 months/guarantee deposit)_分類(定期存款-3個月以上/保函保證金)';
comment on column FIT_AUDIT_ST_DEPOSIT.depositno
  is 'Deposit number_定存單號';
comment on column FIT_AUDIT_ST_DEPOSIT.bank
  is 'Bank_銀行名稱';
comment on column FIT_AUDIT_ST_DEPOSIT.bankaccount
  is 'Bank account_銀行帳號';
comment on column FIT_AUDIT_ST_DEPOSIT.startdate
  is 'Start date _起始日';
comment on column FIT_AUDIT_ST_DEPOSIT.enddate
  is 'End date_到期日';
comment on column FIT_AUDIT_ST_DEPOSIT.rate
  is 'Rate_年利率';
comment on column FIT_AUDIT_ST_DEPOSIT.originalcurrency
  is 'Original currency_原幣幣別';
comment on column FIT_AUDIT_ST_DEPOSIT.balance_originalcurrency
  is 'Balance in original currency_原幣金額';
comment on column FIT_AUDIT_ST_DEPOSIT.exchangerate
  is 'Exchange rate_匯率';
comment on column FIT_AUDIT_ST_DEPOSIT.presentationcurrency
  is 'Company''s presentational currency_記帳幣別';
comment on column FIT_AUDIT_ST_DEPOSIT.balance_presentationcurrency
  is 'Balance in company''s presentational currency_月底餘額';
comment on column FIT_AUDIT_ST_DEPOSIT.bank_prc
  is 'Bank in PRC_內地銀行';
alter table FIT_AUDIT_ST_DEPOSIT
  add constraint FIT_AUDIT_ST_DEPOSIT_PK primary key (ENTITY, ACCOUNTCODE, DEPOSITNO, YEAR, PERIOD, ORIGINALCURRENCY);

prompt
prompt Creating table FIT_AUDIT_ST_DEPOSIT_T
prompt =====================================
prompt
create table FIT_AUDIT_ST_DEPOSIT_T
(
  generatetype                 VARCHAR2(2) not null,
  year                         VARCHAR2(4) not null,
  period                       VARCHAR2(2) not null,
  entity                       VARCHAR2(20) not null,
  entityname                   VARCHAR2(240) not null,
  accountcode                  VARCHAR2(20) not null,
  accountname                  VARCHAR2(50) not null,
  nature                       VARCHAR2(100) not null,
  depositno                    VARCHAR2(50) not null,
  bank                         VARCHAR2(80) not null,
  bankaccount                  VARCHAR2(100) not null,
  startdate                    DATE not null,
  enddate                      DATE not null,
  rate                         NUMBER not null,
  originalcurrency             VARCHAR2(3) not null,
  balance_originalcurrency     NUMBER not null,
  exchangerate                 NUMBER not null,
  presentationcurrency         VARCHAR2(3) not null,
  balance_presentationcurrency NUMBER not null,
  bank_prc                     VARCHAR2(50) not null
)
;
comment on table FIT_AUDIT_ST_DEPOSIT_T
  is 'Short-term deposit over 3months_三個月以上定存明細表';
comment on column FIT_AUDIT_ST_DEPOSIT_T.generatetype
  is 'GenerateType_標識';
comment on column FIT_AUDIT_ST_DEPOSIT_T.year
  is 'Year_年份';
comment on column FIT_AUDIT_ST_DEPOSIT_T.period
  is 'Period_月份';
comment on column FIT_AUDIT_ST_DEPOSIT_T.entity
  is 'Company code_法人編碼';
comment on column FIT_AUDIT_ST_DEPOSIT_T.entityname
  is 'Company name_法人名稱';
comment on column FIT_AUDIT_ST_DEPOSIT_T.accountcode
  is 'Account code_科目明細';
comment on column FIT_AUDIT_ST_DEPOSIT_T.accountname
  is 'Account name_科目名稱';
comment on column FIT_AUDIT_ST_DEPOSIT_T.nature
  is 'Nature(deposit over 3 months/guarantee deposit)_分類(定期存款-3個月以上/保函保證金)';
comment on column FIT_AUDIT_ST_DEPOSIT_T.depositno
  is 'Deposit number_定存單號';
comment on column FIT_AUDIT_ST_DEPOSIT_T.bank
  is 'Bank_銀行名稱';
comment on column FIT_AUDIT_ST_DEPOSIT_T.bankaccount
  is 'Bank account_銀行帳號';
comment on column FIT_AUDIT_ST_DEPOSIT_T.startdate
  is 'Start date _起始日';
comment on column FIT_AUDIT_ST_DEPOSIT_T.enddate
  is 'End date_到期日';
comment on column FIT_AUDIT_ST_DEPOSIT_T.rate
  is 'Rate_年利率';
comment on column FIT_AUDIT_ST_DEPOSIT_T.originalcurrency
  is 'Original currency_原幣幣別';
comment on column FIT_AUDIT_ST_DEPOSIT_T.balance_originalcurrency
  is 'Balance in original currency_原幣金額';
comment on column FIT_AUDIT_ST_DEPOSIT_T.exchangerate
  is 'Exchange rate_匯率';
comment on column FIT_AUDIT_ST_DEPOSIT_T.presentationcurrency
  is 'Company''s presentational currency_記帳幣別';
comment on column FIT_AUDIT_ST_DEPOSIT_T.balance_presentationcurrency
  is 'Balance in company''s presentational currency_月底餘額';
comment on column FIT_AUDIT_ST_DEPOSIT_T.bank_prc
  is 'Bank in PRC_內地銀行';
alter table FIT_AUDIT_ST_DEPOSIT_T
  add constraint FIT_AUDIT_ST_DEPOSIT_T_PK primary key (ENTITY, ACCOUNTCODE, DEPOSITNO, YEAR, PERIOD, ORIGINALCURRENCY);

prompt
prompt Creating table FIT_AUDIT_TABLE
prompt ==============================
prompt
create table FIT_AUDIT_TABLE
(
  table_name VARCHAR2(200) not null,
  comments   VARCHAR2(200) not null
)
;
alter table FIT_AUDIT_TABLE
  add constraint PK_FIT_AUDIT_TABLE primary key (TABLE_NAME);

prompt
prompt Creating table FIT_AUDIT_TABLE_COLUMNS
prompt ======================================
prompt
create table FIT_AUDIT_TABLE_COLUMNS
(
  id          VARCHAR2(50) not null,
  column_name VARCHAR2(200) not null,
  data_type   VARCHAR2(50) not null,
  comments    VARCHAR2(200) not null,
  serial      INTEGER not null,
  locked      CHAR(1) default 'F' not null,
  table_name  VARCHAR2(50) not null,
  nullable    CHAR(1) default 'T'
)
;
comment on column FIT_AUDIT_TABLE_COLUMNS.locked
  is '是否锁定 T-锁定，F-不锁定';
comment on column FIT_AUDIT_TABLE_COLUMNS.nullable
  is '是否可为空 T-可为空，F-不能为空';
alter table FIT_AUDIT_TABLE_COLUMNS
  add constraint PK_FIT_AUDIT_TABLE_COLUMNS primary key (ID);

prompt
prompt Creating table FIT_AUDIT_TABLE_KEY
prompt ==================================
prompt
create table FIT_AUDIT_TABLE_KEY
(
  id          VARCHAR2(50) not null,
  column_name VARCHAR2(200) not null,
  data_type   VARCHAR2(50) not null,
  comments    VARCHAR2(200) not null,
  serial      INTEGER not null,
  table_name  VARCHAR2(50) not null
)
;
alter table FIT_AUDIT_TABLE_KEY
  add constraint PK_FIT_AUDIT_TABLE_KEY primary key (ID);

prompt
prompt Creating table FIT_AUDIT_TAX_REC
prompt ================================
prompt
create table FIT_AUDIT_TAX_REC
(
  generatetype       VARCHAR2(2) not null,
  year               VARCHAR2(4) not null,
  period             VARCHAR2(2) not null,
  entity             VARCHAR2(50) not null,
  entityname         VARCHAR2(240) not null,
  income             NUMBER not null,
  expenses           NUMBER not null,
  preunrecognisedtax NUMBER not null,
  overprovision      NUMBER not null,
  underprovision     NUMBER not null,
  unrecognisedtax    NUMBER not null,
  others             NUMBER not null,
  taxlosses          NUMBER not null
)
;
comment on table FIT_AUDIT_TAX_REC
  is 'Tax rec disclosure_Tax rec disclosure';
comment on column FIT_AUDIT_TAX_REC.generatetype
  is 'GenerateType_標識';
comment on column FIT_AUDIT_TAX_REC.year
  is 'Year_年份';
comment on column FIT_AUDIT_TAX_REC.period
  is 'Period_月份';
comment on column FIT_AUDIT_TAX_REC.entity
  is 'Company code_法人編碼';
comment on column FIT_AUDIT_TAX_REC.entityname
  is 'Company name_法人名稱';
comment on column FIT_AUDIT_TAX_REC.income
  is 'Income not subject to tax_Income not subject to tax';
comment on column FIT_AUDIT_TAX_REC.expenses
  is 'Expenses not deductible for tax purpose_Expenses not deductible for tax purpose';
comment on column FIT_AUDIT_TAX_REC.preunrecognisedtax
  is 'Utilisation of previously unrecognised tax losses_Utilisation of previously unrecognised tax losses';
comment on column FIT_AUDIT_TAX_REC.overprovision
  is 'Over provision for prior year_Over provision for prior year';
comment on column FIT_AUDIT_TAX_REC.underprovision
  is 'Under provision for prior year_Under provision for prior year';
comment on column FIT_AUDIT_TAX_REC.unrecognisedtax
  is 'Unrecognised tax losses_Unrecognised tax losses';
comment on column FIT_AUDIT_TAX_REC.others
  is 'Others_Others';
comment on column FIT_AUDIT_TAX_REC.taxlosses
  is 'Tax losses for which deferred income tax asset was recognised_Tax losses for which deferred income tax asset was recognised';
alter table FIT_AUDIT_TAX_REC
  add constraint FIT_AUDIT_TAX_REC_PK primary key (YEAR, PERIOD, ENTITY);

prompt
prompt Creating table FIT_AUDIT_TAX_REC_T
prompt ==================================
prompt
create table FIT_AUDIT_TAX_REC_T
(
  generatetype       VARCHAR2(2) not null,
  year               VARCHAR2(4) not null,
  period             VARCHAR2(2) not null,
  entity             VARCHAR2(50) not null,
  entityname         VARCHAR2(240) not null,
  income             NUMBER not null,
  expenses           NUMBER not null,
  preunrecognisedtax NUMBER not null,
  overprovision      NUMBER not null,
  underprovision     NUMBER not null,
  unrecognisedtax    NUMBER not null,
  others             NUMBER not null,
  taxlosses          NUMBER not null
)
;
comment on table FIT_AUDIT_TAX_REC_T
  is 'Tax rec disclosure_Tax rec disclosure';
comment on column FIT_AUDIT_TAX_REC_T.generatetype
  is 'GenerateType_標識';
comment on column FIT_AUDIT_TAX_REC_T.year
  is 'Year_年份';
comment on column FIT_AUDIT_TAX_REC_T.period
  is 'Period_月份';
comment on column FIT_AUDIT_TAX_REC_T.entity
  is 'Company code_法人編碼';
comment on column FIT_AUDIT_TAX_REC_T.entityname
  is 'Company name_法人名稱';
comment on column FIT_AUDIT_TAX_REC_T.income
  is 'Income not subject to tax_Income not subject to tax';
comment on column FIT_AUDIT_TAX_REC_T.expenses
  is 'Expenses not deductible for tax purpose_Expenses not deductible for tax purpose';
comment on column FIT_AUDIT_TAX_REC_T.preunrecognisedtax
  is 'Utilisation of previously unrecognised tax losses_Utilisation of previously unrecognised tax losses';
comment on column FIT_AUDIT_TAX_REC_T.overprovision
  is 'Over provision for prior year_Over provision for prior year';
comment on column FIT_AUDIT_TAX_REC_T.underprovision
  is 'Under provision for prior year_Under provision for prior year';
comment on column FIT_AUDIT_TAX_REC_T.unrecognisedtax
  is 'Unrecognised tax losses_Unrecognised tax losses';
comment on column FIT_AUDIT_TAX_REC_T.others
  is 'Others_Others';
comment on column FIT_AUDIT_TAX_REC_T.taxlosses
  is 'Tax losses for which deferred income tax asset was recognised_Tax losses for which deferred income tax asset was recognised';
alter table FIT_AUDIT_TAX_REC_T
  add constraint FIT_AUDIT_TAX_REC_T_PK primary key (YEAR, PERIOD, ENTITY);

prompt
prompt Creating table FIT_AUDIT_TIME_DEPOSIT
prompt =====================================
prompt
create table FIT_AUDIT_TIME_DEPOSIT
(
  generatetype                 VARCHAR2(2) not null,
  year                         VARCHAR2(4) not null,
  period                       VARCHAR2(2) not null,
  entity                       VARCHAR2(20) not null,
  entityname                   VARCHAR2(240) not null,
  accountcode                  VARCHAR2(20) not null,
  accountname                  VARCHAR2(50) not null,
  depositno                    VARCHAR2(50) not null,
  bank                         VARCHAR2(80) not null,
  bankaccount                  VARCHAR2(100) not null,
  startdate                    DATE not null,
  enddate                      DATE not null,
  rate                         NUMBER not null,
  originalcurrency             VARCHAR2(3) not null,
  balance_originalcurrency     NUMBER not null,
  exchangerate                 NUMBER not null,
  presentationcurrency         VARCHAR2(3) not null,
  balance_presentationcurrency NUMBER not null,
  bank_prc                     VARCHAR2(50) not null
)
;
comment on table FIT_AUDIT_TIME_DEPOSIT
  is 'Time deposit within 3 months_三個月以內定存明細表';
comment on column FIT_AUDIT_TIME_DEPOSIT.generatetype
  is 'GenerateType_標識';
comment on column FIT_AUDIT_TIME_DEPOSIT.year
  is 'Year_年份';
comment on column FIT_AUDIT_TIME_DEPOSIT.period
  is 'Period_月份';
comment on column FIT_AUDIT_TIME_DEPOSIT.entity
  is 'Company code_法人編碼';
comment on column FIT_AUDIT_TIME_DEPOSIT.entityname
  is 'Company name_法人名稱';
comment on column FIT_AUDIT_TIME_DEPOSIT.accountcode
  is 'Account code_科目明細';
comment on column FIT_AUDIT_TIME_DEPOSIT.accountname
  is 'Account name_科目名稱';
comment on column FIT_AUDIT_TIME_DEPOSIT.depositno
  is 'Deposit number_定存單號';
comment on column FIT_AUDIT_TIME_DEPOSIT.bank
  is 'Bank_銀行名稱';
comment on column FIT_AUDIT_TIME_DEPOSIT.bankaccount
  is 'Bank account_銀行賬號';
comment on column FIT_AUDIT_TIME_DEPOSIT.startdate
  is 'Start date _起始日';
comment on column FIT_AUDIT_TIME_DEPOSIT.enddate
  is 'End date_到期日';
comment on column FIT_AUDIT_TIME_DEPOSIT.rate
  is 'Rate_年利率';
comment on column FIT_AUDIT_TIME_DEPOSIT.originalcurrency
  is 'Original currency_原幣幣別';
comment on column FIT_AUDIT_TIME_DEPOSIT.balance_originalcurrency
  is 'Balance in original currency _原幣金額';
comment on column FIT_AUDIT_TIME_DEPOSIT.exchangerate
  is 'Exchange rate_匯率';
comment on column FIT_AUDIT_TIME_DEPOSIT.presentationcurrency
  is 'Company''s presentational currency_記帳幣別';
comment on column FIT_AUDIT_TIME_DEPOSIT.balance_presentationcurrency
  is 'Balance in company''s presentational currency_月底餘額';
comment on column FIT_AUDIT_TIME_DEPOSIT.bank_prc
  is 'Bank in PRC_內地銀行';
alter table FIT_AUDIT_TIME_DEPOSIT
  add constraint FIT_AUDIT_TIME_DEPOSIT_PK primary key (YEAR, PERIOD, ENTITY, ACCOUNTCODE, DEPOSITNO, ORIGINALCURRENCY);

prompt
prompt Creating table FIT_AUDIT_TIME_DEPOSIT_T
prompt =======================================
prompt
create table FIT_AUDIT_TIME_DEPOSIT_T
(
  generatetype                 VARCHAR2(2) not null,
  year                         VARCHAR2(4) not null,
  period                       VARCHAR2(2) not null,
  entity                       VARCHAR2(20) not null,
  entityname                   VARCHAR2(240) not null,
  accountcode                  VARCHAR2(20) not null,
  accountname                  VARCHAR2(50) not null,
  depositno                    VARCHAR2(50) not null,
  bank                         VARCHAR2(80) not null,
  bankaccount                  VARCHAR2(100) not null,
  startdate                    DATE not null,
  enddate                      DATE not null,
  rate                         NUMBER not null,
  originalcurrency             VARCHAR2(3) not null,
  balance_originalcurrency     NUMBER not null,
  exchangerate                 NUMBER not null,
  presentationcurrency         VARCHAR2(3) not null,
  balance_presentationcurrency NUMBER not null,
  bank_prc                     VARCHAR2(50) not null
)
;
comment on table FIT_AUDIT_TIME_DEPOSIT_T
  is 'Time deposit within 3 months_三個月以內定存明細表';
comment on column FIT_AUDIT_TIME_DEPOSIT_T.generatetype
  is 'GenerateType_標識';
comment on column FIT_AUDIT_TIME_DEPOSIT_T.year
  is 'Year_年份';
comment on column FIT_AUDIT_TIME_DEPOSIT_T.period
  is 'Period_月份';
comment on column FIT_AUDIT_TIME_DEPOSIT_T.entity
  is 'Company code_法人編碼';
comment on column FIT_AUDIT_TIME_DEPOSIT_T.entityname
  is 'Company name_法人名稱';
comment on column FIT_AUDIT_TIME_DEPOSIT_T.accountcode
  is 'Account code_科目明細';
comment on column FIT_AUDIT_TIME_DEPOSIT_T.accountname
  is 'Account name_科目名稱';
comment on column FIT_AUDIT_TIME_DEPOSIT_T.depositno
  is 'Deposit number_定存單號';
comment on column FIT_AUDIT_TIME_DEPOSIT_T.bank
  is 'Bank_銀行名稱';
comment on column FIT_AUDIT_TIME_DEPOSIT_T.bankaccount
  is 'Bank account_銀行賬號';
comment on column FIT_AUDIT_TIME_DEPOSIT_T.startdate
  is 'Start date _起始日';
comment on column FIT_AUDIT_TIME_DEPOSIT_T.enddate
  is 'End date_到期日';
comment on column FIT_AUDIT_TIME_DEPOSIT_T.rate
  is 'Rate_年利率';
comment on column FIT_AUDIT_TIME_DEPOSIT_T.originalcurrency
  is 'Original currency_原幣幣別';
comment on column FIT_AUDIT_TIME_DEPOSIT_T.balance_originalcurrency
  is 'Balance in original currency _原幣金額';
comment on column FIT_AUDIT_TIME_DEPOSIT_T.exchangerate
  is 'Exchange rate_匯率';
comment on column FIT_AUDIT_TIME_DEPOSIT_T.presentationcurrency
  is 'Company''s presentational currency_記帳幣別';
comment on column FIT_AUDIT_TIME_DEPOSIT_T.balance_presentationcurrency
  is 'Balance in company''s presentational currency_月底餘額';
comment on column FIT_AUDIT_TIME_DEPOSIT_T.bank_prc
  is 'Bank in PRC_內地銀行';
alter table FIT_AUDIT_TIME_DEPOSIT_T
  add constraint FIT_AUDIT_TIME_DEPOSIT_T_PK primary key (YEAR, PERIOD, ENTITY, ACCOUNTCODE, DEPOSITNO, ORIGINALCURRENCY);

prompt
prompt Creating table FIT_AUDIT_UNREMIT_EARNINGS
prompt =========================================
prompt
create table FIT_AUDIT_UNREMIT_EARNINGS
(
  generatetype         VARCHAR2(2) not null,
  year                 VARCHAR2(4) not null,
  period               VARCHAR2(2) not null,
  entity               VARCHAR2(50) not null,
  entityname           VARCHAR2(240) not null,
  retainedearnings     NUMBER not null,
  sharecaptial         NUMBER not null,
  statutoryreserve     NUMBER not null,
  undistributedprofits NUMBER not null,
  taxrate              NUMBER not null,
  dtl                  NUMBER not null
)
;
comment on table FIT_AUDIT_UNREMIT_EARNINGS
  is 'Unremitted earnings disclosure_Unremitted earnings disclosure';
comment on column FIT_AUDIT_UNREMIT_EARNINGS.generatetype
  is 'GenerateType_標識';
comment on column FIT_AUDIT_UNREMIT_EARNINGS.year
  is 'Year_年份';
comment on column FIT_AUDIT_UNREMIT_EARNINGS.period
  is 'Period_月份';
comment on column FIT_AUDIT_UNREMIT_EARNINGS.entity
  is 'Company code_法人編碼';
comment on column FIT_AUDIT_UNREMIT_EARNINGS.entityname
  is 'Company name_法人名稱';
comment on column FIT_AUDIT_UNREMIT_EARNINGS.retainedearnings
  is 'Retained earnings (per local book)_Retained earnings (per local book)
';
comment on column FIT_AUDIT_UNREMIT_EARNINGS.sharecaptial
  is 'Share captial_Share captial
';
comment on column FIT_AUDIT_UNREMIT_EARNINGS.statutoryreserve
  is 'Statutory reserve_Statutory reserve
';
comment on column FIT_AUDIT_UNREMIT_EARNINGS.undistributedprofits
  is 'Undistributed profits_Undistributed profits
';
comment on column FIT_AUDIT_UNREMIT_EARNINGS.taxrate
  is 'Witholding tax rate_Witholding tax rate
';
comment on column FIT_AUDIT_UNREMIT_EARNINGS.dtl
  is 'Deferred income tax liabilities_Deferred income tax liabilities
';
alter table FIT_AUDIT_UNREMIT_EARNINGS
  add constraint FIT_AUDIT_UE_PK primary key (YEAR, PERIOD, ENTITY);

prompt
prompt Creating table FIT_AUDIT_UNREMIT_EARNINGS_T
prompt ===========================================
prompt
create table FIT_AUDIT_UNREMIT_EARNINGS_T
(
  generatetype         VARCHAR2(2) not null,
  year                 VARCHAR2(4) not null,
  period               VARCHAR2(2) not null,
  entity               VARCHAR2(50) not null,
  entityname           VARCHAR2(240) not null,
  retainedearnings     NUMBER not null,
  sharecaptial         NUMBER not null,
  statutoryreserve     NUMBER not null,
  undistributedprofits NUMBER not null,
  taxrate              NUMBER not null,
  dtl                  NUMBER not null
)
;
comment on table FIT_AUDIT_UNREMIT_EARNINGS_T
  is 'Unremitted earnings disclosure_Unremitted earnings disclosure';
comment on column FIT_AUDIT_UNREMIT_EARNINGS_T.generatetype
  is 'GenerateType_標識';
comment on column FIT_AUDIT_UNREMIT_EARNINGS_T.year
  is 'Year_年份';
comment on column FIT_AUDIT_UNREMIT_EARNINGS_T.period
  is 'Period_月份';
comment on column FIT_AUDIT_UNREMIT_EARNINGS_T.entity
  is 'Company code_法人編碼';
comment on column FIT_AUDIT_UNREMIT_EARNINGS_T.entityname
  is 'Company name_法人名稱';
comment on column FIT_AUDIT_UNREMIT_EARNINGS_T.retainedearnings
  is 'Retained earnings (per local book)_Retained earnings (per local book)
';
comment on column FIT_AUDIT_UNREMIT_EARNINGS_T.sharecaptial
  is 'Share captial_Share captial
';
comment on column FIT_AUDIT_UNREMIT_EARNINGS_T.statutoryreserve
  is 'Statutory reserve_Statutory reserve
';
comment on column FIT_AUDIT_UNREMIT_EARNINGS_T.undistributedprofits
  is 'Undistributed profits_Undistributed profits
';
comment on column FIT_AUDIT_UNREMIT_EARNINGS_T.taxrate
  is 'Witholding tax rate_Witholding tax rate
';
comment on column FIT_AUDIT_UNREMIT_EARNINGS_T.dtl
  is 'Deferred income tax liabilities_Deferred income tax liabilities
';
alter table FIT_AUDIT_UNREMIT_EARNINGS_T
  add constraint FIT_AUDIT_UE_T_PK primary key (YEAR, PERIOD, ENTITY);

prompt
prompt Creating table FIT_CCT
prompt ======================
prompt
create table FIT_CCT
(
  id                   VARCHAR2(50) not null,
  year                 VARCHAR2(100),
  organization         VARCHAR2(100),
  item                 VARCHAR2(100),
  cap                  VARCHAR2(100),
  rolling_forecast     VARCHAR2(100),
  vscap                VARCHAR2(100),
  percent              VARCHAR2(100),
  year_rate            VARCHAR2(100),
  light                VARCHAR2(100),
  warning_instructions VARCHAR2(200),
  currency             VARCHAR2(10) default 'USD',
  system               VARCHAR2(10) default 'FIT'
)
;
comment on table FIT_CCT
  is 'CCT關聯交易';
comment on column FIT_CCT.year
  is '年';
comment on column FIT_CCT.organization
  is '組織';
comment on column FIT_CCT.item
  is '項目';
comment on column FIT_CCT.cap
  is 'CAP';
comment on column FIT_CCT.rolling_forecast
  is '滾動預測';
comment on column FIT_CCT.vscap
  is 'vs CAP';
comment on column FIT_CCT.percent
  is '%';
comment on column FIT_CCT.year_rate
  is '年度CAP佔比';
comment on column FIT_CCT.light
  is '燈號';
comment on column FIT_CCT.warning_instructions
  is '預警說明';
comment on column FIT_CCT.system
  is '體系';
alter table FIT_CCT
  add constraint PK_FIT_CCT_ID primary key (ID);

prompt
prompt Creating table FIT_CHANNEL
prompt ==========================
prompt
create table FIT_CHANNEL
(
  id                  VARCHAR2(50) not null,
  corporation_code    VARCHAR2(50),
  customer_code       VARCHAR2(100),
  customer_short_name VARCHAR2(100),
  customer_name       VARCHAR2(100),
  area                VARCHAR2(100),
  country             VARCHAR2(100),
  groups              VARCHAR2(100),
  classification1     VARCHAR2(100),
  classification2     VARCHAR2(100),
  receipt_condition   VARCHAR2(100),
  price_condition     VARCHAR2(100),
  trade_customer      VARCHAR2(100),
  document            VARCHAR2(200),
  channel             VARCHAR2(100)
)
;
comment on table FIT_CHANNEL
  is '渠道映射';
comment on column FIT_CHANNEL.corporation_code
  is '法人';
comment on column FIT_CHANNEL.customer_code
  is '客戶代碼';
comment on column FIT_CHANNEL.customer_short_name
  is '客戶簡稱';
comment on column FIT_CHANNEL.customer_name
  is '客戶全名';
comment on column FIT_CHANNEL.area
  is '地區別';
comment on column FIT_CHANNEL.country
  is '國別';
comment on column FIT_CHANNEL.groups
  is '所屬集團';
comment on column FIT_CHANNEL.classification1
  is '客戶分類-1';
comment on column FIT_CHANNEL.classification2
  is '客戶分類-2';
comment on column FIT_CHANNEL.receipt_condition
  is '收款條件';
comment on column FIT_CHANNEL.price_condition
  is '價格條件';
comment on column FIT_CHANNEL.trade_customer
  is '交易客戶歸類';
comment on column FIT_CHANNEL.document
  is '對照檔（法人+客戶代碼）';
comment on column FIT_CHANNEL.channel
  is '營收渠道';
alter table FIT_CHANNEL
  add constraint PK_FIT_CHANNEL_ID primary key (ID);

prompt
prompt Creating table FIT_CHECK_EXIST
prompt ==============================
prompt
create table FIT_CHECK_EXIST
(
  value VARCHAR2(200)
)
;
comment on table FIT_CHECK_EXIST
  is '临时表,用来检查值是否存在';

prompt
prompt Creating table FIT_COA_MAPPING
prompt ==============================
prompt
create table FIT_COA_MAPPING
(
  id                    VARCHAR2(50) not null,
  source_corporate_code VARCHAR2(200),
  source_account_code   VARCHAR2(200),
  source_account_name   VARCHAR2(200),
  target_account_code   VARCHAR2(200),
  target_account_name   VARCHAR2(200),
  attribute1            VARCHAR2(200),
  attribute2            VARCHAR2(200),
  attribute3            VARCHAR2(200),
  attribute4            VARCHAR2(200),
  attribute5            VARCHAR2(200)
)
;
comment on column FIT_COA_MAPPING.source_corporate_code
  is 'ERP法人編碼';
comment on column FIT_COA_MAPPING.source_account_code
  is 'ERP科目編碼';
comment on column FIT_COA_MAPPING.source_account_name
  is 'ERP科目名称';
comment on column FIT_COA_MAPPING.target_account_code
  is 'HFM科目編碼';
comment on column FIT_COA_MAPPING.target_account_name
  is 'HFM科目名称';
comment on column FIT_COA_MAPPING.attribute1
  is '符號轉換';
alter table FIT_COA_MAPPING
  add constraint PK_COA_MAPPING_ID primary key (ID);

prompt
prompt Creating table FIT_COA_MAPPING_TEMP
prompt ===================================
prompt
create table FIT_COA_MAPPING_TEMP
(
  id                    VARCHAR2(50) not null,
  source_corporate_code VARCHAR2(200),
  source_account_code   VARCHAR2(200),
  source_account_name   VARCHAR2(200),
  target_account_code   VARCHAR2(200),
  target_account_name   VARCHAR2(200),
  attribute1            VARCHAR2(200),
  attribute2            VARCHAR2(200),
  attribute3            VARCHAR2(200),
  attribute4            VARCHAR2(200),
  attribute5            VARCHAR2(200)
)
;
comment on column FIT_COA_MAPPING_TEMP.source_corporate_code
  is 'ERP法人編碼';
comment on column FIT_COA_MAPPING_TEMP.source_account_code
  is 'ERP科目編碼';
comment on column FIT_COA_MAPPING_TEMP.source_account_name
  is 'ERP科目名?';
comment on column FIT_COA_MAPPING_TEMP.target_account_code
  is 'HFM科目編碼';
comment on column FIT_COA_MAPPING_TEMP.target_account_name
  is 'HFM科目名?';
comment on column FIT_COA_MAPPING_TEMP.attribute1
  is '符號轉換';
alter table FIT_COA_MAPPING_TEMP
  add constraint PK_COA_MAPPING_ID1 primary key (ID);

prompt
prompt Creating table FIT_CORPORAT_MAP
prompt ===============================
prompt
create table FIT_CORPORAT_MAP
(
  erp_corporat_code VARCHAR2(200),
  corporat_name     VARCHAR2(200),
  type              VARCHAR2(200),
  hfm_corporat_code VARCHAR2(200),
  attribute1        VARCHAR2(200),
  attribute2        VARCHAR2(200),
  attribute3        VARCHAR2(200),
  attribute4        VARCHAR2(200),
  attribute5        VARCHAR2(200)
)
;
comment on table FIT_CORPORAT_MAP
  is '法人編碼映射';

prompt
prompt Creating table FIT_CORPORAT_MAP_TEMP
prompt ====================================
prompt
create table FIT_CORPORAT_MAP_TEMP
(
  erp_corporat_code VARCHAR2(200),
  corporat_name     VARCHAR2(200),
  type              VARCHAR2(200),
  hfm_corporat_code VARCHAR2(200),
  attribute1        VARCHAR2(200),
  attribute2        VARCHAR2(200),
  attribute3        VARCHAR2(200),
  attribute4        VARCHAR2(200),
  attribute5        VARCHAR2(200)
)
;
comment on table FIT_CORPORAT_MAP_TEMP
  is '法人編碼映射';

prompt
prompt Creating table FIT_CUSTOMER_MAPPING
prompt ===================================
prompt
create table FIT_CUSTOMER_MAPPING
(
  id             VARCHAR2(50) not null,
  customer       VARCHAR2(200),
  customer_group VARCHAR2(200)
)
;
comment on table FIT_CUSTOMER_MAPPING
  is '客戶對應表';
comment on column FIT_CUSTOMER_MAPPING.customer
  is 'RFQ&SAMPLE&CCMS_客戶';
comment on column FIT_CUSTOMER_MAPPING.customer_group
  is '對應經管客戶集團';
alter table FIT_CUSTOMER_MAPPING
  add constraint PK_FIT_CUSTOMER_MAPPING_ID primary key (ID);

prompt
prompt Creating table FIT_CUSTOMER_NAME_SPECIFY
prompt ========================================
prompt
create table FIT_CUSTOMER_NAME_SPECIFY
(
  id                       VARCHAR2(50) not null,
  corporation_code         VARCHAR2(50),
  customer_code            VARCHAR2(100),
  customer_short_name      VARCHAR2(100),
  specification_short_name VARCHAR2(100),
  specification_full_name  VARCHAR2(100),
  specification_group_name VARCHAR2(100)
)
;
comment on table FIT_CUSTOMER_NAME_SPECIFY
  is '客戶名稱規範映射';
comment on column FIT_CUSTOMER_NAME_SPECIFY.corporation_code
  is '法人';
comment on column FIT_CUSTOMER_NAME_SPECIFY.customer_code
  is '客戶代碼';
comment on column FIT_CUSTOMER_NAME_SPECIFY.customer_short_name
  is '客戶簡稱';
comment on column FIT_CUSTOMER_NAME_SPECIFY.specification_short_name
  is '客戶簡稱（規范）';
comment on column FIT_CUSTOMER_NAME_SPECIFY.specification_full_name
  is '客戶全稱（規范）';
comment on column FIT_CUSTOMER_NAME_SPECIFY.specification_group_name
  is '客戶集團（規范）';
alter table FIT_CUSTOMER_NAME_SPECIFY
  add constraint PK_FIT_CUS_NAME_SPECIFY_ID primary key (ID);

prompt
prompt Creating table FIT_DIMENSION
prompt ============================
prompt
create table FIT_DIMENSION
(
  id        VARCHAR2(50) not null,
  dimension VARCHAR2(200) not null,
  parent    VARCHAR2(200),
  alias     VARCHAR2(200),
  type      VARCHAR2(50) not null
)
;
comment on table FIT_DIMENSION
  is '维度表';
comment on column FIT_DIMENSION.dimension
  is '维度值';
comment on column FIT_DIMENSION.parent
  is '父类成员';
comment on column FIT_DIMENSION.alias
  is '别名表';
comment on column FIT_DIMENSION.type
  is '维度类型';
alter table FIT_DIMENSION
  add constraint PK_FIT_DIMENSION_ID primary key (ID);

prompt
prompt Creating table FIT_FIXED_ASSETS
prompt ===============================
prompt
create table FIT_FIXED_ASSETS
(
  id               VARCHAR2(50) not null,
  corporation_code VARCHAR2(50) not null,
  year             VARCHAR2(4) not null,
  period           VARCHAR2(2) not null,
  item_code        VARCHAR2(100),
  item_desc        VARCHAR2(100),
  begin_balance    VARCHAR2(100),
  increase_amount  VARCHAR2(100),
  dispose_amount   VARCHAR2(100),
  transfer_amount  VARCHAR2(100),
  end_amount       VARCHAR2(100)
)
;
comment on table FIT_FIXED_ASSETS
  is '固资';
comment on column FIT_FIXED_ASSETS.corporation_code
  is '法人编码';
comment on column FIT_FIXED_ASSETS.year
  is '年';
comment on column FIT_FIXED_ASSETS.period
  is '期間';
comment on column FIT_FIXED_ASSETS.item_code
  is '科目編號';
comment on column FIT_FIXED_ASSETS.item_desc
  is '科目描述';
comment on column FIT_FIXED_ASSETS.begin_balance
  is '期初餘額';
comment on column FIT_FIXED_ASSETS.increase_amount
  is '增添金額';
comment on column FIT_FIXED_ASSETS.dispose_amount
  is '處分金額';
comment on column FIT_FIXED_ASSETS.transfer_amount
  is '移轉金額';
comment on column FIT_FIXED_ASSETS.end_amount
  is '期末余額';
alter table FIT_FIXED_ASSETS
  add constraint PK_FIT_FIXED_ASSETS_ID primary key (ID);

prompt
prompt Creating table FIT_FORECAST_DETAIL_REVENUE
prompt ==========================================
prompt
create table FIT_FORECAST_DETAIL_REVENUE
(
  id                           VARCHAR2(50) not null,
  entity                       VARCHAR2(50) not null,
  industry                     VARCHAR2(200 CHAR) not null,
  product                      VARCHAR2(200) not null,
  combine                      VARCHAR2(200 CHAR) not null,
  customer                     VARCHAR2(200 CHAR) not null,
  type                         VARCHAR2(200) not null,
  currency                     VARCHAR2(50) not null,
  version                      VARCHAR2(50) not null,
  scenarios                    VARCHAR2(50) not null,
  year                         CHAR(4) not null,
  industry_demand_trend        VARCHAR2(100),
  industry_demand_trend_served VARCHAR2(100),
  component_usage              VARCHAR2(100),
  average_sales_price          VARCHAR2(100),
  total_available_market       VARCHAR2(100),
  served_available_market      VARCHAR2(100),
  allocation                   VARCHAR2(100),
  revenue                      VARCHAR2(100),
  quantity                     VARCHAR2(100),
  quantity_month1              VARCHAR2(100),
  quantity_month2              VARCHAR2(100),
  quantity_month3              VARCHAR2(100),
  quantity_month4              VARCHAR2(100),
  quantity_month5              VARCHAR2(100),
  quantity_month6              VARCHAR2(100),
  quantity_month7              VARCHAR2(100),
  quantity_month8              VARCHAR2(100),
  quantity_month9              VARCHAR2(100),
  quantity_month10             VARCHAR2(100),
  quantity_month11             VARCHAR2(100),
  quantity_month12             VARCHAR2(100),
  revenue_month1               VARCHAR2(100),
  revenue_month2               VARCHAR2(100),
  revenue_month3               VARCHAR2(100),
  revenue_month4               VARCHAR2(100),
  revenue_month5               VARCHAR2(100),
  revenue_month6               VARCHAR2(100),
  revenue_month7               VARCHAR2(100),
  revenue_month8               VARCHAR2(100),
  revenue_month9               VARCHAR2(100),
  revenue_month10              VARCHAR2(100),
  revenue_month11              VARCHAR2(100),
  revenue_month12              VARCHAR2(100),
  price_month1                 VARCHAR2(100),
  price_month2                 VARCHAR2(100),
  price_month3                 VARCHAR2(100),
  price_month4                 VARCHAR2(100),
  price_month5                 VARCHAR2(100),
  price_month6                 VARCHAR2(100),
  price_month7                 VARCHAR2(100),
  price_month8                 VARCHAR2(100),
  price_month9                 VARCHAR2(100),
  price_month10                VARCHAR2(100),
  price_month11                VARCHAR2(100),
  price_month12                VARCHAR2(100)
)
;
comment on table FIT_FORECAST_DETAIL_REVENUE
  is '預算/預測營收明細';
comment on column FIT_FORECAST_DETAIL_REVENUE.entity
  is '法人';
comment on column FIT_FORECAST_DETAIL_REVENUE.industry
  is '次產業';
comment on column FIT_FORECAST_DETAIL_REVENUE.product
  is '產品料號';
comment on column FIT_FORECAST_DETAIL_REVENUE.combine
  is '最終客戶';
comment on column FIT_FORECAST_DETAIL_REVENUE.customer
  is '賬款客戶';
comment on column FIT_FORECAST_DETAIL_REVENUE.type
  is '交易類型';
comment on column FIT_FORECAST_DETAIL_REVENUE.currency
  is '交易貨幣';
comment on column FIT_FORECAST_DETAIL_REVENUE.version
  is '版本';
comment on column FIT_FORECAST_DETAIL_REVENUE.scenarios
  is '場景';
comment on column FIT_FORECAST_DETAIL_REVENUE.year
  is '年';
comment on column FIT_FORECAST_DETAIL_REVENUE.industry_demand_trend
  is 'Industry Demand Trend';
comment on column FIT_FORECAST_DETAIL_REVENUE.industry_demand_trend_served
  is 'Industry Demand Trend-Served';
comment on column FIT_FORECAST_DETAIL_REVENUE.component_usage
  is 'Component Usage/ 用量';
comment on column FIT_FORECAST_DETAIL_REVENUE.average_sales_price
  is 'Average Sales Price / 平均單價';
comment on column FIT_FORECAST_DETAIL_REVENUE.total_available_market
  is 'Total Available Market / TAM';
comment on column FIT_FORECAST_DETAIL_REVENUE.served_available_market
  is 'Served Available Market / SAM';
comment on column FIT_FORECAST_DETAIL_REVENUE.allocation
  is 'Allocation';
comment on column FIT_FORECAST_DETAIL_REVENUE.revenue
  is '營收';
comment on column FIT_FORECAST_DETAIL_REVENUE.quantity
  is '銷售數量';
comment on column FIT_FORECAST_DETAIL_REVENUE.quantity_month1
  is '銷售數量1月';
comment on column FIT_FORECAST_DETAIL_REVENUE.quantity_month2
  is '銷售數量2月';
comment on column FIT_FORECAST_DETAIL_REVENUE.quantity_month3
  is '銷售數量3月';
comment on column FIT_FORECAST_DETAIL_REVENUE.quantity_month4
  is '銷售數量4月';
comment on column FIT_FORECAST_DETAIL_REVENUE.quantity_month5
  is '銷售數量5月';
comment on column FIT_FORECAST_DETAIL_REVENUE.quantity_month6
  is '銷售數量6月';
comment on column FIT_FORECAST_DETAIL_REVENUE.quantity_month7
  is '銷售數量7月';
comment on column FIT_FORECAST_DETAIL_REVENUE.quantity_month8
  is '銷售數量8月';
comment on column FIT_FORECAST_DETAIL_REVENUE.quantity_month9
  is '銷售數量9月';
comment on column FIT_FORECAST_DETAIL_REVENUE.quantity_month10
  is '銷售數量10月';
comment on column FIT_FORECAST_DETAIL_REVENUE.quantity_month11
  is '銷售數量11月';
comment on column FIT_FORECAST_DETAIL_REVENUE.quantity_month12
  is '銷售數量12月';
comment on column FIT_FORECAST_DETAIL_REVENUE.revenue_month1
  is '營收1月';
comment on column FIT_FORECAST_DETAIL_REVENUE.revenue_month2
  is '營收2月';
comment on column FIT_FORECAST_DETAIL_REVENUE.revenue_month3
  is '營收3月';
comment on column FIT_FORECAST_DETAIL_REVENUE.revenue_month4
  is '營收4月';
comment on column FIT_FORECAST_DETAIL_REVENUE.revenue_month5
  is '營收5月';
comment on column FIT_FORECAST_DETAIL_REVENUE.revenue_month6
  is '營收6月';
comment on column FIT_FORECAST_DETAIL_REVENUE.revenue_month7
  is '營收7月';
comment on column FIT_FORECAST_DETAIL_REVENUE.revenue_month8
  is '營收8月';
comment on column FIT_FORECAST_DETAIL_REVENUE.revenue_month9
  is '營收9月';
comment on column FIT_FORECAST_DETAIL_REVENUE.revenue_month10
  is '營收10月';
comment on column FIT_FORECAST_DETAIL_REVENUE.revenue_month11
  is '營收11月';
comment on column FIT_FORECAST_DETAIL_REVENUE.revenue_month12
  is '營收12月';
comment on column FIT_FORECAST_DETAIL_REVENUE.price_month1
  is '单价1月';
comment on column FIT_FORECAST_DETAIL_REVENUE.price_month2
  is '单价2月';
comment on column FIT_FORECAST_DETAIL_REVENUE.price_month3
  is '单价3月';
comment on column FIT_FORECAST_DETAIL_REVENUE.price_month4
  is '单价4月';
comment on column FIT_FORECAST_DETAIL_REVENUE.price_month5
  is '单价5月';
comment on column FIT_FORECAST_DETAIL_REVENUE.price_month6
  is '单价6月';
comment on column FIT_FORECAST_DETAIL_REVENUE.price_month7
  is '单价7月';
comment on column FIT_FORECAST_DETAIL_REVENUE.price_month8
  is '单价8月';
comment on column FIT_FORECAST_DETAIL_REVENUE.price_month9
  is '单价9月';
comment on column FIT_FORECAST_DETAIL_REVENUE.price_month10
  is '单价10月';
comment on column FIT_FORECAST_DETAIL_REVENUE.price_month11
  is '单价11月';
comment on column FIT_FORECAST_DETAIL_REVENUE.price_month12
  is '单价12月';
alter table FIT_FORECAST_DETAIL_REVENUE
  add constraint PK_FIT_FORECAST_DET_REV_ID primary key (ID);

prompt
prompt Creating table FIT_FORECAST_DETAIL_REV_SRC
prompt ==========================================
prompt
create table FIT_FORECAST_DETAIL_REV_SRC
(
  id                           VARCHAR2(50) not null,
  entity                       VARCHAR2(50) not null,
  industry                     VARCHAR2(200 CHAR) not null,
  product                      VARCHAR2(200) not null,
  combine                      VARCHAR2(200 CHAR) not null,
  customer                     VARCHAR2(200 CHAR) not null,
  type                         VARCHAR2(200) not null,
  currency                     VARCHAR2(50) not null,
  version                      VARCHAR2(50) not null,
  scenarios                    VARCHAR2(50) not null,
  year                         CHAR(4) not null,
  industry_demand_trend        VARCHAR2(100),
  industry_demand_trend_served VARCHAR2(100),
  component_usage              VARCHAR2(100),
  average_sales_price          VARCHAR2(100),
  total_available_market       VARCHAR2(100),
  served_available_market      VARCHAR2(100),
  allocation                   VARCHAR2(100),
  revenue                      VARCHAR2(100),
  quantity                     VARCHAR2(100),
  quantity_month1              VARCHAR2(100),
  quantity_month2              VARCHAR2(100),
  quantity_month3              VARCHAR2(100),
  quantity_month4              VARCHAR2(100),
  quantity_month5              VARCHAR2(100),
  quantity_month6              VARCHAR2(100),
  quantity_month7              VARCHAR2(100),
  quantity_month8              VARCHAR2(100),
  quantity_month9              VARCHAR2(100),
  quantity_month10             VARCHAR2(100),
  quantity_month11             VARCHAR2(100),
  quantity_month12             VARCHAR2(100),
  revenue_month1               VARCHAR2(100),
  revenue_month2               VARCHAR2(100),
  revenue_month3               VARCHAR2(100),
  revenue_month4               VARCHAR2(100),
  revenue_month5               VARCHAR2(100),
  revenue_month6               VARCHAR2(100),
  revenue_month7               VARCHAR2(100),
  revenue_month8               VARCHAR2(100),
  revenue_month9               VARCHAR2(100),
  revenue_month10              VARCHAR2(100),
  revenue_month11              VARCHAR2(100),
  revenue_month12              VARCHAR2(100),
  material_month1              VARCHAR2(100),
  material_month2              VARCHAR2(100),
  material_month3              VARCHAR2(100),
  material_month4              VARCHAR2(100),
  material_month5              VARCHAR2(100),
  material_month6              VARCHAR2(100),
  material_month7              VARCHAR2(100),
  material_month8              VARCHAR2(100),
  material_month9              VARCHAR2(100),
  material_month10             VARCHAR2(100),
  material_month11             VARCHAR2(100),
  material_month12             VARCHAR2(100),
  manual_month1                VARCHAR2(100),
  manual_month2                VARCHAR2(100),
  manual_month3                VARCHAR2(100),
  manual_month4                VARCHAR2(100),
  manual_month5                VARCHAR2(100),
  manual_month6                VARCHAR2(100),
  manual_month7                VARCHAR2(100),
  manual_month8                VARCHAR2(100),
  manual_month9                VARCHAR2(100),
  manual_month10               VARCHAR2(100),
  manual_month11               VARCHAR2(100),
  manual_month12               VARCHAR2(100),
  manufacture_month1           VARCHAR2(100),
  manufacture_month2           VARCHAR2(100),
  manufacture_month3           VARCHAR2(100),
  manufacture_month4           VARCHAR2(100),
  manufacture_month5           VARCHAR2(100),
  manufacture_month6           VARCHAR2(100),
  manufacture_month7           VARCHAR2(100),
  manufacture_month8           VARCHAR2(100),
  manufacture_month9           VARCHAR2(100),
  manufacture_month10          VARCHAR2(100),
  manufacture_month11          VARCHAR2(100),
  manufacture_month12          VARCHAR2(100),
  price_month1                 VARCHAR2(100),
  price_month2                 VARCHAR2(100),
  price_month3                 VARCHAR2(100),
  price_month4                 VARCHAR2(100),
  price_month5                 VARCHAR2(100),
  price_month6                 VARCHAR2(100),
  price_month7                 VARCHAR2(100),
  price_month8                 VARCHAR2(100),
  price_month9                 VARCHAR2(100),
  price_month10                VARCHAR2(100),
  price_month11                VARCHAR2(100),
  price_month12                VARCHAR2(100)
)
;
comment on column FIT_FORECAST_DETAIL_REV_SRC.entity
  is 'SBU_法人';
comment on column FIT_FORECAST_DETAIL_REV_SRC.industry
  is '次產業';
comment on column FIT_FORECAST_DETAIL_REV_SRC.product
  is '產品料號';
comment on column FIT_FORECAST_DETAIL_REV_SRC.combine
  is '最終客戶';
comment on column FIT_FORECAST_DETAIL_REV_SRC.customer
  is '賬款客戶';
comment on column FIT_FORECAST_DETAIL_REV_SRC.type
  is '交易類型';
comment on column FIT_FORECAST_DETAIL_REV_SRC.currency
  is '交易貨幣';
comment on column FIT_FORECAST_DETAIL_REV_SRC.version
  is '版本';
comment on column FIT_FORECAST_DETAIL_REV_SRC.scenarios
  is '場景';
comment on column FIT_FORECAST_DETAIL_REV_SRC.year
  is '年';
comment on column FIT_FORECAST_DETAIL_REV_SRC.industry_demand_trend
  is 'Industry Demand Trend';
comment on column FIT_FORECAST_DETAIL_REV_SRC.industry_demand_trend_served
  is 'Industry Demand Trend-Served';
comment on column FIT_FORECAST_DETAIL_REV_SRC.component_usage
  is 'Component Usage/ 用量';
comment on column FIT_FORECAST_DETAIL_REV_SRC.average_sales_price
  is 'Average Sales Price / 平均單價';
comment on column FIT_FORECAST_DETAIL_REV_SRC.total_available_market
  is 'Total Available Market / TAM';
comment on column FIT_FORECAST_DETAIL_REV_SRC.served_available_market
  is 'Served Available Market / SAM';
comment on column FIT_FORECAST_DETAIL_REV_SRC.allocation
  is 'Allocation';
comment on column FIT_FORECAST_DETAIL_REV_SRC.revenue
  is '營收';
comment on column FIT_FORECAST_DETAIL_REV_SRC.quantity
  is '銷售數量';
comment on column FIT_FORECAST_DETAIL_REV_SRC.quantity_month1
  is '銷售數量1月';
comment on column FIT_FORECAST_DETAIL_REV_SRC.quantity_month2
  is '銷售數量2月';
comment on column FIT_FORECAST_DETAIL_REV_SRC.quantity_month3
  is '銷售數量3月';
comment on column FIT_FORECAST_DETAIL_REV_SRC.quantity_month4
  is '銷售數量4月';
comment on column FIT_FORECAST_DETAIL_REV_SRC.quantity_month5
  is '銷售數量5月';
comment on column FIT_FORECAST_DETAIL_REV_SRC.quantity_month6
  is '銷售數量6月';
comment on column FIT_FORECAST_DETAIL_REV_SRC.quantity_month7
  is '銷售數量7月';
comment on column FIT_FORECAST_DETAIL_REV_SRC.quantity_month8
  is '銷售數量8月';
comment on column FIT_FORECAST_DETAIL_REV_SRC.quantity_month9
  is '銷售數量9月';
comment on column FIT_FORECAST_DETAIL_REV_SRC.quantity_month10
  is '銷售數量10月';
comment on column FIT_FORECAST_DETAIL_REV_SRC.quantity_month11
  is '銷售數量11月';
comment on column FIT_FORECAST_DETAIL_REV_SRC.quantity_month12
  is '銷售數量12月';
comment on column FIT_FORECAST_DETAIL_REV_SRC.revenue_month1
  is '營收1月';
comment on column FIT_FORECAST_DETAIL_REV_SRC.revenue_month2
  is '營收2月';
comment on column FIT_FORECAST_DETAIL_REV_SRC.revenue_month3
  is '營收3月';
comment on column FIT_FORECAST_DETAIL_REV_SRC.revenue_month4
  is '營收4月';
comment on column FIT_FORECAST_DETAIL_REV_SRC.revenue_month5
  is '營收5月';
comment on column FIT_FORECAST_DETAIL_REV_SRC.revenue_month6
  is '營收6月';
comment on column FIT_FORECAST_DETAIL_REV_SRC.revenue_month7
  is '營收7月';
comment on column FIT_FORECAST_DETAIL_REV_SRC.revenue_month8
  is '營收8月';
comment on column FIT_FORECAST_DETAIL_REV_SRC.revenue_month9
  is '營收9月';
comment on column FIT_FORECAST_DETAIL_REV_SRC.revenue_month10
  is '營收10月';
comment on column FIT_FORECAST_DETAIL_REV_SRC.revenue_month11
  is '營收11月';
comment on column FIT_FORECAST_DETAIL_REV_SRC.revenue_month12
  is '營收12月';
comment on column FIT_FORECAST_DETAIL_REV_SRC.material_month1
  is '销售材料成本1月';
comment on column FIT_FORECAST_DETAIL_REV_SRC.material_month2
  is '销售材料成本2月';
comment on column FIT_FORECAST_DETAIL_REV_SRC.material_month3
  is '销售材料成本3月';
comment on column FIT_FORECAST_DETAIL_REV_SRC.material_month4
  is '销售材料成本4月';
comment on column FIT_FORECAST_DETAIL_REV_SRC.material_month5
  is '销售材料成本5月';
comment on column FIT_FORECAST_DETAIL_REV_SRC.material_month6
  is '销售材料成本6月';
comment on column FIT_FORECAST_DETAIL_REV_SRC.material_month7
  is '销售材料成本7月';
comment on column FIT_FORECAST_DETAIL_REV_SRC.material_month8
  is '销售材料成本8月';
comment on column FIT_FORECAST_DETAIL_REV_SRC.material_month9
  is '销售材料成本9月';
comment on column FIT_FORECAST_DETAIL_REV_SRC.material_month10
  is '销售材料成本10月';
comment on column FIT_FORECAST_DETAIL_REV_SRC.material_month11
  is '销售材料成本11月';
comment on column FIT_FORECAST_DETAIL_REV_SRC.material_month12
  is '销售材料成本12月';
comment on column FIT_FORECAST_DETAIL_REV_SRC.manual_month1
  is '销售人工成本1月';
comment on column FIT_FORECAST_DETAIL_REV_SRC.manual_month2
  is '销售人工成本2月';
comment on column FIT_FORECAST_DETAIL_REV_SRC.manual_month3
  is '销售人工成本3月';
comment on column FIT_FORECAST_DETAIL_REV_SRC.manual_month4
  is '销售人工成本4月';
comment on column FIT_FORECAST_DETAIL_REV_SRC.manual_month5
  is '销售人工成本5月';
comment on column FIT_FORECAST_DETAIL_REV_SRC.manual_month6
  is '销售人工成本6月';
comment on column FIT_FORECAST_DETAIL_REV_SRC.manual_month7
  is '销售人工成本7月';
comment on column FIT_FORECAST_DETAIL_REV_SRC.manual_month8
  is '销售人工成本8月';
comment on column FIT_FORECAST_DETAIL_REV_SRC.manual_month9
  is '销售人工成本9月';
comment on column FIT_FORECAST_DETAIL_REV_SRC.manual_month10
  is '销售人工成本10月';
comment on column FIT_FORECAST_DETAIL_REV_SRC.manual_month11
  is '销售人工成本11月';
comment on column FIT_FORECAST_DETAIL_REV_SRC.manual_month12
  is '销售人工成本12月';
comment on column FIT_FORECAST_DETAIL_REV_SRC.manufacture_month1
  is '销售制造成本1月';
comment on column FIT_FORECAST_DETAIL_REV_SRC.manufacture_month2
  is '销售制造成本2月';
comment on column FIT_FORECAST_DETAIL_REV_SRC.manufacture_month3
  is '销售制造成本3月';
comment on column FIT_FORECAST_DETAIL_REV_SRC.manufacture_month4
  is '销售制造成本4月';
comment on column FIT_FORECAST_DETAIL_REV_SRC.manufacture_month5
  is '销售制造成本5月';
comment on column FIT_FORECAST_DETAIL_REV_SRC.manufacture_month6
  is '销售制造成本6月';
comment on column FIT_FORECAST_DETAIL_REV_SRC.manufacture_month7
  is '销售制造成本7月';
comment on column FIT_FORECAST_DETAIL_REV_SRC.manufacture_month8
  is '销售制造成本8月';
comment on column FIT_FORECAST_DETAIL_REV_SRC.manufacture_month9
  is '销售制造成本9月';
comment on column FIT_FORECAST_DETAIL_REV_SRC.manufacture_month10
  is '销售制造成本10月';
comment on column FIT_FORECAST_DETAIL_REV_SRC.manufacture_month11
  is '销售制造成本11月';
comment on column FIT_FORECAST_DETAIL_REV_SRC.manufacture_month12
  is '销售制造成本12月';
comment on column FIT_FORECAST_DETAIL_REV_SRC.price_month1
  is '单价1月';
comment on column FIT_FORECAST_DETAIL_REV_SRC.price_month2
  is '单价2月';
comment on column FIT_FORECAST_DETAIL_REV_SRC.price_month3
  is '单价3月';
comment on column FIT_FORECAST_DETAIL_REV_SRC.price_month4
  is '单价4月';
comment on column FIT_FORECAST_DETAIL_REV_SRC.price_month5
  is '单价5月';
comment on column FIT_FORECAST_DETAIL_REV_SRC.price_month6
  is '单价6月';
comment on column FIT_FORECAST_DETAIL_REV_SRC.price_month7
  is '单价7月';
comment on column FIT_FORECAST_DETAIL_REV_SRC.price_month8
  is '单价8月';
comment on column FIT_FORECAST_DETAIL_REV_SRC.price_month9
  is '单价9月';
comment on column FIT_FORECAST_DETAIL_REV_SRC.price_month10
  is '单价10月';
comment on column FIT_FORECAST_DETAIL_REV_SRC.price_month11
  is '单价11月';
comment on column FIT_FORECAST_DETAIL_REV_SRC.price_month12
  is '单价12月';
alter table FIT_FORECAST_DETAIL_REV_SRC
  add constraint PK_FIT_FORECAST_DET_REV_SRC_ID primary key (ID);

prompt
prompt Creating table FIT_GENERAL_LEDGER
prompt =================================
prompt
create table FIT_GENERAL_LEDGER
(
  id                  VARCHAR2(50) not null,
  corporation_code    VARCHAR2(50) not null,
  year                VARCHAR2(4) not null,
  period              VARCHAR2(2) not null,
  item_code           VARCHAR2(100),
  item_desc           VARCHAR2(100),
  assets_state        VARCHAR2(100),
  balance_state       VARCHAR2(100),
  begin_balance       VARCHAR2(100),
  curr_debit_balance  VARCHAR2(100),
  curr_credit_balance VARCHAR2(100),
  end_balance         VARCHAR2(100)
)
;
comment on table FIT_GENERAL_LEDGER
  is '總賬';
comment on column FIT_GENERAL_LEDGER.corporation_code
  is '法人编码';
comment on column FIT_GENERAL_LEDGER.year
  is '年';
comment on column FIT_GENERAL_LEDGER.period
  is '期間';
comment on column FIT_GENERAL_LEDGER.item_code
  is '科目編號';
comment on column FIT_GENERAL_LEDGER.item_desc
  is '科目描述';
comment on column FIT_GENERAL_LEDGER.assets_state
  is '資產損益別(1.資產負債2.損益)';
comment on column FIT_GENERAL_LEDGER.balance_state
  is '正常餘額型態 (1.借餘/2.貸餘)';
comment on column FIT_GENERAL_LEDGER.begin_balance
  is '期初餘額';
comment on column FIT_GENERAL_LEDGER.curr_debit_balance
  is '本期借方發生額';
comment on column FIT_GENERAL_LEDGER.curr_credit_balance
  is '本期貸方發生額';
comment on column FIT_GENERAL_LEDGER.end_balance
  is '期末餘額';
alter table FIT_GENERAL_LEDGER
  add constraint PK_FIT_GENERAL_LEDGER_ID primary key (ID);

prompt
prompt Creating table FIT_GENERAL_LEDGER_TEST
prompt ======================================
prompt
create table FIT_GENERAL_LEDGER_TEST
(
  id                  VARCHAR2(50) not null,
  corporation_code    VARCHAR2(50) not null,
  year                VARCHAR2(4) not null,
  period              VARCHAR2(2) not null,
  item_code           VARCHAR2(50),
  assets_state        VARCHAR2(50),
  balance_state       VARCHAR2(50),
  begin_balance       VARCHAR2(20),
  curr_debit_balance  VARCHAR2(20),
  curr_credit_balance VARCHAR2(20),
  end_balance         VARCHAR2(20),
  item_desc           VARCHAR2(100)
)
;

prompt
prompt Creating table FIT_HFM_CUSTOMER_MAPPING
prompt =======================================
prompt
create table FIT_HFM_CUSTOMER_MAPPING
(
  id               VARCHAR2(50) not null,
  corporation_code VARCHAR2(200),
  erp_code         VARCHAR2(200),
  erp_desc         VARCHAR2(200),
  hfm_code         VARCHAR2(200),
  hfm_desc         VARCHAR2(200)
)
;
comment on table FIT_HFM_CUSTOMER_MAPPING
  is '客戶映射維護';
comment on column FIT_HFM_CUSTOMER_MAPPING.corporation_code
  is 'ERP法人编码';
comment on column FIT_HFM_CUSTOMER_MAPPING.erp_code
  is 'ERP客戶編碼';
comment on column FIT_HFM_CUSTOMER_MAPPING.erp_desc
  is 'ERP客戶描述';
comment on column FIT_HFM_CUSTOMER_MAPPING.hfm_code
  is 'HFM客戶編碼';
comment on column FIT_HFM_CUSTOMER_MAPPING.hfm_desc
  is 'HFM客戶描述';
alter table FIT_HFM_CUSTOMER_MAPPING
  add constraint PK_HFM_CUS_MAPPING_ID primary key (ID);

prompt
prompt Creating table FIT_ICP_CUST_MAPPING
prompt ===================================
prompt
create table FIT_ICP_CUST_MAPPING
(
  erp_entity_code VARCHAR2(200),
  erp_icp_code    VARCHAR2(200),
  erp_icp_name    VARCHAR2(200),
  hfm_icp_code    VARCHAR2(200),
  hfm_icp_name    VARCHAR2(200),
  attribute1      VARCHAR2(200),
  attribute2      VARCHAR2(200),
  attribute3      VARCHAR2(200),
  attribute4      VARCHAR2(200),
  attribute5      VARCHAR2(200)
)
;
comment on column FIT_ICP_CUST_MAPPING.erp_entity_code
  is 'ER_ entity編碼';
comment on column FIT_ICP_CUST_MAPPING.erp_icp_code
  is 'ERPICPname';
comment on column FIT_ICP_CUST_MAPPING.hfm_icp_code
  is 'HFMICP編碼';
comment on column FIT_ICP_CUST_MAPPING.hfm_icp_name
  is 'HFMICP描述';

prompt
prompt Creating table FIT_ICP_VENDOR_MAPPING
prompt =====================================
prompt
create table FIT_ICP_VENDOR_MAPPING
(
  erp_entity_code VARCHAR2(200),
  erp_icp_code    VARCHAR2(200),
  erp_icp_name    VARCHAR2(200),
  hfm_icp_code    VARCHAR2(200),
  hfm_icp_name    VARCHAR2(200),
  attribute1      VARCHAR2(200),
  attribute2      VARCHAR2(200),
  attribute3      VARCHAR2(200),
  attribute4      VARCHAR2(200),
  attribute5      VARCHAR2(200)
)
;
comment on column FIT_ICP_VENDOR_MAPPING.erp_icp_code
  is 'ERPICPNAME';
comment on column FIT_ICP_VENDOR_MAPPING.hfm_icp_code
  is 'HFMICP編碼';
comment on column FIT_ICP_VENDOR_MAPPING.hfm_icp_name
  is 'HFMICP描述';

prompt
prompt Creating table FIT_LEGAL_ENTITY_LIST
prompt ====================================
prompt
create table FIT_LEGAL_ENTITY_LIST
(
  id               VARCHAR2(50) not null,
  corporation_code VARCHAR2(100),
  legal_full_name  VARCHAR2(100),
  legal_name       VARCHAR2(100),
  currency         VARCHAR2(100),
  system           VARCHAR2(100),
  erp_code         VARCHAR2(100)
)
;
comment on table FIT_LEGAL_ENTITY_LIST
  is '管報法人列表';
comment on column FIT_LEGAL_ENTITY_LIST.corporation_code
  is '管報法人編碼';
comment on column FIT_LEGAL_ENTITY_LIST.legal_full_name
  is '法人全稱';
comment on column FIT_LEGAL_ENTITY_LIST.legal_name
  is '法人簡稱';
comment on column FIT_LEGAL_ENTITY_LIST.currency
  is '本位幣';
comment on column FIT_LEGAL_ENTITY_LIST.system
  is '體系';
comment on column FIT_LEGAL_ENTITY_LIST.erp_code
  is '對應ERP編碼';
alter table FIT_LEGAL_ENTITY_LIST
  add constraint PK_LEGAL_ENTITY_LIST_ID primary key (ID);

prompt
prompt Creating table FIT_OUTSOURCE_ACTUAL_NUMBER
prompt ==========================================
prompt
create table FIT_OUTSOURCE_ACTUAL_NUMBER
(
  id             VARCHAR2(50) not null,
  year           VARCHAR2(100),
  period         VARCHAR2(100),
  factory        VARCHAR2(100),
  bu             VARCHAR2(100),
  sbu            VARCHAR2(100),
  outsource_cost VARCHAR2(100),
  factory_cost   VARCHAR2(100),
  ntd            VARCHAR2(100) default 'NTD' not null,
  revenue        VARCHAR2(100)
)
;
comment on table FIT_OUTSOURCE_ACTUAL_NUMBER
  is '外包成本收集';
comment on column FIT_OUTSOURCE_ACTUAL_NUMBER.year
  is '年';
comment on column FIT_OUTSOURCE_ACTUAL_NUMBER.period
  is '月';
comment on column FIT_OUTSOURCE_ACTUAL_NUMBER.factory
  is '廠區';
comment on column FIT_OUTSOURCE_ACTUAL_NUMBER.bu
  is 'BU';
comment on column FIT_OUTSOURCE_ACTUAL_NUMBER.sbu
  is 'SBU';
comment on column FIT_OUTSOURCE_ACTUAL_NUMBER.outsource_cost
  is '外包成本';
comment on column FIT_OUTSOURCE_ACTUAL_NUMBER.factory_cost
  is '場內自製成本';
comment on column FIT_OUTSOURCE_ACTUAL_NUMBER.ntd
  is 'NTD';
comment on column FIT_OUTSOURCE_ACTUAL_NUMBER.revenue
  is '廠區營收';
alter table FIT_OUTSOURCE_ACTUAL_NUMBER
  add constraint PK_FIT_OUTSOURCE_ACTUAL_NO_ID primary key (ID);

prompt
prompt Creating table FIT_PARAMETER
prompt ============================
prompt
create table FIT_PARAMETER
(
  key   VARCHAR2(100) not null,
  value VARCHAR2(200)
)
;
alter table FIT_PARAMETER
  add constraint PK_FIT_PARAMETER_KEY primary key (KEY);

prompt
prompt Creating table FIT_PARAMETER_ENTITY
prompt ===================================
prompt
create table FIT_PARAMETER_ENTITY
(
  entity_code VARCHAR2(200)
)
;

prompt
prompt Creating table FIT_PERIOD_TRANS
prompt ===============================
prompt
create table FIT_PERIOD_TRANS
(
  period_num NUMBER,
  period_en  VARCHAR2(100)
)
;

prompt
prompt Creating table FIT_PLANNING
prompt ===========================
prompt
create table FIT_PLANNING
(
  id                  VARCHAR2(50) default sys_guid() not null,
  account             VARCHAR2(200),
  jan                 VARCHAR2(50),
  feb                 VARCHAR2(50),
  mar                 VARCHAR2(50),
  apr                 VARCHAR2(50),
  may                 VARCHAR2(50),
  jun                 VARCHAR2(50),
  jul                 VARCHAR2(50),
  aug                 VARCHAR2(50),
  sep                 VARCHAR2(50),
  oct                 VARCHAR2(50),
  nov                 VARCHAR2(50),
  dec                 VARCHAR2(50),
  yt                  VARCHAR2(50),
  point_of_view       VARCHAR2(200) not null,
  data_load_cube_name VARCHAR2(10) default 'Plan3'
)
;
alter table FIT_PLANNING
  add constraint PK_FIT_PLANNING_ID primary key (ID);

prompt
prompt Creating table FIT_PRICE
prompt ========================
prompt
create table FIT_PRICE
(
  id       VARCHAR2(50) not null,
  year     VARCHAR2(100),
  month    VARCHAR2(100),
  customer VARCHAR2(100),
  category VARCHAR2(100),
  decision VARCHAR2(100),
  revenue  VARCHAR2(100),
  rate     VARCHAR2(100),
  currency VARCHAR2(100) default 'NTD' not null
)
;
comment on table FIT_PRICE
  is '營收價格決定權';
comment on column FIT_PRICE.year
  is '年';
comment on column FIT_PRICE.month
  is '月';
comment on column FIT_PRICE.customer
  is '最終客戶';
comment on column FIT_PRICE.category
  is '項目';
comment on column FIT_PRICE.decision
  is '決定權';
comment on column FIT_PRICE.revenue
  is '營收';
comment on column FIT_PRICE.rate
  is '佔比(%)';
comment on column FIT_PRICE.currency
  is 'NTD';
alter table FIT_PRICE
  add constraint PK_PRICE_ID primary key (ID);

prompt
prompt Creating table FIT_PRIMARY_SECONDARY_INDUSTRY
prompt =============================================
prompt
create table FIT_PRIMARY_SECONDARY_INDUSTRY
(
  id                     VARCHAR2(50) not null,
  bu                     VARCHAR2(100),
  sbu                    VARCHAR2(100),
  pm_director            VARCHAR2(100),
  collection_window      VARCHAR2(100),
  product                VARCHAR2(100),
  customer_product       VARCHAR2(100),
  account_customer_code  VARCHAR2(100),
  account_customer_name  VARCHAR2(200),
  account_customer_group VARCHAR2(200),
  document               VARCHAR2(300),
  major_industry         VARCHAR2(100),
  secondary_industry     VARCHAR2(100),
  brand                  VARCHAR2(100),
  brand_customer_name    VARCHAR2(100),
  customer_level         VARCHAR2(100),
  aggregate_time         VARCHAR2(100)
)
;
comment on table FIT_PRIMARY_SECONDARY_INDUSTRY
  is '主次產業對照檔';
comment on column FIT_PRIMARY_SECONDARY_INDUSTRY.pm_director
  is 'PM主管(敬稱略)';
comment on column FIT_PRIMARY_SECONDARY_INDUSTRY.collection_window
  is '收集窗口(敬稱略)';
comment on column FIT_PRIMARY_SECONDARY_INDUSTRY.product
  is '產品品號';
comment on column FIT_PRIMARY_SECONDARY_INDUSTRY.customer_product
  is '客戶產品品號';
comment on column FIT_PRIMARY_SECONDARY_INDUSTRY.account_customer_code
  is '帳款客戶（代碼）';
comment on column FIT_PRIMARY_SECONDARY_INDUSTRY.account_customer_name
  is '帳款客戶（名稱）';
comment on column FIT_PRIMARY_SECONDARY_INDUSTRY.account_customer_group
  is '帳款客戶（集團）';
comment on column FIT_PRIMARY_SECONDARY_INDUSTRY.document
  is '對照檔（產品料號+客戶料號+客戶代碼）';
comment on column FIT_PRIMARY_SECONDARY_INDUSTRY.major_industry
  is '主產業';
comment on column FIT_PRIMARY_SECONDARY_INDUSTRY.secondary_industry
  is '次產業';
comment on column FIT_PRIMARY_SECONDARY_INDUSTRY.brand
  is '品牌or直售';
comment on column FIT_PRIMARY_SECONDARY_INDUSTRY.brand_customer_name
  is '品牌客戶名稱';
comment on column FIT_PRIMARY_SECONDARY_INDUSTRY.customer_level
  is '客戶分級分類';
comment on column FIT_PRIMARY_SECONDARY_INDUSTRY.aggregate_time
  is '更新匯總時間';
alter table FIT_PRIMARY_SECONDARY_INDUSTRY
  add constraint PK_FIT_PRIMARY_SEC_INDUSTRY_ID primary key (ID);

prompt
prompt Creating table FIT_PRODUCT_BCG
prompt ==============================
prompt
create table FIT_PRODUCT_BCG
(
  id             VARCHAR2(50) not null,
  version        VARCHAR2(6),
  sbu            VARCHAR2(100),
  part_no        VARCHAR2(100),
  product_family VARCHAR2(100),
  product_series VARCHAR2(100),
  bcg            VARCHAR2(100)
)
;
comment on table FIT_PRODUCT_BCG
  is '產品BCG映射';
comment on column FIT_PRODUCT_BCG.version
  is '版本（年月）';
comment on column FIT_PRODUCT_BCG.sbu
  is 'SBU';
comment on column FIT_PRODUCT_BCG.part_no
  is 'partNo_col';
comment on column FIT_PRODUCT_BCG.product_family
  is 'productFamily_col';
comment on column FIT_PRODUCT_BCG.product_series
  is 'productSeries_col';
comment on column FIT_PRODUCT_BCG.bcg
  is 'BCG';
alter table FIT_PRODUCT_BCG
  add constraint PK_FIT_PRODUCT_BCG_ID primary key (ID);

prompt
prompt Creating table FIT_PRODUCT_NO_UNIT_COST
prompt =======================================
prompt
create table FIT_PRODUCT_NO_UNIT_COST
(
  id                          VARCHAR2(50) not null,
  product                     VARCHAR2(100) not null,
  material_standard_cost1     VARCHAR2(50),
  material_adjust_cost1       VARCHAR2(50),
  material_cost1              VARCHAR2(50),
  standard_hours1             VARCHAR2(50),
  adjust_hours1               VARCHAR2(50),
  hours1                      VARCHAR2(50),
  manual_standard_rate1       VARCHAR2(50),
  manual_adjust_rate1         VARCHAR2(50),
  manual_rate1                VARCHAR2(50),
  manual_cost1                VARCHAR2(50),
  manufacture_standard_rate1  VARCHAR2(50),
  manufacture_adjust_rate1    VARCHAR2(50),
  manufacture_rate1           VARCHAR2(50),
  manufacture_cost1           VARCHAR2(50),
  unit_cost1                  VARCHAR2(50),
  sbu                         VARCHAR2(200),
  material_standard_cost2     VARCHAR2(50),
  material_adjust_cost2       VARCHAR2(50),
  material_cost2              VARCHAR2(50),
  standard_hours2             VARCHAR2(50),
  adjust_hours2               VARCHAR2(50),
  hours2                      VARCHAR2(50),
  manual_standard_rate2       VARCHAR2(50),
  manual_adjust_rate2         VARCHAR2(50),
  manual_rate2                VARCHAR2(50),
  manual_cost2                VARCHAR2(50),
  manufacture_standard_rate2  VARCHAR2(50),
  manufacture_adjust_rate2    VARCHAR2(50),
  manufacture_rate2           VARCHAR2(50),
  manufacture_cost2           VARCHAR2(50),
  unit_cost2                  VARCHAR2(50),
  material_standard_cost3     VARCHAR2(50),
  material_adjust_cost3       VARCHAR2(50),
  material_cost3              VARCHAR2(50),
  standard_hours3             VARCHAR2(50),
  adjust_hours3               VARCHAR2(50),
  hours3                      VARCHAR2(50),
  manual_standard_rate3       VARCHAR2(50),
  manual_adjust_rate3         VARCHAR2(50),
  manual_rate3                VARCHAR2(50),
  manual_cost3                VARCHAR2(50),
  manufacture_standard_rate3  VARCHAR2(50),
  manufacture_adjust_rate3    VARCHAR2(50),
  manufacture_rate3           VARCHAR2(50),
  manufacture_cost3           VARCHAR2(50),
  unit_cost3                  VARCHAR2(50),
  material_standard_cost4     VARCHAR2(50),
  material_adjust_cost4       VARCHAR2(50),
  material_cost4              VARCHAR2(50),
  standard_hours4             VARCHAR2(50),
  adjust_hours4               VARCHAR2(50),
  hours4                      VARCHAR2(50),
  manual_standard_rate4       VARCHAR2(50),
  manual_adjust_rate4         VARCHAR2(50),
  manual_rate4                VARCHAR2(50),
  manual_cost4                VARCHAR2(50),
  manufacture_standard_rate4  VARCHAR2(50),
  manufacture_adjust_rate4    VARCHAR2(50),
  manufacture_rate4           VARCHAR2(50),
  manufacture_cost4           VARCHAR2(50),
  unit_cost4                  VARCHAR2(50),
  material_standard_cost5     VARCHAR2(50),
  material_adjust_cost5       VARCHAR2(50),
  material_cost5              VARCHAR2(50),
  standard_hours5             VARCHAR2(50),
  adjust_hours5               VARCHAR2(50),
  hours5                      VARCHAR2(50),
  manual_standard_rate5       VARCHAR2(50),
  manual_adjust_rate5         VARCHAR2(50),
  manual_rate5                VARCHAR2(50),
  manual_cost5                VARCHAR2(50),
  manufacture_standard_rate5  VARCHAR2(50),
  manufacture_adjust_rate5    VARCHAR2(50),
  manufacture_rate5           VARCHAR2(50),
  manufacture_cost5           VARCHAR2(50),
  unit_cost5                  VARCHAR2(50),
  material_standard_cost6     VARCHAR2(50),
  material_adjust_cost6       VARCHAR2(50),
  material_cost6              VARCHAR2(50),
  standard_hours6             VARCHAR2(50),
  adjust_hours6               VARCHAR2(50),
  hours6                      VARCHAR2(50),
  manual_standard_rate6       VARCHAR2(50),
  manual_adjust_rate6         VARCHAR2(50),
  manual_rate6                VARCHAR2(50),
  manual_cost6                VARCHAR2(50),
  manufacture_standard_rate6  VARCHAR2(50),
  manufacture_adjust_rate6    VARCHAR2(50),
  manufacture_rate6           VARCHAR2(50),
  manufacture_cost6           VARCHAR2(50),
  unit_cost6                  VARCHAR2(50),
  material_standard_cost7     VARCHAR2(50),
  material_adjust_cost7       VARCHAR2(50),
  material_cost7              VARCHAR2(50),
  standard_hours7             VARCHAR2(50),
  adjust_hours7               VARCHAR2(50),
  hours7                      VARCHAR2(50),
  manual_standard_rate7       VARCHAR2(50),
  manual_adjust_rate7         VARCHAR2(50),
  manual_rate7                VARCHAR2(50),
  manual_cost7                VARCHAR2(50),
  manufacture_standard_rate7  VARCHAR2(50),
  manufacture_adjust_rate7    VARCHAR2(50),
  manufacture_rate7           VARCHAR2(50),
  manufacture_cost7           VARCHAR2(50),
  unit_cost7                  VARCHAR2(50),
  material_standard_cost8     VARCHAR2(50),
  material_adjust_cost8       VARCHAR2(50),
  material_cost8              VARCHAR2(50),
  standard_hours8             VARCHAR2(50),
  adjust_hours8               VARCHAR2(50),
  hours8                      VARCHAR2(50),
  manual_standard_rate8       VARCHAR2(50),
  manual_adjust_rate8         VARCHAR2(50),
  manual_rate8                VARCHAR2(50),
  manual_cost8                VARCHAR2(50),
  manufacture_standard_rate8  VARCHAR2(50),
  manufacture_adjust_rate8    VARCHAR2(50),
  manufacture_rate8           VARCHAR2(50),
  manufacture_cost8           VARCHAR2(50),
  unit_cost8                  VARCHAR2(50),
  material_standard_cost9     VARCHAR2(50),
  material_adjust_cost9       VARCHAR2(50),
  material_cost9              VARCHAR2(50),
  standard_hours9             VARCHAR2(50),
  adjust_hours9               VARCHAR2(50),
  hours9                      VARCHAR2(50),
  manual_standard_rate9       VARCHAR2(50),
  manual_adjust_rate9         VARCHAR2(50),
  manual_rate9                VARCHAR2(50),
  manual_cost9                VARCHAR2(50),
  manufacture_standard_rate9  VARCHAR2(50),
  manufacture_adjust_rate9    VARCHAR2(50),
  manufacture_rate9           VARCHAR2(50),
  manufacture_cost9           VARCHAR2(50),
  unit_cost9                  VARCHAR2(50),
  material_standard_cost10    VARCHAR2(50),
  material_adjust_cost10      VARCHAR2(50),
  material_cost10             VARCHAR2(50),
  standard_hours10            VARCHAR2(50),
  adjust_hours10              VARCHAR2(50),
  hours10                     VARCHAR2(50),
  manual_standard_rate10      VARCHAR2(50),
  manual_adjust_rate10        VARCHAR2(50),
  manual_rate10               VARCHAR2(50),
  manual_cost10               VARCHAR2(50),
  manufacture_standard_rate10 VARCHAR2(50),
  manufacture_adjust_rate10   VARCHAR2(50),
  manufacture_rate10          VARCHAR2(50),
  manufacture_cost10          VARCHAR2(50),
  unit_cost10                 VARCHAR2(50),
  material_standard_cost11    VARCHAR2(50),
  material_adjust_cost11      VARCHAR2(50),
  material_cost11             VARCHAR2(50),
  standard_hours11            VARCHAR2(50),
  adjust_hours11              VARCHAR2(50),
  hours11                     VARCHAR2(50),
  manual_standard_rate11      VARCHAR2(50),
  manual_adjust_rate11        VARCHAR2(50),
  manual_rate11               VARCHAR2(50),
  manual_cost11               VARCHAR2(50),
  manufacture_standard_rate11 VARCHAR2(50),
  manufacture_adjust_rate11   VARCHAR2(50),
  manufacture_rate11          VARCHAR2(50),
  manufacture_cost11          VARCHAR2(50),
  unit_cost11                 VARCHAR2(50),
  material_standard_cost12    VARCHAR2(50),
  material_adjust_cost12      VARCHAR2(50),
  material_cost12             VARCHAR2(50),
  standard_hours12            VARCHAR2(50),
  adjust_hours12              VARCHAR2(50),
  hours12                     VARCHAR2(50),
  manual_standard_rate12      VARCHAR2(50),
  manual_adjust_rate12        VARCHAR2(50),
  manual_rate12               VARCHAR2(50),
  manual_cost12               VARCHAR2(50),
  manufacture_standard_rate12 VARCHAR2(50),
  manufacture_adjust_rate12   VARCHAR2(50),
  manufacture_rate12          VARCHAR2(50),
  manufacture_cost12          VARCHAR2(50),
  unit_cost12                 VARCHAR2(50),
  entity                      VARCHAR2(100),
  year                        CHAR(4),
  scenarios                   VARCHAR2(50)
)
;
comment on table FIT_PRODUCT_NO_UNIT_COST
  is '產品料號單位成本表';
comment on column FIT_PRODUCT_NO_UNIT_COST.product
  is '產品料號';
comment on column FIT_PRODUCT_NO_UNIT_COST.material_standard_cost1
  is '單位材料標準成本1月';
comment on column FIT_PRODUCT_NO_UNIT_COST.material_adjust_cost1
  is '單位材料調整成本1月';
comment on column FIT_PRODUCT_NO_UNIT_COST.material_cost1
  is '單位材料成本1月';
comment on column FIT_PRODUCT_NO_UNIT_COST.standard_hours1
  is '單位標準工時1月';
comment on column FIT_PRODUCT_NO_UNIT_COST.adjust_hours1
  is '單位調整工時1月';
comment on column FIT_PRODUCT_NO_UNIT_COST.hours1
  is '單位工時1月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manual_standard_rate1
  is '單位人工標準費率1月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manual_adjust_rate1
  is '單位人工調整費率1月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manual_rate1
  is '單位人工費率1月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manual_cost1
  is '單位人工成本1月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manufacture_standard_rate1
  is '單位製造標準費率1月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manufacture_adjust_rate1
  is '單位製造調整費率1月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manufacture_rate1
  is '單位製造費率1月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manufacture_cost1
  is '單位製造成本1月';
comment on column FIT_PRODUCT_NO_UNIT_COST.unit_cost1
  is '單位成本1月';
comment on column FIT_PRODUCT_NO_UNIT_COST.material_standard_cost2
  is '單位材料標準成本2月';
comment on column FIT_PRODUCT_NO_UNIT_COST.material_adjust_cost2
  is '單位材料調整成本2月';
comment on column FIT_PRODUCT_NO_UNIT_COST.material_cost2
  is '單位材料成本2月';
comment on column FIT_PRODUCT_NO_UNIT_COST.standard_hours2
  is '單位標準工時2月';
comment on column FIT_PRODUCT_NO_UNIT_COST.adjust_hours2
  is '單位調整工時2月';
comment on column FIT_PRODUCT_NO_UNIT_COST.hours2
  is '單位工時2月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manual_standard_rate2
  is '單位人工標準費率2月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manual_adjust_rate2
  is '單位人工調整費率2月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manual_rate2
  is '單位人工費率2月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manual_cost2
  is '單位人工成本2月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manufacture_standard_rate2
  is '單位製造標準費率2月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manufacture_adjust_rate2
  is '單位製造調整費率2月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manufacture_rate2
  is '單位製造費率2月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manufacture_cost2
  is '單位製造成本2月';
comment on column FIT_PRODUCT_NO_UNIT_COST.unit_cost2
  is '單位成本2月';
comment on column FIT_PRODUCT_NO_UNIT_COST.material_standard_cost3
  is '單位材料標準成本3月';
comment on column FIT_PRODUCT_NO_UNIT_COST.material_adjust_cost3
  is '單位材料調整成本3月';
comment on column FIT_PRODUCT_NO_UNIT_COST.material_cost3
  is '單位材料成本3月';
comment on column FIT_PRODUCT_NO_UNIT_COST.standard_hours3
  is '單位標準工時3月';
comment on column FIT_PRODUCT_NO_UNIT_COST.adjust_hours3
  is '單位調整工時3月';
comment on column FIT_PRODUCT_NO_UNIT_COST.hours3
  is '單位工時3月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manual_standard_rate3
  is '單位人工標準費率3月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manual_adjust_rate3
  is '單位人工調整費率3月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manual_rate3
  is '單位人工費率3月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manual_cost3
  is '單位人工成本3月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manufacture_standard_rate3
  is '單位製造標準費率3月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manufacture_adjust_rate3
  is '單位製造調整費率3月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manufacture_rate3
  is '單位製造費率3月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manufacture_cost3
  is '單位製造成本3月';
comment on column FIT_PRODUCT_NO_UNIT_COST.unit_cost3
  is '單位成本3月';
comment on column FIT_PRODUCT_NO_UNIT_COST.material_standard_cost4
  is '單位材料標準成本4月';
comment on column FIT_PRODUCT_NO_UNIT_COST.material_adjust_cost4
  is '單位材料調整成本4月';
comment on column FIT_PRODUCT_NO_UNIT_COST.material_cost4
  is '單位材料成本4月';
comment on column FIT_PRODUCT_NO_UNIT_COST.standard_hours4
  is '單位標準工時4月';
comment on column FIT_PRODUCT_NO_UNIT_COST.adjust_hours4
  is '單位調整工時4月';
comment on column FIT_PRODUCT_NO_UNIT_COST.hours4
  is '單位工時4月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manual_standard_rate4
  is '單位人工標準費率4月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manual_adjust_rate4
  is '單位人工調整費率4月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manual_rate4
  is '單位人工費率4月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manual_cost4
  is '單位人工成本4月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manufacture_standard_rate4
  is '單位製造標準費率4月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manufacture_adjust_rate4
  is '單位製造調整費率4月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manufacture_rate4
  is '單位製造費率4月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manufacture_cost4
  is '單位製造成本4月';
comment on column FIT_PRODUCT_NO_UNIT_COST.unit_cost4
  is '單位成本4月';
comment on column FIT_PRODUCT_NO_UNIT_COST.material_standard_cost5
  is '單位材料標準成本5月';
comment on column FIT_PRODUCT_NO_UNIT_COST.material_adjust_cost5
  is '單位材料調整成本5月';
comment on column FIT_PRODUCT_NO_UNIT_COST.material_cost5
  is '單位材料成本5月';
comment on column FIT_PRODUCT_NO_UNIT_COST.standard_hours5
  is '單位標準工時5月';
comment on column FIT_PRODUCT_NO_UNIT_COST.adjust_hours5
  is '單位調整工時5月';
comment on column FIT_PRODUCT_NO_UNIT_COST.hours5
  is '單位工時5月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manual_standard_rate5
  is '單位人工標準費率5月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manual_adjust_rate5
  is '單位人工調整費率5月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manual_rate5
  is '單位人工費率5月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manual_cost5
  is '單位人工成本5月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manufacture_standard_rate5
  is '單位製造標準費率5月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manufacture_adjust_rate5
  is '單位製造調整費率5月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manufacture_rate5
  is '單位製造費率5月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manufacture_cost5
  is '單位製造成本5月';
comment on column FIT_PRODUCT_NO_UNIT_COST.unit_cost5
  is '單位成本5月';
comment on column FIT_PRODUCT_NO_UNIT_COST.material_standard_cost6
  is '單位材料標準成本6月';
comment on column FIT_PRODUCT_NO_UNIT_COST.material_adjust_cost6
  is '單位材料調整成本6月';
comment on column FIT_PRODUCT_NO_UNIT_COST.material_cost6
  is '單位材料成本6月';
comment on column FIT_PRODUCT_NO_UNIT_COST.standard_hours6
  is '單位標準工時6月';
comment on column FIT_PRODUCT_NO_UNIT_COST.adjust_hours6
  is '單位調整工時6月';
comment on column FIT_PRODUCT_NO_UNIT_COST.hours6
  is '單位工時6月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manual_standard_rate6
  is '單位人工標準費率6月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manual_adjust_rate6
  is '單位人工調整費率6月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manual_rate6
  is '單位人工費率6月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manual_cost6
  is '單位人工成本6月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manufacture_standard_rate6
  is '單位製造標準費率6月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manufacture_adjust_rate6
  is '單位製造調整費率6月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manufacture_rate6
  is '單位製造費率6月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manufacture_cost6
  is '單位製造成本6月';
comment on column FIT_PRODUCT_NO_UNIT_COST.unit_cost6
  is '單位成本6月';
comment on column FIT_PRODUCT_NO_UNIT_COST.material_standard_cost7
  is '單位材料標準成本7月';
comment on column FIT_PRODUCT_NO_UNIT_COST.material_adjust_cost7
  is '單位材料調整成本7月';
comment on column FIT_PRODUCT_NO_UNIT_COST.material_cost7
  is '單位材料成本7月';
comment on column FIT_PRODUCT_NO_UNIT_COST.standard_hours7
  is '單位標準工時7月';
comment on column FIT_PRODUCT_NO_UNIT_COST.adjust_hours7
  is '單位調整工時7月';
comment on column FIT_PRODUCT_NO_UNIT_COST.hours7
  is '單位工時7月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manual_standard_rate7
  is '單位人工標準費率7月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manual_adjust_rate7
  is '單位人工調整費率7月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manual_rate7
  is '單位人工費率7月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manual_cost7
  is '單位人工成本7月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manufacture_standard_rate7
  is '單位製造標準費率7月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manufacture_adjust_rate7
  is '單位製造調整費率7月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manufacture_rate7
  is '單位製造費率7月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manufacture_cost7
  is '單位製造成本7月';
comment on column FIT_PRODUCT_NO_UNIT_COST.unit_cost7
  is '單位成本7月';
comment on column FIT_PRODUCT_NO_UNIT_COST.material_standard_cost8
  is '單位材料標準成本8月';
comment on column FIT_PRODUCT_NO_UNIT_COST.material_adjust_cost8
  is '單位材料調整成本8月';
comment on column FIT_PRODUCT_NO_UNIT_COST.material_cost8
  is '單位材料成本8月';
comment on column FIT_PRODUCT_NO_UNIT_COST.standard_hours8
  is '單位標準工時8月';
comment on column FIT_PRODUCT_NO_UNIT_COST.adjust_hours8
  is '單位調整工時8月';
comment on column FIT_PRODUCT_NO_UNIT_COST.hours8
  is '單位工時8月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manual_standard_rate8
  is '單位人工標準費率8月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manual_adjust_rate8
  is '單位人工調整費率8月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manual_rate8
  is '單位人工費率8月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manual_cost8
  is '單位人工成本8月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manufacture_standard_rate8
  is '單位製造標準費率8月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manufacture_adjust_rate8
  is '單位製造調整費率8月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manufacture_rate8
  is '單位製造費率8月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manufacture_cost8
  is '單位製造成本8月';
comment on column FIT_PRODUCT_NO_UNIT_COST.unit_cost8
  is '單位成本8月';
comment on column FIT_PRODUCT_NO_UNIT_COST.material_standard_cost9
  is '單位材料標準成本9月';
comment on column FIT_PRODUCT_NO_UNIT_COST.material_adjust_cost9
  is '單位材料調整成本9月';
comment on column FIT_PRODUCT_NO_UNIT_COST.material_cost9
  is '單位材料成本9月';
comment on column FIT_PRODUCT_NO_UNIT_COST.standard_hours9
  is '單位標準工時9月';
comment on column FIT_PRODUCT_NO_UNIT_COST.adjust_hours9
  is '單位調整工時9月';
comment on column FIT_PRODUCT_NO_UNIT_COST.hours9
  is '單位工時9月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manual_standard_rate9
  is '單位人工標準費率9月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manual_adjust_rate9
  is '單位人工調整費率9月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manual_rate9
  is '單位人工費率9月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manual_cost9
  is '單位人工成本9月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manufacture_standard_rate9
  is '單位製造標準費率9月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manufacture_adjust_rate9
  is '單位製造調整費率9月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manufacture_rate9
  is '單位製造費率9月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manufacture_cost9
  is '單位製造成本9月';
comment on column FIT_PRODUCT_NO_UNIT_COST.unit_cost9
  is '單位成本9月';
comment on column FIT_PRODUCT_NO_UNIT_COST.material_standard_cost10
  is '單位材料標準成本10月';
comment on column FIT_PRODUCT_NO_UNIT_COST.material_adjust_cost10
  is '單位材料調整成本10月';
comment on column FIT_PRODUCT_NO_UNIT_COST.material_cost10
  is '單位材料成本10月';
comment on column FIT_PRODUCT_NO_UNIT_COST.standard_hours10
  is '單位標準工時10月';
comment on column FIT_PRODUCT_NO_UNIT_COST.adjust_hours10
  is '單位調整工時10月';
comment on column FIT_PRODUCT_NO_UNIT_COST.hours10
  is '單位工時10月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manual_standard_rate10
  is '單位人工標準費率10月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manual_adjust_rate10
  is '單位人工調整費率10月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manual_rate10
  is '單位人工費率10月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manual_cost10
  is '單位人工成本10月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manufacture_standard_rate10
  is '單位製造標準費率10月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manufacture_adjust_rate10
  is '單位製造調整費率10月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manufacture_rate10
  is '單位製造費率10月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manufacture_cost10
  is '單位製造成本10月';
comment on column FIT_PRODUCT_NO_UNIT_COST.unit_cost10
  is '單位成本10月';
comment on column FIT_PRODUCT_NO_UNIT_COST.material_standard_cost11
  is '單位材料標準成本11月';
comment on column FIT_PRODUCT_NO_UNIT_COST.material_adjust_cost11
  is '單位材料調整成本11月';
comment on column FIT_PRODUCT_NO_UNIT_COST.material_cost11
  is '單位材料成本11月';
comment on column FIT_PRODUCT_NO_UNIT_COST.standard_hours11
  is '單位標準工時11月';
comment on column FIT_PRODUCT_NO_UNIT_COST.adjust_hours11
  is '單位調整工時11月';
comment on column FIT_PRODUCT_NO_UNIT_COST.hours11
  is '單位工時11月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manual_standard_rate11
  is '單位人工標準費率11月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manual_adjust_rate11
  is '單位人工調整費率11月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manual_rate11
  is '單位人工費率11月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manual_cost11
  is '單位人工成本11月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manufacture_standard_rate11
  is '單位製造標準費率11月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manufacture_adjust_rate11
  is '單位製造調整費率11月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manufacture_rate11
  is '單位製造費率11月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manufacture_cost11
  is '單位製造成本11月';
comment on column FIT_PRODUCT_NO_UNIT_COST.unit_cost11
  is '單位成本11月';
comment on column FIT_PRODUCT_NO_UNIT_COST.material_standard_cost12
  is '單位材料標準成本12月';
comment on column FIT_PRODUCT_NO_UNIT_COST.material_adjust_cost12
  is '單位材料調整成本12月';
comment on column FIT_PRODUCT_NO_UNIT_COST.material_cost12
  is '單位材料成本12月';
comment on column FIT_PRODUCT_NO_UNIT_COST.standard_hours12
  is '單位標準工時12月';
comment on column FIT_PRODUCT_NO_UNIT_COST.adjust_hours12
  is '單位調整工時12月';
comment on column FIT_PRODUCT_NO_UNIT_COST.hours12
  is '單位工時12月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manual_standard_rate12
  is '單位人工標準費率12月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manual_adjust_rate12
  is '單位人工調整費率12月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manual_rate12
  is '單位人工費率12月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manual_cost12
  is '單位人工成本12月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manufacture_standard_rate12
  is '單位製造標準費率12月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manufacture_adjust_rate12
  is '單位製造調整費率12月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manufacture_rate12
  is '單位製造費率12月';
comment on column FIT_PRODUCT_NO_UNIT_COST.manufacture_cost12
  is '單位製造成本12月';
comment on column FIT_PRODUCT_NO_UNIT_COST.unit_cost12
  is '單位成本12月';
comment on column FIT_PRODUCT_NO_UNIT_COST.entity
  is 'SBU_法人';
comment on column FIT_PRODUCT_NO_UNIT_COST.year
  is '年';
comment on column FIT_PRODUCT_NO_UNIT_COST.scenarios
  is '场景';
alter table FIT_PRODUCT_NO_UNIT_COST
  add constraint PK_PRODUCT_NO_UNIT_COST_ID primary key (ID);
alter table FIT_PRODUCT_NO_UNIT_COST
  add constraint UN_FIT_PRODUCT_NO_UNIT_COST unique (PRODUCT, YEAR, SCENARIOS, SBU, ENTITY);

prompt
prompt Creating table FIT_REGION
prompt =========================
prompt
create table FIT_REGION
(
  id           VARCHAR2(50) not null,
  code         VARCHAR2(100),
  department   VARCHAR2(100),
  region       VARCHAR2(100),
  judge_region VARCHAR2(100)
)
;
comment on table FIT_REGION
  is '区域映射';
comment on column FIT_REGION.code
  is '業務代碼';
comment on column FIT_REGION.department
  is '業務部門';
comment on column FIT_REGION.region
  is '大區域';
comment on column FIT_REGION.judge_region
  is '營收判定區域';
alter table FIT_REGION
  add constraint PK_FIT_REGION_ID primary key (ID);

prompt
prompt Creating table FIT_REVENUE_ACTUAL_NUMBER
prompt ========================================
prompt
create table FIT_REVENUE_ACTUAL_NUMBER
(
  id                   VARCHAR2(50) not null,
  year                 VARCHAR2(100),
  period               VARCHAR2(100),
  factory              VARCHAR2(100),
  bu                   VARCHAR2(100),
  sbu                  VARCHAR2(100),
  announcement_revenue VARCHAR2(100),
  ntd                  VARCHAR2(100) default 'NTD' not null
)
;
comment on table FIT_REVENUE_ACTUAL_NUMBER
  is '廠區營收收集';
comment on column FIT_REVENUE_ACTUAL_NUMBER.year
  is '年';
comment on column FIT_REVENUE_ACTUAL_NUMBER.period
  is '月';
comment on column FIT_REVENUE_ACTUAL_NUMBER.factory
  is '廠區';
comment on column FIT_REVENUE_ACTUAL_NUMBER.bu
  is 'BU';
comment on column FIT_REVENUE_ACTUAL_NUMBER.sbu
  is 'SBU';
comment on column FIT_REVENUE_ACTUAL_NUMBER.announcement_revenue
  is '廠區營收';
comment on column FIT_REVENUE_ACTUAL_NUMBER.ntd
  is 'NTD';
alter table FIT_REVENUE_ACTUAL_NUMBER
  add constraint PK_FIT_REVENUE_ACTUAL_NO_ID primary key (ID);

prompt
prompt Creating table FIT_REVENUE_DETAIL_ACTUAL
prompt ========================================
prompt
create table FIT_REVENUE_DETAIL_ACTUAL
(
  id                    VARCHAR2(50) not null,
  corporation_code      VARCHAR2(50) not null,
  year                  VARCHAR2(4) not null,
  period                VARCHAR2(2) not null,
  customer_code         VARCHAR2(100),
  customer_name         VARCHAR2(100),
  department            VARCHAR2(100),
  category              VARCHAR2(100),
  invoice_item          VARCHAR2(100),
  invoice_no            VARCHAR2(100),
  invoice_date          VARCHAR2(100),
  invoice_sign_date     VARCHAR2(100),
  sale_item             VARCHAR2(100),
  sale_no               VARCHAR2(100),
  sale_date             VARCHAR2(100),
  store_no              VARCHAR2(100),
  product_no            VARCHAR2(100),
  customer_product_no   VARCHAR2(100),
  quantity              VARCHAR2(100),
  price                 VARCHAR2(100),
  currency              VARCHAR2(100),
  rate                  VARCHAR2(100),
  source_untax_amount   VARCHAR2(100),
  currency_untax_amount VARCHAR2(100),
  supplier_code         VARCHAR2(100),
  supplier_name         VARCHAR2(100),
  production_unit       VARCHAR2(100),
  cd                    VARCHAR2(100),
  version               VARCHAR2(20) not null,
  invoice_number        VARCHAR2(100)
)
;
comment on table FIT_REVENUE_DETAIL_ACTUAL
  is '營收明細實際數';
comment on column FIT_REVENUE_DETAIL_ACTUAL.corporation_code
  is '管報法人編碼';
comment on column FIT_REVENUE_DETAIL_ACTUAL.year
  is '年';
comment on column FIT_REVENUE_DETAIL_ACTUAL.period
  is '期間';
comment on column FIT_REVENUE_DETAIL_ACTUAL.customer_code
  is '客戶代碼';
comment on column FIT_REVENUE_DETAIL_ACTUAL.customer_name
  is '客戶名稱';
comment on column FIT_REVENUE_DETAIL_ACTUAL.department
  is '業務部門';
comment on column FIT_REVENUE_DETAIL_ACTUAL.category
  is '類別';
comment on column FIT_REVENUE_DETAIL_ACTUAL.invoice_item
  is '發票項次';
comment on column FIT_REVENUE_DETAIL_ACTUAL.invoice_no
  is '發票號碼';
comment on column FIT_REVENUE_DETAIL_ACTUAL.invoice_date
  is '發票日期';
comment on column FIT_REVENUE_DETAIL_ACTUAL.invoice_sign_date
  is '發票立賬日期';
comment on column FIT_REVENUE_DETAIL_ACTUAL.sale_item
  is '銷單項次';
comment on column FIT_REVENUE_DETAIL_ACTUAL.sale_no
  is '銷單號碼';
comment on column FIT_REVENUE_DETAIL_ACTUAL.sale_date
  is '銷貨日期';
comment on column FIT_REVENUE_DETAIL_ACTUAL.store_no
  is '倉碼';
comment on column FIT_REVENUE_DETAIL_ACTUAL.product_no
  is '產品品號';
comment on column FIT_REVENUE_DETAIL_ACTUAL.customer_product_no
  is '客戶產品品號';
comment on column FIT_REVENUE_DETAIL_ACTUAL.quantity
  is '數量';
comment on column FIT_REVENUE_DETAIL_ACTUAL.price
  is '單價';
comment on column FIT_REVENUE_DETAIL_ACTUAL.currency
  is '幣別';
comment on column FIT_REVENUE_DETAIL_ACTUAL.rate
  is '匯率';
comment on column FIT_REVENUE_DETAIL_ACTUAL.source_untax_amount
  is '未稅金額(原幣)';
comment on column FIT_REVENUE_DETAIL_ACTUAL.currency_untax_amount
  is '未稅金額(本幣)';
comment on column FIT_REVENUE_DETAIL_ACTUAL.supplier_code
  is '送貨客戶代碼';
comment on column FIT_REVENUE_DETAIL_ACTUAL.supplier_name
  is '送貨客戶名稱';
comment on column FIT_REVENUE_DETAIL_ACTUAL.production_unit
  is '生產單位';
comment on column FIT_REVENUE_DETAIL_ACTUAL.cd
  is 'CD';
comment on column FIT_REVENUE_DETAIL_ACTUAL.version
  is 'V1-财报 V2-管报';
comment on column FIT_REVENUE_DETAIL_ACTUAL.invoice_number
  is '發票NO.';
alter table FIT_REVENUE_DETAIL_ACTUAL
  add constraint PK_FIT_REVENUE_DTL_ACTUAL_ID primary key (ID);

prompt
prompt Creating table FIT_REVENUE_DETAIL_MANUAL
prompt ========================================
prompt
create table FIT_REVENUE_DETAIL_MANUAL
(
  id                    VARCHAR2(50) not null,
  serial_number         NUMBER(10),
  year                  VARCHAR2(100),
  quarter               VARCHAR2(100),
  period                VARCHAR2(100),
  corporation_code      VARCHAR2(100),
  customer_code         VARCHAR2(100),
  customer_name         VARCHAR2(100),
  department            VARCHAR2(100),
  category              VARCHAR2(100),
  invoice_item          VARCHAR2(100),
  invoice_no            VARCHAR2(100),
  invoice_date          VARCHAR2(100),
  invoice_sign_date     VARCHAR2(100),
  sale_item             VARCHAR2(100),
  sale_no               VARCHAR2(100),
  sale_date             VARCHAR2(100),
  store_no              VARCHAR2(100),
  sbu                   VARCHAR2(100),
  product_no            VARCHAR2(100),
  customer_product_no   VARCHAR2(100),
  quantity              VARCHAR2(100),
  price                 VARCHAR2(100),
  currency              VARCHAR2(100),
  rate                  VARCHAR2(100),
  source_untax_amount   VARCHAR2(100),
  currency_untax_amount VARCHAR2(100),
  currenty_rate         VARCHAR2(100),
  month_revenue_amount  VARCHAR2(100),
  month_rate            VARCHAR2(100),
  month_revenue_rate    VARCHAR2(100),
  supplier_code         VARCHAR2(100),
  supplier_name         VARCHAR2(100),
  production_unit       VARCHAR2(100),
  cd                    VARCHAR2(100),
  sale_category         VARCHAR2(100),
  customer_info         VARCHAR2(100),
  segment               VARCHAR2(100),
  leading_industry1     VARCHAR2(100),
  leading_industry2     VARCHAR2(100),
  leading_industry3     VARCHAR2(100),
  leading_industry4     VARCHAR2(100),
  leading_industry5     VARCHAR2(100),
  secondary_industry    VARCHAR2(100),
  is_unique             VARCHAR2(100),
  simple_specification  VARCHAR2(100),
  full_specification    VARCHAR2(100),
  group_specification   VARCHAR2(100),
  grade                 VARCHAR2(100),
  area                  VARCHAR2(100),
  channel               VARCHAR2(100),
  bcg                   VARCHAR2(100),
  strategy              VARCHAR2(100),
  version               VARCHAR2(20) not null,
  legal_code            VARCHAR2(100),
  local_currency        VARCHAR2(100),
  invoice_number        VARCHAR2(100)
)
;
comment on table FIT_REVENUE_DETAIL_MANUAL
  is '營收明細手工處理';
comment on column FIT_REVENUE_DETAIL_MANUAL.serial_number
  is '序號';
comment on column FIT_REVENUE_DETAIL_MANUAL.year
  is '立賬年度';
comment on column FIT_REVENUE_DETAIL_MANUAL.quarter
  is '立賬季度';
comment on column FIT_REVENUE_DETAIL_MANUAL.period
  is '立賬月份';
comment on column FIT_REVENUE_DETAIL_MANUAL.corporation_code
  is '法人簡稱';
comment on column FIT_REVENUE_DETAIL_MANUAL.customer_code
  is '客戶代碼';
comment on column FIT_REVENUE_DETAIL_MANUAL.customer_name
  is '客戶名稱';
comment on column FIT_REVENUE_DETAIL_MANUAL.department
  is '業務部門';
comment on column FIT_REVENUE_DETAIL_MANUAL.category
  is '類別';
comment on column FIT_REVENUE_DETAIL_MANUAL.invoice_item
  is '發票項次';
comment on column FIT_REVENUE_DETAIL_MANUAL.invoice_no
  is '發票號碼';
comment on column FIT_REVENUE_DETAIL_MANUAL.invoice_date
  is '發票日期';
comment on column FIT_REVENUE_DETAIL_MANUAL.invoice_sign_date
  is '發票立賬日期';
comment on column FIT_REVENUE_DETAIL_MANUAL.sale_item
  is '銷單項次';
comment on column FIT_REVENUE_DETAIL_MANUAL.sale_no
  is '銷單號碼';
comment on column FIT_REVENUE_DETAIL_MANUAL.sale_date
  is '銷貨日期';
comment on column FIT_REVENUE_DETAIL_MANUAL.store_no
  is '倉碼';
comment on column FIT_REVENUE_DETAIL_MANUAL.sbu
  is 'SBU';
comment on column FIT_REVENUE_DETAIL_MANUAL.product_no
  is '產品品號';
comment on column FIT_REVENUE_DETAIL_MANUAL.customer_product_no
  is '客戶產品品號';
comment on column FIT_REVENUE_DETAIL_MANUAL.quantity
  is '數量';
comment on column FIT_REVENUE_DETAIL_MANUAL.price
  is '單價';
comment on column FIT_REVENUE_DETAIL_MANUAL.currency
  is '幣別';
comment on column FIT_REVENUE_DETAIL_MANUAL.rate
  is '匯率';
comment on column FIT_REVENUE_DETAIL_MANUAL.source_untax_amount
  is '未稅金額(原幣)';
comment on column FIT_REVENUE_DETAIL_MANUAL.currency_untax_amount
  is '未稅金額(本幣)';
comment on column FIT_REVENUE_DETAIL_MANUAL.currenty_rate
  is '財報匯率(USD)';
comment on column FIT_REVENUE_DETAIL_MANUAL.month_revenue_amount
  is '營收未稅金額(USD)';
comment on column FIT_REVENUE_DETAIL_MANUAL.month_rate
  is '財報匯率(NTD)';
comment on column FIT_REVENUE_DETAIL_MANUAL.month_revenue_rate
  is '營收未稅金額(NTD)';
comment on column FIT_REVENUE_DETAIL_MANUAL.supplier_code
  is '送貨客戶代碼';
comment on column FIT_REVENUE_DETAIL_MANUAL.supplier_name
  is '送貨客戶名稱';
comment on column FIT_REVENUE_DETAIL_MANUAL.production_unit
  is '生產單位';
comment on column FIT_REVENUE_DETAIL_MANUAL.cd
  is 'CD';
comment on column FIT_REVENUE_DETAIL_MANUAL.sale_category
  is '銷售大類';
comment on column FIT_REVENUE_DETAIL_MANUAL.customer_info
  is '對照檔（產品料號+客戶料號+客戶代碼）';
comment on column FIT_REVENUE_DETAIL_MANUAL.segment
  is '部門別';
comment on column FIT_REVENUE_DETAIL_MANUAL.leading_industry1
  is '主產業1';
comment on column FIT_REVENUE_DETAIL_MANUAL.leading_industry2
  is '主產業2';
comment on column FIT_REVENUE_DETAIL_MANUAL.leading_industry3
  is '主產業3';
comment on column FIT_REVENUE_DETAIL_MANUAL.leading_industry4
  is '主產業4';
comment on column FIT_REVENUE_DETAIL_MANUAL.leading_industry5
  is '主產業5';
comment on column FIT_REVENUE_DETAIL_MANUAL.secondary_industry
  is '次產業';
comment on column FIT_REVENUE_DETAIL_MANUAL.is_unique
  is '主產業是否唯一';
comment on column FIT_REVENUE_DETAIL_MANUAL.simple_specification
  is '客戶簡稱規範';
comment on column FIT_REVENUE_DETAIL_MANUAL.full_specification
  is '客戶全稱規範';
comment on column FIT_REVENUE_DETAIL_MANUAL.group_specification
  is '客戶集團規範';
comment on column FIT_REVENUE_DETAIL_MANUAL.grade
  is '客戶分級分類';
comment on column FIT_REVENUE_DETAIL_MANUAL.area
  is '區域';
comment on column FIT_REVENUE_DETAIL_MANUAL.channel
  is '渠道';
comment on column FIT_REVENUE_DETAIL_MANUAL.bcg
  is '產品BCG';
comment on column FIT_REVENUE_DETAIL_MANUAL.strategy
  is '策略';
comment on column FIT_REVENUE_DETAIL_MANUAL.version
  is 'V1-财报 V2-管报';
comment on column FIT_REVENUE_DETAIL_MANUAL.legal_code
  is '管報法人編碼';
comment on column FIT_REVENUE_DETAIL_MANUAL.local_currency
  is '本位幣';
comment on column FIT_REVENUE_DETAIL_MANUAL.invoice_number
  is '發票NO.';
alter table FIT_REVENUE_DETAIL_MANUAL
  add constraint PK_FIT_REVENUE_DTL_MANUAL_ID primary key (ID);

prompt
prompt Creating table FIT_REVENUE_DETAIL_MANUAL_TEMP
prompt =============================================
prompt
create table FIT_REVENUE_DETAIL_MANUAL_TEMP
(
  id                    VARCHAR2(50) not null,
  serial_number         NUMBER(10),
  year                  VARCHAR2(100),
  quarter               VARCHAR2(100),
  period                VARCHAR2(100),
  corporation_code      VARCHAR2(100),
  customer_code         VARCHAR2(100),
  customer_name         VARCHAR2(100),
  department            VARCHAR2(100),
  category              VARCHAR2(100),
  invoice_item          VARCHAR2(100),
  invoice_no            VARCHAR2(100),
  invoice_date          VARCHAR2(100),
  invoice_sign_date     VARCHAR2(100),
  sale_item             VARCHAR2(100),
  sale_no               VARCHAR2(100),
  sale_date             VARCHAR2(100),
  store_no              VARCHAR2(100),
  sbu                   VARCHAR2(100),
  product_no            VARCHAR2(100),
  customer_product_no   VARCHAR2(100),
  quantity              VARCHAR2(100),
  price                 VARCHAR2(100),
  currency              VARCHAR2(100),
  rate                  VARCHAR2(100),
  source_untax_amount   VARCHAR2(100),
  currency_untax_amount VARCHAR2(100),
  currenty_rate         VARCHAR2(100),
  month_revenue_amount  VARCHAR2(100),
  month_rate            VARCHAR2(100),
  month_revenue_rate    VARCHAR2(100),
  supplier_code         VARCHAR2(100),
  supplier_name         VARCHAR2(100),
  production_unit       VARCHAR2(100),
  cd                    VARCHAR2(100),
  sale_category         VARCHAR2(100),
  customer_info         VARCHAR2(100),
  segment               VARCHAR2(100),
  leading_industry1     VARCHAR2(100),
  leading_industry2     VARCHAR2(100),
  leading_industry3     VARCHAR2(100),
  leading_industry4     VARCHAR2(100),
  leading_industry5     VARCHAR2(100),
  secondary_industry    VARCHAR2(100),
  is_unique             VARCHAR2(100),
  simple_specification  VARCHAR2(100),
  full_specification    VARCHAR2(100),
  group_specification   VARCHAR2(100),
  grade                 VARCHAR2(100),
  area                  VARCHAR2(100),
  channel               VARCHAR2(100),
  bcg                   VARCHAR2(100),
  strategy              VARCHAR2(100),
  version               VARCHAR2(20) not null,
  legal_code            VARCHAR2(100),
  local_currency        VARCHAR2(100),
  invoice_number        VARCHAR2(100)
)
;
comment on table FIT_REVENUE_DETAIL_MANUAL_TEMP
  is '營收明細手工處理';
comment on column FIT_REVENUE_DETAIL_MANUAL_TEMP.serial_number
  is '序號';
comment on column FIT_REVENUE_DETAIL_MANUAL_TEMP.year
  is '立賬年度';
comment on column FIT_REVENUE_DETAIL_MANUAL_TEMP.quarter
  is '立賬季度';
comment on column FIT_REVENUE_DETAIL_MANUAL_TEMP.period
  is '立賬月份';
comment on column FIT_REVENUE_DETAIL_MANUAL_TEMP.corporation_code
  is '法人簡稱';
comment on column FIT_REVENUE_DETAIL_MANUAL_TEMP.customer_code
  is '客戶代碼';
comment on column FIT_REVENUE_DETAIL_MANUAL_TEMP.customer_name
  is '客戶名稱';
comment on column FIT_REVENUE_DETAIL_MANUAL_TEMP.department
  is '業務部門';
comment on column FIT_REVENUE_DETAIL_MANUAL_TEMP.category
  is '類別';
comment on column FIT_REVENUE_DETAIL_MANUAL_TEMP.invoice_item
  is '發票項次';
comment on column FIT_REVENUE_DETAIL_MANUAL_TEMP.invoice_no
  is '發票號碼';
comment on column FIT_REVENUE_DETAIL_MANUAL_TEMP.invoice_date
  is '發票日期';
comment on column FIT_REVENUE_DETAIL_MANUAL_TEMP.invoice_sign_date
  is '發票立賬日期';
comment on column FIT_REVENUE_DETAIL_MANUAL_TEMP.sale_item
  is '銷單項次';
comment on column FIT_REVENUE_DETAIL_MANUAL_TEMP.sale_no
  is '銷單號碼';
comment on column FIT_REVENUE_DETAIL_MANUAL_TEMP.sale_date
  is '銷貨日期';
comment on column FIT_REVENUE_DETAIL_MANUAL_TEMP.store_no
  is '倉碼';
comment on column FIT_REVENUE_DETAIL_MANUAL_TEMP.sbu
  is 'SBU';
comment on column FIT_REVENUE_DETAIL_MANUAL_TEMP.product_no
  is '產品品號';
comment on column FIT_REVENUE_DETAIL_MANUAL_TEMP.customer_product_no
  is '客戶產品品號';
comment on column FIT_REVENUE_DETAIL_MANUAL_TEMP.quantity
  is '數量';
comment on column FIT_REVENUE_DETAIL_MANUAL_TEMP.price
  is '單價';
comment on column FIT_REVENUE_DETAIL_MANUAL_TEMP.currency
  is '幣別';
comment on column FIT_REVENUE_DETAIL_MANUAL_TEMP.rate
  is '匯率';
comment on column FIT_REVENUE_DETAIL_MANUAL_TEMP.source_untax_amount
  is '未稅金額(原幣)';
comment on column FIT_REVENUE_DETAIL_MANUAL_TEMP.currency_untax_amount
  is '未稅金額(本幣)';
comment on column FIT_REVENUE_DETAIL_MANUAL_TEMP.currenty_rate
  is '財報匯率(USD)';
comment on column FIT_REVENUE_DETAIL_MANUAL_TEMP.month_revenue_amount
  is '營收未稅金額(USD)';
comment on column FIT_REVENUE_DETAIL_MANUAL_TEMP.month_rate
  is '財報匯率(NTD)';
comment on column FIT_REVENUE_DETAIL_MANUAL_TEMP.month_revenue_rate
  is '營收未稅金額(NTD)';
comment on column FIT_REVENUE_DETAIL_MANUAL_TEMP.supplier_code
  is '送貨客戶代碼';
comment on column FIT_REVENUE_DETAIL_MANUAL_TEMP.supplier_name
  is '送貨客戶名稱';
comment on column FIT_REVENUE_DETAIL_MANUAL_TEMP.production_unit
  is '生產單位';
comment on column FIT_REVENUE_DETAIL_MANUAL_TEMP.cd
  is 'CD';
comment on column FIT_REVENUE_DETAIL_MANUAL_TEMP.sale_category
  is '銷售大類';
comment on column FIT_REVENUE_DETAIL_MANUAL_TEMP.customer_info
  is '對照檔（產品料號+客戶料號+客戶代碼）';
comment on column FIT_REVENUE_DETAIL_MANUAL_TEMP.segment
  is '部門別';
comment on column FIT_REVENUE_DETAIL_MANUAL_TEMP.leading_industry1
  is '主產業1';
comment on column FIT_REVENUE_DETAIL_MANUAL_TEMP.leading_industry2
  is '主產業2';
comment on column FIT_REVENUE_DETAIL_MANUAL_TEMP.leading_industry3
  is '主產業3';
comment on column FIT_REVENUE_DETAIL_MANUAL_TEMP.leading_industry4
  is '主產業4';
comment on column FIT_REVENUE_DETAIL_MANUAL_TEMP.leading_industry5
  is '主產業5';
comment on column FIT_REVENUE_DETAIL_MANUAL_TEMP.secondary_industry
  is '次產業';
comment on column FIT_REVENUE_DETAIL_MANUAL_TEMP.is_unique
  is '主產業是否唯一';
comment on column FIT_REVENUE_DETAIL_MANUAL_TEMP.simple_specification
  is '客戶簡稱規範';
comment on column FIT_REVENUE_DETAIL_MANUAL_TEMP.full_specification
  is '客戶全稱規範';
comment on column FIT_REVENUE_DETAIL_MANUAL_TEMP.group_specification
  is '客戶集團規範';
comment on column FIT_REVENUE_DETAIL_MANUAL_TEMP.grade
  is '客戶分級分類';
comment on column FIT_REVENUE_DETAIL_MANUAL_TEMP.area
  is '區域';
comment on column FIT_REVENUE_DETAIL_MANUAL_TEMP.channel
  is '渠道';
comment on column FIT_REVENUE_DETAIL_MANUAL_TEMP.bcg
  is '產品BCG';
comment on column FIT_REVENUE_DETAIL_MANUAL_TEMP.strategy
  is '策略';
comment on column FIT_REVENUE_DETAIL_MANUAL_TEMP.version
  is 'V1-财报 V2-管报';
comment on column FIT_REVENUE_DETAIL_MANUAL_TEMP.legal_code
  is '管報法人編碼';
comment on column FIT_REVENUE_DETAIL_MANUAL_TEMP.local_currency
  is '本位幣';
comment on column FIT_REVENUE_DETAIL_MANUAL_TEMP.invoice_number
  is '發票NO.';
alter table FIT_REVENUE_DETAIL_MANUAL_TEMP
  add constraint PK_FIT_REVENUE_MANUAL_TEMP_ID primary key (ID);

prompt
prompt Creating table FIT_REVENUE_ESTIMATE_INDUSTRY
prompt ============================================
prompt
create table FIT_REVENUE_ESTIMATE_INDUSTRY
(
  id           VARCHAR2(50) not null,
  year         VARCHAR2(4),
  version      VARCHAR2(100),
  organization VARCHAR2(100),
  system       VARCHAR2(100),
  scene        VARCHAR2(100),
  industry     VARCHAR2(100),
  month1       VARCHAR2(100),
  month2       VARCHAR2(100),
  month3       VARCHAR2(100),
  month4       VARCHAR2(100),
  month5       VARCHAR2(100),
  month6       VARCHAR2(100),
  month7       VARCHAR2(100),
  month8       VARCHAR2(100),
  month9       VARCHAR2(100),
  month10      VARCHAR2(100),
  month11      VARCHAR2(100),
  month12      VARCHAR2(100),
  currency     VARCHAR2(10) default 'NTD'
)
;
comment on table FIT_REVENUE_ESTIMATE_INDUSTRY
  is '營收預估by產業';
comment on column FIT_REVENUE_ESTIMATE_INDUSTRY.year
  is '年';
comment on column FIT_REVENUE_ESTIMATE_INDUSTRY.version
  is '版本';
comment on column FIT_REVENUE_ESTIMATE_INDUSTRY.organization
  is '組織';
comment on column FIT_REVENUE_ESTIMATE_INDUSTRY.system
  is '體系';
comment on column FIT_REVENUE_ESTIMATE_INDUSTRY.scene
  is '情景';
comment on column FIT_REVENUE_ESTIMATE_INDUSTRY.industry
  is '主產業';
comment on column FIT_REVENUE_ESTIMATE_INDUSTRY.month1
  is '1月';
comment on column FIT_REVENUE_ESTIMATE_INDUSTRY.month2
  is '2月';
comment on column FIT_REVENUE_ESTIMATE_INDUSTRY.month3
  is '3月';
comment on column FIT_REVENUE_ESTIMATE_INDUSTRY.month4
  is '4月';
comment on column FIT_REVENUE_ESTIMATE_INDUSTRY.month5
  is '5月';
comment on column FIT_REVENUE_ESTIMATE_INDUSTRY.month6
  is '6月';
comment on column FIT_REVENUE_ESTIMATE_INDUSTRY.month7
  is '7月';
comment on column FIT_REVENUE_ESTIMATE_INDUSTRY.month8
  is '8月';
comment on column FIT_REVENUE_ESTIMATE_INDUSTRY.month9
  is '9月';
comment on column FIT_REVENUE_ESTIMATE_INDUSTRY.month10
  is '10月';
comment on column FIT_REVENUE_ESTIMATE_INDUSTRY.month11
  is '11月';
comment on column FIT_REVENUE_ESTIMATE_INDUSTRY.month12
  is '12月';
alter table FIT_REVENUE_ESTIMATE_INDUSTRY
  add constraint PK_FIT_REVENUE_ESTIMATE_ID primary key (ID);

prompt
prompt Creating table FIT_REVENUE_TARGET_INDUSTRY
prompt ==========================================
prompt
create table FIT_REVENUE_TARGET_INDUSTRY
(
  id           VARCHAR2(50) not null,
  year         VARCHAR2(4),
  version      VARCHAR2(100),
  organization VARCHAR2(100),
  system       VARCHAR2(100),
  scene        VARCHAR2(100),
  industry     VARCHAR2(100),
  month1       VARCHAR2(100),
  month2       VARCHAR2(100),
  month3       VARCHAR2(100),
  month4       VARCHAR2(100),
  month5       VARCHAR2(100),
  month6       VARCHAR2(100),
  month7       VARCHAR2(100),
  month8       VARCHAR2(100),
  month9       VARCHAR2(100),
  month10      VARCHAR2(100),
  month11      VARCHAR2(100),
  month12      VARCHAR2(100),
  currency     VARCHAR2(10) default 'NTD'
)
;
comment on table FIT_REVENUE_TARGET_INDUSTRY
  is '營收目標by產業';
comment on column FIT_REVENUE_TARGET_INDUSTRY.year
  is '年';
comment on column FIT_REVENUE_TARGET_INDUSTRY.version
  is '版本';
comment on column FIT_REVENUE_TARGET_INDUSTRY.organization
  is '組織';
comment on column FIT_REVENUE_TARGET_INDUSTRY.system
  is '體系';
comment on column FIT_REVENUE_TARGET_INDUSTRY.scene
  is '情景';
comment on column FIT_REVENUE_TARGET_INDUSTRY.industry
  is '主產業';
comment on column FIT_REVENUE_TARGET_INDUSTRY.month1
  is '1月';
comment on column FIT_REVENUE_TARGET_INDUSTRY.month2
  is '2月';
comment on column FIT_REVENUE_TARGET_INDUSTRY.month3
  is '3月';
comment on column FIT_REVENUE_TARGET_INDUSTRY.month4
  is '4月';
comment on column FIT_REVENUE_TARGET_INDUSTRY.month5
  is '5月';
comment on column FIT_REVENUE_TARGET_INDUSTRY.month6
  is '6月';
comment on column FIT_REVENUE_TARGET_INDUSTRY.month7
  is '7月';
comment on column FIT_REVENUE_TARGET_INDUSTRY.month8
  is '8月';
comment on column FIT_REVENUE_TARGET_INDUSTRY.month9
  is '9月';
comment on column FIT_REVENUE_TARGET_INDUSTRY.month10
  is '10月';
comment on column FIT_REVENUE_TARGET_INDUSTRY.month11
  is '11月';
comment on column FIT_REVENUE_TARGET_INDUSTRY.month12
  is '12月';
alter table FIT_REVENUE_TARGET_INDUSTRY
  add constraint PK_FIT_REVENUE_TARGET_ID primary key (ID);

prompt
prompt Creating table FIT_SALE_CLASSIFICATION
prompt ======================================
prompt
create table FIT_SALE_CLASSIFICATION
(
  id             VARCHAR2(50) not null,
  customer_code  VARCHAR2(100),
  customer_name  VARCHAR2(200),
  classification VARCHAR2(100)
)
;
comment on table FIT_SALE_CLASSIFICATION
  is '銷售大類映射';
comment on column FIT_SALE_CLASSIFICATION.customer_code
  is '客戶代碼';
comment on column FIT_SALE_CLASSIFICATION.customer_name
  is '客戶名稱';
comment on column FIT_SALE_CLASSIFICATION.classification
  is '銷售大類';
alter table FIT_SALE_CLASSIFICATION
  add constraint PK_FIT_SALE_CLASSIFICATION_ID primary key (ID);

prompt
prompt Creating table FIT_SBU_MAPPING
prompt ==============================
prompt
create table FIT_SBU_MAPPING
(
  id           VARCHAR2(50) not null,
  year         VARCHAR2(4),
  old_sbu_name VARCHAR2(100),
  new_sbu_name VARCHAR2(100),
  change_desc  VARCHAR2(100)
)
;
comment on table FIT_SBU_MAPPING
  is '新老SBU對照表';
comment on column FIT_SBU_MAPPING.year
  is '年';
comment on column FIT_SBU_MAPPING.old_sbu_name
  is '老SBU名稱';
comment on column FIT_SBU_MAPPING.new_sbu_name
  is '新SBU名稱';
comment on column FIT_SBU_MAPPING.change_desc
  is '變更說明';
alter table FIT_SBU_MAPPING
  add constraint PK_FIT_SBU_MAPPING_ID primary key (ID);

prompt
prompt Creating table FIT_STORE_SBU
prompt ============================
prompt
create table FIT_STORE_SBU
(
  id   VARCHAR2(50) not null,
  code VARCHAR2(100),
  sbu  VARCHAR2(100)
)
;
comment on table FIT_STORE_SBU
  is '倉碼&SBU映射';
comment on column FIT_STORE_SBU.code
  is '倉碼';
comment on column FIT_STORE_SBU.sbu
  is 'SBU';
alter table FIT_STORE_SBU
  add constraint PK_FIT_STORE_SBU_ID primary key (ID);

prompt
prompt Creating table FIT_SUPPLIER_MAPPING
prompt ===================================
prompt
create table FIT_SUPPLIER_MAPPING
(
  id               VARCHAR2(50) not null,
  corporation_code VARCHAR2(200),
  supplier_code    VARCHAR2(200),
  supplier_desc    VARCHAR2(200),
  customer_code    VARCHAR2(200),
  customer_desc    VARCHAR2(200)
)
;
comment on table FIT_SUPPLIER_MAPPING
  is '供應商映射維護';
comment on column FIT_SUPPLIER_MAPPING.corporation_code
  is 'ERP法人编码';
comment on column FIT_SUPPLIER_MAPPING.supplier_code
  is 'ERP供應商編碼';
comment on column FIT_SUPPLIER_MAPPING.supplier_desc
  is 'ERP供應商描述';
comment on column FIT_SUPPLIER_MAPPING.customer_code
  is 'HFM客戶編碼';
comment on column FIT_SUPPLIER_MAPPING.customer_desc
  is 'HFM客戶描述';
alter table FIT_SUPPLIER_MAPPING
  add constraint PK_SUPPLIER_MAPPING_ID primary key (ID);

prompt
prompt Creating table FIT_TAKEAWAY_ACTUAL_NUMBER
prompt =========================================
prompt
create table FIT_TAKEAWAY_ACTUAL_NUMBER
(
  id     VARCHAR2(50) not null,
  year   VARCHAR2(100),
  period VARCHAR2(100),
  sbu    VARCHAR2(100),
  amount VARCHAR2(100),
  ntd    VARCHAR2(100) default 'NTD' not null
)
;
comment on table FIT_TAKEAWAY_ACTUAL_NUMBER
  is '外買外賣收集';
comment on column FIT_TAKEAWAY_ACTUAL_NUMBER.year
  is '年';
comment on column FIT_TAKEAWAY_ACTUAL_NUMBER.period
  is '月';
comment on column FIT_TAKEAWAY_ACTUAL_NUMBER.sbu
  is 'SBU';
comment on column FIT_TAKEAWAY_ACTUAL_NUMBER.amount
  is '外賣外賣金額';
comment on column FIT_TAKEAWAY_ACTUAL_NUMBER.ntd
  is 'NTD';
alter table FIT_TAKEAWAY_ACTUAL_NUMBER
  add constraint PK_FIT_TAKEAWAY_ACTUAL_NO_ID primary key (ID);

prompt
prompt Creating table FIT_TEST
prompt =======================
prompt
create table FIT_TEST
(
  id               VARCHAR2(50) not null,
  corporation_code VARCHAR2(50) not null,
  year             VARCHAR2(4) not null,
  period           VARCHAR2(2) not null,
  username         VARCHAR2(100),
  language         VARCHAR2(100),
  role             VARCHAR2(100)
)
;
comment on table FIT_TEST
  is 'FIT_TEST';
comment on column FIT_TEST.corporation_code
  is '法人编码';
comment on column FIT_TEST.year
  is '';
comment on column FIT_TEST.period
  is '月';
comment on column FIT_TEST.username
  is '用户名';
comment on column FIT_TEST.language
  is '语言';
comment on column FIT_TEST.role
  is '角色';
alter table FIT_TEST
  add constraint PK_FIT_TEST_ID primary key (ID);

prompt
prompt Creating table FIT_USER
prompt =======================
prompt
create table FIT_USER
(
  id               VARCHAR2(50) not null,
  username         VARCHAR2(20) not null,
  password         VARCHAR2(100),
  corporation_code VARCHAR2(2000 CHAR),
  enable           CHAR(1) default 'T' not null,
  creator          VARCHAR2(50),
  create_time      DATE,
  updator          VARCHAR2(50),
  update_time      DATE,
  type             VARCHAR2(20) not null,
  menus            VARCHAR2(500),
  entity           VARCHAR2(2000 CHAR),
  attribute        VARCHAR2(50),
  ebs              VARCHAR2(2000 CHAR)
)
;
comment on column FIT_USER.corporation_code
  is '法人编码/SBU';
comment on column FIT_USER.enable
  is '是否启用';
comment on column FIT_USER.type
  is '用户类型';
comment on column FIT_USER.menus
  is '菜单';
comment on column FIT_USER.entity
  is 'HFM公司编码';
comment on column FIT_USER.attribute
  is 'HFM用户属性(single-单体用户,group-集团用户)';
comment on column FIT_USER.ebs
  is 'EBS';
alter table FIT_USER
  add constraint PK_FIT_USER_ID primary key (ID);
alter table FIT_USER
  add constraint UN_FIT_USER_USERNAME unique (USERNAME);

prompt
prompt Creating table FIT_USER_LOG
prompt ===========================
prompt
create table FIT_USER_LOG
(
  id            VARCHAR2(50) not null,
  method        VARCHAR2(200),
  parameter     VARCHAR2(1000 CHAR),
  status        VARCHAR2(50),
  message       VARCHAR2(500 CHAR),
  operator      VARCHAR2(50),
  operator_time TIMESTAMP(6)
)
;
comment on table FIT_USER_LOG
  is '用户操作日志';
comment on column FIT_USER_LOG.method
  is '方法';
comment on column FIT_USER_LOG.parameter
  is '参数';
comment on column FIT_USER_LOG.status
  is '状态';
comment on column FIT_USER_LOG.message
  is '消息';
comment on column FIT_USER_LOG.operator
  is '操作人';
comment on column FIT_USER_LOG.operator_time
  is '操作时间';
alter table FIT_USER_LOG
  add constraint PK_FIT_USER_LOG_ID primary key (ID);

prompt
prompt Creating table HFM_EA_EXTRACT
prompt =============================
prompt
create table HFM_EA_EXTRACT
(
  prefix     NVARCHAR2(10) not null,
  appname    NVARCHAR2(10) not null,
  task       NVARCHAR2(256),
  dimension  INTEGER not null,
  dtimestamp FLOAT not null
)
;
alter table HFM_EA_EXTRACT
  add unique (PREFIX, DIMENSION);

prompt
prompt Creating table HS_DATA_CONSOLIDATION_PRE
prompt ========================================
prompt
create table HS_DATA_CONSOLIDATION_PRE
(
  loadid        NUMBER(38),
  scenario      NVARCHAR2(255) not null,
  year          NVARCHAR2(255) not null,
  period        NVARCHAR2(255) not null,
  viewdimension NVARCHAR2(255) not null,
  value         NVARCHAR2(255) not null,
  entity        NVARCHAR2(255) not null,
  account       NVARCHAR2(255) not null,
  icp           NVARCHAR2(255) not null,
  custom1       NVARCHAR2(255) not null,
  custom2       NVARCHAR2(255) not null,
  custom3       NVARCHAR2(255) not null,
  custom4       NVARCHAR2(255) not null,
  data          FLOAT not null
)
;
comment on table HS_DATA_CONSOLIDATION_PRE
  is '- Sample table to hold the data for a Consolidation Application';
comment on column HS_DATA_CONSOLIDATION_PRE.loadid
  is '- an identifier provided by the user for filtering during import process';
comment on column HS_DATA_CONSOLIDATION_PRE.scenario
  is '- the scenario of the set of data for consolidation';
comment on column HS_DATA_CONSOLIDATION_PRE.year
  is '- the fiscal calendar year of consolidation';
comment on column HS_DATA_CONSOLIDATION_PRE.period
  is '- the time period of consolidation';
comment on column HS_DATA_CONSOLIDATION_PRE.viewdimension
  is '- the mode of calendar intelligence for consolidation';
comment on column HS_DATA_CONSOLIDATION_PRE.value
  is '- the different types of values stored in the consolidation application';
comment on column HS_DATA_CONSOLIDATION_PRE.entity
  is '- the organizational structure of the company for which consolidation';
comment on column HS_DATA_CONSOLIDATION_PRE.account
  is '- the hierarchy of natural accounts';
comment on column HS_DATA_CONSOLIDATION_PRE.icp
  is '- the intercompany balances for an existing account';
comment on column HS_DATA_CONSOLIDATION_PRE.custom1
  is '- a user specified for analysis of detailed data';
comment on column HS_DATA_CONSOLIDATION_PRE.custom2
  is '- a user specified for analysis of detailed data';
comment on column HS_DATA_CONSOLIDATION_PRE.custom3
  is '- a user specified for analysis of detailed data';
comment on column HS_DATA_CONSOLIDATION_PRE.custom4
  is '- a user specified for analysis of detailed data';
comment on column HS_DATA_CONSOLIDATION_PRE.data
  is '- the data for this intersection';

prompt
prompt Creating table I$_ORA_INC_FILE
prompt ==============================
prompt
create table I$_ORA_INC_FILE
(
  inc01      VARCHAR2(200),
  inc02      VARCHAR2(200),
  inc03      VARCHAR2(200),
  inc04      NUMBER,
  inc05      VARCHAR2(200),
  inc06      VARCHAR2(200),
  inc07      VARCHAR2(200),
  inc08      VARCHAR2(200),
  inc09      VARCHAR2(200),
  ind_update CHAR(1)
)
nologging;

prompt
prompt Creating table INTF_HFM_CGHFMEX_01
prompt ==================================
prompt
create table INTF_HFM_CGHFMEX_01
(
  d_account   VARCHAR2(150) not null,
  d_entity    VARCHAR2(150),
  d_movement  VARCHAR2(150),
  d_complex   VARCHAR2(150),
  d_project   VARCHAR2(150),
  d_elimitype VARCHAR2(150),
  d_backup1   VARCHAR2(150),
  d_backup2   VARCHAR2(150),
  d_icp       VARCHAR2(150),
  d_period    VARCHAR2(150),
  d_scenario  VARCHAR2(150),
  d_value     VARCHAR2(150),
  d_view      VARCHAR2(150),
  d_year      VARCHAR2(150),
  data        NUMBER
)
;
comment on table INTF_HFM_CGHFMEX_01
  is '??余?本位??据';
comment on column INTF_HFM_CGHFMEX_01.d_account
  is '科目';
comment on column INTF_HFM_CGHFMEX_01.d_entity
  is '?体';
comment on column INTF_HFM_CGHFMEX_01.d_movement
  is '???型';
comment on column INTF_HFM_CGHFMEX_01.d_complex
  is '?品';
comment on column INTF_HFM_CGHFMEX_01.d_project
  is '?目';
comment on column INTF_HFM_CGHFMEX_01.d_elimitype
  is '抵消?型';
comment on column INTF_HFM_CGHFMEX_01.d_backup1
  is '?用1';
comment on column INTF_HFM_CGHFMEX_01.d_backup2
  is '?用2';
comment on column INTF_HFM_CGHFMEX_01.d_icp
  is '??方';
comment on column INTF_HFM_CGHFMEX_01.d_period
  is '期?';
comment on column INTF_HFM_CGHFMEX_01.d_scenario
  is '?景';
comment on column INTF_HFM_CGHFMEX_01.d_value
  is '值';
comment on column INTF_HFM_CGHFMEX_01.d_view
  is '??';
comment on column INTF_HFM_CGHFMEX_01.d_year
  is '年';
comment on column INTF_HFM_CGHFMEX_01.data
  is '金?';

prompt
prompt Creating table J$NOT_WRITEOFF_DETAIL
prompt ====================================
prompt
create table J$NOT_WRITEOFF_DETAIL
(
  jrn_subscriber VARCHAR2(400 CHAR) not null,
  jrn_consumed   VARCHAR2(1 CHAR),
  jrn_flag       VARCHAR2(1 CHAR),
  jrn_date       DATE,
  id             VARCHAR2(50)
)
;

prompt
prompt Creating table J$NOT_WRITEOFF_DETAIL_NEW
prompt ========================================
prompt
create table J$NOT_WRITEOFF_DETAIL_NEW
(
  jrn_subscriber VARCHAR2(400 CHAR) not null,
  jrn_consumed   VARCHAR2(1 CHAR),
  jrn_flag       VARCHAR2(1 CHAR),
  jrn_date       DATE,
  id             VARCHAR2(50)
)
;

prompt
prompt Creating table LOG_TST
prompt ======================
prompt
create table LOG_TST
(
  a VARCHAR2(50),
  b VARCHAR2(50),
  c VARCHAR2(2000)
)
;

prompt
prompt Creating table NOT_WRITEOFF_DETAIL
prompt ==================================
prompt
create table NOT_WRITEOFF_DETAIL
(
  id                   VARCHAR2(50) not null,
  corporationcode      VARCHAR2(50),
  year                 VARCHAR2(50),
  period               VARCHAR2(50),
  ap_arnumber          VARCHAR2(50),
  voucher              VARCHAR2(50),
  recorddate           VARCHAR2(50),
  enddate              VARCHAR2(50),
  merchantcode         VARCHAR2(50),
  suppliercategory     VARCHAR2(50),
  itemcode             VARCHAR2(50),
  transactioncurrency  VARCHAR2(50),
  originalbalance      VARCHAR2(50),
  currentbalance       VARCHAR2(50),
  currentassessbalance VARCHAR2(50),
  overdueday           VARCHAR2(50),
  apaccount            VARCHAR2(50),
  departmentcode       VARCHAR2(50),
  summary              VARCHAR2(50)
)
;
comment on column NOT_WRITEOFF_DETAIL.corporationcode
  is '法人??';
comment on column NOT_WRITEOFF_DETAIL.year
  is '年';
comment on column NOT_WRITEOFF_DETAIL.period
  is '期?';
comment on column NOT_WRITEOFF_DETAIL.ap_arnumber
  is 'AP/AR??';
comment on column NOT_WRITEOFF_DETAIL.voucher
  is '帳款編號/傳票編號（??）';
comment on column NOT_WRITEOFF_DETAIL.recorddate
  is '入帳日期';
comment on column NOT_WRITEOFF_DETAIL.enddate
  is '到期日';
comment on column NOT_WRITEOFF_DETAIL.merchantcode
  is '客商代?';
comment on column NOT_WRITEOFF_DETAIL.suppliercategory
  is '供應商類別';
comment on column NOT_WRITEOFF_DETAIL.itemcode
  is '科目代碼';
comment on column NOT_WRITEOFF_DETAIL.transactioncurrency
  is '交易??';
comment on column NOT_WRITEOFF_DETAIL.originalbalance
  is '原幣餘額';
comment on column NOT_WRITEOFF_DETAIL.currentbalance
  is '本幣餘額';
comment on column NOT_WRITEOFF_DETAIL.currentassessbalance
  is '本幣重評餘額';
comment on column NOT_WRITEOFF_DETAIL.overdueday
  is '逾期天數';
comment on column NOT_WRITEOFF_DETAIL.apaccount
  is 'AP帳齡';
comment on column NOT_WRITEOFF_DETAIL.departmentcode
  is '部門代碼';
comment on column NOT_WRITEOFF_DETAIL.summary
  is '摘要';
alter table NOT_WRITEOFF_DETAIL
  add constraint PK_NOT_WRITEOFF_DETAIL_ID primary key (ID);

prompt
prompt Creating table NOT_WRITEOFF_DETAIL_NEW
prompt ======================================
prompt
create table NOT_WRITEOFF_DETAIL_NEW
(
  id                   VARCHAR2(50) not null,
  corporationcode      VARCHAR2(50),
  year                 VARCHAR2(50),
  period               VARCHAR2(50),
  ap_arnumber          VARCHAR2(50),
  voucher              VARCHAR2(50),
  recorddate           VARCHAR2(50),
  enddate              VARCHAR2(50),
  merchantcode         VARCHAR2(50),
  suppliercategory     VARCHAR2(50),
  itemcode             VARCHAR2(50),
  transactioncurrency  VARCHAR2(50),
  originalbalance      VARCHAR2(50),
  currentbalance       VARCHAR2(50),
  currentassessbalance VARCHAR2(50),
  overdueday           VARCHAR2(50),
  apaccount            VARCHAR2(50),
  departmentcode       VARCHAR2(50),
  summary              VARCHAR2(50)
)
;
alter table NOT_WRITEOFF_DETAIL_NEW
  add constraint TEST_KEY primary key (ID);

prompt
prompt Creating table NO_MAPPING_VALIDATE
prompt ==================================
prompt
create table NO_MAPPING_VALIDATE
(
  entity       VARCHAR2(200),
  account_code VARCHAR2(200)
)
;

prompt
prompt Creating table ORA_INC_FILE
prompt ===========================
prompt
create table ORA_INC_FILE
(
  inc01      VARCHAR2(200),
  inc02      VARCHAR2(200),
  inc03      VARCHAR2(200),
  inc04      NUMBER,
  inc05      VARCHAR2(200),
  inc06      VARCHAR2(200),
  inc07      VARCHAR2(200),
  inc08      VARCHAR2(200),
  inc09      VARCHAR2(200),
  attribute1 VARCHAR2(200),
  attribute2 VARCHAR2(200),
  attribute3 VARCHAR2(200),
  attribute4 VARCHAR2(200),
  attribute5 VARCHAR2(200)
)
;

prompt
prompt Creating table SIP_ENTITY
prompt =========================
prompt
create table SIP_ENTITY
(
  id            VARCHAR2(50) default sys_guid() not null,
  entity        VARCHAR2(50) not null,
  parent_entity VARCHAR2(50)
)
;
comment on table SIP_ENTITY
  is '法人编码表';
comment on column SIP_ENTITY.id
  is 'ID';
comment on column SIP_ENTITY.entity
  is '公司编码';
comment on column SIP_ENTITY.parent_entity
  is '父类公司编码';
alter table SIP_ENTITY
  add constraint PK_SIP_ENTITY_ID primary key (ID);
alter table SIP_ENTITY
  add constraint UN_SIP_ENTITY unique (ENTITY);

prompt
prompt Creating table SNP_CHECK_TAB
prompt ============================
prompt
create table SNP_CHECK_TAB
(
  catalog_name  VARCHAR2(100 CHAR),
  schema_name   VARCHAR2(100 CHAR),
  resource_name VARCHAR2(100 CHAR),
  full_res_name VARCHAR2(100 CHAR),
  err_type      VARCHAR2(1 CHAR),
  err_mess      VARCHAR2(250 CHAR),
  check_date    DATE,
  origin        VARCHAR2(100 CHAR),
  cons_name     VARCHAR2(128 CHAR),
  cons_type     VARCHAR2(2 CHAR),
  err_count     NUMBER(10)
)
;

prompt
prompt Creating table SNP_PLAN_TABLE
prompt =============================
prompt
create table SNP_PLAN_TABLE
(
  statement_id      VARCHAR2(30),
  plan_id           NUMBER,
  timestamp         DATE,
  remarks           VARCHAR2(4000),
  operation         VARCHAR2(30),
  options           VARCHAR2(255),
  object_node       VARCHAR2(128),
  object_owner      VARCHAR2(30),
  object_name       VARCHAR2(30),
  object_alias      VARCHAR2(65),
  object_instance   INTEGER,
  object_type       VARCHAR2(30),
  optimizer         VARCHAR2(255),
  search_columns    NUMBER,
  id                INTEGER,
  parent_id         INTEGER,
  depth             INTEGER,
  position          INTEGER,
  cost              INTEGER,
  cardinality       INTEGER,
  bytes             INTEGER,
  other_tag         VARCHAR2(255),
  partition_start   VARCHAR2(255),
  partition_stop    VARCHAR2(255),
  partition_id      INTEGER,
  other             LONG,
  distribution      VARCHAR2(30),
  cpu_cost          INTEGER,
  io_cost           INTEGER,
  temp_space        INTEGER,
  access_predicates VARCHAR2(4000),
  filter_predicates VARCHAR2(4000),
  projection        VARCHAR2(4000),
  time              INTEGER,
  qblock_name       VARCHAR2(30)
)
;

prompt
prompt Creating table SNP_SUBSCRIBERS
prompt ==============================
prompt
create table SNP_SUBSCRIBERS
(
  jrn_tname      VARCHAR2(61 CHAR) not null,
  jrn_subscriber VARCHAR2(400 CHAR) not null,
  jrn_refdate    DATE,
  jrn_row_count  NUMBER(20),
  jrn_data_cmd   VARCHAR2(2000 CHAR),
  jrn_count_cmd  VARCHAR2(2000 CHAR)
)
;
alter table SNP_SUBSCRIBERS
  add constraint PK_SNP_JRN_SBS primary key (JRN_TNAME, JRN_SUBSCRIBER);

prompt
prompt Creating table TAB1
prompt ===================
prompt
create table TAB1
(
  c1 FLOAT(62),
  c2 FLOAT(63),
  c3 FLOAT(64),
  c4 FLOAT(65),
  c5 FLOAT(66),
  c6 FLOAT(67),
  c7 FLOAT(68),
  c8 FLOAT
)
;

prompt
prompt Creating table TEST1
prompt ====================
prompt
create table TEST1
(
  id    NUMBER,
  value NUMBER
)
;

prompt
prompt Creating table TEST_YEARS
prompt =========================
prompt
create table TEST_YEARS
(
  years NUMBER,
  value NUMBER
)
;

