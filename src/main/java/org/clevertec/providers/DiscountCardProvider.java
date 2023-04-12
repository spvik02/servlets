package org.clevertec.providers;

import org.clevertec.models.DiscountCard;

public interface DiscountCardProvider extends Provider<DiscountCard> {

    DiscountCard find(int id);
}
