<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav" %>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/desktop/formElement" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="multi-checkout" tagdir="/WEB-INF/tags/desktop/checkout/multi" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<c:url value="${currentStepUrl}" var="choosePaymentMethodUrl"/>
<template:page pageTitle="${pageTitle}" hideHeaderLinks="true">

	<div id="globalMessages">
		<common:globalMessages/>
	</div>

	<multi-checkout:checkoutProgressBar steps="${checkoutSteps}" progressBarId="${progressBarId}"/>

	<div class="span-14 append-1">
		<div id="checkoutContentPanel" class="clearfix">
			<div class="headline"><spring:theme code="checkout.multi.paymentMethod.addPaymentDetails.paymentCard"/></div>
			<div class="required right"><spring:theme code="form.required"/></div>
			<div class="description"><spring:theme code="checkout.multi.paymentMethod.addPaymentDetails.enterYourCardDetails"/></div>

			<form:form method="post" commandName="paymentDetailsForm" class="create_update_payment_form">
				<div class="cardForm">
					<form:hidden path="paymentId" class="create_update_payment_id"/>
					<formElement:formSelectBox idKey="cardType" labelKey="payment.cardType" path="cardTypeCode" mandatory="true" skipBlank="false" skipBlankMessageKey="payment.cardType.pleaseSelect" items="${cardTypes}" tabindex="1"/>
					<formElement:formInputBox idKey="nameOnCard" labelKey="payment.nameOnCard" path="nameOnCard" inputCSS="text" mandatory="true" tabindex="2"/>
					<formElement:formInputBox idKey="cardNumber" labelKey="payment.cardNumber" path="cardNumber" inputCSS="text" mandatory="true" tabindex="3"/>
					<fieldset id="startDate" class="cardDate">
						<legend><spring:theme code="payment.startDate"/></legend>
						<formElement:formSelectBox idKey="StartMonth" labelKey="payment.month" path="startMonth" mandatory="true" skipBlank="false" skipBlankMessageKey="" items="${months}" tabindex="4"/>
						<formElement:formSelectBox idKey="StartYear" labelKey="payment.year" path="startYear" mandatory="true" skipBlank="false" skipBlankMessageKey="" items="${startYears}" tabindex="5"/>
					</fieldset>
					<fieldset class="cardDate">
						<legend><spring:theme code="payment.expiryDate"/></legend>
						<formElement:formSelectBox idKey="ExpiryMonth" labelKey="payment.month" path="expiryMonth" mandatory="true" skipBlank="false" skipBlankMessageKey="" items="${months}" tabindex="6"/>
						<formElement:formSelectBox idKey="ExpiryYear" labelKey="payment.year" path="expiryYear" mandatory="true" skipBlank="false" skipBlankMessageKey="" items="${expiryYears}" tabindex="7"/>
					</fieldset>
					<div id="issueNum">
						<formElement:formInputBox idKey="payment.issueNumber" labelKey="payment.issueNumber" path="issueNumber" inputCSS="text" mandatory="false" tabindex="8"/>
					</div>
				</div>

				<div class="headline clear"><spring:theme code="checkout.multi.paymentMethod.addPaymentDetails.billingAddress"/></div>
				<div class="description"><spring:theme code="checkout.multi.paymentMethod.addPaymentDetails.billingAddressDiffersFromDeliveryAddress"/></div>
				<div>
					<c:if test="${cartData.deliveryItemsQuantity > 0}">
						<form:checkbox id="differentAddress" path="newBillingAddress" tabindex="9"/>
						<label for="differentAddress"><spring:theme code="checkout.multi.paymentMethod.addPaymentDetails.enterDifferentBillingAddress"/></label>
					</c:if>
				</div>

				<div id="newBillingAddressFields" class="cardForm">
					<form:hidden path="billingAddress.addressId" class="create_update_address_id"/>
					<formElement:formSelectBox idKey="address.title" labelKey="address.title" path="billingAddress.titleCode" mandatory="true" skipBlank="false" skipBlankMessageKey="address.title.pleaseSelect" items="${titles}" tabindex="10"/>
					<formElement:formInputBox idKey="address.firstName" labelKey="address.firstName" path="billingAddress.firstName" inputCSS="text" mandatory="true" tabindex="11"/>
					<formElement:formInputBox idKey="address.surname" labelKey="address.surname" path="billingAddress.lastName" inputCSS="text" mandatory="true" tabindex="12"/>
					<formElement:formInputBox idKey="address.line1" labelKey="address.line1" path="billingAddress.line1" inputCSS="text" mandatory="true" tabindex="14"/>
					<formElement:formInputBox idKey="address.line2" labelKey="address.line2" path="billingAddress.line2" inputCSS="text" mandatory="false" tabindex="15"/>
					<formElement:formInputBox idKey="address.townCity" labelKey="address.townCity" path="billingAddress.townCity" inputCSS="text" mandatory="true" tabindex="16"/>
					<formElement:formInputBox idKey="address.postcode" labelKey="address.postcode" path="billingAddress.postcode" inputCSS="text" mandatory="true" tabindex="17"/>
					<formElement:formSelectBox idKey="address.country" labelKey="address.country" path="billingAddress.countryIso" mandatory="true" skipBlank="false" skipBlankMessageKey="address.selectCountry" items="${billingCountries}" itemValue="isocode" tabindex="18"/>
					<form:hidden path="billingAddress.shippingAddress"/>
					<form:hidden path="billingAddress.billingAddress"/>
				</div>

				<div class="save_payment_details">
					<sec:authorize access="!hasAnyRole('ROLE_ANONYMOUS')">
						<form:checkbox id="SaveDetails" path="saveInAccount" tabindex="19"/>
						<label for="SaveDetails"><spring:theme code="checkout.multi.paymentMethod.addPaymentDetails.savePaymentDetailsInAccount"/></label>
					</sec:authorize>
				</div>

				<div class="form-actions">
					<c:if test="${not hasNoPaymentInfo}">
						<a class="button" href="${choosePaymentMethodUrl}"><spring:theme code="checkout.multi.cancel" text="Cancel"/></a>
					</c:if>
					<ycommerce:testId code="editPaymentMethod_savePaymentMethod_button">
						<button class="positive" tabindex="20" id="lastInTheForm" type="submit">
							<spring:theme code="checkout.multi.paymentMethod.addPaymentDetails.useThesePaymentDetails"/>
						</button>
					</ycommerce:testId>
				</div>
			</form:form>
		</div>
	</div>
	<multi-checkout:checkoutOrderDetails cartData="${cartData}" showShipDeliveryEntries="true" showPickupDeliveryEntries="true" showTax="true"/>

	<cms:pageSlot position="SideContent" var="feature" element="div" class="span-24 side-content-slot cms_disp-img_slot">
		<cms:component component="${feature}"/>
	</cms:pageSlot>

</template:page>
