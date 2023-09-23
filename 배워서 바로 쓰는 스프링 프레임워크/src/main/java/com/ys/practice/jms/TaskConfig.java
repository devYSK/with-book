package com.ys.practice.jms;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling // -- used for scheduling FixedDepositProcessorJob's sendEmail
					// method
public class TaskConfig {

}
