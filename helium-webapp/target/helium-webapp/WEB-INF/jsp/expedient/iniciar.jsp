<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>

<html>
<head>
	<title><fmt:message key='expedient.iniciar.disponibles' /></title>
	<meta name="titolcmp" content="Nou expedient"/>
	<script type="text/javascript" src="<c:url value="/js/selectable.js"/>"></script>
	<link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
<script type="text/javascript">
// <![CDATA[
var comprovacio = new Array();<c:forEach var="et" items="${expedientTipus}">
comprovacio["${et.id}"] = new Array(<c:forEach var="id" items="${definicionsProces[et.id].idsWithSameKey}" varStatus="status"><c:choose><c:when test="${(et.teNumero and et.demanaNumero) or (et.teTitol and et.demanaTitol) or definicionsProces[et.id].hasStartTaskWithSameKey[status.index]}">true</c:when><c:otherwise>false</c:otherwise></c:choose><c:if test="${not status.last}">,</c:if></c:forEach>);</c:forEach>
function confirmar(e, form) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	if (!comprovacio[form.expedientTipusId.value][form.definicioProcesId.selectedIndex])
		return confirm("<fmt:message key='expedient.iniciar.confirm_iniciar' />");
	else
		return true;
}
// ]]>
</script>
</head>
<body>

	<display:table name="expedientTipus" id="registre" requestURI="" class="displaytag">
		<display:column property="codi" title="Codi"/>
		<display:column property="nom" title="Nom"/>
		<display:column>
			<form action="<c:url value="/expedient/iniciar.html"/>" method="post" onsubmit="return confirmar(event, this)">
				<input type="hidden" name="expedientTipusId" value="${registre.id}"/>
				<select name="definicioProcesId">
					<option value="${definicionsProces.id}">&lt;&lt; <fmt:message key='expedient.iniciar.darrera_versio' /> &gt;&gt;</option>
					<c:forEach var="id" items="${definicionsProces[registre.id].idsWithSameKey}" varStatus="status">
						<option value="${id}">${definicionsProces[registre.id].idsMostrarWithSameKey[status.index]}</option>
					</c:forEach>
				</select>
				<button type="submit" class="submitButton"><fmt:message key='comuns.iniciar' /></button>
			</form>
		</display:column>
	</display:table>

</body>
</html>
