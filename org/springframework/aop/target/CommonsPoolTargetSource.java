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

package org.springframework.aop.target;

import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;

import org.springframework.beans.BeansException;
import org.springframework.core.Constants;

/**
 * {@link org.springframework.aop.TargetSource} implementation that holds
 * objects in a configurable Apache Commons Pool.
 *
 * <p>By default, an instance of {@code GenericObjectPool} is created.
 * Subclasses may change the type of {@code ObjectPool} used by
 * overriding the {@code createObjectPool()} method.
 *
 * <p>Provides many configuration properties mirroring those of the Commons Pool
 * {@code GenericObjectPool} class; these properties are passed to the
 * {@code GenericObjectPool} during construction. If creating a subclass of this
 * class to change the {@code ObjectPool} implementation type, pass in the values
 * of configuration properties that are relevant to your chosen implementation.
 *
 * <p>The {@code testOnBorrow}, {@code testOnReturn} and {@code testWhileIdle}
 * properties are explicitly not mirrored because the implementation of
 * {@code PoolableObjectFactory} used by this class does not implement
 * meaningful validation. All exposed Commons Pool properties use the
 * corresponding Commons Pool defaults.
 *
 * <p>Compatible with Apache Commons Pool 1.5.x and 1.6.
 * Note that this class doesn't declare Commons Pool 1.6's generic type
 * in order to remain compatible with Commons Pool 1.5.x at runtime.
 *
 * <p>
 *  {@link orgspringframeworkaopTargetSource}实现,在可配置的Apache Commons池中保存对象
 * 
 * <p>默认情况下,创建{@code GenericObjectPool}的实例子类可以通过覆盖{@code createObjectPool()}方法来更改所使用的{@code ObjectPool}的
 * 类型。
 * 
 *  <p>提供许多配置属性镜像Commons Pool {@code GenericObjectPool}类的配置属性;这些属性在构造期间传递给{@code GenericObjectPool}如果创建此
 * 类的子类来更改{@code ObjectPool}实现类型,请传递与您选择的实现相关的配置属性值。
 * 
 * <p> {@code testOnBorrow},{@code testOnReturn}和{@code testWhileIdle}属性显式不镜像,因为此类使用的{@code PoolableObjectFactory}
 * 的实现不能实现有意义的验证所有公开的Commons Pool属性使用相应的Commons Pool默认值。
 * 
 *  <p>与Apache Commons Pool 15x和16兼容请注意,此类不会声明Commons Pool 16的通用类型,以便在运行时与Commons Pool 15x保持兼容
 * 
 * 
 * @author Rod Johnson
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @see GenericObjectPool
 * @see #createObjectPool()
 * @see #setMaxSize
 * @see #setMaxIdle
 * @see #setMinIdle
 * @see #setMaxWait
 * @see #setTimeBetweenEvictionRunsMillis
 * @see #setMinEvictableIdleTimeMillis
 * @deprecated as of Spring 4.2, in favor of {@link CommonsPool2TargetSource}
 */
@SuppressWarnings({"rawtypes", "unchecked", "serial"})
@Deprecated
public class CommonsPoolTargetSource extends AbstractPoolingTargetSource implements PoolableObjectFactory {

	private static final Constants constants = new Constants(GenericObjectPool.class);


	private int maxIdle = GenericObjectPool.DEFAULT_MAX_IDLE;

	private int minIdle = GenericObjectPool.DEFAULT_MIN_IDLE;

	private long maxWait = GenericObjectPool.DEFAULT_MAX_WAIT;

	private long timeBetweenEvictionRunsMillis = GenericObjectPool.DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS;

	private long minEvictableIdleTimeMillis = GenericObjectPool.DEFAULT_MIN_EVICTABLE_IDLE_TIME_MILLIS;

	private byte whenExhaustedAction = GenericObjectPool.DEFAULT_WHEN_EXHAUSTED_ACTION;

	/**
	 * The Apache Commons {@code ObjectPool} used to pool target objects
	 * <p>
	 *  Apache Commons {@code ObjectPool}用于汇集目标对象
	 * 
	 */
	private ObjectPool pool;


	/**
	 * Create a CommonsPoolTargetSource with default settings.
	 * Default maximum size of the pool is 8.
	 * <p>
	 *  使用默认设置创建CommonsPoolTargetSource池的默认最大大小为8
	 * 
	 * 
	 * @see #setMaxSize
	 * @see GenericObjectPool#setMaxActive
	 */
	public CommonsPoolTargetSource() {
		setMaxSize(GenericObjectPool.DEFAULT_MAX_ACTIVE);
	}

