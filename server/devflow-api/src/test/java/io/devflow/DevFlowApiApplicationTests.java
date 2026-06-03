package io.devflow;

import io.devflow.contributions.repository.ContributionDayRepository;
import io.devflow.contributions.repository.ContributionEventRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(properties = "spring.autoconfigure.exclude="
		+ "org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration,"
		+ "org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration,"
		+ "org.springframework.boot.data.jpa.autoconfigure.DataJpaRepositoriesAutoConfiguration")
class DevFlowApiApplicationTests {

	@MockitoBean
	private ContributionDayRepository contributionDayRepository;

	@MockitoBean
	private ContributionEventRepository contributionEventRepository;

	@Test
	void contextLoads() {
	}

}
