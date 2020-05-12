package com.yss.acs.iddemo.interfaces.controller;

import com.alibaba.fastjson.JSON;
import com.yss.fsip.generic.Result;
import com.yss.id.client.util.IdUtil;
import com.yss.id.core.constans.IDFormatEnum;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;

@Scope("prototype")
@Controller
@RequestMapping("/test")
public class TestController {

	static int DEFAULT_QUEUE_SIZE = 10000;

	@ResponseBody
	@RequestMapping(value = "/getSegmentNextId", method = RequestMethod.GET, produces = "application/json")
	public Result getSegmentNextId(@RequestParam String bizTag, @RequestParam int number, @RequestParam int poolsize,
								   HttpServletRequest request) {
		List<String> bizTags = new ArrayList<String>();
		if (bizTag.equalsIgnoreCase("null")) {
			bizTags.add(null);
		} else {
			bizTags.add(bizTag);
		}
		Set<String> treeSet = Collections.synchronizedSet(new TreeSet<String>());
		List<String> list = Collections.synchronizedList(new ArrayList<String>());
		List<String> duplist = Collections.synchronizedList(new ArrayList<String>());
		AtomicLong atomicLong = new AtomicLong(0L);

		StringBuffer msg = new StringBuffer();
		IDCallBack callBack = new IDCallBack() {
			@Override
			public String getId() {
				String id = IdUtil.getSegmentNextId(bizTags.get(0));
				atomicLong.incrementAndGet();
				return id;
			}
		};

		long startTime = System.currentTimeMillis();
		executeThreadPoolTaskWithNoLinkedBlockQueue(number, poolsize, callBack);
		long intervalTime = System.currentTimeMillis() - startTime;
		appendParams(msg, request)
				.append("耗时(毫秒)：" + intervalTime + "，总条数：" + atomicLong.get() + "，实际条数：" + list.size() + ",重复的数据条数" + (list.size() - treeSet.size())
						+ "，平均毫秒条数：" + list.size() / intervalTime + "，重复的数据：" + duplist.toString());
		return new Result(String.valueOf(HttpStatus.OK.value()), msg.toString(), list);
	}

	@ResponseBody
	@RequestMapping(value = "/getTimerSegmentNextId", method = RequestMethod.GET, produces = "application/json")
	public Result getTimerSegmentNextId(@RequestParam String bizTag, @RequestParam int time, @RequestParam int poolsize, HttpServletRequest request) {
		List<String> bizTags = new ArrayList<String>();
		if (bizTag.equalsIgnoreCase("null")) {
			bizTags.add(null);
		} else {
			bizTags.add(bizTag);
		}
		AtomicLong atomicLong = new AtomicLong(0L);

		StringBuffer msg = new StringBuffer();
		IDCallBack callBack = callBack = new IDCallBack() {
			@Override
			public String getId() {
				String id = IdUtil.getSegmentNextId(bizTags.get(0));
				atomicLong.incrementAndGet();
				return id;
			}
		};

		long startTime = System.currentTimeMillis();
		ExecutorService executorService = Executors.newFixedThreadPool(poolsize);
		executeThreadPoolTaskTimer(executorService, poolsize, time, callBack);
		long intervalTime = System.currentTimeMillis() - startTime;
		appendParams(msg, request)
				.append("预计耗时(毫秒)：" + time +  "，实际耗时(毫秒)：" + intervalTime + "，总条数：" + atomicLong.get());
		return new Result(String.valueOf(HttpStatus.OK.value()), msg.toString(), null);
	}


	@ResponseBody
	@RequestMapping(value = "/getSnowflakeNextId", method = RequestMethod.GET, produces = "application/json")
	public Result getSnowflakeNextId(@RequestParam String idFormatEnum, @RequestParam int number,
									 @RequestParam int poolsize, HttpServletRequest request) {

		List<IDFormatEnum> idFormatEnums = new ArrayList<IDFormatEnum>();
		if (idFormatEnum.equalsIgnoreCase("null")) {
			idFormatEnums.add(null);
		} else {
			idFormatEnums.add(Enum.valueOf(IDFormatEnum.class, idFormatEnum));
		}
		// List<String> list = Collections.synchronizedList(new ArrayList<String>());
		Set<String> treeSet = Collections.synchronizedSet(new TreeSet<String>());
		List<String> list = Collections.synchronizedList(new ArrayList<String>());
		List<String> duplist = Collections.synchronizedList(new ArrayList<String>());
		StringBuffer msg = new StringBuffer();
		AtomicLong atomicLong = new AtomicLong(0L);

		IDCallBack callBack = new IDCallBack() {
			@Override
			public String getId() {
				String id = IdUtil.getSnowflakeNextId(idFormatEnums.get(0));
				atomicLong.incrementAndGet();
				return id;
			}
		};

		long startTime = System.currentTimeMillis();
		executeThreadPoolTaskWithNoLinkedBlockQueue(number, poolsize, callBack);
		long intervalTime = System.currentTimeMillis() - startTime;
		appendParams(msg, request)
				.append("耗时(毫秒)：" + intervalTime + "，实际条数：" + atomicLong.get() + ",重复的数据条数" + (list.size() - treeSet.size())
						+ "，平均毫秒条数：" + list.size() / intervalTime + "，重复的数据：" + duplist.toString());
		return new Result(String.valueOf(HttpStatus.OK.value()), msg.toString(), list);
	}

