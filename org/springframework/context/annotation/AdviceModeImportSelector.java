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

package org.springframework.context.annotation;

import java.lang.annotation.Annotation;

import org.springframework.core.GenericTypeResolver;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

 /**
 * Convenient base class for {@link ImportSelector} implementations that select imports
 * based on an {@link AdviceMode} value from an annotation (such as the {@code @Enable*}
 * annotations).
 *
 * <p>
 *  {@link ImportSelector}实现的便利基类,根据注释(例如{@code @ Enable *}注释)的{@link AdviceMode}值选择导入)
 * 
 * 
 * @author Chris Beams
 * @since 3.1
 * @param <A> annotation containing {@linkplain #getAdviceModeAttributeName() AdviceMode attribute}
 */
public abstract class AdviceModeImportSelector<A extends Annotation> implements ImportSelector {

	public static final String DEFAULT_ADVICE_MODE_ATTRIBUTE_NAME = "mode";


	/**
	 * The name of the {@link AdviceMode} attribute for the annotation specified by the
	 * generic type {@code A}. The default is {@value #DEFAULT_ADVICE_MODE_ATTRIBUTE_NAME},
	 * but subclasses may override in order to customize.
	 * <p>
	 * 通用类型{@code A}指定的注释的{@link AdviceMode}属性的名称默认值为{@value #DEFAULT_ADVICE_MODE_ATTRIBUTE_NAME},但子类可能会覆盖,以
	 * 便自定义。
	 * 
	 */
	protected String getAdviceModeAttributeName() {
		return DEFAULT_ADVICE_MODE_ATTRIBUTE_NAME;
	}

	/**
	 * This implementation resolves the type of annotation from generic metadata and
	 * validates that (a) the annotation is in fact present on the importing
	 * {@code @Configuration} class and (b) that the given annotation has an
	 * {@linkplain #getAdviceModeAttributeName() advice mode attribute} of type
	 * {@link AdviceMode}.
	 * <p>The {@link #selectImports(AdviceMode)} method is then invoked, allowing the
	 * concrete implementation to choose imports in a safe and convenient fashion.
	 * <p>
	 *  该实现解决了通用元数据的注释类型,并验证(a)注释实际上存在于导入{@code @Configuration}类和(b)给定的注释具有{@linkplain #getAdviceModeAttributeName()建议模式属性}
	 *  {@link AdviceMode} <p>然后调用{@link #selectImports(AdviceMode)}方法,允许具体实现以安全方便的方式选择导入。
	 * 
	 * 
	 * @throws IllegalArgumentException if expected annotation {@code A} is not present
	 * on the importing {@code @Configuration} class or if {@link #selectImports(AdviceMode)}
	 * returns {@code null}
	 */
	@Override
	public final String[] selectImports(AnnotationMetadata importingClassMetadata) {
		Class<?> annoType = GenericTypeResolver.resolveTypeArgument(getClass(), AdviceModeImportSelector.class);
		AnnotationAttributes attributes = AnnotationConfigUtils.attributesFor(importingClassMetadata, annoType);
		if (attributes == null) {
			throw new IllegalArgumentException(String.format(
				"@%s is not present on importing class '%s' as expected",
				annoType.getSimpleName(), importingClassMetadata.getClassName()));
		}

		AdviceMode adviceMode = attributes.getEnum(this.getAdviceModeAttributeName());
		String[] imports = selectImports(adviceMode);
		if (imports == null) {
			throw new IllegalArgumentException(String.format("Unknown AdviceMode: '%s'", adviceMode));
		}
		return imports;
	}

	/**
	 * Determine which classes should be imported based on the given {@code AdviceMode}.
	 * <p>Returning {@code null} from this method indicates that the {@code AdviceMode} could
	 * not be handled or was unknown and that an {@code IllegalArgumentException} should
	 * be thrown.
	 * <p>
	 * 根据给定的{@code AdviceMode}确定哪些类应该导入?从此方法返回{@code null}表示{@code AdviceMode}无法处理或未知,并且{@code IllegalArgumentException}
	 * 应该抛出。
	 * 
	 * @param adviceMode the value of the {@linkplain #getAdviceModeAttributeName()
	 * advice mode attribute} for the annotation specified via generics.
	 * @return array containing classes to import; empty array if none, {@code null} if
	 * the given {@code AdviceMode} is unknown.
	 */
	protected abstract String[] selectImports(AdviceMode adviceMode);

}
