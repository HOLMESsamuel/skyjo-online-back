package org.online.skyjo.batch;

import io.quarkus.runtime.StartupEvent;
import org.quartz.*;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

@ApplicationScoped
public class ApplicationScheduler {

	@Inject
	org.quartz.Scheduler quartz;

	void onStart(@Observes StartupEvent event) throws SchedulerException{
		JobDetail job = JobBuilder.newJob(RemoveOldGamesJob.class)
				.withIdentity("removeOldGames", "cleaning")
				.build();

		Trigger trigger = TriggerBuilder.newTrigger()
				.withIdentity("initTrigger", "init")
				.withSchedule(SimpleScheduleBuilder.simpleSchedule()
						.withIntervalInMinutes(5)
						.repeatForever())
				.build();

		quartz.scheduleJob(job, trigger);
	}
}
