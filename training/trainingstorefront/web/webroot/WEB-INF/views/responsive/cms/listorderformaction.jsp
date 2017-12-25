<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>

<c:if test="${product.multidimensional}" >
	<c:url value="${product.url}/orderForm" var="productOrderFormUrl"/>
	<form:form id="orderForm${product.code}" action="${productOrderFormUrl}" method="get">		
		<button id="productOrderFormButton" type="submit" class="btn btn-block btn-default glyphicon glyphicon-list-alt productOrderFormButton">

		</button>
	</form:form>
</c:if>
