ACC.common.cart = {

	bindAll: function ()
	{
		this.bindMultidCartProduct();
	},
	bindMultidCartProduct: function ()
	{

		$(document).on("click",'.submitRemoveProductMultiD', function ()
				{	
					var itemIndex = $(this).data("index");
					
					var $form = $('#updateCartForm' + itemIndex);
					var initialCartQuantity = $form.find('input[name=initialQuantity]');
					var cartQuantity = $form.find('input[name=quantity]');
					var entryNumber = $form.find('input[name=entryNumber]').val(); 
					var productCode = $form.find('input[name=productCode]').val(); 
					
					cartQuantity.val(0);
					initialCartQuantity.val(0);
							
					ACC.track.trackRemoveFromCart(productCode, initialCartQuantity, cartQuantity.val());
						
					var method = $form.attr("method") ? $form.attr("method").toUpperCase() : "GET";
					$.ajax({
						url: $form.attr("action"),
						data: $form.serialize(),
						type: method,
						success: function(data) 
						{
							location.reload();
						},
						error: function() 
						{
							alert("Failed to remove quantity. Error details [" + xht + ", " + textStatus + ", " + ex + "]");
						}

					});
				
				});
		
		
		// link to display the multi-d grid in non read-only mode
	    $(document).on("click",'.updateQuantityProduct', function (event){
	        ACC.common.cart.populateAndShowGrid(this, event, false);
		});
	    
	    // link to display the multi-d grid in read-only mode
        $(document).on("click",'.showQuantityProduct', function (event){
            ACC.common.cart.populateAndShowGrid(this, event, true);
        });
        
        // link to display the multi-d grid in read-only mode
        $(document).on("click",'.showQuantityProductOverlay', function (event){
            ACC.common.cart.populateAndShowCheckoutGridOverlay(this, event);
        });
	    
	},
        
	populateAndShowGrid: function(element, event, readOnly)
    {
        event.preventDefault();

        var itemIndex = $(element).data("index");
        
        var gridEntries = $('#grid' + itemIndex);
        
        grid = $("#ajaxGrid" + itemIndex);
        
        if (!grid.is(":hidden")) {
        	grid.slideUp();
        	$("#QuantityProductToggle" + itemIndex).html("+");
        	return;
        }
      
		grid.slideDown("slow");
		$("#QuantityProductToggle" + itemIndex).html("-");

		if(grid.html() != ""){
			return;
		}
    		        
        var strSubEntries = gridEntries.data("sub-entries");
        var arrSubEntries= strSubEntries.split(',');
        var firstVariantCode = arrSubEntries[0].split(':')[0];

        var mapCodeQuantity = new Object();
        for (var i = 0; i < arrSubEntries.length; i++)
        {
            var arrValue = arrSubEntries[i].split(":");
            mapCodeQuantity[arrValue[0]] = arrValue[1];
        }

        var targetUrl = ACC.config.contextPath;

        if (readOnly === false)
        {
            targetUrl = targetUrl + '/cart/getProductVariantMatrix';
        }
        else
        {
            targetUrl = targetUrl + "/checkout/multi/getProductVariantMatrix";
        }

        var method = "GET";
        $.ajax({
            url: targetUrl,
            data: {productCode: firstVariantCode},
            type: method,
            success: function(data)
            {
                grid.html(data);
                
                if (grid.find("div[id='"+firstVariantCode+"']").val() === undefined){
                	location.reload();
                }

                var $gridContainer = grid.find(".product-grid-container");
                var numGrids = $gridContainer.length;

                for (var i = 0; i < numGrids; i++)
                {
                    ACC.common.cart.getProductQuantity($gridContainer.eq(i), mapCodeQuantity);
                }

                ACC.common.cart.coreTableActions(itemIndex, mapCodeQuantity);
                $("#ajaxGrid").removeAttr('id');

                grid.slideDown("slow");
            },
            error: function(xht, textStatus, ex)
            {
                alert("Failed to get variant matrix. Error details [" + xht + ", " + textStatus + ", " + ex + "]");
            }

        });
    },
    
    populateAndShowCheckoutGridOverlay: function(element, event )
    {
        event.preventDefault();

        var itemIndex = $(element).data("index");
        var gridEntries = $('#grid' + itemIndex);

        var strSubEntries = gridEntries.data("sub-entries");
        var arrSubEntries= strSubEntries.split(',');
        var firstVariantCode = arrSubEntries[0].split(':')[0];

        var targetUrl = ACC.config.contextPath + '/checkout/multi/getProductVariantMatrix?productCode=' + firstVariantCode;

        var mapCodeQuantity = new Object();
        for (var i = 0; i < arrSubEntries.length; i++)
        {
            var arrValue = arrSubEntries[i].split(":");
            mapCodeQuantity[arrValue[0]] = arrValue[1];
        }


        $.colorbox({
            href:       targetUrl,
            width:      1000,
            height:     250,
            onComplete: function() {
                $.colorbox.resize();

                var grid = $("#ajaxGrid");
                var $gridContainer = grid.find(".product-grid-container");
                var numGrids = $gridContainer.length;
                for (var i = 0; i < numGrids; i++)
                {
                    ACC.common.cart.getProductQuantity($gridContainer.eq(i), mapCodeQuantity);
                }

                ACC.common.cart.coreTableActions(itemIndex, mapCodeQuantity);
            },

        });
    },

    getProductQuantity: function(gridContainer, mapData)
	{
		var skus          = jQuery.map(gridContainer.find("input[type='hidden'].sku"), function(o) {return o.value});
		var quantities    = jQuery.map(gridContainer.find("input[type='textbox'].sku-quantity"), function(o) {return o});
		
		var totalPrice = 0.0;
		var totalQuantity = 0.0;
	
		$.each(skus, function(index, skuId) 
		{ 
			var quantity = mapData[skuId];
			if (quantity != undefined)
			{
				quantities[index].value = quantity;
				totalQuantity += parseFloat(quantity);
				
				var indexPattern = "[0-9]+";
				var currentIndex = parseInt(quantities[index].id.match(indexPattern));
				
				var currentPrice = $("input[id='productPrice["+currentIndex+"]']").val();
				totalPrice += parseFloat(currentPrice) * parseInt(quantity);
			}
		});
		
		var subTotalValue = Currency.formatMoney(Number(totalPrice).toFixed(2), Currency.money_format[ACC.common.currentCurrency]);
		var avgPriceValue = 0.0;
		if (totalQuantity > 0)
		{
			avgPriceValue = Currency.formatMoney(Number(totalPrice/totalQuantity).toFixed(2), Currency.money_format[ACC.common.currentCurrency]);
		}

		gridContainer.parent().find('#quantity').html(totalQuantity);
		gridContainer.parent().find("#avgPrice").html(avgPriceValue);
		gridContainer.parent().find("#subtotal").html(subTotalValue);
		
		var $inputQuantityValue = gridContainer.parent().find('#quantityValue');
		var $inputAvgPriceValue = gridContainer.parent().find('#avgPriceValue');
		var $inputSubtotalValue = gridContainer.parent().find('#subtotalValue');

		$inputQuantityValue.val(totalQuantity);
		$inputAvgPriceValue.val(Number(totalPrice/totalQuantity).toFixed(2));
		$inputSubtotalValue.val(Number(totalPrice).toFixed(2));
		
	}, 
	
	coreTableActions: function(itemIndex, mapCodeQuantity)  
	{
        var skuQuantityClass = '.sku-quantity';

		var quantityBefore = 0;
		var quantityAfter = 0;

		var grid = $('#ajaxGrid' + itemIndex);
		
		grid.on('click', skuQuantityClass, function(event) {
            $(this).select();
        });

        grid.on('focusin', skuQuantityClass, function(event) {
            quantityBefore = jQuery.trim(this.value);
            if (quantityBefore == "") {
                quantityBefore = 0;
                this.value = 0;
            }
        });

        grid.on('focusout', skuQuantityClass, function(event) {
            var indexPattern           = "[0-9]+";
            var currentIndex           = parseInt($(this).attr("id").match(indexPattern));
            var $gridGroup             = $(this).parents('.orderForm_grid_group');
            var $closestQuantityValue  = $gridGroup.find('#quantityValue');
            var $closestAvgPriceValue  = $gridGroup.find('#avgPriceValue');
            var $closestSubtotalValue  = $gridGroup.find('#subtotalValue');
            
            var currentQuantityValue   = $closestQuantityValue.val();
            var currentSubtotalValue   = $closestSubtotalValue.val();

            var currentPrice = $("input[id='productPrice["+currentIndex+"]']").val();
            var variantCode = $("input[id='cartEntries["+currentIndex+"].sku']").val();

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
            } else {
                $closestQuantityValue.val(parseInt(currentQuantityValue) + (parseInt(quantityAfter) - parseInt(quantityBefore)));
                $closestSubtotalValue.val(parseFloat(currentSubtotalValue) + parseFloat(currentPrice) * (parseInt(quantityAfter) - parseInt(quantityBefore)));
            }

            if (parseInt($closestQuantityValue.val()) > 0) {
                $closestAvgPriceValue.val(parseFloat($closestSubtotalValue.val()) / parseInt($closestQuantityValue.val()));
            } else {
                $closestAvgPriceValue.val(0);
            }

            $closestQuantityValue.parent().find('#quantity').html($closestQuantityValue.val());
            $closestAvgPriceValue.parent().find('#avgPrice').html(ACC.productorderform.formatTotalsCurrency($closestAvgPriceValue.val()));
            $closestSubtotalValue.parent().find('#subtotal').html(ACC.productorderform.formatTotalsCurrency($closestSubtotalValue.val()));
            
            if (quantityBefore != quantityAfter)
            {
            	var method = "POST";
            	$.ajax({
            		url: ACC.config.contextPath + '/cart/updateMultiD',
            		data: {productCode: variantCode, quantity: quantityAfter, entryNumber: -1},
					type: method,
					success: function(data, textStatus, xhr) 
					{
						ACC.common.cart.refreshCartData(data, -1, null, itemIndex);
						mapCodeQuantity[variantCode] = quantityAfter;
					},
					error: function(xhr, textStatus, error) 
					{
						var redirectUrl = xhr.getResponseHeader("redirectUrl");
						var connection = xhr.getResponseHeader("Connection");
						// check if error leads to a redirect
						if (redirectUrl !== null) {
							window.location = redirectUrl;
						// check if error is caused by a closed connection
						} else if (connection === "close") {
							window.location.reload();
						}
					}
				
				});
            }

        }); 

	},
	
	refreshCartData: function(cartData, entryNum, quantity, itemIndex) 
	{
		// if cart is empty, we need to reload the whole page
		if (cartData.entries.length == 0)
		{
			location.reload();
		}
		else
		{
			var form;	
			var removeItem = false;
		
			if (entryNum == -1) // grouped item
			{
				var editLink = $('#QuantityProduct' + itemIndex);
				form = editLink.closest('form');
				var productCode = form.find('input[name=productCode]').val(); 
			
				var quantity = 0;
				var entryPrice = 0;
				for (var i = 0; i < cartData.entries.length; i++)
				{
					var entry = cartData.entries[i];
					if (entry.product.code == productCode)
					{			
						quantity = entry.quantity;
						entryPrice = entry.totalPrice;
						break;
					}
				}

				if (quantity == 0)
				{
					removeItem = true;
					form.parent().parent().remove();
				}
				else
				{
					form.find(".qty").html(quantity);
					form.parent().parent().find(".total").html(entryPrice.formattedValue);
				}
			
			}
			else //ungrouped item
			{
				form = $('#updateCartForm' + itemIndex);
		
				if (quantity == 0)
				{
					removeItem = true;
					form.parent().parent().remove();
				}
				else
				{
					for (var i = 0; i < cartData.entries.length; i++)
					{
						var entry = cartData.entries[i];
						if (entry.entryNumber == entryNum)
						{				
							form.find('input[name=quantity]').val(entry.quantity);
							form.parent().parent().find(".total").html(entry.totalPrice.formattedValue);
						}
					}
				}
			}
			
			// remove item, need to update other items' entry numbers
			if (removeItem === true)
			{
				$('.cartItem').each(function(index)
				{
					form = $(this).find('.quantity').children().first();
					var productCode = form.find('input[name=productCode]').val(); 

					for (var i = 0; i < cartData.entries.length; i++)
					{
						var entry = cartData.entries[i];
						if (entry.product.code == productCode)
						{				
							form.find('input[name=entryNumber]').val(entry.entryNumber);
							form.attr('id','updateCartForm' + entry.entryNumber);
							form.find('input[name=quantity]').attr('id','quantity' + entry.entryNumber);
							form.find('label[class=skip]').attr('for','quantity' + entry.entryNumber);
							break;
						}
					}
				});
			}
			// refresh mini cart 	
			ACC.minicart.refreshMiniCartCount();
			$('#orderTotals').next().remove();
			$('#orderTotals').remove();
			$('#ajaxCart').html($("#cartTotalsTemplate").tmpl(cartData));
		}
	}
}


$(document).ready(function ()
{
	ACC.common.cart.bindAll();
});
