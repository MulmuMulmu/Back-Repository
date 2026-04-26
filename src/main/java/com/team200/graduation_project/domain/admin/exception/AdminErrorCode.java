package com.team200.graduation_project.domain.admin.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AdminErrorCode {

    ADMIN_LOGIN_FAILED(HttpStatus.BAD_REQUEST,
            "COMMON400", "아이디 또는 비밀번호가 일치하지 않습니다."),
    ADMIN_LOGIN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,
            "COMMON500", "관리자 로그인을 처리할 수 없습니다."),
    ADMIN_LOGOUT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,
            "COMMON500", "로그아웃을 처리할 수 없습니다."),
    ADMIN_DASHBOARD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,
            "COMMON500", "사용자 통계 정보를 불러올 수 없습니다."),
    ADMIN_TODAY_REPORT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,
            "COMMON500", "당일 신고 건수를 불러올 수 없습니다."),
    ADMIN_TODAY_SHARE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,
            "COMMON500", "당일 나눔 횟수를 불러올 수 없습니다."),
    ADMIN_REPORT_LIST_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,
            "COMMON500", "신고 목록을 조회할 수 없습니다."),
    ADMIN_REPORT_DETAIL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,
            "COMMON500", "신고 내역 한 건을 자세히 불러올 수 없습니다."),
    ADMIN_SHARE_MASKING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,
            "COMMON500", "게시글을 숨김 처리 할 수 없습니다."),
    ADMIN_USER_ACTION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,
            "COMMON500", "사용자 상태를 변경할 수 없습니다."),
    ADMIN_SHARE_DETAIL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,
            "COMMON500", "나눔 정보를 불러올 수 없습니다."),
    ADMIN_USER_LIST_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,
            "COMMON500", "사용자 리스트를 불러올 수 없습니다."),
    ADMIN_USER_SHARE_LIST_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,
            "COMMON500", "사용자의 나눔 리스트를 불러올 수 없습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,
            "COMMON500", "예기치 않은 서버 에러가 발생했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
