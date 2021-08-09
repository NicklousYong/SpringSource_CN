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

package org.springframework.context.annotation;

import org.springframework.instrument.classloading.LoadTimeWeaver;

/**
 * Interface to be implemented by
 * {@link org.springframework.context.annotation.Configuration @Configuration}
 * classes annotated with {@link EnableLoadTimeWeaving @EnableLoadTimeWeaving} that wish to
 * customize the {@link LoadTimeWeaver} instance to be used.
 *
 * <p>See {@link org.springframework.scheduling.annotation.EnableAsync @EnableAsync}
 * for usage examples and information on how a default {@code LoadTimeWeaver}
 * is selected when this interface is not used.
 *
 * <p>
 * 要通过{@link EnableLoadTimeWeaving @EnableLoadTimeWeaving}注释的{@link orgspringframeworkcontextannotationConfiguration @Configuration}
 * 类实现的接口,希望自定义要使用的{@link LoadTimeWeaver}实例。
 * 
 *  <p>有关使用示例的信息,请参阅{@link orgspringframeworkschedulingannotationEnableAsync @EnableAsync}以及当不使用此接口时如何选择
 * 默认{@code LoadTimeWeaver}的信息。
 * 
 * @author Chris Beams
 * @since 3.1
 * @see LoadTimeWeavingConfiguration
 * @see EnableLoadTimeWeaving
 */
public interface LoadTimeWeavingConfigurer {

	/**
	 * Create, configure and return the {@code LoadTimeWeaver} instance to be used. Note
	 * that it is unnecessary to annotate this method with {@code @Bean}, because the
	 * object returned will automatically be registered as a bean by
	 * {@link LoadTimeWeavingConfiguration#loadTimeWeaver()}
	 * <p>
	 * 
	 */
	LoadTimeWeaver getLoadTimeWeaver();

}
