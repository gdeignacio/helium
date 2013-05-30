<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import = "java.util.*" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>

	
<div class="dialog-form-massive-bar" id="dialog-form-mass" style="display:none" title="<fmt:message key='expedient.massiva.proces' />">
	<div id="massiva_contens"></div>
</div>

<div class="wait"></div>

<script>
	var timer = null;
	var numResults = 10;
	var progres;
	
	$(function() {
		$( "#dialog-form-mass" ).dialog({
			autoOpen: false,
			height: 520,
			width: 900,
			modal: true,
			resizable: false,
			buttons: {
				<fmt:message key='comuns.tancar' />: function() {
					clearTimeout(timer);
					$(this).dialog("close");
				},
				"<fmt:message key='comuns.mes.dades' />": function() {
					var numFiles = $("#accordio_massiva h3").length;
					carregaExecucionsMassives(numFiles + 10)
				}
			},
			close: function(){
				clearTimeout(timer);
			}
		});
		
		$("#botoMassiu")
		.click(function() {
			$("body").addClass("loading");
			carregaExecucionsMassives(10);
			$( "#dialog-form-mass" ).dialog( "open" );
		});
	});
	
	function carregaExecucionsMassives(numResultats) {
		numResults = numResultats;
		$.ajax({
			url: "/helium/expedient/refreshBarsExpedientMassive.html",
			dataType: 'json',
			data: {results: numResultats},
			async: false,
			success: function(data){
					var length = data.length;
					var execucio = null;
					var content = "";
					if (length == 0) {
						content = "<h4><fmt:message key='execucions.massives.no'/></h4>";
					} else {
						content = '<div id="accordio_massiva">';
						for (var i = 0; i < length; i++) {
							execucio = data[i];
							var exps =  execucio.expedients.length;
							
							content +=	'<h3 id="mass_' + execucio.id + '">' +
											'<span class="massiu-data">' + execucio.data + '</span>' +
											'<span class="massiu-accio">' + execucio.text + '</span>' +
											'<div class="massiu-progres" id="pbar_' + execucio.id + '"><span class="plabel" id="plabel_' + execucio.id + '">' + execucio.progres + '%</span></div>' +
										'</h3>';
							content +=	'<div>';
							if (exps > 0) {
								var tableHeader = '<table class="displaytag" id="massexpt_' + execucio.id + '">' +
													'<thead>' +
													'<tr>' +
														'<th class="massiu-expedient"><fmt:message key="expedient.llistat.expedient"/></th>' +
														'<th class="massiu-estat"><fmt:message key="expedient.consulta.estat"/></th>' +
														'<th class="massiu-opcions"></th>' +
													'</tr>' +
													'</thead>' +
													'<tbody>';
								content += tableHeader;
								for (var j = 0; j < exps; j++) {
									var expedient = execucio.expedients[j];
									var estat = "";
									var opcions = "";
									if (expedient.estat == "ESTAT_CANCELAT"){
										estat = "<img border=\"0\" title=\"<fmt:message key='expedient.termini.estat.cancelat'/>\" alt=\"<fmt:message key='expedient.termini.estat.cancelat'/>\" src=\"/helium/img/mass_canceled.png\"><label style=\"padding-left: 10px\"><fmt:message key='expedient.termini.estat.cancelat'/></label>";
									} else if (expedient.estat == "ESTAT_ERROR"){
										estat = "<img border=\"0\" title=\"<fmt:message key='expedient.termini.estat.error'/>\" alt=\"<fmt:message key='expedient.termini.estat.error'/>\" src=\"/helium/img/mass_error.png\"><label style=\"padding-left: 10px\"><fmt:message key='expedient.termini.estat.error'/></label>";
										opcions = "<label style=\"cursor: pointer\" onclick=\"alert('" + expedient.error + "')\"><fmt:message key='expedient.termini.estat.error'/></label>";
									} else if (expedient.estat == "ESTAT_FINALITZAT"){
										estat = "<img border=\"0\" title=\"<fmt:message key='expedient.termini.estat.finalizat'/>\" alt=\"<fmt:message key='expedient.termini.estat.finalizat'/>\" src=\"/helium/img/mass_fin.png\"><label style=\"padding-left: 10px\"><fmt:message key='expedient.termini.estat.finalizat'/></label>";
									} else if (expedient.estat == "ESTAT_PENDENT"){
										estat = "<img border=\"0\" title=\"<fmt:message key='expedient.termini.estat.pendent_solament'/>\" alt=\"<fmt:message key='expedient.termini.estat.pendent_solament'/>\" src=\"/helium/img/mass_pend.png\"><label style=\"padding-left: 10px\"><fmt:message key='expedient.termini.estat.pendent_solament'/></label>";
										if (expedient.tasca == "") {
											opcions = "<img style=\"cursor: pointer\" onclick=\"cancelarExpedientMassiveAct('/helium/expedient/cancelExpedientMassiveAct.html','" + expedient.id + "')\" border=\"0\" title=\"<fmt:message key='expedient.termini.estat.cancelat'/>\" alt=\"<fmt:message key='expedient.termini.estat.cancelat'/>\" src=\"/helium/img/mass_cancel.png\">";
										}
									} else {
										estat = "<img border=\"0\" title=\"<fmt:message key='expedient.termini.estat.process'/>\" alt=\"<fmt:message key='expedient.termini.estat.process'/>\" src=\"/helium/img/mass_prog.png\"><label style=\"padding-left: 10px\"><fmt:message key='expedient.termini.estat.process'/></label>";
									}
									
									var row =	'<tr id="massexp_' + expedient.id + '" + class="mass_expedient exp_' + execucio.id + ' ' + (j % 2 == 0 ? 'odd' : 'even') + '">' +
													'<td class="massiu-expedient">' + expedient.titol + '</td>' +
													'<td class="massiu-estat">' + estat + '</td>' +
													'<td class="massiu-opcions">' + opcions + '</td>' +
						   						'</tr>';
									content += row;
								}
								content += '</tbody></table>';
							}
							content += '</div>';
						}
					}
					$("#massiva_contens").html(content);
					if ($.isFunction($.fn.progressbar)) {
						for (var i = 0; i < length; i++) {
							execucio = data[i];
							$("#pbar_" + execucio.id).progressbar({value: execucio.progres});
						}
	    			}
					$( "#accordio_massiva" ).accordion({
					      collapsible: true,
					      active: false
				    });
					if (length > 0) {
						timer = setTimeout(refreshExecucionsMassives, 1500);
					}
				}
		})
				//.done(function() { console.log( "second success" ); })
				//.fail(function( jqxhr, textStatus, error ) {
				//	var err = textStatus + ', ' + error;
				//	console.log( "Request Failed: " + err);
				//})
		.always(function() {
			$("body").removeClass("loading");
		});
	}
	
	function refreshExecucionsMassives() {
		$.ajax({
			url: "/helium/expedient/refreshBarsExpedientMassive.html",
			dataType: 'json',
			data: {results: numResults},
			async: false,
			success: function(data){
					var length = data.length;
					var execucio = null;
					if (length > 0) {
						// Actualitzam barres de progrés
						if ($.isFunction($.fn.progressbar)) {
							for (var i = 0; i < length; i++) {
								execucio = data[i];
								progres = execucio.progres;
		    					$("#pbar_" + execucio.id).progressbar("value", progres);
		    					$("#plabel_" + execucio.id).text(progres + "%");
							}
		    			}
						
						for (var i = 0; i < length; i++) {
							execucio = data[i];
							var exps =  execucio.expedients.length;
							var content = "";
							// Afegim noves execucions
							if ($("#mass_" + execucio.id).length == 0) {
								content +=	'<h3 id="mass_' + execucio.id + '">' +
												'<span class="massiu-data">' + execucio.data + '</span>' +
												'<span class="massiu-accio">' + execucio.text + '</span>' +
												'<div class="massiu-progres" id="pbar_' + execucio.id + '"><span id="plabel_' + execucio.id + '">' + execucio.progres + '%</span></div>' +
											'</h3>';
								content +=	'<div>';
								if (exps > 0) {
									var tableHeader = '<table class="displaytag" id="massexpt_' + execucio.id + '">' +
														'<thead>' +
														'<tr>' +
															'<th class="massiu-expedient"><fmt:message key="expedient.llistat.expedient"/></th>' +
															'<th class="massiu-estat"><fmt:message key="expedient.consulta.estat"/></th>' +
															'<th class="massiu-opcions"></th>' +
														'</tr>' +
														'</thead>' +
														'<tbody>';
									content += tableHeader;
									for (var j = 0; j < exps; j++) {
										var expedient = execucio.expedients[j];
										var estat = "";
										var opcions = "";
										if (expedient.estat == "ESTAT_CANCELAT"){
											estat = "<img border=\"0\" title=\"<fmt:message key='expedient.termini.estat.cancelat'/>\" alt=\"<fmt:message key='expedient.termini.estat.cancelat'/>\" src=\"/helium/img/mass_canceled.png\"><label style=\"padding-left: 10px\"><fmt:message key='expedient.termini.estat.cancelat'/></label>";
										} else if (expedient.estat == "ESTAT_ERROR"){
											estat = "<img border=\"0\" title=\"<fmt:message key='expedient.termini.estat.error'/>\" alt=\"<fmt:message key='expedient.termini.estat.error'/>\" src=\"/helium/img/mass_error.png\"><label style=\"padding-left: 10px\"><fmt:message key='expedient.termini.estat.error'/></label>";
											opcions = "<label style=\"cursor: pointer\" onclick=\"alert('" + expedient.error + "')\"><fmt:message key='expedient.termini.estat.error'/></label>";
										} else if (expedient.estat == "ESTAT_FINALITZAT"){
											estat = "<img border=\"0\" title=\"<fmt:message key='expedient.termini.estat.finalizat'/>\" alt=\"<fmt:message key='expedient.termini.estat.finalizat'/>\" src=\"/helium/img/mass_fin.png\"><label style=\"padding-left: 10px\"><fmt:message key='expedient.termini.estat.finalizat'/></label>";
										} else if (expedient.estat == "ESTAT_PENDENT"){
											estat = "<img border=\"0\" title=\"<fmt:message key='expedient.termini.estat.pendent_solament'/>\" alt=\"<fmt:message key='expedient.termini.estat.pendent_solament'/>\" src=\"/helium/img/mass_pend.png\"><label style=\"padding-left: 10px\"><fmt:message key='expedient.termini.estat.pendent_solament'/></label>";
											if (expedient.tasca == "") {
												opcions = "<img style=\"cursor: pointer\" onclick=\"cancelarExpedientMassiveAct('/helium/expedient/cancelExpedientMassiveAct.html','" + expedient.id + "')\" border=\"0\" title=\"<fmt:message key='expedient.termini.estat.cancelat'/>\" alt=\"<fmt:message key='expedient.termini.estat.cancelat'/>\" src=\"/helium/img/mass_cancel.png\">";
											}
										} else {
											estat = "<img border=\"0\" title=\"<fmt:message key='expedient.termini.estat.process'/>\" alt=\"<fmt:message key='expedient.termini.estat.process'/>\" src=\"/helium/img/mass_prog.png\"><label style=\"padding-left: 10px\"><fmt:message key='expedient.termini.estat.process'/></label>";
										}
										
										var row =	'<tr id="massexp_' + expedient.id + '" + class="mass_expedient exp_' + execucio.id + ' ' + (j % 2 == 0 ? 'odd' : 'even') + '">' +
														'<td class="massiu-expedient">' + expedient.titol + '</td>' +
														'<td class="massiu-estat">' + estat + '</td>' +
														'<td class="massiu-opcions">' + opcions + '</td>' +
							   						'</tr>';
										content += row;
									}
									content += '</tbody></table>';
								}
								content += '</div>';
								$("#accordio_massiva").prepend(content);
								if ($.isFunction($.fn.progressbar)) {
									$("#pbar_" + execucio.id).progressbar({ value: execucio.progres	});
								}
							} else {
								// Actualitzam execucions existents
								if (exps > 0) {
									for (var j = 0; j < exps; j++) {
										var expedient = execucio.expedients[j];
										var estat = "";
										var opcio = "";
										if (expedient.estat == "ESTAT_CANCELAT"){
											estat = "<img border=\"0\" title=\"<fmt:message key='expedient.termini.estat.cancelat'/>\" alt=\"<fmt:message key='expedient.termini.estat.cancelat'/>\" src=\"/helium/img/mass_canceled.png\"><label style=\"padding-left: 10px\"><fmt:message key='expedient.termini.estat.cancelat'/></label>";
										} else if (expedient.estat == "ESTAT_ERROR"){
											estat = "<img border=\"0\" title=\"<fmt:message key='expedient.termini.estat.error'/>\" alt=\"<fmt:message key='expedient.termini.estat.error'/>\" src=\"/helium/img/mass_error.png\"><label style=\"padding-left: 10px\"><fmt:message key='expedient.termini.estat.error'/></label>";
											opcio = "<label style=\"cursor: pointer\" onclick=\"alert('" + expedient.error + "')\"><fmt:message key='expedient.termini.estat.error'/></label>";
										} else if (expedient.estat == "ESTAT_FINALITZAT"){
											estat = "<img border=\"0\" title=\"<fmt:message key='expedient.termini.estat.finalizat'/>\" alt=\"<fmt:message key='expedient.termini.estat.finalizat'/>\" src=\"/helium/img/mass_fin.png\"><label style=\"padding-left: 10px\"><fmt:message key='expedient.termini.estat.finalizat'/></label>";
										} else if (expedient.estat == "ESTAT_PENDENT"){
											estat = "<img border=\"0\" title=\"<fmt:message key='expedient.termini.estat.pendent_solament'/>\" alt=\"<fmt:message key='expedient.termini.estat.pendent_solament'/>\" src=\"/helium/img/mass_pend.png\"><label style=\"padding-left: 10px\"><fmt:message key='expedient.termini.estat.pendent_solament'/></label>";
											if (expedient.tasca == "") {
												opcio = "<img style=\"cursor: pointer\" onclick=\"cancelarExpedientMassiveAct('/helium/expedient/cancelExpedientMassiveAct.html','" + expedient.id + "')\" border=\"0\" title=\"<fmt:message key='expedient.termini.estat.cancelat'/>\" alt=\"<fmt:message key='expedient.termini.estat.cancelat'/>\" src=\"/helium/img/mass_cancel.png\">";
											}
										} else {
											estat = "<img border=\"0\" title=\"<fmt:message key='expedient.termini.estat.process'/>\" alt=\"<fmt:message key='expedient.termini.estat.process'/>\" src=\"/helium/img/mass_prog.png\"><label style=\"padding-left: 10px\"><fmt:message key='expedient.termini.estat.process'/></label>";
										}
										
										var estat_org = $("#massexp_" + expedient.id + " td:nth-child(2)").html();
										var opcio_org = $("#massexp_" + expedient.id + " td:nth-child(3)").html();
										if (estat != estat_org) $("#massexp_" + expedient.id + " td:nth-child(2)").html(estat);										
										if (opcio != opcio_org) $("#massexp_" + expedient.id + " td:nth-child(3)").html(opcio);
									}
								}
							}
						}
					}
// 					$( "#accordio_massiva" ).accordion({
// 					      collapsible: true,
// 					      active: false
// 				    });
					timer = setTimeout(refreshExecucionsMassives, 1500);
				}
		});
				//.done(function() { console.log( "second success" ); })
				//.fail(function( jqxhr, textStatus, error ) {
				//	var err = textStatus + ', ' + error;
				//	console.log( "Request Failed: " + err);
				//})
// 		.always(function() { });
	}
	
	function cancelarExpedientMassiveAct(url,id) {
		$.post(url, { idExp: id }, function(data){});
	}
</script>