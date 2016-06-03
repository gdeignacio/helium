
package net.conselldemallorca.helium.ws.tramitacio.v1;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.9-b14002
 * Generated source version: 2.2
 * 
 */
@WebServiceClient(name = "TramitacioServiceImplService", targetNamespace = "http://conselldemallorca.net/helium/ws/tramitacio/v1", wsdlLocation = "file:/home/likewise-open/LIMIT_CECOMASA/josepg/git/helium/doc/wsdl/TramitacioServiceV1.wsdl")
public class TramitacioServiceImplService
    extends Service
{

    private final static URL TRAMITACIOSERVICEIMPLSERVICE_WSDL_LOCATION;
    private final static WebServiceException TRAMITACIOSERVICEIMPLSERVICE_EXCEPTION;
    private final static QName TRAMITACIOSERVICEIMPLSERVICE_QNAME = new QName("http://conselldemallorca.net/helium/ws/tramitacio/v1", "TramitacioServiceImplService");

    static {
        URL url = null;
        WebServiceException e = null;
        try {
            url = new URL("file:/home/likewise-open/LIMIT_CECOMASA/josepg/git/helium/doc/wsdl/TramitacioServiceV1.wsdl");
        } catch (MalformedURLException ex) {
            e = new WebServiceException(ex);
        }
        TRAMITACIOSERVICEIMPLSERVICE_WSDL_LOCATION = url;
        TRAMITACIOSERVICEIMPLSERVICE_EXCEPTION = e;
    }

    public TramitacioServiceImplService() {
        super(__getWsdlLocation(), TRAMITACIOSERVICEIMPLSERVICE_QNAME);
    }

    public TramitacioServiceImplService(WebServiceFeature... features) {
        super(__getWsdlLocation(), TRAMITACIOSERVICEIMPLSERVICE_QNAME, features);
    }

    public TramitacioServiceImplService(URL wsdlLocation) {
        super(wsdlLocation, TRAMITACIOSERVICEIMPLSERVICE_QNAME);
    }

    public TramitacioServiceImplService(URL wsdlLocation, WebServiceFeature... features) {
        super(wsdlLocation, TRAMITACIOSERVICEIMPLSERVICE_QNAME, features);
    }

    public TramitacioServiceImplService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public TramitacioServiceImplService(URL wsdlLocation, QName serviceName, WebServiceFeature... features) {
        super(wsdlLocation, serviceName, features);
    }

    /**
     * 
     * @return
     *     returns TramitacioService
     */
    @WebEndpoint(name = "TramitacioServiceImplPort")
    public TramitacioService getTramitacioServiceImplPort() {
        return super.getPort(new QName("http://conselldemallorca.net/helium/ws/tramitacio/v1", "TramitacioServiceImplPort"), TramitacioService.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns TramitacioService
     */
    @WebEndpoint(name = "TramitacioServiceImplPort")
    public TramitacioService getTramitacioServiceImplPort(WebServiceFeature... features) {
        return super.getPort(new QName("http://conselldemallorca.net/helium/ws/tramitacio/v1", "TramitacioServiceImplPort"), TramitacioService.class, features);
    }

    private static URL __getWsdlLocation() {
        if (TRAMITACIOSERVICEIMPLSERVICE_EXCEPTION!= null) {
            throw TRAMITACIOSERVICEIMPLSERVICE_EXCEPTION;
        }
        return TRAMITACIOSERVICEIMPLSERVICE_WSDL_LOCATION;
    }

}
