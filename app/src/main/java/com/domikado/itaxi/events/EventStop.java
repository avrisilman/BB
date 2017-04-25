package com.domikado.itaxi.events;

import java.math.BigDecimal;

public class EventStop {

    private final BigDecimal fare;

    public EventStop(BigDecimal fare) {
        this.fare = fare;
    }

    public BigDecimal getFare() {
        return fare;
    }
}
