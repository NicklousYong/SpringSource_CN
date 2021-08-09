/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2014 the original author or authors.
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

package org.springframework.jmx.export.metadata;

import org.springframework.util.StringUtils;

/**
 * Metadata that indicates a JMX notification emitted by a bean.
 *
 * <p>
 *  指示由bean发布的JMX通知的元数据
 * 
 * 
 * @author Rob Harrop
 * @since 2.0
 */
public class ManagedNotification {

	private String[] notificationTypes;

	private String name;

	private String description;


	/**
	 * Set a single notification type, or a list of notification types
	 * as comma-delimited String.
	 * <p>
	 *  设置单个通知类型,或者以逗号分隔的String的通知类型列表
	 * 
	 */
	public void setNotificationType(String notificationType) {
		this.notificationTypes = StringUtils.commaDelimitedListToStringArray(notificationType);
	}

	/**
	 * Set a list of notification types.
	 * <p>
	 *  设置通知类型列表
	 * 
	 */
	public void setNotificationTypes(String... notificationTypes) {
		this.notificationTypes = notificationTypes;
	}

	/**
	 * Return the list of notification types.
	 * <p>
	 * 返回通知类型列表
	 * 
	 */
	public String[] getNotificationTypes() {
		return this.notificationTypes;
	}

	/**
	 * Set the name of this notification.
	 * <p>
	 *  设置此通知的名称
	 * 
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Return the name of this notification.
	 * <p>
	 *  返回此通知的名称
	 * 
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Set a description for this notification.
	 * <p>
	 *  设置此通知的说明
	 * 
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Return a description for this notification.
	 * <p>
	 *  返回此通知的说明
	 */
	public String getDescription() {
		return this.description;
	}

}
