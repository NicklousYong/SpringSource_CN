/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2012 the original author or authors.
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

package org.springframework.jmx.export.naming;

import java.io.IOException;
import java.util.Properties;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.jmx.support.ObjectNameManager;
import org.springframework.util.CollectionUtils;

/**
 * {@code ObjectNamingStrategy} implementation that builds
 * {@code ObjectName} instances from the key used in the
 * "beans" map passed to {@code MBeanExporter}.
 *
 * <p>Can also check object name mappings, given as {@code Properties}
 * or as {@code mappingLocations} of properties files. The key used
 * to look up is the key used in {@code MBeanExporter}'s "beans" map.
 * If no mapping is found for a given key, the key itself is used to
 * build an {@code ObjectName}.
 *
 * <p>
 *  {@code ObjectNamingStrategy}实现,从"bean"映射中使用的密钥生成{@code ObjectName}实例,传递给{@code MBeanExporter}
 * 
 * <p>还可以检查对象名称映射,以{@code属性}或{@code mappingLocations}作为属性文件给出用于查找的密钥是{@code MBeanExporter}的"beans"映射中使用的
 * 密钥如果没有找到给定键的映射,该键本身用于构建一个{@code ObjectName}。
 * 
 * 
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @since 1.2
 * @see #setMappings
 * @see #setMappingLocation
 * @see #setMappingLocations
 * @see org.springframework.jmx.export.MBeanExporter#setBeans
 */
public class KeyNamingStrategy implements ObjectNamingStrategy, InitializingBean {

	/**
	 * {@code Log} instance for this class.
	 * <p>
	 *  这个类的{@code Log}实例
	 * 
	 */
	protected final Log logger = LogFactory.getLog(getClass());

	/**
	 * Stores the mappings of bean key to {@code ObjectName}.
	 * <p>
	 *  将bean密钥的映射存储到{@code ObjectName}
	 * 
	 */
	private Properties mappings;

	/**
	 * Stores the {@code Resource}s containing properties that should be loaded
	 * into the final merged set of {@code Properties} used for {@code ObjectName}
	 * resolution.
	 * <p>
	 *  存储{@code Resource}包含应该加载到{@code ObjectName}分辨率的最终合并的{@code属性}集合中的属性
	 * 
	 */
	private Resource[] mappingLocations;

	/**
	 * Stores the result of merging the {@code mappings} {@code Properties}
	 * with the properties stored in the resources defined by {@code mappingLocations}.
	 * <p>
	 *  存储将{@code映射} {@code属性}与存储在{@code mappingLocations}中定义的资源中的属性合并的结果
	 * 
	 */
	private Properties mergedMappings;


	/**
	 * Set local properties, containing object name mappings, e.g. via
	 * the "props" tag in XML bean definitions. These can be considered
	 * defaults, to be overridden by properties loaded from files.
	 * <p>
	 * 设置本地属性,包含对象名称映射,例如通过XML bean定义中的"props"标签这些可以被认为是默认值,被从文件加载的属性覆盖
	 * 
	 */
	public void setMappings(Properties mappings) {
		this.mappings = mappings;
	}

	/**
	 * Set a location of a properties file to be loaded,
	 * containing object name mappings.
	 * <p>
	 *  设置要加载的属性文件的位置,其中包含对象名称映射
	 * 
	 */
	public void setMappingLocation(Resource location) {
		this.mappingLocations = new Resource[]{location};
	}

	/**
	 * Set location of properties files to be loaded,
	 * containing object name mappings.
	 * <p>
	 *  设置要加载的属性文件的位置,包含对象名称映射
	 * 
	 */
	public void setMappingLocations(Resource[] mappingLocations) {
		this.mappingLocations = mappingLocations;
	}


	/**
	 * Merges the {@code Properties} configured in the {@code mappings} and
	 * {@code mappingLocations} into the final {@code Properties} instance
	 * used for {@code ObjectName} resolution.
	 * <p>
	 *  将{@code映射}和{@code映射关联}中配置的{@code属性}合并到用于{@code ObjectName}解析的最终{@code属性}实例中
	 * 
	 * 
	 * @throws IOException
	 */
	@Override
	public void afterPropertiesSet() throws IOException {
		this.mergedMappings = new Properties();

		CollectionUtils.mergePropertiesIntoMap(this.mappings, this.mergedMappings);

		if (this.mappingLocations != null) {
			for (int i = 0; i < this.mappingLocations.length; i++) {
				Resource location = this.mappingLocations[i];
				if (logger.isInfoEnabled()) {
					logger.info("Loading JMX object name mappings file from " + location);
				}
				PropertiesLoaderUtils.fillProperties(this.mergedMappings, location);
			}
		}
	}


	/**
	 * Attempts to retrieve the {@code ObjectName} via the given key, trying to
	 * find a mapped value in the mappings first.
	 * <p>
	 *  尝试通过给定的键检索{@code ObjectName},尝试首先在映射中找到映射的值
	 */
	@Override
	public ObjectName getObjectName(Object managedBean, String beanKey) throws MalformedObjectNameException {
		String objectName = null;
		if (this.mergedMappings != null) {
			objectName = this.mergedMappings.getProperty(beanKey);
		}
		if (objectName == null) {
			objectName = beanKey;
		}
		return ObjectNameManager.getInstance(objectName);
	}

}
