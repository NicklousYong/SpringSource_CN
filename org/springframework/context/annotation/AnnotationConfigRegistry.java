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

package org.springframework.context.annotation;

/**
 * Common interface for annotation config application contexts,
 * defining {@link #register} and {@link #scan} methods.
 *
 * <p>
 *  注释配置应用程序上下文的通用界面,定义{@link #register}和{@link #scan}方法
 * 
 * 
 * @author Juergen Hoeller
 * @since 4.1
 */
public interface AnnotationConfigRegistry {

	/**
	 * Register one or more annotated classes to be processed.
	 * <p>Calls to {@code register} are idempotent; adding the same
	 * annotated class more than once has no additional effect.
	 * <p>
	 * 注册要处理的一个或多个注释类<p>对{@code register}的调用是幂等的;不止一次添加相同的注释类没有额外的作用
	 * 
	 * 
	 * @param annotatedClasses one or more annotated classes,
	 * e.g. {@link Configuration @Configuration} classes
	 */
	void register(Class<?>... annotatedClasses);

	/**
	 * Perform a scan within the specified base packages.
	 * <p>
	 *  在指定的基本包中执行扫描
	 * 
	 * @param basePackages the packages to check for annotated classes
	 */
	void scan(String... basePackages);

}
