package com.example.simpletravel.form;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReviewEditForm {
    @NotNull(message = "評価を設定してください。")
    @Min(value = 0, message = "評価は0から5の範囲で設定してください。")
    @Max(value = 5, message = "評価は1から5の範囲で設定してください。")
    private Integer numberOfStars;

    @NotBlank(message = "コメントを記入してください。")
    private String comment;
}
