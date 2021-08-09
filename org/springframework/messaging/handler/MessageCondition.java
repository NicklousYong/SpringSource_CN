/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2013 the original author or authors.
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

package org.springframework.messaging.handler;

import org.springframework.messaging.Message;

/**
 * Contract for mapping conditions to messages.
 *
 * <p>Message conditions can be combined (e.g. type + method-level conditions),
 * matched to a specific Message, as well as compared to each other in the
 * context of a Message to determine which one matches a request more closely.
 *
 * <p>
 *  将条件映射到消息的合同
 * 
 * <p>消息条件可以组合(例如,类型+方法级条件),与特定消息匹配,并且在消息的上下文中彼此比较,以确定哪个更匹配请求
 * 
 * 
 * @param <T> The kind of condition that this condition can be combined with or compared to
 *
 * @author Rossen Stoyanchev
 * @since 4.0
 */
public interface MessageCondition<T> {

	/**
	 * Define the rules for combining this condition with another.
	 * For example combining type- and method-level conditions.
	 * <p>
	 *  定义将此条件与另一个条件组合的规则例如组合类型和方法级条件
	 * 
	 * 
	 * @param other the condition to combine with
	 * @return the resulting message condition
	 */
	T combine(T other);

	/**
	 * Check if this condition matches the given Message and returns a
	 * potentially new condition with content tailored to the current message.
	 * For example a condition with destination patterns might return a new
	 * condition with sorted, matching patterns only.
	 * <p>
	 *  检查此条件是否符合给定的消息,并返回具有针对当前消息的内容的潜在新条件例如,具有目的地模式的条件可能会返回一个新的条件,并且仅使用排序匹配模式
	 * 
	 * 
	 * @return a condition instance in case of a match; or {@code null} if there is no match.
	 */
	T getMatchingCondition(Message<?> message);

	/**
	 * Compare this condition to another in the context of a specific message.
	 * It is assumed both instances have been obtained via
	 * {@link #getMatchingCondition(Message)} to ensure they have content
	 * relevant to current message only.
	 * <p>
	 * 在特定消息的上下文中将此条件与另一个条件进行比较假定已经通过{@link #getMatchingCondition(Message)}获取了两个实例,以确保它们只具有与当前消息相关的内容
	 */
	int compareTo(T other, Message<?> message);

}
