package com.sany.bigdata.imp;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.hadoop.fs.FileSystem;


public class UploadWorker implements Runnable{
	BlockingQueue<FileSegment> queue;
	CyclicBarrier barrier;
	java.util.concurrent.atomic.AtomicInteger count; 
	FileSystem fileSystem;
	GenFileHelper genFileHelper;
	public UploadWorker(GenFileHelper genFileHelper,FileSystem fileSystem,AtomicInteger count, BlockingQueue<FileSegment> queue,CyclicBarrier barrier)
	{
		this.queue = queue;
		this.barrier = barrier;
		this.count = count;
		this.fileSystem = fileSystem;
		this.genFileHelper = genFileHelper;
	}
	@Override
	public void run() {
		while(true)
		{
			try {
				FileSegment segment = queue.poll(genFileHelper.getUploadqueuetimewait(),TimeUnit.SECONDS);
				if(segment == null)
				{
					if(count.get() == 0)
						break;
					 
				}
				else
				{
					count.decrementAndGet();
					UploadDataTask task = new UploadDataTask( fileSystem,segment);
					task.run();
					if(count.get() == 0)
						break;
				}
				
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				break;
			}
//			try {
//				barrier.await();
//			} catch (InterruptedException | BrokenBarrierException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//				break;
//			}
		}
		
	}
	

}
