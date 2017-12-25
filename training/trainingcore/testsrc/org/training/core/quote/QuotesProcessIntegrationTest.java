/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package org.training.core.quote;


import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.acceleratorfacades.order.impl.DefaultAcceleratorCheckoutFacade;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.basecommerce.util.BaseCommerceBaseTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.impl.DefaultQuoteFacade;
import de.hybris.platform.commercefacades.quote.data.QuoteData;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commerceservices.enums.QuoteUserType;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.impl.DefaultCommerceCheckoutService;
import de.hybris.platform.commerceservices.order.impl.DefaultCommerceQuoteService;
import de.hybris.platform.commerceservices.order.strategies.impl.DefaultQuoteStateSelectionStrategy;
import de.hybris.platform.commerceservices.order.strategies.impl.DefaultQuoteUpdateExpirationTimeStrategy;
import de.hybris.platform.commerceservices.order.strategies.impl.DefaultQuoteUserIdentificationStrategy;
import de.hybris.platform.commerceservices.order.strategies.impl.DefaultQuoteUserTypeIdentificationStrategy;
import de.hybris.platform.commerceservices.order.strategies.impl.DefaultQuoteMetadataValidationStrategy;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.order.QuoteService;
import de.hybris.platform.payment.commands.factory.CommandFactoryRegistry;
import de.hybris.platform.payment.impl.DefaultPaymentServiceImpl;
import de.hybris.platform.payment.methods.impl.DefaultCardPaymentServiceImpl;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.context.ContextConfiguration;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.given;

@Ignore // Fails randomly on CSP pipeline
@ContextConfiguration(locations =
		{ "classpath:/payment-spring-test.xml" })
@IntegrationTest
public class QuotesProcessIntegrationTest extends BaseCommerceBaseTest
{
	private static final Logger LOG = Logger.getLogger(QuotesProcessIntegrationTest.class);
	private static final String TEST_BASESITE_UID = "testSite";
	private static final double EPS = 0.001;
    private static final String[] productArr = {"HW1210-3422","HW1210-3423","HW1210-3424","HW1210-3425"};
	private static final String SELLERAPPROVER = "sellerapprover";
	private static final String SALESREP1 = "salesrep1@test.com";
	private static final String SALESREP2 = "salesrep2@test.com";
	private static final String SALESREP3 = "salesrep3@test.com";
	private static final String QUOTEBUYER1 = "quotebuyer1@test.com";
	private static final String QUOTEBUYER2 = "quotebuyer2@test.com";
	private static final String QUOTEBUYER3 = "quotebuyer3@test.com";

	@Resource
	private BaseSiteService baseSiteService;
	@Resource
	private CartFacade cartFacade;
	@Resource
	private ProductService productService;
	@Resource
	private CatalogVersionService catalogVersionService;
	@Resource
	private UserService userService;
	@Resource
	private DefaultQuoteFacade quoteFacade;
	@Resource
	private QuoteService quoteService;
	@Resource
	private DefaultCommerceQuoteService commerceQuoteService;
	@Mock
	private DefaultQuoteUserIdentificationStrategy quoteUserIdentificationStrategy;
	@Mock
	private DefaultQuoteUserTypeIdentificationStrategy quoteUserTypeIdentificationStrategy;
	@Resource(name = "acceleratorCheckoutFacade")
	private DefaultAcceleratorCheckoutFacade checkoutFacade;
	@Resource
	private DefaultCardPaymentServiceImpl cardPaymentService;
	@Resource
	private DefaultPaymentServiceImpl paymentService;
	@Resource
	private DefaultCommerceCheckoutService commerceCheckoutService;
	@Resource
	private UserFacade userFacade;
	@Resource
	private CommandFactoryRegistry mockupCommandFactoryRegistry;
	@Resource
	private DefaultQuoteStateSelectionStrategy quoteStateSelectionStrategy;
	@Resource
	private DefaultQuoteUpdateExpirationTimeStrategy quoteUpdateExpirationTimeStrategy;
	@Resource
	private DefaultQuoteMetadataValidationStrategy quoteMetadataValidationStrategy;

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultCatalog();
		createDefaultUsers();
		importCsv( "/test/testQuoteProcessFlow.impex", "utf-8" );

		final BaseSiteModel baseSiteForUID = baseSiteService.getBaseSiteForUID( TEST_BASESITE_UID );
		baseSiteService.setCurrentBaseSite( baseSiteForUID, false );
		final CatalogVersionModel catalogVersionModel = catalogVersionService.getCatalogVersion( "testCatalog", "Online" );
		assertNotNull( catalogVersionModel );

