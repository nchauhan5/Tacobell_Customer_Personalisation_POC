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
package org.training.test.orders;

import de.hybris.platform.basecommerce.strategies.BaseStoreSelectorStrategy;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminSiteService;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.user.converters.populator.AddressReversePopulator;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.enums.SalesApplication;
import de.hybris.platform.commerceservices.impersonation.ImpersonationContext;
import de.hybris.platform.commerceservices.impersonation.ImpersonationService;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCheckoutService;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.commerceservices.service.data.CommerceOrderResult;
import de.hybris.platform.core.enums.CreditCardType;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.payment.dto.BillingInfo;
import de.hybris.platform.payment.dto.CardInfo;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.ticket.enums.CsEventReason;
import de.hybris.platform.ticket.enums.CsInterventionType;
import de.hybris.platform.ticket.enums.CsTicketCategory;
import de.hybris.platform.ticket.enums.CsTicketPriority;
import de.hybris.platform.ticket.service.TicketBusinessService;
import de.hybris.platform.ticketsystem.data.CsTicketParameter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Create test order data
 */
public class AcceleratorTestOrderData
{
	private static final Logger LOG = Logger.getLogger(AcceleratorTestOrderData.class);

	private static final String ELECTRONICS_SITE_ID = "electronics";
	private static final String CUSTOMER_UID = "OrderHistoryUser@test.com";

	private CMSAdminSiteService cmsAdminSiteService;
	private UserService userService;
	private ImpersonationService impersonationService;
	private CustomerAccountService customerAccountService;
	private CartFacade cartFacade;
	private CartService cartService;
	private CheckoutFacade checkoutFacade;
	private CommerceCheckoutService commerceCheckoutService;
	private AddressReversePopulator addressReversePopulator;
	private BaseStoreSelectorStrategy baseStoreSelectorStrategy;
	private ModelService modelService;
	private CommonI18NService i18nService;
	private TicketBusinessService ticketBusinessService;
	private BaseSiteService baseSiteService;

	protected CMSAdminSiteService getCmsAdminSiteService()
	{
		return cmsAdminSiteService;
	}

	@Required
	public void setCmsAdminSiteService(final CMSAdminSiteService cmsAdminSiteService)
	{
		this.cmsAdminSiteService = cmsAdminSiteService;
	}

	protected UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	protected ImpersonationService getImpersonationService()
	{
		return impersonationService;
	}

	@Required
	public void setImpersonationService(final ImpersonationService siteImpersonationService)
	{
		this.impersonationService = siteImpersonationService;
	}

	protected CustomerAccountService getCustomerAccountService()
	{
		return customerAccountService;
	}

	@Required
	public void setCustomerAccountService(final CustomerAccountService customerAccountService)
	{
		this.customerAccountService = customerAccountService;
	}

	protected CartFacade getCartFacade()
	{
		return cartFacade;
	}

	@Required
	public void setCartFacade(final CartFacade cartFacade)
	{
		this.cartFacade = cartFacade;
	}

	protected CartService getCartService()
	{
		return cartService;
	}

