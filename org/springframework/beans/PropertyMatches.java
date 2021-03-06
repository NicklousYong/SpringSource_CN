/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.beans;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/**
 * Helper class for calculating property matches, according to a configurable
 * distance. Provide the list of potential matches and an easy way to generate
 * an error message. Works for both java bean properties and fields.
 * <p>
 * Mainly for use within the framework and in particular the binding facility
 *
 * <p>
 *  根据可配置的距离计算属性匹配的助手类提供潜在匹配列表和生成错误消息的简单方法适用于java bean属性和字段
 * <p>
 * 主要用于框架内,特别是绑定设施
 * 
 * 
 * @author Alef Arendsen
 * @author Arjen Poutsma
 * @author Juergen Hoeller
 * @author Stephane Nicoll
 * @since 2.0
 * @see #forProperty(String, Class)
 * @see #forField(String, Class)
 */
public abstract class PropertyMatches {

	//---------------------------------------------------------------------
	// Static section
	//---------------------------------------------------------------------

	/** Default maximum property distance: 2 */
	public static final int DEFAULT_MAX_DISTANCE = 2;


	/**
	 * Create PropertyMatches for the given bean property.
	 * <p>
	 *  为给定的bean属性创建PropertyMatches
	 * 
	 * 
	 * @param propertyName the name of the property to find possible matches for
	 * @param beanClass the bean class to search for matches
	 */
	public static PropertyMatches forProperty(String propertyName, Class<?> beanClass) {
		return forProperty(propertyName, beanClass, DEFAULT_MAX_DISTANCE);
	}

	/**
	 * Create PropertyMatches for the given bean property.
	 * <p>
	 *  为给定的bean属性创建PropertyMatches
	 * 
	 * 
	 * @param propertyName the name of the property to find possible matches for
	 * @param beanClass the bean class to search for matches
	 * @param maxDistance the maximum property distance allowed for matches
	 */
	public static PropertyMatches forProperty(String propertyName, Class<?> beanClass, int maxDistance) {
		return new BeanPropertyMatches(propertyName, beanClass, maxDistance);
	}

	/**
	 * Create PropertyMatches for the given field property.
	 * <p>
	 *  为给定的字段属性创建PropertyMatches
	 * 
	 * 
	 * @param propertyName the name of the field to find possible matches for
	 * @param beanClass the bean class to search for matches
	 */
	public static PropertyMatches forField(String propertyName, Class<?> beanClass) {
		return forField(propertyName, beanClass, DEFAULT_MAX_DISTANCE);
	}

	/**
	 * Create PropertyMatches for the given field property.
	 * <p>
	 *  为给定的字段属性创建PropertyMatches
	 * 
	 * 
	 * @param propertyName the name of the field to find possible matches for
	 * @param beanClass the bean class to search for matches
	 * @param maxDistance the maximum property distance allowed for matches
	 */
	public static PropertyMatches forField(String propertyName, Class<?> beanClass, int maxDistance) {
		return new FieldPropertyMatches(propertyName, beanClass, maxDistance);
	}


	//---------------------------------------------------------------------
	// Instance section
	//---------------------------------------------------------------------

	private final String propertyName;

	private String[] possibleMatches;


	/**
	 * Create a new PropertyMatches instance for the given property and possible matches.
	 * <p>
	 *  为给定的属性和可能的​​匹配创建一个新的PropertyMatches实例
	 * 
	 */
	private PropertyMatches(String propertyName, String[] possibleMatches) {
		this.propertyName = propertyName;
		this.possibleMatches = possibleMatches;
	}

	/**
	 * Return the name of the requested property.
	 * <p>
	 *  返回请求的属性的名称
	 * 
	 */
	public String getPropertyName() {
		return propertyName;
	}

	/**
	 * Return the calculated possible matches.
	 * <p>
	 *  返回计算的可能的匹配
	 * 
	 */
	public String[] getPossibleMatches() {
		return possibleMatches;
	}

	/**
	 * Build an error message for the given invalid property name,
	 * indicating the possible property matches.
	 * <p>
	 *  为给定的无效属性名称生成错误消息,指示可能的属性匹配
	 * 
	 */
	public abstract String buildErrorMessage();

	protected void appendHintMessage(StringBuilder msg) {
		msg.append("Did you mean ");
		for (int i = 0; i < this.possibleMatches.length; i++) {
			msg.append('\'');
			msg.append(this.possibleMatches[i]);
			if (i < this.possibleMatches.length - 2) {
				msg.append("', ");
			}
			else if (i == this.possibleMatches.length - 2) {
				msg.append("', or ");
			}
		}
		msg.append("'?");
	}