	/**
	 * Set the maximum number of idle objects in the pool.
	 * Default is 8.
	 * <p>
	 *  设置池中空闲对象的最大数目默认值为8
	 * 
	 * 
	 * @see GenericObjectPool#setMaxIdle
	 */
	public void setMaxIdle(int maxIdle) {
		this.maxIdle = maxIdle;
	}

	/**
	 * Return the maximum number of idle objects in the pool.
	 * <p>
	 *  返回池中空闲对象的最大数量
	 * 
	 */
	public int getMaxIdle() {
		return this.maxIdle;
	}

	/**
	 * Set the minimum number of idle objects in the pool.
	 * Default is 0.
	 * <p>
	 * 设置池中空闲对象的最小数量默认为0
	 * 
	 * 
	 * @see GenericObjectPool#setMinIdle
	 */
	public void setMinIdle(int minIdle) {
		this.minIdle = minIdle;
	}

	/**
	 * Return the minimum number of idle objects in the pool.
	 * <p>
	 *  返回池中空闲对象的最小数量
	 * 
	 */
	public int getMinIdle() {
		return this.minIdle;
	}

	/**
	 * Set the maximum waiting time for fetching an object from the pool.
	 * Default is -1, waiting forever.
	 * <p>
	 *  设置从池中获取对象的最大等待时间默认值为-1,永远等待
	 * 
	 * 
	 * @see GenericObjectPool#setMaxWait
	 */
	public void setMaxWait(long maxWait) {
		this.maxWait = maxWait;
	}

	/**
	 * Return the maximum waiting time for fetching an object from the pool.
	 * <p>
	 *  返回从池中获取对象的最大等待时间
	 * 
	 */
	public long getMaxWait() {
		return this.maxWait;
	}

	/**
	 * Set the time between eviction runs that check idle objects whether
	 * they have been idle for too long or have become invalid.
	 * Default is -1, not performing any eviction.
	 * <p>
	 *  设置检查空闲对象的驱逐运行之间的时间,无论它们是否空闲时间过长或已变为无效默认值为-1,不执行任何驱逐
	 * 
	 * 
	 * @see GenericObjectPool#setTimeBetweenEvictionRunsMillis
	 */
	public void setTimeBetweenEvictionRunsMillis(long timeBetweenEvictionRunsMillis) {
		this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
	}

	/**
	 * Return the time between eviction runs that check idle objects.
	 * <p>
	 *  返回检查空闲对象的驱逐运行之间的时间
	 * 
	 */
	public long getTimeBetweenEvictionRunsMillis() {
		return this.timeBetweenEvictionRunsMillis;
	}

	/**
	 * Set the minimum time that an idle object can sit in the pool before
	 * it becomes subject to eviction. Default is 1800000 (30 minutes).
	 * <p>Note that eviction runs need to be performed to take this
	 * setting into effect.
	 * <p>
	 *  设置空闲对象可以在池中被放置之前可以坐在的最小时间默认值为1800000(30分钟)<p>请注意,需要执行逐出运行才能使此设置生效
	 * 
	 * 
	 * @see #setTimeBetweenEvictionRunsMillis
	 * @see GenericObjectPool#setMinEvictableIdleTimeMillis
	 */
	public void setMinEvictableIdleTimeMillis(long minEvictableIdleTimeMillis) {
		this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
	}

	/**
	 * Return the minimum time that an idle object can sit in the pool.
	 * <p>
	 * 返回空闲对象可以坐在池中的最短时间
	 * 
	 */
	public long getMinEvictableIdleTimeMillis() {
		return this.minEvictableIdleTimeMillis;
	}

	/**
	 * Set the action to take when the pool is exhausted. Uses the
	 * constant names defined in Commons Pool's GenericObjectPool class:
	 * "WHEN_EXHAUSTED_BLOCK", "WHEN_EXHAUSTED_FAIL", "WHEN_EXHAUSTED_GROW".
	 * <p>
	 *  设置池耗尽时采取的操作使用Commons Pool的GenericObjectPool类中定义的常量名称："WHEN_EXHAUSTED_BLOCK","WHEN_EXHAUSTED_FAIL","W
	 * HEN_EXHAUSTED_GROW"。
	 * 
	 * 
	 * @see #setWhenExhaustedAction(byte)
	 */
	public void setWhenExhaustedActionName(String whenExhaustedActionName) {
		setWhenExhaustedAction(constants.asNumber(whenExhaustedActionName).byteValue());
	}

