package org.incode.module.document.spi;

import javax.inject.Inject;

import org.apache.isis.applib.services.bookmark.Bookmark;
import org.apache.isis.applib.services.bookmark.BookmarkService;
import org.apache.isis.applib.services.command.Command;
import org.apache.isis.applib.services.command.CommandDtoProcessorForActionAbstract;
import org.apache.isis.schema.cmd.v1.CommandDto;
import org.apache.isis.schema.cmd.v1.ParamDto;
import org.apache.isis.schema.common.v1.ValueType;
import org.apache.isis.schema.utils.CommonDtoUtils;

import org.incode.module.document.dom.impl.docs.Document;

public abstract class DeriveBlobFromReturnedDocumentAbstract
        extends CommandDtoProcessorForActionAbstract {

    private final int paramNum;

    protected DeriveBlobFromReturnedDocumentAbstract(final int paramNum) {
        this.paramNum = paramNum;
    }

    @Override
    public CommandDto process(
            final Command command,
            final CommandDto commandDto) {
        final Bookmark result = command.getResult();
        if(result == null) {
            return commandDto;
        }

        try {
            final Document document = bookmarkService.lookup(result, Document.class);
            if (document != null) {
                ParamDto paramDto = getParamDto(commandDto, paramNum);
                CommonDtoUtils.setValueOn(paramDto, ValueType.BLOB, document.getBlob(), bookmarkService);
            }
        } catch(Exception ex) {
            return commandDto;
        }

        return commandDto;
    }

    @Inject
    BookmarkService bookmarkService;

}
