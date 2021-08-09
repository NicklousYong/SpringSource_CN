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

package org.springframework.web.method.annotation;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionAttributeStore;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.context.request.WebRequest;

/**
 * Manages controller-specific session attributes declared via
 * {@link SessionAttributes @SessionAttributes}. Actual storage is
 * delegated to a {@link SessionAttributeStore} instance.
 *
 * <p>When a controller annotated with {@code @SessionAttributes} adds
 * attributes to its model, those attributes are checked against names and
 * types specified via {@code @SessionAttributes}. Matching model attributes
 * are saved in the HTTP session and remain there until the controller calls
 * {@link SessionStatus#setComplete()}.
 *
 * <p>
 *  管理通过{@link SessionAttributes @SessionAttributes}声明的特定于控制器的会话属性将实际存储委派给{@link SessionAttributeStore}实
 * 例。
 * 
 * <p>当使用{@code @SessionAttributes}注释的控制器向其模型添加属性时,将通过{@code @SessionAttributes}指定的名称和类型来检查这些属性。
 * 匹配模型属性保存在HTTP会话中,并保留在控制器调用{@link SessionStatus#setComplete()}。
 * 
 * 
 * @author Rossen Stoyanchev
 * @since 3.1
 */
public class SessionAttributesHandler {

	private final Set<String> attributeNames = new HashSet<String>();

	private final Set<Class<?>> attributeTypes = new HashSet<Class<?>>();

	private final Set<String> knownAttributeNames =
			Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>(4));

	private final SessionAttributeStore sessionAttributeStore;


	/**
	 * Create a new instance for a controller type. Session attribute names and
	 * types are extracted from the {@code @SessionAttributes} annotation, if
	 * present, on the given type.
	 * <p>
	 *  为控制器类型创建一个新实例会话属性名称和类型从给定类型的{@code @SessionAttributes}注释(如果存在)中提取
	 * 
	 * 
	 * @param handlerType the controller type
	 * @param sessionAttributeStore used for session access
	 */
	public SessionAttributesHandler(Class<?> handlerType, SessionAttributeStore sessionAttributeStore) {
		Assert.notNull(sessionAttributeStore, "SessionAttributeStore may not be null");
		this.sessionAttributeStore = sessionAttributeStore;

		SessionAttributes annotation =
				AnnotatedElementUtils.findMergedAnnotation(handlerType, SessionAttributes.class);
		if (annotation != null) {
			this.attributeNames.addAll(Arrays.asList(annotation.names()));
			this.attributeTypes.addAll(Arrays.asList(annotation.types()));
		}

		for (String attributeName : this.attributeNames) {
			this.knownAttributeNames.add(attributeName);
		}
	}

	/**
	 * Whether the controller represented by this instance has declared any
	 * session attributes through an {@link SessionAttributes} annotation.
	 * <p>
	 *  由此实例表示的控制器是否通过{@link SessionAttributes}注释声明任何会话属性
	 * 
	 */
	public boolean hasSessionAttributes() {
		return (this.attributeNames.size() > 0 || this.attributeTypes.size() > 0);
	}

	/**
	 * Whether the attribute name or type match the names and types specified
	 * via {@code @SessionAttributes} in underlying controller.
	 *
	 * <p>Attributes successfully resolved through this method are "remembered"
	 * and subsequently used in {@link #retrieveAttributes(WebRequest)} and
	 * {@link #cleanupAttributes(WebRequest)}.
	 *
	 * <p>
	 *  属性名称或类型是否与底层控制器中的{@code @SessionAttributes}指定的名称和类型相匹配
	 * 
	 * <p>通过此方法成功解析的属性被"记住",随后在{@link #retrieveAttributes(WebRequest))和{@link #cleanupAttributes(WebRequest))中使用}
	 * 。
	 * 
	 * 
	 * @param attributeName the attribute name to check, never {@code null}
	 * @param attributeType the type for the attribute, possibly {@code null}
	 */
	public boolean isHandlerSessionAttribute(String attributeName, Class<?> attributeType) {
		Assert.notNull(attributeName, "Attribute name must not be null");
		if (this.attributeNames.contains(attributeName) || this.attributeTypes.contains(attributeType)) {
			this.knownAttributeNames.add(attributeName);
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Store a subset of the given attributes in the session. Attributes not
	 * declared as session attributes via {@code @SessionAttributes} are ignored.
	 * <p>
	 *  在会话中存储给定属性的子集通过{@code @SessionAttributes}未声明为会话属性的属性被忽略
	 * 
	 * 
	 * @param request the current request
	 * @param attributes candidate attributes for session storage
	 */
	public void storeAttributes(WebRequest request, Map<String, ?> attributes) {
		for (String name : attributes.keySet()) {
			Object value = attributes.get(name);
			Class<?> attrType = (value != null) ? value.getClass() : null;

			if (isHandlerSessionAttribute(name, attrType)) {
				this.sessionAttributeStore.storeAttribute(request, name, value);
			}
		}
	}

	/**
	 * Retrieve "known" attributes from the session, i.e. attributes listed
	 * by name in {@code @SessionAttributes} or attributes previously stored
	 * in the model that matched by type.
	 * <p>
	 *  从会话中检索"已知"属性,即{@code @SessionAttributes}中按名称列出的属性或先前存储在模型中的类型的属性
	 * 
	 * 
	 * @param request the current request
	 * @return a map with handler session attributes, possibly empty
	 */
	public Map<String, Object> retrieveAttributes(WebRequest request) {
		Map<String, Object> attributes = new HashMap<String, Object>();
		for (String name : this.knownAttributeNames) {
			Object value = this.sessionAttributeStore.retrieveAttribute(request, name);
			if (value != null) {
				attributes.put(name, value);
			}
		}
		return attributes;
	}

	/**
	 * Remove "known" attributes from the session, i.e. attributes listed
	 * by name in {@code @SessionAttributes} or attributes previously stored
	 * in the model that matched by type.
	 * <p>
	 *  从会话中删除"已知"属性,即{@code @SessionAttributes}中的名称列出的属性或以前存储在模型中的类型的属性
	 * 
	 * 
	 * @param request the current request
	 */
	public void cleanupAttributes(WebRequest request) {
		for (String attributeName : this.knownAttributeNames) {
			this.sessionAttributeStore.cleanupAttribute(request, attributeName);
		}
	}

	/**
	 * A pass-through call to the underlying {@link SessionAttributeStore}.
	 * <p>
	 *  对底层{@link SessionAttributeStore}的传递调用
	 * 
	 * @param request the current request
	 * @param attributeName the name of the attribute of interest
	 * @return the attribute value or {@code null}
	 */
	Object retrieveAttribute(WebRequest request, String attributeName) {
		return this.sessionAttributeStore.retrieveAttribute(request, attributeName);
	}

}
