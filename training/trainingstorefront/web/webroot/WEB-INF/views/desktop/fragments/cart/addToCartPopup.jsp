<%@ page trimDirectiveWhitespaces="true" contentType="application/json" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product" %>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/desktop/cart" %>




{"cartData": {
"total": "${cartData.totalPrice.value}",
"products": [
<c:forEach items="${cartData.entries}" var="cartEntry" varStatus="status">
	{
		"sku":		"${cartEntry.product.code}",
		"name": 	"<c:out value='${cartEntry.product.name}' />",
		"qty": 		"${cartEntry.quantity}",
		"price": 	"${cartEntry.basePrice.value}",
		"categories": [
		<c:forEach items="${cartEntry.product.categories}" var="category" varStatus="categoryStatus">
			"<c:out value='${category.name}' />"<c:if test="${not categoryStatus.last}">,</c:if>
		</c:forEach>
		]
	}<c:if test="${not status.last}">,</c:if>
</c:forEach>
]
},
"cartAnalyticsData":{"cartCode" : "${cartCode}","productPostPrice":"${entry.basePrice.value}","productName":"<c:out value='${product.name}' />"}
,
"addToCartLayer":"<spring:escapeBody javaScriptEscape="true">
<spring:theme code="text.addToCart" var="addToCartText"/>
<c:url value="/cart" var="cartUrl"/>
<c:url value="/cart/checkout" var="checkoutUrl"/>
<div id="addToCartLayer">
<div class="cart_popup_error_msg">
	<c:choose>
		<c:when test="${multidErrorMsgs ne null and not empty multidErrorMsgs}">
			<c:forEach items="${multidErrorMsgs}" var="multidErrorMsg">
				<spring:theme code="${multidErrorMsg}" />
				</br>
			</c:forEach>
		</c:when>
		<c:otherwise>
			<spring:theme code="${errorMsg}" />
		</c:otherwise>
	</c:choose>
</div>
<div class="legend"><spring:theme code="basket.added.to.basket" />
	<c:if test="${numberShowing > 0 and fn:length(products) > numberShowing}">
		<p class="legend">
			<spring:theme code="popup.cart.showing" arguments="${numberShowing},${fn:length(products)}"/>
			<c:if test="${fn:length(products) > numberShowing}">
				<a href="${cartUrl}">Show All</a>
			</c:if>
		</p>
	</c:if>
</div>
<c:choose>
	<c:when test="${modifications ne null}">
		<c:forEach items="${modifications}" var="modification" end="${numberShowing - 1}">
			<c:set var="product" value="${modification.entry.product}" />
			<c:set var="entry" value="${modification.entry}" />
			<c:set var="quantity" value="${modification.quantityAdded}" />
			<cart:popupCartItems entry="${entry}" product="${product}" quantity="${quantity}"/>
		</c:forEach>
	</c:when>
	<c:otherwise>
		<cart:popupCartItems entry="${entry}" product="${product}" quantity="${quantity}"/>
	</c:otherwise>
</c:choose>
<div class="links"><a href="${cartUrl}" class="button positive"><spring:theme code="checkout.checkout" /></a></div>

</div>


</spring:escapeBody>"
}