		catalogVersionService.setSessionCatalogVersions( Collections.singletonList( catalogVersionModel ) );
		cardPaymentService.setCommandFactoryRegistry( mockupCommandFactoryRegistry );
		paymentService.setCardPaymentService( cardPaymentService );
		commerceCheckoutService.setPaymentService( paymentService );
		quoteFacade.setQuoteUserIdentificationStrategy( quoteUserIdentificationStrategy );
		commerceQuoteService.setQuoteUserTypeIdentificationStrategy(quoteUserTypeIdentificationStrategy);
		quoteStateSelectionStrategy.setQuoteUserTypeIdentificationStrategy(quoteUserTypeIdentificationStrategy);
		quoteUpdateExpirationTimeStrategy.setQuoteUserTypeIdentificationStrategy(quoteUserTypeIdentificationStrategy);
		quoteMetadataValidationStrategy.setQuoteUserTypeIdentificationStrategy(quoteUserTypeIdentificationStrategy);
	}

	@Test
	public void testBuyerSubmitQuoteWithAutoApproval() throws Exception
	{
		final String quoteCode = initiateQuoteForBuyer(QUOTEBUYER1, 2 );
		checkForTargetQuoteState( quoteCode, QuoteState.SELLER_REQUEST );
		assertEquals( QuoteState.SELLER_REQUEST, quoteService.getCurrentQuoteForCode( quoteCode ).getState() );

		final UserModel salesrep = userService.getUserForUID(SALESREP1);
		given( quoteUserIdentificationStrategy.getCurrentQuoteUser() ).willReturn( salesrep );
		given( quoteUserTypeIdentificationStrategy.getCurrentQuoteUserType(salesrep)).willReturn(Optional.of(QuoteUserType.SELLER));
		quoteFacade.submitQuote( quoteCode );

		checkForTargetQuoteState( quoteCode, QuoteState.BUYER_OFFER );
		assertEquals( QuoteState.BUYER_OFFER, quoteService.getCurrentQuoteForCode( quoteCode ).getState() );
	}

	@Test
	public void testBuyerSubmitQuoteWithSellerApprover() throws Exception
	{
		final String quoteCode = initiateQuoteForBuyer(QUOTEBUYER2, 4 );
		checkForTargetQuoteState( quoteCode, QuoteState.SELLER_REQUEST );
		assertEquals( QuoteState.SELLER_REQUEST, quoteService.getCurrentQuoteForCode( quoteCode ).getState() );

		final UserModel salesrep = userService.getUserForUID(SALESREP2);
		given( quoteUserIdentificationStrategy.getCurrentQuoteUser() ).willReturn( salesrep );
		given( quoteUserTypeIdentificationStrategy.getCurrentQuoteUserType(salesrep)).willReturn(Optional.of(QuoteUserType.SELLER));
		quoteFacade.submitQuote( quoteCode );
		checkForTargetQuoteState( quoteCode, QuoteState.SELLERAPPROVER_PENDING );
		assertEquals( QuoteState.SELLERAPPROVER_PENDING, quoteService.getCurrentQuoteForCode( quoteCode ).getState() );

		final UserModel sellerApprover = userService.getUserForUID( SELLERAPPROVER );
		given( quoteUserIdentificationStrategy.getCurrentQuoteUser() ).willReturn( sellerApprover );
		given( quoteUserTypeIdentificationStrategy.getCurrentQuoteUserType(sellerApprover)).willReturn(Optional.of(QuoteUserType.SELLERAPPROVER));
		quoteFacade.approveQuote( quoteCode );
		checkForTargetQuoteState( quoteCode, QuoteState.BUYER_OFFER );
		assertEquals( QuoteState.BUYER_OFFER, quoteService.getCurrentQuoteForCode( quoteCode ).getState() );

		final UserModel buyer = userService.getUserForUID(QUOTEBUYER2);
		given( quoteUserIdentificationStrategy.getCurrentQuoteUser() ).willReturn( buyer);
		given( quoteUserTypeIdentificationStrategy.getCurrentQuoteUserType(buyer)).willReturn(Optional.of(QuoteUserType.BUYER));

		quoteFacade.acceptAndPrepareCheckout( quoteCode );
		completeCheckoutProcess();
		checkForTargetQuoteState( quoteCode, QuoteState.BUYER_ORDERED );
		assertEquals( QuoteState.BUYER_ORDERED, quoteService.getCurrentQuoteForCode( quoteCode ).getState() );
	}

	@Test
	public void testBuyerSubmitQuoteWithSellerRejection() throws Exception
	{
		final String quoteCode = initiateQuoteForBuyer(QUOTEBUYER3, 4 );
		checkForTargetQuoteState( quoteCode, QuoteState.SELLER_REQUEST );
		assertEquals( QuoteState.SELLER_REQUEST, quoteService.getCurrentQuoteForCode( quoteCode ).getState() );

		final UserModel salesrep = userService.getUserForUID(SALESREP3);
		given( quoteUserIdentificationStrategy.getCurrentQuoteUser() ).willReturn( salesrep );
		given( quoteUserTypeIdentificationStrategy.getCurrentQuoteUserType(salesrep)).willReturn(Optional.of(QuoteUserType.SELLER));
		quoteFacade.submitQuote( quoteCode );
		checkForTargetQuoteState( quoteCode, QuoteState.SELLERAPPROVER_PENDING );
		assertEquals( QuoteState.SELLERAPPROVER_PENDING, quoteService.getCurrentQuoteForCode( quoteCode ).getState() );

		final UserModel sellerApprover = userService.getUserForUID( SELLERAPPROVER );
		given( quoteUserIdentificationStrategy.getCurrentQuoteUser() ).willReturn( sellerApprover );
		given( quoteUserTypeIdentificationStrategy.getCurrentQuoteUserType(sellerApprover)).willReturn(Optional.of(QuoteUserType.SELLERAPPROVER));
		quoteFacade.rejectQuote( quoteCode );
		checkForTargetQuoteState( quoteCode, QuoteState.SELLER_REQUEST );
		assertEquals( QuoteState.SELLER_REQUEST, quoteService.getCurrentQuoteForCode( quoteCode ).getState() );
	}

	protected void addItemsToCartBeforeCheckout( final int numOfProducts )
	{
		int qtyCounter = 0;
		for( int i = 1; i <= numOfProducts && i <= productArr.length; i++ )
		{
			final ProductModel product = productService.getProductForCode( catalogVersionService.getSessionCatalogVersions().iterator().next(),
					productArr[i - 1] );
			assertNotNull( product );
			try
			{
				cartFacade.addToCart( product.getCode(), i );
				qtyCounter = +i;
			}
			catch( final CommerceCartModificationException e2 )
			{
				fail();
			}
		}
		assertEquals( qtyCounter, cartFacade.getSessionCart().getEntries().size() );
	}

	protected String initiateQuoteForBuyer(final String quoteBuyerId, final int numOfProducts )
	{
		final UserModel quoteBuyer = userService.getUserForUID( quoteBuyerId );
		userService.setCurrentUser( quoteBuyer );
		cartFacade.removeSessionCart();
		cartFacade.getSessionCart();
		addItemsToCartBeforeCheckout( numOfProducts );
		given( quoteUserIdentificationStrategy.getCurrentQuoteUser() ).willReturn( quoteBuyer );
		given( quoteUserTypeIdentificationStrategy.getCurrentQuoteUserType(quoteBuyer)).willReturn(Optional.of(QuoteUserType.BUYER));
		final QuoteData newQuoteData = quoteFacade.initiateQuote();
		final String quoteCode = newQuoteData.getCode();
		quoteFacade.enableQuoteEdit( quoteCode );
		quoteFacade.submitQuote( quoteCode );
		return quoteCode;
	}

	protected OrderData completeCheckoutProcess() throws CommerceCartModificationException, InvalidCartException
	{
		LOG.info( "Set delivery address..." );
		final AddressData deliveryAddress = buildDeliveryAddress();
		checkoutFacade.setDeliveryAddress( deliveryAddress );
		userFacade.addAddress( deliveryAddress );
		LOG.info( "Set payment info..." );
		final CCPaymentInfoData paymentInfo = buildPaymentInfo( deliveryAddress );
		final CCPaymentInfoData newPaymentSubscription = checkoutFacade.createPaymentSubscription( paymentInfo );
		checkoutFacade.setPaymentDetails( newPaymentSubscription.getId() );
		final OrderData order = checkoutFacade.placeOrder();
		LOG.info( "Order submited successfully..." );
		return order;
	}

	protected AddressData buildDeliveryAddress()
	{
		final AddressData address = new AddressData();
		address.setId( "12345" );
		address.setFirstName( "First" );
		address.setLastName( "Last" );
		address.setLine1( "123 ABC St" );
		address.setPostalCode( "12345" );
		address.setTown( "New York" );
		final CountryData countryData = new CountryData();
		countryData.setIsocode( "US" );
		address.setCountry( countryData );
		address.setBillingAddress( true );
		address.setShippingAddress( true );
		address.setDefaultAddress( true );
		return address;
	}

	protected CCPaymentInfoData buildPaymentInfo( final AddressData billingAddress )
	{
		final CCPaymentInfoData paymentInfo = new CCPaymentInfoData();
		paymentInfo.setAccountHolderName( "First Last" );
		paymentInfo.setBillingAddress( billingAddress );
		paymentInfo.setCardNumber( "4111111111111111" );
		paymentInfo.setCardType( "visa" );
		paymentInfo.setExpiryMonth( "1" );
		paymentInfo.setExpiryYear( "2020" );
		paymentInfo.setSubscriptionId( "123" );
		return paymentInfo;
	}

	protected void checkForTargetQuoteState( final String quoteCode, final QuoteState targetQuoteState )
	{
		final long waitUntil = System.currentTimeMillis() + 20 * 1000;
		while( quoteService.getCurrentQuoteForCode( quoteCode ).getState() != targetQuoteState && System.currentTimeMillis() < waitUntil )
		{
			try
			{
				Thread.sleep( 1000 );
			}
			catch( InterruptedException e )
			{
				e.printStackTrace();
			}
		}
	}
}
