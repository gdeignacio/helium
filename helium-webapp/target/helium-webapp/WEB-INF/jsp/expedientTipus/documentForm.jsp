<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
	<title>Tipus d'expedient: ${expedientTipus.nom}</title>
	<meta name="titolcmp" content="<fmt:message key="comuns.disseny"/>" />
	<link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
	<c:import url="../common/formIncludes.jsp"/>
    <link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
</head>
<body>

	<c:import url="../common/tabsExpedientTipus.jsp">
		<c:param name="tabActiu" value="documents"/>
	</c:import>

	<form:form action="documentForm.html" cssClass="uniForm" enctype="multipart/form-data">
		<input type="hidden" name="expedientTipusId" value="${param.expedientTipusId}"/>
		<input type="hidden" name="definicioProcesId" value="${param.definicioProcesId}"/>
		<div class="inlineLabels">
			<c:if test="${not empty command.id}"><form:hidden path="id"/></c:if>
			<form:hidden path="definicioProces"/>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="codi"/>
				<c:param name="required" value="true"/>
				<c:param name="label"><fmt:message key="comuns.codi"/></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="nom"/>
				<c:param name="required" value="true"/>
				<c:param name="label"><fmt:message key="comuns.nom"/></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="descripcio"/>
				<c:param name="type" value="textarea"/>
				<c:param name="label"><fmt:message key="comuns.descripcio"/></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="arxiuContingut"/>
				<c:param name="type" value="file"/>
				<c:param name="fileUrl"><c:url value="/expedientTipus/documentDownload.html"><c:param name="expedientTipusId" value="${param.expedientTipusId}"/><c:param name="definicioProcesId" value="${param.definicioProcesId}"/><c:param name="id" value="${command.id}"/></c:url></c:param>
				<c:param name="fileExists" value="${not empty command.arxiuNom}"/>
				<c:param name="label"><fmt:message key="comuns.arxiu"/></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="plantilla"/>
				<c:param name="type" value="checkbox"/>
				<c:param name="label"><fmt:message key="defproc.docform.es_plantilla"/></c:param>
			</c:import>
		</div>
		<c:import url="../common/formElement.jsp">
			<c:param name="type" value="buttons"/>
			<c:param name="values">submit,cancel</c:param>
			<c:param name="titles"><c:choose><c:when test="${empty command.id}"><fmt:message key="comuns.crear"/>,<fmt:message key="comuns.cancelar"/></c:when><c:otherwise><fmt:message key="comuns.modificar"/>,<fmt:message key="comuns.cancelar"/></c:otherwise></c:choose></c:param>
		</c:import>
	</form:form>

	<p class="aclaracio"><fmt:message key="comuns.camps_marcats"/> <img src="<c:url value="/img/bullet_red.png"/>" alt="<fmt:message key="comuns.camp_oblig"/>" title="<fmt:message key="comuns.camp_oblig"/>" border="0"/> <fmt:message key="comuns.son_oblig"/></p>

</body>
</html>
