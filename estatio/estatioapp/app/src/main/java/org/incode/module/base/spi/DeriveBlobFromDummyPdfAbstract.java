package org.incode.module.base.spi;

import java.io.IOException;
import java.net.URL;

import javax.inject.Inject;

import com.google.common.io.Resources;

import org.apache.isis.applib.services.bookmark.BookmarkService;
import org.apache.isis.applib.services.command.Command;
import org.apache.isis.applib.services.command.CommandDtoProcessorForActionAbstract;
import org.apache.isis.applib.value.Blob;
import org.apache.isis.schema.cmd.v1.CommandDto;
import org.apache.isis.schema.cmd.v1.ParamDto;
import org.apache.isis.schema.common.v1.ValueType;
import org.apache.isis.schema.utils.CommonDtoUtils;

import org.incode.module.base.dom.MimeTypeData;

public abstract class DeriveBlobFromDummyPdfAbstract
        extends CommandDtoProcessorForActionAbstract {

    static {
        final URL resource = Resources.getResource(DeriveBlobFromDummyPdfAbstract.class, "dummy.pdf");
        final byte[] bytes;
        try {
            bytes = Resources.toByteArray(resource);
            blob = new Blob("dummy.pdf", MimeTypeData.APPLICATION_PDF.asStr(), bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Blob blob;

    private final int paramNum;

    public DeriveBlobFromDummyPdfAbstract(final int paramNum) {
        this.paramNum = paramNum;
    }

    @Override
    public CommandDto process(
            final Command command,
            final CommandDto commandDto) {

        ParamDto paramDto = getParamDto(commandDto, paramNum);
        CommonDtoUtils.setValueOn(paramDto, ValueType.BLOB, blob, bookmarkService);

        return commandDto;
    }

    @Inject
    BookmarkService bookmarkService;

}
