# Architecture

## Structure

```text
├── domain/           # 도메인 계층 (핵심 비즈니스 로직)
│   ├── model/        # 도메인 모델
│   ├── service/      # 도메인 서비스
│   └── port/         # 포트 (인터페이스)
├── application/      # 애플리케이션 계층 (유스케이스)
│   └── service/      # 애플리케이션 서비스
├── adapter/          # 어댑터 계층
│   ├── inbound/      # 인바운드 어댑터 (컨트롤러)
│   └── outbound/     # 아웃바운드 어댑터 (리포지토리 구현체)
└── config/           # 설정
```

## Summary

- 도메인 계층 (Domain Layer)
    - 비즈니스 로직의 핵심
    - 외부 의존성이 없는 순수한 코드
    - Book, BookId, Money 등의 도메인 모델
- 포트 (Ports)
    - 인바운드 포트: 외부에서 도메인을 호출하는 인터페이스 (BookService)
    - 아웃바운드 포트: 도메인이 외부를 호출하는 인터페이스 (BookRepository)
- 어댑터 (Adapters)
    - 인바운드 어댑터: 외부 요청을 도메인으로 변환 (BookController)
    - 아웃바운드 어댑터: 도메인 요청을 외부 시스템으로 변환 (BookRepositoryAdapter)
- 의존성 방향
    - 모든 의존성이 도메인을 향하고, 도메인은 외부에 의존하지 않습니다.

```text
Controller → ApplicationService → Domain ← RepositoryAdapter
```