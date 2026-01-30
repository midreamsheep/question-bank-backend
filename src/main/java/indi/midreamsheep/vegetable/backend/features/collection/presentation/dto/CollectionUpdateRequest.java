package indi.midreamsheep.vegetable.backend.features.collection.presentation.dto;

import indi.midreamsheep.vegetable.backend.features.problem.domain.Visibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 题单更新请求 DTO。
 *
 * @param name 名称
 * @param description 简介
 * @param visibility 可见性
 */
public record CollectionUpdateRequest(
        @NotBlank String name,
        String description,
        @NotNull Visibility visibility
) {
}

