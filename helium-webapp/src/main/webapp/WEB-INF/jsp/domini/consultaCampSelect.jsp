<%@ page language="java" contentType="text/plain; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:set var="campText" value="${camp.dominiCampText}" />
<c:if test="${not empty camp.enumeracio}">
	<c:set var="campText" value="valor" />
</c:if>
<c:if test="${not empty camp.consulta}">
	<c:set var="campText" value="${camp.consultaCampText}" />
</c:if>
<c:set var="campValor" value="${camp.dominiCampValor}" />
<c:if test="${not empty camp.enumeracio}">
	<c:set var="campValor" value="codi" />
</c:if>
<c:if test="${not empty camp.consulta}">
	<c:set var="campValor" value="${camp.consultaCampValor}" />
</c:if>
[
<c:forEach var="fila" items="${resultat}" varStatus="status">{<c:forEach
		var="columna" items="${fila.columnes}">
		<c:if test="${columna.codi == campText}">
			<c:set var="columnaValor" value="${columna.valor}" />text:"<%=(pageContext.getAttribute("columnaValor")
								.toString()).replace("\"", "\\\"")%>"</c:if>
	</c:forEach>
	<c:forEach var="columna" items="${fila.columnes}">
		<c:if test="${columna.codi == campValor}">
			<c:set var="columnaValor" value="${columna.valor}" />,valor:"<%=(pageContext.getAttribute("columnaValor")
								.toString()).replace("\"", "\\\"")%>"</c:if>
	</c:forEach>}<c:if test="${not status.last}">,</c:if>
</c:forEach>
]
