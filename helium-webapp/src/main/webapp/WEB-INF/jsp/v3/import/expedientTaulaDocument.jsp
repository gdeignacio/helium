<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<c:set var="document" value="${dada}"/>
<c:set var="psignaPendentActual" value="${null}" scope="request"/>
<c:forEach var="pendent" items="${portasignaturesPendent}">
	<c:if test="${pendent.documentStoreId == document.id}"><c:set var="psignaPendentActual" value="${pendent}" scope="request"/></c:if>
</c:forEach>
<td id="cela-${expedientId}-${document.id}">									
	<c:choose>
		<c:when test="${not empty document.error}">
			<span class="fa fa-warning fa-2x" title="${document.error}"></span>
		</c:when>
		<c:otherwise>
			<table id="document_${document.id}" class="table-condensed marTop6 tableDocuments">
				<thead>
					<tr>
						<td class="left">
							<a href="<c:url value="/v3/expedient/${expedientId}/document/${document.id}/descarregar"/>">
								<span class="fa fa-file fa-4x" title="Descarregar document"></span>
								<c:if test="${document.adjunt}">
									<span class="adjuntIcon icon fa fa-paperclip fa-2x"></span>
								</c:if>
								<span class="extensionIcon">
									${fn:toUpperCase(document.arxiuExtensio)}
								</span>
							</a>
						</td>
						<td class="right">
							<c:if test="${not empty document.id}">
								<table class="marTop6 tableDocuments">
									<thead>
										<tr>
											<td class="tableDocumentsTd">
												<c:if test="${!document.signat && expedient.permisWrite}">
													<a 	href="../../v3/expedient/${expedientId}/document/${document.processInstanceId}/${document.id}/modificar"
														data-rdt-link-modal="true" 
														data-rdt-link-modal-min-height="265" 
														data-rdt-link-callback="recargarPanel(${document.processInstanceId});"
														class="icon modificar" >
															<span class="fa fa-2x fa-pencil" title="<spring:message code='expedient.document.modificar' />"></span>
													</a>
												</c:if>
												<c:if test="${document.signat}">	
												
													<c:choose>
														<c:when test="${not empty document.signaturaUrlVerificacio}">
															<a 	class="icon signature"
															   	data-rdt-link-modal="true" 
															   	data-rdt-link-modal-min-height="400" 
															   	href="${document.signaturaUrlVerificacio}">
																<span class="fa fa-2x fa-certificate" title="<spring:message code='expedient.document.signat' />"></span>
															</a>
														</c:when>
														<c:otherwise>																			
															<a 	data-rdt-link-modal="true"
																class="icon signature" 
																href="<c:url value='../../v3/expedient/${expedientId}/verificarSignatura/${document.processInstanceId}/${document.id}/${document.documentCodi}'/>?urlVerificacioCustodia=${document.signaturaUrlVerificacio}">
																<span class="fa fa-2x fa-certificate" title="<spring:message code='expedient.document.signat' />"></span>
															</a>
														</c:otherwise>
													</c:choose>
													<c:if test="${expedient.permisWrite}">
														<a 	class="icon signature fa-stack fa-2x" 
															data-rdt-link-confirm="<spring:message code='expedient.document.confirm_esborrar_signatures' />"
															data-rdt-link-ajax=true
															href='<c:url value="../../v3/expedient/${expedientId}/document/${document.id}/signaturaEsborrar"/>' 
															data-rdt-link-callback="esborrarSignatura(${document.id});"
															title="<spring:message code='expedient.document.esborrar.signatures' />">
															<i class="fa fa-certificate fa-stack-1x"></i>
														  	<i class="fa fa-ban fa-stack-2x text-danger"></i>
														</a>
													</c:if>
												</c:if>
												<c:if test="${document.registrat}">
													<a 	data-rdt-link-modal="true" 
														class="icon registre" 
														href="<c:url value='../../v3/expedient/${expedientId}/verificarRegistre/${document.processInstanceId}/${document.id}/${document.documentCodi}'/>">
														<span class="fa fa-book fa-2x" title="<spring:message code='expedient.document.registrat' />"></span>
													</a>
												</c:if>
												<c:if test="${expedient.permisWrite}">
													<a 	class="icon fa fa-trash-o fa-2x" 
														data-rdt-link-confirm="<spring:message code='expedient.document.confirm_esborrar_proces' />"
														data-rdt-link-ajax=true
														href='<c:url value="../../v3/expedient/${expedientId}/document/${document.processInstanceId}/${document.id}/esborrar"/>' 
														data-rdt-link-callback="recargarPanel(${document.processInstanceId});"
														title="<spring:message code='expedient.document.esborrar'/>">
													</a>
												</c:if>
												<!-- FRAGMENT INFO FIRMA PENDENT -->
												<c:if test="${not empty psignaPendentActual}">
													<c:choose>
														<c:when test="${psignaPendentActual.error}">
															<a 	data-psigna = "${document.id}"
																class="icon fa fa-exclamation-triangle fa-2x psigna-info" 
																style="cursor:pointer"
																title="<spring:message code='expedient.document.pendent.psigna.error'/>">
															</a>
															<c:if test="${psignaPendentActual.error}">
																<c:if test="${expedient.permisWrite or expedient.permisAdministration}">
																	<form id="form_psigna_${document.id}" action="<c:url value='/expedient/documentPsignaReintentar.html'/>">
																		<input type="hidden" name="id" value="${document.processInstanceId}"/>
																		<input type="hidden" name="psignaId" value="${psignaPendentActual.documentId}"/>
																	</form>
																</c:if>
															</c:if>
														</c:when>
														<c:otherwise>
															<a 	data-psigna = "${document.id}"
																class="icon fa fa-clock-o fa-2x psigna-info"
																style="cursor:pointer" 
																title="<spring:message code='expedient.document.pendent.psigna'/>">
															</a>
														</c:otherwise>
													</c:choose>
												</c:if>
												<!-- FI FRAGMENT -->
											</td>
										</tr>
										<tr>
											<td>
												<spring:message code='expedient.document.data' /> <fmt:formatDate value="${document.dataDocument}" pattern="dd/MM/yyyy"/>
											</td>
										</tr>
										<c:if test="${not empty document.dataCreacio}">
											<tr>
												<td>
													<spring:message code='expedient.document.adjuntat' /> <fmt:formatDate value="${document.dataCreacio}" pattern="dd/MM/yyyy hh:mm"/>
												</td>
											</tr>
										</c:if>
									</thead>
								</table>
							</c:if>
						</td>
					</tr>
				</thead>
				<tbody>
					<tr>
						<td colspan="2">
							<strong class="nom_document">
								<c:choose>
									<c:when test="${not document.adjunt}">${document.documentNom}</c:when>
									<c:otherwise>${document.adjuntTitol}</c:otherwise>
								</c:choose>
							</strong><br/>
						</td>
					</tr>
				</tbody>
			</table>
		</c:otherwise>
	</c:choose>
