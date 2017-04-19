package net.conselldemallorca.helium.core.model.exception;

import java.util.ArrayList;
import java.util.List;

import net.conselldemallorca.helium.core.model.service.ServiceProxy;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

@Aspect
public class IncidentThrowsAdviceHelper {

//	private static Log logger = LogFactory.getLog(IncidentThrowsAdviceHelper.class);
	
	private static ThreadLocal<DadesAdvice> dadesAdviceThreadLocal = new ThreadLocal<DadesAdvice>();

	public static void setDadesAdvice(DadesAdvice dadesAdvice) {
		dadesAdviceThreadLocal.set(dadesAdvice);
	}
	public static DadesAdvice getDadesAdvice() {
		return dadesAdviceThreadLocal.get();
	}
	public static void initDadesAdvice(Signature signature) {
		DadesAdvice dadesAdvice = dadesAdviceThreadLocal.get();
		if (dadesAdvice == null) 
			dadesAdvice = new DadesAdvice();
		if (dadesAdvice.getSignature() == null) {
			dadesAdvice.setSignature(signature);
			dadesAdviceThreadLocal.set(dadesAdvice);
		}
	}
	public static void addDadesAdvicePortasignatures(Integer idPortasignatures) {
		DadesAdvice dadesAdvice = dadesAdviceThreadLocal.get();
		dadesAdvice.addIdsPortasignatures(idPortasignatures);
		dadesAdviceThreadLocal.set(dadesAdvice);
	}
	public static void clearDadesAdvice(Signature signature) {
		DadesAdvice dadesAdvice = dadesAdviceThreadLocal.get();
//		System.out.println(">>>>>>>>>>>>>>>>>>>>>>> Signatura before: " + dadesAdvice.getSignature().toString());
//		System.out.println(">>>>>>>>>>>>>>>>>>>>>>> Signatura after: " + signature.toString());
		if (dadesAdvice.getSignature().equals(signature)) 
//			System.out.println(">>>>>>>>>>>>>>>>>>>>>>> Signatures iguals ");
			dadesAdviceThreadLocal.set(new DadesAdvice());
	}
	
	@Before("execution(* net.conselldemallorca.helium.core.model.service.ExpedientService*.*(..)) "
			+ "|| execution(* net.conselldemallorca.helium.core.model.service.TascaService*.*(..)) "
			+ "|| execution(* net.conselldemallorca.helium.jbpm3.spring.SpringJobExecutorThread.executeJob(..))")
	public void before(JoinPoint joinPoint) {

		Signature signature = joinPoint.getSignature();
//		String methodName = signature.getName();
//		System.out.println(">>>>>>>>>>>>>>>>>>>>>>> Okay - we're in the before handler...\nmethod: " + methodName); 
		initDadesAdvice(signature);

	}
	
	/**
	 * Called between the throw and the catch
	 */
	@AfterThrowing(pointcut = "execution(* net.conselldemallorca.helium.core.model.service.ExpedientService*.*(..)) "
			+ "|| execution(* net.conselldemallorca.helium.core.model.service.TascaService*.*(..))", throwing = "e")
	public void afterThrowing(JoinPoint joinPoint, Throwable e) {

		Signature signature = joinPoint.getSignature();
//		System.out.println(">>>>>>>>>>>>>>>>>>>>>>> Okay - we're in the afterThrowing handler...");
		if (!getDadesAdvice().getIdsPortasignatures().isEmpty()) {
//			System.out.println(">>>>>>>>>>>>>>>>>>>>>>> Eliminam: " + getDadesAdvice().getIdsPortasignatures());
			ServiceProxy.getInstance().getPluginService().deletePortasignatures(getDadesAdvice().getIdsPortasignatures());
		}
		clearDadesAdvice(signature);
	}
	
	@AfterReturning("execution(* net.conselldemallorca.helium.core.model.service.ExpedientService*.*(..)) "
			+ "|| execution(* net.conselldemallorca.helium.core.model.service.TascaService*.*(..)) "
			+ "|| execution(* net.conselldemallorca.helium.jbpm3.spring.SpringJobExecutorThread.executeJob(..))")
	public void after(JoinPoint joinPoint) {

		Signature signature = joinPoint.getSignature();
//		String methodName = signature.getName();
//		System.out.println(">>>>>>>>>>>>>>>>>>>>>>> Okay - we're in the after handler...\nmethod: " + methodName); 
		clearDadesAdvice(signature);
	}
	
	static class DadesAdvice {
		Signature signature;
		List<Integer> idsPortasignatures = new ArrayList<Integer>();
		
		public DadesAdvice() {
			idsPortasignatures = new ArrayList<Integer>();
		}
		public Signature getSignature() {
			return signature;
		}
		public void setSignature(Signature signature) {
			this.signature = signature;
		}
		public List<Integer> getIdsPortasignatures() {
			return idsPortasignatures;
		}
		public void setIdsPortasignatures(List<Integer> idsPortasignatures) {
			this.idsPortasignatures = idsPortasignatures;
		}
		public void addIdsPortasignatures(Integer idPortasignatures) {
			this.idsPortasignatures.add(idPortasignatures);
		}
	}
}