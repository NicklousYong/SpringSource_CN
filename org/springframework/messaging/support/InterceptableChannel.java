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

package org.springframework.messaging.support;

import java.util.List;

/**
 * A {@link org.springframework.messaging.MessageChannel MessageChannel} that
 * maintains a list {@link org.springframework.messaging.support.ChannelInterceptor
 * ChannelInterceptors} and allows interception of message sending.
 *
 * <p>
 *  维护列表{@link orgspringframeworkmessagingsupportChannelInterceptor ChannelInterceptors}并允许拦截消息发送的{@link orgspringframeworkmessagingMessageChannel MessageChannel}
 * 。
 * 
 * 
 * @author Rossen Stoyanchev
 * @since 4.1
 */
public interface InterceptableChannel {

	/**
	 * Set the list of channel interceptors clearing any existing interceptors.
	 * <p>
	 * 设置清除任何现有拦截器的通道拦截器列表
	 * 
	 */
	void setInterceptors(List<ChannelInterceptor> interceptors);

	/**
	 * Add a channel interceptor to the end of the list.
	 * <p>
	 *  将频道拦截器添加到列表的末尾
	 * 
	 */
	void addInterceptor(ChannelInterceptor interceptor);

	/**
	 * Add a channel interceptor at the specified index.
	 * <p>
	 *  在指定的索引处添加一个通道拦截器
	 * 
	 */
	void addInterceptor(int index, ChannelInterceptor interceptor);

	/**
	 * Return the list of configured interceptors.
	 * <p>
	 *  返回配置的拦截器列表
	 * 
	 */
	List<ChannelInterceptor> getInterceptors();

	/**
	 * Remove the given interceptor.
	 * <p>
	 *  删除给定的拦截器
	 * 
	 */
	boolean removeInterceptor(ChannelInterceptor interceptor);

	/**
	 * Remove the interceptor at the given index.
	 * <p>
	 *  删除给定索引的拦截器
	 */
	ChannelInterceptor removeInterceptor(int index);

}
