<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="address" tagdir="/WEB-INF/tags/responsive/address"%>

<spring:url value="/my-account/address-book" var="addressBookUrl"/>

<c:choose>
	<c:when test="${edit eq true }">
        <c:set var="headline"><spring:theme code="text.account.addressBook.updateAddress" text="Update Address"/></c:set>
	</c:when>
	<c:otherwise>
        <c:set var="headline"><spring:theme code="text.account.addressBook.addAddress" text="New Address"/></c:set>
	</c:otherwise>
</c:choose>
<div class="back-link border">
    <div class="row">
        <div class="container-lg col-md-6">
            <button type="button" class="addressBackBtn" data-back-to-addresses="${addressBookUrl}">
                <span class="glyphicon glyphicon-chevron-left"></span>
            </button>
            <span class="label">${headline}</span>
        </div>
    </div>
</div>
<div class="row">
    <div class="container-lg col-md-6">
        <div class="account-section-content">
            <div class="account-section-form">
                <address:addressFormSelector supportedCountries="${countries}" regions="${regions}" cancelUrl="/my-account/address-book" addressBook="true" />
            </div>
        </div>
    </div>
</div>

