package com.biblioteca.backend.models.dtos;

import com.biblioteca.backend.enums.CustomDateConstraint;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BookLoanDTO {
    private Long bookLoanId;

    @NotNull(message = "{notNull.bookLoanDTO.memberId}")
    private Long memberId;

    @NotNull(message = "{notNull.bookLoanDTO.employeeId}")
    private Long employeeId;

    @NotNull(message = "{notNull.bookLoanDTO.bookId}")
    private Long bookId;

    @NotNull(message = "{notNull.bookLoanDTO.loanDate}")
    @CustomDateConstraint(message = "{valid.bookLoanDTO.loanDate}")
    private String loanDate;

    @NotNull(message = "{notNull.bookLoanDTO.returnDate}")
    @CustomDateConstraint(message = "{valid.bookLoanDTO.returnDate}")
    private String returnDate;
}
