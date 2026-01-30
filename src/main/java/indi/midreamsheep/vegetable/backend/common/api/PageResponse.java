package indi.midreamsheep.vegetable.backend.common.api;

import java.util.List;

/**
 * 通用分页响应体。
 *
 * @param items 数据列表
 * @param page 页码（从 1 开始）
 * @param pageSize 每页大小
 * @param total 总条数
 * @param <T> 数据类型
 */
public record PageResponse<T>(
        List<T> items,
        int page,
        int pageSize,
        long total
) {
}
