package com.biblioteca.backend.models.entities;

import com.biblioteca.backend.utils.Utils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_book_loan")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class BookLoan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // @ApiModelProperty(notes = "Book loan ID")
    private Long id;

    @Column(name = "code", length = 10, unique = true, nullable = false)
    // @ApiModelProperty(notes = "Book loan code", required = true, example = "BL1234567890")
    private String code;

    @Column(name = "loan_date", nullable = false)
    // @ApiModelProperty(notes = "Book loan date", required = true, example = "2020-05-25")
    private LocalDate loanDate;

    @Column(name = "return_date", nullable = false)
    // @ApiModelProperty(notes = "Book return date", required = true, example = "2020-05-25")
    private LocalDate returnDate;

    @Column(nullable = false)
    // @ApiModelProperty(notes = "Book loan status", required = true, example = "P")
    private String status;

    @Column(name = "creation_date", nullable = false)
    // @ApiModelProperty(notes = "Book loan creation date", required = true, example = "2020-05-25")
    private LocalDateTime creationDate;

    @Column(name = "modification_date")
    // @ApiModelProperty(notes = "Book loan modification date", example = "2020-06-01")
    private LocalDateTime modificationDate;

    // BOOK_LOAN(M):MEMBER(1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    // BOOK_LOAN(M):EMPLOYEE(1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    // BOOK_LOAN(M):BOOK(1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    @PrePersist
    public void prePersist() {
        code = Utils.makeRandom10StringCode("BL");
        status = "P"; // P => PENDING, A => ACTIVE, O => OVERDUE, C => CANCELED, R => RETURNED
        creationDate = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        modificationDate = LocalDateTime.now();
    }
}