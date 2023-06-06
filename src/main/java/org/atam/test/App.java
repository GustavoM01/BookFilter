package org.atam.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.atam.test.model.Book;
import org.atam.test.util.BookFilter;

/**
 * Hello world!
 *
 */
public class App 
{

    public static void main( String[] args )
    {
        BookFilter bookFilter = new BookFilter();
        bookFilter.findBookByFilter("old");
    }


}
