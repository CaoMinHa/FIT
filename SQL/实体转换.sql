select f.hfm_corporat_code from fit_corporat_map f
where f.erp_corporat_code='A065028'

select t.* from fit_corporat_map t,fit_corporat_map f
where t.hfm_corporat_code=f.hfm_corporat_code and f.erp_corporat_code='A065028' and t.erp_corporat_code like '%@%'


select t.hfm_corporat_code,c.target_account_code,c.attribute1 from fit_corporat_map t,fit_corporat_map f,fit_coa_mapping c
where t.hfm_corporat_code=f.hfm_corporat_code and t.erp_corporat_code like '%@%' and f.erp_corporat_code='A065028' and t.attribute1=c.source_corporate_code

INSERT INTO HS_DATA_CONSOLIDATION HDC 
select 2,
       'Actual',
       ar.year,
       'Jul',
       ' YTD',
       '<Entity Currency>',
       m.hfm_corporat_code,
       c.target_account_code,
       c_map.hfm_code,
       '[NONE]',
       '[NONE]',
       ar.currency,
       '[NONE]',
       sum(decode(c.attribute1,'Y',ar.CURRENCY_UNAMOUNT * -1,ar.CURRENCY_UNAMOUNT)) End_Balance
from FIT_COA_MAPPING c,FIT_AR_RECEIVE ar,FIT_HFM_Customer_Mapping c_map,(
     select t.erp_corporat_code,t.hfm_corporat_code from fit_corporat_map t,fit_corporat_map f where t.hfm_corporat_code=f.hfm_corporat_code and f.erp_corporat_code in ('A065028') and t.erp_corporat_code like '%@%'
) m
where c.source_corporate_code=m.erp_corporat_code and c.source_account_code=ar.item_code and ar.year=to_char(2018) and ar.period=to_char(7) and ar.corporation_code in ('A065028') and ar.customer=c_map.erp_code
group by ar.year,ar.period,m.hfm_corporat_code,c.target_account_code,c_map.hfm_code,ar.currency;

INSERT INTO HS_DATA_CONSOLIDATION HDC 
select 2,
       'Actual',
       ar.year,
       'Jul',
       ' YTD',
       '<Entity Currency>',
       m.hfm_corporat_code,
       c.target_account_code,
       c_map.hfm_code,
       '[NONE]',
       'TransCurr',
       ar.currency,
       '[NONE]',
       sum(decode(c.attribute1,'Y',ar.SRC_UNAMOUNT * -1,ar.SRC_UNAMOUNT)) End_Balance
from FIT_COA_MAPPING c,FIT_AR_RECEIVE ar,FIT_HFM_Customer_Mapping c_map,(
     select t.erp_corporat_code,t.hfm_corporat_code from fit_corporat_map t,fit_corporat_map f where t.hfm_corporat_code=f.hfm_corporat_code and f.erp_corporat_code in ('A065028') and t.erp_corporat_code like '%@%'
) m
where c.source_corporate_code=m.erp_corporat_code and c.source_account_code=ar.item_code and ar.year=to_char(2018) and ar.period=to_char(7) and ar.corporation_code in ('A065028') and ar.customer=c_map.erp_code
group by ar.year,ar.period,m.hfm_corporat_code,c.target_account_code,c_map.hfm_code,ar.currency;


select * from HS_DATA_CONSOLIDATION
delete from HS_DATA_CONSOLIDATION
 INSERT INTO HS_DATA_CONSOLIDATION values(2,'Actual','2018','7','3','6','4','6','6','4','6','6','4',6);
