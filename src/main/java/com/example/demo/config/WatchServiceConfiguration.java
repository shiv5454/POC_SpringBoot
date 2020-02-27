package com.example.demo.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WatchServiceConfiguration {

	private static final Logger log = LoggerFactory.getLogger(WatchServiceConfiguration.class);
	@Value("${source.path}")
	private String sourcePath;

	@Bean("watchService")
	public WatchService getWatchService() {
		WatchService watchService = null;
		Path path = Paths.get(sourcePath);
		if(!path.toFile().isDirectory()||!path.toFile().canRead())
			throw new IllegalArgumentException("Source path: " + path + " is eithet not a folder or Read Access is Denied.");
		try {
			watchService = path.getFileSystem().newWatchService();
			path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
			log.info("Started watching \"{}\" directory ", sourcePath);
		} catch (IOException e) {
			log.error("Failed to create WatchService for directory \"" + sourcePath + "\"", e);
		}

		return watchService;
	}
}
