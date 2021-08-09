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

package org.springframework.web.servlet.view.xml;

import java.io.ByteArrayOutputStream;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBElement;
import javax.xml.transform.stream.StreamResult;

import org.springframework.oxm.Marshaller;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.AbstractView;

/**
 * Spring-MVC {@link View} that allows for response context to be rendered as the result
 * of marshalling by a {@link Marshaller}.
 *
 * <p>The Object to be marshalled is supplied as a parameter in the model and then
 * {@linkplain #locateToBeMarshalled(Map) detected} during response rendering. Users can
 * either specify a specific entry in the model via the {@link #setModelKey(String) sourceKey}
 * property or have Spring locate the Source object.
 *
 * <p>
 *  Spring-MVC {@link View}允许响应上下文由{@link Marshaller}编组的结果呈现,
 * 
 * <p>要编组的对象作为参数在模型中提供,然后在响应呈现期间{@linkplain #locateToBeMarshalled(Map))检测到}用户可以通过{@link #setModelKey(String)来指定模型中的特定条目)sourceKey}
 * 属性或者有Spring定位Source对象。
 * 
 * 
 * @author Arjen Poutsma
 * @author Juergen Hoeller
 * @since 3.0
 */
public class MarshallingView extends AbstractView {

	/**
	 * Default content type. Overridable as bean property.
	 * <p>
	 *  默认内容类型可覆盖为bean属性
	 * 
	 */
	public static final String DEFAULT_CONTENT_TYPE = "application/xml";


	private Marshaller marshaller;

	private String modelKey;


	/**
	 * Construct a new {@code MarshallingView} with no {@link Marshaller} set.
	 * The marshaller must be set after construction by invoking {@link #setMarshaller}.
	 * <p>
	 *  构造一个新的{@code MarshallingView},没有{@link Marshaller} set通过调用{@link #setMarshaller},必须在构造之后设置编组者
	 * 
	 */
	public MarshallingView() {
		setContentType(DEFAULT_CONTENT_TYPE);
		setExposePathVariables(false);
	}

	/**
	 * Constructs a new {@code MarshallingView} with the given {@link Marshaller} set.
	 * <p>
	 *  使用给定的{@link Marshaller}集构建新的{@code MarshallingView}
	 * 
	 */
	public MarshallingView(Marshaller marshaller) {
		this();
		Assert.notNull(marshaller, "Marshaller must not be null");
		this.marshaller = marshaller;
	}


	/**
	 * Set the {@link Marshaller} to be used by this view.
	 * <p>
	 *  设置此视图使用的{@link Marshaller}
	 * 
	 */
	public void setMarshaller(Marshaller marshaller) {
		this.marshaller = marshaller;
	}

	/**
	 * Set the name of the model key that represents the object to be marshalled.
	 * If not specified, the model map will be searched for a supported value type.
	 * <p>
	 * 设置表示要编组的对象的模型键的名称如果未指定,将搜索模型图以获取支持的值类型
	 * 
	 * 
	 * @see Marshaller#supports(Class)
	 */
	public void setModelKey(String modelKey) {
		this.modelKey = modelKey;
	}

	@Override
	protected void initApplicationContext() {
		Assert.notNull(this.marshaller, "Property 'marshaller' is required");
	}


	@Override
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Object toBeMarshalled = locateToBeMarshalled(model);
		if (toBeMarshalled == null) {
			throw new IllegalStateException("Unable to locate object to be marshalled in model: " + model);
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
		this.marshaller.marshal(toBeMarshalled, new StreamResult(baos));

		setResponseContentType(request, response);
		response.setContentLength(baos.size());
		baos.writeTo(response.getOutputStream());
	}

	/**
	 * Locate the object to be marshalled.
	 * <p>The default implementation first attempts to look under the configured
	 * {@linkplain #setModelKey(String) model key}, if any, before attempting to
	 * locate an object of {@linkplain Marshaller#supports(Class) supported type}.
	 * <p>
	 *  找到要编组的对象<p>默认实现首先尝试查找{@linkplain Marshaller#supports(Class)的对象之前,先查找配置的{@linkplain #setModelKey(String)模型键}
	 * (如果有)支持型}。
	 * 
	 * 
	 * @param model the model Map
	 * @return the Object to be marshalled (or {@code null} if none found)
	 * @throws IllegalStateException if the model object specified by the
	 * {@linkplain #setModelKey(String) model key} is not supported by the marshaller
	 * @see #setModelKey(String)
	 */
	protected Object locateToBeMarshalled(Map<String, Object> model) throws IllegalStateException {
		if (this.modelKey != null) {
			Object value = model.get(this.modelKey);
			if (value == null) {
				throw new IllegalStateException("Model contains no object with key [" + this.modelKey + "]");
			}
			if (!isEligibleForMarshalling(this.modelKey, value)) {
				throw new IllegalStateException("Model object [" + value + "] retrieved via key [" +
						this.modelKey + "] is not supported by the Marshaller");
			}
			return value;
		}
		for (Map.Entry<String, Object> entry : model.entrySet()) {
			Object value = entry.getValue();
			if (value != null && (model.size() == 1 || !(value instanceof BindingResult)) &&
					isEligibleForMarshalling(entry.getKey(), value)) {
				return value;
			}
		}
		return null;
	}

	/**
	 * Check whether the given value from the current view's model is eligible
	 * for marshalling through the configured {@link Marshaller}.
	 * <p>The default implementation calls {@link Marshaller#supports(Class)},
	 * unwrapping a given {@link JAXBElement} first if applicable.
	 * <p>
	 *  检查当前视图模型中的给定值是否符合通过配置的{@link Marshaller}进行编组<p>默认实现调用{@link Marshaller#supports(Class)},首先解除给定的{@link JAXBElement}
	 *  if适用。
	 * 
	 * @param modelKey the value's key in the model (never {@code null})
	 * @param value the value to check (never {@code null})
	 * @return whether the given value is to be considered as eligible
	 * @see Marshaller#supports(Class)
	 */
	protected boolean isEligibleForMarshalling(String modelKey, Object value) {
		Class<?> classToCheck = value.getClass();
		if (value instanceof JAXBElement) {
			classToCheck = ((JAXBElement) value).getDeclaredType();
		}
		return this.marshaller.supports(classToCheck);
	}

}
