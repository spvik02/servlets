package org.clevertec.providers;

import org.clevertec.models.Receipt;

public interface ReceiptProvider extends Provider<Receipt> {
    Receipt find(int id);
}
