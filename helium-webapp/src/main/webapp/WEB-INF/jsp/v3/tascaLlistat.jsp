<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<html>
<head>
	<title><spring:message code="tasca.llistat.titol"/></title>
	<meta name="capsaleraTipus" content="llistat"/>
	<meta name="title" content="<spring:message code='tasca.llistat.titol'/>"/>
	<meta name="title-icon-class" content="fa fa-clipboard"/>
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/DT_bootstrap.css"/>" rel="stylesheet">
	<script src="<c:url value="/js/jquery/jquery.maskedinput.js"/>"></script>
	<script src="<c:url value="/js/jquery.dataTables.js"/>"></script>
	<script src="<c:url value="/js/DT_bootstrap.js"/>"></script>
	<script src="<c:url value="/js/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/helium.datatable.js"/>"></script>
	<script src="<c:url value="/js/select2.min.js"/>"></script>
	<script src="<c:url value="/js/select2-locales/select2_locale_${idioma}.js"/>"></script>
	
	<script src="<c:url value="/js/moment.js"/>"></script>
	<script src="<c:url value="/js/moment-with-locales.min.js"/>"></script>
	<script src="<c:url value="/js/bootstrap-datetimepicker.js"/>"></script>
	<link href="<c:url value="/css/bootstrap-datetimepicker.min.css"/>" rel="stylesheet">
	
	<script type="text/javascript">
		$(document).ready(function() {
			$("#taulaDades").heliumDataTable({
				ajaxSourceUrl: "<c:url value="/v3/tasca/datatable"/>",
				localeUrl: "<c:url value="/js/dataTables-locales/dataTables_locale_ca.txt"/>",
				alertesRefreshUrl: "<c:url value="/nodeco/v3/missatges"/>",
				rowClickCallback: function(row) {
					<c:if test="${tascaConsultaCommand.consultaTramitacioMassivaTascaId == null}">
						if ($('a.consultar-tasca', $(row)).length > 0)
							$('a.consultar-tasca', $(row))[0].click();
					</c:if>
				},
				seleccioCallback: function(seleccio) {
					$('#reasignacioMassivaCount').html(seleccio.length);
					<c:if test="${tascaConsultaCommand.consultaTramitacioMassivaTascaId != null}">
						$('#tramitacioMassivaCount').html(seleccio.length);
					</c:if>
				}
			});	
			$("button[data-toggle=button]").click(function() {
				$("input[name="+$(this).data("path")+"]").val(!$(this).hasClass('active'));
				$(this).blur();
				$("button[value=consultar]").click();
			});
			$('.date_time').datetimepicker({
				locale: moment.locale('${idioma}'),
				minDate: new Date(),
				format: "DD/MM/YYYY HH:mm"
		    });
			$("button[data-toggle=button]").click(function() {
				$("input[name="+$(this).data("path")+"]").val(!$(this).hasClass('active'));
				$(this).blur();
				actualizarBotonesFiltros($(this).attr('id'));
				$("button[value=consultar]").click();
			});
						
			<c:if test="${entornId != null}">
				$('#expedientTipusId').on('change', function() {
					var tipus = $(this).val();
					$('#tasca').select2('val', '', true);
					$('#tasca option[value!=""]').remove();
					
					var value = -1;
					var entornId = ${entornId};
					if ($(this).val())
						value = $(this).val();
					
					//tasques per expedientTipus
					$.get('tasca/tasques/${entornId}/' + value)				
					.done(function(data) {
						for (var i = 0; i < data.length; i++) {
							$('#tasca').append('<option value="' + data[i].codi + '">' + data[i].valor + '</option>');
						}
					})
					.fail(function() {
						alert("<spring:message code="expedient.llistat.tasca.ajax.error"/>");
					});

					//permisos d'expedientTipus
					if (value != undefined && value != "-1"){
						$.get('tasca/expedientTipusAmbPermis/${entornId}/' + value)				
						.done(function(data) {
							if(data != undefined && data.permisReassignment){
								$('#responsableDiv').show();
							}else{
								$('#responsableDiv').hide();
								if($('#responsable').data('select2'))
									$('#responsable').data('select2').clear();
							}
						})
						.fail(function() {
							alert("<spring:message code="expedient.llistat.expedientTipusPermis.ajax.error"/>");
						});
					}else{
						$('#responsableDiv').hide();
						if($('#responsable').data('select2'))
							$('#responsable').data('select2').clear();
					}
					
				});
	
				$('#expedientTipusId').trigger('change');
			</c:if>
			actualizarBotonesFiltros();
		});
		function massivaTasca(element, tipo) {
			var href = null;
			
			if (tipo === 'reassignacio') href = "<c:url value='/modal/v3/tasca/massivaReassignacioTasca'/>";
			else if (tipo === 'tramitacio') href = "<c:url value='/modal/v3/expedient/massivaTramitacioTasca'/>";
			else return false;
			
			$(element).attr('href', href + "?massiva=${tascaConsultaCommand.consultaTramitacioMassivaTascaId != null}&inici="+$('#inici').val()+"&correu="+$('#correu').is(':checked'));
		}	
		function actualizarBotonesFiltros(id) {
			$('#nomesTasquesPersonalsCheck').attr('disabled', false);
			$('#nomesTasquesGrupCheck').attr('disabled', false);
			$('#responsable').select2("val", "", true);
			$('#responsable').attr('disabled', false);
			var nomesTasquesPersonals = ($('#nomesTasquesPersonalsCheck').hasClass('active') && id == null) || (!$('#nomesTasquesPersonalsCheck').hasClass('active') && id == 'nomesTasquesPersonalsCheck') || ($('#nomesTasquesPersonalsCheck').hasClass('active') && id != 'nomesTasquesPersonalsCheck');
			var nomesTasquesGrup = ($('#nomesTasquesGrupCheck').hasClass('active') && id == null) || (!$('#nomesTasquesGrupCheck').hasClass('active') && id == 'nomesTasquesGrupCheck') || ($('#nomesTasquesGrupCheck').hasClass('active') && id != 'nomesTasquesGrupCheck');
			if (nomesTasquesPersonals) {
				$('#nomesTasquesGrupCheck').attr('disabled', true);
			}
			if (nomesTasquesGrup) {
				$('#nomesTasquesPersonalsCheck').attr('disabled', true);
			}
		}
		function agafar(tascaId, correcte) {
			if (correcte) {
 				var start = new Date().getTime();	
				$("#taulaDades").dataTable().fnDraw();
				setTimeout(function (){					   
	 				do {
	 					while (new Date().getTime() < start + 500);
	 				} while ($('.datatable-dades-carregant', $("#taulaDades")).is(':visible'));
			    	$('#dropdown-menu-'+tascaId+' #tramitar-tasca-'+tascaId).click();
				}, 1000);
			}
		}
		function alliberar(tascaId, correcte) {
			if (correcte) {
				$("#taulaDades").dataTable().fnDraw();
			}
		}
		function seleccionarMassivaTodos() {
			var numColumna = $("#taulaDades").data("rdt-seleccionable-columna");
			if ($('#taulaDades').find('th:eq('+numColumna+')').find('input[type=checkbox]')[0].checked) {
				$('#taulaDades').find('th:eq('+numColumna+')').find('input[type=checkbox]')[0].checked = false;
			}
			$('#taulaDades').find('th:eq('+numColumna+')').find('input[type=checkbox]')[0].click();
		}
		function deseleccionarMassivaTodos() {
			var numColumna = $("#taulaDades").data("rdt-seleccionable-columna");
			if (!$('#taulaDades').find('th:eq('+numColumna+')').find('input[type=checkbox]')[0].checked) {
				$('#taulaDades').find('th:eq('+numColumna+')').find('input[type=checkbox]')[0].checked = true;
			}
			$('#taulaDades').find('th:eq('+numColumna+')').find('input[type=checkbox]')[0].click();
		}
	</script>
	<style type="text/css">
		#opciones .label-titol {padding-bottom: 0px;} 
 		.control-group {width: 100%;display: inline-block;} 
 		.control-group-mid {width: 48%;} 
  		.control-group.left {float: left; margin-right:1%;} 
  		#div_timer label {float:left;} 
	</style>
