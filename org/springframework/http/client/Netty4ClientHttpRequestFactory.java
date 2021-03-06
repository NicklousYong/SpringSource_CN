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

package org.springframework.http.client;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.SocketChannelConfig;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.timeout.ReadTimeoutHandler;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpMethod;
import org.springframework.util.Assert;

/**
 * {@link org.springframework.http.client.ClientHttpRequestFactory} implementation that
 * uses <a href="http://netty.io/">Netty 4</a> to create requests.
 *
 * <p>Allows to use a pre-configured {@link EventLoopGroup} instance - useful for sharing
 * across multiple clients.
 *
 * <p>
 *  {@link orgspringframeworkhttpclientClientHttpRequestFactory}实现,使用<a href=\"http://nettyio/\"> Netty 
 * 4 </a>创建请求。
 * 
 * <p>允许使用预配置的{@link EventLoopGroup}实例,可用于跨多个客户端共享
 * 
 * 
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 * @since 4.1.2
 */
public class Netty4ClientHttpRequestFactory implements ClientHttpRequestFactory,
		AsyncClientHttpRequestFactory, InitializingBean, DisposableBean {

	/**
	 * The default maximum response size.
	 * <p>
	 *  默认最大响应大小
	 * 
	 * 
	 * @see #setMaxResponseSize(int)
	 */
	public static final int DEFAULT_MAX_RESPONSE_SIZE = 1024 * 1024 * 10;


	private final EventLoopGroup eventLoopGroup;

	private final boolean defaultEventLoopGroup;

	private int maxResponseSize = DEFAULT_MAX_RESPONSE_SIZE;

	private SslContext sslContext;

	private int connectTimeout = -1;

	private int readTimeout = -1;

	private volatile Bootstrap bootstrap;


	/**
	 * Create a new {@code Netty4ClientHttpRequestFactory} with a default
	 * {@link NioEventLoopGroup}.
	 * <p>
	 *  使用默认{@link NioEventLoopGroup}创建一个新的{@code Netty4ClientHttpRequestFactory}
	 * 
	 */
	public Netty4ClientHttpRequestFactory() {
		int ioWorkerCount = Runtime.getRuntime().availableProcessors() * 2;
		this.eventLoopGroup = new NioEventLoopGroup(ioWorkerCount);
		this.defaultEventLoopGroup = true;
	}

	/**
	 * Create a new {@code Netty4ClientHttpRequestFactory} with the given
	 * {@link EventLoopGroup}.
	 * <p><b>NOTE:</b> the given group will <strong>not</strong> be
	 * {@linkplain EventLoopGroup#shutdownGracefully() shutdown} by this factory;
	 * doing so becomes the responsibility of the caller.
	 * <p>
	 *  创建一个新的{@code Netty4ClientHttpRequestFactory}与给定的{@link EventLoopGroup} <p> <b>注意：</b>给定的组将<strong>不</strong>
	 * 是{@linkplain EventLoopGroup#shutdownGracefully()shutdown }由本厂这样做成为呼叫者的责任。
	 * 
	 */
	public Netty4ClientHttpRequestFactory(EventLoopGroup eventLoopGroup) {
		Assert.notNull(eventLoopGroup, "EventLoopGroup must not be null");
		this.eventLoopGroup = eventLoopGroup;
		this.defaultEventLoopGroup = false;
	}


	/**
	 * Set the default maximum response size.
	 * <p>By default this is set to {@link #DEFAULT_MAX_RESPONSE_SIZE}.
	 * <p>
	 *  设置默认最大响应大小<p>默认设置为{@link #DEFAULT_MAX_RESPONSE_SIZE}
	 * 
	 * 
	 * @see HttpObjectAggregator#HttpObjectAggregator(int)
	 * @since 4.1.5
	 */
	public void setMaxResponseSize(int maxResponseSize) {
		this.maxResponseSize = maxResponseSize;
	}

	/**
	 * Set the SSL context. When configured it is used to create and insert an
	 * {@link io.netty.handler.ssl.SslHandler} in the channel pipeline.
	 * <p>By default this is not set.
	 * <p>
	 *  设置SSL上下文配置时,它用于在通道管道中创建和插入{@link ionettyhandlersslSslHandler} <p>默认情况下,该值不设置
	 * 
	 */
	public void setSslContext(SslContext sslContext) {
		this.sslContext = sslContext;
	}

	/**
	 * Set the underlying connect timeout (in milliseconds).
	 * A timeout value of 0 specifies an infinite timeout.
	 * <p>
	 * 设置底层连接超时(以毫秒为单位)超时值为0指定无限超时
	 * 
	 * 
	 * @see ChannelConfig#setConnectTimeoutMillis(int)
	 */
	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	/**
	 * Set the underlying URLConnection's read timeout (in milliseconds).
	 * A timeout value of 0 specifies an infinite timeout.
	 * <p>
	 *  设置底层URLConnection的读取超时(以毫秒为单位)超时值为0指定无限超时
	 * 
	 * 
	 * @see ReadTimeoutHandler
	 */
	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}

	private Bootstrap getBootstrap() {
		if (this.bootstrap == null) {
			Bootstrap bootstrap = new Bootstrap();
			bootstrap.group(this.eventLoopGroup).channel(NioSocketChannel.class)
					.handler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel channel) throws Exception {
							configureChannel(channel.config());
							ChannelPipeline pipeline = channel.pipeline();
							if (sslContext != null) {
								pipeline.addLast(sslContext.newHandler(channel.alloc()));
							}
							pipeline.addLast(new HttpClientCodec());
							pipeline.addLast(new HttpObjectAggregator(maxResponseSize));
							if (readTimeout > 0) {
								pipeline.addLast(new ReadTimeoutHandler(readTimeout,
										TimeUnit.MILLISECONDS));
							}
						}
					});
			this.bootstrap = bootstrap;
		}
		return this.bootstrap;
	}

	/**
	 * Template method for changing properties on the given {@link SocketChannelConfig}.
	 * <p>The default implementation sets the connect timeout based on the set property.
	 * <p>
	 *  在给定的{@link SocketChannelConfig}上更改属性的模板方法<p>默认实现根据set属性设置连接超时
	 * 
	 * @param config the channel configuration
	 */
	protected void configureChannel(SocketChannelConfig config) {
		if (this.connectTimeout >= 0) {
			config.setConnectTimeoutMillis(this.connectTimeout);
		}
	}

	@Override
	public void afterPropertiesSet() {
		getBootstrap();
	}


	@Override
	public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException {
		return createRequestInternal(uri, httpMethod);
	}

	@Override
	public AsyncClientHttpRequest createAsyncRequest(URI uri, HttpMethod httpMethod) throws IOException {
		return createRequestInternal(uri, httpMethod);
	}

	private Netty4ClientHttpRequest createRequestInternal(URI uri, HttpMethod httpMethod) {
		return new Netty4ClientHttpRequest(getBootstrap(), uri, httpMethod);
	}


	@Override
	public void destroy() throws InterruptedException {
		if (this.defaultEventLoopGroup) {
			// Clean up the EventLoopGroup if we created it in the constructor
			this.eventLoopGroup.shutdownGracefully().sync();
		}
	}

}
