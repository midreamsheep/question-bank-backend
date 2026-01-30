package indi.midreamsheep.vegetable.backend.features.report.domain;

/**
 * 举报状态。
 */
public enum ReportStatus {
    /**
     * 待处理。
     */
    OPEN,
    /**
     * 已处理（有效举报）。
     */
    RESOLVED,
    /**
     * 已驳回（无效举报）。
     */
    REJECTED
}

