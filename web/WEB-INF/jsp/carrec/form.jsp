<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
	<title><c:choose><c:when test="${empty command.id}">Crear nou càrrec</c:when><c:otherwise>Modificar càrrec</c:otherwise></c:choose></title>
	<meta name="titolcmp" content="Organització">
	<c:import url="../common/formIncludes.jsp"/>
</head>
<body>

	<form:form action="form.html" cssClass="uniForm">
		<div class="inlineLabels">
			<c:if test="${not empty command.id}"><form:hidden path="id"/></c:if>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="codi"/>
				<c:param name="required" value="true"/>
				<c:param name="label">Codi</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="nomHome"/>
				<c:param name="required" value="true"/>
				<c:param name="label">Nom home</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="nomDona"/>
				<c:param name="required" value="true"/>
				<c:param name="label">Nom dona</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="tractamentHome"/>
				<c:param name="required" value="true"/>
				<c:param name="label">Tractament home</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="tractamentDona"/>
				<c:param name="required" value="true"/>
				<c:param name="label">Tractament dona</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="area"/>
				<c:param name="type" value="suggest"/>
				<c:param name="required" value="true"/>
				<c:param name="label">Àrea</c:param>
				<c:param name="suggestUrl"><c:url value="/area/suggest.html"/></c:param>
				<c:param name="suggestText">${command.area.nom}</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="descripcio"/>
				<c:param name="type" value="textarea"/>
				<c:param name="label">Descripció</c:param>
			</c:import>
		</div>
		<c:import url="../common/formElement.jsp">
			<c:param name="type" value="buttons"/>
			<c:param name="values">submit,cancel</c:param>
			<c:param name="titles"><c:choose><c:when test="${empty command.id}">Crear,Cancel·lar</c:when><c:otherwise>Modificar,Cancel·lar</c:otherwise></c:choose></c:param>
		</c:import>
	</form:form>

	<p class="aclaracio">Els camps marcats amb <img src="<c:url value="/img/bullet_red.png"/>" alt="Camp obligatori" title="Camp obligatori" border="0"/> són obligatoris</p>

</body>
</html>
