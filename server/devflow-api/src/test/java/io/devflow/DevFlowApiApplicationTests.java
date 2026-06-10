package io.devflow;

import io.devflow.contributions.repository.ContributionDayRepository;
import io.devflow.contributions.repository.ContributionEventRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class DevFlowApiApplicationTests {

	@MockitoBean
	private ContributionDayRepository contributionDayRepository;

	@MockitoBean
	private ContributionEventRepository contributionEventRepository;

	@MockitoBean
	private io.devflow.users.repository.UserRepository userRepository;

	@Test
	void contextLoads() {
	}

}
