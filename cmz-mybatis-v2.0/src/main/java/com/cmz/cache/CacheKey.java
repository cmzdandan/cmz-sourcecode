package com.cmz.cache;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年10月9日 下午9:34:52
 * @description 缓存Key
 */
public class CacheKey {

	// 默认哈希值
	private static final int DEFAULT_HASHCODE = 17;

	// 倍数
	private static final int DEFAULT_MULTIPLIER = 37;

	private int hashCode;

	private int count;

	private int multiplier;

	public CacheKey() {
		this.hashCode = DEFAULT_HASHCODE;
		this.count = 0;
		this.multiplier = DEFAULT_MULTIPLIER;
	}

	/**
	 * 获取哈希值
	 * 
	 * @return
	 */
	public int getCode() {
		return hashCode;
	}

	/**
	 * 计算CacheKey中的HashCode
	 * 
	 * @param object
	 */
	public void update(Object object) {
		int baseHashCode = object == null ? 1 : object.hashCode();
		count++;
		baseHashCode *= count;
		hashCode = multiplier * hashCode + baseHashCode;
	}

}
