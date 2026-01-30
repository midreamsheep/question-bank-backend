package indi.midreamsheep.vegetable.backend.features.collection.presentation.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * 题单排序调整请求 DTO。
 *
 * @param items 排序项
 */
public record CollectionReorderRequest(
        @NotEmpty @Valid List<CollectionItemRequest> items
) {
}
