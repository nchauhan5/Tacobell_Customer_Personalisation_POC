<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product" %>
<%@ taglib prefix="grid" tagdir="/WEB-INF/tags/desktop/grid" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/desktop/nav/breadcrumb" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<template:page pageTitle="${pageTitle}">
	<jsp:attribute name="pageScripts">
		<product:productDetailsJavascript/>
	</jsp:attribute>

	<jsp:body>
		<c:if test="${not empty message}">
			<spring:theme code="${message}"/>
		</c:if>
		
		<div id="globalMessages">
			<common:globalMessages/>
		</div>

		<div class="span-24 orderFormHeader">
			<div class="span-3" id="primary_image">
				<product:productPrimaryImage product="${product}" format="thumbnail"/>
			</div>

			<div class="span-13">
				<product:productPricePanel product="${product}" isOrderForm="${true}"/>
				<div class="pd_name">${product.manufacturer}</div>
				<p>${product.summary}</p>
				<input type="hidden" name="productCodePost" id="productCodePost" value="${product.baseProduct}">
			</div>

			<div class="span-8 last orderFormTotal">
				<product:productFormAddToCartPanel product="${product}"/>
			</div>
		</div>

		<grid:gridLegend />
		
		<button class="js-expand-grid-button right">
			<spring:theme code="product.grid.expand" />
		</button>
		<c:url value="/cart/addGrid?CSRFToken=${CSRFToken}" var="addToCartGridUrl"/>
		<spring:theme code="product.grid.future.tooltip.error" var="gridFutureTooltipErrorMessage"/>
		<spring:theme code="product.grid.future.tooltip.delivery" var="gridFutureTooltipDeliveryMessage" />
		<spring:theme code="product.grid.future.tooltip.qty" var="gridFutureTooltipQtyMessage" />
		<spring:theme code="product.grid.confirmQtys.message" var="gridConfirmMessage" />
		<form:form name="AddToCartOrderForm" id="AddToCartOrderForm" class="add_to_cart_order_form"
			  action="${addToCartGridUrl}" method="post"
			  data-grid-confirm-message="${gridConfirmMessage}"
			  data-grid-future-tooltip-error-message="${gridFutureTooltipErrorMessage}"
			  data-grid-future-tooltip-heading-delivery="${gridFutureTooltipDeliveryMessage}"
			  data-grid-future-tooltip-heading-qty="${gridFutureTooltipQtyMessage}">
			<product:productOrderFormGrid product="${product}" />

		</form:form>

		<product:productOrderFormJQueryTemplates />
	</jsp:body>
</template:page>
