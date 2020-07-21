package com.xxl.job.executor.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class XxlJobExecutorExampleBootApplicationTests {

	@Autowired
	private RedisTemplate redisTemplate;

	@Test
	public void test() {

	}


	@Test
	public void  testRedis(){
		RedisSerializer stringSerializer = new StringRedisSerializer();
		redisTemplate.setKeySerializer(stringSerializer);
		redisTemplate.setValueSerializer(stringSerializer);
		redisTemplate.setHashKeySerializer(stringSerializer);
		redisTemplate.setHashValueSerializer(stringSerializer);
//        redisTemplate.opsForValue().set("aaa","i'm commming");
//		System.out.println(redisTemplate.opsForValue().get("aaa"));
		redisTemplate.opsForHash().put("list","1","1");
		String list1 =(String) redisTemplate.opsForHash().get("list", "1");
		System.out.println(list1);
	}








}