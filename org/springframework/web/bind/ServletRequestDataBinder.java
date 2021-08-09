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

package org.springframework.web.bind;

import javax.servlet.ServletRequest;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.validation.BindException;
import org.springframework.web.multipart.MultipartRequest;
import org.springframework.web.util.WebUtils;

/**
 * Special {@link org.springframework.validation.DataBinder} to perform data binding
 * from servlet request parameters to JavaBeans, including support for multipart files.
 *
 * <p>See the DataBinder/WebDataBinder superclasses for customization options,
 * which include specifying allowed/required fields, and registering custom
 * property editors.
 *
 * <p>Can also be used for manual data binding in custom web controllers:
 * for example, in a plain Controller implementation or in a MultiActionController
 * handler method. Simply instantiate a ServletRequestDataBinder for each binding
 * process, and invoke {@code bind} with the current ServletRequest as argument:
 *
 * <pre class="code">
 * MyBean myBean = new MyBean();
 * // apply binder to custom target object
 * ServletRequestDataBinder binder = new ServletRequestDataBinder(myBean);
 * // register custom editors, if desired
 * binder.registerCustomEditor(...);
 * // trigger actual binding of request parameters
 * binder.bind(request);
 * // optionally evaluate binding errors
 * Errors errors = binder.getErrors();
 * ...</pre>
 *
 * <p>
 *  执行从servlet请求参数到JavaBeans的数据绑定的特殊{@link orgspringframeworkvalidationDataBinder},包括支持多部分文件
 * 
 * <p>请参阅DataBinder / WebDataBinder超类用于自定义选项,其中包括指定允许/必填字段以及注册自定义属性编辑器
 * 
 *  <p>也可以用于自定义Web控制器中的手动数据绑定：例如,在一个简单的Controller实现或MultiActionController处理程序方法中,简单地为每个绑定过程实例化一个ServletR
 * equestDataBinder,并使用当前的ServletRequest调用{@code bind}作为论证：。
 * 
 * <pre class="code">
 * MyBean myBean = new MyBean(); //将绑定器应用于自定义目标对象ServletRequestDataBinder binder = new ServletRequestDat
 * aBinder(myBean); //注册自定义编辑器,如果需要binderregisterCustomEditor(); //触发实际绑定请求参数bindingbind(request); //可选地
 * 评估绑定错误错误错误= bindergetErrors(); </PRE>。
 * 
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see #bind(javax.servlet.ServletRequest)
 * @see #registerCustomEditor
 * @see #setAllowedFields
 * @see #setRequiredFields
 * @see #setFieldMarkerPrefix
 */
public class ServletRequestDataBinder extends WebDataBinder {

	/**
	 * Create a new ServletRequestDataBinder instance, with default object name.
	 * <p>
	 *  创建一个新的ServletRequestDataBinder实例,具有默认对象名称
	 * 
	 * 
	 * @param target the target object to bind onto (or {@code null}
	 * if the binder is just used to convert a plain parameter value)
	 * @see #DEFAULT_OBJECT_NAME
	 */
	public ServletRequestDataBinder(Object target) {
		super(target);
	}

	/**
	 * Create a new ServletRequestDataBinder instance.
	 * <p>
	 *  创建一个新的ServletRequestDataBinder实例
	 * 
	 * 
	 * @param target the target object to bind onto (or {@code null}
	 * if the binder is just used to convert a plain parameter value)
	 * @param objectName the name of the target object
	 */
	public ServletRequestDataBinder(Object target, String objectName) {
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
	 * <p>The type of the target property for a multipart file can be MultipartFile,
	 * byte[], or String. The latter two receive the contents of the uploaded file;
	 * all metadata like original file name, content type, etc are lost in those cases.
	 * <p>
	 * 将给定请求的参数绑定到此binder的目标,并在多部分请求的情况下绑定多部分文件<p>此调用可以创建字段错误,表示基本绑定错误,如必填字段(代码"必需")或类型不匹配值与bean属性之间(代码"type
	 * Mismatch")<p>多部分文件通过参数名称绑定,就像普通HTTP参数一样：ie"uploadedFile"到"uploadedFile"bean属性,调用"setUploadedFile"sett
	 * er方法>多部分文件的目标属性的类型可以是MultipartFile,byte []或String后者接收上传文件的内容;在这些情况下,所有元数据(如原始文件名,内容类型等)都将丢失。
	 * 
	 * 
	 * @param request request with parameters to bind (can be multipart)
	 * @see org.springframework.web.multipart.MultipartHttpServletRequest
	 * @see org.springframework.web.multipart.MultipartFile
	 * @see #bind(org.springframework.beans.PropertyValues)
	 */
	public void bind(ServletRequest request) {
		MutablePropertyValues mpvs = new ServletRequestParameterPropertyValues(request);
		MultipartRequest multipartRequest = WebUtils.getNativeRequest(request, MultipartRequest.class);
		if (multipartRequest != null) {
			bindMultipart(multipartRequest.getMultiFileMap(), mpvs);
		}
		addBindValues(mpvs, request);
		doBind(mpvs);
	}

	/**
	 * Extension point that subclasses can use to add extra bind values for a
	 * request. Invoked before {@link #doBind(MutablePropertyValues)}.
	 * The default implementation is empty.
	 * <p>
	 * 子类可用于为请求添加额外绑定值的扩展点在{@link #doBind(MutablePropertyValues)}之前调用}默认实现为空
	 * 
	 * 
	 * @param mpvs the property values that will be used for data binding
	 * @param request the current request
	 */
	protected void addBindValues(MutablePropertyValues mpvs, ServletRequest request) {
	}

	/**
	 * Treats errors as fatal.
	 * <p>Use this method only if it's an error if the input isn't valid.
	 * This might be appropriate if all input is from dropdowns, for example.
	 * <p>
	 *  将错误视为致命的<p>仅当输入无效时才使用此方法如果所有输入都是来自下拉列表,则可能适用
	 * 
	 * @throws ServletRequestBindingException subclass of ServletException on any binding problem
	 */
	public void closeNoCatch() throws ServletRequestBindingException {
		if (getBindingResult().hasErrors()) {
			throw new ServletRequestBindingException(
					"Errors binding onto object '" + getBindingResult().getObjectName() + "'",
					new BindException(getBindingResult()));
		}
	}

}
