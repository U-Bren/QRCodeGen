/*
 * Copyright (C) 2013 Stefan Ganzer
 *
 * This file is part of QRCodeGen.
 *
 * QRCodeGen is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * QRCodeGen is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package qrcodegen.tools;

import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import net.jcip.annotations.GuardedBy;

/**
 *
 * @author Stefan Ganzer
 */
public class DelayedAction {

	private final long delta;
	private final long delay;
	private final TimeUnit unit;
	private final ScheduledExecutorService exec;
	@GuardedBy("this")
	private ScheduledFuture<?> lastAction;
	@GuardedBy("this")
	private long timeOfLastAddAction;

	public DelayedAction(ScheduledExecutorService exec, long delta, long delay, TimeUnit unit) {
		this.exec = exec;
		this.delta = delta;
		this.delay = delay;
		this.unit = unit;
		timeOfLastAddAction = 0;
	}

	public synchronized void addAction(Callable<?> action, long delayInNanoSeconds) throws Exception {
		if (lastAction != null) {
			lastAction.cancel(false);
		}
		if (exec.isShutdown()) {
			return;
		}
		long currentTime = System.nanoTime();
		long elapsedTime = currentTime - timeOfLastAddAction;
		if (elapsedTime < unit.toNanos(delta) + delayInNanoSeconds) {
			lastAction = exec.schedule(action, delay, unit);
		} else {
			action.call();
		}
		timeOfLastAddAction = currentTime;
	}

	public synchronized void shutdown() {
		if (lastAction != null) {
			lastAction.cancel(false);
		}
		exec.shutdown();
	}
}
