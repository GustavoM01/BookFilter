package org.atam.test.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.atam.test.model.Book;
import org.atam.test.model.BookDate;

public class BookFilter {

  private static final String JSON_PATH = "src/main/resources/books.json";
  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MM-dd-yyy");

  public void findBookByFilter(String filter) {
    try {
      List<Book> books = readJsonFile(JSON_PATH);
      filter(filter, books);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private Optional<BookDate> filter(String filter, List<Book> books) {
    List<Book> booksWithNoPublicationDate = filterBooksWithNoPublicationDate(books);
    System.out.println(booksWithNoPublicationDate);

    Book mostRecentBook = findMostRecentBookByKeyword(books, filter);

    List<Book> sortedBooks = sortBooksByPublicationDateAndBioLength(books);
    String outputFilePath = "src/main/resources/books-sorted.json";
    try {
      createJsonFile(sortedBooks, outputFilePath);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return Optional
        .of(BookDate.builder().book(mostRecentBook)
            .date(getDateFromTimestamp(Long.parseLong(mostRecentBook.getPublicationTimestamp())))
            .build());
  }

  private static List<Book> readJsonFile(String filePath) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.readValue(new File(filePath),
        mapper.getTypeFactory().constructCollectionType(List.class, Book.class));
  }

  private static List<Book> filterBooksWithNoPublicationDate(List<Book> books) {
    return books.stream()
        .filter(book -> book.getPublicationTimestamp() == null)
        .collect(Collectors.toList());
  }

  private static Book findMostRecentBookByKeyword(List<Book> books, String keyword) {
    return books.stream()
        .filter(
            book -> book.getTitle().contains(keyword) || book.getSummary().contains(keyword) || book
                .getAuthor().getBio().contains(keyword))
        .max(Comparator
            .comparingLong(book -> null == book.getPublicationTimestamp() ? 0L : Long.parseLong(
                book.getPublicationTimestamp())))
        .orElse(null);
  }

  private static List<Book> sortBooksByPublicationDateAndBioLength(List<Book> books) {
    List<Book> bookList = books.stream()
        .sorted(Comparator
            .comparingLong(book -> null == book.getPublicationTimestamp() ? 0L : Long.parseLong(
                book.getPublicationTimestamp()))).collect(Collectors.toList());
    bookList.sort((a, b) -> {
      if (null != a.getPublicationTimestamp() && null != b.getPublicationTimestamp() && a
          .getPublicationTimestamp().equals(b.getPublicationTimestamp())) {
        return a.getAuthor().getBio().length() - b.getAuthor().getBio().length();
      } else {
        return 0;
      }
    });
    return bookList;
  }


  private static void createJsonFile(List<Book> books, String filePath) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    mapper.enable(SerializationFeature.INDENT_OUTPUT);

    ObjectNode rootNode = mapper.createObjectNode();
    rootNode.set("books", mapper.valueToTree(books));

    mapper.writeValue(new File(filePath), rootNode);
  }

  private static String getDateFromTimestamp(Long timestamp) {
    LocalDateTime time = LocalDateTime
        .ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.systemDefault());
    return time.format(FORMATTER);
  }
}
