package com.daicy.concurrency.tutorialAtomicReference;

import com.vmlens.api.AllInterleavings;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TestThreadSafe {
		@Test
		public void test() throws InterruptedException {
			
			try (AllInterleavings allInterleavings = new AllInterleavings("TestThreadSafe");) {
				while (allInterleavings.hasNext()) {		
			final UpdateStateWithCompareAndSet object = new UpdateStateWithCompareAndSet();
			Thread first = new Thread( () ->    {  object.update(); } ) ;
			Thread second = new Thread( () ->   {  object.update();  } ) ;
			first.start();
			second.start();
			first.join();
			second.join();	
			assertTrue(  object.getState().isAccessedByMultipleThreads() );
				}
			}
		}
}
