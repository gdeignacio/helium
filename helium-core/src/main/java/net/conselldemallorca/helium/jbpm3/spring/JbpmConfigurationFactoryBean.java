package net.conselldemallorca.helium.jbpm3.spring;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmContext;
import org.jbpm.configuration.ObjectFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

/**
 * Factory bean that produces the (singleton) jBPM configuration bean.
 * 
 * Features : - returns the singleton JbpmConfiguration object for this application
 *            - allows the injection of the Spring-configured session factory, which will then be used by
 *              jBPM to access the database
 *            - allows to start a prefdefined nr of job executor threads and shuts them down properly
 *              when the application context goes doen.
 * 
 * @author Joram Barrez
 */
@SuppressWarnings("rawtypes")
public class JbpmConfigurationFactoryBean implements FactoryBean, InitializingBean, ApplicationListener {
	
	/** Logger for this class. */
	private static final Log LOG = LogFactory.getLog(JbpmConfigurationFactoryBean.class);
	
	/** The singleton object that this factory produces */
	private JbpmConfiguration jbpmConfiguration;
	
	/** The jBPM object factory */
	private ObjectFactory objectFactory;
	
	/** Indicates whether the job executor must be started */
	private boolean startJobExecutor;
	
	/** The Hibernate session factory used by jBPM and the application */
	private SessionFactory sessionFactory;
	
	/**
	 * Default constructor.
	 */
	public JbpmConfigurationFactoryBean() {
		
	}
	
	public Object getObject() throws Exception {
		return jbpmConfiguration;
	}

	public Class getObjectType() {
		return JbpmConfiguration.class;
	}

	public boolean isSingleton() {
		return true;
	}

	public void afterPropertiesSet() throws Exception {
		
		LOG.info("All properties set. Initializing the jBPM configuration");
		
		// Create jbpm Config object
		JbpmConfiguration.Configs.setDefaultObjectFactory(objectFactory);
		jbpmConfiguration = new JbpmConfiguration(objectFactory);
		
		// Inject session factory
		JbpmContext ctx = null;
		try {
			 ctx = jbpmConfiguration.createJbpmContext();
			 ctx.setSessionFactory(sessionFactory);
			 LOG.info("SessionFactory injected in the jBPM config. jBPM will now use this session factory "
					 + "to create its Hibernate sessions");
		} finally {
			if (ctx != null) {
				ctx.close();
			}
		}
		
		// Start job executor if needed
		if (startJobExecutor) {
			LOG.info("Starting job executor ...");
			jbpmConfiguration.startJobExecutor();
			LOG.info("Job executor started.");
		}
	}
	
	public void onApplicationEvent(ApplicationEvent applicationEvent) {
		if (applicationEvent instanceof ContextClosedEvent) {
			jbpmConfiguration.getJobExecutor().stop();
		}
	}

	public void setObjectFactory(ObjectFactory objectFactory) {
		this.objectFactory = objectFactory;
	}


	public void setStartJobExecutor(boolean startJobExecutor) {
		this.startJobExecutor = startJobExecutor;
	}
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

}
