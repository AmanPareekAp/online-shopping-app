package com.online_shopping.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderStateChangeDto {

    @NotNull
    Integer orderProductId;
    @NotEmpty
    private String fromStatus;
    @NotEmpty
    private String toStatus;
}
