package com.agenciacrud.gestornegocio.util.processador;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

public class ProcessadorAgendaAutomatica implements ServletContextListener {

	private final Logger log = (Logger) LogManager.getLogger(ProcessadorAgendaAutomatica.class);
	private GeradorObrigacoesAutomaticoMensal geradorObrigacoes;
	
	
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		log.fatal("   ##### Iniciando Processador ##### ");
		try {
		System.out.println("Entrei no main da job");
        SchedulerFactory schedFact = new StdSchedulerFactory();
        Scheduler sched = (Scheduler) schedFact.getScheduler();
        sched.start();
        JobDetail job = JobBuilder.newJob(AgendaAutomatica.class)
            .withIdentity("myJob", "group1")
            .build();
        
        //CronScheduleBuilder.cronSchedule("0 0 1 1 * ?") -> segundos, minutos, horas, dia do mês, Mês, dia da semana e ano opcional
        Trigger trigger = TriggerBuilder
            .newTrigger()
            .withIdentity("myTrigger", "group1")
            .withSchedule(CronScheduleBuilder.cronSchedule("0 0 1 1 * ?"))
            .build();
         sched.scheduleJob(job, trigger);
         System.out.println("Final da rotina");
		} catch (Exception e) {
            System.out.println("erro");
            e.printStackTrace();
        }

		//ativarGeradorAgendamentos();
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		log.fatal("    ##### Finalizando Processador  ##### ");
		
		if (geradorObrigacoes != null)
			geradorObrigacoes.stop();

	}

	private void ativarGeradorAgendamentos() {
		geradorObrigacoes = new GeradorObrigacoesAutomaticoMensal();
		geradorObrigacoes.start();
		log.info("Gerador de Alertas iniciado");
	}
	
}
