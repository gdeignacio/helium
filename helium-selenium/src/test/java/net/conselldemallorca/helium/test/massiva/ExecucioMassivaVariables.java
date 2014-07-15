package net.conselldemallorca.helium.test.massiva;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.conselldemallorca.helium.test.util.BaseTest;
import net.conselldemallorca.helium.test.util.VariableExpedient;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ExecucioMassivaVariables extends BaseTest {
	String entorn = carregarPropietat("tramas.entorn.nom", "Nom de l'entorn de proves no configurat al fitxer de properties");
	String titolEntorn = carregarPropietat("tramas.entorn.titol", "Titol de l'entorn de proves no configurat al fitxer de properties");
	String usuari = carregarPropietat("test.base.usuari.configuracio", "Usuari configuració de l'entorn de proves no configurat al fitxer de properties");
	String nomDefProc = carregarPropietat("defproc.deploy.definicio.proces.nom", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String nomSubDefProc = carregarPropietat("defproc.deploy.definicio.subproces.nom", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String pathDefProc = carregarPropietatPath("defproc.deploy.definicio.subproces.main_direct.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String pathDefProcScript = carregarPropietatPath("defproc.deploy.expexe_executar_script.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String pathSubDefProc = carregarPropietatPath("defproc.subproces.deploy.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String exportDefProc = carregarPropietatPath("defproc.tasca_dades.exp.export.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String nomTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.nom", "Nom del tipus d'expedient de proves no configurat al fitxer de properties");
	String codTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.codi", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");
	String accioPathDefProc = carregarPropietatPath("tramsel_accio.deploy.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String exportTipExpProc = carregarPropietatPath("tramsel_accio.export.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String exportTipExpMasProc = carregarPropietatPath("tramas_massivo.export.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	int numExpedientesTramMasiva = Integer.parseInt(carregarPropietat("tramas.num_expedientes_tram_masiva", "Número de espedientes para las pruebas de tramitación masiva al fitxer de properties"));
			
	@Test
	public void a0_inicialitzacio() {
		carregarUrlConfiguracio();
		crearEntorn(entorn, titolEntorn);
		assignarPermisosEntorn(entorn, usuari, "DESIGN", "ORGANIZATION", "READ", "ADMINISTRATION");
		seleccionarEntorn(titolEntorn);
		crearTipusExpedient(nomTipusExp, codTipusExp);
		assignarPermisosTipusExpedient(codTipusExp, usuari, "DESIGN","CREATE","SUPERVISION","WRITE","MANAGE","DELETE","READ","ADMINISTRATION");
	}
	
	@Test
	public void a_crear_dades() throws InterruptedException {
		carregarUrlConfiguracio();
		
		seleccionarEntorn(titolEntorn);

		importarDadesTipExp(codTipusExp, exportTipExpProc);
		
		desplegarDefinicioProcesEntorn(nomTipusExp, nomSubDefProc, pathSubDefProc);
		desplegarDefinicioProcesEntorn(nomTipusExp, nomDefProc, pathDefProc);
		
		importarDadesDefPro(nomDefProc, exportDefProc);
		importarDadesDefPro(nomSubDefProc, exportDefProc);

		// Los volvemos a desplegar para tener 2 versiones diferentes
		desplegarDefinicioProcesEntorn(nomTipusExp, nomSubDefProc, pathSubDefProc);
		desplegarDefinicioProcesEntorn(nomTipusExp, nomDefProc, pathDefProc);
		
		screenshotHelper.saveScreenshot("tramitar/dadesexpedient/crear_dades/1.png");
	}
	
	@Test
	public void b_visualitzacio_variables() throws InterruptedException {
		carregarUrlConfiguracio();
		
		seleccionarEntorn(titolEntorn);
		
		iniciarExpediente( codTipusExp, "SE-22/2014", "Expedient de prova Selenium " + (new Date()).getTime());

		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/llistat.html')]")));
		actions.click();
		actions.build().perform();
		
		screenshotHelper.saveScreenshot("tramitar/dadesexpedient/visualizacio_dades_process/1.png");
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'"+nomDefProc+"')]")).click();
				
		driver.findElement(By.xpath("//*[@id='tabnav']//a[contains(@href,'/helium/definicioProces/campLlistat.html')]")).click();
		
		List<VariableExpedient> listaVariables = new ArrayList<VariableExpedient>();
		
		// Leemos las variables
		int i = 1;
		while(i <= driver.findElements(By.xpath("//*[@id='registre']/tbody/tr")).size()) {
			VariableExpedient variable = new VariableExpedient();
			
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+i+"]")).click();
			
			String codi = driver.findElement(By.xpath("//*[@id='codi0']")).getAttribute("value");
			String etiqueta = driver.findElement(By.xpath("//*[@id='etiqueta0']")).getAttribute("value");
			String tipo = driver.findElement(By.xpath("//*[@id='tipus0']")).getAttribute("value");
			String observaciones = driver.findElement(By.xpath("//*[@id='observacions0']")).getText();
			boolean multiple = driver.findElement(By.xpath("//*[@id='multiple0']")).isSelected();
			boolean oculta = driver.findElement(By.xpath("//*[@id='ocult0']")).isSelected();
			
			variable.setCodi(codi);
			variable.setEtiqueta(etiqueta);
			variable.setTipo(tipo);
			variable.setObservaciones(observaciones);
			variable.setOculta(oculta);
			variable.setMultiple(multiple);
			
			screenshotHelper.saveScreenshot("tramitar/dadesexpedient/visualizacio_dades_process/2"+i+".png");
			
			driver.findElement(By.xpath("//*[@id='command']/div[4]/button[2]")).click();
			
			if ("REGISTRE".equals(tipo)) {
				WebElement button = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+i+"]/td[6]/form/button"));
				button.click();
				int j = 1;
				while(j <= driver.findElements(By.xpath("//*[@id='registre']/tbody/tr")).size()) {
					VariableExpedient variableReg = new VariableExpedient();
					
					driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+j+"]")).click();
					
					String[] var = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+j+"]/td[1]")).getText().split("/");
					String codiReg = var[0];
					String etiquetaReg = var[1];
					boolean obligatorioReg = "Si".equals(driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+j+"]/td[3]")).getText());
					String tipoReg = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+j+"]/td[2]")).getText();

					variableReg.setCodi(codiReg);
					variableReg.setEtiqueta(etiquetaReg);
					variableReg.setTipo(tipoReg);
					variableReg.setObligatorio(obligatorioReg);
					variableReg.setMultiple(multiple); // Si lo era la variable padre del registro
					
					variable.getRegistro().add(variableReg);
					
					screenshotHelper.saveScreenshot("tramitar/dadesexpedient/visualizacio_dades_process/3"+i+"-"+j+".png");
					j++;
				}
				
				driver.findElement(By.xpath("//*[@id='command']/div[2]/button[2]")).click();
			}
			
			listaVariables.add(variable);
			
			screenshotHelper.saveScreenshot("tramitar/dadesexpedient/visualizacio_dades_process/3"+i+".png");
			i++;
		}
		
		// Vemos el resto de parámetros de la primera tarea
		driver.findElement(By.xpath("//*[@id='tabnav']//a[contains(@href,'/definicioProces/tascaLlistat.html')]")).click();
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr/td[3]/form/button")).click();
				
		Iterator<VariableExpedient> it = listaVariables.iterator();
		while(it.hasNext()) {
			VariableExpedient var = it.next();
			if (existeixElement("//td[contains(text(),'"+var.getCodi()+"/"+var.getEtiqueta()+"')]")) {
				boolean obligatorio = driver.findElement(By.xpath("//td[contains(text(),'"+var.getCodi()+"/"+var.getEtiqueta()+"')]/parent::tr/td[4]/input")).isSelected();
				var.setObligatorio(obligatorio);
				
				boolean readonly = driver.findElement(By.xpath("//td[contains(text(),'"+var.getCodi()+"/"+var.getEtiqueta()+"')]/parent::tr/td[5]/input")).isSelected();
				var.setReadOnly(readonly);
			} else {
				it.remove();
			}
		}
		
		consultarTareas(null, null, nomTipusExp, false);
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr/td[contains(a/text(), '01 - Entrada')]/a")).click();
				
		// Comprobamos que se muestran los labels, variables, observaciones y botones según el tipo de variable
				
		for (VariableExpedient variable : listaVariables) {
			comprobarVariable(variable, false);			
		}
		
		// Guardamos y validamos
		driver.findElement(By.xpath("//*/button[contains(text(), 'Guardar')]")).click();
		existeixElementAssert("//*[@id='infos']/p", "No se guardó correctamente");
		
		driver.findElement(By.xpath("//*/button[contains(text(), 'Validar')]")).click();
		existeixElementAssert("//*[@id='infos']/p", "No se validó correctamente");
		noExisteixElementAssert("//*/div[contains(@class, 'error')]", "Existían errores de validación");
		
		existeixElementAssert("//*/button[contains(text(), 'Modificar')]", "No existía el botón modificar");
		
		screenshotHelper.saveScreenshot("TasquesDadesTasca/visualizacio_tasca_dades/5.png");
	}
	
	@Test
	public void g_modificar_variables() throws InterruptedException {
		carregarUrlConfiguracio();
		
		seleccionarEntorn(titolEntorn);
		
		// Eliminamos los expedientes
		eliminarExpedient(null, null, nomTipusExp);
		
		desplegarDefinicioProcesEntorn(nomTipusExp, nomDefProc, accioPathDefProc);
		importarDadesTipExp(codTipusExp, exportTipExpProc);
		
		// Vamos las variables disponibles
		
		// Iniciamos los expedientes
		for (int i = 0; i < numExpedientesTramMasiva; i++) {
			iniciarExpediente(codTipusExp,"SE-"+i+"/2014", "Expedient de prova Selenium " + (new Date()).getTime() );
		}
		
		consultarExpedientes(null, null, nomTipusExp);
		
		driver.findElement(By.xpath("//*[@id='massivaInfoForm']/button[2]")).click();
		
//		// Ejecutamos una modificación de variable
//		
//		WebElement selectVar = driver.findElement(By.xpath("//*[@id='var0']"));
//		List<WebElement> optionsVar = selectVar.findElements(By.tagName("option"));
//		for (WebElement var : optionsVar) {
//			if ("exp_nom".equals(var.getAttribute("value"))) {
//				var.click();
//			}
//		}
//		
//		driver.findElement(By.xpath("//*[@id='modificarVariablesMasCommand']//button[1]")).click();
//		acceptarAlerta();
//		driver.findElement(By.xpath("//*[@id='exp_nom0']")).sendKeys("Un texto");
//		driver.findElement(By.xpath("//*[@id='command']//button[1]")).click();
//		acceptarAlerta();
//		existeixElementAssert("//*[@id='infos']/p", "No se ejecutó la operación masiva correctamente");
//		
//		// Vemos las variables de los n expedientes
//		// Esperamos 10 segundos
//		Thread.sleep(1000*10);		
//		for (int i = 1; i <= numExpedientesTramMasiva; i++) {
//			consultarExpedientes(null, null, nomTipusExp);
//			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+i+"]//a[contains(@href,'/expedient/info.html')]")).click();
//			
//			driver.findElement(By.xpath("//*[@id='tabnav']//a[contains(text(), 'Dades')]")).click();
//			existeixElementAssert("//*[@id='codi']/tbody/tr/td[contains(text(),'Nombre del expediente')]","No se encontró la variable 'Nombre del expediente'");
//			
//			String mensaje = driver.findElement(By.xpath("//*[@id='codi']/tbody/tr/td[contains(text(),'message')]/parent::tr/td[2]")).getText().trim();
//			assertTrue("El valor de la variable no era el esperado", "Un texto".equals(mensaje));
//		}
		
		// Eliminamos los expedientes
		eliminarExpedient(null, null, nomTipusExp);
	}

	@Test
	public void z_limpiar() throws InterruptedException {
		carregarUrlConfiguracio();
		
		seleccionarEntorn(titolEntorn);
		
		eliminarExpedient(null, null, nomTipusExp);
		
		// Eliminar el tipo de expediente
		eliminarTipusExpedient(codTipusExp);
		
		eliminarEntorn(entorn);
		
		screenshotHelper.saveScreenshot("TasquesDadesTasca/finalizar_expedient/1.png");	
	}
	
	private void comprobarVariable(VariableExpedient variable, boolean esModal) {
		existeixElementAssert("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']","La variable no oculta no se ha mostrado : " + variable.getEtiqueta());
		if (variable.isReadOnly()) {
			// Es readonly
			existeixElementAssert("//*[@id='commandReadOnly']/div/div/label[contains(text(), '"+variable.getEtiqueta()+"')]","La variable readonly no se ha mostrado : " + variable.getEtiqueta());
		} else {
			// No es readonly
			existeixElementAssert("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']", "La etiqueta no coincidia: " + variable.getEtiqueta());
			
			if (!esModal) {
				if (variable.isObligatorio()) {
					existeixElementAssert("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/*/em/img", "La variable no estaba como obligatoria : " + variable.getEtiqueta());
				} else {
					noExisteixElementAssert("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/*/em/img", "La variable estaba como obligatoria : " + variable.getEtiqueta());
				}
				
				if (!"REGISTRE".equals(variable.getTipo())) {
					if (variable.isMultiple() && !esModal) {
						existeixElementAssert("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/button[contains(text(), '+')]", "La variable no contenía el botón de múltiple : " + variable.getEtiqueta());
					} else {
						noExisteixElementAssert("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/button[contains(text(), '+')]", "La variable contenía el botón de múltiple : " + variable.getEtiqueta());
					}	
				}
				if (variable.getObservaciones() != null && !variable.getObservaciones().isEmpty()) {
					existeixElementAssert("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/p[@class='formHint']","La variable debe mostrar observaciones : " + variable.getEtiqueta());
					String obs = driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/p[@class='formHint']")).getText();
					assertTrue("La observación no coincidía : " + variable.getEtiqueta(), obs.equals(variable.getObservaciones()));
				} else {
					noExisteixElementAssert("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/p[@class='formHint']","La variable no debe mostrar observaciones : " + variable.getEtiqueta());
				}
			}
			if ("STRING".equals(variable.getTipo())) {
				existeixElementAssert("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/input[@class='textInput']", "No tenía un input: " + variable.getEtiqueta());
				driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/input[@class='textInput']")).clear();
				driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/input[@class='textInput']")).sendKeys("Texto 1 " + variable.getEtiqueta());

				if (variable.isMultiple() && !esModal) {
					driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/button[contains(text(), '+')]")).click();
					driver.findElement(By.xpath("//*[@id='"+variable.getCodi()+"1']")).clear();
					driver.findElement(By.xpath("//*[@id='"+variable.getCodi()+"1']")).sendKeys("Texto 2 " + variable.getEtiqueta());
				}
			} else if ("INTEGER".equals(variable.getTipo())) {
				existeixElementAssert("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/input[@class='textInput']", "No tenía un input: " + variable.getEtiqueta());
				driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/input[@class='textInput']")).clear();
				driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/input[@class='textInput']")).sendKeys("1234");
				
				if (variable.isMultiple() && !esModal) {
					driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/button[contains(text(), '+')]")).click();
					driver.findElement(By.xpath("//*[@id='"+variable.getCodi()+"1']")).clear();
					driver.findElement(By.xpath("//*[@id='"+variable.getCodi()+"1']")).sendKeys("12345");
				}
			} else if ("FLOAT".equals(variable.getTipo())) {
				existeixElementAssert("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/input[@class='textInput']", "No tenía un input: " + variable.getEtiqueta());
				driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/input[@class='textInput']")).clear();
				driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/input[@class='textInput']")).sendKeys("1234");
				
				if (variable.isMultiple() && !esModal) {
					driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/button[contains(text(), '+')]")).click();
					driver.findElement(By.xpath("//*[@id='"+variable.getCodi()+"1']")).clear();
					driver.findElement(By.xpath("//*[@id='"+variable.getCodi()+"1']")).sendKeys("12345");
				}
			} else if ("BOOLEAN".equals(variable.getTipo())) {
				existeixElementAssert("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/input[@type='checkbox']", "No tenía un checkbox: " + variable.getEtiqueta());
				driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/input[@type='checkbox']")).click();
				
				if (variable.isMultiple() && !esModal) {
					driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/button[contains(text(), '+')]")).click();
					driver.findElement(By.xpath("//*[@id='"+variable.getCodi()+"1']")).clear();
					driver.findElement(By.xpath("//*[@id='"+variable.getCodi()+"1']")).click();;
				}
			} else if ("TEXTAREA".equals(variable.getTipo())) {
				existeixElementAssert("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/textarea", "No tenía un textarea: " + variable.getEtiqueta());
				driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/textarea")).clear();
				driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/textarea")).sendKeys("Texto 1 " + variable.getEtiqueta());
				
				if (variable.isMultiple() && !esModal) {
					driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/button[contains(text(), '+')]")).click();
					driver.findElement(By.xpath("//*[@id='"+variable.getCodi()+"1']")).clear();
					driver.findElement(By.xpath("//*[@id='"+variable.getCodi()+"1']")).sendKeys("Texto 2 " + variable.getEtiqueta());
				}
			} else if ("DATE".equals(variable.getTipo())) {
				existeixElementAssert("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/input[@class='textInput hasDatepicker']", "No tenía un input: " + variable.getEtiqueta());
				driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/input[@class='textInput hasDatepicker']")).clear();
				driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/input[@class='textInput hasDatepicker']")).sendKeys("13/11/2014");
				
				if (variable.isMultiple() && !esModal) {
					driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/button[contains(text(), '+')]")).click();
					driver.findElement(By.xpath("//*[@id='"+variable.getCodi()+"1']")).clear();
					driver.findElement(By.xpath("//*[@id='"+variable.getCodi()+"1']")).sendKeys("14/12/2014");
				}
			} else if ("PRICE".equals(variable.getTipo())) {
				existeixElementAssert("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/input[@class='textInput']", "No tenía un input: " + variable.getEtiqueta());
				driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/input[@class='textInput']")).clear();
				driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/input[@class='textInput']")).sendKeys("1234");
				
				if (variable.isMultiple() && !esModal) {
					driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/button[contains(text(), '+')]")).click();
					driver.findElement(By.xpath("//*[@id='"+variable.getCodi()+"1']")).clear();
					driver.findElement(By.xpath("//*[@id='"+variable.getCodi()+"1']")).sendKeys("12345");
				}
			} else if ("TERMINI".equals(variable.getTipo())) {
				boolean cond = driver.findElements(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/div/ul/li/label/select")).size() == 2
						&& driver.findElements(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/div/ul/li/label/input")).size() == 1;
				assertTrue("El termini no era correcto : " + variable.getEtiqueta(), cond);
				WebElement select1 = driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/div/ul/li[1]/label/select"));
				List<WebElement> options1 = select1.findElements(By.tagName("option"));
				options1.get(options1.size()-1).click();
				WebElement select2 = driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/div/ul/li[2]/label/select"));
				List<WebElement> options2 = select2.findElements(By.tagName("option"));
				options2.get(options2.size()-1).click();
				driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/div/ul/li/label/input")).clear();
				driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/div/ul/li/label/input")).sendKeys("2");
			} else if ("SELECCIO".equals(variable.getTipo())) {
				existeixElementAssert("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/select", "No tenía un select: " + variable.getEtiqueta());
				WebElement selectTipusExpedient = driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/select"));
				List<WebElement> options = selectTipusExpedient.findElements(By.tagName("option"));
				options.get(options.size()-1).click();
			} else if ("SUGGEST".equals(variable.getTipo())) {
				existeixElementAssert("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/input[@class='textInput']", "No tenía un input: " + variable.getEtiqueta());
				driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/input[@class='textInput']")).clear();
				driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/input[@class='textInput']")).sendKeys(usuari);
				driver.findElement(By.xpath("//*[@class='ac_results']/ul/li[1]")).click();
			} else if ("REGISTRE".equals(variable.getTipo())) {
				existeixElementAssert("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/div/button", "No tenía un botón: " + variable.getEtiqueta());
				driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/div/button")).click();
				
				String url = driver.findElement(By.xpath("//*[@id='"+variable.getCodi()+"']")).getAttribute("src");
				url = url.substring(url.indexOf("registre.html"));
				if (modalObertaAssert(url)) {
					vesAModal(url);
					
					for (VariableExpedient variableReg : variable.getRegistro()) {
						comprobarVariable(variableReg, true);
					}
					
					driver.findElement(By.xpath("//*[@id='command']/div[3]/button[1]")).click();
					tornaAPare();
				}
			} else if ("ACCIO".equals(variable.getTipo())) {
				existeixElementAssert("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/div/button", "No tenía un botón: " + variable.getEtiqueta());
			}
		}

		screenshotHelper.saveScreenshot("TasquesDadesTasca/visualizacio_tasca_dades/4-"+variable.getCodi()+".png");
	}
}
