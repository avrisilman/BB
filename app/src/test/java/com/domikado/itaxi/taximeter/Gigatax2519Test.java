package com.domikado.itaxi.taximeter;

import com.domikado.itaxi.data.taximeter.Gigatax2519;
import com.domikado.itaxi.events.EventHire;
import com.domikado.itaxi.events.EventStop;
import com.domikado.itaxi.events.EventVacant;
import com.domikado.itaxi.utils.HexData;

import org.greenrobot.eventbus.EventBus;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class Gigatax2519Test {

    @Mock
    EventBus eventBus;

    @InjectMocks
    Gigatax2519 gigatax;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void validHiredCommand() {
        ArgumentCaptor<EventHire> captor = ArgumentCaptor.forClass(EventHire.class);
        gigatax.call(HexData.stringToBytes("8001404911404911101600000000650000000040"));
        verify(eventBus).post(captor.capture());

        assertThat(captor.getValue().getClass(), is(equalTo(EventHire.class)));
    }

    @Test
    public void validVacantCommand() {
        ArgumentCaptor<EventVacant> captor = ArgumentCaptor.forClass(EventVacant.class);
        gigatax.call(HexData.stringToBytes("800040491140491110160000000065000000003F"));
        verify(eventBus).post(captor.capture());

        assertThat(captor.getValue().getClass(), is(equalTo(EventVacant.class)));
    }

    @Test
    public void validStopCommand() {
        ArgumentCaptor<EventStop> captor = ArgumentCaptor.forClass(EventStop.class);
        gigatax.call(HexData.stringToBytes("8002404911404911101600000000650000000041"));
        verify(eventBus).post(captor.capture());

        assertThat(captor.getValue().getClass(), is(equalTo(EventStop.class)));
        assertThat(captor.getValue().getFare(), is(equalTo(new BigDecimal(6500))));
    }

    @Test
    public void unknownCommand() {
        gigatax.call(HexData.stringToBytes("8007404911404911101600000000650000000040"));
        verifyNoMoreInteractions(eventBus);
    }

    @Test
    public void noise() {
        ArgumentCaptor<EventHire> captor = ArgumentCaptor.forClass(EventHire.class);
        gigatax.call(HexData.stringToBytes("013642")); // noise
        verifyNoMoreInteractions(eventBus);

        gigatax.call(HexData.stringToBytes("005E")); // noise
        verifyNoMoreInteractions(eventBus);

        gigatax.call(HexData.stringToBytes("AA093642")); // invalid data
        verifyNoMoreInteractions(eventBus);

        gigatax.call(HexData.stringToBytes("005E")); // noise
        verifyNoMoreInteractions(eventBus);

        gigatax.call(HexData.stringToBytes("8007404911404911101600000000650000000040"));  // invalid data
        verifyNoMoreInteractions(eventBus);

        gigatax.call(HexData.stringToBytes("AA002150")); // noise
        verifyNoMoreInteractions(eventBus);

        gigatax.call(HexData.stringToBytes("00")); // noise
        verifyNoMoreInteractions(eventBus);

        gigatax.call(HexData.stringToBytes("2949502911160000000065000000000001006500100464000070"));  // invalid data
        verifyNoMoreInteractions(eventBus);

        gigatax.call(HexData.stringToBytes("AA002150"));  // noise
        verifyNoMoreInteractions(eventBus);

        gigatax.call(HexData.stringToBytes("8001404911404911101600000000"));  // valid data
        gigatax.call(HexData.stringToBytes("650000000040"));  // valid data
        verify(eventBus).post(captor.capture());

        assertThat(captor.getValue().getClass(), is(equalTo(EventHire.class)));
    }
}
