package com.yss.acs.iddemo.interfaces.controller;

import com.yss.acs.iddemo.domain.IdTestRepository;
import com.yss.acs.iddemo.domain.TIdTest;
import com.yss.fsip.generic.Result;
import com.yss.fsip.generic.ResultFactory;
import com.yss.id.core.constans.IDFormatEnum;
import com.yss.id.client.util.IdUtil;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Description: 菜单目录
 * @Author gumpLiu
 * @Date 2019-12-12
 * @Version V1.0
 **/
@Api(value="/id",tags="菜单目录管理")
@Controller
@RequestMapping("/id")
public class IdController {

	@Autowired
	IdTestRepository idTestRepository;

	private ExecutorService executorService  = Executors.newFixedThreadPool(2);

	@ResponseBody
	@RequestMapping(value="/find/{bizTag}/{timeStr}", method = RequestMethod.GET, produces="application/json")
	public Result find(@PathVariable String bizTag, @PathVariable long timeStr) {

		long startTime = System.currentTimeMillis();

		for(;;){
			executorService.execute(()->{
				TIdTest test = new TIdTest();
				String id = IdUtil.getSegmentNextId(bizTag);
				test.setTestId(id);
				idTestRepository.save(test);
			});

			long time  = System.currentTimeMillis() - startTime;

			if(time > timeStr){
				break;
			}
		}

		return ResultFactory.success("success");
	}

	@ResponseBody
	@RequestMapping(value="/get/{bizTag}", method = RequestMethod.GET, produces="application/json")
	public Result get(@PathVariable String bizTag) {

		return ResultFactory.success(IdUtil.getSegmentNextId(bizTag));
	}

	@ResponseBody
	@RequestMapping(value="/get/{bizTag}/{time}", method = RequestMethod.GET, produces="application/json")
	public Result getId(@PathVariable String bizTag, @PathVariable long time) {

		long startTime = System.currentTimeMillis();

		for (int i= 0; i < time; i++){
			IdUtil.getSegmentNextId(bizTag);
		}
		long startEndTime = System.currentTimeMillis() - startTime;
		System.out.println("time=" + startEndTime);

		return ResultFactory.success();
	}

	@ResponseBody
	@RequestMapping(value="/find/fixed/{bizTag}", method = RequestMethod.GET, produces="application/json")
	public Result fixedFind(@PathVariable String bizTag) {

		AtomicLong num = new AtomicLong(0);
		for(;;){
			executorService.execute(()->{
				if(num.incrementAndGet() >= 27){
					return;
				}
				TIdTest test = new TIdTest();
				String id = IdUtil.getSegmentFixedLengthNextId(bizTag, 1);
				test.setTestId(id);
				idTestRepository.save(test);
			});


			if( num.get() >= 27){
				break;
			}
		}

		return ResultFactory.success("success");
	}


	@ResponseBody
	@RequestMapping(value = "/findzwy/{bizTag}", method = RequestMethod.GET, produces = "application/json")
	public Result findzwy(@PathVariable String bizTag) {
		for(int j = 0; j< 10000; j ++ ){
			IdUtil.getSegmentNextId(bizTag);
		}

		return ResultFactory.success("success");
	}

	public Result testResult(boolean result, String msg, Object object) {
		if (result) {
			return new Result("Sucess", msg, object);
		} else {
			return new Result("Fail", msg, object);
		}
	}


	@ResponseBody
	@RequestMapping(value="/find/fixed/{bizTag}/{length}", method = RequestMethod.GET, produces="application/json")
	public Result fixedLengthFind(@PathVariable String bizTag,
								  @PathVariable int length) {

		String id = IdUtil.getSegmentFixedLengthNextId(bizTag, length);
		return ResultFactory.success(id);
	}

	@ResponseBody
	@RequestMapping(value="/snowflake/{timeStr}", method = RequestMethod.GET, produces="application/json")
	public Result snowflake(@PathVariable long timeStr) {

		long startTime = System.currentTimeMillis();

		for(;;){
			executorService.execute(
					()->{
						TIdTest test = new TIdTest();
						String id = IdUtil.getSnowflakeNextId(IDFormatEnum.ID_FORMAT_MILLISECOND);
						test.setTestId(id);
						idTestRepository.save(test);
					});

			long time  = System.currentTimeMillis() - startTime;

			if(time > timeStr){
				break;
			}
		}

		return ResultFactory.success("success");
	}
}
