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
import org.springframework.core.ResolvableType;

/**
 * Extended variant of the standard {@link ApplicationListener} interface,
 * exposing further metadata such as the supported event type.
 *
 * <p>As of Spring Framework 4.2, supersedes {@link SmartApplicationListener} with
 * proper handling of generics-based event.
 *
 * <p>
 *  标准的{@link ApplicationListener}接口的扩展变体,可以显示更多的元数据,如支持的事件类型
 * 
 * <p>截至Spring Framework 42,取代{@link SmartApplicationListener},正确处理基于泛型的事件
 * 
 * 
 * @author Stephane Nicoll
 * @since 4.2
 */
public interface GenericApplicationListener extends ApplicationListener<ApplicationEvent>, Ordered {

	/**
	 * Determine whether this listener actually supports the given event type.
	 * <p>
	 *  确定此侦听器是否实际支持给定的事件类型
	 * 
	 */
	boolean supportsEventType(ResolvableType eventType);

	/**
	 * Determine whether this listener actually supports the given source type.
	 * <p>
	 *  确定此侦听器是否实际支持给定的源类型
	 */
	boolean supportsSourceType(Class<?> sourceType);

}
