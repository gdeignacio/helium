<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security"%>

<html>
<head>
	<title>Tipus d'expedient: ${expedientTipus.nom}</title>
	<meta name="titolcmp" content="<fmt:message key='comuns.disseny' />" />
	<link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
	<script type="text/javascript" src="<c:url value="/js/selectable.js"/>"></script>
    <link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
<script type="text/javascript">
// <![CDATA[
function confirmar(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("<fmt:message key='domini.llistat.confirmacio' />");
}
// ]]>
</script>
</head>
<body>

	<c:import url="../common/tabsExpedientTipus.jsp">
		<c:param name="tabActiu" value="dominis"/>
	</c:import>
	
	<display:table name="llistat" id="registre" requestURI="" class="displaytag selectable">
		<display:column property="codi" titleKey="comuns.codi" sortable="true" url="/expedientTipus/dominiForm.html?expedientTipusId=${expedientTipus.id}" paramId="id" paramProperty="id"/>
		<display:column property="nom" titleKey="comuns.nom" sortable="true"/>
		<display:column property="tipus" titleKey="comuns.tipus"/>
		<%--display:column>
	    	<form action="consulta.html">
				<input type="hidden" name="id" value="${registre.id}"/>
				<button type="submit" class="submitButton">Provar</button>
			</form>
	    </display:column--%>
		<display:column>
			<a href="<c:url value="/expedientTipus/dominiEsborrar.html"><c:param name="expedientTipusId" value="${expedientTipus.id}"/><c:param name="dominiId" value="${registre.id}"/></c:url>" onclick="return confirmar(event)"><img src="<c:url value="/img/cross.png"/>" alt="<fmt:message key='comuns.esborrar' />" title="<fmt:message key='comuns.esborrar' />" border="0"/></a>
		</display:column>
	</display:table>
	<script type="text/javascript">initSelectable(7);</script>

	<form action="<c:url value="/expedientTipus/dominiForm.html"/>">
		<input type="hidden" name="expedientTipusId" value="${expedientTipus.id}"/>
		<button type="submit" class="submitButton"><fmt:message key='domini.llistat.nou' /></button>
	</form>

</body>
</html>
