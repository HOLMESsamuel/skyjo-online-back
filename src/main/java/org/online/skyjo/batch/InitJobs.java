package org.online.skyjo.batch;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;

@ApplicationScoped
public class InitJobs {
	public void init(@Observes @Initialized(ApplicationScoped.class) Object init) {
		ApplicationScheduler scheduler = new ApplicationScheduler();
		scheduler.init();
	}
}
