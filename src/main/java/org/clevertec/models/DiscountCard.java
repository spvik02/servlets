package org.clevertec.models;

import java.time.LocalDate;

public interface DiscountCard {

    double getPercentageOfDiscount();
    int getId();
    LocalDate getDateOfRegistration();
}
