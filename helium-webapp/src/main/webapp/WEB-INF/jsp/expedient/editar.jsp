<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
	<title>Expedient: ${expedient.identificadorLimitat}</title>
	<meta name="titolcmp" content="<fmt:message key='comuns.consultes' />" />
	<link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
	<link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
	<c:import url="../common/formIncludes.jsp"/>
</head>
<body>

	<c:import url="../common/tabsExpedient.jsp">
		<c:param name="tabActiu" value="info"/>
	</c:import>
	<h3 class="titol-tab titol-dades-tasca">Modificar informació de l'expedient</h3>

	<form:form action="editar.html" cssClass="uniForm">
		<div class="inlineLabels">
			<input type="hidden" name="id" value="${param.id}"/>
			<form:hidden path="expedientId"/>
			<c:if test="${expedient.tipus.teNumero and expedient.tipus.demanaNumero}">
				<c:import url="../common/formElement.jsp">
					<c:param name="property" value="numero"/>
					<c:param name="required" value="true"/>
					<c:param name="label">Número</c:param>
				</c:import>
				</c:if>
			<c:if test="${expedient.tipus.teTitol and expedient.tipus.demanaTitol}">
				<c:import url="../common/formElement.jsp">
					<c:param name="property" value="titol"/>
					<c:param name="type" value="textarea"/>
					<c:param name="required" value="true"/>
					<c:param name="label">Títol</c:param>
				</c:import>
			</c:if>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="dataInici"/>
				<c:param name="required" value="true"/>
				<c:param name="type" value="date"/>
				<c:param name="label">Data d'inici</c:param>
			</c:import>
			<c:choose>
				<c:when test="${expedient.iniciadorTipus == 'SISTRA'}">
					<c:import url="../common/formElement.jsp">
						<c:param name="property" value="iniciadorCodi"/>
						<c:param name="type" value="custom"/>
						<c:param name="label">Iniciador</c:param>
						<c:param name="content">
							<span class="staticField">Entrada núm. ${expedient.bantelEntradaNum}</span>
							<input id="iniciadorCodi" name="iniciadorCodi" value="${command.iniciadorCodi}" type="hidden"/>
						</c:param>
					</c:import>
				</c:when>
				<c:otherwise>
					<c:import url="../common/formElement.jsp">
						<c:param name="property" value="iniciadorCodi"/>
						<c:param name="type" value="suggest"/>
						<c:param name="required" value="true"/>
						<c:param name="label">Iniciador</c:param>
						<c:param name="suggestUrl"><c:url value="/persona/suggest.html"/></c:param>
						<c:param name="suggestText">${iniciador.nomSencer}</c:param>
					</c:import>
				</c:otherwise>
			</c:choose>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="responsableCodi"/>
				<c:param name="type" value="suggest"/>
				<c:param name="required" value="true"/>
				<c:param name="label">Responsable</c:param>
				<c:param name="suggestUrl"><c:url value="/persona/suggest.html"/></c:param>
				<c:param name="suggestText">${responsable.nomSencer}</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="comentari"/>
				<c:param name="label">Comentari</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="estatId"/>
				<c:param name="type" value="select"/>
				<c:param name="items" value="estats"/>
				<c:param name="itemLabel" value="nom"/>
				<c:param name="itemValue" value="id"/>
				<c:param name="itemBuit" value="<< Seleccioni un estat >>"/>
				<c:param name="label">Estat</c:param>
			</c:import>
			<c:if test="${globalProperties['app.georef.actiu']}">
				<c:choose>
					<c:when test="${globalProperties['app.georef.tipus']=='ref'}">
						<c:import url="../common/formElement.jsp">
							<c:param name="property" value="geoReferencia"/>
							<c:param name="label">Codi georeferencial</c:param>
						</c:import>
					</c:when>
					<c:otherwise>
						<c:import url="../common/formElement.jsp">
							<c:param name="property" value="geoPosX"/>
							<c:param name="type" value="number"/>
							<c:param name="keyfilter">/[\d\-\.]/</c:param>
							<c:param name="label">Coordenada X</c:param>
						</c:import>
						<c:import url="../common/formElement.jsp">
							<c:param name="property" value="geoPosY"/>
							<c:param name="type" value="number"/>
							<c:param name="keyfilter">/[\d\-\.]/</c:param>
							<c:param name="label">Coordenada Y</c:param>
						</c:import>
					</c:otherwise>
				</c:choose>
			</c:if>
		</div>
		<c:import url="../common/formElement.jsp">
			<c:param name="type" value="buttons"/>
			<c:param name="values">submit,cancel</c:param>
			<c:param name="titles"><fmt:message key='comuns.modificar' />,<fmt:message key='comuns.cancelar' /></c:param>
		</c:import>
	</form:form>

	<p class="aclaracio"><fmt:message key='comuns.camps_marcats' /> <img src="<c:url value="/img/bullet_red.png"/>" alt="<fmt:message key='comuns.camp_oblig' />" title="<fmt:message key='comuns.camp_oblig' />" border="0"/> <fmt:message key='comuns.son_oblig' /></p>

</body>
</html>
