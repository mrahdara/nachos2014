package nachos.threads;

import nachos.machine.*;

import java.util.Iterator;
import java.util.LinkedList;
/**
 * Uses the hardware timer to provide preemption, and to allow threads to sleep
 * until a certain time.
 */
public class Alarm {
	/**
	 * Allocate a new Alarm. Set the machine's timer interrupt handler to this
	 * alarm's callback.
	 * 
	 * <p>
	 * <b>Note</b>: Nachos will not function correctly with more than one alarm.
	 */
	public Alarm() {
		Machine.timer().setInterruptHandler(new Runnable() {
			public void run() {
				timerInterrupt();
			}
		});
	}

	/**
	 * The timer interrupt handler. This is called by the machine's timer
	 * periodically (approximately every 500 clock ticks). Causes the current
	 * thread to yield, forcing a context switch if there is another thread that
	 * should be run.
	 */
	public void timerInterrupt() {
		boolean intStatus = Machine.interrupt().disable();
		Iterator<KThread> i = waitQueue.iterator();
		Iterator<Long> j = waitTime.iterator();
		while (i.hasNext())
		{
			if (Machine.timer().getTime() >=j.next())
			{
				i.next().ready(); 
				i.remove();
				j.remove();
			}
			else i.next();
		}
		Machine.interrupt().restore(intStatus);
		KThread.yield();
	}

	/**
	 * Put the current thread to sleep for at least <i>x</i> ticks, waking it up
	 * in the timer interrupt handler. The thread must be woken up (placed in
	 * the scheduler ready set) during the first timer interrupt where
	 * 
	 * <p>
	 * <blockquote> (current time) >= (WaitUntil called time)+(x) </blockquote>
	 * 
	 * @param x
	 *            the minimum number of clock ticks to wait.
	 * 
	 * @see nachos.machine.Timer#getTime()
	 */
	public void waitUntil(long x) {
		boolean intStatus = Machine.interrupt().disable();
		waitQueue.add(KThread.currentThread());
		waitTime.add(Machine.timer().getTime() + x);
		KThread.sleep();
		Machine.interrupt().restore(intStatus);
	}
	
	private LinkedList<KThread> waitQueue = new LinkedList<KThread>();
	private LinkedList<Long> waitTime = new LinkedList<Long>(); 
}
