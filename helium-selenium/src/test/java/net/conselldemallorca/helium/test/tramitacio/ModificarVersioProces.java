package net.conselldemallorca.helium.test.tramitacio;

import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import net.conselldemallorca.helium.test.util.BaseTest;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ModificarVersioProces extends BaseTest {

	String entorn = carregarPropietat("entorn.nom", "Nom de l'entorn de proves no configurat al fitxer de properties");
	String codTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.codi", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");
	String nomDefProc = carregarPropietat("defproc.deploy.definicio.proces.nom", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String pathDefProc = carregarPropietat("defproc.mod.exp.deploy.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String nomTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.nom", "Nom del tipus d'expedient de proves no configurat al fitxer de properties");
	
	@Test
	public void a_modificarVersioExp() throws InterruptedException {
		carregarUrlConfiguracio(); 
		
		// Selecció directe
		actions.moveToElement(driver.findElement(By.id("menuEntorn")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//li[@id='menuEntorn']/ul[@class='llista-entorns']/li[contains(., '" + entorn + "')]/a")));
		actions.click();
		actions.build().perform();
		
		screenshotHelper.saveScreenshot("tramitar/modificarInfoExp/1.png");
		
		desplegarDefinicioProcesEntorn(nomTipusExp, nomDefProc, pathDefProc);
		
		// No hay expedientes. Iniciamos uno
		importarDadesDefPro(nomDefProc, properties.getProperty("defproc.mod.exp.export.arxiu.path"));
					
		String[] res = iniciarExpediente(nomDefProc,codTipusExp,"SE-22/2014", "Expedient de prova Selenium " + (new Date()).getTime() );
				
		actions.moveToElement(driver.findElement(By.id("menuConsultes")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//*[@id='menuConsultes']/ul/li[1]/a")));
		actions.click();
		actions.build().perform();
		
		WebElement selectTipusExpedient = driver.findElement(By.xpath("//*[@id='expedientTipus0']"));
		List<WebElement> options = selectTipusExpedient.findElements(By.tagName("option"));
		for (WebElement option : options) {
			if (option.getText().equals(properties.getProperty("defproc.deploy.tipus.expedient.nom"))) {
				option.click();
				break;
			}
		}
		
		driver.findElement(By.xpath("//*[@id='command']/div[2]/div[6]/button[1]")).click();	
		
		screenshotHelper.saveScreenshot("tramitar/modificarInfoExp/2.png");
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]/td[6]/a/img")).click();	
					
		// Empezamos a cambiar la versión
		driver.findElement(By.xpath("//*[@id='tabnav']/li[9]/a")).click();
		
		driver.findElement(By.xpath("//*[@id='content']/div/h3[3]/a")).click();
		
		driver.findElement(By.xpath("//*[@id='definicioProcesId0']")).click();
		WebElement selectDef = driver.findElement(By.xpath("//*[@id='definicioProcesId0']"));
		List<WebElement> allOptions = selectDef.findElements(By.tagName("option"));
		String versionCambiada = null;
		for (WebElement option : allOptions) {
			if (option.equals(allOptions.get(0))) {
				versionCambiada = option.getText().trim();
				option.click();
				break;
			}
		}
		
		driver.findElement(By.xpath("//*[@id='canviVersioProcesCommand']/div[3]/button")).click();
		acceptarAlerta();
		
		existeixElementAssert("//*[@id='infos']","No se cambió la versión");
		
		screenshotHelper.saveScreenshot("tramitar/modificarInfoExp/3.png");
		
		driver.findElement(By.xpath("//*[@id='tabnav']/li[1]/a")).click();
		
		// Vemos la versión
		WebElement element = driver.findElement(By.xpath("//*[@id='content']/dl"));
		
		List<WebElement> claves = element.findElements(By.xpath("dt"));
		List<WebElement> valores = element.findElements(By.xpath("dd"));

		String versionNueva = null;
		for (int i=0; i < claves.size() ; i++) {
			if ("Definició de procés".equals(claves.get(i).getText())) {
				versionNueva = valores.get(i).getText().trim();
				break;
			}
		}	
		assertTrue("La versión no ha cambiado", versionNueva.equals(versionCambiada));	
		
		screenshotHelper.saveScreenshot("tramitar/modificarInfoExp/4.png");
		
		eliminarExpedient(res[0], res[1]);
			
		// Eliminar la def de proceso
		eliminarDefinicioProces(nomDefProc);
		
		screenshotHelper.saveScreenshot("tramitar/modificarInfoExp/5.png");
	}
}
