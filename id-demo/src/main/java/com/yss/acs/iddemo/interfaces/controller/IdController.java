package com.yss.acs.iddemo.interfaces.controller;

import com.yss.acs.iddemo.domain.IdTestRepository;
import com.yss.acs.iddemo.domain.TIdTest;
import com.yss.fsip.generic.Result;
import com.yss.fsip.generic.ResultFactory;
import com.yss.id.client.util.IdUtil;
import com.yss.id.core.constans.IDFormatEnum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Description: 菜单目录
 * @Author gumpLiu
 * @Date 2019-12-12
 * @Version V1.0
 **/
//@Api(value="/id",tags="菜单目录管理")
@Controller
@RequestMapping("/id")
public class IdController {

	@Autowired
	IdTestRepository idTestRepository;

	private ExecutorService executorService  = Executors.newFixedThreadPool(5);

	@ResponseBody
	@RequestMapping(value="/find/{bizTag}/{timeStr}", method = RequestMethod.GET, produces="application/json")
	public Result find(@PathVariable String bizTag, @PathVariable long timeStr) {

		long startTime = System.currentTimeMillis();

		for(int i=0; i< timeStr; i++){
			IdUtil.getSegmentNextId(bizTag);

		}
		long time  = System.currentTimeMillis() - startTime;
		System.out.println("time=" + time);


		return ResultFactory.success("success");
	}

	@ResponseBody
	@RequestMapping(value="/get/{bizTag}", method = RequestMethod.GET, produces="application/json")
	public Result get(@PathVariable String bizTag) {

		return ResultFactory.success(IdUtil.getSegmentFixedLengthNextId(" "));
	}




	@ResponseBody
	@RequestMapping(value="/{type}/{timeStr}/{core}", method = RequestMethod.GET, produces="application/json")
	public void get(@PathVariable long timeStr,@PathVariable int core,@PathVariable String type) {

		long startTime = System.currentTimeMillis();
		System.out.println("=================");
		CountDownLatch countDownLatch = new CountDownLatch(core);
		for(int i = 0; i < core; i++){
			executorService.execute(()-> {
				for (int j = 0; j < timeStr/core; j++) {
					TIdTest test = new TIdTest();
					String id ="";
					if(type.equals("xh")){
							id =	IdUtil.getSnowflakeNextId(IDFormatEnum.ID_FORMAT_SHOT_YEAR_SECOND);

					}else if(type.equals("hd")){
							id = IdUtil.getSegmentFixedLengthNextId("lsp-love-sxj");
					}

					test.setTestId(id);
					idTestRepository.save(test);
				}
				countDownLatch.countDown();
			});
		}

		try {
			countDownLatch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}


		long time  = System.currentTimeMillis() - startTime;

		System.out.println("==========time=" + time);
	}
}
