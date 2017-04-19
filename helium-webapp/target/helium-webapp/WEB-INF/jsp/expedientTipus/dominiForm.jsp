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
<script type="text/javascript">
// <![CDATA[
function disable(blocid) {
	$("#" + blocid).find("input,select,textarea").prop('disabled', true);
}
function enable(blocid) {
	$("#" + blocid).find("input,select,textarea").prop('disabled', false);
}
function canviTipus(input) {
	if (input.value == "CONSULTA_WS") {
		enable("camps_ws");
		disable("camps_sql");
	} else if (input.value == "CONSULTA_SQL") {
		enable("camps_sql");
		disable("camps_ws");
	} else {
		disable("camps_ws");
		disable("camps_sql");
	}
}
function canviTipusAuth(input) {
	if (input.value == "NONE") {
		disable("camps_usupass");
	} else {
		enable("camps_usupass");
	}
}
// ]]>
</script>
</head>
<body>

	<c:import url="../common/tabsExpedientTipus.jsp">
		<c:param name="tabActiu" value="dominis"/>
	</c:import>
	
	<form:form action="dominiForm.html" cssClass="uniForm">
		<div class="inlineLabels col first">
			<h3><fmt:message key="domini.form.dades_dom"/></h3>
			<input id="expedientTipusId" name="expedientTipusId" value="${param.expedientTipusId}" type="hidden"/>
			<c:if test="${not empty command.id}"><form:hidden path="id"/></c:if>
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
				<c:param name="property" value="tipus"/>
				<c:param name="required" value="true"/>
				<c:param name="type" value="select"/>
				<c:param name="items" value="tipusDomini"/>
				<c:param name="itemBuit">&lt;&lt; <fmt:message key="domini.form.selec_tipus"/> &gt;&gt;</c:param>
				<c:param name="label"><fmt:message key="comuns.tipus"/></c:param>
				<c:param name="onchange">canviTipus(this)</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="cacheSegons"/>
				<c:param name="required" value="true"/>
				<c:param name="label"><fmt:message key="domini.form.temps_cache"/></c:param>
				<c:param name="comment"><fmt:message key="domini.form.en_segons"/></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="descripcio"/>
				<c:param name="type" value="textarea"/>
				<c:param name="label"><fmt:message key="comuns.descripcio"/></c:param>
			</c:import>
		</div>
		<div class="inlineLabels col last">
			<h3><fmt:message key="domini.form.dades_ws"/></h3>
			<div id="camps_ws">
				<c:import url="../common/formElement.jsp">
					<c:param name="property" value="url"/>
					<c:param name="type" value="textarea"/>
					<c:param name="label"><fmt:message key="domini.form.url"/></c:param>
				</c:import>
				<c:import url="../common/formElement.jsp">
					<c:param name="property" value="tipusAuth"/>
					<c:param name="type" value="select"/>
					<c:param name="items" value="tipusAuth"/>
					<c:param name="label"><fmt:message key="domini.form.tipus.auth"/></c:param>
					<c:param name="onchange">canviTipusAuth(this)</c:param>
				</c:import>
				<div id="camps_usupass">
					<c:import url="../common/formElement.jsp">
						<c:param name="property" value="origenCredencials"/>
						<c:param name="type" value="select"/>
						<c:param name="items" value="origenCredencials"/>
						<c:param name="label"><fmt:message key="domini.form.origen.creds"/></c:param>
					</c:import>
					<c:import url="../common/formElement.jsp">
						<c:param name="property" value="usuari"/>
						<c:param name="label"><fmt:message key="domini.form.usuari"/></c:param>
					</c:import>
					<c:import url="../common/formElement.jsp">
						<c:param name="property" value="contrasenya"/>
						<c:param name="label"><fmt:message key="domini.form.contrasenya"/></c:param>
					</c:import>
				</div>
			</div>
			<h3><fmt:message key="domini.form.dades_sql"/></h3>
			<div id="camps_sql">
				<c:import url="../common/formElement.jsp">
					<c:param name="property" value="jndiDatasource"/>
					<c:param name="label"><fmt:message key="domini.form.jndi"/></c:param>
				</c:import>
				<c:import url="../common/formElement.jsp">
					<c:param name="property" value="sql"/>
					<c:param name="type" value="textarea"/>
					<c:param name="label"><fmt:message key="domini.form.sql"/></c:param>
				</c:import>
			</div>
		</div>
		<c:import url="../common/formElement.jsp">
			<c:param name="type" value="buttons"/>
			<c:param name="values">submit,cancel</c:param>
			<c:param name="titles"><c:choose><c:when test="${empty command.id}"><fmt:message key="comuns.crear"/>,<fmt:message key="comuns.cancelar"/></c:when><c:otherwise><fmt:message key="comuns.modificar"/>,<fmt:message key="comuns.cancelar"/></c:otherwise></c:choose></c:param>
		</c:import>
	</form:form>

	<p class="aclaracio"><fmt:message key="comuns.camps_marcats"/> <img src="<c:url value="/img/bullet_red.png"/>" alt="<fmt:message key="comuns.camp_oblig"/>" title="<fmt:message key="comuns.camp_oblig"/>" border="0"/> <fmt:message key="comuns.son_oblig"/></p>

	<script type="text/javascript">$(document).ready(canviTipus(document.getElementById("tipus0")));</script>
	<script type="text/javascript">$(document).ready(canviTipusAuth(document.getElementById("tipusAuth0")));</script>

</body>
</html>
