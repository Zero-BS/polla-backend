package org.zerobs;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import lombok.extern.slf4j.Slf4j;
import org.zerobs.entities.Principal;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.UUID;

@Controller
@Slf4j
public class BookController {
    @Inject
    Principal principal;

    @Post("book")
    public BookSaved save(@Valid @Body Book book) {
        BookSaved bookSaved = new BookSaved();
        bookSaved.setName(principal.getName());
        bookSaved.setIsbn(UUID.randomUUID().toString());
        return bookSaved;
    }
}
