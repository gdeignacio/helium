<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security"%>

<html>
<head>
	<title><fmt:message key='expedient.llistat.expedients' /></title>
	<meta name="titolcmp" content="<fmt:message key='comuns.consultes' />" />
	<script type="text/javascript" src="<c:url value="/js/selectable.js"/>"></script>
    <link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
<script type="text/javascript">
// <![CDATA[
function confirmar(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("<fmt:message key='expedient.iniciar.confirm_esborrar' />");
}
// ]]>
</script>
</head>
<body>

	<display:table name="llistat" id="registre" requestURI="" class="displaytag selectable">
		<display:column property="identificador" titleKey="expedient.llistat.expedient" sortable="true" url="/expedient/info.html" paramId="id" paramProperty="processInstanceId"/>
		<display:column property="dataInici" titleKey="expedient.info.iniciat_el" format="{0,date,dd/MM/yyyy HH:mm}" sortable="true"/>
		<%--display:column title="Iniciat per">
			<c:choose>
				<c:when test="${registre.iniciadorTipus == 'SISTRA'}">Entrada núm. ${registre.bantelEntradaNum}</c:when>
				<c:otherwise>${registre.iniciadorPersona.nomSencer}</c:otherwise>
			</c:choose>
		</display:column--%>
		<display:column property="tipus.nom" titleKey="expedient.info.tipus"/>
		<display:column titleKey="expedient.consulta.estat">
			<c:if test="${registre.aturat}"><img src="<c:url value="/img/stop.png"/>" alt="<fmt:message key='expedient.info.aturat' />" title="<fmt:message key='expedient.info.aturat' />" border="0"/></c:if>
			<c:choose>
				<c:when test="${empty registre.dataFi}">
					<c:choose><c:when test="${empty registre.estat}"><fmt:message key='expedient.consulta.iniciat' /></c:when><c:otherwise>${registre.estat.nom}</c:otherwise></c:choose>
				</c:when>
				<c:otherwise><fmt:message key='expedient.consulta.finalitzat' /></c:otherwise>
			</c:choose>
		</display:column>
		<display:column>
			<security:accesscontrollist domainObject="${registre.tipus}" hasPermission="16,8">
				<a href="<c:url value="/expedient/delete.html"><c:param name="id" value="${registre.id}"/></c:url>" onclick="return confirmar(event)"><img src="<c:url value="/img/cross.png"/>" alt="<fmt:message key='comuns.esborrar' />" title="<fmt:message key='comuns.esborrar' />" border="0"/></a>
			</security:accesscontrollist>
		</display:column>
	</display:table>
	<script type="text/javascript">initSelectable();</script>

</body>
</html>
