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

package org.springframework.web.bind.support;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.util.ClassUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartRequest;

/**
 * Special {@link org.springframework.validation.DataBinder} to perform data binding
 * from web request parameters to JavaBeans, including support for multipart files.
 *
 * <p>See the DataBinder/WebDataBinder superclasses for customization options,
 * which include specifying allowed/required fields, and registering custom
 * property editors.
 *
 * <p>Can also used for manual data binding in custom web controllers or interceptors
 * that build on Spring's {@link org.springframework.web.context.request.WebRequest}
 * abstraction: e.g. in a {@link org.springframework.web.context.request.WebRequestInterceptor}
 * implementation. Simply instantiate a WebRequestDataBinder for each binding
 * process, and invoke {@code bind} with the current WebRequest as argument:
 *
 * <pre class="code">
 * MyBean myBean = new MyBean();
 * // apply binder to custom target object
 * WebRequestDataBinder binder = new WebRequestDataBinder(myBean);
 * // register custom editors, if desired
 * binder.registerCustomEditor(...);
 * // trigger actual binding of request parameters
 * binder.bind(request);
 * // optionally evaluate binding errors
 * Errors errors = binder.getErrors();
 * ...</pre>
 *
 * <p>
 *  执行从Web请求参数到JavaBeans的数据绑定的特殊{@link orgspringframeworkvalidationDataBinder},包括支持多部分文件
 * 
 * <p>请参阅DataBinder / WebDataBinder超类用于自定义选项,其中包括指定允许/必填字段以及注册自定义属性编辑器
 * 
 *  <p>还可以用于在Spring的{@link orgspringframeworkwebcontextrequestWebRequest}抽象构建的自定义Web控制器或拦截器中的手动数据绑定：例如,在
 * {@link orgspringframeworkwebcontextrequestWebRequestInterceptor}实现中简单地为每个绑定过程实例化一个WebRequestDataBinde
 * r,并调用{@code bind}与当前的WebRequest作为参数：。
 * 
 * <pre class="code">
 * MyBean myBean = new MyBean(); //将绑定器应用到自定义目标对象WebRequestDataBinder binder = new WebRequestDataBinder(
 * myBean); //注册自定义编辑器,如果需要binderregisterCustomEditor(); //触发实际绑定请求参数bindingbind(request); //可选地评估绑定错误错误
 * 错误= bindergetErrors(); </PRE>。
 * 
 * 
 * @author Juergen Hoeller
 * @author Brian Clozel
 * @since 2.5.2
 * @see #bind(org.springframework.web.context.request.WebRequest)
 * @see #registerCustomEditor
 * @see #setAllowedFields
 * @see #setRequiredFields
 * @see #setFieldMarkerPrefix
 */
public class WebRequestDataBinder extends WebDataBinder {

	private static final boolean servlet3Parts = ClassUtils.hasMethod(HttpServletRequest.class, "getParts");


	/**
	 * Create a new WebRequestDataBinder instance, with default object name.
	 * <p>
	 *  创建一个新的WebRequestDataBinder实例,具有默认对象名称
	 * 
	 * 
	 * @param target the target object to bind onto (or {@code null}
	 * if the binder is just used to convert a plain parameter value)
	 * @see #DEFAULT_OBJECT_NAME
	 */
	public WebRequestDataBinder(Object target) {
		super(target);
	}

	/**
	 * Create a new WebRequestDataBinder instance.
	 * <p>
	 *  创建一个新的WebRequestDataBinder实例
	 * 
	 * 
	 * @param target the target object to bind onto (or {@code null}
	 * if the binder is just used to convert a plain parameter value)
	 * @param objectName the name of the target object
	 */
	public WebRequestDataBinder(Object target, String objectName) {
		super(target, objectName);
	}


