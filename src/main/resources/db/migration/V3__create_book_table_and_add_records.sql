CREATE TABLE book (
  id INT NOT NULL,
  author_id INT NOT NULL,
  title VARCHAR(400) NOT NULL,

  CONSTRAINT pk_t_book PRIMARY KEY (id),
  CONSTRAINT fk_t_book_author_id FOREIGN KEY (author_id) REFERENCES author(id)
);


INSERT INTO author VALUES (next value for s_author_id, 'George', 'Orwell', '1903-06-25', 1903, null);
INSERT INTO author VALUES (next value for s_author_id, 'Paulo', 'Coelho', '1947-08-24', 1947, null);

INSERT INTO book VALUES (1, 1, '1984');
INSERT INTO book VALUES (2, 1, 'Animal Farm');
INSERT INTO book VALUES (3, 2, 'O Alquimista');
INSERT INTO book VALUES (4, 2, 'Brida');
