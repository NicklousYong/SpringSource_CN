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

package org.springframework.util;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;

/**
 * An {@link IdGenerator} that uses {@link SecureRandom} for the initial seed and
 * {@link Random} thereafter, instead of calling {@link UUID#randomUUID()} every
 * time as {@link org.springframework.util.JdkIdGenerator JdkIdGenerator} does.
 * This provides a better balance between securely random ids and performance.
 *
 * <p>
 *  除非适用法律要求或以书面形式同意,根据许可证分发的软件以"按原样"分发,不附带任何明示或暗示的担保或条件,请参阅许可证中有关特定语言的权限和限制许可证
 * 
 * 
 * @author Rossen Stoyanchev
 * @author Rob Winch
 * @since 4.0
 */
public class AlternativeJdkIdGenerator implements IdGenerator {

	private final Random random;


	public AlternativeJdkIdGenerator() {
		SecureRandom secureRandom = new SecureRandom();
		byte[] seed = new byte[8];
		secureRandom.nextBytes(seed);
		this.random = new Random(new BigInteger(seed).longValue());
	}


	@Override
	public UUID generateId() {
		byte[] randomBytes = new byte[16];
		this.random.nextBytes(randomBytes);

		long mostSigBits = 0;
		for (int i = 0; i < 8; i++) {
			mostSigBits = (mostSigBits << 8) | (randomBytes[i] & 0xff);
		}

		long leastSigBits = 0;
		for (int i = 8; i < 16; i++) {
			leastSigBits = (leastSigBits << 8) | (randomBytes[i] & 0xff);
		}

		return new UUID(mostSigBits, leastSigBits);
	}

}