</head>
<body>
	<form:form action="" method="post" cssClass="well formbox" commandName="tascaConsultaCommand">
		<form:hidden path="filtreDesplegat"/>
		<c:choose>
			<c:when test="${tascaConsultaCommand.consultaTramitacioMassivaTascaId != null}">
				<div id="div_timer" class="control-group left control-group-mid">
			    	<label for="inici"><spring:message code="expedient.consulta.datahorainici" /></label>
					<div class='col-sm-6'>
			            <div class="form-group">
			                <div class='input-group date date_time' id='inici_timer'>
			                    <input id="inici" name="inici" class="form-control date_time" data-format="dd/MM/aaaa hh:mm" type="text">
			                    <span class="input-group-addon"><span class="fa fa-calendar"></span></span>
			                </div>
			            </div>
			            <script type="text/javascript">
		                    $(document).ready(function() {
								$("#inici").on('focus', function() {
									$('.fa-calendar').click();
								});
							});
	                    </script>
			    	</div>
				</div>
				
				<div class="control-group form-group control-group-mid">
					<input type="checkbox" id="correu" name="correu" value="${correu}"/>
					<label for="correu"><spring:message code="expedient.massiva.correu"/></label>
				</div>
			</c:when>
			<c:otherwise>
				<div id="filtresCollapsable" class="collapse<c:if test="${true or tascaConsultaCommand.filtreDesplegat}"> in</c:if>">
					<div class="row">
						<div class="col-md-3">
							<hel:inputText name="titol" textKey="tasca.llistat.filtre.camp.titol" placeholderKey="tasca.llistat.filtre.camp.titol" inline="true"/>
						</div>
						<div class="col-md-3">
							<hel:inputText name="expedient" textKey="tasca.llistat.filtre.camp.expedient" placeholderKey="tasca.llistat.filtre.camp.expedient" inline="true"/>
						</div>
						<div class="col-md-3">
							<hel:inputSelect emptyOption="true" name="expedientTipusId" textKey="tasca.llistat.filtre.camp.tipexp" placeholderKey="tasca.llistat.filtre.camp.tipexp" optionItems="${expedientTipusAccessibles}" optionValueAttribute="id" optionTextAttribute="nom"  disabled="${not empty expedientTipusActual}" inline="true"/>
						</div>		
						<div class="col-md-3">
							<hel:inputSelect emptyOption="true" inline="true" name="tasca" textKey="tasca.llistat.filtre.camp.tasca" placeholderKey="tasca.llistat.filtre.camp.tasca"/>
						</div>
						<div class="col-md-4">
							<label><spring:message code="tasca.llistat.filtre.camp.datcre"/></label>
							<div class="row">
								<div class="col-md-6">
									<hel:inputDate name="dataCreacioInicial" textKey="tasca.llistat.filtre.camp.datcre.ini" placeholder="dd/mm/aaaa" inline="true"/>
								</div>
								<div class="col-md-6">
									<hel:inputDate name="dataCreacioFinal" textKey="tasca.llistat.filtre.camp.datcre.fin" placeholder="dd/mm/aaaa" inline="true"/>
								</div>
							</div>
						</div>
						<div class="col-md-4">
							<label><spring:message code="tasca.llistat.filtre.camp.datlim"/></label>
							<div class="row">
								<div class="col-md-6">
									<hel:inputDate name="dataLimitInicial" textKey="tasca.llistat.filtre.camp.datlim.ini" placeholder="dd/mm/aaaa" inline="true"/>
								</div>
								<div class="col-md-6">
									<hel:inputDate name="dataLimitFinal" textKey="tasca.llistat.filtre.camp.datlim.fin" placeholder="dd/mm/aaaa" inline="true"/>
								</div>
							</div>
						</div>
						<div class="col-md-4">				
							<label>&nbsp;</label>
							<div class="row">
							<c:choose>
								<c:when test="${not empty expedientTipus and expedientTipus.permisReassignment}">
									<div class="col-md-12"  id="responsableDiv">
										<hel:inputSuggest inline="true" name="responsable" urlConsultaInicial="tasca/persona/suggestInici" urlConsultaLlistat="tasca/persona/suggest" textKey="expedient.editar.responsable" placeholderKey="expedient.editar.responsable"/>
									</div>
								</c:when>
								<c:otherwise>
									<div class="col-md-12"  id="responsableDiv" style="display: none">
										<hel:inputSuggest inline="true" name="responsable" urlConsultaInicial="tasca/persona/suggestInici" urlConsultaLlistat="tasca/persona/suggest" textKey="expedient.editar.responsable" placeholderKey="expedient.editar.responsable"/>
									</div>
								</c:otherwise>
								</c:choose>
							</div>
						</div>
					</div>
				</div>
				
				<button style="display:none" type="submit" name="accio" value="consultar"></button>
				
				<div class="row">						
					<div class="col-md-12">
						<form:hidden path="nomesTasquesPersonals"/>
						<form:hidden path="nomesTasquesGrup"/>
						<div class="row">
							<div class="col-md-6 btn-group">
								<button id="nomesTasquesPersonalsCheck" data-path="nomesTasquesPersonals" title="<spring:message code="tasca.llistat.filtre.camp.personals"/>" class="btn btn-default<c:if test="${tascaConsultaCommand.nomesTasquesPersonals}"> active</c:if>" data-toggle="button"><span class="fa fa-user"></span></button>
								<button id="nomesTasquesGrupCheck" data-path="nomesTasquesGrup" title="<spring:message code="tasca.llistat.filtre.camp.grup"/>" class="btn btn-default<c:if test="${tascaConsultaCommand.nomesTasquesGrup}"> active</c:if>" data-toggle="button"><span class="fa fa-users"></span></button>
							</div>
							<div class="col-md-6">
							<div class="pull-right">
								<input type="hidden" name="consultaRealitzada" value="true"/>
								<button type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.filtre.netejar"/></button>
								<button type="submit" name="accio" value="consultar" class="btn btn-primary"><span class="fa fa-filter"></span>&nbsp;<spring:message code="comu.filtre.filtrar"/></button>
							</div>
							</div>
						</div>
					</div>
				</div>
			</c:otherwise>
		</c:choose>
	</form:form>
	<table id="taulaDades" class="table table-striped table-bordered table-hover" data-rdt-button-template="tableButtonsTemplate" <c:if test="${tascaConsultaCommand.consultaTramitacioMassivaTascaId != null}"> data-rdt-paginable="false"</c:if> data-rdt-seleccionable-columna="0" data-rdt-filtre-form-id="tascaConsultaCommand" data-rdt-seleccionable="true" <c:if test="${not empty preferenciesUsuari.numElementosPagina}">data-rdt-display-length-default="${preferenciesUsuari.numElementosPagina}"</c:if>>
		<thead>
			<tr data-toggle="context" data-target="#context-menu">
				<th data-rdt-property="id" width="4%" data-rdt-sortable="false" data-rdt-visible="true"></th>
				<th data-rdt-property="titol" data-rdt-template="cellPersonalGroupTemplate" data-rdt-visible="true" >
					<spring:message code="tasca.llistat.columna.titol"/>
					<script id="cellPersonalGroupTemplate" type="text/x-jsrender">
						{{:titol}}
						{{if !agafada && responsables != null}}
							<span class="fa fa-users" title="<spring:message code="enum.tasca.etiqueta.grup"/>"></span>
						{{/if}}
						<div class="pull-right">
							{{if cancelled}}
								<span class="label label-danger" title="<spring:message code="enum.tasca.etiqueta.CA"/>">CA</span>
							{{/if}}
							{{if suspended}}
								<span class="label label-info" title="<spring:message code="enum.tasca.etiqueta.SU"/>">SU</span>
							{{/if}}
							{{if open}}
								<span class="label label-warning" title="<spring:message code="enum.tasca.etiqueta.PD"/>"></span>
							{{/if}}
							{{if completed}}
								<span class="label label-success" title="<spring:message code="enum.tasca.etiqueta.FI"/>">FI</span>
							{{/if}}
							{{if agafada}}
								<span class="label label-default" title="<spring:message code="enum.tasca.etiqueta.AG"/>">AG</span>
							{{/if}}
 							{{if !completed && tascaTramitacioMassiva && assignadaUsuariActual}}													
								<span <c:if test="${tascaConsultaCommand.consultaTramitacioMassivaTascaId == null}">onclick="javascript: $('td').unbind('click');window.location='../v3/tasca/{{:id}}/massiva';"</c:if>><span class="label label-default" title="<spring:message code="tasca.llistat.accio.tramitar_massivament"/>"><i class="fa fa-files-o"></i></span></span>
							{{/if}}	
						</div>
					</script>
				</th>
				<th data-rdt-property="expedientIdentificador" data-rdt-visible="true"><spring:message code="tasca.llistat.columna.expedient"/></th>
				<th data-rdt-property="responsableString" data-rdt-visible="true"><spring:message code="expedient.tasca.columna.asignada_a"/></th>
				<th data-rdt-property="expedientTipusNom" data-rdt-visible="true"><spring:message code="tasca.llistat.columna.tipexp"/></th>
				<th data-rdt-property="createTime" data-rdt-type="datetime" data-rdt-sorting="desc" data-rdt-visible="true"><spring:message code="tasca.llistat.columna.creada"/></th>
				<th data-rdt-property="dueDate" data-rdt-type="date" data-rdt-visible="true"><spring:message code="tasca.llistat.columna.limit"/></th>
				<th data-rdt-property="prioritat" data-rdt-visible="false"><spring:message code="tasca.llistat.columna.prioritat"/></th>
				<th data-rdt-property="id" data-rdt-template="cellAccionsTemplate" data-rdt-context="true" data-rdt-visible="<c:out value="${tascaConsultaCommand.consultaTramitacioMassivaTascaId == null}"/>" data-rdt-sortable="false" data-rdt-nowrap="true" width="10%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
 						<div id="dropdown-menu-{{:id}}" class="dropdown navbar-right">
 							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								{{if open and !suspended}}
									{{if assignee == "${dadesPersona.codi}" && assignadaUsuariActual}}
										<li><a id="tramitar-tasca-{{:id}}" class="consultar-tasca" href="<c:url value="../v3/expedient/{{:expedientId}}/tasca/{{:id}}"/>" data-rdt-link-modal="true" data-rdt-link-modal-maximize="true"><span class="fa fa-external-link"></span> <spring:message code="tasca.llistat.accio.tramitar"/></a></li>
										{{if tascaTramitacioMassiva}}
											<li><a href="../v3/tasca/{{:id}}/massiva"><span class="fa fa-files-o"></span> <spring:message code="tasca.llistat.accio.tramitar_massivament"/></a></li>
										{{/if}}
									{{/if}}
									{{if !agafada && responsables != null && assignadaUsuariActual}}
 										<li><a data-rdt-link-ajax=true data-rdt-link-callback="agafar({{:id}});" data-rdt-link-confirm="<spring:message code="expedient.tasca.confirmacio.agafar"/>" href="../v3/expedient/{{:expedientId}}/tasca/{{:id}}/agafar" data-rdt-link-ajax="true"><span class="fa fa-chain"></span> <spring:message code="tasca.llistat.accio.agafar"/></a></li>
									{{/if}}
									<li><a href="../v3/expedient/{{:expedientId}}/tasca/{{:id}}/suspendre" data-rdt-link-confirm="<spring:message code="tasca.llistat.confirmacio.suspendre"/>"><span class="fa fa-pause"></span> <spring:message code="tasca.llistat.accio.suspendre"/></a></li>
								{{/if}}
								<li><a href="../v3/expedient/{{:expedientId}}" class="consultar-expedient"><span class="fa fa-folder-open"></span>&nbsp;<spring:message code="expedient.llistat.accio.consultar.expedient"/></a></li>
								{{if open}}
									<li><a href="../v3/expedient/{{:expedientId}}/tasca/{{:id}}/reassignar" data-rdt-link-modal="true"><span class="fa fa-share-square-o"></span>&nbsp;<spring:message code="tasca.llistat.accio.reassignar"/></a></li>
								{{/if}}
								{{if suspended}}
									<li><a href="../v3/expedient/{{:expedientId}}/tasca/{{:id}}/reprendre" data-rdt-link-confirm="<spring:message code="tasca.llistat.confirmacio.reprendre"/>"><span class="fa fa-play"></span> <spring:message code="tasca.llistat.accio.reprendre"/></a></li>
								{{/if}}
								{{if !cancelled}}
									<li><a href="../v3/expedient/{{:expedientId}}/tasca/{{:id}}/cancelar" data-rdt-link-confirm="<spring:message code="tasca.llistat.confirmacio.cancelar"/>"><span class="fa fa-times"></span> <spring:message code="tasca.llistat.accio.cancelar"/></a></li>
								{{/if}}
								{{if responsables != null && agafada && assignee == "${dadesPersona.codi}" && open}}
									<li><a href="<c:url value="../v3/expedient/{{:expedientId}}/tasca/{{:id}}/alliberar"/>" data-rdt-link-ajax="true" data-rdt-link-callback="alliberar({{:id}});" data-rdt-link-confirm="<spring:message code="expedient.tasca.confirmacio.alliberar"/>"><span class="fa fa-chain-broken"></span> <spring:message code="tasca.llistat.accio.alliberar"/></a></li>
								{{/if}}
 							</ul>
 						</div>
					</script>
				</th>
				<th data-rdt-property="agafada" data-rdt-visible="false"></th>
				<th data-rdt-property="cancelled" data-rdt-visible="false"></th>
				<th data-rdt-property="assignee" data-rdt-visible="false"></th>
				<th data-rdt-property="assignadaUsuariActual" data-rdt-visible="false"></th>
				<th data-rdt-property="suspended" data-rdt-visible="false"></th>
				<th data-rdt-property="tascaTramitacioMassiva" data-rdt-visible="false"></th>
				<th data-rdt-property="open" data-rdt-visible="false"></th>
				<th data-rdt-property="completed" data-rdt-visible="false"></th>				
				<th data-rdt-property="expedientId" data-rdt-visible="false"></th>
				<th data-rdt-property="responsables" data-rdt-visible="false"></th>
			</tr>
		</thead>
	</table>
	<script id="tableButtonsTemplate" type="text/x-jsrender">
		<div style="text-align:right">
			<div id="btnTramitacio" class="btn-group">
				<c:choose>
					<c:when test="${tascaConsultaCommand.consultaTramitacioMassivaTascaId == null}">
						<a class="btn btn-default" href="../v3/tasca/seleccioTots" data-rdt-link-ajax="true" title="<spring:message code="expedient.llistat.accio.seleccio.tots"/>"><span class="fa fa-check-square-o"></span></a>
						<a class="btn btn-default" href="../v3/tasca/seleccioNetejar" data-rdt-link-ajax="true" title="<spring:message code="expedient.llistat.accio.seleccio.netejar"/>"><span class="fa fa-square-o"></span></a>
						<a onclick="massivaTasca(this,'reassignacio');" class="btn btn-default" data-rdt-link-modal="true" href="#"><spring:message code="tasca.llistat.reassignacions.massiva"/>&nbsp;<span id="reasignacioMassivaCount" class="badge">&nbsp;</span></a>
					</c:when>
						<c:otherwise>
						<a class="btn btn-default" href="#" onclick="seleccionarMassivaTodos()" title="<spring:message code="expedient.llistat.accio.seleccio.tots"/>"><span class="fa fa-check-square-o"></span></a>
						<a class="btn btn-default" href="#" onclick="deseleccionarMassivaTodos()" title="<spring:message code="expedient.llistat.accio.seleccio.netejar"/>"><span class="fa fa-square-o"></span></a>
						<a onclick="massivaTasca(this,'reassignacio');" class="btn btn-default" data-rdt-link-modal="true" href="#"><spring:message code="tasca.llistat.reassignacions.massiva"/>&nbsp;<span id="reasignacioMassivaCount" class="badge">&nbsp;</span></a>
						<a onclick="massivaTasca(this,'tramitacio');" class="btn btn-default" data-rdt-link-modal="true" data-rdt-link-modal-maximize="true" href="#"><spring:message code="expedient.llistat.tramitacio.massiva"/>&nbsp;<span id="tramitacioMassivaCount" class="badge">&nbsp;</span></a>
					</c:otherwise>
				</c:choose>	
			</div>
		</div>
	</script>
</body>
</html>
