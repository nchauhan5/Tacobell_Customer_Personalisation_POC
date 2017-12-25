<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="quote" tagdir="/WEB-INF/tags/responsive/quote" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%--
    ~ /*
    ~  * [y] hybris Platform
    ~  *
    ~  * Copyright (c) 2000-2017 SAP SE or an SAP affiliate company.
    ~  * All rights reserved.
    ~  *
    ~  * This software is the confidential and proprietary information of SAP
    ~  * ("Confidential Information"). You shall not disclose such Confidential
    ~  * Information and shall use it only in accordance with the terms of the
    ~  * license agreement you entered into with SAP.
    ~  *
    ~  */
--%>

<spring:htmlEscape defaultHtmlEscape="true"/>
<spring:url value="/my-account/my-quotes/" var="quoteBaseLink" htmlEscape="false" />
<spring:url value="/quote/${quoteData.code}/checkout/" var="acceptQuoteAndCheckoutUrl" htmlEscape="false" />
<spring:url value="/quote/${quoteData.code}/edit/" var="editQuoteUrl" htmlEscape="false" />
<spring:url value="/quote/${quoteData.code}/submit/" var="submitQuoteUrl" htmlEscape="false" />
<spring:url value="/quote/${quoteData.code}/approve/" var="approveQuoteUrl" htmlEscape="false" />
<spring:url value="/quote/${quoteData.code}/reject/" var="rejectQuoteUrl" htmlEscape="false" />


<div class="cart-header border">
    <div class="row">
        <div class="col-xs-12 col-sm-6 pull-right">
            <c:if test="${not empty savedCartCount and savedCartCount ne 0}">
                <spring:url value="/my-account/saved-carts" var="listSavedCartUrl" htmlEscape="false"/>
                <a href="${listSavedCartUrl}" class="save__cart--link cart__head--link">
                    <spring:theme code="saved.cart.total.number" arguments="${savedCartCount}"/>
                </a>
            </c:if>
            <c:if test="${not empty quoteCount and quoteCount ne 0}">
                <spring:url value="/my-account/my-quotes" var="listQuotesUrl" htmlEscape="false"/>
                    <a href="${listQuotesUrl}" class="cart__quotes--link cart__head--link">
                        <spring:theme code="saved.quote.total.number" arguments="${quoteCount}"/>
                    </a>
            </c:if>
        </div>
    </div>
</div>

<c:if test="${quoteData.totalPrice.value lt 0}">
    <div class="alert alert-danger" role="alert">
        <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <spring:theme code="text.quote.not.submitable"/>
     </div>
