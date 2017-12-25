ACC.productorderform = {

	$addToCartOrderForm:        $("#AddToCartOrderForm"),
	$omsErrorMessageContainer:  $("#globalMessages"),
	$emptySkuQuantityInputs:    $(".sku-quantity[value]"),
    $nonEmptySkuQuantityInputs: $(".sku-quantity[value]"),

	// Templates
	$futureTooltipTemplate:      $("#future-tooltip-template"),
	$futureTooltipErrorTemplate: $("#future-tooltip-error-template"),
	$omsErrorMessageTemplate:    $("#oms-error-message-template"),
  
	coreTableActions: function()  {
        var skuQuantityClass = '.sku-quantity';

		var quantityBefore = 0;
		var quantityAfter = 0;

		ACC.productorderform.$addToCartOrderForm.on('click', skuQuantityClass, function(event) {
            $(this).select();
        });

        ACC.productorderform.$addToCartOrderForm.on('focusin', skuQuantityClass, function(event) {
            quantityBefore = jQuery.trim(this.value);
            if (quantityBefore == "") {
                quantityBefore = 0;
                this.value = 0;
            }
        });

        ACC.productorderform.$addToCartOrderForm.on('focusout', skuQuantityClass, function(event) {
            var indexPattern           = "[0-9]+";
            var currentIndex           = parseInt($(this).attr("id").match(indexPattern));
            var $gridGroup             = $(this).parents('.orderForm_grid_group');
            var $closestQuantityValue  = $gridGroup.find('#quantityValue');
            var $closestAvgPriceValue  = $gridGroup.find('#avgPriceValue');
            var $closestSubtotalValue  = $gridGroup.find('#subtotalValue');
            var $currentTotalItems     = $('#total-items-count');
            var currentTotalItemsValue = $currentTotalItems.html();
            var currentTotalPrice      = $('#total-price-value').val();
            var currentQuantityValue   = $closestQuantityValue.val();
            var currentSubtotalValue   = $closestSubtotalValue.val();

            var totalPrice = 0;
            var currentPrice = $("input[id='productPrice["+currentIndex+"]']").val();

            quantityAfter = jQuery.trim(this.value);

            if (isNaN(jQuery.trim(this.value))) {
                this.value = 0;
            }

            if (quantityAfter == "") {
                quantityAfter = 0;
                this.value = 0;
            }

            if (quantityBefore == 0) {
                $closestQuantityValue.val(parseInt(currentQuantityValue) + parseInt(quantityAfter));
                $closestSubtotalValue.val(parseFloat(currentSubtotalValue) + parseFloat(currentPrice) * parseInt(quantityAfter));

                $currentTotalItems.html(parseInt(currentTotalItemsValue) + parseInt(quantityAfter));
                totalPrice = parseFloat(currentTotalPrice) + parseFloat(currentPrice) * parseInt(quantityAfter);
            } else {
                $closestQuantityValue.val(parseInt(currentQuantityValue) + (parseInt(quantityAfter) - parseInt(quantityBefore)));
                $closestSubtotalValue.val(parseFloat(currentSubtotalValue) + parseFloat(currentPrice) * (parseInt(quantityAfter) - parseInt(quantityBefore)));

                $currentTotalItems.html(parseInt(currentTotalItemsValue) + (parseInt(quantityAfter) - parseInt(quantityBefore)));
                totalPrice = parseFloat(currentTotalPrice) + parseFloat(currentPrice) * (parseInt(quantityAfter) - parseInt(quantityBefore));
            }
            
            // if there are no items to add, disable addToCartBtn, otherwise, enable it
            if ($('#total-items-count').text() == 0) {
                $('#addToCartBtn').attr('disabled','disabled');
            } else {
            	$('#addToCartBtn').removeAttr('disabled');
            }

            $('#total-price').html(ACC.productorderform.formatTotalsCurrency(totalPrice));
            $('#total-price-value').val(totalPrice);

            if (parseInt($closestQuantityValue.val()) > 0) {
                $closestAvgPriceValue.val(parseFloat($closestSubtotalValue.val()) / parseInt($closestQuantityValue.val()));
            } else {
                $closestAvgPriceValue.val(0);
            }

            $closestQuantityValue.parent().find('#quantity').html($closestQuantityValue.val());
            $closestAvgPriceValue.parent().find('#avgPrice').html(ACC.productorderform.formatTotalsCurrency($closestAvgPriceValue.val()));
            $closestSubtotalValue.parent().find('#subtotal').html(ACC.productorderform.formatTotalsCurrency($closestSubtotalValue.val()));

        });

	},

	bindUpdateFutureStockButton: function(updateFutureStockButton) {
		updateFutureStockButton.live("click", function(event) {
			event.preventDefault();

			var $gridContainer = $(this).parent().parent().find(".product-grid-container");
			var $skus          = jQuery.map($gridContainer.find("input[type='hidden'].sku"), function(o) {return o.value});
			var skusId         = $(this).data('skusId');
			var futureStockUrl = $(this).data('skusFutureStockUrl');
			var postData       = {skus: $skus, productCode: skusId};

			ACC.common.showSpinnerById(skusId);
			
			$.ajax({
				url:         futureStockUrl,
				type:        'POST',
				data:        postData,
				traditional: true,
				dataType:    'json',
				success:     function(data) { ACC.productorderform.updateFuture($gridContainer, $skus, data, skusId)},
				error:       function(xht, textStatus, ex) {
					ACC.common.hideSpinnerById(skusId);
					alert("Failed to get delivery modes. Error details [" + xht + ", " + textStatus + ", " + ex + "]");
				}
			});
		});
	},

	bindExpandGridButton: function(expandGridButton) {
		expandGridButton.click(function(event) {
			event.preventDefault();

			$.colorbox({
				html:      ACC.productorderform.$addToCartOrderForm.clone(true),
				scroll:    true,
				width:     "98%",
				height:    "98%",
				onCleanup: function() { ACC.productorderform.syncGrid("#cboxContent", "form#AddToCartOrderForm") }
			});
		});
	},

	updateFuture: function(gridContainer, skus, freshData, callerId) {
		// clear prior error messages
		ACC.productorderform.$omsErrorMessageContainer.find("div").remove();

		if (freshData !== null && typeof freshData['basket.page.viewFuture.unavailable'] !== 'undefined') { 
			// future stock service is not available
			$.tmpl(ACC.productorderform.$omsErrorMessageTemplate, {
				errorMessage:  freshData['basket.page.viewFuture.unavailable']
			}).appendTo(ACC.productorderform.$omsErrorMessageContainer);
		}
		else {
			$.each(skus, function(index, skuId) {
				var stocks = freshData[skuId];

				var cell               = gridContainer.find("[data-sku-id='" + skuId + "']");
				var isCurrentlyInStock = cell[0].attributes['class'].nodeValue.indexOf("in-stock") != -1;
				var futureStockPresent = typeof stocks !== 'undefined' && stocks !== null && stocks[0] !== null && typeof stocks[0] !== 'undefined';

				cell.children(".future_tooltip, .out-of-stock, .future-stock").remove(); // remove previous tool tips

				if (futureStockPresent) {
					// we have stock for this product
					if (!isCurrentlyInStock) { cell.addClass("future-stock"); }

					// render template and append to cell
					$.tmpl(ACC.productorderform.$futureTooltipTemplate, {
						deliverMessage: ACC.productorderform.$addToCartOrderForm.data("gridFutureTooltipHeadingDelivery"),
						qtyMessage:     ACC.productorderform.$addToCartOrderForm.data("gridFutureTooltipHeadingQty"),
						formattedDate:  stocks[0].formattedDate,
						availabilities: stocks
					}).appendTo(cell);

				} else {
					// no future stock for this product
					if (!isCurrentlyInStock) {
						cell[0].attributes['class'].nodeValue = "td_stock out-of-stock";
					}
				}
			});
		}
		ACC.common.hideSpinnerById(callerId);
	},

	syncGrid: function(sourceContainer, targetContainer) {
		var $allSkus = $(sourceContainer + " .sku-quantity");

		$.each($allSkus, function(index, sku) {
			var selectorSuffix     = " input[name='" + sku.name + "'].sku-quantity";
			var $skuQuantitySource = $(sourceContainer + selectorSuffix);
			var $skuQuantityTarget = $(targetContainer + selectorSuffix);

			$skuQuantityTarget.val(sku.value);

			ACC.productorderform.syncTotalsBySku($skuQuantitySource, $skuQuantityTarget);
		});
	},

	toJSON: function(gridForm, skipZeroQuantity) {
		var skus          = gridForm.find("input.sku").map(function(index, element) {return element.value}),
			skuQuantities = gridForm.find("input.sku-quantity").map(function(index, element) {return parseInt(element.value)}),
			skusAsJSON      = [];

		for (var i = 0; i < skus.length; i++) {
			if (!(skipZeroQuantity && skuQuantities[i] === 0)) {
				skusAsJSON.push({"product": { "code": skus[i] }, "quantity": skuQuantities[i]});
			}
		}

		return JSON.stringify({"cartEntries": skusAsJSON});
	},

	syncTotalsBySku: function(skuQuantitySource, skuQuantityTarget) {

		var $sourceQuantityValue = $(skuQuantitySource).closest('.orderForm_grid_group').find('#quantityValue');
		var $sourceAvgPriceValue = $(skuQuantitySource).closest('.orderForm_grid_group').find('#avgPriceValue');
		var $sourceSubtotalValue = $(skuQuantitySource).closest('.orderForm_grid_group').find('#subtotalValue');

		var $targetQuantityValue = $(skuQuantityTarget).closest('.orderForm_grid_group').find('#quantityValue');
		var $targetAvgPriceValue = $(skuQuantityTarget).closest('.orderForm_grid_group').find('#avgPriceValue');
		var $targetSubtotalValue = $(skuQuantityTarget).closest('.orderForm_grid_group').find('#subtotalValue');

		$targetQuantityValue.parent().find('#quantity').html($sourceQuantityValue.val());
		$targetAvgPriceValue.parent().find('#avgPrice').html(Number($sourceAvgPriceValue.val()).toFixed(2));
		$targetSubtotalValue.parent().find('#subtotal').html(Number($sourceSubtotalValue.val()).toFixed(2));

		$targetQuantityValue.val($sourceQuantityValue.val());
		$targetAvgPriceValue.val($sourceAvgPriceValue.val());
		$targetSubtotalValue.val($sourceSubtotalValue.val());
	},
	
	formatTotalsCurrency: function(amount)  {
		return Currency.formatMoney(Number(amount).toFixed(2), Currency.money_format[ACC.common.currentCurrency]);
	},
	
	cleanValues: function() {
		if ($(".orderForm_grid_group").length !== 0) {
			var formattedTotal = ACC.productorderform.formatTotalsCurrency('0.00');

			ACC.common.$page.find('#avgPrice, #subtotal, #total-price').html(formattedTotal);
			ACC.common.$page.find('#quantity, #total-items-count').html(0);

			ACC.common.$page.find('#quantityValue, #avgPriceValue, #subtotalValue, #total-price-value').val(0);
			ACC.productorderform.$emptySkuQuantityInputs.val(0);
		}
	},

	bindAll: function() {
		ACC.productorderform.coreTableActions();
		ACC.productorderform.bindUpdateFutureStockButton($(".update_future_stock_button"));
		ACC.productorderform.bindExpandGridButton($(".js-expand-grid-button"));
	},

    calculateGrid: function() {
        ACC.productorderform.$nonEmptySkuQuantityInputs.trigger('focusout');
    }
};

$(document).ready(function() {
	ACC.productorderform.bindAll();
	ACC.productorderform.cleanValues();
    ACC.productorderform.calculateGrid();
});