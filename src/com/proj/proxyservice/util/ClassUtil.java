package com.proj.proxyservice.util;

import java.lang.reflect.Field;

public class ClassUtil {
	private static ClassUtil classUtil = new ClassUtil();

	public static ClassUtil getClassUtil() {
		return classUtil;
	}

	private ClassUtil() {

	}
	
	@SuppressWarnings("rawtypes")
	/**
	 * 获取类下的所有申明的字段
	 * @param c
	 * @return
	 */
	public Field[] getClassFields(Class c){
		Field[] fields= c.getDeclaredFields();
		return fields;
	}
	
	/**
	 * 用反射获取字段的值
	 * @param field 字段
	 * @param t   对象
	 * @return
	 */
	public <T> Object getFieldValue(Field field,T t){
		field.setAccessible(true);
		Object resu=null;
		try {
			resu = field.get(t);
		} catch (Exception e) {
			throw new RuntimeException(t.getClass()+"类的"+field.getName()+"字段取值失败");
		} 
		return resu;
	}
}
