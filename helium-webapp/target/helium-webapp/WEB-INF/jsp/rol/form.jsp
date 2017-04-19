<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
	<head>
		<title>
			<c:choose>
				<c:when test="${empty command.codi}"><fmt:message key='rol.form.crear_nou' /></c:when>
				<c:otherwise><fmt:message key='rol.form.modificar' /></c:otherwise>
			</c:choose>
		</title>
		<meta name="titolcmp" content="<fmt:message key='comuns.configuracio' />" />
		<c:import url="../common/formIncludes.jsp"/>
		<script type="text/javascript">
			// <![CDATA[
			// ]]>
		</script>
	</head>
	
	<body>
		<form:form action="form.html" cssClass="uniForm" enctype="multipart/form-data">
			<input type="hidden" name="id" value="${command.codi}" />
			<div class="inlineLabels">
				<c:choose>
					<c:when test="${empty command.codi}">
						<c:import url="../common/formElement.jsp">
							<c:param name="property" value="codi"/>
							<c:param name="required" value="true"/>
							<c:param name="label"><fmt:message key='comuns.codi' /></c:param>
						</c:import>
					</c:when>
					<c:otherwise>
						<c:import url="../common/formElement.jsp">
							<c:param name="property" value="codi"/>
							<c:param name="disabled" value="true"/>
							<c:param name="required" value="true"/>
							<c:param name="label"><fmt:message key='comuns.codi' /></c:param>
						</c:import>
					</c:otherwise>
				</c:choose>
				<c:import url="../common/formElement.jsp">
					<c:param name="property" value="descripcio"/>
					<c:param name="type" value="textarea"/>
					<c:param name="required" value="true"/>
					<c:param name="label"><fmt:message key='comuns.descripcio' /></c:param>
				</c:import>
			</div>
			<c:import url="../common/formElement.jsp">
				<c:param name="type" value="buttons"/>
				<c:param name="values">submit,cancel</c:param>
				<c:param name="titles"><c:choose><c:when test="${empty command.codi}"><fmt:message key='comuns.crear' />,<fmt:message key='comuns.cancelar' /></c:when><c:otherwise><fmt:message key='comuns.modificar' />,<fmt:message key='comuns.cancelar' /></c:otherwise></c:choose></c:param>
			</c:import>
		</form:form>
		
		<p class="aclaracio"><fmt:message key='comuns.camps_marcats' /> <img src="<c:url value="/img/bullet_red.png"/>" alt="<fmt:message key='comuns.camp_oblig' />" title="<fmt:message key='comuns.camp_oblig' />" border="0"/> <fmt:message key='comuns.son_oblig' /></p>
	</body>
</html>
