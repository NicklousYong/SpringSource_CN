/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2016 the original author or authors.
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

package org.springframework.web.servlet.mvc.method.annotation;

import org.springframework.web.context.request.async.DeferredResult;

/**
 * Contract to adapt a single-value async return value to {@code DeferredResult}.
 *
 * <p>
 * 
 * 
 * @author Rossen Stoyanchev
 * @since 4.3
 */
public interface DeferredResultAdapter {

	/**
	 * Create a {@code DeferredResult} for the given return value.
	 * <p>
	 *  将单值异步返回值修改为{@code DeferredResult}的合同
	 * 
	 * 
	 * @param returnValue the return value (never {@code null})
	 * @return the DeferredResult
	 */
	DeferredResult<?> adaptToDeferredResult(Object returnValue);

}
