package com.heeking.net;

import java.io.IOException;
/**
 * 网络事件反应器池
 * @author zss
 */
public class NIOReactorPool {
	private final NIOReactor[] reactors;
	private volatile int nextReactor;

	public NIOReactorPool(String name, int poolSize) throws IOException {
		reactors = new NIOReactor[poolSize];
		for (int i = 0; i < poolSize; i++) {
			NIOReactor reactor = new NIOReactor(name + "-" + i);
			reactors[i] = reactor;
			reactor.startup();
		}
	}
	public NIOReactor getNextReactor() {
		int i = ++nextReactor;
		if (i >= reactors.length) {
			i = nextReactor = 0;
		}
		return reactors[i];
	}
}
