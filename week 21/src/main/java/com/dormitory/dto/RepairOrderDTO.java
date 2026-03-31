// RepairOrderDTO.java
package com.dormitory.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class RepairOrderDTO {
    @NotBlank(message = "设备类型不能为空")
    private String deviceType;

    @NotBlank(message = "问题描述不能为空")
    private String description;

    private String imageUrl;
}