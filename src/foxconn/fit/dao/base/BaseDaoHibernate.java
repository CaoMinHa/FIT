package foxconn.fit.dao.base;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.impl.CriteriaImpl;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.util.Assert;
import org.springside.modules.orm.Page;
import org.springside.modules.orm.PageRequest;
import org.springside.modules.orm.PageRequest.Sort;
import org.springside.modules.utils.AssertUtils;
import org.springside.modules.utils.ReflectionUtils;

import com.google.common.collect.Lists;

import foxconn.fit.dao.base.PropertyFilter.MatchType;
import foxconn.fit.util.StringUtil;

@SuppressWarnings({"rawtypes","unchecked","incomplete-switch"})
public abstract class BaseDaoHibernate<T> extends HibernateDaoSupport {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());

	protected Class<T> entityClass;
	  
	public BaseDaoHibernate(){
		this.entityClass=ReflectionUtils.getSuperClassGenricType(getClass());
	}
	
	public List<T> find(String hSql, Object object) throws Exception{
		return find(hSql, new Object[] { object });
	}

	public List<T> find(String hSql) throws Exception{
		return (List<T>) getHibernateTemplate().find(hSql);
	}

    public List<T> loadAll() throws Exception{
		return getHibernateTemplate().loadAll(entityClass);
	}

    public void saveOrUpdate(T o) throws Exception{
		getHibernateTemplate().saveOrUpdate(o);
	}

    public Serializable save(T o) throws Exception{
    	return getHibernateTemplate().save(o);
	}

    public void update(T o) throws Exception{
    	getHibernateTemplate().update(o);
	}

	public T get(Serializable id) throws Exception{
		return getHibernateTemplate().get(entityClass, id);
    }

    public T getObject(Serializable id) throws Exception{
		return getHibernateTemplate().get(entityClass, id);
	}

    public void delete(Serializable id) throws Exception{
    	getHibernateTemplate().delete(getObject(id));
	}

    public T delete(Serializable id, T object) throws Exception{
		delete(id);
		return object;
	}

    public void delete(T object) throws Exception{
    	getHibernateTemplate().delete(object);
	}

    public void deleteAll(Collection<T> collection) throws Exception{
    	getHibernateTemplate().deleteAll(collection);
	}

    public int deleteBatchErrand(String statementName, List<T> errandList) throws Exception{
		if (errandList == null) {
			return 0;
		} else {
			deleteAll(errandList);
			return errandList.size();
		}
	}
    
	public Page findPageByHQL(PageRequest pageRequest, List<PropertyFilter> filters) throws Exception{
        List objs = Lists.newArrayList();
        StringBuffer sb = new StringBuffer();
        return findPageByHQL(sb, objs, "x", pageRequest, filters);
    }
	
	/**
	 * 按属性过滤条件列表分页查找对象.
	 */
	public Page<T> findPageByFilter(final PageRequest pageRequest, final List<PropertyFilter> filters) {
		Criterion[] criterions = buildCriterionByPropertyFilters(filters);
		return findPage(pageRequest, criterions);
	}
	
	public Page<Object[]> findPageBySql(PageRequest pageRequest,String sql){
		if (StringUtils.isEmpty(sql)) {
			return null;
		}
		
		BigDecimal bd = (BigDecimal) getSessionFactory().getCurrentSession().createSQLQuery(" select count(1) from ( "+sql+" ) t ").uniqueResult();
		List<Object[]> list = getSessionFactory().getCurrentSession().createSQLQuery(sql)
		 .setMaxResults(pageRequest.getPageSize())
		 .setFirstResult(pageRequest.getPageNo()==1?0:pageRequest.getPageSize()*(pageRequest.getPageNo()-1))
		 .list();
		Page<Object[]> page = new Page<Object[]>();
		page.setCountTotal(true);
		page.setPageNo(pageRequest.getPageNo());
		page.setPageSize(pageRequest.getPageSize());
		page.setTotalItems(bd.intValue());
		page.setResult(list);
		return page;
	}
	
	public Page findPageBySql(PageRequest pageRequest,String sql,Class entity){
		if (StringUtils.isEmpty(sql) || entity==null) {
			return null;
		}
		
		BigDecimal bd = (BigDecimal) getSessionFactory().getCurrentSession().createSQLQuery(" select count(1) from ( "+sql+" ) t ").uniqueResult();
		List list = getSessionFactory().getCurrentSession().createSQLQuery(sql)
				.addEntity(entity)
				.setMaxResults(pageRequest.getPageSize())
				.setFirstResult(pageRequest.getPageNo()==1?0:pageRequest.getPageSize()*(pageRequest.getPageNo()-1))
				.list();
		Page page = new Page();
		page.setCountTotal(true);
		page.setPageNo(pageRequest.getPageNo());
		page.setPageSize(pageRequest.getPageSize());
		page.setTotalItems(bd.intValue());
		page.setResult(list);
		return page;
	}
	
	public Page<Map> findMapPageBySql(PageRequest pageRequest,String sql){
		if (StringUtils.isEmpty(sql)) {
			return null;
		}
		
		BigDecimal bd = (BigDecimal) getSessionFactory().getCurrentSession().createSQLQuery(" select count(1) from ( "+sql+" ) t ").uniqueResult();
		List list = getSessionFactory().getCurrentSession().createSQLQuery(sql)
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
				.setMaxResults(pageRequest.getPageSize())
				.setFirstResult(pageRequest.getPageNo()==1?0:pageRequest.getPageSize()*(pageRequest.getPageNo()-1))
				.list();
		Page page = new Page();
		page.setCountTotal(true);
		page.setPageNo(pageRequest.getPageNo());
		page.setPageSize(pageRequest.getPageSize());
		page.setTotalItems(bd.intValue());
		page.setResult(list);
		return page;
	}
	
	public Page findPage(PageRequest pageRequest, Criterion criterions[]){
        AssertUtils.notNull(pageRequest, "page\u4E0D\u80FD\u4E3A\u7A7A");
        Page page = new Page(pageRequest);
        Criteria c = createCriteria(criterions);
        if(pageRequest.isCountTotal())
        {
            long totalCount = countCriteriaResult(c);
            page.setTotalItems(totalCount);
        }
        setPageRequestToCriteria(c, pageRequest);
        List result = c.list();
        page.setResult(result);
        return page;
    }
	
	protected long countCriteriaResult(Criteria c){
        CriteriaImpl impl = (CriteriaImpl)c;
        Projection projection = impl.getProjection();
        ResultTransformer transformer = impl.getResultTransformer();
        List orderEntries = null;
        try
        {
            orderEntries = (List)ReflectionUtils.getFieldValue(impl, "orderEntries");
            ReflectionUtils.setFieldValue(impl, "orderEntries", new ArrayList());
        }
        catch(Exception e)
        {
            logger.error("\u4E0D\u53EF\u80FD\u629B\u51FA\u7684\u5F02\u5E38:{}", e.getMessage());
        }
        Long totalCountObject = (Long)c.setProjection(Projections.rowCount()).uniqueResult();
        long totalCount = totalCountObject == null ? 0L : totalCountObject.longValue();
        c.setProjection(projection);
        if(projection == null)
            c.setResultTransformer(CriteriaSpecification.ROOT_ENTITY);
        if(transformer != null)
            c.setResultTransformer(transformer);
        try
        {
            ReflectionUtils.setFieldValue(impl, "orderEntries", orderEntries);
        }
        catch(Exception e)
        {
            logger.error("\u4E0D\u53EF\u80FD\u629B\u51FA\u7684\u5F02\u5E38:{}", e.getMessage());
        }
        return totalCount;
    }
	
	protected Criteria setPageRequestToCriteria(Criteria c, PageRequest pageRequest){
        AssertUtils.isTrue(pageRequest.getPageSize() > 0, "Page Size must larger than zero");
        c.setFirstResult(pageRequest.getOffset());
        c.setMaxResults(pageRequest.getPageSize());
        if(pageRequest.isOrderBySetted())
        {
            for(Iterator i$ = pageRequest.getSort().iterator(); i$.hasNext();)
            {
                org.springside.modules.orm.PageRequest.Sort sort = (org.springside.modules.orm.PageRequest.Sort)i$.next();
                if("asc".equals(sort.getDir()))
                    c.addOrder(Order.asc(sort.getProperty()));
                else
                    c.addOrder(Order.desc(sort.getProperty()));
            }

        }
        return c;
    }
	
	public List listByHQL(final List<PropertyFilter> filters,final PageRequest pageRequest) throws Exception{
        List objs = Lists.newArrayList();
        StringBuffer sb = new StringBuffer();
        String alias="x";
        genFiliter(sb, objs, alias, filters);
        if(pageRequest!=null &&pageRequest.isOrderBySetted())
        {
            sb.append(" order by ");
           
            List<Sort> sortList = pageRequest.getSort();
            for (Sort sort : sortList) {
            	sb.append(alias).append(".").append(sort.getProperty()).append(" ").append(sort.getDir()).append(",");
			}
            sb.deleteCharAt(sb.length()-1);
        }
		return createQuery(sb.toString(), objs.toArray(new Object[0])).list();
    }
	
	public List<T> listByFilter(final List<PropertyFilter> filters,final PageRequest pageRequest) {
		Criterion[] criterions = buildCriterionByPropertyFilters(filters);
		Criteria c = createCriteria(criterions);
		if(pageRequest!=null && pageRequest.isOrderBySetted())
        {
            for(Iterator i$ = pageRequest.getSort().iterator(); i$.hasNext();)
            {
                org.springside.modules.orm.PageRequest.Sort sort = (org.springside.modules.orm.PageRequest.Sort)i$.next();
                if("asc".equals(sort.getDir()))
                    c.addOrder(Order.asc(sort.getProperty()));
                else
                    c.addOrder(Order.desc(sort.getProperty()));
            }

        }
		return c.list();
	}
	
	public List listBySql(String sql) {
		if (StringUtils.isEmpty(sql)) {
			return null;
		}
		
		return getSessionFactory().getCurrentSession().createSQLQuery(sql).list();
	}
	
	public List listBySql(String sql,Class entity) {
		if (StringUtils.isEmpty(sql) || entity==null) {
			return null;
		}
		
		return getSessionFactory().getCurrentSession().createSQLQuery(sql).addEntity(entity).list();
	}
	
	public List<Map> listMapBySql(String sql) {
		if (StringUtils.isEmpty(sql)) {
			return null;
		}
		
		return getSessionFactory().getCurrentSession().createSQLQuery(sql).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	}
	
	public Criteria createCriteria(Criterion criterions[]){
        Criteria criteria = getSessionFactory().getCurrentSession().createCriteria(entityClass);
        Criterion arr$[] = criterions;
        int len$ = arr$.length;
        for(int i$ = 0; i$ < len$; i$++)
        {
            Criterion c = arr$[i$];
            criteria.add(c);
        }

        return criteria;
    }
	
	/**
	 * 按属性条件列表创建Criterion数组,辅助函数.
	 */
	public Criterion[] buildCriterionByPropertyFilters(final List<PropertyFilter> filters) {
		List<Criterion> criterionList = new ArrayList<Criterion>();
		for (PropertyFilter filter : filters) {
			if (!filter.hasMultiProperties()) { //只有一个属性需要比较的情况.
				Criterion criterion = buildCriterion(filter.getPropertyName(), filter.getMatchValue(),
						filter.getMatchType());
				criterionList.add(criterion);
			} else {//包含多个属性需要比较的情况,进行or处理.
				Disjunction disjunction = Restrictions.disjunction();
				for (String param : filter.getPropertyNames()) {
					Criterion criterion = buildCriterion(param, filter.getMatchValue(), filter.getMatchType());
					disjunction.add(criterion);
				}
				criterionList.add(disjunction);
			}
		}
		return criterionList.toArray(new Criterion[criterionList.size()]);
	}
	
	/**
	 * 按属性条件参数创建Criterion,辅助函数.
	 */
	protected Criterion buildCriterion(final String propertyName, final Object propertyValue, final MatchType matchType) {
		AssertUtils.hasText(propertyName, "propertyName不能为空");
		Criterion criterion = null;
		//根据MatchType构造criterion
		switch (matchType) {
		case EQ:
			criterion = Restrictions.eq(propertyName, propertyValue);
			break;
			
		case NULL:
			criterion = Restrictions.isNull(propertyName);
			break;
		
		case NNULL:
			criterion = Restrictions.isNotNull(propertyName);
			break;
			
		case LIKE:
			criterion = Restrictions.like(propertyName, StringUtil.escapeSQLLike((String) propertyValue), MatchMode.ANYWHERE);
			break;
			
		case LLIKE:
			criterion = Restrictions.like(propertyName, StringUtil.escapeSQLLike((String) propertyValue), MatchMode.START);
			break;
			
		case RLIKE:
			criterion = Restrictions.like(propertyName, StringUtil.escapeSQLLike((String) propertyValue), MatchMode.END);
			break;

		case LE:
			criterion = Restrictions.le(propertyName, propertyValue);
			break;
			
		case LT:
			criterion = Restrictions.lt(propertyName, propertyValue);
			break;
			
		case GE:
			criterion = Restrictions.ge(propertyName, propertyValue);
			break;
			
		case GT:
			criterion = Restrictions.gt(propertyName, propertyValue);
			break;
			
		case NE:
			criterion = Restrictions.ne(propertyName, propertyValue);
			break;
			
		case OREQ:
			if (propertyValue.getClass().isArray()) {
				Object[] objs=(Object[]) propertyValue;
				Disjunction disjunction = Restrictions.disjunction();
				for(Object val : objs){
					if(val==null || (val instanceof String && StringUtils.isBlank((String)val))){
						continue;
					}
					Criterion temp = Restrictions.eq(propertyName, val);
					disjunction.add(temp);
				}
				criterion = disjunction;
				break;
			}
			
		case ORLIKE:
			if (propertyValue instanceof String || propertyValue instanceof String[]) {
				String[] vals = propertyValue.toString().split(PropertyFilter.COMMA_SEPARATOR);
				Disjunction disjunctions = Restrictions.disjunction();
				for(String val : vals){
					if(StringUtils.isBlank(val)){
						continue;
					}
					Criterion temp = Restrictions.like(propertyName, StringUtil.escapeSQLLike(val), MatchMode.ANYWHERE);
					disjunctions.add(temp);
				}
				criterion = disjunctions;
			}
		}
		return criterion;
	}
	
	public Page findPageByHQL(StringBuffer sb, List objs, String alias, PageRequest pageRequest, List<PropertyFilter> filters){
        genFiliter(sb, objs, alias, filters);
        String defalutOrderBy = null;
        if(pageRequest.isOrderBySetted())
        {
            sb.append(" order by ");
           
            List<Sort> sortList = pageRequest.getSort();
            for (Sort sort : sortList) {
            	sb.append(alias).append(".").append(sort.getProperty()).append(" ").append(sort.getDir()).append(",");
			}
            sb.deleteCharAt(sb.length()-1);
            
            defalutOrderBy = pageRequest.getOrderBy();
        }
        Page result = findPage(pageRequest, sb.toString(), objs.toArray(new Object[0]));
        if(StringUtils.isNotBlank(defalutOrderBy))
            result.setOrderBy(defalutOrderBy);
        return result;
    }
	
	public void genFiliter(StringBuffer sb, List<Object> objs, String alias,List<PropertyFilter> filters){
		sb.append("from " + entityClass.getName() + " " + alias +" where 1=1");
		if(filters != null && filters.size() > 0){
			for(PropertyFilter f : filters){
				if ("MAKE".equals(f.getMatchType().toString())) {
					sb.append(" ");
				}else{
					sb.append(" ")
					  .append("and")
					  .append(" ")
					  .append(alias + "." +  f.getPropertyName());
				}
				switch (f.getMatchType()) {
				case EQ:
					sb.append("= ?");
					objs.add(f.getMatchValue());
					break;
				case LIKE:
					sb.append(" like ?");
					objs.add("%" + f.getMatchValue() + "%");
					break;
				case LLIKE:
					sb.append(" like ?");
					objs.add(f.getMatchValue() + "%");
					break;
				case RLIKE:
					sb.append(" like ?");
					objs.add("%" + f.getMatchValue());
					break;
				case LE:
					sb.append("<= ?");
					objs.add(f.getMatchValue());
					break;
				case LT:
					sb.append("< ?");
					objs.add(f.getMatchValue());
					break;
				case GE:
					sb.append(">= ?");
					objs.add(f.getMatchValue());
					break;
				case GT:
					sb.append("> ?");
					objs.add(f.getMatchValue());
					break;
				case NE:
					sb.append("<> ?");
					objs.add(f.getMatchValue());
					break;
				case NNULL:
					sb.append(" is not null ");
					break;
				case NULL:
					sb.append(" is null ");
					break;
				case OREQ:
					if (f.getMatchValue() instanceof String || f.getMatchValue() instanceof String[]) {
						String temp = f.getMatchValue() == null ? "" : f.getMatchValue().toString();
				        temp = StringUtils.replace(temp, ",", "','");
				        temp = "'" + temp + "'";
				        sb.append(" in (").append(temp).append(")");
						break;
					}
				case NOREQ:
					if (f.getMatchValue() instanceof String || f.getMatchValue() instanceof String[]) {
						String tempn = f.getMatchValue() == null ? "" : f.getMatchValue().toString();
				        tempn = StringUtils.replace(tempn, ",", "','");
				        tempn = "'" + tempn + "'";
				        sb.append(" not in (").append(tempn).append(")");
						break;
					}
				case MAKE:
					String tempmake = f.getMatchValue() == null ? "" : f.getMatchValue().toString();
					sb.append(tempmake);
					break;
				}
			}
		}
	}
	
	protected Page findPage(PageRequest pageRequest, String hql, Object values[]){
        Assert.notNull(pageRequest, "pageRequest不能为空");
        Page page = new Page(pageRequest);
        if(pageRequest.isCountTotal())
        {
            long totalCount = countHqlResult(hql, values);
            page.setTotalItems(totalCount);
            
            if (totalCount<=pageRequest.getPageSize()) {
				pageRequest.setPageNo(1);
			}
        }
        Query q = createQuery(hql, values);
        setPageParameterToQuery(q, pageRequest);
        List result = q.list();
        page.setResult(result);
        return page;
    }
	
    protected Query setPageParameterToQuery(Query q, PageRequest pageRequest){
        q.setFirstResult(pageRequest.getOffset());
        q.setMaxResults(pageRequest.getPageSize());
        return q;
    }
	
	protected long countHqlResult(String hql, Object values[]){
        String countHql = prepareCountHql(hql);
        try
        {
            Long count = (Long)findUnique(countHql, values);
            return count.longValue();
        }
        catch(Exception e)
        {
            throw new RuntimeException((new StringBuilder()).append("hql can't be auto count, hql is:").append(countHql).toString(), e);
        }
    }
	
	public Object findUnique(String hql, Object values[]){
        return createQuery(hql, values).uniqueResult();
    }
	
	protected Query createQuery(String queryString, Object values[]){
        Assert.hasText(queryString, "queryString不能为空");
        Query query = getSessionFactory().getCurrentSession().createQuery(queryString);
        if(values != null)
        {
            for(int i = 0; i < values.length; i++)
                query.setParameter(i, values[i]);

        }
        return query;
    }
	
    private String prepareCountHql(String orgHql){
        String countHql = (new StringBuilder()).append("select count (*) ").append(removeSelect(removeOrders(orgHql))).toString();
        return countHql;
    }
    
    private static String removeSelect(String hql){
        int beginPos = hql.toLowerCase().indexOf("from");
        return hql.substring(beginPos);
    }
    
    private static String removeOrders(String hql){
        Pattern p = Pattern.compile("order\\s*by[\\w|\\W|\\s|\\S]*", 2);
        Matcher m = p.matcher(hql);
        StringBuffer sb = new StringBuffer();
        for(; m.find(); m.appendReplacement(sb, ""));
        m.appendTail(sb);
        return sb.toString();
    }
	
	@Autowired
	protected void setMySessionFactory(HibernateTemplate hibernateTemplate){
		super.setHibernateTemplate(hibernateTemplate);
	}
	
}