	@Required
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}

	protected CheckoutFacade getCheckoutFacade()
	{
		return checkoutFacade;
	}

	@Required
	public void setCheckoutFacade(final CheckoutFacade checkoutFacade)
	{
		this.checkoutFacade = checkoutFacade;
	}

	protected CommerceCheckoutService getCommerceCheckoutService()
	{
		return commerceCheckoutService;
	}

	@Required
	public void setCommerceCheckoutService(final CommerceCheckoutService commerceCheckoutService)
	{
		this.commerceCheckoutService = commerceCheckoutService;
	}

	protected AddressReversePopulator getAddressReversePopulator()
	{
		return addressReversePopulator;
	}

	@Required
	public void setAddressReversePopulator(final AddressReversePopulator addressReversePopulator)
	{
		this.addressReversePopulator = addressReversePopulator;
	}

	protected BaseStoreSelectorStrategy getBaseStoreSelectorStrategy()
	{
		return baseStoreSelectorStrategy;
	}

	@Required
	public void setBaseStoreSelectorStrategy(final BaseStoreSelectorStrategy baseStoreSelectorStrategy)
	{
		this.baseStoreSelectorStrategy = baseStoreSelectorStrategy;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected TicketBusinessService getTicketBusinessService()
	{
		return ticketBusinessService;
	}

	@Required
	public void setTicketBusinessService(final TicketBusinessService ticketBusinessService)
	{
		this.ticketBusinessService = ticketBusinessService;
	}

	@Required
	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}

	/**
	 * @return the {@link BaseSiteService}
	 */
	protected BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	/**
	 * Create stored card subscription info for the PaymentUser@test.com and OrderHistoryUser@test.com customers
	 */
	public void createPaymentInfos()
	{
		createPaymentInfo("paymentuser@test.com", "USD", createVisaCardInfo(), createUkBillingInfo());
		createPaymentInfo("paymentuser@test.com", "USD", createMasterCardInfo(), createGermanyBillingInfo());

		createPaymentInfo("orderhistoryuser@test.com", "USD", createVisaCardInfo(), createUkBillingInfo());
	}

	public void createPaymentInfo(final String customerUid, final String currencyIso, final CardInfo cardInfo,
			final BillingInfo billingInfo)
	{
		// Lookup the site
		final CMSSiteModel cmsSite = getCmsAdminSiteService().getSiteForId(ELECTRONICS_SITE_ID);
		// Lookup the customer
		final CustomerModel customer = getUserService().getUserForUID(customerUid.toLowerCase(), CustomerModel.class);

		// Impersonate site and customer
		final ImpersonationContext ctx = new ImpersonationContext();
		ctx.setSite(cmsSite);
		ctx.setUser(customer);
		ctx.setCurrency(i18nService.getCurrency(currencyIso));
		getImpersonationService().executeInContext(ctx, () -> {
			// Check if the card info already exists
			final List<CreditCardPaymentInfoModel> storedCards = getCustomerAccountService().getCreditCardPaymentInfos(customer,
					true);
			if (!containsCardInfo(storedCards, cardInfo))
			{
				LOG.info("Creating stored card subscription for [" + customerUid + "] card type [" + cardInfo.getCardType() + "]");

				// Create payment subscription
				final String customerTitleCode = customer == null || customer.getTitle() == null ? null
						: customer.getTitle().getCode();
				final CreditCardPaymentInfoModel creditCardPaymentInfoModel = getCustomerAccountService()
						.createPaymentSubscription(customer, cardInfo, billingInfo, customerTitleCode, getPaymentProvider(), true);

				// Make this the default payment option
				getCustomerAccountService().setDefaultPaymentInfo(customer, creditCardPaymentInfoModel);
			}
			return null;
		});
	}

	protected boolean containsCardInfo(final List<CreditCardPaymentInfoModel> storedCards, final CardInfo cardInfo)
	{
		if (storedCards != null && !storedCards.isEmpty() && cardInfo != null)
		{
			for (final CreditCardPaymentInfoModel storedCard : storedCards)
			{
				if (matchesCardInfo(storedCard, cardInfo))
				{
					return true;
				}
			}
		}
		return false;
	}

	protected boolean matchesCardInfo(final CreditCardPaymentInfoModel storedCard, final CardInfo cardInfo)
	{
		return storedCard.getType().equals(cardInfo.getCardType())
				&& StringUtils.equals(storedCard.getCcOwner(), cardInfo.getCardHolderFullName());
	}

	protected String getPaymentProvider()
	{
		return "Mockup";
	}

	protected CardInfo createVisaCardInfo()
	{
		final CardInfo cardInfo = new CardInfo();
		cardInfo.setCardHolderFullName("John Doe");
		cardInfo.setCardNumber("4111111111111111");
		cardInfo.setCardType(CreditCardType.VISA);
		cardInfo.setExpirationMonth(Integer.valueOf(12));
		cardInfo.setExpirationYear(Integer.valueOf(2020));
		return cardInfo;
	}

	protected CardInfo createMasterCardInfo()
	{
		final CardInfo cardInfo = new CardInfo();
		cardInfo.setCardHolderFullName("John Doe");
		cardInfo.setCardNumber("5555555555554444");
		cardInfo.setCardType(CreditCardType.MASTERCARD_EUROCARD);
		cardInfo.setExpirationMonth(Integer.valueOf(11));
		cardInfo.setExpirationYear(Integer.valueOf(2022));
		return cardInfo;
	}

	protected BillingInfo createUkBillingInfo()
	{
		final BillingInfo billingInfo = new BillingInfo();
		billingInfo.setFirstName("John");
		billingInfo.setLastName("Doe");
		billingInfo.setStreet1("Holborn Tower");
		billingInfo.setStreet2("137 High Holborn");
		billingInfo.setCity("London");
		billingInfo.setPostalCode("WC1V 6PL");
		billingInfo.setCountry("GB");
		billingInfo.setPhoneNumber("+44 (0)20 / 7429 4175");
		return billingInfo;
	}

	protected BillingInfo createGermanyBillingInfo()
	{
		final BillingInfo billingInfo = new BillingInfo();
		billingInfo.setFirstName("John");
		billingInfo.setLastName("Doe");
		billingInfo.setStreet1("Nymphenburger Str. 86");
		billingInfo.setStreet2("Some Line 2 data");
		billingInfo.setCity("Munchen");
		billingInfo.setPostalCode("80636");
		billingInfo.setCountry("DE");
		billingInfo.setPhoneNumber("+49 (0)89 / 890 650");
		return billingInfo;
	}

	/**
	 * Create sample orders for the OrderHistoryUser@test.com customer
	 */
	public void createSampleOrders()
	{
		Map<String, Long> products = null;
		// Create sample order in the electronics site
		products = new HashMap<String, Long>();
		products.put("872912", Long.valueOf(1)); // Secure Digital Card 2GB
		products.put("479956", Long.valueOf(1)); // 4GB Memory Stick Pro Duo + adapter
		createSampleOrder(ELECTRONICS_SITE_ID, CUSTOMER_UID, "USD", products, createUkAddressData(), null, false);

		// Create sample order in the apparel-uk site
		products = new HashMap<String, Long>();
		products.put("300310086", Long.valueOf(1)); // Bag Dakine Factor Pack bomber
		products.put("300147511", Long.valueOf(1)); // T-Shirt Men Playboard Logo Tee irish green M
		createSampleOrder("apparel-uk", CUSTOMER_UID, "GBP", products, createUkAddressData(), null, false);

		// Create sample order in the apparel-de site
		products = new HashMap<String, Long>();
		products.put("300020465", Long.valueOf(1)); // Protector Dainese Waistcoat S7 black/silver M
		products.put("300044623", Long.valueOf(1)); // Shades Anon Legion crystal & black gray
		createSampleOrder("apparel-de", CUSTOMER_UID, "EUR", products, createGermanAddressData(), null, false);
	}

	public void createSampleBOPiSOrders()
	{
		Map<String, Long> products = null;
		// Create sample order in the electronics site
		products = new HashMap<String, Long>();
		products.put("300938", Long.valueOf(1)); // Photosmart E317 Digital Camera
		products.put("1981415", Long.valueOf(1)); // PL60 Silver
		createSampleOrder(ELECTRONICS_SITE_ID, CUSTOMER_UID, "USD", products, createUkAddressData(), "Yokosuka", false);

		// Create sample order in the apparel-uk site
		products = new HashMap<String, Long>();
		products.put("300737290", Long.valueOf(1)); // System Tee SS dirty plum S
		products.put("300737281", Long.valueOf(1)); // System Tee SS lime M
		createSampleOrder("apparel-uk", CUSTOMER_UID, "GBP", products, createUkAddressData(), "Newcastle upon Tyne College", false);
	}

	public OrderModel createSampleOrder(final String siteUid, final String customerUid, final String currencyIso,
			final Map<String, Long> products, final AddressData deliveryAddress, final String storeId, final boolean isCSVData)
	{
		OrderModel orderModel = null;

		// Lookup the site
		// Lookup the customer
		final CMSSiteModel cmsSite = getCmsAdminSiteService().getSiteForId(siteUid);
		final CustomerModel customer = getUserService().getUserForUID(customerUid.toLowerCase(), CustomerModel.class);

		// Impersonate site and customer
		final ImpersonationContext ctx = new ImpersonationContext();
		ctx.setSite(cmsSite);
		ctx.setUser(customer);
		ctx.setCurrency(i18nService.getCurrency(currencyIso));
		orderModel = getImpersonationService().executeInContext(ctx,
				new ImpersonationService.Executor<OrderModel, ImpersonationService.Nothing>() // NOSONAR
				{
					@Override
					public OrderModel execute() throws ImpersonationService.Nothing
					{
						final BaseStoreModel baseStore = getBaseStoreSelectorStrategy().getCurrentBaseStore();
						final String submitOrderProcessCode = baseStore.getSubmitOrderProcessCode();
						final String originalPaymentProvider = baseStore.getPaymentProvider();
						OrderModel orderModel = null;
						baseStore.setPaymentProvider("Mockup");
						try
						{

							// Check if the order already exists
							final List<OrderModel> orderList = getCustomerAccountService().getOrderList(customer,
									getBaseStoreSelectorStrategy().getCurrentBaseStore(), null);
							if (isCSVData || !containsOrder(orderList, products))
							{
								baseStore.setSubmitOrderProcessCode("order-process");
								getModelService().save(baseStore);
								LOG.info("Creating order for [" + customerUid + "] for site [" + siteUid + "]");

								// Remove any existing cart
								getCartService().removeSessionCart();

								// Populate cart
								populateCart(products, storeId);

								// Begin checkout

								final AddressModel defaultAddress = getCustomerAccountService().getDefaultAddress(customer);
								// Add an address to the address-book, set as the delivery address

								AddressModel addressModel = null;

								if (null != defaultAddress)
								{
									addressModel = defaultAddress;
								}
								else
								{
									addressModel = getModelService().create(AddressModel.class);
								}

								getAddressReversePopulator().populate(deliveryAddress, addressModel);

								if (null == defaultAddress)
								{
									getCustomerAccountService().saveAddressEntry(customer, addressModel);
								}

								final List<CreditCardPaymentInfoModel> cards = getCustomerAccountService()
										.getCreditCardPaymentInfos(customer, true);
								if (cards.isEmpty())
								{
									createPaymentInfo(customer.getCustomerID(), "USD", createVisaCardInfo(), createUkBillingInfo());
								}

								final CartModel sessionCart = getCartService().getSessionCart();
								final CommerceCheckoutParameter parameter = new CommerceCheckoutParameter();
								parameter.setEnableHooks(true);
								parameter.setCart(sessionCart);
								parameter.setAddress(addressModel);
								parameter.setIsDeliveryAddress(false);

								checkAddressErrors(sessionCart, parameter);

								// Set delivery mode
								getCheckoutFacade().setDeliveryModeIfAvailable();

								// Set payment info
								getCheckoutFacade().setPaymentInfoIfAvailable();

								// Checkout
								getCheckoutFacade().authorizePayment("123");
								if (!isCSVData)
								{
									placeOrder();
								}
								else
								{
									if (sessionCart != null)
									{
										if (sessionCart.getUser().equals(customer) || getUserService().isAnonymousUser(customer))
										{
											final CommerceCheckoutParameter checkoutParameter = new CommerceCheckoutParameter();
											checkoutParameter.setEnableHooks(true);
											checkoutParameter.setCart(sessionCart);
											checkoutParameter.setSalesApplication(SalesApplication.WEB);

											final CommerceOrderResult commerceOrderResult = getCommerceCheckoutService()
													.placeOrder(checkoutParameter);
											orderModel = commerceOrderResult.getOrder();
										}
									}
									if (orderModel != null)
									{
										// Remove cart
										getCartService().removeSessionCart();
										getModelService().refresh(orderModel);
									}

									// as the process will exit immediately that we finish initialising.
									try
									{
										Thread.sleep(10000);
									}
									catch (final InterruptedException e)
									{
										LOG.error(e);
									}
								}
							}

						}
						catch (final Exception e)
						{
							LOG.error("Exception in createSampleOrder", e);
						}
						finally
						{
							baseStore.setPaymentProvider(originalPaymentProvider);
							baseStore.setSubmitOrderProcessCode(submitOrderProcessCode);
							getModelService().save(baseStore);
						}
						return orderModel;
					}
				});
		return orderModel;

	}

	protected void checkOrderData(final OrderData orderData, final String message)
	{
		if (orderData == null)
		{
			LOG.error("Failed to placeOrder");
		}
		else
		{
			LOG.info("Created order [" + orderData.getCode() + "]");

			// Sleep for 5s to allow the fulfilment processes to run for this order
			// Only have to worry about this here if we are running initialize from ant
			// as the process will exit immediately that we finish initialising.
			try
			{
				Thread.sleep(10000);
			}
			catch (final InterruptedException e)
			{
				LOG.error(message, e);
			}
		}
	}

	protected void checkAddressErrors(final CartModel sessionCart, final CommerceCheckoutParameter parameter)
	{
		if (!getCommerceCheckoutService().setDeliveryAddress(parameter))
		{
			LOG.error("Failed to set delivery address on cart");
		}

		if (sessionCart.getDeliveryAddress() == null)
		{
			LOG.error("Failed to set delivery address");
		}
	}

	protected void placeOrder()
	{
		try
		{
			final OrderData orderData = getCheckoutFacade().placeOrder();
			checkOrderData(orderData, "Exception during sleep in order to allow the fulfilment processes to run for this order");
		}
		catch (final InvalidCartException e)
		{
			LOG.error("Exception during placing order", e);
		}
	}

	protected void populateCart(final Map<String, Long> products, final String storeId)
	{
		for (final Map.Entry<String, Long> productEntry : products.entrySet())
		{
			try
			{
				getCartFacade().addToCart(productEntry.getKey(), productEntry.getValue().longValue(), storeId);
			}
			catch (final CommerceCartModificationException e)
			{
				LOG.error("Exception during adding product with code " + productEntry.getKey() + " to cart", e);
			}
		}
	}

	protected AddressData createUkAddressData()
	{
		final AddressData data = new AddressData();
		data.setTitle("Mr.");
		data.setTitleCode("mr");
		data.setFirstName("John");
		data.setLastName("Doe");

		data.setCompanyName("hybris");
		data.setLine1("137 High Holborn");
		data.setLine2("");
		data.setTown("London");
		data.setPostalCode("WC1V 6PL");

		final CountryData countryData = new CountryData();
		countryData.setIsocode("GB");
		countryData.setName("UK");
		data.setCountry(countryData);

		data.setPhone("+44 (0)20 / 7429 4175");
		data.setEmail("sales@hybris.local");
		data.setShippingAddress(true);
		data.setBillingAddress(true);

		return data;
	}

	protected AddressData createGermanAddressData()
	{
		final AddressData data = new AddressData();
		data.setTitle("Mr.");
		data.setTitleCode("mr");
		data.setFirstName("John");
		data.setLastName("Doe");

		data.setCompanyName("hybris");
		data.setLine1("Nymphenburger Str. 89");
		data.setLine2("");
		data.setTown("Munchen");
		data.setPostalCode("80636");

		final CountryData countryData = new CountryData();
		countryData.setIsocode("DE");
		countryData.setName("Germany");
		data.setCountry(countryData);

		data.setPhone("+49 (0)89 / 890 650");
		data.setEmail("sales@hybris.local");
		data.setShippingAddress(true);
		data.setBillingAddress(true);

		return data;
	}

	protected boolean containsOrder(final List<OrderModel> orderList, final Map<String, Long> products)
	{
		if (orderList != null && !orderList.isEmpty() && products != null)
		{
			for (final OrderModel order : orderList)
			{
				if (matchesOrder(order, products))
				{
					return true;
				}
			}
		}
		return false;
	}

	protected boolean matchesOrder(final OrderModel order, final Map<String, Long> products)
	{
		final Map<String, Long> entryQuantityMap = getEntryQuantityMap(order);
		final Map<String, Long> productsTreeMap = new TreeMap<String, Long>(products);

		return entryQuantityMap.equals(productsTreeMap);
	}

	protected Map<String, Long> getEntryQuantityMap(final OrderModel order)
	{
		final TreeMap<String, Long> result = new TreeMap<String, Long>();

		for (final AbstractOrderEntryModel entry : order.getEntries())
		{
			final ProductModel product = entry.getProduct();
			if (product != null)
			{
				final String productCode = product.getCode();
				if (result.containsKey(productCode))
				{
					final long newQuantity = result.get(productCode).longValue() + entry.getQuantity().longValue();
					result.put(productCode, Long.valueOf(newQuantity));
				}
				else
				{
					result.put(productCode, entry.getQuantity());
				}
			}
		}
		return result;
	}

	public void generateCSVCustomersWithOrdersAndTickets()
	{
		baseSiteService.setCurrentBaseSite(ELECTRONICS_SITE_ID, true);

		createSampleCustomer();
		final Map<String, OrderModel> ordersMap = createSampleOrder();
		createSampleTicket(ordersMap);
	}

	protected void createSampleCustomer()
	{
		final List<String> customers = parseCSVFile("customers.csv");
		final Iterator<TitleModel> titleItr = getCustomerAccountService().getTitles().iterator();
		final Map<String, TitleModel> titleMap = new HashMap();

		while (titleItr.hasNext())
		{
			final TitleModel titleModel = titleItr.next();
			titleMap.put(titleModel.getCode().toLowerCase(), titleModel);
		}

		for (final String customerData : customers)
		{
			final String[] splitData = customerData.split(",");

			final CustomerModel newCustomer = getModelService().create(CustomerModel.class);
			newCustomer.setUid(splitData[0]);
			newCustomer.setName(splitData[2]);
			newCustomer.setTitle(titleMap.get(splitData[1].toLowerCase()));
			newCustomer.setOriginalUid(splitData[0]);

			newCustomer.setSessionLanguage(i18nService.getCurrentLanguage());
			newCustomer.setSessionCurrency(i18nService.getCurrentCurrency());

			try
			{
				getCustomerAccountService().register(newCustomer, "123456");
				createPaymentInfo(splitData[0], "USD", createVisaCardInfo(), createUkBillingInfo());
			}
			catch (final DuplicateUidException e)
			{
				LOG.info("Can't create customer due to " + e);
			}
		}
	}

	protected Map<String, OrderModel> createSampleOrder()
	{
		final Map<String, OrderModel> orderMap = new HashMap();

		final List<String> orders = parseCSVFile("orders.csv");

		for (final String orderData : orders)
		{
			final String[] splitData = orderData.split(",");

			LOG.info("Creating a Order for " + splitData[0]);

			final Map<String, Long> products = new HashMap<String, Long>();
			final String[] productsData = splitData[2].split(";");

			for (final String product : productsData)
			{
				products.put(product, Long.valueOf(1));
			}

			orderMap.put(splitData[0],
					createSampleOrder(ELECTRONICS_SITE_ID, splitData[1], "USD", products, createUkAddressData(), null, true));
		}
		return orderMap;
	}

	protected void createSampleTicket(final Map<String, OrderModel> associatedItems)
	{
		final List<String> tickets = parseCSVFile("tickets.csv");

		for (final String ticketData : tickets)
		{
			final String[] splitData = ticketData.split("(?<!\\\\),");

			LOG.info("Creating a ticket for " + splitData[0]);

			final CsTicketParameter ticketParameters = new CsTicketParameter();

			ticketParameters.setCustomer(userService.getUserForUID(splitData[0]));
			ticketParameters.setCategory(CsTicketCategory.valueOf(splitData[1]));
			ticketParameters.setPriority(CsTicketPriority.valueOf(splitData[2]));
			ticketParameters.setInterventionType(CsInterventionType.valueOf(splitData[3]));
			ticketParameters.setReason(CsEventReason.valueOf(splitData[4]));
			ticketParameters.setHeadline(splitData[5].replaceAll("\\\\,", ","));
			ticketParameters.setCreationNotes(splitData[6].replaceAll("\\\\,", ","));
			if (splitData.length == 8 && !splitData[7].isEmpty() && associatedItems.containsKey(splitData[7]))
			{
				ticketParameters.setAssociatedTo(associatedItems.get(splitData[7]));
			}
			getTicketBusinessService().createTicket(ticketParameters);
		}
	}


	public void createSampleOrdersForCustomer(final String customerUID)
	{
		// Create sample order in the electronics site
		{
			final Map<String, Long> products = new HashMap<String, Long>();
			products.put("872912", Long.valueOf(1)); // Secure Digital Card 2GB
			products.put("479956", Long.valueOf(1)); // 4GB Memory Stick Pro Duo + adapter
			createSampleOrder(ELECTRONICS_SITE_ID, customerUID + "@domain.net", "USD", products, createUkAddressData(), null, false);
		}
	}

	protected List<String> parseCSVFile(final String csvFile)
	{
		final List<String> results = new ArrayList();

		try (final BufferedReader br = new BufferedReader(
				new FileReader(new File(getClass().getClassLoader().getResource(csvFile).getFile()))))
		{
			String line = "";
			//bypass first line
			br.readLine(); // NOSONAR
			while ((line = br.readLine()) != null)
			{
				results.add(line);
			}
		}
		catch (final IOException e)
		{
			LOG.error("Failed to parse CSV file", e);
		}

		return results;
	}

	protected CommonI18NService getI18nService()
	{
		return i18nService;
	}

	@Required
	public void setI18nService(final CommonI18NService i18nService)
	{
		this.i18nService = i18nService;
	}
}
