package rounderall.architecture.config

import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

/**
 * 헥사고날 아키텍처 설정
 * 
 * JPA 리포지토리 활성화 및 기타 설정을 담당합니다.
 */
@Configuration
@EnableJpaRepositories(basePackages = ["rounderall.architecture.adapter.outbound"])
class HexagonalArchitectureConfig
