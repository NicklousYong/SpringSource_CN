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
package org.springframework.web.servlet.mvc.method.annotation;


import java.io.IOException;
import java.io.OutputStream;

/**
 * A controller method return value type for asynchronous request processing
 * where the application can write directly to the response {@code OutputStream}
 * without holding up the Servlet container thread.
 *
 * <p><strong>Note:</strong> when using this option it is highly recommended to
 * configure explicitly the TaskExecutor used in Spring MVC for executing
 * asynchronous requests. Both the MVC Java config and the MVC namespaces provide
 * options to configure asynchronous handling. If not using those, an application
 * can set the {@code taskExecutor} property of
 * {@link org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter
 * RequestMappingHandlerAdapter}.
 *
 * <p>
 *  用于异步请求处理的控制器方法返回值类型,其中应用程序可以直接写入响应{@code OutputStream}而不会阻止Servlet容器线程
 * 
 * <p> <strong>注意：</strong>当使用此选项时,强烈建议您明确配置Spring MVC中用于执行异步请求的TaskExecutor MVC Java配置和MVC命名空间提供了配置异步处理
 * 的选项使用这些应用程序,应用程序可以设置{@link orgspringframeworkwebservletmvcmethodannotationRequestMappingHandlerAdapter RequestMappingHandlerAdapter}
 * 的{@code taskExecutor}属性。
 * 
 * @author Rossen Stoyanchev
 * @since 4.2
 */
public interface StreamingResponseBody {

	/**
	 * A callback for writing to the response body.
	 * <p>
	 * 
	 * 
	 * @param outputStream the stream for the response body
	 * @throws IOException an exception while writing
	 */
	void writeTo(OutputStream outputStream) throws IOException;

}
