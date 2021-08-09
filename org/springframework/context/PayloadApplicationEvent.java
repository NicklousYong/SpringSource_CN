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

package org.springframework.context;

import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;
import org.springframework.util.Assert;

/**
 * An {@link ApplicationEvent} that carries an arbitrary payload.
 *
 * <p>Mainly intended for internal use within the framework.
 *
 * <p>
 *  携带任意有效载荷的{@link ApplicationEvent}
 * 
 *  <p>主要用于框架内部使用
 * 
 * 
 * @author Stephane Nicoll
 * @since 4.2
 * @param <T> the payload type of the event
 */
@SuppressWarnings("serial")
public class PayloadApplicationEvent<T> extends ApplicationEvent implements ResolvableTypeProvider {

	private final T payload;


	/**
	 * Create a new PayloadApplicationEvent.
	 * <p>
	 *  创建一个新的PayloadApplicationEvent
	 * 
	 * 
	 * @param source the object on which the event initially occurred (never {@code null})
	 * @param payload the payload object (never {@code null})
	 */
	public PayloadApplicationEvent(Object source, T payload) {
		super(source);
		Assert.notNull(payload, "Payload must not be null");
		this.payload = payload;
	}


	@Override
	public ResolvableType getResolvableType() {
		return ResolvableType.forClassWithGenerics(getClass(), ResolvableType.forInstance(getPayload()));
	}

	/**
	 * Return the payload of the event.
	 * <p>
	 *  返回事件的有效载荷
	 */
	public T getPayload() {
		return this.payload;
	}

}
