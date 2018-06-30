package com.crossover.techtrial.service;


import com.crossover.techtrial.model.Panel;
import com.crossover.techtrial.repository.PanelRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
public class PanelServiceImplTest {

    private static final String ANY_SERIAL = "1234567890123456";

    private Panel anyPanel;

    @MockBean
    private PanelRepository panelRepositoryMock;

    private PanelService underTest;

    @Before
    public void setUp() {
        underTest = new PanelServiceImpl(panelRepositoryMock);
        anyPanel = makeAnyPanel();
    }

    @Test
    public void shouldRegisterPanel() {
        underTest.register(anyPanel);
        verify(panelRepositoryMock, times(1)).save(anyPanel);
    }

    @Test
    public void shouldFindBySerial() {
        given(panelRepositoryMock.findBySerial(ANY_SERIAL)).willReturn(anyPanel);
        Panel actualPanel = underTest.findBySerial(ANY_SERIAL);
        assertEquals(anyPanel, actualPanel);
    }

    private Panel makeAnyPanel() {
       return Panel.builder()
            .serial(ANY_SERIAL)
            .brand("anyBrand")
            .latitude(12.123456)
            .longitude(23.234567)
            .build();
    }
}
