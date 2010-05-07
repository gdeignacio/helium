<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>

<html>
<head>
	<title>Persones</title>
	<meta name="titolcmp" content="Configuració">
	<script type="text/javascript" src="<c:url value="/js/selectable.js"/>"></script>
    <link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
<script type="text/javascript">
// <![CDATA[
function confirmar(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("Estau segur que voleu esborrar aquest registre?");
}
// ]]>
</script>
</head>
<body>

	<display:table name="llistat" id="registre" requestURI="" class="displaytag selectable">
		<display:column property="codi" title="Codi" sortable="true" url="/persona/form.html" paramId="id" paramProperty="id"/>
		<display:column property="nomSencer" title="Nom" sortable="true"/>
		<display:column property="email" title="A/E" sortable="true"/>
		<display:column title="Accés?" sortable="false">
			<c:choose><c:when test="${registre.login}">Si</c:when><c:otherwise>No</c:otherwise></c:choose>
		</display:column>
		<display:column>
			<a href="<c:url value="/persona/delete.html"><c:param name="id" value="${registre.id}"/></c:url>" onclick="return confirmar(event)"><img src="<c:url value="/img/cross.png"/>" alt="Esborrar" title="Esborrar" border="0"/></a>
		</display:column>
		<display:setProperty name="paging.banner.item_name">persona</display:setProperty>
		<display:setProperty name="paging.banner.items_name">persona</display:setProperty>
	</display:table>
	<script type="text/javascript">initSelectable();</script>

	<form action="form.html">
		<button type="submit" class="submitButton">Nova persona</button>
	</form>

</body>
</html>
