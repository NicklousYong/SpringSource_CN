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

package org.springframework.beans.factory.parsing;

import org.springframework.beans.BeanMetadataElement;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanReference;

/**
 * Interface that describes the logical view of a set of {@link BeanDefinition BeanDefinitions}
 * and {@link BeanReference BeanReferences} as presented in some configuration context.
 *
 * <p>With the introduction of {@link org.springframework.beans.factory.xml.NamespaceHandler pluggable custom XML tags},
 * it is now possible for a single logical configuration entity, in this case an XML tag, to
 * create multiple {@link BeanDefinition BeanDefinitions} and {@link BeanReference RuntimeBeanReferences}
 * in order to provide more succinct configuration and greater convenience to end users. As such, it can
 * no longer be assumed that each configuration entity (e.g. XML tag) maps to one {@link BeanDefinition}.
 * For tool vendors and other users who wish to present visualization or support for configuring Spring
 * applications it is important that there is some mechanism in place to tie the {@link BeanDefinition BeanDefinitions}
 * in the {@link org.springframework.beans.factory.BeanFactory} back to the configuration data in a way
 * that has concrete meaning to the end user. As such, {@link org.springframework.beans.factory.xml.NamespaceHandler}
 * implementations are able to publish events in the form of a {@code ComponentDefinition} for each
 * logical entity being configured. Third parties can then {@link ReaderEventListener subscribe to these events},
 * allowing for a user-centric view of the bean metadata.
 *
 * <p>Each {@code ComponentDefinition} has a {@link #getSource source object} which is configuration-specific.
 * In the case of XML-based configuration this is typically the {@link org.w3c.dom.Node} which contains the user
 * supplied configuration information. In addition to this, each {@link BeanDefinition} enclosed in a
 * {@code ComponentDefinition} has its own {@link BeanDefinition#getSource() source object} which may point
 * to a different, more specific, set of configuration data. Beyond this, individual pieces of bean metadata such
 * as the {@link org.springframework.beans.PropertyValue PropertyValues} may also have a source object giving an
 * even greater level of detail. Source object extraction is handled through the
 * {@link SourceExtractor} which can be customized as required.
 *
 * <p>Whilst direct access to important {@link BeanReference BeanReferences} is provided through
 * {@link #getBeanReferences}, tools may wish to inspect all {@link BeanDefinition BeanDefinitions} to gather
 * the full set of {@link BeanReference BeanReferences}. Implementations are required to provide
 * all {@link BeanReference BeanReferences} that are required to validate the configuration of the
 * overall logical entity as well as those required to provide full user visualisation of the configuration.
 * It is expected that certain {@link BeanReference BeanReferences} will not be important to
 * validation or to the user view of the configuration and as such these may be ommitted. A tool may wish to
 * display any additional {@link BeanReference BeanReferences} sourced through the supplied
 * {@link BeanDefinition BeanDefinitions} but this is not considered to be a typical case.
 *
 * <p>Tools can determine the important of contained {@link BeanDefinition BeanDefinitions} by checking the
 * {@link BeanDefinition#getRole role identifier}. The role is essentially a hint to the tool as to how
 * important the configuration provider believes a {@link BeanDefinition} is to the end user. It is expected
 * that tools will <strong>not</strong> display all {@link BeanDefinition BeanDefinitions} for a given
 * {@code ComponentDefinition} choosing instead to filter based on the role. Tools may choose to make
 * this filtering user configurable. Particular notice should be given to the
 * {@link BeanDefinition#ROLE_INFRASTRUCTURE INFRASTRUCTURE role identifier}. {@link BeanDefinition BeanDefinitions}
 * classified with this role are completely unimportant to the end user and are required only for
 * internal implementation reasons.
 *
 * <p>
 *  描述在一些配置上下文中呈现的一组{@link BeanDefinition BeanDefinitions}和{@link BeanReference BeanReferences}的逻辑视图的界面。
 * 
 * 通过引入{@link orgspringframeworkbeansfactoryxmlNamespaceHandler可插入的自定义XML标签},现在可以使单个逻辑配置实体(在本例中为XML标签)创建
 * 多个{@link BeanDefinition BeanDefinitions}和{@link BeanReference RuntimeBeanReferences }以便为终端用户提供更简洁的配置和
 * 更大的便利。
 * 因此,不能再假定每个配置实体(例如XML标签)映射到一个{@link BeanDefinition}对于希望呈现的工具供应商和其他用户可视化或支持配置Spring应用程序,重要的是有一些机制将{@link BeanDefinition BeanDefinitions}
 * 绑定到{@link orgspringframeworkbeansfactoryBeanFactory}以对终端用户有具体含义的方式返回到配置数据。
 * 因此,{@link orgspringframeworkbeansfactoryxmlNamespaceHandler}实现能够为正在配置的每个逻辑实体以{@code ComponentDefinition}
 * 的形式发布事件第三方然后可以{@link ReaderEventListener订阅这些事件},允许以bean为中心的bean元数据视图。
 * 
 * <p>每个{@code ComponentDefinition}都有一个{@link #getSource源对象},这是特定于配置的。
 * 在基于XML的配置的情况下,这通常是包含用户提供的配置信息的{@link orgw3cdomNode}另外为此,{@code ComponentDefinition}中包含的每个{@link BeanDefinition}
 * 都有自己的{@link BeanDefinition#getSource()源对象},可以指向不同的,更具体的一组配置数据除此之外,各个部分的bean元数据(如{@link orgspringframeworkbeansPropertyValue PropertyValues}
 * )也可能具有提供更高级别细节的源对象源对象提取通过{@link SourceExtractor}处理,可以根据需要进行自定义。
 * <p>每个{@code ComponentDefinition}都有一个{@link #getSource源对象},这是特定于配置的。
 * 
 * <p>尽管通过{@link #getBeanReferences}可以直接访问重要的{@link BeanReference BeanReferences},但工具可能希望检查所有{@link BeanDefinition BeanDefinitions}
 * 以收集完整的{@link BeanReference BeanReferences}实现集需要提供验证整个逻辑实体的配置所需的所有{@link BeanReference BeanReferences}
 * 以及提供配置的完全用户可视化所需的配置。
 * 预期某些{@link BeanReference BeanReferences}将不重要验证或用户视图的配置,因此这些可能被忽略一个工具可能希望显示通过提供的{@link BeanDefinition BeanDefinitions}
 * 提供的任何附加的{@link BeanReference BeanReference},但这不被认为是典型的例子。
 * 
 * <p>工具可以通过检查{@link BeanDefinition#getRole角色标识符}来确定包含的{@link BeanDefinition BeanDefinitions}的重要性。
 * 该角色本质上是该工具的一个提示,关于配置提供者相信一个{@link BeanDefinition }是最终用户希望工具将<strong>不</strong>显示给定{@code ComponentDefinition}
 * 
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @since 2.0
 * @see AbstractComponentDefinition
 * @see CompositeComponentDefinition
 * @see BeanComponentDefinition
 * @see ReaderEventListener#componentRegistered(ComponentDefinition)
 */
