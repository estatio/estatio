package org.incode.module.base.dom;

import org.junit.Test;

import org.apache.isis.applib.value.Blob;

import static org.assertj.core.api.Assertions.assertThat;

public class MimeTypeData_newBlob_Test {

    @Test
    public void when_provided_name_ends_with_correct_suffix() throws Exception {
        // given
        final MimeTypeData mimeType = MimeTypeData.APPLICATION_PDF;
        final byte[] bytes = new byte[0];

        // when
        final Blob blob = mimeType.newBlob("foo.pdf", bytes);

        // then
        assertThat(blob.getName()).isEqualTo("foo.pdf");
        assertThat(blob.getBytes()).isSameAs(bytes);
    }

    @Test
    public void when_provided_name_does_not_end_with_correct_suffix() throws Exception {
        // given
        final MimeTypeData mimeType = MimeTypeData.APPLICATION_PDF;
        final byte[] bytes = new byte[0];

        // when
        final Blob blob = mimeType.newBlob("foo.docx", bytes);

        // then
        assertThat(blob.getName()).isEqualTo("foo.docx.pdf");
        assertThat(blob.getBytes()).isSameAs(bytes);
    }

}