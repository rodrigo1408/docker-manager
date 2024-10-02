package com.resultx.docker_manager.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.RemoteApiVersion;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;

@Configuration
public class DockerClientConfig {

	@Value("${docker.socket.path}")
	private String dockerSocketPath;

	@Bean
	@Lazy(false)
	DockerClient buildDockerClient() {
		DefaultDockerClientConfig.Builder dockerClientConfigBuilder = DefaultDockerClientConfig
				.createDefaultConfigBuilder();

		if (this.dockerSocketPath != null && this.dockerSocketPath.startsWith("npipe://")) {
			dockerClientConfigBuilder.withDockerHost(dockerSocketPath).withApiVersion(RemoteApiVersion.VERSION_1_24)
					.withDockerTlsVerify(false);
		}

		DefaultDockerClientConfig dockerClientConfig = dockerClientConfigBuilder.build();

		ApacheDockerHttpClient dockerHttpClient = new ApacheDockerHttpClient.Builder()
				.dockerHost(dockerClientConfig.getDockerHost()).maxConnections(5)
				.connectionTimeout(Duration.ofMillis(300)).responseTimeout(Duration.ofSeconds(3)).build();

		DockerClient client = DockerClientBuilder.getInstance(dockerClientConfig).withDockerHttpClient(dockerHttpClient)
				.build();

		client.pingCmd().exec();

		return client;
	}
}