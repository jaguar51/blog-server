package me.academeg.api.entity;

/**
 * ArticleStatus
 *
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 */
public enum ArticleStatus {
    PUBLISHED,
    DRAFT,
    LOCK, // lock article by admin or moderator
}