	/**
	 * Set the action to take when the pool is exhausted. Uses the
	 * constant values defined in Commons Pool's GenericObjectPool class.
	 * <p>
	 *  设置池耗尽时采取的操作使用Commons Pool的GenericObjectPool类中定义的常量值
	 * 
	 * 
	 * @see GenericObjectPool#setWhenExhaustedAction(byte)
	 * @see GenericObjectPool#WHEN_EXHAUSTED_BLOCK
	 * @see GenericObjectPool#WHEN_EXHAUSTED_FAIL
	 * @see GenericObjectPool#WHEN_EXHAUSTED_GROW
	 */
	public void setWhenExhaustedAction(byte whenExhaustedAction) {
		this.whenExhaustedAction = whenExhaustedAction;
	}

	/**
	 * Return the action to take when the pool is exhausted.
	 * <p>
	 *  当游泳池耗尽时,返回采取的行动
	 * 
	 */
	public byte getWhenExhaustedAction() {
		return whenExhaustedAction;
	}


	/**
	 * Creates and holds an ObjectPool instance.
	 * <p>
	 *  创建并保存一个ObjectPool实例
	 * 
	 * 
	 * @see #createObjectPool()
	 */
	@Override
	protected final void createPool() {
		logger.debug("Creating Commons object pool");
		this.pool = createObjectPool();
	}

	/**
	 * Subclasses can override this if they want to return a specific Commons pool.
	 * They should apply any configuration properties to the pool here.
	 * <p>Default is a GenericObjectPool instance with the given pool size.
	 * <p>
	 *  子类可以重写这个,如果他们想要返回一个特定的Commons池他们应该应用任何配置属性到这里池<p>默认是一个GenericObjectPool实例与给定的池大小
	 * 
	 * 
	 * @return an empty Commons {@code ObjectPool}.
	 * @see org.apache.commons.pool.impl.GenericObjectPool
	 * @see #setMaxSize
	 */
	protected ObjectPool createObjectPool() {
		GenericObjectPool gop = new GenericObjectPool(this);
		gop.setMaxActive(getMaxSize());
		gop.setMaxIdle(getMaxIdle());
		gop.setMinIdle(getMinIdle());
		gop.setMaxWait(getMaxWait());
		gop.setTimeBetweenEvictionRunsMillis(getTimeBetweenEvictionRunsMillis());
		gop.setMinEvictableIdleTimeMillis(getMinEvictableIdleTimeMillis());
		gop.setWhenExhaustedAction(getWhenExhaustedAction());
		return gop;
	}


	/**
	 * Borrow an object from the {@code ObjectPool}.
	 * <p>
	 *  从{@code ObjectPool}中借用一个对象
	 * 
	 */
	@Override
	public Object getTarget() throws Exception {
		return this.pool.borrowObject();
	}

	/**
	 * Returns the specified object to the underlying {@code ObjectPool}.
	 * <p>
	 * 将指定的对象返回给底层{@code ObjectPool}
	 * 
	 */
	@Override
	public void releaseTarget(Object target) throws Exception {
		this.pool.returnObject(target);
	}

	@Override
	public int getActiveCount() throws UnsupportedOperationException {
		return this.pool.getNumActive();
	}

	@Override
	public int getIdleCount() throws UnsupportedOperationException {
		return this.pool.getNumIdle();
	}


	/**
	 * Closes the underlying {@code ObjectPool} when destroying this object.
	 * <p>
	 *  在销毁此对象时关闭底层的{@code ObjectPool}
	 */
	@Override
	public void destroy() throws Exception {
		logger.debug("Closing Commons ObjectPool");
		this.pool.close();
	}


	//----------------------------------------------------------------------------
	// Implementation of org.apache.commons.pool.PoolableObjectFactory interface
	//----------------------------------------------------------------------------

	@Override
	public Object makeObject() throws BeansException {
		return newPrototypeInstance();
	}

	@Override
	public void destroyObject(Object obj) throws Exception {
		destroyPrototypeInstance(obj);
	}

	@Override
	public boolean validateObject(Object obj) {
		return true;
	}

	@Override
	public void activateObject(Object obj) {
	}

	@Override
	public void passivateObject(Object obj) {
	}

}
