package org.online.skyjo.batch;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

public class ApplicationScheduler {
	public void init() {
		try {
			SchedulerFactory schedulerFactory = new StdSchedulerFactory();
			Scheduler scheduler = schedulerFactory.getScheduler();
			JobDetail job = JobBuilder.newJob(RemoveOldGamesJob.class)
					.withIdentity("RemoveOldGamesJob", "group1")
					.build();
			Trigger trigger = TriggerBuilder.newTrigger()
					.withIdentity("RemoveOldGamesTrigger", "group1")
					.startNow()
					.withSchedule(SimpleScheduleBuilder.simpleSchedule()
							.withIntervalInMinutes(5)
							.repeatForever())
					.build();
			scheduler.scheduleJob(job, trigger);
			scheduler.start();
		} catch (SchedulerException ex) {
			// Gérer l'exception
		}
	}
}
