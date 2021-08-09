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

package org.springframework.aop.framework;

/**
 * Interface to be implemented by factories that are able to create
 * AOP proxies based on {@link AdvisedSupport} configuration objects.
 *
 * <p>Proxies should observe the following contract:
 * <ul>
 * <li>They should implement all interfaces that the configuration
 * indicates should be proxied.
 * <li>They should implement the {@link Advised} interface.
 * <li>They should implement the equals method to compare proxied
 * interfaces, advice, and target.
 * <li>They should be serializable if all advisors and target
 * are serializable.
 * <li>They should be thread-safe if advisors and target
 * are thread-safe.
 * </ul>
 *
 * <p>Proxies may or may not allow advice changes to be made.
 * If they do not permit advice changes (for example, because
 * the configuration was frozen) a proxy should throw an
 * {@link AopConfigException} on an attempted advice change.
 *
 * <p>
 *  可由根据{@link AdvisedSupport}配置对象创建AOP代理的工厂实现的接口
 * 
 *  代理人应遵守以下合同：
 * <ul>
 * 他们应该实现配置指示应该被代理的所有接口<li>他们应该实现{@link Advised}界面<li>他们应该实现equals方法来比较代理接口,建议和目标<li>他们如果所有顾问和目标都是可序列化的,
 * 则应该是可序列化的。
 * <li>如果顾问和目标是线程安全的,则应该是线程安全的。
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 */
public interface AopProxyFactory {

	/**
	 * Create an {@link AopProxy} for the given AOP configuration.
	 * <p>
	 * </ul>
	 * 
	 *  代理可能会也可能不会允许建议更改如果不允许建议更改(例如,因为配置被冻结),代理应该在尝试的建议更改时抛出一个{@link AopConfigException}
	 * 
	 * 
	 * @param config the AOP configuration in the form of an
	 * AdvisedSupport object
	 * @return the corresponding AOP proxy
	 * @throws AopConfigException if the configuration is invalid
	 */
	AopProxy createAopProxy(AdvisedSupport config) throws AopConfigException;

}