	/**
	 * Bind the parameters of the given request to this binder's target,
	 * also binding multipart files in case of a multipart request.
	 * <p>This call can create field errors, representing basic binding
	 * errors like a required field (code "required"), or type mismatch
	 * between value and bean property (code "typeMismatch").
	 * <p>Multipart files are bound via their parameter name, just like normal
	 * HTTP parameters: i.e. "uploadedFile" to an "uploadedFile" bean property,
	 * invoking a "setUploadedFile" setter method.
	 * <p>The type of the target property for a multipart file can be Part, MultipartFile,
	 * byte[], or String. The latter two receive the contents of the uploaded file;
	 * all metadata like original file name, content type, etc are lost in those cases.
	 * <p>
	 * 将给定请求的参数绑定到此binder的目标,并在多部分请求的情况下绑定多部分文件<p>此调用可以创建字段错误,表示基本绑定错误,如必填字段(代码"必需")或类型不匹配值与bean属性之间(代码"type
	 * Mismatch")<p>多部分文件通过参数名称绑定,就像普通HTTP参数一样：ie"uploadedFile"到"uploadedFile"bean属性,调用"setUploadedFile"sett
	 * er方法>多部分文件的目标属性的类型可以是Part,MultipartFile,byte []或String后者接收上传文件的内容;在这些情况下,所有元数据(如原始文件名,内容类型等)都将丢失。
	 * 
	 * 
	 * @param request request with parameters to bind (can be multipart)
	 * @see org.springframework.web.multipart.MultipartRequest
	 * @see org.springframework.web.multipart.MultipartFile
	 * @see javax.servlet.http.Part
	 * @see #bind(org.springframework.beans.PropertyValues)
	 */
	public void bind(WebRequest request) {
		MutablePropertyValues mpvs = new MutablePropertyValues(request.getParameterMap());
		if (isMultipartRequest(request) && request instanceof NativeWebRequest) {
			MultipartRequest multipartRequest = ((NativeWebRequest) request).getNativeRequest(MultipartRequest.class);
			if (multipartRequest != null) {
				bindMultipart(multipartRequest.getMultiFileMap(), mpvs);
			}
			else if (servlet3Parts) {
				HttpServletRequest serlvetRequest = ((NativeWebRequest) request).getNativeRequest(HttpServletRequest.class);
				new Servlet3MultipartHelper(isBindEmptyMultipartFiles()).bindParts(serlvetRequest, mpvs);
			}
		}
		doBind(mpvs);
	}

	/**
	 * Check if the request is a multipart request (by checking its Content-Type header).
	 * <p>
	 * 检查请求是否是多部分请求(通过检查其Content-Type标头)
	 * 
	 * 
	 * @param request request with parameters to bind
	 */
	private boolean isMultipartRequest(WebRequest request) {
		String contentType = request.getHeader("Content-Type");
		return (contentType != null && StringUtils.startsWithIgnoreCase(contentType, "multipart"));
	}

	/**
	 * Treats errors as fatal.
	 * <p>Use this method only if it's an error if the input isn't valid.
	 * This might be appropriate if all input is from dropdowns, for example.
	 * <p>
	 *  将错误视为致命的<p>仅当输入无效时才使用此方法如果所有输入都是来自下拉列表,则可能适用
	 * 
	 * 
	 * @throws BindException if binding errors have been encountered
	 */
	public void closeNoCatch() throws BindException {
		if (getBindingResult().hasErrors()) {
			throw new BindException(getBindingResult());
		}
	}


	/**
	 * Encapsulate Part binding code for Servlet 3.0+ only containers.
	 * <p>
	 *  封装Servlet 30+以上容器的零件绑定代码
	 * 
	 * @see javax.servlet.http.Part
	 */
	private static class Servlet3MultipartHelper {

		private final boolean bindEmptyMultipartFiles;

		public Servlet3MultipartHelper(boolean bindEmptyMultipartFiles) {
			this.bindEmptyMultipartFiles = bindEmptyMultipartFiles;
		}

		public void bindParts(HttpServletRequest request, MutablePropertyValues mpvs) {
			try {
				MultiValueMap<String, Part> map = new LinkedMultiValueMap<String, Part>();
				for (Part part : request.getParts()) {
					map.add(part.getName(), part);
				}
				for (Map.Entry<String, List<Part>> entry: map.entrySet()) {
					if (entry.getValue().size() == 1) {
						Part part = entry.getValue().get(0);
						if (this.bindEmptyMultipartFiles || part.getSize() > 0) {
							mpvs.add(entry.getKey(), part);
						}
					}
					else {
						mpvs.add(entry.getKey(), entry.getValue());
					}
				}
			}
			catch (Exception ex) {
				throw new MultipartException("Failed to get request parts", ex);
			}
		}
	}

}
