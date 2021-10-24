package org.zerobs.polla.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExceptionResponseBody {
    private String messageTitle;
    private String messageText;
    private int internalCode;
}