public interface ComponentDefinition extends BeanMetadataElement {

	/**
	 * Get the user-visible name of this {@code ComponentDefinition}.
	 * <p>This should link back directly to the corresponding configuration data
	 * for this component in a given context.
	 * <p>
	 * 的所有{@link BeanDefinition BeanDefinitions},而不是根据工具可能选择的角色进行过滤过滤用户可配置特殊通知应给予{@link BeanDefinition#ROLE_INFRASTRUCTURE基础设施角色标识符}
	 * 根据此角色分类的{@link BeanDefinition BeanDefinitions}对于最终用户是完全不重要的,仅在内部实现原因时才需要。
	 * <p>工具可以通过检查{@link BeanDefinition#getRole角色标识符}来确定包含的{@link BeanDefinition BeanDefinitions}的重要性。
	 * 
	 */
	String getName();

	/**
	 * Return a friendly description of the described component.
	 * <p>Implementations are encouraged to return the same value from
	 * {@code toString()}.
	 * <p>
	 * 获取此{{@code ComponentDefinition} <p>的用户可见名称。这将直接链接到给定上下文中此组件的相应配置数据
	 * 
	 */
	String getDescription();

	/**
	 * Return the {@link BeanDefinition BeanDefinitions} that were registered
	 * to form this {@code ComponentDefinition}.
	 * <p>It should be noted that a {@code ComponentDefinition} may well be related with
	 * other {@link BeanDefinition BeanDefinitions} via {@link BeanReference references},
	 * however these are <strong>not</strong> included as they may be not available immediately.
	 * Important {@link BeanReference BeanReferences} are available from {@link #getBeanReferences()}.
	 * <p>
	 *  返回描述的组件的友好描述<p>鼓励实现从{@code toString()}返回相同的值
	 * 
	 * 
	 * @return the array of BeanDefinitions, or an empty array if none
	 */
	BeanDefinition[] getBeanDefinitions();

	/**
	 * Return the {@link BeanDefinition BeanDefinitions} that represent all relevant
	 * inner beans within this component.
	 * <p>Other inner beans may exist within the associated {@link BeanDefinition BeanDefinitions},
	 * however these are not considered to be needed for validation or for user visualization.
	 * <p>
	 *  返回已经注册以形成此{@code ComponentDefinition} <p>的{@link BeanDefinition BeanDefinitions}应该注意的是,{@code ComponentDefinition}
	 * 可能通过{@link BeanReference与其他{@link BeanDefinition BeanDefinitions}相关联引用},但是这些<strong>不是</strong>,因为它们可
	 * 能不可用重要{@link BeanReference BeanReferences}可从{@link #getBeanReferences()}获得。
	 * 
	 * 
	 * @return the array of BeanDefinitions, or an empty array if none
	 */
	BeanDefinition[] getInnerBeanDefinitions();

	/**
	 * Return the set of {@link BeanReference BeanReferences} that are considered
	 * to be important to this {@code ComponentDefinition}.
	 * <p>Other {@link BeanReference BeanReferences} may exist within the associated
	 * {@link BeanDefinition BeanDefinitions}, however these are not considered
	 * to be needed for validation or for user visualization.
	 * <p>
	 * 返回代表此组件中所有相关内部bean的{@link BeanDefinition BeanDefinitions} <p>其他内部bean可能存在于关联的{@link BeanDefinition BeanDefinitions}
	 * 中,但这些不被认为是验证或用户可视化所需的。
	 * 
	 * 
	 * @return the array of BeanReferences, or an empty array if none
	 */
	BeanReference[] getBeanReferences();

}
