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

package org.springframework.scheduling.annotation;

import java.util.concurrent.Executor;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

/**
 * Interface to be implemented by @{@link org.springframework.context.annotation.Configuration
 * Configuration} classes annotated with @{@link EnableAsync} that wish to customize the
 * {@link Executor} instance used when processing async method invocations or the
 * {@link AsyncUncaughtExceptionHandler} instance used to process exception thrown from
 * async method with {@code void} return type.
 *
 * <p>Consider using {@link AsyncConfigurerSupport} providing default implementations for
 * both methods if only one element needs to be customized. Furthermore, backward compatibility
 * of this interface will be insured in case new customization options are introduced
 * in the future.
 *
 * <p>See @{@link EnableAsync} for usage examples.
 *
 * <p>
 * 用@ {@ link orgspringframeworkcontextannotationConfiguration Configuration}实现的接口,它们使用@ {@ link EnableAsync}
 * 注释,希望自定义处理异步方法调用时使用的{@link Executor}实例或用于处理异常的{@link AsyncUncaughtExceptionHandler}实例从异步方法抛出{@code void}
 * 返回类型。
 * 
 *  <p>考虑使用{@link AsyncConfigurerSupport}为两种方法提供默认实现,只需要定制一个元素。此外,如果将来引入新的定制选项,此接口的向后兼容性将被保险
 * 
 *  <p>有关使用示例,请参阅@ {@ link EnableAsync}
 * 
 * @author Chris Beams
 * @author Stephane Nicoll
 * @since 3.1
 * @see AbstractAsyncConfiguration
 * @see EnableAsync
 * @see AsyncConfigurerSupport
 */
public interface AsyncConfigurer {

	/**
	 * The {@link Executor} instance to be used when processing async
	 * method invocations.
	 * <p>
	 * 
	 */
	Executor getAsyncExecutor();

	/**
	 * The {@link AsyncUncaughtExceptionHandler} instance to be used
	 * when an exception is thrown during an asynchronous method execution
	 * with {@code void} return type.
	 * <p>
	 *  处理异步方法调用时使用的{@link Executor}实例
	 * 
	 */
	AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler();

}