	/**
	 * Calculate the distance between the given two Strings
	 * according to the Levenshtein algorithm.
	 * <p>
	 *  根据Levenshtein算法计算给定两个字符串之间的距离
	 * 
	 * 
	 * @param s1 the first String
	 * @param s2 the second String
	 * @return the distance value
	 */
	private static int calculateStringDistance(String s1, String s2) {
		if (s1.length() == 0) {
			return s2.length();
		}
		if (s2.length() == 0) {
			return s1.length();
		}
		int d[][] = new int[s1.length() + 1][s2.length() + 1];

		for (int i = 0; i <= s1.length(); i++) {
			d[i][0] = i;
		}
		for (int j = 0; j <= s2.length(); j++) {
			d[0][j] = j;
		}

		for (int i = 1; i <= s1.length(); i++) {
			char s_i = s1.charAt(i - 1);
			for (int j = 1; j <= s2.length(); j++) {
				int cost;
				char t_j = s2.charAt(j - 1);
				if (s_i == t_j) {
					cost = 0;
				}
				else {
					cost = 1;
				}
				d[i][j] = Math.min(Math.min(d[i - 1][j] + 1, d[i][j - 1] + 1),
						d[i - 1][j - 1] + cost);
			}
		}

		return d[s1.length()][s2.length()];
	}

	private static class BeanPropertyMatches extends PropertyMatches {

		private BeanPropertyMatches(String propertyName, Class<?> beanClass, int maxDistance) {
			super(propertyName, calculateMatches(propertyName,
					BeanUtils.getPropertyDescriptors(beanClass), maxDistance));
		}

		/**
		 * Generate possible property alternatives for the given property and
		 * class. Internally uses the {@code getStringDistance} method, which
		 * in turn uses the Levenshtein algorithm to determine the distance between
		 * two Strings.
		 * <p>
		 * 为给定的属性和类生成可能的属性替代内部使用{@code getStringDistance}方法,后者又使用Levenshtein算法来确定两个字符串之间的距离
		 * 
		 * @param propertyDescriptors the JavaBeans property descriptors to search
		 * @param maxDistance the maximum distance to accept
		 */
		private static String[] calculateMatches(String propertyName, PropertyDescriptor[] propertyDescriptors, int maxDistance) {
			List<String> candidates = new ArrayList<String>();
			for (PropertyDescriptor pd : propertyDescriptors) {
				if (pd.getWriteMethod() != null) {
					String possibleAlternative = pd.getName();
					if (calculateStringDistance(propertyName, possibleAlternative) <= maxDistance) {
						candidates.add(possibleAlternative);
					}
				}
			}
			Collections.sort(candidates);
			return StringUtils.toStringArray(candidates);
		}


		@Override
		public String buildErrorMessage() {
			String propertyName = getPropertyName();
			String[] possibleMatches = getPossibleMatches();
			StringBuilder msg = new StringBuilder();
			msg.append("Bean property '");
			msg.append(propertyName);
			msg.append("' is not writable or has an invalid setter method. ");

			if (ObjectUtils.isEmpty(possibleMatches)) {
				msg.append("Does the parameter type of the setter match the return type of the getter?");
			}
			else {
				appendHintMessage(msg);
			}
			return msg.toString();
		}

	}

	private static class FieldPropertyMatches extends PropertyMatches {

		private FieldPropertyMatches(String propertyName, Class<?> beanClass, int maxDistance) {
			super(propertyName, calculateMatches(propertyName, beanClass, maxDistance));
		}

		private static String[] calculateMatches(final String propertyName, Class<?> beanClass, final int maxDistance) {
			final List<String> candidates = new ArrayList<String>();
			ReflectionUtils.doWithFields(beanClass, new ReflectionUtils.FieldCallback() {
				@Override
				public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
					String possibleAlternative = field.getName();
					if (calculateStringDistance(propertyName, possibleAlternative) <= maxDistance) {
						candidates.add(possibleAlternative);
					}
				}
			});
			Collections.sort(candidates);
			return StringUtils.toStringArray(candidates);
		}


		@Override
		public String buildErrorMessage() {
			String propertyName = getPropertyName();
			String[] possibleMatches = getPossibleMatches();
			StringBuilder msg = new StringBuilder();
			msg.append("Bean property '");
			msg.append(propertyName);
			msg.append("' has no matching field. ");

			if (!ObjectUtils.isEmpty(possibleMatches)) {
				appendHintMessage(msg);
			}
			return msg.toString();
		}

	}

}
