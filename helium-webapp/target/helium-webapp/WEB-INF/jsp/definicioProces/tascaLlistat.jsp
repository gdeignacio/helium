<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>

<html>
<head>
	<title><fmt:message key='comuns.def_proces' />: ${definicioProces.jbpmName}</title>
	<meta name="titolcmp" content="<fmt:message key='comuns.disseny' />" />
	<script type="text/javascript" src="<c:url value="/js/selectable.js"/>"></script>
    <link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
    <link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
</head>
<body>

	<c:import url="../common/tabsDefinicioProces.jsp">
		<c:param name="tabActiu" value="tasques"/>
	</c:import>

	<display:table name="tasques" id="registre" requestURI="" defaultsort="1" class="displaytag selectable">
		<display:column sortProperty="jbpmName" titleKey="comuns.codi" sortable="true" url="/definicioProces/tascaForm.html?definicioProcesId=${param.definicioProcesId}" paramId="id" paramProperty="id">
			<c:if test="${registre.jbpmName == definicioProces.startTaskName}"><img src="<c:url value="/img/control_play_blue.png"/>" alt="<fmt:message key='defproc.tascallist.tasca_ini' />" title="<fmt:message key='defproc.tascallist.tasca_ini' />" border="0" style="float:left;margin-right:3px;position:relative;top:-2px"/></c:if>
			${registre.jbpmName}
		</display:column>
		<display:column property="nom" titleKey="comuns.titol" sortable="true"/>
		<display:column>
	    	<form action="tascaCamps.html">
				<input type="hidden" name="definicioProcesId" value="${definicioProces.id}"/>
				<input type="hidden" name="tascaId" value="${registre.id}"/>
				<button type="submit" class="submitButton"><fmt:message key='comuns.variables' />&nbsp;(${fn:length(registre.camps)})</button>
			</form>
	    </display:column>
		<display:column>
			<c:if test="${registre.jbpmName != definicioProces.startTaskName}">
		    	<form action="tascaDocuments.html">
					<input type="hidden" name="definicioProcesId" value="${definicioProces.id}"/>
					<input type="hidden" name="tascaId" value="${registre.id}"/>
					<button type="submit" class="submitButton"><fmt:message key='comuns.documents' />&nbsp;(${fn:length(registre.documents)})</button>
				</form>
			</c:if>
	    </display:column>
	    <display:column>
			<c:if test="${registre.jbpmName != definicioProces.startTaskName}">
		    	<form action="tascaFirmes.html">
					<input type="hidden" name="definicioProcesId" value="${definicioProces.id}"/>
					<input type="hidden" name="tascaId" value="${registre.id}"/>
					<button type="submit" class="submitButton"><fmt:message key='defproc.tascallist.signatures' />&nbsp;(${fn:length(registre.firmes)})</button>
				</form>
			</c:if>
	    </display:column>
	    <%--display:column>
	    	<form action="tascaValidacions.html">
				<input type="hidden" name="definicioProcesId" value="${definicioProces.id}"/>
				<input type="hidden" name="tascaId" value="${registre.id}"/>
				<button type="submit" class="submitButton"<c:if test="${registre.tipus != 'FORM'}"> disabled="disabled"</c:if>>Validacions</button>
			</form>
	    </display:column--%>
	</display:table>
	<script type="text/javascript">initSelectable();</script>

</body>
</html>
