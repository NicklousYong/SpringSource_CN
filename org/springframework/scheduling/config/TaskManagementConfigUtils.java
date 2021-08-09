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

package org.springframework.scheduling.config;

/**
 * Configuration constants for internal sharing across subpackages.
 *
 * <p>
 *  跨子包内部共享的配置常数
 * 
 * 
 * @author Juergen Hoeller
 * @since 4.1
 */
public class TaskManagementConfigUtils {

	/**
	 * The bean name of the internally managed Scheduled annotation processor.
	 * <p>
	 *  内部管理的计划注释处理器的bean名称
	 * 
	 */
	public static final String SCHEDULED_ANNOTATION_PROCESSOR_BEAN_NAME =
			"org.springframework.context.annotation.internalScheduledAnnotationProcessor";

	/**
	 * The bean name of the internally managed Async annotation processor.
	 * <p>
	 *  内部管理的Async注释处理器的bean名称
	 * 
	 */
	public static final String ASYNC_ANNOTATION_PROCESSOR_BEAN_NAME =
			"org.springframework.context.annotation.internalAsyncAnnotationProcessor";

	/**
	 * The bean name of the internally managed AspectJ async execution aspect.
	 * <p>
	 * 内部管理的AspectJ异步执行方面的bean名称
	 */
	public static final String ASYNC_EXECUTION_ASPECT_BEAN_NAME =
			"org.springframework.scheduling.config.internalAsyncExecutionAspect";

}
