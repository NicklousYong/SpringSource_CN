/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2008 the original author or authors.
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

package org.springframework.remoting;

import org.springframework.core.NestedRuntimeException;

/**
 * Generic remote access exception. A service proxy for any remoting
 * protocol should throw this exception or subclasses of it, in order
 * to transparently expose a plain Java business interface.
 *
 * <p>When using conforming proxies, switching the actual remoting protocol
 * e.g. from Hessian to Burlap does not affect client code. Clients work
 * with a plain natural Java business interface that the service exposes.
 * A client object simply receives an implementation for the interface that
 * it needs via a bean reference, like it does for a local bean as well.
 *
 * <p>A client may catch RemoteAccessException if it wants to, but as
 * remote access errors are typically unrecoverable, it will probably let
 * such exceptions propagate to a higher level that handles them generically.
 * In this case, the client code doesn't show any signs of being involved in
 * remote access, as there aren't any remoting-specific dependencies.
 *
 * <p>Even when switching from a remote service proxy to a local implementation
 * of the same interface, this amounts to just a matter of configuration. Obviously,
 * the client code should be somewhat aware that it <i>might be working</i>
 * against a remote service, for example in terms of repeated method calls that
 * cause unnecessary roundtrips etc. However, it doesn't have to be aware whether
 * it is <i>actually working</i> against a remote service or a local implementation,
 * or with which remoting protocol it is working under the hood.
 *
 * <p>
 *  通用远程访问异常任何远程处理协议的服务代理应抛出该异常或其子类,以便透明地暴露一个普通的Java业务界面
 * 
 * <p>当使用一致的代理时,将实际的远程处理协议(例如从Hessian切换到Burlap)不会影响客户端代码客户端使用服务公开的普通天然Java业务接口工作客户端对象只需接收所需接口的实现一个bean引用
 * ,就像它对本地bean一样。
 * 
 *  客户端可能会捕获RemoteAccessException,但是由于远程访问错误通常是不可恢复的,所以它可能会将这种异常传播到更高级别来处理它们在这种情况下,客户端代码不会显示任何标志参与远程访问,因
 * 为没有任何远程特定的依赖关系。
 * 
 * 即使从远程服务代理切换到同一接口的本地实现,这相当于配置的问题显然,客户端代码应该有点意识到它可能正在工作</i>远程服务,例如重复的方法调用,导致不必要的roundtrips等。
 * 
 * @author Juergen Hoeller
 * @since 14.05.2003
 */
public class RemoteAccessException extends NestedRuntimeException {

	/** Use serialVersionUID from Spring 1.2 for interoperability */
	private static final long serialVersionUID = -4906825139312227864L;


	/**
	 * Constructor for RemoteAccessException.
	 * <p>
	 * 然而,它不必知道是否实际工作在远程服务或本地实现,或与哪个远程协议是在引擎盖下工作的。
	 * 
	 * 
	 * @param msg the detail message
	 */
	public RemoteAccessException(String msg) {
		super(msg);
	}

	/**
	 * Constructor for RemoteAccessException.
	 * <p>
	 *  RemoteAccessException的构造方法
	 * 
	 * 
	 * @param msg the detail message
	 * @param cause the root cause (usually from using an underlying
	 * remoting API such as RMI)
	 */
	public RemoteAccessException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
