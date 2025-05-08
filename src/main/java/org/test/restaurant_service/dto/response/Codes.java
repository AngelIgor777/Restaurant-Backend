package org.test.restaurant_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Codes {
    private int trueCode;
    private int falseCode1;
    private int falseCode2;
}