	@ResponseBody
	@RequestMapping(value = "/getTimerSnowflakeNextId", method = RequestMethod.GET, produces = "application/json")
	public Result getTimerSnowflakeNextId(@RequestParam String idFormatEnum, @RequestParam int time,
										  @RequestParam int poolsize, HttpServletRequest request) {

		StringBuffer msg = new StringBuffer();
		AtomicLong atomicLong = new AtomicLong(0L);
		IDCallBack callBack =  new IDCallBack() {
			@Override
			public String getId() {
				String id = IdUtil.getSnowflakeNextId(IDFormatEnum.valueOf(idFormatEnum));
				atomicLong.incrementAndGet();
				return id;
			}
		};

		long startTime = System.currentTimeMillis();
		ExecutorService executorService = Executors.newFixedThreadPool(poolsize);

		executeThreadPoolTaskTimer(executorService, poolsize, time, callBack);

		long intervalTime = System.currentTimeMillis() - startTime;
		appendParams(msg, request)
				.append("预计耗时(毫秒)：" + time +  "，实际耗时(毫秒)：" + intervalTime + "，总条数：" + atomicLong.get());

		return new Result(String.valueOf(HttpStatus.OK.value()), msg.toString(), null);
	}



	public void executeThreadPoolTaskTimer(ExecutorService executorService, int poolsize,int time, IDCallBack callBack) {
		CountDownLatch downLatch = new CountDownLatch(poolsize);

		for (int i = 0; i < poolsize; i++) {
			try {
				executorService.execute(()->{
					long startTime = System.currentTimeMillis();
					for(;;){
						if(System.currentTimeMillis() - startTime > time ){
							break;
						}
						callBack.getId();
					}
					downLatch.countDown();
				});
			}catch (Exception e){

			}
		}
		try {
			downLatch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}


	public void executeThreadPoolTaskWithNoLinkedBlockQueue(int number, int poolsize, IDCallBack callBack) {
		ExecutorService executorService = Executors.newFixedThreadPool(poolsize);
		CountDownLatch latch = new CountDownLatch(poolsize);
		int perTime = number / poolsize;
		int mod = number % poolsize;
		for (int i = 0; i < poolsize; i++) {
			if (i < poolsize - 1) {
				executorService.execute(() -> {
					for (int j = 0; j < perTime; j++) {
						callBack.getId();
					}
					latch.countDown();
				});
			} else {
				executorService.execute(() -> {
					for (int j = 0; j < perTime + mod; j++) {
						callBack.getId();
					}
					latch.countDown();
				});
			}
		}
		try {
			latch.await();

		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			executorService.shutdown();
		}
	}

	public void executeThreadPoolTaskWithLinkedBlockQueue(int time, int poolsize, IDCallBack callBack,
			int maxQueuesize) {
		ExecutorService executorService = Executors.newFixedThreadPool(poolsize);
		CountDownLatch latch = new CountDownLatch(time);
		Set<String> set = Collections.synchronizedSet(new HashSet<String>());
		AtomicLong integer = new AtomicLong(0);
		for (;;) {
			int queueSize = ((ThreadPoolExecutor) executorService).getQueue().size();
			if (maxQueuesize - queueSize > 0) {
				executorService.execute(() -> {
					integer.getAndIncrement();
					if (integer.get() > time) {
						return;
					}
					String id = callBack.getId();
					set.add(id);
					latch.countDown();
				});
				if (integer.get() > time) {
					break;
				}
			}
		}
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void executeThreadPoolTaskWithLinkedQueue(int time, int poolsize, IDCallBack callBack) {
		ExecutorService executorService = Executors.newFixedThreadPool(poolsize);
		CountDownLatch latch = new CountDownLatch(time);
		Set<String> set = Collections.synchronizedSet(new HashSet<String>());
		StringBuffer msg = new StringBuffer();
		long startTime = System.currentTimeMillis();
		AtomicLong integer = new AtomicLong(0);
		for (;;) {
			int queueSize = ((ThreadPoolExecutor) executorService).getQueue().size();
			if (DEFAULT_QUEUE_SIZE - queueSize > 0) {
				executorService.execute(() -> {
					integer.getAndIncrement();
					if (integer.get() > time) {
						return;
					}
					String id = callBack.getId();
					set.add(id);
					latch.countDown();
				});
				if (integer.get() > time) {
					break;
				}
			}
		}
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public StringBuffer appendParams(StringBuffer buffer, HttpServletRequest request) {
		// Object string = JSON.toJSON(request.getParameterMap());
		Object string = JSON.toJSONString(request.getParameterMap());
		System.out.println(string);
		return buffer.append(string);
	}

	// 任务回调
	@FunctionalInterface
	public interface IDCallBack {
		public String getId();
	}

	public interface TaskRunnable extends Runnable {
		int time = 0;
	}

	public static void main(String[] args) {
		long start = System.currentTimeMillis();

		List<Integer> list = new ArrayList<Integer>();
		Set<Integer> set1 = new HashSet<Integer>();
		for (int i = 1; i < 10; i++) {
			set1.add(i);
		}
		for (int i = 0; i < 15; i++) {
			if (!set1.add(i)) {
				list.add(i);
			}
		}

		System.out.println(list.toString());

	}

}
