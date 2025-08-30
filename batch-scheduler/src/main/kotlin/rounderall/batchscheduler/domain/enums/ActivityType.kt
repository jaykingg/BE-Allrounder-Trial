package rounderall.batchscheduler.domain.enums

enum class ActivityType(
    val description: String,
) {
    LOGIN("로그인"),
    VIEW_PRODUCT("상품 조회"),
    PURCHASE_PRODUCT("상품 구매"),
    LOGOUT("로그아웃")
}