package com.cmz.session;

import com.cmz.executor.Executor;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年10月8日 下午10:30:56
 * @description 默认的SqlSession实现
 */
public class DefaultSqlSession {
	
	private Configuration configuration;
    private Executor executor;
    
    public DefaultSqlSession(Configuration configuration) {
        this.configuration = configuration;
        // 根据全局配置决定是否使用缓存装饰
        this.executor = configuration.newExecutor();
    }
    
    public Configuration getConfiguration() {
        return configuration;
    }
    
    public <T> T getMapper(Class<T> clazz){
        return configuration.getMapper(clazz, this);
    }
    
    @SuppressWarnings("unchecked")
	public <T> T selectOne(String statement, Object[] parameter, Class<?> pojo)  {
        String sql = getConfiguration().getMappedStatement(statement);
        // 打印代理对象时会自动调用toString()方法，触发invoke()
        return (T) executor.query(sql, parameter, pojo);
    }

}
