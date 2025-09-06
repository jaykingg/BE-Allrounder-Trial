package rounderall.security.infrastructure.config

import com.navercorp.fixturemonkey.FixtureMonkey
import com.navercorp.fixturemonkey.kotlin.KotlinPlugin
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FixtureMonkeyConfig {
    
    @Bean
    fun fixtureMonkey(): FixtureMonkey {
        return FixtureMonkey.builder()
            .plugin(KotlinPlugin())
            .build()
    }
}
