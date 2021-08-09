/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2009 the original author or authors.
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

package org.springframework.jmx.export;

import javax.management.ObjectName;

/**
 * Interface that defines the set of MBean export operations that are intended to be
 * accessed by application developers during application runtime.
 *
 * <p>This interface should be used to export application resources to JMX using Spring's
 * management interface generation capabilties and, optionally, it's {@link ObjectName}
 * generation capabilities.
 *
 * <p>
 *  接口,用于定义应用程序开发人员在应用程序运行时期间访问的MBean导出操作集
 * 
 * <p>此接口应用于使用Spring的管理界面生成能力将应用程序资源导出到JMX,并且可选地,它是{@link ObjectName}生成功能
 * 
 * 
 * @author Rob Harrop
 * @since 2.0
 * @see MBeanExporter
 */
public interface MBeanExportOperations {

	/**
	 * Register the supplied resource with JMX. If the resource is not a valid MBean already,
	 * Spring will generate a management interface for it. The exact interface generated will
	 * depend on the implementation and its configuration. This call also generates an
	 * {@link ObjectName} for the managed resource and returns this to the caller.
	 * <p>
	 *  使用JMX注册提供的资源如果资源不是有效的MBean,Spring将为其生成管理接口生成的确切接口将取决于实现及其配置此调用还会为托管资源生成{@link ObjectName}并将其返回给调用者
	 * 
	 * 
	 * @param managedResource the resource to expose via JMX
	 * @return the {@link ObjectName} under which the resource was exposed
	 * @throws MBeanExportException if Spring is unable to generate an {@link ObjectName}
	 * or register the MBean
	 */
	ObjectName registerManagedResource(Object managedResource) throws MBeanExportException;

	/**
	 * Register the supplied resource with JMX. If the resource is not a valid MBean already,
	 * Spring will generate a management interface for it. The exact interface generated will
	 * depend on the implementation and its configuration.
	 * <p>
	 *  使用JMX注册提供的资源如果资源不是有效的MBean,Spring将为其生成管理接口生成的准确接口将取决于实现及其配置
	 * 
	 * 
	 * @param managedResource the resource to expose via JMX
	 * @param objectName the {@link ObjectName} under which to expose the resource
	 * @throws MBeanExportException if Spring is unable to register the MBean
	 */
	void registerManagedResource(Object managedResource, ObjectName objectName) throws MBeanExportException;

	/**
	 * Remove the specified MBean from the underlying MBeanServer registry.
	 * <p>
	 * 从底层的MBeanServer注册表中删除指定的MBean
	 * 
	 * @param objectName the {@link ObjectName} of the resource to remove
	 */
	void unregisterManagedResource(ObjectName objectName);

}
