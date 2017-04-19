<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>

<html>
<head>
	<title><fmt:message key="alerta.llistat.alertes"/></title>
	<script type="text/javascript" src="<c:url value="/js/selectable.js"/>"></script>
    <link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
<script type="text/javascript">
// <![CDATA[
function confirmarEsborrar(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("<fmt:message key="alerta.llistat.confirmacio"/>");
}
// ]]>
</script>
</head>
<body>

	<c:if test="${not empty tipusExpedientsPermesos}">
		<div class="missatgesGris">
			<h4 class="titol-consulta"><fmt:message key="alerta.llistat.select_tipus.titol"/></h4>
			<form action="<c:url value="${formUrl}"/>" style="display:inline">
				<select name="expedientTipusId">
				<option value="">&lt;&lt; <fmt:message key="alerta.llistat.select_tipus.seleccioni"/> &gt;&gt;</option>
				<c:forEach var="tipusExpedient" items="${tipusExpedientsPermesos}">
					<option value="${tipusExpedient.id}"<c:if test="${tipusExpedient.id == param.expedientTipusId}"> selected="selected"</c:if>>${tipusExpedient.nom}</option>
				</c:forEach>
				</select>
				<input type="submit" value="<fmt:message key="alerta.llistat.select_tipus.mostrar"/>"/>
			</form>
		</div>	
	</c:if>

	<display:table name="llistat" id="registre" requestURI="" class="displaytag selectable" defaultsort="1" defaultorder="descending">
		<display:column property="dataCreacio" titleKey="alerta.llistat.creada_el" sortable="true" format="{0,date,dd/MM/yyyy}"/>
		<c:if test="${not empty param.expedientTipusId}">
			<display:column titleKey="alerta.llistat.destinatari" sortable="true">
				${persones[registre.destinatari]}
			</display:column>
		</c:if>
		<display:column titleKey="alerta.llistat.expedient" sortable="true">
			<a href="<c:url value="/expedient/info.html"><c:param name="id" value="${registre.expedient.processInstanceId}"/></c:url>">${registre.expedient.identificador}</a>
		</display:column>
		<display:column titleKey="alerta.llistat.causa" sortable="true">
			<c:choose>
				<c:when test="${not empty registre.terminiIniciat}"><c:choose>
					<c:when test="${not empty registre.terminiIniciat.taskInstanceId
									&& not (registre.terminiIniciat.estat=='COMPLETAT_TEMPS')
									&& not (registre.terminiIniciat.estat=='COMPLETAT_FORA')}">
						<a href="<c:url value="/tasca/info.html"><c:param name="id" value="${registre.terminiIniciat.taskInstanceId}"/></c:url>">${registre.terminiIniciat.termini.nom}</a>
					</c:when>
					<c:otherwise>${registre.terminiIniciat.termini.nom}</c:otherwise>
				</c:choose></c:when>
				<c:when test="${not empty registre.causa}">${registre.causa}</c:when>
				<c:otherwise> - </c:otherwise>
			</c:choose>
		</display:column>
		<c:choose>
			<c:when test="${empty registre.terminiIniciat}"><c:set var="estilPrioritat"></c:set></c:when>
			<c:when test="${registre.terminiIniciat.estat=='AVIS'}"><c:set var="estilPrioritat">background-color:orange</c:set></c:when>
			<c:when test="${registre.terminiIniciat.estat=='CADUCAT'}"><c:set var="estilPrioritat">color:white;background-color:darkred</c:set></c:when>
			<c:when test="${registre.terminiIniciat.estat=='COMPLETAT_FORA'}"><c:set var="estilPrioritat">color:white;background-color:red</c:set></c:when>
			<c:otherwise><c:set var="estilPrioritat">background-color:green</c:set></c:otherwise>
		</c:choose>
		<display:column titleKey="alerta.llistat.prioritat" sortable="true" style="${estilPrioritat}">
			<c:choose>
				<c:when test="${not empty registre.terminiIniciat}"><c:choose>
					<c:when test="${registre.terminiIniciat.estat=='AVIS'}"><fmt:message key="alerta.llistat.apunt_dexpirar"/></c:when>
					<c:when test="${registre.terminiIniciat.estat=='CADUCAT'}"><fmt:message key="alerta.llistat.expirat"/></c:when>
					<c:when test="${registre.terminiIniciat.estat=='COMPLETAT_TEMPS'}"><fmt:message key="alerta.llistat.completat_atemps"/></c:when>
					<c:when test="${registre.terminiIniciat.estat=='COMPLETAT_FORA'}"><fmt:message key="alerta.llistat.completat_fora"/></c:when>
					<c:otherwise>${registre.text}</c:otherwise>
				</c:choose></c:when>
				<c:otherwise>${registre.text}</c:otherwise>
			</c:choose>
		</display:column>
		<%-- <c:choose>
			<c:when test="${empty registre.prioritat || registre.prioritat=='MOLT_BAIXA'}"><c:set var="textPrioritat"><fmt:message key='alerta.llistat.m_baixa' /></c:set><c:set var="estilPrioritat">background-color:lightgreen</c:set></c:when>
			<c:when test="${registre.prioritat=='BAIXA'}"><c:set var="textPrioritat"><fmt:message key='alerta.llistat.baixa' /></c:set><c:set var="estilPrioritat">background-color:green</c:set></c:when>
			<c:when test="${registre.prioritat=='NORMAL'}"><c:set var="textPrioritat"><fmt:message key='alerta.llistat.normal' /></c:set><c:set var="estilPrioritat">color:white;background-color:orange</c:set></c:when>
			<c:when test="${registre.prioritat=='ALTA'}"><c:set var="textPrioritat"><fmt:message key='alerta.llistat.alta' /></c:set><c:set var="estilPrioritat">color:white;background-color:red</c:set></c:when>
			<c:when test="${registre.prioritat=='MOLT_ALTA'}"><c:set var="textPrioritat"><fmt:message key='alerta.llistat.m_alta' /></c:set><c:set var="estilPrioritat">color:white;background-color:darkred</c:set></c:when>
		</c:choose>
		<display:column titleKey="alerta.llistat.prioritat" value="${textPrioritat}" sortable="true" sortProperty="prioritat" style="${estilPrioritat}"/--%>
		<%-- <display:column titleKey="alerta.llistat.text" sortable="true">
			<c:choose>
				<c:when test="${not empty registre.terminiIniciat.taskInstanceId}"><a href="<c:url value="/tasca/info.html"><c:param name="id" value="${registre.terminiIniciat.taskInstanceId}"/></c:url>">${registre.text}</a></c:when>
				<c:otherwise>${registre.text}</c:otherwise>
			</c:choose>
		</display:column> --%>
		<display:column titleKey="alerta.llistat.data_limit" sortable="true">
			<c:if test="${not empty registre.terminiIniciat}">
				<fmt:formatDate value="${registre.terminiIniciat.dataFi}" pattern="dd/MM/yyyy"/>
			</c:if>
		</display:column>
		<c:if test="${empty param.expedientTipusId}">
			<display:column>
				<c:choose>
					<c:when test="${registre.llegida}">
						<a href="<c:url value="/alerta/nollegir.html"><c:param name="id" value="${registre.id}"/></c:url>"><img src="<c:url value="/img/email_open.png"/>" alt="<fmt:message key="alerta.llistat.marcar_no"/>" title="<fmt:message key="alerta.llistat.marcar_no"/>" border="0"/></a>
					</c:when>
					<c:otherwise>
						<a href="<c:url value="/alerta/llegir.html"><c:param name="id" value="${registre.id}"/></c:url>"><img src="<c:url value="/img/email.png"/>" alt="<fmt:message key="alerta.llistat.marcar_llegida"/>" title="<fmt:message key="alerta.llistat.marcar_llegida"/>" border="0"/></a>
					</c:otherwise>
				</c:choose>
			</display:column>
			<display:column>
				<a href="<c:url value="/alerta/esborrar.html"><c:param name="id" value="${registre.id}"/></c:url>" onclick="return confirmarEsborrar(event)"><img src="<c:url value="/img/cross.png"/>" alt="<fmt:message key="comuns.esborrar"/>" title="<fmt:message key="comuns.esborrar"/>" border="0"/></a>
			</display:column>
		</c:if>
	</display:table>
	<script type="text/javascript">initSelectable();</script>

</body>
</html>
