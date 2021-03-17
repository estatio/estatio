package org.estatio.module.event.dom;

import org.junit.Test;

import org.isisaddons.module.fakedata.dom.FakeDataService;

import static org.assertj.core.api.Assertions.assertThat;

public class Event_Test {

    private FakeDataService fakeDataService = new FakeDataService(){{
        init();
    }};

    @Test
    public void changeNotes() throws Exception {

        // given
        final Event event = new Event();

        // when
        final String newNotes = fakeDataService.lorem().paragraph();
        event.changeNotes(newNotes);

        // then
        assertThat(event.getNotes()).isEqualTo(newNotes);
    }

}