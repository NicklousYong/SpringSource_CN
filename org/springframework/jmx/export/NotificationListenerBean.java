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

package org.springframework.jmx.export;

import javax.management.NotificationListener;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.jmx.support.NotificationListenerHolder;
import org.springframework.util.Assert;

/**
 * Helper class that aggregates a {@link javax.management.NotificationListener},
 * a {@link javax.management.NotificationFilter}, and an arbitrary handback
 * object.
 *
 * <p>Also provides support for associating the encapsulated
 * {@link javax.management.NotificationListener} with any number of
 * MBeans from which it wishes to receive
 * {@link javax.management.Notification Notifications} via the
 * {@link #setMappedObjectNames mappedObjectNames} property.
 *
 * <p>Note: This class supports Spring bean names as
 * {@link #setMappedObjectNames "mappedObjectNames"} as well, as alternative
 * to specifying JMX object names. Note that only beans exported by the
 * same {@link MBeanExporter} are supported for such bean names.
 *
 * <p>
 *  汇总一个{@link javaxmanagementNotificationListener},一个{@link javaxmanagementNotificationFilter}和一个任意回调对象
 * 的助手类。
 * 
 * <p>还提供支持将封装的{@link javaxmanagementNotificationListener}与通过{@link #setMappedObjectNames mappedObjectNames}
 * 属性从中收到{@link javaxmanagementNotification Notifications}的任何数量的MBean相关联。
 * 
 *  注意：此类支持Spring bean名称作为{@link #setMappedObjectNames"mappedObjectNames"},作为指定JMX对象名称的替代方法请注意,只有这样的bean
 * 名称支持由同一个{@link MBeanExporter}导出的bean。
 * 
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @since 2.0
 * @see MBeanExporter#setNotificationListeners
 */
public class NotificationListenerBean extends NotificationListenerHolder implements InitializingBean {

	/**
	 * Create a new instance of the {@link NotificationListenerBean} class.
	 * <p>
	 * 
	 */
	public NotificationListenerBean() {
	}

	/**
	 * Create a new instance of the {@link NotificationListenerBean} class.
	 * <p>
	 *  创建{@link NotificationListenerBean}类的新实例
	 * 
	 * 
	 * @param notificationListener the encapsulated listener
	 */
	public NotificationListenerBean(NotificationListener notificationListener) {
		Assert.notNull(notificationListener, "NotificationListener must not be null");
		setNotificationListener(notificationListener);
	}


	@Override
	public void afterPropertiesSet() {
		if (getNotificationListener() == null) {
			throw new IllegalArgumentException("Property 'notificationListener' is required");
		}
	}

	void replaceObjectName(Object originalName, Object newName) {
		if (this.mappedObjectNames != null && this.mappedObjectNames.contains(originalName)) {
			this.mappedObjectNames.remove(originalName);
			this.mappedObjectNames.add(newName);
		}
	}

}
