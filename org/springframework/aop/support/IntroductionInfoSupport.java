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

package org.springframework.aop.support;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.aopalliance.intercept.MethodInvocation;

import org.springframework.aop.IntroductionInfo;
import org.springframework.util.ClassUtils;

/**
 * Support for implementations of {@link org.springframework.aop.IntroductionInfo}.
 *
 * <p>Allows subclasses to conveniently add all interfaces from a given object,
 * and to suppress interfaces that should not be added. Also allows for querying
 * all introduced interfaces.
 *
 * <p>
 *  支持{@link orgspringframeworkaopIntroductionInfo}的实现
 * 
 * <p>允许子类方便地从给定对象添加所有接口,并抑制不应该添加的接口还允许查询所有引入的接口
 * 
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 */
@SuppressWarnings("serial")
public class IntroductionInfoSupport implements IntroductionInfo, Serializable {

	protected final Set<Class<?>> publishedInterfaces = new LinkedHashSet<Class<?>>();

	private transient Map<Method, Boolean> rememberedMethods = new ConcurrentHashMap<Method, Boolean>(32);


	/**
	 * Suppress the specified interface, which may have been autodetected
	 * due to the delegate implementing it. Call this method to exclude
	 * internal interfaces from being visible at the proxy level.
	 * <p>Does nothing if the interface is not implemented by the delegate.
	 * <p>
	 *  禁止指定的接口,由于委托实现它可能已被自动检测。调用此方法以排除内部接口在代理级别上不可见<p>如果接口未由代理实现,则不执行任何操作
	 * 
	 * 
	 * @param intf the interface to suppress
	 */
	public void suppressInterface(Class<?> intf) {
		this.publishedInterfaces.remove(intf);
	}

	@Override
	public Class<?>[] getInterfaces() {
		return this.publishedInterfaces.toArray(new Class<?>[this.publishedInterfaces.size()]);
	}

	/**
	 * Check whether the specified interfaces is a published introduction interface.
	 * <p>
	 *  检查指定的接口是否是已发布的介绍界面
	 * 
	 * 
	 * @param ifc the interface to check
	 * @return whether the interface is part of this introduction
	 */
	public boolean implementsInterface(Class<?> ifc) {
		for (Class<?> pubIfc : this.publishedInterfaces) {
			if (ifc.isInterface() && ifc.isAssignableFrom(pubIfc)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Publish all interfaces that the given delegate implements at the proxy level.
	 * <p>
	 *  发布给定委托在代理级实现的所有接口
	 * 
	 * 
	 * @param delegate the delegate object
	 */
	protected void implementInterfacesOnObject(Object delegate) {
		this.publishedInterfaces.addAll(ClassUtils.getAllInterfacesAsSet(delegate));
	}

	/**
	 * Is this method on an introduced interface?
	 * <p>
	 *  这种方法在介绍的界面上?
	 * 
	 * 
	 * @param mi the method invocation
	 * @return whether the invoked method is on an introduced interface
	 */
	protected final boolean isMethodOnIntroducedInterface(MethodInvocation mi) {
		Boolean rememberedResult = this.rememberedMethods.get(mi.getMethod());
		if (rememberedResult != null) {
			return rememberedResult;
		}
		else {
			// Work it out and cache it.
			boolean result = implementsInterface(mi.getMethod().getDeclaringClass());
			this.rememberedMethods.put(mi.getMethod(), result);
			return result;
		}
	}


	//---------------------------------------------------------------------
	// Serialization support
	//---------------------------------------------------------------------

	/**
	 * This method is implemented only to restore the logger.
	 * We don't make the logger static as that would mean that subclasses
	 * would use this class's log category.
	 * <p>
	 * 这种方法仅用于恢复记录器我们不会使记录器静态,因为这意味着子类将使用此类的日志类别
	 */
	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
		// Rely on default serialization; just initialize state after deserialization.
		ois.defaultReadObject();
		// Initialize transient fields.
		this.rememberedMethods = new ConcurrentHashMap<Method, Boolean>(32);
	}

}