</td>
<c:if test="${not empty psignaPendentActual}">
	<div id="psigna_${document.id}" class="modal fade">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-label="<spring:message code="comu.boto.tancar"/>"><span aria-hidden="true">&times;</span></button>
					<h4 class="modal-title">Document pendent del portasignatures</h4>
				</div>
				<div class="modal-body">
					<ul class="list-group">
					  	<li class="list-group-item"><strong><spring:message code="common.icones.doc.psigna.id"/></strong><span class="pull-right">${psignaPendentActual.documentId}</span></li>
					  	<li class="list-group-item"><strong><spring:message code="common.icones.doc.psigna.data.enviat"/></strong><span class="pull-right"><fmt:formatDate value="${psignaPendentActual.dataEnviat}" pattern="dd/MM/yyyy HH:mm"/></span></li>
					  	<li class="list-group-item"><strong><spring:message code="common.icones.doc.psigna.estat"/></strong><span class="pull-right">${psignaPendentActual.estat}</span></li>
						<c:if test="${not empty psignaPendentActual.motiuRebuig}">
							<li class="list-group-item"><strong><spring:message code="common.icones.doc.psigna.motiu.rebuig"/></strong><span class="pull-right">${psignaPendentActual.motiuRebuig}</span></li>
						</c:if>
						<c:if test="${not empty psignaPendentActual.dataProcessamentPrimer}">
							<li class="list-group-item"><strong><spring:message code="common.icones.doc.psigna.data.proces.primer"/></strong><span class="pull-right"><fmt:formatDate value="${psignaPendentActual.dataProcessamentPrimer}" pattern="dd/MM/yyyy HH:mm"/></span></li>
						</c:if>
						<c:if test="${not empty psignaPendentActual.dataProcessamentDarrer}">
							<li class="list-group-item"><strong><spring:message code="common.icones.doc.psigna.data.proces.darrer"/></strong><span class="pull-right"><fmt:formatDate value="${psignaPendentActual.dataProcessamentDarrer}" pattern="dd/MM/yyyy HH:mm"/></span></li>
						</c:if>
					</ul>
					<c:if test="${psignaPendentActual.error}">
						<div class="panel-group" id="accordion" role="tablist" aria-multiselectable="true">
							<div class="panel panel-default">
								<div class="panel-heading" role="tab" id="headingOne">
									<h4 class="panel-title">
										<a role="button" data-toggle="collapse" data-parent="#accordion" href="#collapseOne" aria-expanded="true" aria-controls="collapseOne">
											<strong><spring:message code="common.icones.doc.psigna.error.processant"/></strong>
										</a>
									</h4>
								</div>
								<div id="collapseOne" class="panel-collapse collapse in" role="tabpanel" aria-labelledby="headingOne">
									<div class="panel-body panell-error">
										${psignaPendentActual.errorProcessant}
									</div>
								</div>
							</div>
						</div>
					</c:if>
				</div>
				<div class="modal-footer">
					<c:if test="${psignaPendentActual.error}">
						<c:if test="${expedient.permisWrite or expedient.permisAdministration}">
							<button type="button" class="btn btn-primary"  onclick="reprocessar(${document.id})">
								<i class="fa fa-file-text-o"></i> <spring:message code="common.icones.doc.psigna.reintentar"/>
							</button>
						</c:if>
					</c:if>
					<button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="comu.boto.tancar"/></button>
				</div>
			</div>
		</div>
	</div>
</c:if>