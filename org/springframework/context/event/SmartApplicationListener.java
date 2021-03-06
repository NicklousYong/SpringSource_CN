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

package org.springframework.context.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;

/**
 * Extended variant of the standard {@link ApplicationListener} interface,
 * exposing further metadata such as the supported event type.
 *
 * <p>Users are <bold>strongly advised</bold> to use the {@link GenericApplicationListener}
 * interface instead as it provides an improved detection of generics-based
 * event types.
 *
 * <p>
 *  标准的{@link ApplicationListener}接口的扩展变体,可以显示更多的元数据,如支持的事件类型
 * 
 * <p>用户强烈建议</bold>使用{@link GenericApplicationListener}接口,因为它提供了基于泛型的事件类型的改进检测
 * 
 * 
 * @author Juergen Hoeller
 * @since 3.0
 * @see GenericApplicationListener
 */
public interface SmartApplicationListener extends ApplicationListener<ApplicationEvent>, Ordered {

	/**
	 * Determine whether this listener actually supports the given event type.
	 * <p>
	 *  确定此侦听器是否实际支持给定的事件类型
	 * 
	 */
	boolean supportsEventType(Class<? extends ApplicationEvent> eventType);

	/**
	 * Determine whether this listener actually supports the given source type.
	 * <p>
	 *  确定此侦听器是否实际支持给定的源类型
	 */
	boolean supportsSourceType(Class<?> sourceType);

}
