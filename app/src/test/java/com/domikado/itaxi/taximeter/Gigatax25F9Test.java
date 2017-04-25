package com.domikado.itaxi.taximeter;

import com.domikado.itaxi.data.taximeter.Gigatax25F9;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class Gigatax25F9Test {

    @Mock
    EventBus eventBus;

    @InjectMocks
    Gigatax25F9 gigatax;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void validVacantCommand() {
        ArgumentCaptor<EventVacant> captor = ArgumentCaptor.forClass(EventVacant.class);
        gigatax.call(HexData.stringToBytes("AA0021502949502911160000000065000000000001006500100464000070"));
        verify(eventBus).post(captor.capture());

        assertThat(captor.getValue().getClass(), is(equalTo(EventVacant.class)));
    }

    @Test
    public void validHiredCommand() {
        ArgumentCaptor<EventHire> captor = ArgumentCaptor.forClass(EventHire.class);
        gigatax.call(HexData.stringToBytes("AA01525029525029111600000000650000000000010065001004640000AB"));
        verify(eventBus).post(captor.capture());

        assertThat(captor.getValue().getClass(), is(equalTo(EventHire.class)));
    }

    @Test
    public void validStopCommand() {
        ArgumentCaptor<EventStop> captor = ArgumentCaptor.forClass(EventStop.class);
        gigatax.call(HexData.stringToBytes("AA022150294750291116000000006500000000000100650010046300006F"));
        verify(eventBus).post(captor.capture());

        assertThat(captor.getValue().getClass(), is(equalTo(EventStop.class)));
        assertThat(captor.getValue().getFare(), is(equalTo(new BigDecimal(6500))));
    }

    @Test
    public void validStopCommandFareMillion() {
        ArgumentCaptor<EventStop> captor = ArgumentCaptor.forClass(EventStop.class);
        gigatax.call(HexData.stringToBytes("AA022150294750291116000000005012010000000100650010046300006D"));
        verify(eventBus).post(captor.capture());

        assertThat(captor.getValue().getClass(), is(equalTo(EventStop.class)));
        assertThat(captor.getValue().getFare(), is(equalTo(new BigDecimal(1125000))));
    }

    @Test
    public void unknownCommand() {
        gigatax.call(HexData.stringToBytes("AA043642123642121016000000006500000000000400650010049500005F"));
        verifyNoMoreInteractions(eventBus);
    }

    @Test
    public void noise() {
        ArgumentCaptor<EventVacant> captor = ArgumentCaptor.forClass(EventVacant.class);
        gigatax.call(HexData.stringToBytes("013642")); // noise
        verifyNoMoreInteractions(eventBus);

        gigatax.call(HexData.stringToBytes("005E")); // noise
        verifyNoMoreInteractions(eventBus);

        gigatax.call(HexData.stringToBytes("AA093642")); // invalid data
        verifyNoMoreInteractions(eventBus);

        gigatax.call(HexData.stringToBytes("005E")); // noise
        verifyNoMoreInteractions(eventBus);

        gigatax.call(HexData.stringToBytes("123642121016000000006500000000000400650010049500"));  // invalid data
        verifyNoMoreInteractions(eventBus);

        gigatax.call(HexData.stringToBytes("AA002150")); // valid data
        verifyNoMoreInteractions(eventBus);

        gigatax.call(HexData.stringToBytes("00")); // noise
        verifyNoMoreInteractions(eventBus);

        gigatax.call(HexData.stringToBytes("2949502911160000000065000000000001006500100464000070"));  // valid data
        verifyNoMoreInteractions(eventBus);

        gigatax.call(HexData.stringToBytes("AA002150"));  // valid data
        gigatax.call(HexData.stringToBytes("2949502911160000000065000000000001006500100464000070"));  // valid data
        gigatax.call(HexData.stringToBytes("00")); // noise
        gigatax.call(HexData.stringToBytes("AA002150"));  // valid data
        gigatax.call(HexData.stringToBytes("2949502911160000000065000000000001006500100464000070"));  // valid data
        verify(eventBus, times(2)).post(captor.capture());

        assertThat(captor.getValue().getClass(), is(equalTo(EventVacant.class)));
    }
}
