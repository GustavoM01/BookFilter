package org.atam.test.model;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Book {

  private String id;
  private String title;
  private String publicationTimestamp;
  private String pages;
  private String summary;
  private AuthorInfo author;

}
