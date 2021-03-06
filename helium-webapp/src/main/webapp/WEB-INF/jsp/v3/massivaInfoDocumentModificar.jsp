<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<html>
<head>
	<title><spring:message code='expedient.document.modificar' /></title>
	<hel:modalHead/>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.keyfilter-1.8.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery.price_format.1.8.min.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.maskedinput.js"/>"></script>
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/js/select2.min.js"/>"></script>
	<script src="<c:url value="/js/select2-locales/select2_locale_${idioma}.js"/>"></script>
	<script src="<c:url value="/js/helium3Tasca.js"/>"></script>
	
	<link href="<c:url value="/css/datepicker.css"/>" rel="stylesheet">
	<script src="<c:url value="/js/bootstrap-datepicker.js"/>"></script>
	<script src="<c:url value="/js/locales/bootstrap-datepicker.ca.js"/>"></script>
	
	<script src="<c:url value="/js/moment.js"/>"></script>
	<script src="<c:url value="/js/moment-with-locales.min.js"/>"></script>
	<script src="<c:url value="/js/bootstrap-datetimepicker.js"/>"></script>
	<link href="<c:url value="/css/bootstrap-datetimepicker.min.css"/>" rel="stylesheet">
	
</script>
<style type="text/css">
	.btn-file {position: relative; overflow: hidden;}
	.btn-file input[type=file] {position: absolute; top: 0; right: 0; min-width: 100%; min-height: 100%; font-size: 100px; text-align: right; filter: alpha(opacity = 0); opacity: 0; outline: none; background: white; cursor: inherit; display: block;}
	.col-xs-4 {width: 7%;}		
	.col-xs-8 {width: 93%;}
	#s2id_estatId {width: 100% !important;}
	#amagarFile { margin-left: -15px;padding-bottom: 15px;width: 100%;}
</style>
</head>
<body>
	<c:url value="/v3/expedient/document/arxiuMostrar" var="downloadUrl"><c:param name="token" value="${document.tokenSignatura}"/></c:url>
	<form:form cssClass="form-horizontal form-tasca" action="documentModificarMas" enctype="multipart/form-data" method="post" commandName="documentExpedientCommand">
		<div class="inlineLabels">
			<input id="inici" name="inici" value="${inici}" type="hidden"/>
			<input id="correu" name="correu" value="${correu}" type="hidden"/>
			<form:hidden path="docId"/>
			<form:hidden path="codi"/>
			<h4 class="titol-missatge">
				${documentExpedientCommand.nom}
	 			<c:if test="${document.plantilla}"> 
	 				<a title="<spring:message code='expedient.massiva.tasca.doc.generar' />" href="<c:url value="../../../../v3/expedient/massiva/documentGenerarMas"><c:param name="docId" value="${documentExpedientCommand.docId}"/><c:param name="inici" value="${inici}"/><c:param name="correu" value="${correu}"/></c:url>">
	 					<i class="fa fa-file-text-o"></i>
	 				</a>
	 			</c:if> 
 				<c:if test="${not empty document.tokenSignatura}">
					<a title="<spring:message code='comuns.descarregar' />" id="downloadUrl" href="${downloadUrl}">
						<i class="fa fa-download"></i>
					</a>
					<a title="<spring:message code='expedient.massiva.tasca.doc.borrar' />" id="removeUrl" href="#" onclick="return mostrarAmagarFile()">
						<i class="fa fa-times"></i>
					</a>
				</c:if>
			</h4>
			<div id="amagarFile" class="input-group <c:if test="${downloadUrl != ''}">hide</c:if>">
				<label class="control-label col-xs-4 obligatori" for="nom"><spring:message code='expedient.document.arxiu' /></label>
		        <div class="col-xs-8 arxiu">					
		            <div class="input-group">
		                <form:input path="contingut" readonly="readonly" cssClass="form-control" />
		                <span class="input-group-btn">
		                    <span class="btn btn-default btn-file">
		                        <spring:message code='expedient.document.arxiu' />… <input type="file" name="arxiu">
		                    </span>
		                </span>
		            </div>
				</div>
        	</div>
        
			<script type="text/javascript">
				// <![CDATA[
				$(document).on('change', '.btn-file :file', function() {
					var input = $(this),
					numFiles = input.get(0).files ? input.get(0).files.length : 1,
					label = input.val().replace(/\\/g, '/').replace(/.*\//, '');
					input.trigger('fileselect', [numFiles, label]);
				});
				
				$(document).ready( function() {
					$('.btn-file :file').on('fileselect', function(event, numFiles, label) {
						var input = $(this).parents('.input-group').find(':text'),
						log = numFiles > 1 ? numFiles + ' files selected' : label;
						if( input.length ) {
							input.val(log);
						} else {
							if( log )
								alert(log);
						}
					});
				}); 				

				function mostrarAmagarFile() {
					$("#amagarFile").removeClass("hide");
					$("#downloadUrl").hide();
					$("#removeUrl").hide();
				}
				// ]]>
			</script>
			
			<hel:inputDate required="true" name="data" textKey="expedient.document.data" placeholder="dd/mm/aaaa"/>
		</div>
		<div id="modal-botons" class="well">
			<button type="button" class="btn btn-default modal-tancar" style="float: none;" name="submit" value="cancel"><spring:message code="comu.boto.cancelar"/></button>
			<button class="btn btn-primary right" type="submit" style="float: none;" name="accio" value="document_modificar">
				<spring:message code='comuns.modificar' />
			</button>
		</div>
	</form:form>
</body>
</html>