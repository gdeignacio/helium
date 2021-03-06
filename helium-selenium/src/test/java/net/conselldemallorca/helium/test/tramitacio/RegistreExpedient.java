package net.conselldemallorca.helium.test.tramitacio;

import static org.junit.Assert.assertTrue;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.fail;
import net.conselldemallorca.helium.test.util.BaseTest;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RegistreExpedient extends BaseTest {

	String entorn = carregarPropietat("exptramit.registre.expedient.entorn.nom", "Nom de l'entorn de proves no configurat al fitxer de properties");
	String titolEntorn = carregarPropietat("exptramit.registre.expedient.entorn.titol", "Titol de l'entorn de proves no configurat al fitxer de properties");
	String codTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.codi", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");
	String nomDefProc = carregarPropietat("defproc.deploy.definicio.proces.nom", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String exportTipExpProc = carregarPropietatPath("tipexp.tasca_dades_doc.exp.export.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String nomSubDefProc = carregarPropietat("defproc.deploy.definicio.subproces.nom", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String pathDefProc = carregarPropietatPath("defproc.mod.exp.deploy.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String pathDefProcTermini = carregarPropietatPath("defproc.termini.exp.export.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String nomTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.nom", "Nom del tipus d'expedient de proves no configurat al fitxer de properties");
	String usuari = carregarPropietat("test.base.usuari.configuracio", "Usuari configuració de l'entorn de proves no configurat al fitxer de properties");
	
	// XPATHS
	String linkTasca 	= "//*[@id='registre']/tbody/tr[1]/td/a[contains(@href, '/tasca/info.html')]";
	String guardaTasca	= "//*[@id='command']/div/div[@class='buttonHolder']/button[text() = 'Guardar']";
	String botoConsultarExps  = "//*[@id='command']/div/div[@class='buttonHolder']/button[text() = 'Consultar']";
	String botoInformacioExp  = "//*[@id='registre']/tbody/tr[1]/td/a[contains(@href, '/expedient/info.html')]";
	String pestanyaRegistre   = "//*[@id='tabnav']/li/a[contains(@href, '/expedient/registre.html')]";
	String botoRetrocedir	  = "//*[@id='registre']/tbody/tr[2]/td/a[contains(@href, '/expedient/retrocedir.html')]";
	String texteAccioRetroces = "//*[@id='registre']/tbody/tr[2]/td[contains(text(), 'Retrocedir')]";
	String botoEliminarExp    = "//*[@id='registre']/tbody/tr/td/a[contains(@href, '/expedient/delete.html')]";
	
	String botoRegDetallat	  = "//*[@id='content']/form/div[@class='buttonHolder']/button";
	
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
		screenshotHelper.saveScreenshot("tramitar/dadesexpedient/crear_dades/1.png");
	}
	
	@Test
	public void a_iniciar_expedient() throws InterruptedException {
		carregarUrlConfiguracio(); 
		
		seleccionarEntorn(titolEntorn);

		screenshotHelper.saveScreenshot("RegistreExpedient/iniciar_expedient/1.png");

		desplegarDefinicioProcesEntorn(nomTipusExp, nomDefProc, pathDefProc);
		importarDadesDefPro(nomDefProc, pathDefProcTermini);

		screenshotHelper.saveScreenshot("RegistreExpedient/iniciar_expedient/2.png");
		
		iniciarExpediente( codTipusExp, "SE-22/2014", "Expedient de prova Selenium " + (new Date()).getTime());

		screenshotHelper.saveScreenshot("RegistreExpedient/iniciar_expedient/3.png");
	}

	@Test
	public void b_visualizar_tasques() throws InterruptedException {
		carregarUrlConfiguracio();

		seleccionarEntorn(titolEntorn);

		screenshotHelper.saveScreenshot("RegistreExpedient/visualizar_tasques/1.png");

		consultarExpedientes(null, null, nomTipusExp);

		screenshotHelper.saveScreenshot("RegistreExpedient/visualizar_tasques/2.png");

		assertTrue("No había ningún expediente", !driver.findElements(By.xpath("//*[@id='registre']/tbody/tr")).isEmpty());

		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]//a[contains(@href,'/expedient/info.html')]")).click();

		driver.findElement(By.xpath("//*[@id='tabnav']/li[8]/a")).click();

		screenshotHelper.saveScreenshot("RegistreExpedient/visualizar_tasques/3.png");

		if (!existeixElement("//*[@id='content']/form/div/button[contains(text(), 'Veure registre detallat')]")) {
			driver.findElement(By.xpath("//*[@id='content']/form/div/button")).click();
		}

		int i = 1;
		while (existeixElement("//*[@id='registre']/tbody/tr[" + i + "]")) {
			comprobarDatosRegistro(i, false);

			comprobarDatosRegistroModal("//*[@id='registre']/tbody/tr[" + i + "]/td[4]/span/a");
			comprobarDatosRegistroModal("//*[@id='registre']/tbody/tr[" + i + "]/td[5]/a");
			
			i++;
		}
		
		screenshotHelper.saveScreenshot("RegistreExpedient/visualizar_tasques/4.png");
	}

	@Test
	public void c_visualizar_tasques_detall() throws InterruptedException {
		carregarUrlConfiguracio();

		seleccionarEntorn(titolEntorn);

		screenshotHelper.saveScreenshot("RegistreExpedient/visualizar_tasques_detall/1.png");

		consultarExpedientes(null, null, nomTipusExp);

		screenshotHelper.saveScreenshot("RegistreExpedient/visualizar_tasques_detall/2.png");

		assertTrue("No había ningún expediente", !driver.findElements(By.xpath("//*[@id='registre']/tbody/tr")).isEmpty());

		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]//a[contains(@href,'/expedient/info.html')]")).click();

		driver.findElement(By.xpath("//*[@id='tabnav']/li[8]/a")).click();

		screenshotHelper.saveScreenshot("RegistreExpedient/visualizar_tasques_detall/3.png");

		if (existeixElement("//*[@id='content']/form/div/button[contains(text(), 'Veure registre detallat')]")) {
			driver.findElement(By.xpath("//*[@id='content']/form/div/button")).click();
		}

		int i = 1;
		while (existeixElement("//*[@id='registre']/tbody/tr[" + i + "]")) {
			comprobarDatosRegistro(i, false);

			comprobarDatosRegistroModal("//*[@id='registre']/tbody/tr[" + i + "]/td[4]/span/a");
			comprobarDatosRegistroModal("//*[@id='registre']/tbody/tr[" + i + "]/td[5]/a");

			i++;
		}
		
		screenshotHelper.saveScreenshot("RegistreExpedient/visualizar_tasques_detall/4.png");
	}

	@Test
	public void e_visualizar_accions_tasca() throws InterruptedException {
		carregarUrlConfiguracio();

		seleccionarEntorn(titolEntorn);

		screenshotHelper.saveScreenshot("RegistreExpedient/visualizar_accions_tasca/1.png");

		consultarExpedientes(null, null, nomTipusExp);

		screenshotHelper.saveScreenshot("RegistreExpedient/visualizar_accions_tasca/2.png");

		assertTrue("No había ningún expediente", !driver.findElements(By.xpath("//*[@id='registre']/tbody/tr")).isEmpty());

		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]//a[contains(@href,'/expedient/info.html')]")).click();

		driver.findElement(By.xpath("//*[@id='tabnav']/li[8]/a")).click();

		screenshotHelper.saveScreenshot("RegistreExpedient/visualizar_accions_tasca/3.png");

		if (!existeixElement("//*[@id='content']/form/div/button[contains(text(), 'Veure registre detallat')]")) {
			driver.findElement(By.xpath("//*[@id='content']/form/div/button")).click();
		}
		
		// Comprobamos desde el registro no detallado
		int i = 1;
		while (existeixElement("//*[@id='registre']/tbody/tr[" + i + "]")) {
			// Buscamos la fila
			String pathUrl = "//*[@id='registre']/tbody/tr[" + i + "]/td[4]/span/a";
			
			// Pulsamos sobre su acción
			if (existeixElement(pathUrl)) {
				WebElement url = driver.findElement(By.xpath(pathUrl));
				url.click();
				if (modalObertaAssert(url.getAttribute("href"))) {
					vesAModal(url.getAttribute("href"));
					
					"Guardar dades de la tasca".equals(comprobarDatosRegistro(1, false));
					"Validar dades de la tasca".equals(comprobarDatosRegistro(2, false));
					"Completar tramitació de la tasca".equals(comprobarDatosRegistro(3, false));
					
					tornaAPare();

					if (existeixElement("html/body/div[8]/div[1]/a/span")) {
						driver.findElement(By.xpath("html/body/div[8]/div[1]/a/span")).click();
					} else {
						driver.findElement(By.xpath("//*[@id='content']/h3")).click();
					}
				}
			}
			i++;
		}

		if (existeixElement("//*[@id='content']/form/div/button[contains(text(), 'Veure registre detallat')]")) {
			driver.findElement(By.xpath("//*[@id='content']/form/div/button")).click();
		}
		
		// Comprobamos desde el registro detallado
		i = 1;
		while (existeixElement("//*[@id='registre']/tbody/tr[" + i + "]")) {
			// Buscamos la fila
			String pathUrl = "//*[@id='registre']/tbody/tr[" + i + "]/td[4]/span/a";
			
			// Pulsamos sobre su acción
			if (existeixElement(pathUrl)) {
				WebElement url = driver.findElement(By.xpath(pathUrl));
				url.click();
				if (modalObertaAssert(url.getAttribute("href"))) {
					vesAModal(url.getAttribute("href"));
					
					"Guardar dades de la tasca".equals(comprobarDatosRegistro(1, false));
					"Validar dades de la tasca".equals(comprobarDatosRegistro(2, false));
					"Completar tramitació de la tasca".equals(comprobarDatosRegistro(3, false));
					
					tornaAPare();

					if (existeixElement("html/body/div[8]/div[1]/a/span")) {
						driver.findElement(By.xpath("html/body/div[8]/div[1]/a/span")).click();
					} else {
						driver.findElement(By.xpath("//*[@id='content']/h3")).click();
					}
				}
			}
			i++;
		}
		
		screenshotHelper.saveScreenshot("RegistreExpedient/visualizar_accions_tasca/4.png");
	}
		
	@Test
	public void f_retrocedir_tasca() throws InterruptedException {
		
		carregarUrlConfiguracio();
		
		seleccionarEntorn(titolEntorn);
		
		accedirTasquesPersonals();
		
		screenshotHelper.saveScreenshot("RegistreExpedient/retrocedirTasca/1_Llistat_tasques.png");
		
		driver.findElement(By.xpath(linkTasca)).click();
		
		screenshotHelper.saveScreenshot("RegistreExpedient/retrocedirTasca/2_Tasca_1.png");
		
		Calendar avull = Calendar.getInstance();
		driver.findElement(By.id("var_dat010")).sendKeys(new SimpleDateFormat("dd/MM/yyyy").format(avull.getTime()));
		
		driver.findElement(By.xpath(guardaTasca)).click();
		
		existeixElementAssert("//*[@class='missatgesOk']", "No s'han pogut guardar els canvis de la tasca del tipus d´expedient "+codTipusExp+".");
				
		screenshotHelper.saveScreenshot("RegistreExpedient/retrocedirTasca/3_Tasca_Guardada.png");
		
		accedirPantallaConsultes();
		
		driver.findElement(By.xpath(botoConsultarExps)).click();
		
		screenshotHelper.saveScreenshot("RegistreExpedient/retrocedirTasca/4_Llista_expedients.png");
		
		driver.findElement(By.xpath(botoInformacioExp)).click();
		
		driver.findElement(By.xpath(pestanyaRegistre)).click();
		
		screenshotHelper.saveScreenshot("RegistreExpedient/retrocedirTasca/5_tasques_expedient.png");
		
		driver.findElement(By.xpath(botoRetrocedir)).click();
		
		if (isAlertPresent()) { acceptarAlerta(); }
		
		screenshotHelper.saveScreenshot("RegistreExpedient/retrocedirTasca/6_tasca_retrocedida.png");
		
		if (existeixElement(botoRetrocedir)) {
			fail("RegistreExpedient/retrocedirTasca/7_error-tasca_NO_retrocedida.png");
		}
		
		//Eliminam l´expedient per fer la seguent prova
		accedirPantallaConsultes();		
		driver.findElement(By.xpath(botoConsultarExps)).click();
		
		while (existeixElement(botoEliminarExp)) {
			driver.findElement(By.xpath(botoEliminarExp)).click();
			if (isAlertPresent()) {acceptarAlerta();}
		}
	}

	@Test
	public void g_retrocedir_tasca_detallat() throws InterruptedException {
		
		carregarUrlConfiguracio();
		
		seleccionarEntorn(titolEntorn);
		
		iniciarExpediente(codTipusExp, "2222", "Titul exp. Retrocedir per detall.");
		
		accedirTasquesPersonals();
		
		screenshotHelper.saveScreenshot("RegistreExpedient/retrocedirTascaDetall/1_Llistat_tasques.png");
		
		driver.findElement(By.xpath(linkTasca)).click();
		
		screenshotHelper.saveScreenshot("RegistreExpedient/retrocedirTascaDetall/2_Tasca_1.png");
		
		Calendar avull = Calendar.getInstance();
		driver.findElement(By.id("var_dat010")).sendKeys(new SimpleDateFormat("dd/MM/yyyy").format(avull.getTime()));
		
		driver.findElement(By.xpath(guardaTasca)).click();
		
		Thread.sleep(3000);
		
		existeixElementAssert("//*[@class='missatgesOk']", "No s'han pogut guardar els canvis de la tasca del tipus d´expedient "+codTipusExp+".");
		
		screenshotHelper.saveScreenshot("RegistreExpedient/retrocedirTascaDetall/3_Tasca_Guardada.png");
		
		accedirPantallaConsultes();
		
		driver.findElement(By.xpath(botoConsultarExps)).click();
		
		screenshotHelper.saveScreenshot("RegistreExpedient/retrocedirTascaDetall/4_Llista_expedients.png");
		
		driver.findElement(By.xpath(botoInformacioExp)).click();
		
		driver.findElement(By.xpath(pestanyaRegistre)).click();
		
		screenshotHelper.saveScreenshot("RegistreExpedient/retrocedirTascaDetall/5_tasques_expedient.png");
		
		driver.findElement(By.xpath(botoRegDetallat)).click();
		
		screenshotHelper.saveScreenshot("RegistreExpedient/retrocedirTascaDetall/6_tasca_detall.png");
		
		driver.findElement(By.xpath(botoRetrocedir)).click();
		
		if (isAlertPresent()) { acceptarAlerta(); }
		
		screenshotHelper.saveScreenshot("RegistreExpedient/retrocedirTascaDetall/7_tasca_retrocedida.png");
		
		if (existeixElement(botoRetrocedir)) {
			fail("RegistreExpedient/retrocedirTasca/7_error-tasca_NO_retrocedida.png");
		}
	}

	@Test
	public void z_limpiar() throws InterruptedException {
		
		carregarUrlConfiguracio();
		
		seleccionarEntorn(titolEntorn);
		
		eliminarExpedient(null, null, nomTipusExp);
			
		// Eliminar la def de proceso
		eliminarDefinicioProces(nomDefProc);
		eliminarDefinicioProces(nomSubDefProc);
		
		// Eliminar el tipo de expediente
		eliminarTipusExpedient(codTipusExp);
		
		eliminarEntorn(entorn);
		
		screenshotHelper.saveScreenshot("TasquesDadesTasca/finalizar_expedient/1.png");	
	}

	// ********************************************
	// F U N C I O N S   P R I V A D E S
	// ********************************************
	
	private void accedirTasquesPersonals() {
		actions.moveToElement(driver.findElement(By.id("menuTasques")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/tasca/personaLlistat.html')]")));
		actions.click();
		actions.build().perform();
	}
	
	private void accedirPantallaConsultes() {
		actions.moveToElement(driver.findElement(By.id("menuConsultes")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/expedient/consulta.html')]")));
		try { Thread.sleep(1000); } catch (Exception ex) {}
		actions.click();
		actions.build().perform();
	}
	
	private String comprobarDatosRegistro(int i, boolean modal) {
		String data = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[" + i + "]/td[1]")).getText();
		assertTrue("La fecha de la fila " + i + " no seguía el formato correcto", isDate(data, "dd/MM/yyyy HH:mm:ss"));

		String responsable = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[" + i + "]/td[2]")).getText();
		assertTrue("El responsable de la fila " + i + " no estaba informado", !responsable.isEmpty());

		String element = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[" + i + "]/td[3]")).getText();

		String accio = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[" + i + "]/td[4]")).getText();
		boolean mostrarAccio = existeixElement("//*[@id='registre']/tbody/tr[" + i + "]/td[4]/span/a/img");

		String informacio = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[" + i + "]/td[5]")).getText();
		boolean mostrarInformacio = existeixElement("//*[@id='registre']/tbody/tr[" + i + "]/td[5]/a/img");

		String token = null;
		if (!modal) {
			token = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[" + i + "]/td[6]")).getText();
		}
		boolean retroceder = existeixElement("//*[@id='registre']/tbody/tr[" + i + "]/td[7]/a/img"); 
		
		// Comprobamos que todo se ajusta a lo establecido
		if ("Iniciar expedient".equals(accio)) {
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", informacio.isEmpty());
			assertTrue("El campo 'token' de la fila " + i + " no era correcto", modal ? true : !token.isEmpty());
			assertTrue("El campo 'element' de la fila " + i + " no era correcto", "Expedient".equals(element));
			assertTrue("El campo 'retroceder' de la fila " + i + " no era correcto", modal ? true : !retroceder);
			assertTrue("El campo 'accio' de la fila " + i + " no era correcto", !mostrarAccio);
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", !mostrarInformacio);
		} else if ("Crear variable al procés".equals(accio)) {
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", informacio.contains("Variable:"));
			assertTrue("El campo 'token' de la fila " + i + " no era correcto", modal ? true : !token.isEmpty());
			assertTrue("El campo 'element' de la fila " + i + " no era correcto", element.contains("Procés:"));
			assertTrue("El campo 'retroceder' de la fila " + i + " no era correcto", modal ? true : retroceder);
			assertTrue("El campo 'accio' de la fila " + i + " no era correcto", !mostrarAccio);
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", !mostrarInformacio);
		} else if ("Modificar variable del procés".equals(accio)) {
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", informacio.contains("Variable:"));
			assertTrue("El campo 'token' de la fila " + i + " no era correcto", modal ? true : !token.isEmpty());
			assertTrue("El campo 'element' de la fila " + i + " no era correcto", element.contains("Procés:"));
			assertTrue("El campo 'retroceder' de la fila " + i + " no era correcto", modal ? true : retroceder);
			assertTrue("El campo 'accio' de la fila " + i + " no era correcto", !mostrarAccio);
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", !mostrarInformacio);
		} else if ("Esborrar variable del procés".equals(accio)) {
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", informacio.contains("Variable:"));
			assertTrue("El campo 'token' de la fila " + i + " no era correcto", modal ? true : !token.isEmpty());
			assertTrue("El campo 'element' de la fila " + i + " no era correcto", element.contains("Procés:"));
			assertTrue("El campo 'retroceder' de la fila " + i + " no era correcto", modal ? true : retroceder);
			assertTrue("El campo 'accio' de la fila " + i + " no era correcto", !mostrarAccio);
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", !mostrarInformacio);
		} else if ("Afegir document al procés".equals(accio)) {
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", informacio.contains("Document:"));
			assertTrue("El campo 'token' de la fila " + i + " no era correcto", modal ? true : !token.isEmpty());
			assertTrue("El campo 'element' de la fila " + i + " no era correcto", element.contains("Procés:"));
			assertTrue("El campo 'retroceder' de la fila " + i + " no era correcto", modal ? true : retroceder);
			assertTrue("El campo 'accio' de la fila " + i + " no era correcto", !mostrarAccio);
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", !mostrarInformacio);
		} else if ("Modificar document del procés".equals(accio)) {
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", informacio.contains("Document:"));
			assertTrue("El campo 'token' de la fila " + i + " no era correcto", modal ? true : !token.isEmpty());
			assertTrue("El campo 'element' de la fila " + i + " no era correcto", element.contains("Procés:"));
			assertTrue("El campo 'retroceder' de la fila " + i + " no era correcto", modal ? true : retroceder);
			assertTrue("El campo 'accio' de la fila " + i + " no era correcto", !mostrarAccio);
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", !mostrarInformacio);
		} else if ("Esborrar document del procés".equals(accio)) {
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", informacio.contains("Document:"));
			assertTrue("El campo 'token' de la fila " + i + " no era correcto", modal ? true : !token.isEmpty());
			assertTrue("El campo 'element' de la fila " + i + " no era correcto", element.contains("Procés:"));
			assertTrue("El campo 'retroceder' de la fila " + i + " no era correcto", modal ? true : retroceder);
			assertTrue("El campo 'accio' de la fila " + i + " no era correcto", !mostrarAccio);
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", !mostrarInformacio);
		} else if ("Adjuntar document al procés".equals(accio)) {
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", informacio.contains("Document:"));
			assertTrue("El campo 'token' de la fila " + i + " no era correcto", modal ? true : !token.isEmpty());
			assertTrue("El campo 'element' de la fila " + i + " no era correcto", element.contains("Procés:"));
			assertTrue("El campo 'retroceder' de la fila " + i + " no era correcto", modal ? true : retroceder);
			assertTrue("El campo 'accio' de la fila " + i + " no era correcto", !mostrarAccio);
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", !mostrarInformacio);
		} else if ("Canviar responsable de la tasca".equals(accio)) {
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", !informacio.isEmpty());
			assertTrue("El campo 'token' de la fila " + i + " no era correcto", modal ? true : !token.isEmpty());
			assertTrue("El campo 'element' de la fila " + i + " no era correcto", element.contains("Tasca:"));
			assertTrue("El campo 'retroceder' de la fila " + i + " no era correcto", modal ? true : retroceder);
			assertTrue("El campo 'accio' de la fila " + i + " no era correcto", !mostrarAccio);
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", !mostrarInformacio);
		} else if ("Guardar dades de la tasca".equals(accio)) {
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", informacio.isEmpty());
			assertTrue("El campo 'token' de la fila " + i + " no era correcto", modal ? true : !token.isEmpty());
			assertTrue("El campo 'element' de la fila " + i + " no era correcto", element.contains("Tasca:"));
			assertTrue("El campo 'retroceder' de la fila " + i + " no era correcto", modal ? true : retroceder);
			assertTrue("El campo 'accio' de la fila " + i + " no era correcto", !mostrarAccio);
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", !mostrarInformacio);
		} else if ("Validar dades de la tasca".equals(accio)) {
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", informacio.isEmpty());
			assertTrue("El campo 'token' de la fila " + i + " no era correcto", modal ? true : !token.isEmpty());
			assertTrue("El campo 'element' de la fila " + i + " no era correcto", element.contains("Tasca:"));
			assertTrue("El campo 'retroceder' de la fila " + i + " no era correcto", modal ? true : retroceder);
			assertTrue("El campo 'accio' de la fila " + i + " no era correcto", !mostrarAccio);
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", !mostrarInformacio);
		} else if ("Restaurar dades de la tasca".equals(accio)) {
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", informacio.isEmpty());
			assertTrue("El campo 'token' de la fila " + i + " no era correcto", modal ? true : !token.isEmpty());
			assertTrue("El campo 'element' de la fila " + i + " no era correcto", element.contains("Tasca:"));
			assertTrue("El campo 'retroceder' de la fila " + i + " no era correcto", modal ? true : retroceder);
			assertTrue("El campo 'accio' de la fila " + i + " no era correcto", !mostrarAccio);
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", !mostrarInformacio);
		} else if ("Executar accio de la tasca".equals(accio)) {
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", !informacio.isEmpty());
			assertTrue("El campo 'token' de la fila " + i + " no era correcto", modal ? true : !token.isEmpty());
			assertTrue("El campo 'element' de la fila " + i + " no era correcto", element.contains("Tasca:"));
			assertTrue("El campo 'retroceder' de la fila " + i + " no era correcto", modal ? true : retroceder);
			assertTrue("El campo 'accio' de la fila " + i + " no era correcto", !mostrarAccio);
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", !mostrarInformacio);
		} else if ("Afegir document a la tasca".equals(accio)) {
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", !informacio.isEmpty());
			assertTrue("El campo 'token' de la fila " + i + " no era correcto", modal ? true : !token.isEmpty());
			assertTrue("El campo 'element' de la fila " + i + " no era correcto", element.contains("Tasca:"));
			assertTrue("El campo 'retroceder' de la fila " + i + " no era correcto", modal ? true : retroceder);
			assertTrue("El campo 'accio' de la fila " + i + " no era correcto", !mostrarAccio);
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", !mostrarInformacio);
		} else if ("Modificar document de la tasca".equals(accio)) {
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", !informacio.isEmpty());
			assertTrue("El campo 'token' de la fila " + i + " no era correcto", modal ? true : !token.isEmpty());
			assertTrue("El campo 'element' de la fila " + i + " no era correcto", element.contains("Tasca:"));
			assertTrue("El campo 'retroceder' de la fila " + i + " no era correcto", modal ? true : retroceder);
			assertTrue("El campo 'accio' de la fila " + i + " no era correcto", !mostrarAccio);
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", !mostrarInformacio);
		} else if ("Esborrar document de la tasca".equals(accio)) {
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", !informacio.isEmpty());
			assertTrue("El campo 'token' de la fila " + i + " no era correcto", modal ? true : !token.isEmpty());
			assertTrue("El campo 'element' de la fila " + i + " no era correcto", element.contains("Tasca:"));
			assertTrue("El campo 'retroceder' de la fila " + i + " no era correcto", modal ? true : retroceder);
			assertTrue("El campo 'accio' de la fila " + i + " no era correcto", !mostrarAccio);
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", !mostrarInformacio);
		} else if ("Suspendre execució de la tasca".equals(accio)) {
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", informacio.isEmpty());
			assertTrue("El campo 'token' de la fila " + i + " no era correcto", modal ? true : token.isEmpty());
			assertTrue("El campo 'element' de la fila " + i + " no era correcto", element.contains("Tasca:"));
			assertTrue("El campo 'retroceder' de la fila " + i + " no era correcto", modal ? true : !retroceder);
			assertTrue("El campo 'accio' de la fila " + i + " no era correcto", !mostrarAccio);
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", !mostrarInformacio);
		} else if ("Continuar execució de la tasca".equals(accio)) {
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", informacio.isEmpty());
			assertTrue("El campo 'token' de la fila " + i + " no era correcto", modal ? true : token.isEmpty());
			assertTrue("El campo 'element' de la fila " + i + " no era correcto", element.contains("Tasca:"));
			assertTrue("El campo 'retroceder' de la fila " + i + " no era correcto", modal ? true : !retroceder);
			assertTrue("El campo 'accio' de la fila " + i + " no era correcto", !mostrarAccio);
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", !mostrarInformacio);
		} else if ("Cancel·lar execució de la tasca".equals(accio)) {
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", informacio.isEmpty());
			assertTrue("El campo 'token' de la fila " + i + " no era correcto", modal ? true : token.isEmpty());
			assertTrue("El campo 'element' de la fila " + i + " no era correcto", element.contains("Tasca:"));
			assertTrue("El campo 'retroceder' de la fila " + i + " no era correcto", modal ? true : !retroceder);
			assertTrue("El campo 'accio' de la fila " + i + " no era correcto", !mostrarAccio);
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", !mostrarInformacio);
		} else if ("Modificar dades de l'expedient".equals(accio)) {
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", informacio.isEmpty());
			assertTrue("El campo 'token' de la fila " + i + " no era correcto", modal ? true : token.isEmpty());
			assertTrue("El campo 'element' de la fila " + i + " no era correcto", "Expedient".equals(element));
			assertTrue("El campo 'retroceder' de la fila " + i + " no era correcto", modal ? true : !retroceder);
			assertTrue("El campo 'accio' de la fila " + i + " no era correcto", !mostrarAccio);
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", !mostrarInformacio);
		} else if ("Aturar tramitació de l'expedient".equals(accio)) {
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", informacio.contains("Missatges:"));
			assertTrue("El campo 'token' de la fila " + i + " no era correcto", modal ? true : token.isEmpty());
			assertTrue("El campo 'element' de la fila " + i + " no era correcto", "Expedient".equals(element));
			assertTrue("El campo 'retroceder' de la fila " + i + " no era correcto", modal ? true : !retroceder);
			assertTrue("El campo 'accio' de la fila " + i + " no era correcto", !mostrarAccio);
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", !mostrarInformacio);
			
			// Debe existir un mensaje de "Expedient aturat: XXXX"
			existeixElementAssert("//*[@class='missatgesAturat']", "No se encontró el mensaje de error de la fila " + i);
			String mensaje = informacio.replaceAll("Missatges: ", "");
			String mensajeAturar = driver.findElement(By.xpath("//*[@class='missatgesAturat']/p")).getText();
			assertTrue("El mensaje de la fila " + i + " era incorrecto", ("Expedient aturat: " + mensaje).equals(mensajeAturar));
		} else if ("Reprendre tramitació de l'expedient".equals(accio)) {
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", informacio.isEmpty());
			assertTrue("El campo 'token' de la fila " + i + " no era correcto", modal ? true : !token.isEmpty());
			assertTrue("El campo 'element' de la fila " + i + " no era correcto", "Expedient".equals(element));
			assertTrue("El campo 'retroceder' de la fila " + i + " no era correcto", modal ? true : !retroceder);
			assertTrue("El campo 'accio' de la fila " + i + " no era correcto", !mostrarAccio);
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", !mostrarInformacio);
		} else if ("Afegir expedient relacionat".equals(accio)) {
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", informacio.isEmpty());
			assertTrue("El campo 'token' de la fila " + i + " no era correcto", modal ? true : token.isEmpty());
			assertTrue("El campo 'element' de la fila " + i + " no era correcto", "Expedient".equals(element));
			assertTrue("El campo 'retroceder' de la fila " + i + " no era correcto", modal ? true : !retroceder);
			assertTrue("El campo 'accio' de la fila " + i + " no era correcto", !mostrarAccio);
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", !mostrarInformacio);
		} else if ("Esborrar expedient relacionat".equals(accio)) {
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", informacio.isEmpty());
			assertTrue("El campo 'token' de la fila " + i + " no era correcto", modal ? true : token.isEmpty());
			assertTrue("El campo 'element' de la fila " + i + " no era correcto", "Expedient".equals(element));
			assertTrue("El campo 'retroceder' de la fila " + i + " no era correcto", modal ? true : !retroceder);
			assertTrue("El campo 'accio' de la fila " + i + " no era correcto", !mostrarAccio);
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", !mostrarInformacio);
		} else if ("Executar acció de l'expedient".equals(accio)) {
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", informacio.contains("Acció:"));
			assertTrue("El campo 'token' de la fila " + i + " no era correcto", modal ? true : !token.isEmpty());
			assertTrue("El campo 'element' de la fila " + i + " no era correcto", "Expedient".equals(element));
			assertTrue("El campo 'retroceder' de la fila " + i + " no era correcto", modal ? true : retroceder);
			assertTrue("El campo 'accio' de la fila " + i + " no era correcto", !mostrarAccio);
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", !mostrarInformacio);
		} else if ("Retrocedir accions de l'expedient".equals(accio)) {
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", informacio.isEmpty());
			assertTrue("El campo 'token' de la fila " + i + " no era correcto", modal ? true : token.isEmpty());
			assertTrue("El campo 'element' de la fila " + i + " no era correcto", "Expedient".equals(element));
			assertTrue("El campo 'retroceder' de la fila " + i + " no era correcto", modal ? true : !retroceder);
			assertTrue("El campo 'accio' de la fila " + i + " no era correcto", !mostrarAccio);
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", mostrarInformacio);
		} else if ("Retrocedir tasques de l'expedient".equals(accio)) {
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", informacio.isEmpty());
			assertTrue("El campo 'token' de la fila " + i + " no era correcto", modal ? true : token.isEmpty());
			assertTrue("El campo 'element' de la fila " + i + " no era correcto", "Expedient".equals(element));
			assertTrue("El campo 'retroceder' de la fila " + i + " no era correcto", modal ? true : !retroceder);
			assertTrue("El campo 'accio' de la fila " + i + " no era correcto", !mostrarAccio);
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", mostrarInformacio);
		} else if ("Completar tramitació de la tasca".equals(accio)) {
			assertTrue("El campo 'token' de la fila " + i + " no era correcto", modal ? true : !token.isEmpty());
			assertTrue("El campo 'element' de la fila " + i + " no era correcto", element.contains("Tasca:"));
			assertTrue("El campo 'retroceder' de la fila " + i + " no era correcto", modal ? true : retroceder);
			assertTrue("El campo 'accio' de la fila " + i + " no era correcto", !mostrarAccio);
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", !mostrarInformacio);
		} else if ("Executar script al procés".equals(accio)) {
			if (modal) {
				assertTrue("Error en el título de la ventana modal de la fila " + i + "", driver.findElement(By.xpath("//*[@id='DOMWindow']/h3")).getText().contains("Executar script al procés"));
				assertTrue("No re recuperó el texto del Script de la fila " + i + "", !driver.findElement(By.xpath("//*[@id='DOMWindow']/p")).getText().isEmpty());
			} else {
				assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", informacio.isEmpty());
				assertTrue("El campo 'token' de la fila " + i + " no era correcto", modal ? true : !token.isEmpty());
				assertTrue("El campo 'element' de la fila " + i + " no era correcto", element.contains("Procés:"));
				assertTrue("El campo 'retroceder' de la fila " + i + " no era correcto", modal ? true : !retroceder);
				assertTrue("El campo 'accio' de la fila " + i + " no era correcto", !mostrarAccio);
				assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", mostrarInformacio);
			}
		} else if ("Signar document de la tasca".equals(accio)) {
			// No se guarda
		} else if ("Actualitzar versió del procés".equals(accio)) {
			// No se guarda
		} else if ("Procesar document del portasignatures".equals(accio)) {
			
		} else if ("Tasca".equals(element)) {
			assertTrue("El campo 'token' de la fila " + i + " no era correcto", modal ? true : !token.isEmpty());
			assertTrue("El campo 'retroceder' de la fila " + i + " no era correcto", modal ? true : retroceder);
			assertTrue("El campo 'accio' de la fila " + i + " no era correcto", mostrarAccio);
			assertTrue("El campo 'informacio' de la fila " + i + " no era correcto", !mostrarInformacio);
		} else {
			// Otro caso
		}
		
		return accio;
	}

	private void comprobarDatosRegistroModal(String pathUrl) {
		if (existeixElement(pathUrl)) {
			WebElement url = driver.findElement(By.xpath(pathUrl));
			url.click();
			if (modalObertaAssert(url.getAttribute("href"))) {
				vesAModal(url.getAttribute("href"));
				
				int j = 1;
				while (existeixElement("//*[@id='registre']/tbody/tr[" + j + "]")) {
					comprobarDatosRegistro(j, true);
					
					j++;
				}
				
				tornaAPare();

				if (existeixElement("html/body/div[8]/div[1]/a/span")) {
					driver.findElement(By.xpath("html/body/div[8]/div[1]/a/span")).click();
				} else {
					driver.findElement(By.xpath("//*[@id='content']/h3")).click();
				}
			}
		}
	}
}