</c:if>
<div class="well well-tertiary well-lg">
    <div class="order-detail-overview">
        <div class="row">
            <div class="col-sm-12 col-md-9">
                <div class="row">
                    <div class="col-sm-3">
                        <div class="item-group">
                            <span class="item-label"><spring:theme code="text.account.quote.code"/></span>
                            <span class="item-value">${fn:escapeXml(quoteData.code)}</span>
                        </div>
                    </div>
                    <div class="col-sm-3">
                        <div class="item-group">
                            <span class="item-label">Version</span>
                            <span class="item-value">${fn:escapeXml(quoteData.version)}</span>
                        </div>
                    </div>
                    <div class="col-sm-3">
                        <div class="item-group">
                            <span class="item-label"><spring:theme code="text.quote.state.label"/></span>
                            <span class="item-value"><spring:theme code="text.account.quote.status.display.${quoteData.state}"/></span>
                        </div>
                    </div>
                    <div class="col-sm-3">
                        <div class="item-group">
                     	   <span class="item-label"><spring:theme code="text.account.quote.date.updated"/></span>
                           <span class="item-value"><fmt:formatDate value="${quoteData.updatedTime}" dateStyle="medium" timeStyle="short" type="both"/></span>
             		   </div>
                    </div>
                    <div class="col-sm-3">
                        <div class="item-group">
                            <span class="item-label"><spring:theme code="text.account.quote.date.placed"/></span>
                            <span class="item-value"><fmt:formatDate value="${quoteData.creationTime}" dateStyle="medium" timeStyle="short" type="both"/></span>
                        </div>
                    </div>
                    <c:if test="${ycommerce:isQuoteUserSalesRep() || ycommerce:isQuoteUserSellerApprover() || allowedActions['CHECKOUT']}">
                        <div class="col-sm-3">
                            <div class="item-group">
                                <span class="item-label"><spring:theme code="text.quote.expiration.time.label"/></span>
                                <span class="item-value"><fmt:formatDate value="${quoteData.expirationTime}" dateStyle="medium" timeStyle="short" type="both"/></span>
                            </div>
                        </div>
                    </c:if>
                    <div class="col-sm-12">
                        <div class="item-group">
                            <span class="item-label"><spring:theme code="text.quote.name.label"/></span>
                            <span class="item-value">${fn:escapeXml(quoteData.name)}</span>
                        </div>
                    </div>
                    <div class="col-sm-12">
                        <div class="item-group">
                            <span class="item-label"><spring:theme code="text.quote.description.label"/></span>
                            <span class="item-value">${fn:escapeXml(quoteData.description)}</span>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-sm-12 col-md-3">
                <div class="item-action">
                    <c:if test="${allowedActions['CANCEL']}">
                        <button type="button" class="btn btn-default btn-block item__btn js-quote-cancel-btn">
                            <spring:theme code="quote.cancel" />
                        </button>
                    </c:if>
                    <c:if test="${allowedActions['EDIT']}">
                        <button type="button" class="btn btn-primary btn-block item__btn ${allowedActions['CHECKOUT'] ? 'js-quote-warning-btn':'js-quote-edit-btn'}" 
                            id="editQuoteButton" data-quote-edit-url="${editQuoteUrl}">
                            <spring:theme code="quote.edit" />
                        </button>    
                    </c:if>
                    <c:if test="${allowedActions['SUBMIT']}">
                        <form:form action="${submitQuoteUrl}" id="submitQuoteForm" method="POST">
                            <c:choose>
                                <c:when test="${quoteData.totalPrice.value lt 0}">
                                    <button type="submit" class="btn btn-primary btn-block re-order item__btn js-quote-submit-btn" id="submitQuoteButton" disabled="disabled">
                                        <spring:theme code="quote.submit" />
                                    </button>
                                </c:when>
                                <c:otherwise>
                                    <button type="submit" class="btn btn-primary btn-block re-order item__btn js-quote-submit-btn" id="submitQuoteButton">
                                        <spring:theme code="quote.submit" />
                                    </button>
                                </c:otherwise>
                            </c:choose>
                        </form:form>
                    </c:if>
                    <c:if test="${allowedActions['APPROVE']}">
                        <form:form action="${approveQuoteUrl}" id="approveQuoteForm" method="POST">
                            <button type="submit" class="btn btn-primary btn-block re-order item__btn">
                                <spring:theme code="quote.approve" />
                            </button>
                        </form:form>
                    </c:if>
                    <c:if test="${allowedActions['REJECT']}">
                        <form:form action="${rejectQuoteUrl}" id="rejectQuoteForm" method="POST">
                            <button type="submit" class="btn btn-primary btn-block re-order item__btn">
                                <spring:theme code="quote.reject" />
                            </button>
                        </form:form>
                    </c:if>
                    <c:if test="${allowedActions['CHECKOUT']}">
                        <form:form action="${acceptQuoteAndCheckoutUrl}" id="checkoutQuoteForm" method="POST" class="url-holder">
                            <button type="submit" class="btn btn-primary btn-block item__btn js-quote-checkout-btn" id="checkoutQuoteButton">
                                <spring:theme code="quote.acceptandcheckout" />
                            </button>
                        </form:form>
                    </c:if>
                </div>
            </div>
        </div>
    </div>
</div>
<div class="quote__comments">
    <quote:quoteComments comments="${quoteData.comments}" disabled="true"/>
</div>
<quote:quoteCancelConfirmation quoteData="${quoteData}"/>
<quote:quoteSubmitConfirmation quoteData="${quoteData}"/>
<quote:quoteAcceptAndCheckoutConfirmation quoteData="${quoteData}"/>
<quote:quoteEditConfirm quoteData="${quoteData}"/>
