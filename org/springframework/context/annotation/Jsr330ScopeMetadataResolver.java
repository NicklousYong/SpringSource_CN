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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;

/**
 * Simple {@link ScopeMetadataResolver} implementation that follows JSR-330 scoping rules:
 * defaulting to prototype scope unless {@link javax.inject.Singleton} is present.
 *
 * <p>This scope resolver can be used with {@link ClassPathBeanDefinitionScanner} and
 * {@link AnnotatedBeanDefinitionReader} for standard JSR-330 compliance. However,
 * in practice, you will typically use Spring's rich default scoping instead - or extend
 * this resolver with custom scoping annotations that point to extended Spring scopes.
 *
 * <p>
 *  遵循JSR-330范围规则的简单{@link ScopeMetadataResolver}实现：默认为原型范围,除非存在{@link javaxinjectSingleton}
 * 
 * <p>此范围解析器可以与标准JSR-330合规的{@link ClassPathBeanDefinitionScanner}和{@link AnnotatedBeanDefinitionReader}一
 * 起使用。
 * 但实际上,通常会使用Spring的丰富的默认范围,或者使用自定义的范围注释扩展此解析器那就是扩展Spring范围。
 * 
 * 
 * @author Juergen Hoeller
 * @since 3.0
 * @see #registerScope
 * @see #resolveScopeName
 * @see ClassPathBeanDefinitionScanner#setScopeMetadataResolver
 * @see AnnotatedBeanDefinitionReader#setScopeMetadataResolver
 */
public class Jsr330ScopeMetadataResolver implements ScopeMetadataResolver {

	private final Map<String, String> scopeMap = new HashMap<String, String>();


	public Jsr330ScopeMetadataResolver() {
		registerScope("javax.inject.Singleton", BeanDefinition.SCOPE_SINGLETON);
	}


	/**
	 * Register an extended JSR-330 scope annotation, mapping it onto a
	 * specific Spring scope by name.
	 * <p>
	 *  注册一个扩展的JSR-330作用域注释,通过名称映射到一个特定的Spring范围
	 * 
	 * 
	 * @param annotationType the JSR-330 annotation type as a Class
	 * @param scopeName the Spring scope name
	 */
	public final void registerScope(Class<?> annotationType, String scopeName) {
		this.scopeMap.put(annotationType.getName(), scopeName);
	}

	/**
	 * Register an extended JSR-330 scope annotation, mapping it onto a
	 * specific Spring scope by name.
	 * <p>
	 *  注册一个扩展的JSR-330作用域注释,通过名称映射到一个特定的Spring范围
	 * 
	 * 
	 * @param annotationType the JSR-330 annotation type by name
	 * @param scopeName the Spring scope name
	 */
	public final void registerScope(String annotationType, String scopeName) {
		this.scopeMap.put(annotationType, scopeName);
	}

	/**
	 * Resolve the given annotation type into a named Spring scope.
	 * <p>The default implementation simply checks against registered scopes.
	 * Can be overridden for custom mapping rules, e.g. naming conventions.
	 * <p>
	 *  将给定的注释类型解析为一个命名的Spring范围<p>默认实现简单地检查注册的范围可以覆盖自定义映射规则,例如命名约定
	 * 
	 * @param annotationType the JSR-330 annotation type
	 * @return the Spring scope name
	 */
	protected String resolveScopeName(String annotationType) {
		return this.scopeMap.get(annotationType);
	}


	@Override
	public ScopeMetadata resolveScopeMetadata(BeanDefinition definition) {
		ScopeMetadata metadata = new ScopeMetadata();
		metadata.setScopeName(BeanDefinition.SCOPE_PROTOTYPE);
		if (definition instanceof AnnotatedBeanDefinition) {
			AnnotatedBeanDefinition annDef = (AnnotatedBeanDefinition) definition;
			Set<String> annTypes = annDef.getMetadata().getAnnotationTypes();
			String found = null;
			for (String annType : annTypes) {
				Set<String> metaAnns = annDef.getMetadata().getMetaAnnotationTypes(annType);
				if (metaAnns.contains("javax.inject.Scope")) {
					if (found != null) {
						throw new IllegalStateException("Found ambiguous scope annotations on bean class [" +
								definition.getBeanClassName() + "]: " + found + ", " + annType);
					}
					found = annType;
					String scopeName = resolveScopeName(annType);
					if (scopeName == null) {
						throw new IllegalStateException(
								"Unsupported scope annotation - not mapped onto Spring scope name: " + annType);
					}
					metadata.setScopeName(scopeName);
				}
			}
		}
		return metadata;
	}

}
