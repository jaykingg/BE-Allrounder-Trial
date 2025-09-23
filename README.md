# Allrounder - 멀티모듈 실습 프로젝트

이 프로젝트는 다양한 Spring Boot 기술들을 독립적인 모듈로 구성하여 실습할 수 있는 멀티모듈 프로젝트입니다.

## 🏗️ 프로젝트 구조

```
allrounder/
├── batch-scheduler/     # Spring Batch & Quartz Scheduler
├── architecture/        # Hexagonal Architecture & DDD
├── security/           # RBAC, ABAC, PBAC, JWT
├── kafka/              # Apache Kafka
├── kotest-spec/        # Kotest, MockK, Fixture Monkey
├── querydsl/           # QueryDSL
└── redis/              # Redis & Feature Flag
```

## 🚀 시작하기

### 1. 인프라 실행

```bash
# Docker Compose로 필요한 서비스들 실행
docker-compose up -d

# 실행된 서비스들 확인
docker-compose ps
```

### 2. 개별 모듈 실행

각 모듈은 독립적으로 실행할 수 있습니다:

```bash
# Batch/Scheduler 모듈 실행
./gradlew :batch-scheduler:bootRun

# Architecture 모듈 실행
./gradlew :architecture:bootRun

# Security 모듈 실행
./gradlew :security:bootRun

# Kafka 모듈 실행
./gradlew :kafka:bootRun

# Kotest Spec 모듈 실행
./gradlew :kotest-spec:bootRun

# QueryDSL 모듈 실행
./gradlew :querydsl:bootRun

# Redis 모듈 실행
./gradlew :redis:bootRun
```

### 3. 테스트 실행

```bash
# 전체 테스트 실행
./gradlew test

# 특정 모듈 테스트 실행
./gradlew :kotest-spec:test
```

## 📋 모듈별 상세 정보

### ✅ Batch/Scheduler 모듈

- **포트**: 8081
- **기능**: Spring Batch Job, Quartz Scheduler
- **데이터베이스**: batch_scheduler
- **[📖 상세 가이드](./batch-scheduler/ReadMe.md)**

### ✅ Architecture 모듈

- **포트**: 8082
- **기능**: Hexagonal Architecture, DDD 패턴
- **데이터베이스**: architecture
- **[📖 상세 가이드](./architecture/ReadMe.md)**

### ✅ Security 모듈

- **포트**: 8083
- **기능**: RBAC, ABAC, PBAC, JWT 인증
- **데이터베이스**: security
- **[📖 상세 가이드](./security/ReadMe.md)**

### Kafka 모듈

- **포트**: 8084
- **기능**: Kafka Producer/Consumer, Streams
- **브로커**: localhost:9092

### Kotest Spec 모듈

- **포트**: 8085
- **기능**: Kotest, MockK, Fixture Monkey 테스트
- **데이터베이스**: kotest_spec

### QueryDSL 모듈

- **포트**: 8086
- **기능**: QueryDSL을 이용한 타입 안전한 쿼리
- **데이터베이스**: querydsl

### Redis 모듈

- **포트**: 8087
- **기능**: Redis 분산락, Redis 캐싱
- **Redis**: localhost:6379

## 🛠️ 기술 스택

- **언어**: Kotlin 1.9.25
- **프레임워크**: Spring Boot 3.5.4
- **빌드 도구**: Gradle (Kotlin DSL)
- **데이터베이스**: MySQL 8.0
- **캐시**: Redis 7
- **메시지 브로커**: Apache Kafka
- **테스트**: Kotest, MockK, Fixture Monkey, TestContainers

## 📝 개발 가이드

각 모듈은 독립적으로 개발할 수 있으며, 필요에 따라 다른 모듈의 기능을 참조할 수 있습니다.

### 모듈별 설정

- 각 모듈의 `application.yml`에서 포트와 데이터베이스 설정을 확인하세요
- Docker Compose로 실행된 서비스들의 상태를 확인하세요

### 테스트 작성

- Kotest Spec 모듈을 참조하여 테스트 패턴을 확인하세요
- Fixture Monkey를 활용한 테스트 데이터 생성 방법을 학습하세요

## 🔧 문제 해결

### 포트 충돌

각 모듈이 다른 포트를 사용하도록 설정되어 있습니다. 필요시 `application.yml`에서 포트를 변경하세요.

### 데이터베이스 연결 실패

```bash
# Docker Compose 서비스 상태 확인
docker-compose ps

# MySQL 컨테이너 로그 확인
docker-compose logs mysql
```

### Kafka 연결 실패

```bash
# Kafka 컨테이너 상태 확인
docker-compose logs kafka
```
