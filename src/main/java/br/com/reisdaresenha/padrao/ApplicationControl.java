package br.com.reisdaresenha.padrao;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.ObjectAlreadyExistsException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import br.com.reisdaresenha.job.RDRJob;


/** 
 * @author Renan Celso
 */
@ManagedBean(name = "applicationControl")
@ApplicationScoped
public class ApplicationControl implements Serializable {

	private static final long serialVersionUID = 3102335277951787470L;
	
	protected transient Logger log = Logger.getLogger(ApplicationControl.class.getName());	
	private Date dataHoraInicio;
	
	@PostConstruct
	public void init() {	
		
		try {
			
			HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);		
			PropertyConfigurator.configure(session.getServletContext().getRealPath("/") +File.separator+"WEB-INF"+File.separator+"log4j.properties");
			Locale.setDefault(new Locale("pt", "BR"));		
			dataHoraInicio = new Date();		
						
			/** INICIAR JOB **/
			//String value = "0 0/1 * * * ?"; 
			String value = "0 0/5 * * * ?"; 
			
			SchedulerFactory schedFact = new StdSchedulerFactory();
			Scheduler sched = schedFact.getScheduler();								
			sched.start();
			
			JobDetail job = JobBuilder.newJob(RDRJob.class).withIdentity("job1", "grupo1").build();			
			
			Trigger trigger = TriggerBuilder.newTrigger().withIdentity("job1", "grupo1").withSchedule(CronScheduleBuilder.cronSchedule(value)).build();
			
			sched.scheduleJob(job, trigger);
			/** INICIAR JOB **/
			
		} catch (SchedulerException e) {
			if(e instanceof ObjectAlreadyExistsException){
				log.info(e);
			} else {			
				log.error(e);
			}
		}
	}	
	
	public String getIniciarAplicacao(){
		
		return "";
	}

	public Date getDataHoraInicio() {
		return dataHoraInicio;
	}

	public void setDataHoraInicio(Date dataHoraInicio) {
		this.dataHoraInicio = dataHoraInicio;
	}
	
	public String getDataHoraInicioStr() {
		String formato = "dd/MM/yyyy HH:mm:ss";
        SimpleDateFormat formatter = new SimpleDateFormat(formato);
        return formatter.format(dataHoraInicio);
	}
	
	public String getVersao() {
		InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties");
		Properties prop = new Properties();
		try {
			prop.load(input);	
			
			return "Versao "+prop.getProperty("versao")+" - "+getDataHoraInicioStr();
			
		} catch (IOException e) {
			return "";
		}    	
    }
	
}
