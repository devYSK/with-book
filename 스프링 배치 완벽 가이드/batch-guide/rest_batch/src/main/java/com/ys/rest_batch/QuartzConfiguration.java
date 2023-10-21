package com.ys.rest_batch;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.TriggerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfiguration {

	@Bean
	public JobDetail quartzJobDetail() {
		return JobBuilder.newJob(BatchScheduledJob.class)
			.storeDurably()
			.build();
	}

	@Bean
	public org.quartz.Trigger jobTrigger() {
		SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
			.withIntervalInSeconds(5)
			.withRepeatCount(4);

		return TriggerBuilder.newTrigger()
			.forJob(quartzJobDetail())
			.withSchedule(scheduleBuilder)
			.build();
	}

}
