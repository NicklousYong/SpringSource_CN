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

import java.util.Hashtable;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jmx.export.metadata.JmxAttributeSource;
import org.springframework.jmx.export.metadata.ManagedResource;
import org.springframework.jmx.support.ObjectNameManager;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/**
 * An implementation of the {@link ObjectNamingStrategy} interface
 * that reads the {@code ObjectName} from the source-level metadata.
 * Falls back to the bean key (bean name) if no {@code ObjectName}
 * can be found in source-level metadata.
 *
 * <p>Uses the {@link JmxAttributeSource} strategy interface, so that
 * metadata can be read using any supported implementation. Out of the box,
 * {@link org.springframework.jmx.export.annotation.AnnotationJmxAttributeSource}
 * introspects a well-defined set of Java 5 annotations that come with Spring.
 *
 * <p>
 * 从源级元数据中读取{@code ObjectName}的{@link ObjectNamingStrategy}接口的实现如果在源级元数据中找不到{@code ObjectName},则返回到bean键
 * (bean名称)。
 * 
 *  <p>使用{@link JmxAttributeSource}策略界面,以便使用任何支持的实现即可读取元数据,{@link orgspringframeworkjmxexportannotationAnnotationJmxAttributeSource}
 * 介绍了Spring中附带的一组定义良好的Java 5注释。
 * 
 * 
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @since 1.2
 * @see ObjectNamingStrategy
 * @see org.springframework.jmx.export.annotation.AnnotationJmxAttributeSource
 */
public class MetadataNamingStrategy implements ObjectNamingStrategy, InitializingBean {

	/**
	 * The {@code JmxAttributeSource} implementation to use for reading metadata.
	 * <p>
	 *  用于阅读元数据的{@code JmxAttributeSource}实现
	 * 
	 */
	private JmxAttributeSource attributeSource;

	private String defaultDomain;


	/**
	 * Create a new {@code MetadataNamingStrategy} which needs to be
	 * configured through the {@link #setAttributeSource} method.
	 * <p>
	 *  创建一个新的{@code MetadataNamingStrategy},需要通过{@link #setAttributeSource}方法进行配置
	 * 
	 */
	public MetadataNamingStrategy() {
	}

	/**
	 * Create a new {@code MetadataNamingStrategy} for the given
	 * {@code JmxAttributeSource}.
	 * <p>
	 * 为给定的{@code JmxAttributeSource}创建一个新的{@code MetadataNamingStrategy}
	 * 
	 * 
	 * @param attributeSource the JmxAttributeSource to use
	 */
	public MetadataNamingStrategy(JmxAttributeSource attributeSource) {
		Assert.notNull(attributeSource, "JmxAttributeSource must not be null");
		this.attributeSource = attributeSource;
	}


	/**
	 * Set the implementation of the {@code JmxAttributeSource} interface to use
	 * when reading the source-level metadata.
	 * <p>
	 *  设置在读取源级元数据时使用的{@code JmxAttributeSource}接口的实现
	 * 
	 */
	public void setAttributeSource(JmxAttributeSource attributeSource) {
		Assert.notNull(attributeSource, "JmxAttributeSource must not be null");
		this.attributeSource = attributeSource;
	}

	/**
	 * Specify the default domain to be used for generating ObjectNames
	 * when no source-level metadata has been specified.
	 * <p>The default is to use the domain specified in the bean name
	 * (if the bean name follows the JMX ObjectName syntax); else,
	 * the package name of the managed bean class.
	 * <p>
	 *  指定没有指定源级元数据时用于生成ObjectNames的默认域<p>缺省值是使用bean名称中指定的域(如果该bean名称遵循JMX ObjectName语法);否则,托管bean类的包名称
	 * 
	 */
	public void setDefaultDomain(String defaultDomain) {
		this.defaultDomain = defaultDomain;
	}

	@Override
	public void afterPropertiesSet() {
		if (this.attributeSource == null) {
			throw new IllegalArgumentException("Property 'attributeSource' is required");
		}
	}


	/**
	 * Reads the {@code ObjectName} from the source-level metadata associated
	 * with the managed resource's {@code Class}.
	 * <p>
	 *  从与受管资源的{@code Class}相关联的源级元数据中读取{@code ObjectName}
	 */
	@Override
	public ObjectName getObjectName(Object managedBean, String beanKey) throws MalformedObjectNameException {
		Class<?> managedClass = AopUtils.getTargetClass(managedBean);
		ManagedResource mr = this.attributeSource.getManagedResource(managedClass);

		// Check that an object name has been specified.
		if (mr != null && StringUtils.hasText(mr.getObjectName())) {
			return ObjectNameManager.getInstance(mr.getObjectName());
		}
		else {
			try {
				return ObjectNameManager.getInstance(beanKey);
			}
			catch (MalformedObjectNameException ex) {
				String domain = this.defaultDomain;
				if (domain == null) {
					domain = ClassUtils.getPackageName(managedClass);
				}
				Hashtable<String, String> properties = new Hashtable<String, String>();
				properties.put("type", ClassUtils.getShortName(managedClass));
				properties.put("name", beanKey);
				return ObjectNameManager.getInstance(domain, properties);
			}
		}
	}

}
